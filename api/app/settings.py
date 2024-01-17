"""
Remote Monitoring Agent Settings File

***Do not change any variable name in this file***

You can change any setting you want in this file by changing the value of variables in .env
"""
from decouple import config
from dotenv import load_dotenv
from socket import gethostbyname, gethostname
from pwd import getpwuid
from os import getuid

# Needed Function for settings
def server_hostname() -> str:
    return gethostname()

def server_ip() -> str:
    hostname = server_hostname()
    return gethostbyname(hostname)

def get_current_user() -> str:
    return getpwuid(getuid()).pw_name


load_dotenv(dotenv_path="../.env")

# uvicorn Settings
UVICORN_HOST = config("UVICORN_HOST", default="0.0.0.0")
UVICORN_PORT = config("UVICORN_PORT", cast=int, default=9931)
UVICORN_UDS = config("UVICORN_UDS", default=None)
UVICORN_SSL_CERTFILE = config("UVICORN_SSL_CERTFILE", default=None)
UVICORN_SSL_KEYFILE = config("UVICORN_SSL_KEYFILE", default=None)

# JWT Settings
# Algorithm used for JWT encoding
JWT_ALGORITHM = "HS256"
# JWT Secret key
JWT_SECRET_KEY = "1a094a0ea41912b25364aa2fc9c6e388ed14de309aede75df9ffe533b45673c62b4e2773ba01931f4774e96b99d2055f2790764c46a0b801ae4f83bf9ff79d8c"
# Access token expiration time (in minutes)
JWT_ACCESS_TOKEN_EXPIRE_MINUTES = 1440

# Swagger UI url
DOCS_URL = config("DOCS_URL", default="/docs", cast=str)
# Disable Redoc UI
REDOC_URL = config("REDOC_URL", default="/redocs", cast=str)
# Set on title of documentation page
APP_TITLE = config("APP_TITLE", default="Remote Monitoring Agent", cast=str)
# Set a little summary of the application
APP_SUMMARY = config(
    "APP_SUMMARY", default="Remon Agent Application", cast=str
)
# Description of the application and routes
APP_DESCRIPTION = config(
    "APP_DESCRIPTION", default="Hello World", cast=str
)
# license information
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
PASSCODE_PATH = config(
    "PASSCODE_PATH", default="../.pass", cast=str
)
DEBUG = config(
    "DEBUG", default=False, cast=bool
)
VERSION = config(
    "VERSION", cast=str, default="1.0.0"
)
SERVER_HOSTNAME = config(
    "SERVER_HOSTNME", default=server_hostname(), cast=str
)
SERVER_IP = config(
    "SERVER_IP", default=server_ip(), cast=str
)
ALLOW_ORIGINS = config(
    "ALLOW_ORIGINS", default=["localhost"], cast=list
)
CURRENT_USER = config(
    "CURRENT_USER", default=get_current_user(), cast=str
)