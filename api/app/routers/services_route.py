from fastapi import APIRouter, status as FastApiStatus
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
from fastapi import HTTPException, status as FastApiStatus

from typing import Annotated

from app.schemas.services_schemas import ServiceList, AddService
from app.module.services import list_all_services, list_services, add_new_service


router = APIRouter()


@router.post("/service/add")
def add_service(new_service: AddService):
    if not new_service:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="No service name provided !"
        )

    is_add = add_new_service(new_service.name)

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
def get_service_list(list_type: Annotated[str, None] = None):
    """
    Retrieves a list of services that is in server

    Returns:
        List[Services]: A list of services

        Example Usage:
            GET /service/list
    """
    if list_type != None:
        services: list = list_services(type=list_type)
    else:
        services: list = list_all_services()

    content = {
        "detail": {
            "message": "List of all services retrieved successfully",
            "data": services
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )
