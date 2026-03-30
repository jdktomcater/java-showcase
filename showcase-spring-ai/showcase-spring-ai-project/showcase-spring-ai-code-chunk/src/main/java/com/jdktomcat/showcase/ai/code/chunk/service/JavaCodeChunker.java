package com.jdktomcat.showcase.ai.code.chunk.service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.jdktomcat.showcase.ai.code.chunk.domain.CodeChunk;
import org.apache.commons.codec.digest.DigestUtils;
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
        CompilationUnit cu = StaticJavaParser.parse(source);

        String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getName().asString())
                .orElse("");

        String relativePath = Path.of(repoRoot).relativize(file).toString().replace('\\', '/');
        List<CodeChunk> chunks = new ArrayList<>();

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
            String className = clazz.getNameAsString();

            int classStart = clazz.getBegin().map(p -> p.line).orElse(-1);
            int classEnd = clazz.getEnd().map(p -> p.line).orElse(-1);
            String classDoc = clazz.getJavadoc().map(j -> j.toText()).orElse("");
            String classContent = buildClassSignature(clazz);

            chunks.add(buildChunk(
                    relativePath,
                    packageName,
                    className,
                    null,
                    "class",
                    classStart,
                    classEnd,
                    classDoc,
                    classContent
            ));

            for (MethodDeclaration method : clazz.getMethods()) {
                int start = method.getBegin().map(p -> p.line).orElse(-1);
                int end = method.getEnd().map(p -> p.line).orElse(-1);
                String doc = method.getJavadoc().map(j -> j.toText()).orElse("");
                String content = method.toString();

                chunks.add(buildChunk(
                        relativePath,
                        packageName,
                        className,
                        method.getNameAsString(),
                        "method",
                        start,
                        end,
                        doc,
                        content
                ));
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

    private String buildClassSignature(ClassOrInterfaceDeclaration clazz) {
        String modifiers = clazz.getModifiers().stream()
                .map(modifier -> modifier.getKeyword().asString())
                .collect(Collectors.joining(" "));
        String typeParameters = clazz.getTypeParameters().isEmpty()
                ? ""
                : clazz.getTypeParameters().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "<", ">"));
        String extendsClause = clazz.getExtendedTypes().isEmpty()
                ? ""
                : clazz.getExtendedTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", " extends ", ""));
        String implementsClause = clazz.getImplementedTypes().isEmpty()
                ? ""
                : clazz.getImplementedTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", " implements ", ""));

        return (modifiers.isBlank() ? "" : modifiers + " ")
                + (clazz.isInterface() ? "interface " : "class ")
                + clazz.getNameAsString()
                + typeParameters
                + extendsClause
                + implementsClause;
    }
}
