package com.jdktomcat.showcase.data.druid.skywalking.dubbo.service.provider;

import com.jdktomcat.showcase.data.druid.skywalking.dubbo.service.api.HelloService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 简易服务提供者实现类
 */
@DubboService
public class HelloServiceImpl implements HelloService {
    /**
     * 简单服务方法
     *
     * @param name 名称
     * @return 组装信息
     */
    @Override
    public String say(String name) {
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return "Hello " + name;
    }
}
