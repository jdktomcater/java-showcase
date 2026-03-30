import com.jdktomcat.showcase.ai.code.assistant.CodeAssistantApplication;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CodeAssistantApplication.class)
// 测试结束后自动回滚事务，避免污染数据库
@Transactional
public class CodeAssistantApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(System.getenv("OLLAMA_BASE_URL"));
    }

    @Test
    void testAnalysisWorkflow() {
    }
}
