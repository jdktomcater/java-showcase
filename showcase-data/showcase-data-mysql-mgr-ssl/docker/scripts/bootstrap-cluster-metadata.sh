#!/usr/bin/env bash

set -euo pipefail

ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root@123456}"
CLUSTER_NAME="${MGR_CLUSTER_NAME:-showcaseCluster}"
CA_FILE="${MGR_CA_FILE:-/certs/ca.pem}"

mysql_exec() {
  local host="$1"
  local sql="$2"
  mysql --ssl-mode=VERIFY_CA --ssl-ca="${CA_FILE}" -h"${host}" -uroot -p"${ROOT_PASSWORD}" -N -B -e "${sql}"
}

wait_for_cluster() {
  local tries=60
  local online_members=""

  until [[ "${tries}" -eq 0 ]]; do
    online_members="$(mysql_exec mysql1 "SELECT COUNT(*) FROM performance_schema.replication_group_members WHERE MEMBER_STATE = 'ONLINE';" 2>/dev/null || true)"
    if [[ "${online_members}" == "3" ]]; then
      return 0
    fi
    tries=$((tries - 1))
    sleep 2
  done

  echo "existing Group Replication deployment did not reach 3 ONLINE members in time"
  return 1
}

metadata_exists() {
  local schema_name
  schema_name="$(mysql_exec mysql1 "SELECT SCHEMA_NAME FROM information_schema.schemata WHERE SCHEMA_NAME = 'mysql_innodb_cluster_metadata';" 2>/dev/null || true)"
  [[ "${schema_name}" == "mysql_innodb_cluster_metadata" ]]
}

wait_for_cluster

if metadata_exists; then
  echo "InnoDB Cluster metadata already exists, skipping adoption bootstrap."
  exit 0
fi

tmp_js="$(mktemp)"
trap 'rm -f "${tmp_js}"' EXIT

cat > "${tmp_js}" <<EOF
var cluster;
try {
  cluster = dba.getCluster();
  print("Existing InnoDB Cluster metadata detected.\\n");
} catch (err) {
  print("Adopting the existing Group Replication deployment as InnoDB Cluster '${CLUSTER_NAME}'.\\n");
  cluster = dba.createCluster('${CLUSTER_NAME}', {adoptFromGR: true});
}
cluster.rescan({addUnmanaged: true, removeObsolete: true});
print("InnoDB Cluster metadata is ready for MySQL Router.\\n");
print(JSON.stringify(cluster.status(), null, 2) + "\\n");
EOF

mysqlsh \
  --js \
  --host=mysql1 \
  --port=3306 \
  --user=root \
  --password="${ROOT_PASSWORD}" \
  --ssl-mode=VERIFY_CA \
  --ssl-ca="${CA_FILE}" \
  --no-wizard \
  --file="${tmp_js}"
