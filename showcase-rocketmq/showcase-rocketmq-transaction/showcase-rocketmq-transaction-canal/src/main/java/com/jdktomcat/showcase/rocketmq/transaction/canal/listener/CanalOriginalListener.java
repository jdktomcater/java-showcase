package com.jdktomcat.showcase.rocketmq.transaction.canal.listener;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
@Component
public class CanalOriginalListener {

    @Value("canal.sever")
    private String server;

    @Value("canal.port")
    private Integer port;

    @Value("canal.destination")
    private String destination;

    @Value("canal.user")
    private String user;

    @Value("canal.password")
    private String password;

    @Value("canal.batch:100")
    private Integer batch;

    @Value("canal.subscribe")
    private String subscribe;

    /**
     * 解析数据
     *
     * @param beforeColumns 修改、删除后的数据
     * @param afterColumns  新增、修改、删除前的数据
     * @param dbName        数据库名字
     * @param tableName     表大的名字
     * @param eventType     操作类型（INSERT,UPDATE,DELETE）
     * @param timestamp     消耗时间
     */
    private static void dataDetails(List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns, String dbName, String tableName, CanalEntry.EventType eventType, long timestamp) {
        log.info("db:{} table:{} eventType:{}",dbName,tableName,eventType);
        if (CanalEntry.EventType.INSERT.equals(eventType)) {
            System.out.println("这是一条新增的数据");
        } else if (CanalEntry.EventType.DELETE.equals(eventType)) {
            System.out.println("删除数据：" + afterColumns);
        } else {
            System.out.println("更新数据：更新前数据--" + afterColumns);
            System.out.println("更新数据：更新后数据--" + beforeColumns);

        }
        System.out.println("操作时间：" + timestamp);
    }

    @PostConstruct
    public void run() throws Exception {
        CanalConnector conn = CanalConnectors.newSingleConnector(new InetSocketAddress(server, port), destination, user, password);
        while (true) {
            conn.connect();
            conn.subscribe(subscribe);
            // 回滚到未进行ack的地方
            conn.rollback();
            // 获取数据 每次获取一百条改变数据
            Message message = conn.getWithoutAck(batch);
            //获取这条消息的id
            long id = message.getId();
            int size = message.getEntries().size();
            if (id != -1 && size > 0) {
                // 数据解析
                analysis(message.getEntries());
            } else {
                //暂停1秒防止重复链接数据库
                Thread.sleep(500);
            }
            // 确认消费完成这条消息
            conn.ack(message.getId());
            // 关闭连接
            conn.disconnect();
        }
    }

    /**
     * 数据解析
     */
    private void analysis(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            // 解析binlog
            CanalEntry.RowChange rowChange = null;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("解析出现异常 data:" + entry.toString(), e);
            }
            if (rowChange != null) {
                // 获取操作类型
                CanalEntry.EventType eventType = rowChange.getEventType();
                // 获取当前操作所属的数据库
                String dbName = entry.getHeader().getSchemaName();
                // 获取当前操作所属的表
                String tableName = entry.getHeader().getTableName();
                // 事务提交时间
                long timestamp = entry.getHeader().getExecuteTime();
                for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                    dataDetails(rowData.getBeforeColumnsList(), rowData.getAfterColumnsList(), dbName, tableName, eventType, timestamp);

                }
            }
        }
    }
}
