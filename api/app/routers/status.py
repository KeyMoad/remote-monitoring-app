#!/usr/bin/env python3
# coding: utf-8
from fastapi import APIRouter,  Header, Depends
from app.schemas.status_schemas import Status
from app.utils import queue

router = APIRouter()


@router.post("/status")
async def job_status(data: Status):
    job_id = data.job_id

    result = queue(status=2, job_id=job_id)

    return result
