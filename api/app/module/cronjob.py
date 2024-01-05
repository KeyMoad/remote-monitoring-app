from crontab import CronTab
from croniter import croniter

from app.schemas.cronjob_schemas import *
from app.settings import CURRENT_USER


def validate_cron_schedule(schedule: str) -> bool:
    """
    Validates a cron schedule.

    Args:
        schedule (str): The cron schedule to be validated.

    Returns:
        bool: True if the cron schedule is valid, False otherwise.
    """
    try:
        croniter(schedule)
        return True
    except:
        return False

def create_cron(cron_job: CronJobCreate) -> CronJob:
    """
    Creates a new cron job.

    Args:
        cron_job (CronJobCreate): An object containing the details of the cron job to be created.

    Returns:
        CronJob: A new CronJob object representing the created cron job.

    Raises:
        ValueError: If the provided cron schedule is invalid.
    """
    cron = CronTab(user=CURRENT_USER)
    if not validate_cron_schedule(cron_job.schedule):
        return False

    job = cron.new(command=cron_job.command, comment=cron_job.name)
    job.setall(cron_job.schedule)
    cron.write()

    new_cronjob = CronJob(**cron_job.model_dump())
    return new_cronjob

def delete_cron(cron_job: CronJobDelete) -> bool:
    """
    Deletes a cron job from the user's crontab.

    Args:
        cron_job (CronJobDelete): A `CronJobDelete` object that contains the ID of the cron job to be deleted.

    Returns:
        bool: True if the cron job was successfully deleted, False if the cron job was not found.
    """
    crons = list(CronTab())

    for job in crons:
        if job.comment == cron_job.name:
            crons.remove(job)
            crons.write()
            return True

    return False

def list_cron_jobs() -> CronJobList:
    """
    Lists all cron jobs.

    Returns:
        CronJobList: A CronJobList object containing the list of cron jobs.
    """
    crons = CronTab(user=CURRENT_USER)
    jobs = []

    for job in crons:
        jobs.append(CronJob(
            name=job.comment,
            schedule=str(job.minutes) + " "
            + str(job.hours) + " "
            + str(job.dom) + " "
            + str(job.months) + " "
            + str(job.dow),
            command=job.command
        ))

    return CronJobList(jobs=jobs)
