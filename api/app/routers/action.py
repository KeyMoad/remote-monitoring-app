from fastapi import APIRouter, HTTPException, status as FastApiStatus

from app.schemas.cronjob_schemas import CronJob, CronJobCreate, CronJobDelete
from app.module.cronjob import create_cron, delete_cron

router = APIRouter()


@router.post("/cronjobs/", response_model=CronJob)
def add_cronjob(cronjob: CronJobCreate):
    """
    Create a new cron job.

    Args:
        cronjob (CronJobCreate): The data for creating a new cron job, including the name, command, and schedule.

    Returns:
        CronJob: The created cron job.
    """
    cron_creation_status = create_cron(cron_job=cronjob)
    if not cron_creation_status:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY, detail="Invalid cron schedule format"
        )
    return cron_creation_status

@router.delete("/cronjobs/{job_id}/", status_code=FastApiStatus.HTTP_204_NO_CONTENT)
def delete_cronjob(job_id: int):
    """
    Deletes a cron job with the specified job ID.

    Args:
        job_id (int): The ID of the cron job to be deleted.

    Raises:
        HTTPException: If the cron job is not found.

    Returns:
        None: If the cron job is successfully deleted.
    """
    cron_job = CronJobDelete(id=job_id)
    deleted = delete_cron(cron_job=cron_job)

    if not deleted:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_404_NOT_FOUND, detail="Cron job not found"
        )
