from fastapi import FastAPI, Depends
from .schemas import User as user_schemas
from models.user_models import User
from database.database import engine, SessionLocal, Base
from sqlalchemy.orm import Session

app = FastAPI()

Base.metadata.create_all(engine)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.get('/api/v1/core')
def core():
    return {"Test": "Success"}

@app.post("/api/v1/core/users/create")
def create_user(user: user_schemas, db: Session = Depends(get_db)):
    new_user = User(id=1, user_name=user.user_name, email=user.email, hashed_password=user.password, token="ixvjhvd341ffer#@1=-'")
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return new_user
