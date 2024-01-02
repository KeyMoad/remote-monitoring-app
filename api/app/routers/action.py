from fastapi import APIRouter

from app.schemas.cronjob_schemas import CronJob, CronJobCreate
from app.module.cronjob import create_cron

router = APIRouter()

@router.post("/cronjobs/", response_model=CronJob)
def add_cronjob(cronjob: CronJobCreate):
    cron_creation_status = create_cron(cron_job=cronjob)
    return cron_creation_status
