package com.jdktomcat.showcase.ai.code.chunk.service;

import com.jdktomcat.showcase.ai.code.chunk.domain.CodeGraphNode;
import com.jdktomcat.showcase.ai.code.chunk.domain.CodeGraphRelation;
import com.jdktomcat.showcase.ai.code.chunk.domain.NodeType;
import com.jdktomcat.showcase.ai.code.chunk.domain.RelationType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JavaDependencyAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(JavaDependencyAnalyzer.class);

    private final Path repoRoot;

    public JavaDependencyAnalyzer(@Value("${app.code-rag.repo-root}") String repoRoot) {
        this.repoRoot = Path.of(repoRoot);
    }

    public AnalysisResult analyze(Path file) throws IOException {
        String source = Files.readString(file);
        CompilationUnit cu = JdtAstSupport.parse(file, source, repoRoot);
        String relativePath = JdtAstSupport.normalize(repoRoot.relativize(file));
        String module = extractModule(relativePath);
        String packageName = cu.getPackage() == null ? "" : cu.getPackage().getName().getFullyQualifiedName();
        Set<CodeGraphNode> nodes = new LinkedHashSet<>();
        Set<CodeGraphRelation> relations = new LinkedHashSet<>();
        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration type) {
                String typeFqn = buildTypeFqn(packageName, type);
                String typeId = nodeId(NodeType.TYPE, typeFqn);
                nodes.add(new CodeGraphNode(
                        typeId,
                        NodeType.TYPE,
                        type.getName().getIdentifier(),
                        typeFqn,
                        typeKind(type),
                        relativePath,
                        module,
                        JdtAstSupport.startLine(cu, type),
                        JdtAstSupport.endLine(cu, type)
                ));

                collectTypeAnnotations(type, typeId, nodes, relations);
                collectImports(cu, typeId, nodes, relations);
                collectInheritance(type, typeId, nodes, relations);
                collectFieldDependencies(type, typeId, nodes, relations);
                collectMethods(cu, type, typeId, typeFqn, relativePath, module, nodes, relations);
                return true;
            }
        });
        return new AnalysisResult(new ArrayList<>(nodes), new ArrayList<>(relations));
    }

    private void collectImports(
            CompilationUnit cu,
            String typeId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (Object item : cu.imports()) {
            ImportDeclaration importDeclaration = (ImportDeclaration) item;
            if (importDeclaration.isOnDemand()) {
                continue;
            }
            String importedType = importDeclaration.getName().getFullyQualifiedName();
            String importedTypeId = nodeId(NodeType.TYPE, importedType);
            nodes.add(externalNode(NodeType.TYPE, importedType));
            relations.add(new CodeGraphRelation(typeId, importedTypeId, RelationType.DEPENDS_ON));
        }
    }

    private void collectInheritance(
            TypeDeclaration type,
            String typeId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        if (!type.isInterface() && type.getSuperclassType() != null) {
            String target = resolveTypeName(type.getSuperclassType());
            String targetId = nodeId(NodeType.TYPE, target);
            nodes.add(externalNode(NodeType.TYPE, target));
            relations.add(new CodeGraphRelation(typeId, targetId, RelationType.EXTENDS));
        }

        for (Object item : type.superInterfaceTypes()) {
            Type superInterface = (Type) item;
            String target = resolveTypeName(superInterface);
            String targetId = nodeId(NodeType.TYPE, target);
            nodes.add(externalNode(NodeType.TYPE, target));
            relations.add(new CodeGraphRelation(typeId, targetId, RelationType.IMPLEMENTS));
        }
    }

    private void collectTypeAnnotations(
            TypeDeclaration type,
            String typeId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (Annotation annotation : JdtAstSupport.annotations(type)) {
            String annotationName = annotation.getTypeName().getFullyQualifiedName();
            String annotationId = nodeId(NodeType.ANNOTATION, annotationName);
            nodes.add(externalNode(NodeType.ANNOTATION, annotationName));
            relations.add(new CodeGraphRelation(typeId, annotationId, RelationType.ANNOTATED_BY));
        }
    }

    private void collectFieldDependencies(
            TypeDeclaration type,
            String typeId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (FieldDeclaration field : type.getFields()) {
            String targetType = resolveTypeName(field.getType());
            String targetTypeId = nodeId(NodeType.TYPE, targetType);
            nodes.add(externalNode(NodeType.TYPE, targetType));

            RelationType relationType = hasInjectionAnnotation(field)
                    ? RelationType.INJECTS
                    : RelationType.DEPENDS_ON;
            relations.add(new CodeGraphRelation(typeId, targetTypeId, relationType));
        }
    }

    private void collectMethods(
            CompilationUnit cu,
            TypeDeclaration type,
            String typeId,
            String typeFqn,
            String relativePath,
            String module,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (MethodDeclaration method : type.getMethods()) {
            if (method.isConstructor()) {
                continue;
            }

            String methodFqn = buildMethodFqn(typeFqn, method);
            String methodId = nodeId(NodeType.METHOD, methodFqn);

            nodes.add(new CodeGraphNode(
                    methodId,
                    NodeType.METHOD,
                    method.getName().getIdentifier(),
                    methodFqn,
                    "METHOD",
                    relativePath,
                    module,
                    JdtAstSupport.startLine(cu, method),
                    JdtAstSupport.endLine(cu, method)
            ));
            relations.add(new CodeGraphRelation(typeId, methodId, RelationType.DECLARES));

            collectMethodAnnotations(method, methodId, nodes, relations);
            collectMethodSignatureDependencies(method, methodId, nodes, relations);
            collectMethodCalls(method, methodId, nodes, relations);
        }
    }

    private void collectMethodAnnotations(
            MethodDeclaration method,
            String methodId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (Annotation annotation : JdtAstSupport.annotations(method)) {
            String annotationName = annotation.getTypeName().getFullyQualifiedName();
            String annotationId = nodeId(NodeType.ANNOTATION, annotationName);
            nodes.add(externalNode(NodeType.ANNOTATION, annotationName));
            relations.add(new CodeGraphRelation(methodId, annotationId, RelationType.ANNOTATED_BY));
        }
    }

    private void collectMethodSignatureDependencies(
            MethodDeclaration method,
            String methodId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        String returnType = method.getReturnType2() == null ? "void" : resolveTypeName(method.getReturnType2());
        String returnTypeId = nodeId(NodeType.TYPE, returnType);
        nodes.add(externalNode(NodeType.TYPE, returnType));
        relations.add(new CodeGraphRelation(methodId, returnTypeId, RelationType.RETURNS));

        for (Object item : method.parameters()) {
            SingleVariableDeclaration parameter = (SingleVariableDeclaration) item;
            String parameterType = resolveTypeName(parameter.getType()) + (parameter.isVarargs() ? "..." : "");
            String parameterTypeId = nodeId(NodeType.TYPE, parameterType);
            nodes.add(externalNode(NodeType.TYPE, parameterType));
            relations.add(new CodeGraphRelation(methodId, parameterTypeId, RelationType.HAS_PARAM_TYPE));
        }
    }

    private void collectMethodCalls(
            MethodDeclaration method,
            String callerMethodId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        method.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodInvocation invocation) {
                Optional<String> resolvedTarget = resolveMethodCall(invocation);
                if (resolvedTarget.isEmpty()) {
                    return true;
                }

                String calleeFqn = resolvedTarget.get();
                String calleeId = nodeId(NodeType.METHOD, calleeFqn);
                nodes.add(externalNode(NodeType.METHOD, calleeFqn));
                relations.add(new CodeGraphRelation(callerMethodId, calleeId, RelationType.CALLS));
                return true;
            }
        });
    }

    private Optional<String> resolveMethodCall(MethodInvocation call) {
        try {
            IMethodBinding binding = call.resolveMethodBinding();
            if (binding == null) {
                return Optional.empty();
            }
            IMethodBinding methodBinding = binding.getMethodDeclaration();
            ITypeBinding owner = methodBinding.getDeclaringClass();
            if (owner == null) {
                return Optional.empty();
            }
            return Optional.of(formatQualifiedSignature(owner, methodBinding));
        } catch (RuntimeException ex) {
            log.debug("Failed to resolve method call: {}", call, ex);
            return Optional.empty();
        }
    }

    private boolean hasInjectionAnnotation(BodyDeclaration declaration) {
        return JdtAstSupport.annotations(declaration).stream()
                .map(annotation -> annotation.getTypeName().getFullyQualifiedName())
                .anyMatch(name -> name.equals("Autowired") || name.equals("Inject") || name.equals("Resource"));
    }

    private String buildTypeFqn(String packageName, TypeDeclaration type) {
        return packageName.isBlank() ? type.getName().getIdentifier() : packageName + "." + type.getName().getIdentifier();
    }

    private String buildMethodFqn(String typeFqn, MethodDeclaration method) {
        String parameters = ((List<SingleVariableDeclaration>) method.parameters()).stream()
                .map(parameter -> resolveTypeName(parameter.getType()) + (parameter.isVarargs() ? "..." : ""))
                .collect(Collectors.joining(", "));
        return typeFqn + "#" + method.getName().getIdentifier() + "(" + parameters + ")";
    }

    private String formatQualifiedSignature(ITypeBinding owner, IMethodBinding methodBinding) {
        String ownerName = qualifiedTypeName(owner);
        String parameters = java.util.Arrays.stream(methodBinding.getParameterTypes())
                .map(this::qualifiedTypeName)
                .collect(Collectors.joining(","));
        return ownerName + "." + methodBinding.getName() + "(" + parameters + ")";
    }

    private String qualifiedTypeName(ITypeBinding typeBinding) {
        if (typeBinding == null) {
            return "unknown";
        }
        if (typeBinding.isArray()) {
            return qualifiedTypeName(typeBinding.getElementType()) + "[]".repeat(typeBinding.getDimensions());
        }
        String qualifiedName = typeBinding.getErasure().getQualifiedName();
        return qualifiedName.isBlank() ? typeBinding.getErasure().getName() : qualifiedName;
    }

    private String resolveTypeName(Type type) {
        ITypeBinding binding = type.resolveBinding();
        if (binding == null) {
            return type.toString();
        }
        return qualifiedTypeName(binding);
    }

    private String typeKind(TypeDeclaration type) {
        if (type.isInterface()) {
            return "INTERFACE";
        }
        return org.eclipse.jdt.core.dom.Modifier.isAbstract(type.getModifiers()) ? "ABSTRACT_CLASS" : "CLASS";
    }

    private String extractModule(String relativePath) {
        int index = relativePath.indexOf('/');
        return index < 0 ? relativePath : relativePath.substring(0, index);
    }

    private String nodeId(NodeType type, String fqn) {
        return type.name() + ":" + fqn;
    }

    private CodeGraphNode externalNode(NodeType type, String fqn) {
        String name = fqn;
        int index = Math.max(fqn.lastIndexOf('.'), fqn.lastIndexOf('#'));
        if (index >= 0 && index + 1 < fqn.length()) {
            name = fqn.substring(index + 1);
        }
        return new CodeGraphNode(nodeId(type, fqn), type, name, fqn, null, null, null, null, null);
    }

    public record AnalysisResult(
            List<CodeGraphNode> nodes,
            List<CodeGraphRelation> relations
    ) {
    }
}
