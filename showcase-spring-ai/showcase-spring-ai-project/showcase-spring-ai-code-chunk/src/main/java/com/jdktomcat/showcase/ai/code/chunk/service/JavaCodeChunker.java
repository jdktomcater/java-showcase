package com.jdktomcat.showcase.ai.code.chunk.service;

import com.jdktomcat.showcase.ai.code.chunk.domain.CodeChunk;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JavaCodeChunker {

    @Value("${app.code-rag.repo-name}")
    private String repoName;

    @Value("${app.code-rag.repo-root}")
    private String repoRoot;

    public List<CodeChunk> chunk(Path file) throws IOException {
        String source = Files.readString(file);
        CompilationUnit cu = JdtAstSupport.parse(source);

        String packageName = cu.getPackage() == null
                ? ""
                : cu.getPackage().getName().getFullyQualifiedName();

        String relativePath = Path.of(repoRoot).relativize(file).toString().replace('\\', '/');
        List<CodeChunk> chunks = new ArrayList<>();

        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration type) {
                String className = type.getName().getIdentifier();

                chunks.add(buildChunk(
                        relativePath,
                        packageName,
                        className,
                        null,
                        "class",
                        JdtAstSupport.startLine(cu, type),
                        JdtAstSupport.endLine(cu, type),
                        JdtAstSupport.extractJavadocText(source, type.getJavadoc()),
                        buildClassSignature(type)
                ));

                for (MethodDeclaration method : type.getMethods()) {
                    if (method.isConstructor()) {
                        continue;
                    }
                    chunks.add(buildChunk(
                            relativePath,
                            packageName,
                            className,
                            method.getName().getIdentifier(),
                            "method",
                            JdtAstSupport.startLine(cu, method),
                            JdtAstSupport.endLine(cu, method),
                            JdtAstSupport.extractJavadocText(source, method.getJavadoc()),
                            JdtAstSupport.extractSource(source, method)
                    ));
                }
                return true;
            }
        });

        return chunks;
    }

    private CodeChunk buildChunk(
            String relativePath,
            String packageName,
            String className,
            String methodName,
            String symbolType,
            int startLine,
            int endLine,
            String summary,
            String content
    ) {
        String id = String.join(":",
                repoName,
                relativePath,
                className == null ? "" : className,
                methodName == null ? "" : methodName,
                String.valueOf(startLine),
                String.valueOf(endLine)
        );

        return new CodeChunk(
                id,
                repoName,
                relativePath,
                "java",
                packageName,
                className,
                methodName,
                symbolType,
                startLine,
                endLine,
                summary,
                content,
                DigestUtils.sha256Hex(content)
        );
    }

    private String buildClassSignature(TypeDeclaration type) {
        String modifiers = ((List<IExtendedModifier>) type.modifiers()).stream()
                .filter(IExtendedModifier::isModifier)
                .map(Modifier.class::cast)
                .map(Modifier::getKeyword)
                .map(Modifier.ModifierKeyword::toString)
                .collect(Collectors.joining(" "));

        String typeParameters = type.typeParameters().isEmpty()
                ? ""
                : ((List<Type>) type.typeParameters()).stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "<", ">"));

        String extendsClause = type.isInterface() || type.getSuperclassType() == null
                ? ""
                : " extends " + type.getSuperclassType();

        String superTypesKeyword = type.isInterface() ? " extends " : " implements ";
        String implementsClause = type.superInterfaceTypes().isEmpty()
                ? ""
                : ((List<Type>) type.superInterfaceTypes()).stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", superTypesKeyword, ""));

        return (modifiers.isBlank() ? "" : modifiers + " ")
                + (type.isInterface() ? "interface " : "class ")
                + type.getName().getIdentifier()
                + typeParameters
                + extendsClause
                + implementsClause;
    }
}
