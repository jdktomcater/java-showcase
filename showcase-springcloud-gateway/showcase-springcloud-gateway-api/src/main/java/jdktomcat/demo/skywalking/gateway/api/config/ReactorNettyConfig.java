package jdktomcat.demo.skywalking.gateway.api.config;

import lombok.Data;
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
public class ReactorNettyConfig implements InitializingBean , ApplicationContextAware {
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
        System.setProperty("reactor.ipc.netty.workerCount", worker);
        System.setProperty("reactor.ipc.netty.selectCount", select);
        if(applicationContext instanceof ReactiveWebServerApplicationContext){
            ReactiveWebServerApplicationContext reactiveWebServerApplicationContext = (ReactiveWebServerApplicationContext)applicationContext;
            try{
                Field field = ReactiveWebServerApplicationContext.class.getDeclaredField("serverManager");
                field.setAccessible(true);
                Object fieldValue = field.get(reactiveWebServerApplicationContext);
            }catch (Exception exception){

            }

        }
    }
}
