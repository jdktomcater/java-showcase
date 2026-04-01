package com.jdktomcat.showcase.ai.code.chunk.service;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.Javadoc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
final class JdtAstSupport {

    private static final Map<String, String> COMPILER_OPTIONS = createCompilerOptions();

    private JdtAstSupport() {
    }

    static CompilationUnit parse(String source) throws IOException {
        ASTParser parser = ASTParser.newParser(AST.JLS17);
        configureCommon(parser, source);
        CompilationUnit unit = (CompilationUnit) parser.createAST(null);
        validate(unit, "source");
        return unit;
    }

    static CompilationUnit parse(Path file, String source, Path repoRoot) throws IOException {
        ASTParser parser = ASTParser.newParser(AST.JLS17);
        configureCommon(parser, source);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        List<Path> sourceRoots = collectSourceRoots(repoRoot);
        String[] sourcePathEntries = sourceRoots.stream().map(Path::toString).toArray(String[]::new);
        String[] encodings = sourceRoots.stream().map(path -> StandardCharsets.UTF_8.name()).toArray(String[]::new);
        parser.setEnvironment(currentClasspathEntries(), sourcePathEntries, encodings, true);
        parser.setUnitName(resolveUnitName(file, sourceRoots));
        return (CompilationUnit) parser.createAST(null);
    }

    static List<Path> collectSourceRoots(Path root) {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(Files::isDirectory).filter(JdtAstSupport::isJavaSourceRoot).toList();
        } catch (IOException ex) {
            return List.of();
        }
    }

    static boolean isJavaSourceRoot(Path path) {
        String normalized = normalize(path);
        return normalized.endsWith("/src/main/java") || normalized.endsWith("/src/test/java");
    }

    static String normalize(Path path) {
        return path.toString().replace('\\', '/');
    }

    static int startLine(CompilationUnit cu, ASTNode node) {
        int line = cu.getLineNumber(node.getStartPosition());
        return line <= 0 ? -1 : line;
    }

    static int endLine(CompilationUnit cu, ASTNode node) {
        int endPosition = node.getStartPosition() + Math.max(node.getLength() - 1, 0);
        int line = cu.getLineNumber(endPosition);
        return line <= 0 ? -1 : line;
    }

    static String extractSource(String source, ASTNode node) {
        int start = Math.max(0, node.getStartPosition());
        int end = Math.min(source.length(), start + node.getLength());
        return source.substring(start, end);
    }

    static String extractJavadocText(String source, Javadoc javadoc) {
        if (javadoc == null) {
            return "";
        }
        String raw = extractSource(source, javadoc);
        String withoutOpen = raw.replaceFirst("^/\\*\\*", "");
        String withoutClose = withoutOpen.replaceFirst("\\*/\\s*$", "");
        return Arrays.stream(withoutClose.split("\\R"))
                .map(String::strip)
                .map(line -> line.startsWith("*") ? line.substring(1).stripLeading() : line)
                .collect(Collectors.joining("\n"))
                .trim();
    }

    static List<Annotation> annotations(BodyDeclaration declaration) {
        List<Annotation> annotations = new ArrayList<>();
        for (Object modifier : declaration.modifiers()) {
            if (modifier instanceof IExtendedModifier extendedModifier && extendedModifier.isAnnotation()) {
                annotations.add((Annotation) extendedModifier);
            }
        }
        return annotations;
    }

    private static void configureCommon(ASTParser parser, String source) {
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source.toCharArray());
        parser.setCompilerOptions(new HashMap<>(COMPILER_OPTIONS));
        parser.setStatementsRecovery(true);
    }

    private static String[] currentClasspathEntries() {
        return Arrays.stream(System.getProperty("java.class.path", "").split(File.pathSeparator))
                .filter(entry -> !entry.isBlank())
                .toArray(String[]::new);
    }

    private static String resolveUnitName(Path file, List<Path> sourceRoots) {
        String normalizedFile = normalize(file);
        for (Path sourceRoot : sourceRoots) {
            if (normalizedFile.startsWith(normalize(sourceRoot) + "/")) {
                return normalize(sourceRoot.relativize(file));
            }
        }
        return file.getFileName().toString();
    }

    private static void validate(CompilationUnit unit, String sourceName) throws IOException {
        List<String> errors = Arrays.stream(unit.getProblems()).filter(IProblem::isError).map(IProblem::getMessage).toList();
        if (!errors.isEmpty()) {
            throw new IOException("Parse failed for " + sourceName + ": " + errors);
        }
    }

    private static Map<String, String> createCompilerOptions() {
        Map<String, String> options = new HashMap<>(JavaCore.getOptions());
        JavaCore.setComplianceOptions(JavaCore.VERSION_17, options);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_17);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_17);
        return Collections.unmodifiableMap(options);
    }
}
