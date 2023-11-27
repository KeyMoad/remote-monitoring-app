from fastapi import FastAPI

app = FastAPI()

@app.post('/api/v1/action')
def core():
    return {"Test": "Success"}
