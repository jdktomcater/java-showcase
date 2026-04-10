package com.jdktomcat.showcase.ai.code.chunk.service;

import com.jdktomcat.showcase.ai.code.chunk.domain.EntryPoint;
import com.jdktomcat.showcase.ai.code.chunk.domain.EntryPointType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses Spoon to detect entry points in Java source files.
 */
@Service
public class SpoonEntryPointAnalyzer {

    private final Path repoRoot;

    public SpoonEntryPointAnalyzer(@Value("${app.impact-analysis.repo-root:#{null}}") String repoRoot) {
        this.repoRoot = repoRoot != null && !repoRoot.isBlank() ? Path.of(repoRoot) : Path.of(".").toAbsolutePath();
    }

    public List<EntryPoint> analyze(Path file) throws IOException {
        List<EntryPoint> entryPoints = new ArrayList<>();
        String source = Files.readString(file);
        CtModel model = parseSingleFile(file, source);
        String relativePath = repoRoot.relativize(file).toString();
        String module = extractModule(relativePath);

        for (CtType<?> ctType : model.getElements(new TypeFilter<>(CtType.class))) {
            if (!ctType.isTopLevel()) {
                continue;
            }
            String packageName = ctType.getPackage() == null ? "" : ctType.getPackage().getQualifiedName();
            String typeName = ctType.getSimpleName();
            String typeFqn = packageName.isBlank() ? typeName : packageName + "." + typeName;

            List<String> typeAnnotations = getTypeAnnotations(ctType);

            for (CtMethod<?> method : ctType.getMethods()) {
                EntryPoint entryPoint = analyzeMethod(method, typeFqn, typeName, typeAnnotations, relativePath, module);
                if (entryPoint != null) {
                    entryPoints.add(entryPoint);
                }
            }
        }

        return entryPoints;
    }

    private static CtModel parseSingleFile(Path file, String source) throws IOException {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setComplianceLevel(17);
        launcher.getEnvironment().setIgnoreSyntaxErrors(true);
        launcher.addInputResource(new VirtualFile(file.getFileName().toString(), source));
        try {
            launcher.buildModel();
        } catch (Exception ex) {
            throw new IOException("Parse failed for " + file + ": " + ex.getMessage(), ex);
        }
        return launcher.getModel();
    }

    private EntryPoint analyzeMethod(
            CtMethod<?> method,
            String typeFqn,
            String typeName,
            List<String> typeAnnotations,
            String relativePath,
            String module
    ) {
        String methodName = method.getSimpleName();
        String methodSignature = buildMethodSignature(typeFqn, method);
        int lineNumber = SpoonAstSupport.startLine(method);

        List<String> methodAnnotations = getMethodAnnotations(method);
        EntryPointType entryPointType = determineEntryPointType(methodAnnotations, typeAnnotations);
        if (entryPointType == null) {
            return null;
        }

        String metadata = buildMetadata(methodAnnotations, entryPointType);

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
        if (methodAnnotations.contains("HttpEntry")) {
            return EntryPointType.HTTP;
        }
        if (methodAnnotations.contains("RpcEntry")) {
            return EntryPointType.RPC;
        }
        if (methodAnnotations.contains("MqEntry")) {
            return EntryPointType.MQ;
        }
        if (methodAnnotations.contains("ScheduledEntry")) {
            return EntryPointType.SCHEDULED;
        }
        if (methodAnnotations.contains("EventEntry")) {
            return EntryPointType.EVENT;
        }

        boolean isController = typeAnnotations.contains("RestController") || typeAnnotations.contains("Controller");
        if (isController && methodAnnotations.stream().anyMatch(a -> a.contains("Mapping"))) {
            return EntryPointType.HTTP;
        }
        if (methodAnnotations.stream().anyMatch(a -> a.contains("Mapping") && !a.contains("Exception"))) {
            return EntryPointType.HTTP;
        }

        if (typeAnnotations.contains("DubboService") || typeAnnotations.contains("Service")) {
            return EntryPointType.RPC;
        }
        if (methodAnnotations.contains("DubboService") || methodAnnotations.contains("Service")) {
            return EntryPointType.RPC;
        }

        if (methodAnnotations.contains("RabbitListener")) {
            return EntryPointType.MQ;
        }
        if (methodAnnotations.contains("KafkaListener")) {
            return EntryPointType.MQ;
        }
        if (methodAnnotations.contains("RocketMQMessageListener")) {
            return EntryPointType.MQ;
        }

        if (methodAnnotations.contains("Scheduled")) {
            return EntryPointType.SCHEDULED;
        }

        if (methodAnnotations.contains("EventListener")) {
            return EntryPointType.EVENT;
        }
        if (methodAnnotations.contains("TransactionalEventListener")) {
            return EntryPointType.EVENT;
        }

        return null;
    }

    private List<String> getTypeAnnotations(CtType<?> typeDecl) {
        List<String> annotations = new ArrayList<>();
        for (CtAnnotation<?> annotation : typeDecl.getAnnotations()) {
            CtTypeReference<?> ref = annotation.getAnnotationType();
            if (ref == null) {
                continue;
            }
            String simple = ref.getSimpleName();
            if (simple != null && !simple.isBlank()) {
                annotations.add(simple);
                continue;
            }
            String qualified = ref.getQualifiedName();
            if (qualified != null && !qualified.isBlank()) {
                int lastDot = qualified.lastIndexOf('.');
                annotations.add(lastDot >= 0 ? qualified.substring(lastDot + 1) : qualified);
            } else {
                annotations.add(ref.toString());
            }
        }
        return annotations;
    }

    private List<String> getMethodAnnotations(CtMethod<?> method) {
        List<String> annotations = new ArrayList<>();
        for (CtAnnotation<?> annotation : method.getAnnotations()) {
            CtTypeReference<?> ref = annotation.getAnnotationType();
            if (ref == null) {
                continue;
            }
            String annotationName = ref.getSimpleName();
            if (annotationName == null || annotationName.isBlank()) {
                String qualified = ref.getQualifiedName();
                if (qualified != null && !qualified.isBlank()) {
                    int lastDot = qualified.lastIndexOf('.');
                    annotationName = lastDot >= 0 ? qualified.substring(lastDot + 1) : qualified;
                } else {
                    annotationName = ref.toString();
                }
            }
            annotations.add(annotationName);
        }
        return annotations;
    }

    private String buildMetadata(List<String> annotations, EntryPointType type) {
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

    private String buildMethodSignature(String typeFqn, CtMethod<?> method) {
        StringBuilder params = new StringBuilder();
        for (CtParameter<?> param : method.getParameters()) {
            if (!params.isEmpty()) {
                params.append(", ");
            }
            params.append(param.getType().toString());
            if (param.isVarArgs()) {
                params.append("...");
            }
        }
        return typeFqn + "#" + method.getSimpleName() + "(" + params + ")";
    }

    private String extractModule(String relativePath) {
        int index = relativePath.indexOf('/');
        return index < 0 ? relativePath : relativePath.substring(0, index);
    }
}
