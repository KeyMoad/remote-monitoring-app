from fastapi import FastAPI, Depends
from . import schemas
from database import models
from database.database import engine, SessionLocal
from sqlalchemy.orm import Session

app = FastAPI()

# Remove models.Base.metadata.create_all(bind=engine) from the global scope

def get_db():
    db = SessionLocal()  # Initialize SessionLocal to get a session instance
    try:
        yield db
    finally:
        db.close()

@app.get('/api/v1/core')
def core():
    return {"Test": "Success"}

@app.post("/api/v1/core/users/create")
def create_user(user: schemas.User, db: Session = Depends(get_db)):
    new_user = models.User(id=1, user_name=user.user_name, email=user.email, hashed_password=user.password, token="ixvjhvd341ffer#@1=-'")
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return new_user
