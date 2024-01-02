#!/usr/bin/env python3
# coding: utf-8
import settings
from json import load, dumps
from uuid import uuid5, NAMESPACE_DNS
from time import gmtime, strftime
from subprocess import run
from socket import gethostbyname, gethostname

def give_time() -> str:
    time = strftime('%Y-%m-%d %H:%M:%S', gmtime())
    return time

def id_generator(username: str, job_title: str, time: str = give_time()) -> str:
    g_id = str(uuid5(namespace=NAMESPACE_DNS, name=username+job_title+time))
    return g_id

def queue(status: int, job_id: str, username: str = None, job_title: str = "") -> None:
    lock_file: str = settings.QUEUE_PATH
    with open(file=lock_file, mode="r+") as raw:
        data: dict = load(raw)
        if status == 1:
            for job in list(data):
                job_data = data[job]
                if (username not in job_data.keys()) or (job_title not in job_data[username]):
                    data[job_id] = {username: job_title}
                    result = {"job add": True, "job id": job_id,
                              "job data": data[job_id]}
        elif status == 0:
            if job_id in data.keys():
                result = {"job rm": True, "job id": job_id,
                          "job data": data[job_id]}
                del data[job_id]
        elif status == 2:
            if job_id in data.keys():
                result = {"job runing": True,
                          "job id": job_id, "job data": data[job_id]}
            else:
                result = {"job runing": False, "job id": job_id}
        else:
            result = {"job add": False, "message": "status is not defined"}
        raw.seek(0)
        raw.truncate()
        raw.write(dumps(data, indent=4))

    return result

def run_bash_command(command: str = " ") -> str:
    result = run(args=command, shell=True, executable='/bin/bash',
                 capture_output=True, text=True).stdout
    return result

def server_hostname() -> str:
    hostname = gethostname()
    return hostname

def server_ip() -> str:
    ip = gethostbyname(server_hostname())
    return ip
