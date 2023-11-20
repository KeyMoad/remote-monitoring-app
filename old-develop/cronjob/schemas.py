from pydantic import BaseModel

class CronJob(BaseModel):
    min : str = "0"
    hour : str = "0"
    dom : str = "*"
    mon : str = "*"
    dow : str = "0"
    job : str