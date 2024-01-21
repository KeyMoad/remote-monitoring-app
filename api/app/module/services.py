from fastapi import HTTPException, status as FastApiStatus
from re import match as regex_match
from app.utils import run_bash, Data


class ServiceAction():
    def __init__(self, data: list = Data.load("services")) -> None:
        """
        Database constructor initializes the Database object with a specified data file.

        Args:
        - data_file (str): The path to the data file. Defaults to Const.DATA_FILE.

        """
        self.data: list = data

    def service_status(self, service_name: str):
        """
        Return the status of the given service.

        Args:
        - service_name (str): The name of the service.

        Returns:
        - str: The status of the service (e.g., "active", "inactive", "failed").

        """
        if not service_name in self.data:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_404_NOT_FOUND,
                detail="Provided service is not in our watching services !"
            )

        status, exit_code = run_bash(f"systemctl is-active {service_name}")
        if status == None or exit_code == 1:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
                detail="Somethings went wrong with getting status."
            )
        else:
            return status

    def stop_service(self, service_name: str):
        if not service_name in self.data:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_404_NOT_FOUND,
                detail="Provided service is not in our watching services !"
            )

        status, exit_code = run_bash(f"systemctl stop {service_name}")
        if status == None or exit_code == 1:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
                detail=f"Somethings went wrong with stopping service {service_name}."
            )
        elif exit_code == 5:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
                detail="Provided service is not installed in server !"
            )
        else:
            return status

    def start_service(self, service_name: str):
        if not service_name in self.data:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_404_NOT_FOUND,
                detail="Provided service is not in our watching services !"
            )

        status, exit_code = run_bash(f"systemctl start {service_name}")
        if status == None or exit_code == 1:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
                detail=f"Somethings went wrong with starting service {service_name}."
            )
        elif exit_code == 5:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
                detail="Provided service is not installed in server !"
            )
        else:
            return status

    def active_services(self):
        status, exit_code = run_bash("systemctl --quiet --no-pager list-units --state=active --type=service --no-legend")
        if status == None or exit_code == 1:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
                detail=f"Somethings went wrong with fetching active services."
            )

        # Extract service names from the status
        active_services_status = [line.split()[0] for line in status.splitlines()]

        # Filter the services based on the ones present in self.data
        active_services = [service for service in active_services_status if service in self.data]

        return active_services

    def inactive_services(self):
        status, exit_code = run_bash("systemctl --quiet --no-pager list-units --state=inactive --type=service --no-legend ")
        if status == None or exit_code == 1:
            raise HTTPException(
                status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
                detail=f"Somethings went wrong with fetching inactive services."
            )

        # Extract service names from the status
        active_services_status = [line.split()[0] for line in status.splitlines()]

        # Filter the services based on the ones present in self.data
        active_services = [service for service in active_services_status if service in self.data]

        return active_services

def list_all_services() -> list:
    loaded_services = Data.load(key="services")

    if not loaded_services:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_404_NOT_FOUND,
            detail="No service found !"
        )
    else:
        return loaded_services

def validate_service_name(name: str) -> str:
    # Check if the name consists only of allowed characters
    if not regex_match("^[a-zA-Z0-9_\.]*$", name):
        raise HTTPException(
            status_code=FastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Service name should consist only of the characters a-z, A-Z, 0-9, and the underscore _."
        )

    # Check if the name exceeds 256 characters
    if len(name) > 256:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Service name should not exceed 256 characters."
        )

    # Check for reserved names
    reserved_prefixes = ["org.freedesktop.", "org.gtk.", "org.kde."]
    for prefix in reserved_prefixes:
        if name.startswith(prefix):
            raise HTTPException(
                status_code=FastApiStatus.HTTP_422_UNPROCESSABLE_ENTITY,
                detail=f"Avoid using service names that start with {prefix} as they are reserved."
            )

    # Check if the name follows the .service extension
    if not name.endswith(".service"):
        name += ".service"  

    # All checks passed, the name is valid
    return name

def add_new_service(name: str) -> bool:
    status = Data.write(key="services", value=name)

    if status == 1:
        return True
    elif status == 0:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_417_EXPECTATION_FAILED,
            detail="Failed to add new service !"
        )
    elif status == 2:
        raise HTTPException(
            status_code=FastApiStatus.HTTP_409_CONFLICT,
            detail="Service is already exists !"
        )
