## Architecture
```
api
│
├── app
│   │
│   ├── module
│   │   │
│   │   ├── __init__.py
│   │   │
│   │   ├── cronjob.py
│   │   │
│   │   ├── service.py
│   │   │
│   │   └── metric.py
│   │
│   ├── routers
│   │   │
│   │   ├── __init__.py
│   │   │
│   │   ├── action.py
│   │   │
│   │   ├── auth.py
│   │   │
│   │   ├── listing.py
│   │   │
│   │   └── status.py
│   │
│   ├── schemas
│   │   │
│   │   ├── __init__.py
│   │   │
│   │   ├── auth_schemas.py
│   │   │
│   │   ├── cronjob_schemas.py
│   │   │
│   │   └── status_schemas.py
│   │
│   ├── __init__.py
│   │
│   ├── settings.py
│   │
│   └── utils.py
│
├── __init__.py
│
├── .app_jobs.lock
│
├── .gitignore
│
├── example.env
│
├── installer.sh
│
├── main.py
│
├── README.md
│
├── remon-agent.service
│
├── requirements.txt
│
└── starter.sh
```
