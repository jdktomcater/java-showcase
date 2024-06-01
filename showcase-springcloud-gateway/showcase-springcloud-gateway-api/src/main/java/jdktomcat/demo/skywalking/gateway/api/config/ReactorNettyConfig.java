package jdktomcat.demo.skywalking.gateway.api.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

@Data
@Configuration
@Slf4j
public class ReactorNettyConfig implements InitializingBean, ApplicationContextAware {

    @Value("${reactor.ipc.netty.select:4}")
    private String select;

    @Value("${reactor.ipc.netty.worker:64}")
    private String worker;


    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void afterPropertiesSet() {
        if (applicationContext instanceof ReactiveWebServerApplicationContext) {
            ReactiveWebServerApplicationContext reactiveWebServerApplicationContext = (ReactiveWebServerApplicationContext) applicationContext;
            try {
                Field fServerManager = ReactiveWebServerApplicationContext.class.getDeclaredField("serverManager");
                fServerManager.setAccessible(true);
                Object serverManager = fServerManager.get(reactiveWebServerApplicationContext);
                Field fServer = serverManager.getClass().getDeclaredField("server");
                fServer.setAccessible(true);
                Object server = fServer.get(serverManager);
                Field fHttpServer = server.getClass().getDeclaredField("httpServer");
                fHttpServer.setAccessible(true);
                Object httpServer = fHttpServer.get(server);
                Field fOptions = httpServer.getClass().getDeclaredField("options");
                fOptions.setAccessible(true);
                Object options = fOptions.get(httpServer);
                Field fLoopResources = options.getClass().getSuperclass().getSuperclass().getDeclaredField("loopResources");
                fLoopResources.setAccessible(true);
                Object loopResources = fLoopResources.get(options);
                Field fDefaultLoops = loopResources.getClass().getSuperclass().getDeclaredField("defaultLoops");
                fDefaultLoops.setAccessible(true);
                Object defaultLoops = fDefaultLoops.get(loopResources);
                Field fSelectCount = defaultLoops.getClass().getDeclaredField("selectCount");
                fSelectCount.setAccessible(true);
                fSelectCount.set(defaultLoops, Integer.parseInt(select));
                Field fWorkerCount = defaultLoops.getClass().getDeclaredField("workerCount");
                fWorkerCount.setAccessible(true);
                fWorkerCount.set(defaultLoops, Integer.parseInt(worker));
            } catch (Exception exception) {
                log.error("reset reactor netty config error", exception);
            }
        }
    }
}
