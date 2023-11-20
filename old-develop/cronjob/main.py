from fastapi import FastAPI
from schemas import CronJob

app = FastAPI()

@app.get("/api/v1/cron/list_cronjob")
def list_cron_job():
    return {"None"}

@app.post("/api/v1/cron/create_cronjob")
def create_cron_job(cronjob_request: CronJob):
    return {"cronjob": f"{cronjob_request.min} {cronjob_request.hour} {cronjob_request.dom} {cronjob_request.mon} {cronjob_request.dow} {cronjob_request.job}"}
