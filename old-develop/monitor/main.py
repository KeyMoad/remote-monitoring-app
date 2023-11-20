from fastapi import FastAPI
from typing import Optional

app = FastAPI()

@app.get("/api/v1/monitor")
def read_init_monitor_config():
    return {"None"}

@app.get("/api/v1/monitor/basic_metrics")
def read_basic_metrics():
    return {"None"}

@app.get("/api/v1/monitor/service_status")
def read_service_status():
    return {"None"}
