from time import gmtime, strftime
from subprocess import run
from hashlib import sha512

def get_time() -> str:
    time = strftime('%Y-%m-%d %H:%M:%S', gmtime())
    return time

def run_bash_command(command: str = " ") -> str:
    result = run(args=command,
                 shell=True,
                 executable='/bin/bash',
                 capture_output=True,
                 text=True).stdout

    return str(result)

def str_to_hash(*string: str) -> str:
    return sha512(string="".join(string).encode(encoding="utf-8")).hexdigest()
