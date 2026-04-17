#!/bin/sh

set -eu

mkdir -p /etc/nginx/stream.d /usr/share/nginx/html

MYSQL_USER="${MYSQL_USER:-app_user}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-App#123456}"
MYSQL_SSL_MODE="${MYSQL_SSL_MODE:-VERIFY_CA}"
MYSQL_SSL_CA="${MYSQL_SSL_CA:-/certs/ca.pem}"
DISCOVERY_HOSTS="${DISCOVERY_HOSTS:-mysql1 mysql2 mysql3}"
RW_BACKENDS="${RW_BACKENDS:-}"
RO_BACKENDS="${RO_BACKENDS:-}"
MGR_RW_LISTEN_PORT="${MGR_RW_LISTEN_PORT:-3306}"
MGR_RO_LISTEN_PORT="${MGR_RO_LISTEN_PORT:-3307}"
PROXY_DISCOVERY_INTERVAL="${PROXY_DISCOVERY_INTERVAL:-5}"
PROXY_MODE=""

build_servers() {
    backend_list="$1"
    servers=""

    for backend in ${backend_list}; do
        servers="${servers}    server ${backend} max_fails=3 fail_timeout=3s;\n"
    done

    [ -n "${servers}" ] || return 1
    printf '%b' "${servers}"
}

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
    rw_servers="$1"
    ro_servers="$2"
    status_mode="$3"
    status_rw="$4"
    status_ro="$5"

    export RW_SERVERS="${rw_servers}"
    export RO_SERVERS="${ro_servers}"
    export MGR_RW_LISTEN_PORT
    export MGR_RO_LISTEN_PORT

    {
        echo "mode=${status_mode}"
        echo "rw_backends=${status_rw}"
        echo "ro_backends=${status_ro}"
    } > /usr/share/nginx/html/proxy-status.txt

    envsubst '${RW_SERVERS} ${RO_SERVERS} ${MGR_RW_LISTEN_PORT} ${MGR_RO_LISTEN_PORT}' \
      < /etc/nginx/templates/stream.conf.template > /etc/nginx/stream.d/stream.conf
}

update_config_from_static_backends() {
    rw_servers="$(build_servers "${RW_BACKENDS}")" || return 1
    ro_servers="$(build_servers "${RO_BACKENDS}")" || return 1

    render_config "${rw_servers}" "${ro_servers}" "static_backends" "${RW_BACKENDS}" "${RO_BACKENDS}"
    return 0
}

update_config_once() {
    if [ -n "${RW_BACKENDS}" ] || [ -n "${RO_BACKENDS}" ]; then
        [ -n "${RW_BACKENDS}" ] && [ -n "${RO_BACKENDS}" ] || return 1
        update_config_from_static_backends
        return 0
    fi

    if ! discover_members; then
        return 1
    fi

    rw_servers=""
    ro_servers=""
    primary_host=""

    while IFS="$(printf '\t')" read -r member_host member_role member_state; do
        [ "${member_state}" = "ONLINE" ] || continue
        if [ "${member_role}" = "PRIMARY" ]; then
            primary_host="${member_host}"
            rw_servers="${rw_servers}    server ${member_host}:3306 max_fails=3 fail_timeout=3s;\n"
        elif [ "${member_role}" = "SECONDARY" ]; then
            ro_servers="${ro_servers}    server ${member_host}:3306 max_fails=3 fail_timeout=3s;\n"
        fi
    done < /tmp/mgr-members.txt

    [ -n "${primary_host}" ] || return 1

    if [ -z "${ro_servers}" ]; then
        ro_servers="${rw_servers}"
    fi

    render_config "$(printf '%b' "${rw_servers}")" "$(printf '%b' "${ro_servers}")" \
      "mgr_discovery" "${primary_host}:3306" "$(printf '%s' "${ro_servers}" | tr '\n' ' ' | sed 's/  */ /g')"
    return 0
}

bootstrap_config() {
    until update_config_once; do
        sleep 2
    done

    if [ -n "${RW_BACKENDS}" ] || [ -n "${RO_BACKENDS}" ]; then
        PROXY_MODE="static_backends"
    else
        PROXY_MODE="mgr_discovery"
    fi
}

watch_cluster() {
    [ "${PROXY_MODE}" = "mgr_discovery" ] || return 0

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
