#!/usr/bin/env python3
# coding: utf-8
from logging import getLogger
from fastapi import FastAPI, Request, status as fastApiStatus
from fastapi.encoders import jsonable_encoder
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import JSONResponse
from app.routers import auth, status, action, listing
from app.settings import *
from app.utils import *

app = FastAPI(
    version="1.0.0",
    title=APP_TITLE,
    description=APP_DESCRIPTION,
    summary=APP_SUMMARY,
    docs_url=DOCS_URL,
    redoc_url=REDOC_URL,
    license_info=LICENSE_INFO,
)

logger = getLogger('uvicorn.error')

# Add middleware
app.add_middleware(
    GZipMiddleware,
    minimum_size=100
)
app.add_middleware(
    TrustedHostMiddleware,
    allowed_hosts=["*.limoo.host", "*"]
)
app.add_middleware(
    CORSMiddleware,
    allow_origins=ALLOW_ORIGINS,
    allow_credentials=True,
    allow_methods=["GET", "POST"],
    allow_headers=["*"],
)

app.include_router(router=auth.router, tags=["Auth"])
app.include_router(router=listing.router, tags=["list"])
app.include_router(router=action.router, tags=["Actions"])
app.include_router(router=status.router, tags=["Queue"])


@app.exception_handler(RequestValidationError)
def validation_exception_handler(request: Request, exc: RequestValidationError):
    details = {}
    for error in exc.errors():
        details[error["loc"][-1]] = error.get("msg")
    return JSONResponse(
        status_code=fastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY,
        content=jsonable_encoder({"detail": details}),
    )
