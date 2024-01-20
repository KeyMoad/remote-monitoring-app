from fastapi import HTTPException, status as FastApiStatus
from app.utils import Data

def list_all_services() -> list:
    loaded_services = Data.load(key="services")

    if not loaded_services:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_404_NOT_FOUND,
            detail="No service found !"
        )
    else:
        return loaded_services

def list_services(type: str):
    pass

def add_new_service(name: str) -> bool:
    status = Data.write(key="services", value=name)

    if status == 1:
        return True
    elif status == 0:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
            detail="Failed to add new service !"
        )
    elif status == 2:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_409_CONFLICT,
            detail="Service is already exists !"
        )
