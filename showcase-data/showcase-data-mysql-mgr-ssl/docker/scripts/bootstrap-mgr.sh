#!/usr/bin/env bash

set -euo pipefail

ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root@123456}"
REPL_PASSWORD="${MGR_REPL_PASSWORD:-Repl#123456}"

mysql_exec() {
  local host="$1"
  local sql="$2"
  mysql --ssl-mode=REQUIRED -h"${host}" -uroot -p"${ROOT_PASSWORD}" -e "${sql}"
}

wait_for_node() {
  local host="$1"
  until mysqladmin --ssl-mode=REQUIRED ping -h"${host}" -uroot -p"${ROOT_PASSWORD}" --silent; do
    sleep 2
  done
}

wait_for_online() {
  local host="$1"
  local tries=30
  local state=""
  until [[ "${tries}" -eq 0 ]]; do
    state="$(mysql --ssl-mode=REQUIRED -N -B -h"${host}" -uroot -p"${ROOT_PASSWORD}" \
      -e "SELECT MEMBER_STATE FROM performance_schema.replication_group_members WHERE MEMBER_ID = @@server_uuid;" || true)"
    if [[ "${state}" == "ONLINE" ]]; then
      return 0
    fi
    tries=$((tries - 1))
    sleep 2
  done
  echo "node ${host} failed to become ONLINE"
  return 1
}

for host in mysql1 mysql2 mysql3; do
  wait_for_node "${host}"
  mysql_exec "${host}" "RESET BINARY LOGS AND GTIDS;"
done

mysql_exec mysql1 "SET GLOBAL group_replication_bootstrap_group=ON;"
mysql_exec mysql1 "START GROUP_REPLICATION USER='repl', PASSWORD='${REPL_PASSWORD}';"
mysql_exec mysql1 "SET GLOBAL group_replication_bootstrap_group=OFF;"
wait_for_online mysql1

for host in mysql2 mysql3; do
  mysql_exec "${host}" "START GROUP_REPLICATION USER='repl', PASSWORD='${REPL_PASSWORD}';"
  wait_for_online "${host}"
done

mysql_exec mysql1 "SELECT MEMBER_HOST, MEMBER_PORT, MEMBER_STATE, MEMBER_ROLE FROM performance_schema.replication_group_members;"
