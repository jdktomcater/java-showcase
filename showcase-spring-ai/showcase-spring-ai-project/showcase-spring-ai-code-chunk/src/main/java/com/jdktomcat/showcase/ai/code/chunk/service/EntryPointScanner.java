package com.jdktomcat.showcase.ai.code.chunk.service;

import com.jdktomcat.showcase.ai.code.chunk.domain.EntryPoint;
import com.jdktomcat.showcase.ai.code.chunk.domain.EntryPointType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans Java source files to detect entry points using AST parsing and pattern matching.
 */
@Service
public class EntryPointScanner {

    private static final Logger log = LoggerFactory.getLogger(EntryPointScanner.class);

    private final Path repoRoot;

    // Spring MVC annotations
    private static final Pattern REST_CONTROLLER_PATTERN = Pattern.compile("@RestController");
    private static final Pattern CONTROLLER_PATTERN = Pattern.compile("@Controller");
    private static final Pattern REQUEST_MAPPING_PATTERN = Pattern.compile("@RequestMapping");
    private static final Pattern GET_MAPPING_PATTERN = Pattern.compile("@GetMapping");
    private static final Pattern POST_MAPPING_PATTERN = Pattern.compile("@PostMapping");
    private static final Pattern PUT_MAPPING_PATTERN = Pattern.compile("@PutMapping");
    private static final Pattern DELETE_MAPPING_PATTERN = Pattern.compile("@DeleteMapping");
    private static final Pattern PATCH_MAPPING_PATTERN = Pattern.compile("@PatchMapping");

    // Dubbo annotations
    private static final Pattern DUBBO_SERVICE_PATTERN = Pattern.compile("@(DubboService|Service)");
    private static final Pattern DUBBO_REFERENCE_PATTERN = Pattern.compile("@DubboReference");

    // MQ annotations
    private static final Pattern RABBIT_LISTENER_PATTERN = Pattern.compile("@RabbitListener");
    private static final Pattern KAFKA_LISTENER_PATTERN = Pattern.compile("@KafkaListener");
    private static final Pattern ROCKETMQ_LISTENER_PATTERN = Pattern.compile("@RocketMQMessageListener");

    // Scheduled annotations
    private static final Pattern SCHEDULED_PATTERN = Pattern.compile("@Scheduled");
    private static final Pattern ENABLE_SCHEDULING_PATTERN = Pattern.compile("@EnableScheduling");

    // Event annotations
    private static final Pattern EVENT_LISTENER_PATTERN = Pattern.compile("@EventListener");
    private static final Pattern TRANSACTIONAL_EVENT_LISTENER_PATTERN = Pattern.compile("@TransactionalEventListener");

    // Custom impact analysis annotations
    private static final Pattern HTTP_ENTRY_PATTERN = Pattern.compile("@HttpEntry");
    private static final Pattern RPC_ENTRY_PATTERN = Pattern.compile("@RpcEntry");
    private static final Pattern MQ_ENTRY_PATTERN = Pattern.compile("@MqEntry");
    private static final Pattern SCHEDULED_ENTRY_PATTERN = Pattern.compile("@ScheduledEntry");
    private static final Pattern EVENT_ENTRY_PATTERN = Pattern.compile("@EventEntry");

    public EntryPointScanner(@Value("${app.impact-analysis.repo-root:#{null}}") String repoRoot) {
        this.repoRoot = repoRoot != null && !repoRoot.isBlank() ? Path.of(repoRoot) : Path.of(".").toAbsolutePath();
    }

    /**
     * Scan all Java files in the repository and detect entry points.
     */
    public List<EntryPoint> scan() throws IOException {
        List<EntryPoint> entryPoints = new ArrayList<>();
        List<Path> javaFiles = findJavaFiles();

        for (Path file : javaFiles) {
            try {
                List<EntryPoint> fileEntryPoints = analyzeFile(file);
                entryPoints.addAll(fileEntryPoints);
            } catch (Exception e) {
                log.warn("Failed to analyze file for entry points: {}", file, e);
            }
        }

        log.info("Scanned {} Java files, found {} entry points", javaFiles.size(), entryPoints.size());
        return entryPoints;
    }

    private List<Path> findJavaFiles() throws IOException {
        List<Path> javaFiles = new ArrayList<>();
        Files.walk(repoRoot)
                .filter(path -> path.toString().endsWith(".java"))
                .filter(path -> !path.toString().contains("/target/") && !path.toString().contains("/build/"))
                .forEach(javaFiles::add);
        return javaFiles;
    }

    private List<EntryPoint> analyzeFile(Path file) throws IOException {
        List<EntryPoint> entryPoints = new ArrayList<>();
        String content = Files.readString(file);
        String relativePath = repoRoot.relativize(file).toString();
        String module = extractModule(relativePath);
        String[] lines = content.split("\n");

        // Parse imports to resolve fully qualified names
        List<String> imports = extractImports(content);

        // Find class declarations and their annotations
        List<ClassContext> classContexts = findClassContexts(content, lines, relativePath, module, imports);

        // Find methods and their annotations
        for (ClassContext classContext : classContexts) {
            List<MethodContext> methods = findMethods(content, lines, classContext, imports);
            for (MethodContext method : methods) {
                if (method.isEntryPoint()) {
                    entryPoints.add(method.toEntryPoint(relativePath, module));
                }
            }
        }

        return entryPoints;
    }

    private List<String> extractImports(String content) {
        List<String> imports = new ArrayList<>();
        Pattern importPattern = Pattern.compile("^\\s*import\\s+([\\w.]+);", Pattern.MULTILINE);
        Matcher matcher = importPattern.matcher(content);
        while (matcher.find()) {
            imports.add(matcher.group(1));
        }
        return imports;
    }

    private List<ClassContext> findClassContexts(String content, String[] lines, String filePath, String module, List<String> imports) {
        List<ClassContext> contexts = new ArrayList<>();

        // Simple pattern-based class detection
        Pattern classPattern = Pattern.compile(
                "^\\s*(public\\s+)?(abstract\\s+)?(class|interface|enum)\\s+(\\w+)",
                Pattern.MULTILINE
        );
        Matcher matcher = classPattern.matcher(content);

        while (matcher.find()) {
            int lineNumber = getLineNumber(content, matcher.start());
            String className = matcher.group(4);
            String classType = matcher.group(3);

            // Check for class-level annotations
            List<String> annotations = getClassAnnotations(content, matcher.start());

            contexts.add(new ClassContext(
                    className,
                    classType,
                    filePath,
                    module,
                    lineNumber,
                    annotations,
                    imports
            ));
        }

        return contexts;
    }

    private List<MethodContext> findMethods(String content, String[] lines, ClassContext classContext, List<String> imports) {
        List<MethodContext> methods = new ArrayList<>();

        // Pattern to match method declarations
        Pattern methodPattern = Pattern.compile(
                "^\\s*(public|private|protected)?\\s*(static\\s+)?(\\w+(?:<[^>]+>)?)\\s+(\\w+)\\s*\\([^)]*\\)",
                Pattern.MULTILINE
        );
        Matcher matcher = methodPattern.matcher(content);

        while (matcher.find()) {
            int lineNumber = getLineNumber(content, matcher.start());
            String methodName = matcher.group(4);
            String returnType = matcher.group(3);

            // Check if this method is inside our class context
            if (!isMethodInClassContext(matcher.start(), classContext, content)) {
                continue;
            }

            // Get method annotations
            List<String> annotations = getMethodAnnotations(content, matcher.start());

            // Determine if this is an entry point and its type
            EntryPointType entryPointType = determineEntryPointType(annotations, classContext.annotations());
            String metadata = buildMetadata(annotations, entryPointType);

            if (entryPointType != null) {
                String methodSignature = buildMethodSignature(classContext.className(), methodName, returnType);
                methods.add(new MethodContext(
                        classContext.className(),
                        methodName,
                        methodSignature,
                        lineNumber,
                        entryPointType,
                        metadata,
                        annotations
                ));
            }
        }

        return methods;
    }

    private boolean isMethodInClassContext(int methodPos, ClassContext classContext, String content) {
        // Find the class start position
        Pattern classPattern = Pattern.compile(
                "(class|interface|enum)\\s+" + Pattern.quote(classContext.className()) + "\\s*\\{",
                Pattern.MULTILINE
        );
        Matcher matcher = classPattern.matcher(content);
        if (matcher.find()) {
            int classStart = matcher.start();
            int classEnd = findMatchingBrace(content, matcher.end() - 1);
            return methodPos > classStart && methodPos < classEnd;
        }
        return false;
    }

    private int findMatchingBrace(String content, int openBracePos) {
        int count = 1;
        for (int i = openBracePos + 1; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') count++;
            else if (c == '}') count--;
            if (count == 0) return i;
        }
        return content.length();
    }

    private List<String> getClassAnnotations(String content, int classPos) {
        return getAnnotationsBefore(content, classPos);
    }

    private List<String> getMethodAnnotations(String content, int methodPos) {
        return getAnnotationsBefore(content, methodPos);
    }

    private List<String> getAnnotationsBefore(String content, int position) {
        List<String> annotations = new ArrayList<>();

        // Look backwards from the position to find annotations
        int searchStart = Math.max(0, position - 500);
        String beforeContent = content.substring(searchStart, position);

        Pattern annotationPattern = Pattern.compile("@(\\w+)(?:\\([^)]*\\))?");
        Matcher matcher = annotationPattern.matcher(beforeContent);

        while (matcher.find()) {
            annotations.add(matcher.group(1));
        }

        return annotations;
    }

    private EntryPointType determineEntryPointType(List<String> methodAnnotations, List<String> classAnnotations) {
        // Check custom annotations first
        if (methodAnnotations.contains("HttpEntry")) return EntryPointType.HTTP;
        if (methodAnnotations.contains("RpcEntry")) return EntryPointType.RPC;
        if (methodAnnotations.contains("MqEntry")) return EntryPointType.MQ;
        if (methodAnnotations.contains("ScheduledEntry")) return EntryPointType.SCHEDULED;
        if (methodAnnotations.contains("EventEntry")) return EntryPointType.EVENT;

        // Check Spring MVC annotations
        boolean isController = classAnnotations.contains("RestController") ||
                               classAnnotations.contains("Controller") ||
                               methodAnnotations.stream().anyMatch(a ->
                                   a.contains("Mapping"));
        if (isController && methodAnnotations.stream().anyMatch(a ->
                a.contains("Mapping"))) {
            return EntryPointType.HTTP;
        }

        // Check Dubbo annotations
        if (classAnnotations.contains("DubboService") || classAnnotations.contains("Service")) {
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

    private String buildMetadata(List<String> annotations, EntryPointType type) {
        StringBuilder metadata = new StringBuilder();

        switch (type) {
            case HTTP -> {
                // Extract path and methods from mapping annotations
                for (String annotation : annotations) {
                    if (annotation.contains("Mapping")) {
                        metadata.append(annotation).append(";");
                    }
                }
            }
            case RPC -> {
                for (String annotation : annotations) {
                    if (annotation.equals("DubboService") || annotation.equals("Service")) {
                        metadata.append("protocol=DUBBO;");
                    }
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
                for (String annotation : annotations) {
                    if (annotation.equals("Scheduled")) {
                        metadata.append(annotation).append(";");
                    }
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

    private String buildMethodSignature(String className, String methodName, String returnType) {
        return className + "#" + methodName + "():" + returnType;
    }

    private int getLineNumber(String content, int position) {
        int lineNumber = 1;
        for (int i = 0; i < position && i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                lineNumber++;
            }
        }
        return lineNumber;
    }

    private String extractModule(String relativePath) {
        int index = relativePath.indexOf('/');
        return index < 0 ? relativePath : relativePath.substring(0, index);
    }

    private record ClassContext(
            String className,
            String classType,
            String filePath,
            String module,
            int lineNumber,
            List<String> annotations,
            List<String> imports
    ) {
    }

    private record MethodContext(
            String className,
            String methodName,
            String methodSignature,
            int lineNumber,
            EntryPointType entryPointType,
            String metadata,
            List<String> annotations
    ) {
        public boolean isEntryPoint() {
            return entryPointType != null;
        }

        public EntryPoint toEntryPoint(String filePath, String module) {
            return new EntryPoint(
                    entryPointType.name() + ":" + methodSignature,
                    entryPointType,
                    className,
                    methodName,
                    methodSignature,
                    filePath,
                    module,
                    lineNumber,
                    metadata
            );
        }
    }
}
