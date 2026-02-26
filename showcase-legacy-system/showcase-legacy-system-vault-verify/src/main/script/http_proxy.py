from http.server import BaseHTTPRequestHandler, HTTPServer
import requests

UPSTREAM = "https://txdev-vault.qwasn.com"
BLOCK = False  # True=模拟分区, False=正常转发

class H(BaseHTTPRequestHandler):
    def _handle(self):
        global BLOCK
        if self.path == "/__partition/on":
            BLOCK = True
            self.send_response(200); self.end_headers(); self.wfile.write(b"partition=on"); return
        if self.path == "/__partition/off":
            BLOCK = False
            self.send_response(200); self.end_headers(); self.wfile.write(b"partition=off"); return

        if BLOCK:
            self.send_response(503)
            self.end_headers()
            self.wfile.write(b'{"errors":["simulated network partition"]}')
            return

        url = UPSTREAM + self.path
        body = self.rfile.read(int(self.headers.get("Content-Length", 0) or 0))
        headers = {k: v for k, v in self.headers.items() if k.lower() != "host"}
        r = requests.request(self.command, url, data=body, headers=headers, timeout=10, verify=True)

        self.send_response(r.status_code)
        for k, v in r.headers.items():
            if k.lower() not in ("transfer-encoding", "content-encoding", "connection"):
                self.send_header(k, v)
        self.end_headers()
        self.wfile.write(r.content)

    def do_GET(self): self._handle()
    def do_POST(self): self._handle()
    def do_PUT(self): self._handle()
    def do_DELETE(self): self._handle()

HTTPServer(("127.0.0.1", 18200), H).serve_forever()
