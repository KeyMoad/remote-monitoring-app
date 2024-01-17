from fastapi import APIRouter, Depends
from fastapi import Depends, APIRouter, HTTPException, status, Header
from app.settings import PASSCODE_PATH
from string import ascii_letters, digits
from random import choice
from app.utils import str_to_hash
from app.schemas.auth_schemas import Passcode
from app.module.authentication import authenticate_passcode, create_access_token, validate_token, credentials_exception


router = APIRouter()


@router.get("/passcode")
async def generate_passcode():
    with open(file=PASSCODE_PATH, mode="r+") as pass_code:
        data = pass_code.read()

        if data == "":
            characters = ascii_letters + digits
            code = ''.join(choice(characters) for i in range(20))
            pass_code.write(str_to_hash(code))
            status_code = 200
        else:
            code = "you already have passcode !"
            status_code = 409

    raise HTTPException(
        status_code=status_code,
        detail={"message": "Generating result", "passcode": code},
    )


@router.get("/passcode/update")
async def update_passcode():
    pass


@router.post("/token")
async def login_for_access_token(data: Passcode):
    passcode = data.passcode
    is_passcode = authenticate_passcode(passcode=passcode)

    if not is_passcode:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect passcode",
            headers={"WWW-Authenticate": "Bearer"},
        )

    access_token = create_access_token(data={"sub": "master-phone", "pass": passcode})

    return {"data": {"token": access_token}}


@router.get("/token/test")
async def get_current_user(validate_token: Header = Depends(validate_token)):
    if validate_token:
        return {"data": {"connection": "ok"}}
    else:
        raise credentials_exception
