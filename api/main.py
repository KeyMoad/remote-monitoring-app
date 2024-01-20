from uvicorn import run
from logging import DEBUG as logging_debug, INFO as logging_info

from app import app
from app.settings import (
    DEBUG,
    UVICORN_HOST,
    UVICORN_PORT,
    UVICORN_UDS,
    UVICORN_SSL_CERTFILE,
    UVICORN_SSL_KEYFILE,
    DATA_FILE
)
from app.utils import Data

if __name__ == "__main__":
    try:
        Data.initialize()

        run(
            app="main:app",
            host=('0.0.0.0' if DEBUG else UVICORN_HOST),
            port=UVICORN_PORT,
            uds=(None if DEBUG else UVICORN_UDS),
            ssl_certfile=UVICORN_SSL_CERTFILE,
            ssl_keyfile=UVICORN_SSL_KEYFILE,
            workers=1,
            reload=DEBUG,
            log_level=logging_debug if DEBUG else logging_info
        )
    except FileNotFoundError:
        pass
