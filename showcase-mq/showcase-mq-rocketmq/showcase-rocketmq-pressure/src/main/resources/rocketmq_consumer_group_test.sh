#!/bin/bash
# rocketmq_consumer_group_test.sh

# 测试配置
# BROKER_HOST="localhost"
# BROKER_PORT="10909"
NAMESRV_ADDR="localhost:9876"

# 测试参数
BASELINE_GROUPS=10
STRESS_GROUPS=50
LIMIT_GROUPS=100

echo "开始RocketMQ消费组影响测试..."

# 阶段1: 基准测试
echo "阶段1: 基准测试 (消费组数量: $BASELINE_GROUPS)"
java -jar rocketmq-test-tool.jar \
  --namesrv-addr=$NAMESRV_ADDR \
  --consumer-groups=$BASELINE_GROUPS \
  --topics=5 \
  --message-rate=1000 \
  --duration=1800 \
  --phase=baseline

# 阶段2: 压力测试
echo "阶段2: 压力测试 (消费组数量: $STRESS_GROUPS)"
java -jar rocketmq-test-tool.jar \
  --namesrv-addr=$NAMESRV_ADDR \
  --consumer-groups=$STRESS_GROUPS \
  --topics=20 \
  --message-rate=5000 \
  --duration=1800 \
  --phase=stress

# 阶段3: 极限测试
echo "阶段3: 极限测试 (消费组数量: $LIMIT_GROUPS)"
java -jar rocketmq-test-tool.jar \
  --namesrv-addr=$NAMESRV_ADDR \
  --consumer-groups=$LIMIT_GROUPS \
  --topics=50 \
  --message-rate=10000 \
  --duration=1800 \
  --phase=limit

# 阶段4: 恢复测试
echo "阶段4: 恢复测试 (消费组数量: $BASELINE_GROUPS)"
java -jar rocketmq-test-tool.jar \
  --namesrv-addr=$NAMESRV_ADDR \
  --consumer-groups=$BASELINE_GROUPS \
  --topics=5 \
  --message-rate=1000 \
  --duration=900 \
  --phase=recovery

echo "测试完成，生成报告..."