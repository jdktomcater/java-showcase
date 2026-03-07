@echo off
start "proxy-6360" cmd /c "call python tcp_proxy.py --listen-port 6360 --target-host 172.30.3.20 --target-port 7001 --verbose"
start "proxy-6361" cmd /c "call python tcp_proxy.py --listen-port 6361 --target-host 172.30.3.20 --target-port 7002 --verbose"
start "proxy-6362" cmd /c "call python tcp_proxy.py --listen-port 6362 --target-host 172.30.3.21 --target-port 7001 --verbose"
start "proxy-6363" cmd /c "call python tcp_proxy.py --listen-port 6363 --target-host 172.30.3.21 --target-port 7002 --verbose"
start "proxy-6364" cmd /c "call python tcp_proxy.py --listen-port 6364 --target-host 172.30.3.22 --target-port 7001 --verbose"
start "proxy-6365" cmd /c "call python tcp_proxy.py --listen-port 6365 --target-host 172.30.3.22 --target-port 7002 --verbose"