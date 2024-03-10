
package com.jdktomcat.demo.redis.delay.keyspace.notify.scheduled;

import com.jdktomcat.demo.redis.delay.keyspace.notify.listener.KeyExpiredListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : aleck
 * @Description: redis消息刷新配置
 * @date : 2021年03月18日 10:29
 */
@Component
@Slf4j
public class RedisClusterNodeFactory {

    @Value("${spring.data.redis.password:JnWgl1aXjjdjZK8ZuX5c}")
    private String rPwd;


    @Value("${spring.data.redis.serverName:RedisDelay}")
    private String serverName;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;
    
    @Resource
    private KeyExpiredListener messageListener;

    @Autowired
    private ThreadPoolTaskExecutor taskAsyncPool;


    /**
     * 用于存储已经启动监听的master节点信息
     */
    private final ConcurrentHashMap<String, String> masterNodeMap = new ConcurrentHashMap<>();
    
    @Scheduled(cron = "0/5 * * * * ?")
    public void refreshClusterNode() {
        RedisClusterConnection clusterConnection = redisConnectionFactory.getClusterConnection();
        Iterable<RedisClusterNode> redisClusterNodes = clusterConnection.clusterGetNodes();
        // 用于获取当前的master节点信息
        HashMap<String,String> currentMasterNodeMap = new HashMap<>();
        redisClusterNodes.forEach(redisClusterNode -> {
            if (redisClusterNode.isMaster()) {
                String clusterNodeName = "clusterNodeName" + redisClusterNode.hashCode();
                String hostPort = redisClusterNode.getHost() + ":" + redisClusterNode.getPort();
                currentMasterNodeMap.put(clusterNodeName, hostPort);
                // 不进行重复创建监听
                if (masterNodeMap.containsKey(clusterNodeName)) {
                    return;
                }
                masterNodeMap.put(clusterNodeName, hostPort);
                Config config = new Config();
                SingleServerConfig singleServerConfig = config.useSingleServer();
                singleServerConfig.setAddress("redis://" + redisClusterNode.getHost() + ":" + redisClusterNode.getPort());
                singleServerConfig.setPassword(rPwd);
                singleServerConfig.setKeepAlive(true);
                singleServerConfig.setDatabase(0);
                singleServerConfig.setClientName(serverName);
                RedisConnectionFactory connectionFactory = new RedissonConnectionFactory(Redisson.create(config));
                RedisMessageListenerContainer container = new RedisMessageListenerContainer();
                container.setConnectionFactory(connectionFactory);
                container.setTaskExecutor(taskAsyncPool);
                // 设置监听的Topic
                ChannelTopic channelTopic = new ChannelTopic("__keyevent@0__:expired");
                // 设置监听器
                container.addMessageListener(messageListener, channelTopic);
                log.info("设置服务器节点监听器host：{}，port:{}" ,redisClusterNode.getHost(), redisClusterNode.getPort());
                // 必须调用，否则空指针异常
                container.afterPropertiesSet();
                // 启动监控
                container.start();
            }
        });

        // 比较2个map中的值，去除不存在的值
        masterNodeMap.forEach((key, value) -> {
            if (!currentMasterNodeMap.containsKey(key)) {
                log.info("去除不存在的服务器节点:" + value);
                masterNodeMap.remove(key);
            }
        });
    }
}
