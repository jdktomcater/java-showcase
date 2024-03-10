package jdktomcat.demo.skywalking.dubbo.service.api;

/**
 * 简单服务接口
 */
public interface HelloService {

    /**
     * 简单服务方法
     *
     * @param name 名称
     * @return 组装信息
     */
    String say(String name);

}
