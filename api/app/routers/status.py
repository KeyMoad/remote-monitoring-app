#!/usr/bin/env python3
# coding: utf-8
from fastapi import APIRouter,  Header, Depends
from app.schemas.base import Status
from app.utils import queue
from app.routers.authenticate import validate_token


router = APIRouter()


@router.post("/status")
async def job_status(data: Status, token_check: Header = Depends(validate_token)):
    job_id = data.job_id

    result = queue(status=2, job_id=job_id)

    return result
