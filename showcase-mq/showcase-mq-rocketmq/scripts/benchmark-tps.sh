#!/usr/bin/env bash
#
# RocketMQ 集群 TPS 一键压测脚本
# 依赖: docker, curl, python3
#
# 用法:
#   ./benchmark-tps.sh                  # 默认 60s, 1KB 消息
#   ./benchmark-tps.sh -d 120 -s 4096   # 120s, 4KB 消息
#   ./benchmark-tps.sh --producer-only  # 只测发送 TPS
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPORT_DIR="${SCRIPT_DIR}/benchmark-reports"

TOPIC="${TOPIC:-BenchmarkTest}"
NAMESRV="${NAMESRV:-rmq-name-srv-a:9876}"
MSG_SIZE="${MSG_SIZE:-1024}"
DURATION="${DURATION:-60}"
BROKER_CONTAINER="${BROKER_CONTAINER:-rmq-broker-a}"
DASHBOARD_URL="${DASHBOARD_URL:-http://localhost:8087}"
JAVA_HOME_IN_CONTAINER="${JAVA_HOME_IN_CONTAINER:-/usr/lib/jvm/java-1.8.0-openjdk}"
BENCHMARK_DIR="/home/rocketmq/rocketmq-4.5.2/benchmark"

PRODUCER_ONLY=false
WITH_CONSUMER=true
INTERVAL=5

usage() {
  cat <<'EOF'
RocketMQ TPS 压测脚本

选项:
  -t, --topic TOPIC         Topic 名称 (默认: BenchmarkTest)
  -n, --namesrv ADDR        NameServer 地址 (默认: rmq-name-srv-a:9876)
  -s, --size BYTES          消息大小字节 (默认: 1024)
  -d, --duration SECONDS    压测时长秒 (默认: 60)
  -c, --container NAME      运行 benchmark 的 Broker 容器 (默认: rmq-broker-a)
  --dashboard URL           Dashboard 地址 (默认: http://localhost:8087)
  --producer-only           仅启动 Producer，不启动 Consumer
  --interval SECONDS        Dashboard TPS 采样间隔 (默认: 5)
  -h, --help                显示帮助

环境变量:
  TOPIC, NAMESRV, MSG_SIZE, DURATION, BROKER_CONTAINER, DASHBOARD_URL

示例:
  ./benchmark-tps.sh
  ./benchmark-tps.sh -d 120 -s 4096
  ./benchmark-tps.sh --producer-only -d 30
EOF
}

log() { printf '[%s] %s\n' "$(date '+%H:%M:%S')" "$*"; }
die() { log "ERROR: $*"; exit 1; }

while [[ $# -gt 0 ]]; do
  case "$1" in
    -t|--topic) TOPIC="$2"; shift 2 ;;
    -n|--namesrv) NAMESRV="$2"; shift 2 ;;
    -s|--size) MSG_SIZE="$2"; shift 2 ;;
    -d|--duration) DURATION="$2"; shift 2 ;;
    -c|--container) BROKER_CONTAINER="$2"; shift 2 ;;
    --dashboard) DASHBOARD_URL="$2"; shift 2 ;;
    --producer-only) WITH_CONSUMER=false; PRODUCER_ONLY=true; shift ;;
    --interval) INTERVAL="$2"; shift 2 ;;
    -h|--help) usage; exit 0 ;;
    *) die "未知参数: $1 (使用 -h 查看帮助)" ;;
  esac
done

mkdir -p "${REPORT_DIR}"
RUN_ID="$(date '+%Y%m%d-%H%M%S')"
RUN_DIR="${REPORT_DIR}/${RUN_ID}"
mkdir -p "${RUN_DIR}"

PRODUCER_LOG="${RUN_DIR}/producer.log"
CONSUMER_LOG="${RUN_DIR}/consumer.log"
TPS_LOG="${RUN_DIR}/dashboard-tps.log"
REPORT_FILE="${RUN_DIR}/report.txt"

benchmark_exec() {
  local class="$1"
  shift
  docker exec "${BROKER_CONTAINER}" sh -c "
    cd ${BENCHMARK_DIR}
    export JAVA_HOME=${JAVA_HOME_IN_CONTAINER}
    sh runclass.sh ${class} $*
  "
}

stop_benchmark() {
  docker exec "${BROKER_CONTAINER}" sh -c "
    pkill -f org.apache.rocketmq.example.benchmark.Producer 2>/dev/null || true
    pkill -f org.apache.rocketmq.example.benchmark.Consumer 2>/dev/null || true
  " >/dev/null 2>&1 || true
}

preflight() {
  command -v docker >/dev/null 2>&1 || die "未找到 docker"
  command -v curl >/dev/null 2>&1 || die "未找到 curl"
  command -v python3 >/dev/null 2>&1 || die "未找到 python3"

  docker ps --format '{{.Names}}' | grep -qx "${BROKER_CONTAINER}" \
    || die "容器 ${BROKER_CONTAINER} 未运行，请先执行: docker compose up -d"

  if ! curl -sf "${DASHBOARD_URL}/" >/dev/null 2>&1; then
    log "WARN: Dashboard ${DASHBOARD_URL} 不可达，将跳过 Dashboard TPS 采样"
    DASHBOARD_URL=""
  fi
}

sample_dashboard_tps() {
  [[ -n "${DASHBOARD_URL}" ]] || return 0
  while true; do
    curl -sf "${DASHBOARD_URL}/cluster/list.query" 2>/dev/null | python3 -c "
import sys, json
from datetime import datetime
try:
    data = json.load(sys.stdin)['data']['brokerServer']
except Exception:
    sys.exit(0)
ts = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
for broker, nodes in data.items():
    put = nodes.get('0', {}).get('putTps', 'N/A')
    get_tps = nodes.get('0', {}).get('getFoundTps', 'N/A')
    print(f'{ts} broker={broker} putTps={put} getFoundTps={get_tps}')
" >> "${TPS_LOG}" 2>/dev/null || true
    sleep "${INTERVAL}"
  done
}

parse_producer_stats() {
  python3 - <<PY
import re, statistics, sys
from pathlib import Path

log = Path("${PRODUCER_LOG}")
if not log.exists():
    print("0,0,0,0,0,0,0")
    sys.exit(0)

text = log.read_text(errors="ignore")
pattern = re.compile(
    r"Send TPS:\s*(\d+)\s+Max RT:\s*(\d+)\s+Average RT:\s*([\d.]+)\s+Send Failed:\s*(\d+)"
)
rows = pattern.findall(text)
if not rows:
    print("0,0,0,0,0,0,0")
    sys.exit(0)

tps = [int(r[0]) for r in rows]
max_rt = max(int(r[1]) for r in rows)
avg_rt = round(sum(float(r[2]) for r in rows) / len(rows), 2)
failed = sum(int(r[3]) for r in rows)
print(f"{max(tps)},{statistics.mean(tps):.0f},{min(tps)},{max_rt},{avg_rt},{failed},{len(rows)}")
PY
}

generate_report() {
  IFS=',' read -r MAX_TPS AVG_TPS MIN_TPS MAX_RT AVG_RT FAILED SAMPLES <<< "$(parse_producer_stats)"

  {
    echo "========================================"
    echo " RocketMQ TPS 压测报告"
    echo "========================================"
    echo "时间:       $(date '+%Y-%m-%d %H:%M:%S')"
    echo "Topic:      ${TOPIC}"
    echo "NameServer: ${NAMESRV}"
    echo "消息大小:   ${MSG_SIZE} bytes"
    echo "压测时长:   ${DURATION}s"
    echo "Broker容器: ${BROKER_CONTAINER}"
    echo "Consumer:   $([[ "${WITH_CONSUMER}" == true ]] && echo '是' || echo '否')"
    echo "----------------------------------------"
    echo "Producer 统计 (来自 benchmark 输出)"
    echo "  采样次数:   ${SAMPLES}"
    echo "  峰值 TPS:   ${MAX_TPS}"
    echo "  平均 TPS:   ${AVG_TPS}"
    echo "  最低 TPS:   ${MIN_TPS}"
    echo "  最大 RT:    ${MAX_RT} ms"
    echo "  平均 RT:    ${AVG_RT} ms"
    echo "  发送失败:   ${FAILED}"
    echo "----------------------------------------"
    if [[ -s "${TPS_LOG}" ]]; then
      echo "Dashboard Broker putTps 采样 (最后 5 条):"
      tail -5 "${TPS_LOG}"
    else
      echo "Dashboard TPS: 未采集"
    fi
    echo "----------------------------------------"
    echo "日志文件:"
    echo "  Producer: ${PRODUCER_LOG}"
    echo "  Consumer: ${CONSUMER_LOG}"
    echo "  Dashboard: ${TPS_LOG}"
    echo "========================================"
  } | tee "${REPORT_FILE}"
}

cleanup() {
  stop_benchmark
  [[ -n "${SAMPLER_PID:-}" ]] && kill "${SAMPLER_PID}" 2>/dev/null || true
  [[ -n "${CONSUMER_PID:-}" ]] && kill "${CONSUMER_PID}" 2>/dev/null || true
  [[ -n "${PRODUCER_PID:-}" ]] && kill "${PRODUCER_PID}" 2>/dev/null || true
  wait 2>/dev/null || true
}

trap cleanup EXIT INT TERM

main() {
  preflight
  stop_benchmark

  log "压测配置: topic=${TOPIC}, size=${MSG_SIZE}B, duration=${DURATION}s"
  log "报告目录: ${RUN_DIR}"

  if [[ "${WITH_CONSUMER}" == true ]]; then
    log "启动 Consumer..."
    benchmark_exec org.apache.rocketmq.example.benchmark.Consumer \
      -n "${NAMESRV}" -t "${TOPIC}" > "${CONSUMER_LOG}" 2>&1 &
    CONSUMER_PID=$!
    sleep 3
  fi

  sample_dashboard_tps &
  SAMPLER_PID=$!

  log "启动 Producer，压测 ${DURATION}s..."
  benchmark_exec org.apache.rocketmq.example.benchmark.Producer \
    -n "${NAMESRV}" -t "${TOPIC}" -s "${MSG_SIZE}" -k true > "${PRODUCER_LOG}" 2>&1 &
  PRODUCER_PID=$!

  sleep "${DURATION}"

  log "停止压测..."
  stop_benchmark
  kill "${SAMPLER_PID}" 2>/dev/null || true
  wait "${SAMPLER_PID}" 2>/dev/null || true

  echo
  generate_report
  log "完成。报告: ${REPORT_FILE}"
}

main "$@"
