from time import sleep
from fastapi import APIRouter, HTTPException, status as FastApiStatus
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from app.schemas.cronjob_schemas import CronJob, CronJobCreate, CronJobDelete
from app.module.cronjob import create_cron, delete_cron


router = APIRouter()


@router.post("/cronjobs/")
def add_cronjob(cronjob: CronJobCreate):
    """
    Create a new cron job.

    Args:
        cronjob (CronJobCreate): The data for creating a new cron job, including the name, command, and schedule.

    Returns:
        CronJob: The created cron job.
    """
    cron_creation_status: CronJob = create_cron(cron_job=cronjob)
    if not cron_creation_status:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY, detail="Invalid cron schedule format"
        )

    content = {
        "detail": {
            "message": "Cron job created successfully",
            "data": cron_creation_status
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )

@router.delete("/cronjobs/{job_name}/")
def delete_cronjob(job_name: str):
    """
    Deletes a cron job with the specified job ID.

    Args:
        job_id (int): The ID of the cron job to be deleted.

    Raises:
        HTTPException: If the cron job is not found.

    Returns:
        None: If the cron job is successfully deleted.
    """
    cron_job = CronJobDelete(name=job_name)
    deleted, job = delete_cron(cron_job=cron_job)

    if not deleted:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_404_NOT_FOUND,
            detail="Cron job not found"
        )

    content = {
        "detail": {
            "message": "Cron job deleted successfully",
            "data": {
                "name": job.comment,
                "command": job.command,
                "schedule": str(job.minutes) + " "
                + str(job.hours) + " "
                + str(job.dom) + " "
                + str(job.months) + " "
                + str(job.dow)
            }
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )
