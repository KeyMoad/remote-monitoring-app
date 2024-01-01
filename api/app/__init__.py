#!/usr/bin/env python3
# coding: utf-8
from logging import getLogger
from fastapi import FastAPI, Request, status as fa_status
from fastapi.encoders import jsonable_encoder
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from app.routers import authenticate, lists, backup, status
from app.settings import *
from app.utils import *

app = FastAPI(
    title=APP_TITLE,
    description=APP_DESCRIPTION,
    summary=APP_SUMMARY,
    docs_url=DOCS_URL,
    redoc_url=REDOC_URL,
    version=VERSION,
    license_info=LICENSE_INFO,
)

logger = getLogger('uvicorn.error')

app.add_middleware(
    CORSMiddleware,
    allow_origins=ALLOW_ORIGINS,
    allow_credentials=True,
    allow_methods=["GET", "POST"],
    allow_headers=["*"],
)

app.include_router(router=authenticate.router, tags=["Auth"])
app.include_router(router=lists.router, tags=["list"])
app.include_router(router=backup.router, tags=["Actions"])
app.include_router(router=backup.router, tags=["Actions"])
app.include_router(router=status.router, tags=["Actions"])


@app.exception_handler(RequestValidationError)
def validation_exception_handler(request: Request, exc: RequestValidationError):
    details = {}
    for error in exc.errors():
        details[error["loc"][-1]] = error.get("msg")
    return JSONResponse(
        status_code=fa_status.HTTP_422_UNPROCESSABLE_ENTITY,
        content=jsonable_encoder({"detail": details}),
    )