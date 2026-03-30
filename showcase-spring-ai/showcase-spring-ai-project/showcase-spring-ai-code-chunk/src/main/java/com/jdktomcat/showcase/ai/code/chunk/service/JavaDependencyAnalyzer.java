package com.jdktomcat.showcase.ai.code.chunk.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.jdktomcat.showcase.ai.code.chunk.domain.CodeGraphNode;
import com.jdktomcat.showcase.ai.code.chunk.domain.CodeGraphRelation;
import com.jdktomcat.showcase.ai.code.chunk.domain.NodeType;
import com.jdktomcat.showcase.ai.code.chunk.domain.RelationType;
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

@Service
public class JavaDependencyAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(JavaDependencyAnalyzer.class);

    private final JavaParser javaParser;
    private final Path repoRoot;

    public JavaDependencyAnalyzer(@Value("${app.code-rag.repo-root}") String repoRoot) {
        this.repoRoot = Path.of(repoRoot);
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        registerSourceRoots(typeSolver, this.repoRoot);

        ParserConfiguration configuration = new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17)
                .setSymbolResolver(new JavaSymbolSolver(typeSolver));
        this.javaParser = new JavaParser(configuration);
    }

    public AnalysisResult analyze(Path file) throws IOException {
        String source = Files.readString(file);
        ParseResult<CompilationUnit> parseResult = javaParser.parse(source);
        CompilationUnit cu = parseResult.getResult()
                .orElseThrow(() -> new IOException("Parse failed for " + file + ": " + parseResult.getProblems()));

        String relativePath = normalize(repoRoot.relativize(file));
        String module = extractModule(relativePath);
        String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");

        Set<CodeGraphNode> nodes = new LinkedHashSet<>();
        Set<CodeGraphRelation> relations = new LinkedHashSet<>();

        for (ClassOrInterfaceDeclaration type : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            String typeFqn = buildTypeFqn(packageName, type);
            String typeId = nodeId(NodeType.TYPE, typeFqn);
            nodes.add(new CodeGraphNode(
                    typeId,
                    NodeType.TYPE,
                    type.getNameAsString(),
                    typeFqn,
                    relativePath,
                    module,
                    type.getBegin().map(p -> p.line).orElse(-1),
                    type.getEnd().map(p -> p.line).orElse(-1)
            ));

            collectTypeAnnotations(type, typeId, nodes, relations);
            collectImports(cu, typeId, nodes, relations);
            collectInheritance(type, typeId, nodes, relations);
            collectFieldDependencies(type, typeId, nodes, relations);
            collectMethods(type, typeId, typeFqn, relativePath, module, nodes, relations);
        }

        return new AnalysisResult(new ArrayList<>(nodes), new ArrayList<>(relations));
    }

    private void collectImports(
            CompilationUnit cu,
            String typeId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (ImportDeclaration importDeclaration : cu.getImports()) {
            if (importDeclaration.isAsterisk()) {
                continue;
            }
            String importedType = importDeclaration.getNameAsString();
            String importedTypeId = nodeId(NodeType.TYPE, importedType);
            nodes.add(externalNode(NodeType.TYPE, importedType));
            relations.add(new CodeGraphRelation(typeId, importedTypeId, RelationType.DEPENDS_ON));
        }
    }

    private void collectInheritance(
            ClassOrInterfaceDeclaration type,
            String typeId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (ClassOrInterfaceType extendedType : type.getExtendedTypes()) {
            String target = extendedType.getNameAsString();
            String targetId = nodeId(NodeType.TYPE, target);
            nodes.add(externalNode(NodeType.TYPE, target));
            relations.add(new CodeGraphRelation(typeId, targetId, RelationType.EXTENDS));
        }

        for (ClassOrInterfaceType implementedType : type.getImplementedTypes()) {
            String target = implementedType.getNameAsString();
            String targetId = nodeId(NodeType.TYPE, target);
            nodes.add(externalNode(NodeType.TYPE, target));
            relations.add(new CodeGraphRelation(typeId, targetId, RelationType.IMPLEMENTS));
        }
    }

    private void collectTypeAnnotations(
            ClassOrInterfaceDeclaration type,
            String typeId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (AnnotationExpr annotation : type.getAnnotations()) {
            String annotationName = annotation.getNameAsString();
            String annotationId = nodeId(NodeType.ANNOTATION, annotationName);
            nodes.add(externalNode(NodeType.ANNOTATION, annotationName));
            relations.add(new CodeGraphRelation(typeId, annotationId, RelationType.ANNOTATED_BY));
        }
    }

    private void collectFieldDependencies(
            ClassOrInterfaceDeclaration type,
            String typeId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (FieldDeclaration field : type.getFields()) {
            String targetType = field.getElementType().asString();
            String targetTypeId = nodeId(NodeType.TYPE, targetType);
            nodes.add(externalNode(NodeType.TYPE, targetType));

            RelationType relationType = hasInjectionAnnotation(field.getAnnotations())
                    ? RelationType.INJECTS
                    : RelationType.DEPENDS_ON;
            relations.add(new CodeGraphRelation(typeId, targetTypeId, relationType));
        }
    }

    private void collectMethods(
            ClassOrInterfaceDeclaration type,
            String typeId,
            String typeFqn,
            String relativePath,
            String module,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (MethodDeclaration method : type.getMethods()) {
            String methodFqn = buildMethodFqn(typeFqn, method);
            String methodId = nodeId(NodeType.METHOD, methodFqn);

            nodes.add(new CodeGraphNode(
                    methodId,
                    NodeType.METHOD,
                    method.getNameAsString(),
                    methodFqn,
                    relativePath,
                    module,
                    method.getBegin().map(p -> p.line).orElse(-1),
                    method.getEnd().map(p -> p.line).orElse(-1)
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
        for (AnnotationExpr annotation : method.getAnnotations()) {
            String annotationName = annotation.getNameAsString();
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
        String returnType = method.getType().asString();
        String returnTypeId = nodeId(NodeType.TYPE, returnType);
        nodes.add(externalNode(NodeType.TYPE, returnType));
        relations.add(new CodeGraphRelation(methodId, returnTypeId, RelationType.RETURNS));

        method.getParameters().forEach(parameter -> {
            String parameterType = parameter.getType().asString();
            String parameterTypeId = nodeId(NodeType.TYPE, parameterType);
            nodes.add(externalNode(NodeType.TYPE, parameterType));
            relations.add(new CodeGraphRelation(methodId, parameterTypeId, RelationType.HAS_PARAM_TYPE));
        });
    }

    private void collectMethodCalls(
            MethodDeclaration method,
            String callerMethodId,
            Set<CodeGraphNode> nodes,
            Set<CodeGraphRelation> relations
    ) {
        for (MethodCallExpr call : method.findAll(MethodCallExpr.class)) {
            Optional<String> resolvedTarget = resolveMethodCall(call);
            if (resolvedTarget.isEmpty()) {
                continue;
            }

            String calleeFqn = resolvedTarget.get();
            String calleeId = nodeId(NodeType.METHOD, calleeFqn);
            nodes.add(externalNode(NodeType.METHOD, calleeFqn));
            relations.add(new CodeGraphRelation(callerMethodId, calleeId, RelationType.CALLS));
        }
    }

    private Optional<String> resolveMethodCall(MethodCallExpr call) {
        try {
            ResolvedMethodDeclaration resolved = call.resolve();
            return Optional.of(resolved.getQualifiedSignature());
        } catch (UnsolvedSymbolException ex) {
            log.debug("Unresolved method call: {}", call, ex);
            return Optional.empty();
        } catch (RuntimeException ex) {
            log.debug("Failed to resolve method call: {}", call, ex);
            return Optional.empty();
        }
    }

    private boolean hasInjectionAnnotation(List<AnnotationExpr> annotations) {
        return annotations.stream()
                .map(AnnotationExpr::getNameAsString)
                .anyMatch(name -> name.equals("Autowired") || name.equals("Inject") || name.equals("Resource"));
    }

    private void registerSourceRoots(CombinedTypeSolver typeSolver, Path root) {
        try (var paths = Files.walk(root)) {
            paths.filter(Files::isDirectory)
                    .filter(this::isJavaSourceRoot)
                    .forEach(path -> typeSolver.add(new JavaParserTypeSolver(path.toFile())));
        } catch (IOException ex) {
            log.warn("Failed to scan Java source roots from {}", root, ex);
        }
    }

    private boolean isJavaSourceRoot(Path path) {
        String normalized = normalize(path);
        return normalized.endsWith("/src/main/java") || normalized.endsWith("/src/test/java");
    }

    private String buildTypeFqn(String packageName, ClassOrInterfaceDeclaration type) {
        return packageName.isBlank() ? type.getNameAsString() : packageName + "." + type.getNameAsString();
    }

    private String buildMethodFqn(String typeFqn, MethodDeclaration method) {
        return typeFqn + "#" + method.getSignature().asString();
    }

    private String extractModule(String relativePath) {
        int index = relativePath.indexOf('/');
        return index < 0 ? relativePath : relativePath.substring(0, index);
    }

    private String normalize(Path path) {
        return path.toString().replace('\\', '/');
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
        return new CodeGraphNode(nodeId(type, fqn), type, name, fqn, null, null, null, null);
    }

    public record AnalysisResult(
            List<CodeGraphNode> nodes,
            List<CodeGraphRelation> relations
    ) {
    }
}
