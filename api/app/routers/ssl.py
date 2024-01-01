#!/usr/bin/env python3
# coding: utf-8
from fastapi import APIRouter, Header, Depends
from app.settings import CONTROL_PANEL, SERVER_HOSTNME
from app.module import directadmin, cpanel
from app.routers.authenticate import validate_token
from ssl import create_default_context
from socket import create_connection
from datetime import datetime


router = APIRouter()


def ssl_info(domain: str):
    cont = create_default_context()
    with create_connection(address=(domain, 443)) as s:
        with cont.wrap_socket(sock=s, server_hostname=domain) as domainsocket:
            ssl_info = domainsocket.getpeercert()
            exp_date = datetime.strptime(
                ssl_info['notAfter'], '%b %d %H:%M:%S %Y %Z')
            daytoexp = exp_date - datetime.utcnow()
            res = {"status": True, "day to expire": daytoexp.days,
                   "ssl information": ssl_info}


def enable_ssl():
    if CONTROL_PANEL == "cpanel":
        res = cpanel.enable_ssl()
    elif CONTROL_PANEL == "directadmin":
        res = directadmin.enable_ssl()

    return res


def disable_ssl():
    if CONTROL_PANEL == "cpanel":
        res = cpanel.disable_ssl()
    elif CONTROL_PANEL == "directadmin":
        res = directadmin.disable_ssl()

    return res


@router.get("/ssl/{arg}")
async def ssl(arg: str, token_check: Header = Depends(validate_token)):
    if arg == "enable":
        res = enable_ssl()
    elif arg == "disable":
        res = disable_ssl()

    result = {"msg": f"ssl {arg} for domain {domain}"}
    return result
