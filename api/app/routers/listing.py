from fastapi import APIRouter
from app.module.cronjob import list_cron_jobs
from app.schemas.cronjob_schemas import CronJobList

router = APIRouter()


@router.get("/cronjobs/", response_model=CronJobList)
def get_cronjobs():
    """
    Retrieves a list of cron jobs.

    Returns:
        List[CronJob]: A list of cron jobs.

    Example Usage:
        GET /cronjobs/list
    """
    cron_jobs = list_cron_jobs()
    return cron_jobs
