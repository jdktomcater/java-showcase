#!/bin/bash

echo "Starting RocketMQ Cluster..."

# 创建必要的目录
mkdir -p logs/namesrv
mkdir -p logs/broker-master-1
mkdir -p logs/broker-slave-1
mkdir -p logs/broker-master-2
mkdir -p logs/broker-slave-2

mkdir -p store/namesrv
mkdir -p store/broker-master-1
mkdir -p store/broker-slave-1
mkdir -p store/broker-master-2
mkdir -p store/broker-slave-2

mkdir -p conf

# 启动集群
docker-compose -f rocketmq-cluster-docker-compose.yaml up -d

echo "RocketMQ Cluster is starting..."
echo "Please wait a few minutes for all services to be ready."

# 等待服务启动
sleep 30

echo "Checking cluster status..."
docker-compose -f rocketmq-cluster-docker-compose.yaml ps

echo ""
echo "RocketMQ Cluster Access Information:"
echo "====================================="
echo "Name Server: localhost:9876"
echo "Broker Master 1: localhost:10911"
echo "Broker Slave 1:  localhost:11911"
echo "Broker Master 2: localhost:12911"
echo "Broker Slave 2:  localhost:13911"
echo "Console Dashboard: http://localhost:8080"
echo ""
echo "To view logs:"
echo "  docker-compose -f rocketmq-cluster-docker-compose.yaml logs -f"
echo ""
echo "To stop cluster:"
echo "  docker-compose -f rocketmq-cluster-docker-compose.yaml down" 