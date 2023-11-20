from fastapi import FastAPI

app = FastAPI()

@app.post("/api/v1/action/service_action/{service}")
def service_action(service: str, restart: bool = False, stop: bool = False, start: bool = False, enable: bool = False):
    return {"None": service}
