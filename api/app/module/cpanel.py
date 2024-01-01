#!/usr/bin/env python3
# coding: utf-8
from app.utils import run_bash_command
from json import loads
from app.settings import CREATE_BACKUP_PATH


def packages_list():
    command = "whmapi1 --output=jsonpretty listpkgs"
    _ = run_bash_command(command=command)
    package_result = loads(_)["data"]["pkg"]
    res = [item.get("name", "") for item in package_result]

    return res


def users_list():
    command = "whmapi1 --output=jsonpretty list_users"
    _ = run_bash_command(command=command)
    res = loads(_)["data"]["users"]

    return res


def create_backup(username: str = None, path: str = CREATE_BACKUP_PATH) -> None:
    command = f"/scripts/pkgacct {username} {path}"
    _ = run_bash_command(command=command)


def disable_ssl(username: str = "test", domain: str = "test"):
    command = f"uapi --output=jsonpretty --user={username} SSL delete_ssl domain='{domain}'"
    _ = run_bash_command(command=command)
    res = loads(_)['result']
    return res


def enable_ssl(username: str = "test"):
    command = f"uapi --output=jsonpretty --user={username} SSL start_autossl_check"
    _ = run_bash_command(command=command)
    res = loads(_)['result']
    return res
