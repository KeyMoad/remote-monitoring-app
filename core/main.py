from fastapi import FastAPI
from . import schemas

app = FastAPI()

@app.get('/api/v1/core')
def core():
    return {"Test": "Success"}


@app.post("/api/v1/core/users/create")
def create_user(user: schemas.User):
    return {"message": "User created successfully", "user_details": user}