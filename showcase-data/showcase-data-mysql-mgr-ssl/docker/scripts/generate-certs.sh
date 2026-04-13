#!/usr/bin/env bash

set -euo pipefail

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CERT_DIR="${BASE_DIR}/certs"
CA_DIR="${CERT_DIR}/ca"
CLIENT_DIR="${CERT_DIR}/client"
PASSWORD="${SHOWCASE_MGR_TRUSTSTORE_PASSWORD:-changeit}"

mkdir -p "${CA_DIR}" "${CLIENT_DIR}"

openssl genrsa -out "${CA_DIR}/ca-key.pem" 2048
openssl req -x509 -new -nodes -key "${CA_DIR}/ca-key.pem" -sha256 -days 3650 \
  -out "${CA_DIR}/ca.pem" -subj "/CN=showcase-mgr-ca"

for node in mysql1 mysql2 mysql3; do
  NODE_DIR="${CERT_DIR}/${node}"
  CSR_CONFIG="${NODE_DIR}/csr.cnf"

  mkdir -p "${NODE_DIR}"

  cat > "${CSR_CONFIG}" <<EOF
[ req ]
default_bits       = 2048
prompt             = no
default_md         = sha256
distinguished_name = dn
req_extensions     = req_ext

[ dn ]
CN = ${node}

[ req_ext ]
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = ${node}
DNS.2 = localhost
IP.1 = 127.0.0.1
EOF

  openssl genrsa -out "${NODE_DIR}/server-key.pem" 2048
  openssl req -new -key "${NODE_DIR}/server-key.pem" -out "${NODE_DIR}/server.csr" -config "${CSR_CONFIG}"
  openssl x509 -req -in "${NODE_DIR}/server.csr" -CA "${CA_DIR}/ca.pem" -CAkey "${CA_DIR}/ca-key.pem" \
    -CAcreateserial -out "${NODE_DIR}/server-cert.pem" -days 3650 -sha256 \
    -extensions req_ext -extfile "${CSR_CONFIG}"
  rm -f "${NODE_DIR}/server.csr" "${CSR_CONFIG}"
done

rm -f "${CLIENT_DIR}/truststore.jks"
keytool -importcert -noprompt -alias showcase-mgr-ca -file "${CA_DIR}/ca.pem" \
  -keystore "${CLIENT_DIR}/truststore.jks" -storepass "${PASSWORD}"
