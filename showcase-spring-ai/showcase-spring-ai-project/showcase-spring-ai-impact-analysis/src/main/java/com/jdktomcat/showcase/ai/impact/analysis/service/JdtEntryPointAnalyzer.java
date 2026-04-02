package com.jdktomcat.showcase.ai.impact.analysis.service;

import com.jdktomcat.showcase.ai.impact.analysis.domain.EntryPoint;
import com.jdktomcat.showcase.ai.impact.analysis.domain.EntryPointType;
import org.eclipse.jdt.core.dom.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses Eclipse JDT AST parser to accurately detect entry points in Java source files.
 */
@Service
public class JdtEntryPointAnalyzer {

    private final Path repoRoot;

    public JdtEntryPointAnalyzer(@Value("${app.impact-analysis.repo-root:#{null}}") String repoRoot) {
        this.repoRoot = repoRoot != null && !repoRoot.isBlank() ? Path.of(repoRoot) : Path.of(".").toAbsolutePath();
    }

    /**
     * Analyze a single Java file using AST parsing to detect entry points.
     */
    public List<EntryPoint> analyze(Path file) throws IOException {
        List<EntryPoint> entryPoints = new ArrayList<>();
        
        String source = Files.readString(file);
        CompilationUnit cu = parseCompilationUnit(file, source);
        String relativePath = repoRoot.relativize(file).toString();
        String module = extractModule(relativePath);
        
        String packageName = cu.getPackage() == null ? "" : cu.getPackage().getName().getFullyQualifiedName();
        
        // Visit type declarations
        for (Object item : cu.types()) {
            if (item instanceof TypeDeclaration typeDecl) {
                String typeName = typeDecl.getName().getIdentifier();
                String typeFqn = packageName.isBlank() ? typeName : packageName + "." + typeName;
                
                // Get class-level annotations
                List<String> typeAnnotations = getTypeAnnotations(typeDecl);
                
                // Analyze methods
                for (MethodDeclaration method : typeDecl.getMethods()) {
                    EntryPoint entryPoint = analyzeMethod(method, typeFqn, typeName, typeAnnotations, relativePath, module, cu);
                    if (entryPoint != null) {
                        entryPoints.add(entryPoint);
                    }
                }
            }
        }
        
        return entryPoints;
    }

    private EntryPoint analyzeMethod(
            MethodDeclaration method,
            String typeFqn,
            String typeName,
            List<String> typeAnnotations,
            String relativePath,
            String module,
            CompilationUnit cu
    ) {
        if (method.isConstructor()) {
            return null;
        }
        
        String methodName = method.getName().getIdentifier();
        String methodSignature = buildMethodSignature(typeFqn, method);
        int lineNumber = cu.getLineNumber(method.getStartPosition());
        
        // Get method annotations
        List<String> methodAnnotations = getMethodAnnotations(method);
        
        // Determine entry point type
        EntryPointType entryPointType = determineEntryPointType(methodAnnotations, typeAnnotations);
        
        if (entryPointType == null) {
            return null;
        }
        
        // Build metadata
        String metadata = buildMetadata(method, methodAnnotations, entryPointType);
        
        return new EntryPoint(
                entryPointType.name() + ":" + methodSignature,
                entryPointType,
                typeFqn,
                methodName,
                methodSignature,
                relativePath,
                module,
                lineNumber,
                metadata
        );
    }

    private EntryPointType determineEntryPointType(List<String> methodAnnotations, List<String> typeAnnotations) {
        // Check custom impact analysis annotations
        if (methodAnnotations.contains("HttpEntry")) return EntryPointType.HTTP;
        if (methodAnnotations.contains("RpcEntry")) return EntryPointType.RPC;
        if (methodAnnotations.contains("MqEntry")) return EntryPointType.MQ;
        if (methodAnnotations.contains("ScheduledEntry")) return EntryPointType.SCHEDULED;
        if (methodAnnotations.contains("EventEntry")) return EntryPointType.EVENT;
        
        // Check Spring MVC annotations
        boolean isController = typeAnnotations.contains("RestController") || 
                               typeAnnotations.contains("Controller");
        if (isController && methodAnnotations.stream().anyMatch(a -> 
                a.contains("Mapping"))) {
            return EntryPointType.HTTP;
        }
        
        // Check for mapping annotations even without class-level controller
        if (methodAnnotations.stream().anyMatch(a -> 
                a.contains("Mapping") && !a.contains("Exception"))) {
            return EntryPointType.HTTP;
        }
        
        // Check Dubbo annotations
        if (typeAnnotations.contains("DubboService") || typeAnnotations.contains("Service")) {
            return EntryPointType.RPC;
        }
        if (methodAnnotations.contains("DubboService") || methodAnnotations.contains("Service")) {
            return EntryPointType.RPC;
        }
        
        // Check MQ annotations
        if (methodAnnotations.contains("RabbitListener")) return EntryPointType.MQ;
        if (methodAnnotations.contains("KafkaListener")) return EntryPointType.MQ;
        if (methodAnnotations.contains("RocketMQMessageListener")) return EntryPointType.MQ;
        
        // Check Scheduled annotations
        if (methodAnnotations.contains("Scheduled")) return EntryPointType.SCHEDULED;
        
        // Check Event annotations
        if (methodAnnotations.contains("EventListener")) return EntryPointType.EVENT;
        if (methodAnnotations.contains("TransactionalEventListener")) return EntryPointType.EVENT;
        
        return null;
    }

    private List<String> getTypeAnnotations(TypeDeclaration typeDecl) {
        List<String> annotations = new ArrayList<>();
        for (Object modifier : typeDecl.modifiers()) {
            if (modifier instanceof Annotation annotation) {
                annotations.add(annotation.getTypeName().getFullyQualifiedName());
            }
        }
        return annotations;
    }

    private List<String> getMethodAnnotations(MethodDeclaration method) {
        List<String> annotations = new ArrayList<>();
        for (Object modifier : method.modifiers()) {
            if (modifier instanceof Annotation annotation) {
                String annotationName = annotation.getTypeName().getFullyQualifiedName();
                // Extract simple name
                int lastDot = annotationName.lastIndexOf('.');
                if (lastDot >= 0 && lastDot < annotationName.length() - 1) {
                    annotationName = annotationName.substring(lastDot + 1);
                }
                annotations.add(annotationName);
            }
        }
        return annotations;
    }

    private String buildMetadata(MethodDeclaration method, List<String> annotations, EntryPointType type) {
        StringBuilder metadata = new StringBuilder();
        
        switch (type) {
            case HTTP -> {
                for (String annotation : annotations) {
                    if (annotation.contains("Mapping")) {
                        metadata.append(annotation).append(";");
                    }
                }
            }
            case RPC -> {
                if (annotations.contains("DubboService") || annotations.contains("Service")) {
                    metadata.append("protocol=DUBBO;");
                }
            }
            case MQ -> {
                for (String annotation : annotations) {
                    if (annotation.contains("Listener")) {
                        metadata.append(annotation).append(";");
                    }
                }
            }
            case SCHEDULED -> {
                if (annotations.contains("Scheduled")) {
                    metadata.append("annotation=@Scheduled;");
                }
            }
            case EVENT -> {
                for (String annotation : annotations) {
                    if (annotation.contains("EventListener")) {
                        metadata.append(annotation).append(";");
                    }
                }
            }
        }
        
        return metadata.toString();
    }

    private String buildMethodSignature(String typeFqn, MethodDeclaration method) {
        StringBuilder params = new StringBuilder();
        for (Object item : method.parameters()) {
            SingleVariableDeclaration param = (SingleVariableDeclaration) item;
            if (!params.isEmpty()) {
                params.append(", ");
            }
            params.append(param.getType().toString());
            if (param.isVarargs()) {
                params.append("...");
            }
        }
        return typeFqn + "#" + method.getName().getIdentifier() + "(" + params + ")";
    }

    private CompilationUnit parseCompilationUnit(Path file, String source) {
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setSource(source.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);

        // Set compiler options using Map
        java.util.Map<String, String> options = new java.util.HashMap<>();
        options.put("org.eclipse.jdt.core.compiler.source", "17");
        options.put("org.eclipse.jdt.core.compiler.compliance", "17");
        options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "17");
        parser.setCompilerOptions(options);

        return (CompilationUnit) parser.createAST(null);
    }

    private String extractModule(String relativePath) {
        int index = relativePath.indexOf('/');
        return index < 0 ? relativePath : relativePath.substring(0, index);
    }
}
