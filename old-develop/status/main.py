from fastapi import FastAPI

app = FastAPI()

@app.get("/api/v1")
def show_details():
    return {
        "detail": {
            "version": "v0.1",
            "api_version": "1",
            "Athur": "KeyMoad",
        }
    }

@app.get("/api/v1/status")
def read_status():
    return {"None"}

@app.get("/api/v1/db_status")
def read_db_status():
    return {"None"}
