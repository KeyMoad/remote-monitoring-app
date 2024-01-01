from app.schemas.base import Token
from fastapi import APIRouter, Depends
from string import ascii_letters, digits
from random import choice
from jose import jwt
from datetime import datetime, timedelta
from fastapi import HTTPException, status, Header
from hashlib import sha256
from typing import Optional
from app.settings import JWT_TOKEN_PATH, JWT_ALGORITHM, JWT_SECRET_KEY, JWT_ACCESS_TOKEN_EXPIRE_MINUTES

ACCESS_TOKEN_EXPIRES = timedelta(minutes=JWT_ACCESS_TOKEN_EXPIRE_MINUTES)


CREDENTIALS_EXCEPTION = HTTPException(
    status_code=status.HTTP_401_UNAUTHORIZED,
    detail="Could not validate credentials",
    headers={"WWW-Authenticate": "Bearer"}
)


router = APIRouter()


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    """
    Creates a JWT access token.
    Args:
    - data (dict): Data to be encoded in the token.
    - expires_delta (timedelta | None): Optional timedelta for token expiration.
    Returns:
    - str: Encoded JWT access token.
    """
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(week=1)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, JWT_SECRET_KEY,
                             algorithm=JWT_ALGORITHM)
    return encoded_jwt


def hash_string(input_string):
    # Create a new SHA-256 hash object
    hash_object = sha256()

    # Update the hash object with the bytes-like object of the input string
    hash_object.update(input_string.encode('utf-8'))

    # Get the hexadecimal representation of the hash
    hashed_string = hash_object.hexdigest()

    return hashed_string


def validate_token(authorization: str = Header()):
    try:
        f = open(JWT_TOKEN_PATH, 'r')
        if f.read() != authorization:
            raise CREDENTIALS_EXCEPTION
        f.close()
    except FileNotFoundError:
        raise HTTPException(
            status_code=404, detail="The token file was not found. Please contact the administrator for assistance.")


def valid_password():
    # get random password pf length 8 with letters, digits, and symbols
    characters = ascii_letters + digits
    password = ''.join(choice(characters) for i in range(20))
    return password


@router.get("/generate")
async def generate_keys():
    global validate_password
    validate_password = valid_password()
    raise HTTPException(status_code=200, headers={
                        "Custom-Header": "Passcode generated"}, detail=f"This is your pass: {validate_password}")


@router.get("/token_creator")
async def generate_token(passcode: str):
    global validate_password
    if passcode == validate_password:
        validate_password = ""
        access_token_expires = ACCESS_TOKEN_EXPIRES
        token = create_access_token(
            data={"sub": "Panel"}, expires_delta=access_token_expires
        )
        hashed_token = hash_string(token)
        f = open(JWT_TOKEN_PATH, "w")
        f.write(hashed_token)
        f.close()
        raise HTTPException(status_code=200, headers={
                            "Custom-Header": "Passcode accepted"}, detail=token)
    else:
        raise HTTPException(status_code=401, headers={
                            "Custom-Header": "Passcode is wrong"}, detail="Password is wrong, Please check it again.", )


@router.get("/token/test", response_model=Token)
async def connection_test(token_check: Header = Depends(validate_token)):

    raise HTTPException(status_code=200, headers={
                        "Custom-Header": "Token accepted"}, detail=f"Your token accepted, you are logged in")
