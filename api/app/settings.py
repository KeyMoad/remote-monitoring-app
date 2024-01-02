"""
Remote Monitoring Agent Settings File

***Do not change any variable name in this file***

You can change any setting you want in this file by changing the value of variables in .env
"""
from decouple import config
from dotenv import load_dotenv
from app.utils import server_ip, server_controlpanel, server_hostname

load_dotenv(dotenv_path="../.env")

# uvicorn Settings
UVICORN_HOST = config("UVICORN_HOST", default="0.0.0.0")
UVICORN_PORT = config("UVICORN_PORT", cast=int, default=9931)
UVICORN_UDS = config("UVICORN_UDS", default=None)
UVICORN_SSL_CERTFILE = config("UVICORN_SSL_CERTFILE", default=None)
UVICORN_SSL_KEYFILE = config("UVICORN_SSL_KEYFILE", default=None)

# JWT Settings
JWT_SECRET_KEY = config("JWT_SECRET_KEY", default="123456789", cast=str)
# Algorithm used for JWT encoding
JWT_ALGORITHM = config("JWT_ALGORITHM", default="HS256", cast=str)
# Access token expiration time (in minutes)
JWT_ACCESS_TOKEN_EXPIRE_MINUTES = config(
    "JWT_ACCESS_TOKEN_EXPIRE_MINUTES", cast=int, default=1440
)
JWT_TOKEN_PATH = config("JWT_TOKEN_PATH", cast=str)

# Swagger UI url
DOCS_URL = config("DOCS_URL", default="/docs", cast=str)
# Disable Redoc UI
REDOC_URL = config("REDOC_URL", default="/redocs", cast=str)
# Set on title of documentation page
APP_TITLE = config("APP_TITLE", default="Limoo Agent", cast=str)
# Set a little summary of the application
APP_SUMMARY = config(
    "APP_SUMMARY", default="LimooHost Agent Application", cast=str
)
# Description of the application and routes
APP_DESCRIPTION = config(
    "APP_DESCRIPTION", default="An API to make jobs easier.\n\n\t- ## Admin\n\tA tag that contains **admin** level routes.\n\n\t- ## Auth\n\tJust a few routes for **Authentication and Authorization**.\n\n\t- ## User\n\tEverything a user needs to have fun with the application.", cast=str
)
# license informations
LICENSE_INFO = config(
    "LICENSE_INFO",
    default={
        "name": "Develop, Design, and Support by Mohamadreza Najarbashi.", "url": "https://github.com/KeyMoad"
    },
    cast=dict
)

# Settings
LOG_PATH = config(
    "LOG_PATH", default="../app.log", cast=str
)
DEBUG = config(
    "DEBUG", default=False, cast=bool
)
VERSION = config(
    "VERSION", cast=str
)
QUEUE_PATH = config(
    "QUEUE_PATH", default="../.app_jobs.lock", cast=str
)
CONTROL_PANEL = config(
    "CONTROL_PANEL", default=server_controlpanel(), cast=str
)
SERVER_HOSTNME = config(
    "SERVER_HOSTNME", default=server_hostname(), cast=str
)
SERVER_IP = config(
    "SERVER_IP", default=server_ip(), cast=str
)
ALLOW_ORIGINS = config(
    "ALLOW_ORIGINS", default=["localhost"], cast=list
)
CREATE_BACKUP_PATH = config(
    "CREATE_BACKUP_PATH", default="/var/www/html", cast=str
)
