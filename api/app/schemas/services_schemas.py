from pydantic import BaseModel
from typing import List

class ServiceBase(BaseModel):
    name: str

class Service(ServiceBase):
    state: str

class ServiceList(BaseModel):
    List[Service]

class AddService(ServiceBase):
    pass
