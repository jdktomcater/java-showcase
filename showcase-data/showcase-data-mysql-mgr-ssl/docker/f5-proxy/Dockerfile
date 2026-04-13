FROM nginx:1.27-alpine

RUN apk add --no-cache gettext py3-pymysql python3

COPY discover_mgr.py /discover_mgr.py
COPY nginx.conf /etc/nginx/nginx.conf
COPY stream.conf.template /etc/nginx/templates/stream.conf.template
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
