from pydantic import BaseModel


class CronJobBase(BaseModel):
    name: str
    schedule: str
    command: str

class CronJobCreate(CronJobBase):
    pass

class CronJob(CronJobBase):
    id: int
