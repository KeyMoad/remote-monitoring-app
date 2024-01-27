from fastapi import APIRouter, status as FastApiStatus, Header, Depends
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
from fastapi import HTTPException, status as FastApiStatus

from typing import Annotated

from app.schemas.services_schemas import ServiceList, AddService
from app.module.services import *
from app.module.authentication import validate_token


router = APIRouter()
action = ServiceAction(Data.load("services"))

@router.post("/service/add")
def add_service(
    new_service: AddService,
    validate_token: Header = Depends(validate_token)
):
    if not new_service:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="No service name provided !"
        )

    valid_name = validate_service_name(name=new_service.name)
    is_add = add_new_service(valid_name)

    content = {
        "detail": {
            "message": "New service added successfully",
            "data": is_add
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )

@router.get("/service/list", response_model=ServiceList)
def get_service_list(
    list_type: Annotated[str, None] = None,
    validate_token: Header = Depends(validate_token)
):
    """
    Retrieves a list of services that is in server

    Returns:
        List[Services]: A list of services

        Example Usage:
            GET /service/list
    """
    if list_type != None:
        if list_type == "active":
            services: list = action.active_services()
        elif list_type == "inactive":
            services: list = action.inactive_services()
        else:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="No type action provided !"
            )
    else:
        list_type = "all"
        services: list = list_all_services()

    content = {
        "detail": {
            "message": f"A list of {list_type} services retrieved successfully",
            "data": services
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )

@router.get("/service/{service_name}")
def status(
    service_name: str,
    validate_token: Header = Depends(validate_token)
):
    result = action.service_status(service_name=service_name)

    content = {
        "detail": {
            "message": f"{service_name} currently status.",
            "data": result
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )

@router.post("/service/stop/{service_name}")
def stop(
    service_name: str,
    validate_token: Header = Depends(validate_token)
):
    result = action.stop_service(service_name=service_name)

    content = {
        "detail": {
            "message": f"{service_name} stopping job status",
            "data": result
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )

@router.post("/service/start/{service_name}")
def start(
    service_name: str,
    validate_token: Header = Depends(validate_token)
):
    result = action.start_service(service_name=service_name)

    content = {
        "detail": {
            "message": f"{service_name} starting job status",
            "data": result
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )
