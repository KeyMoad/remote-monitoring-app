from fastapi import FastAPI
from typing import Optional
from pydantic import BaseModel

app = FastAPI()

@app.get("/api/v1")
def show_details():
    return {
        "detail": {
            "version": "v0.1",
            "api_version": "1",
            "Athur": "KeyMoad",
        }
    }

@app.get("/api/v1/status")
def read_status():
    return {"None"}

@app.get("/api/v1/db_status")
def read_db_status():
    return {"None"}

@app.get("/api/v1/monitor")
def read_init_monitor_config():
    return {"None"}

@app.get("/api/v1/monitor/basic_metrics")
def read_basic_metrics():
    return {"None"}

@app.get("/api/v1/monitor/service_status")
def read_service_status():
    return {"None"}

class CronJob(BaseModel):
    minute: str = "0"
    hour: str = "0"
    day_of_month: str = "*"
    month: str = "*"
    day_of_week: str = "0"
    job: str

@app.post("/api/v1/action/create_cronjob")
def create_cron_job(cronjob_request: CronJob):
    return {"None"}

@app.post("/api/v1/action/service_action/{service}")
def service_action(service: str, restart: bool = False, stop: bool = False, start: bool = False, enable: bool = False):
    return {"None": service}
