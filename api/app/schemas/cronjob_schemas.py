from pydantic import BaseModel
from typing import List


class CronJobBase(BaseModel):
    name: str
    schedule: str
    command: str

class CronJobCreate(CronJobBase):
    pass

class CronJob(CronJobBase):

    class Config:
        arbitrary_types_allowed = True

class CronJobList(BaseModel):
    jobs: List[CronJob]

    class Config:
        arbitrary_types_allowed = True

class CronJobDelete(BaseModel):
    name: str

    class Config:
        arbitrary_types_allowed = True
