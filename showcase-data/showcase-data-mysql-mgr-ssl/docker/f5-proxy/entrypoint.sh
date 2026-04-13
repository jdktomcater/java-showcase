#!/bin/sh

set -eu

mkdir -p /etc/nginx/stream.d /usr/share/nginx/html

MYSQL_USER="${MYSQL_USER:-app_user}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-App#123456}"
MYSQL_SSL_MODE="${MYSQL_SSL_MODE:-VERIFY_CA}"
MYSQL_SSL_CA="${MYSQL_SSL_CA:-/certs/ca.pem}"
DISCOVERY_HOSTS="${DISCOVERY_HOSTS:-mysql1 mysql2 mysql3}"
MGR_RW_LISTEN_PORT="${MGR_RW_LISTEN_PORT:-3306}"
MGR_RO_LISTEN_PORT="${MGR_RO_LISTEN_PORT:-3307}"
PROXY_DISCOVERY_INTERVAL="${PROXY_DISCOVERY_INTERVAL:-5}"

query_cluster() {
    host="$1"
    python3 /discover_mgr.py \
      --host "${host}" \
      --user "${MYSQL_USER}" \
      --password "${MYSQL_PASSWORD}" \
      --ssl-mode "${MYSQL_SSL_MODE}" \
      --ssl-ca "${MYSQL_SSL_CA}"
}

discover_members() {
    for host in ${DISCOVERY_HOSTS}; do
        if query_cluster "${host}" > /tmp/mgr-members.txt 2>/tmp/mgr-query.err; then
            return 0
        fi
    done
    return 1
}

render_config() {
    primary_host="$1"
    secondary_servers="$2"

    export PRIMARY_HOST="${primary_host}"
    export SECONDARY_SERVERS="${secondary_servers}"
    export MGR_RW_LISTEN_PORT
    export MGR_RO_LISTEN_PORT

    {
        echo "primary=${primary_host}"
        echo "secondaries=$(printf '%s' "${secondary_servers}" | tr '\n' ' ' | sed 's/  */ /g')"
    } > /usr/share/nginx/html/proxy-status.txt

    envsubst '${PRIMARY_HOST} ${SECONDARY_SERVERS} ${MGR_RW_LISTEN_PORT} ${MGR_RO_LISTEN_PORT}' \
      < /etc/nginx/templates/stream.conf.template > /etc/nginx/stream.d/stream.conf
}

update_config_once() {
    if ! discover_members; then
        return 1
    fi

    primary_host=""
    secondary_servers=""

    while IFS="$(printf '\t')" read -r member_host member_role member_state; do
        [ "${member_state}" = "ONLINE" ] || continue
        if [ "${member_role}" = "PRIMARY" ]; then
            primary_host="${member_host}"
        elif [ "${member_role}" = "SECONDARY" ]; then
            secondary_servers="${secondary_servers}    server ${member_host}:3306 max_fails=3 fail_timeout=3s;\n"
        fi
    done < /tmp/mgr-members.txt

    [ -n "${primary_host}" ] || return 1

    if [ -z "${secondary_servers}" ]; then
        secondary_servers="    server ${primary_host}:3306 max_fails=3 fail_timeout=3s;\n"
    fi

    render_config "${primary_host}" "$(printf '%b' "${secondary_servers}")"
    return 0
}

bootstrap_config() {
    until update_config_once; do
        sleep 2
    done
}

watch_cluster() {
    while true; do
        if update_config_once; then
            nginx -t >/tmp/nginx-test.log 2>&1 && nginx -s reload >/tmp/nginx-reload.log 2>&1 || true
        fi
        sleep "${PROXY_DISCOVERY_INTERVAL}"
    done
}

bootstrap_config
watch_cluster &
exec nginx -g 'daemon off;'
