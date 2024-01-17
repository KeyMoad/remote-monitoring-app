from fastapi import Depends
from datetime import datetime, timedelta
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jose import JWTError, jwt
from typing_extensions import Annotated
from app.settings import JWT_SECRET_KEY, JWT_ALGORITHM, JWT_ACCESS_TOKEN_EXPIRE_MINUTES, PASSCODE_PATH
from app.utils import str_to_hash


auth = HTTPBearer()


credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
)

def authenticate_passcode(passcode: str) -> bool:
    hash_passcode = str_to_hash(passcode)

    with open(file=PASSCODE_PATH, mode="r") as pc:
        data = pc.read().strip()

    return True if hash_passcode == data else False

def create_access_token(data: dict):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=JWT_ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(
        claims=to_encode,
        key=JWT_SECRET_KEY,
        algorithm=JWT_ALGORITHM
    )

    return encoded_jwt

def validate_token(token: Annotated[HTTPAuthorizationCredentials, Depends(dependency=auth)] = None,):
    try:
        payload = jwt.decode(
            token=token.credentials,
            key=JWT_SECRET_KEY,
            algorithms=[JWT_ALGORITHM]
        )
        passcode: str = payload.get("pass")

        if passcode is None:
            raise credentials_exception
    except JWTError:
        raise credentials_exception

    return True
