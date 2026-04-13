#!/usr/bin/env python3

import argparse
import sys

import pymysql


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--host", required=True)
    parser.add_argument("--user", required=True)
    parser.add_argument("--password", required=True)
    parser.add_argument("--ssl-mode", default="VERIFY_CA")
    parser.add_argument("--ssl-ca", default="")
    args = parser.parse_args()

    ssl_options = None
    if args.ssl_mode.upper() != "DISABLED":
        ssl_options = {}
        if args.ssl_ca:
            ssl_options["ca"] = args.ssl_ca

    connection = pymysql.connect(
        host=args.host,
        user=args.user,
        password=args.password,
        port=3306,
        connect_timeout=3,
        ssl=ssl_options,
        cursorclass=pymysql.cursors.Cursor,
    )

    try:
        with connection.cursor() as cursor:
            cursor.execute(
                """
                SELECT MEMBER_HOST, MEMBER_ROLE, MEMBER_STATE
                FROM performance_schema.replication_group_members
                ORDER BY MEMBER_HOST
                """
            )
            for member_host, member_role, member_state in cursor.fetchall():
                sys.stdout.write(f"{member_host}\t{member_role}\t{member_state}\n")
    finally:
        connection.close()

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
