import com.jdktomcat.showcase.ai.langgraph4j.MultiAgentApplication;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MultiAgentApplication.class)
// 测试结束后自动回滚事务，避免污染数据库
@Transactional
public class MultiAgentApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(System.getenv("OLLAMA_BASE_URL"));
    }

    @Test
    void testAnalysisWorkflow() {
    }
}
