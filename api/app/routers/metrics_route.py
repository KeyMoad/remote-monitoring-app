from fastapi import APIRouter, HTTPException, status as FastApiStatus
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from app.module.metrics import gather_cpu_load, gather_memory_usage, gather_load_average


router = APIRouter()


@router.get("/metrics/cpu_load")
def get_cpu_load():
    load = gather_cpu_load()
    content = {
        "detail": {
            "message": "Currently CPU load of host",
            "data": load
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )

@router.get("/metrics/memory_usage")
def get_memory_usage():
    usage = gather_memory_usage()
    content = {
        "detail": {
            "message": "Memory metrics in bytes. Total, Used, Free",
            "data": usage
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )

@router.get("/metrics/load_average")
def get_load_avg():
    average = gather_load_average()
    content = {
        "detail": {
            "message": "The load average is the number of runnable processes over the preceding 1, 5, 15 minute intervals",
            "data": average
        }
    }
    content = jsonable_encoder(content)

    return JSONResponse(
        content=content,
        status_code=FastApiStatus.HTTP_200_OK
    )
