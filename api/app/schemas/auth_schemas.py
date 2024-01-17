from pydantic import BaseModel

class Passcode(BaseModel):
    passcode: str
