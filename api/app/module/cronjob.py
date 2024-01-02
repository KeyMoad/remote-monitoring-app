from crontab import CronTab
from croniter import croniter

from app.schemas.cronjob_schemas import CronJobCreate, CronJob


def validate_cron_schedule(schedule: str) -> bool:
    try:
        croniter(schedule)
        return True
    except:
        return False

def create_cron(cron_job: CronJobCreate) -> CronJob:
    cron = CronTab(user=True)
    if not validate_cron_schedule(cron_job.schedule):
        raise ValueError("Invalid cron schedule format")

    job = cron.new(command=cron_job.command, comment=cron_job.name)
    job.setall(cron_job.schedule)
    cron.write()

    new_cronjob = CronJob(**cron_job.model_dump(), id=job.index)
    return new_cronjob
