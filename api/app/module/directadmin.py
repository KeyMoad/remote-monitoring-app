#!/usr/bin/env python3
# coding: utf-8
from app.utils import run_bash_command
from app.settings import CREATE_BACKUP_PATH
from json import loads
from DirectAdminAPI_SGS_shohani.api import PrettyAPI

api = PrettyAPI(username=admin_da_user, password=admin_da_pwd, server=da_url, json=True)
api.get_domains()
res=api.get_domains()


def packages_list() -> str:
    command = "cat /usr/local/directadmin/data/users/staff/packages.list"
    _ = run_bash_command(command=command)
    res = [item for item in _.split("\n") if item]

    return res


def users_list() -> str:
    command = "curl --insecure -sS -X GET '$(/usr/local/directadmin/directadmin api-url)/CMD_API_SHOW_ALL_USERS'"
    _ = run_bash_command(command=command)
    res = [item.replace("&", "") for item in _.split("list[]=")]

    return res


def create_backup(username: str = None, path: str = CREATE_BACKUP_PATH) -> None:
    b = "/usr/local/directadmin/directadmin admin-backup"
    command = f"{b} --user={username} --destination={path}"
    _ = run_bash_command(command=command)
