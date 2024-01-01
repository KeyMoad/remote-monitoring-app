#!/usr/bin/env python3
# coding: utf-8
from fastapi import APIRouter, Header, Depends
from app.settings import CONTROL_PANEL, SERVER_HOSTNME
from app.routers.authenticate import validate_token
from app.module import directadmin, cpanel


router = APIRouter()


def packages_list() -> str:
    if CONTROL_PANEL == "cpanel":
        res = cpanel.packages_list()
    elif CONTROL_PANEL == "directadmin":
        res = directadmin.packages_list()

    return res


def users_list() -> str:
    if CONTROL_PANEL == "cpanel":
        res = cpanel.users_list()
    elif CONTROL_PANEL == "directadmin":
        res = directadmin.users_list()

    return res


@router.get("/list/{arg}")
async def lists(arg: str, token_check: Header = Depends(validate_token)):
    if arg == "packages":
        res = packages_list()
    elif arg == "users":
        res = users_list()

    result = {"hostname": SERVER_HOSTNME, arg: res}
    return result
