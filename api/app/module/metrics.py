from psutil import cpu_percent, virtual_memory
from os import getloadavg

def gather_cpu_load():
    """
    Gathering the CPU load of the Linux server.

    Returns:
    - float: CPU load as a percentage.
    """
    cpu_load = cpu_percent(interval=0.25)
    return cpu_load

def gather_memory_usage():
    """
    Gathering the memory usage of the Linux server.

    Returns:
    - dict: Memory usage information including total, used, and free memory in bytes.
    """
    memory_info_full = virtual_memory()._asdict()
    memory_info = {
        "total": memory_info_full["total"],
        "used": memory_info_full["used"],
        "free": memory_info_full["free"]
    }

    return memory_info

def gather_load_average():
    """
    Gathering the load average of the Linux server.

    Returns:
    - dict: Load average values for 1-minute, 5-minute, and 15-minute intervals.
    """
    load_average = getloadavg()
    average = {
        "load1": round(load_average[0], 2),
        "load5": round(load_average[1], 2),
        "load15": round(load_average[2], 2)
    }

    return average
