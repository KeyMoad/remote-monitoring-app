#!/usr/bin/env python3
# coding: utf-8
from fastapi import APIRouter, BackgroundTasks, Header, Depends
from app.settings import CONTROL_PANEL
from app.schemas.base import Backup
from app.utils import id_generator, run_bash_command, queue
from app.routers.authenticate import validate_token
from app.module import directadmin, cpanel


router = APIRouter()


def change_permossion(file_name: str = None, path: str = "/var/www/html", mod: int = 755) -> None:
    if file_name != None:
        command = f"chmod {mod} {path}/{file_name}"
        _ = run_bash_command(command=command)


def check_file_name(username: str = None, path: str = "/var/www/html") -> str:
    if username != None:
        command = f"ls {path}/*{username}*"
        _ = run_bash_command(command=command)
        file_name = _.strip().split("/")[4]
    return file_name


def create_backup(username: str = None, path: str = "/var/www/html", job_id: str = "") -> str:
    try:
        status: bool = True
        msg: str = "backup create"
        if CONTROL_PANEL == "cpanel":
            cpanel.create_backup(username=username)
        elif CONTROL_PANEL == "directadmin":
            directadmin.create_backup(username=username)
        else:
            status = False
            msg = "control panel not valid"

        file_name = check_file_name(username=username)
        change_permossion(file_name=file_name)

        # Remove job from queue
        queue(status=0, job_id=job_id)
    except Exception as e:
        status = False
        msg = f"backup creation failed\nerror: {e}"
        file_name = "none"

    result = {"status": status, "message": msg, "backup_file_name": file_name}
    return result


@router.post("/backup")
async def backup(data: Backup, background_tasks: BackgroundTasks, token_check: Header = Depends(validate_token)):
    username = data.username
    job_title = "backup"
    job_id = id_generator(username=username, job_title=job_title)
    file_name = check_file_name(username=username)

    # add task to queue
    queue(username=username, status=1, job_title=job_title, job_id=job_id)
    # run create backup in the background
    background_tasks.add_task(create_backup, username)

    msg = f"create {job_title} from {username} in the background"
    result = {"job_id": job_id, "message": msg, "backup_file_name": file_name}
    return result
