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
│   │   ├── authentication.py
│   │   │
│   │   ├── cronjob.py
│   │   │
│   │   ├── metrics.py
│   │   │
│   │   └── service.py
│   │
│   ├── routers
│   │   │
│   │   ├── __init__.py
│   │   │
│   │   ├── auth_route.py
│   │   │
│   │   ├── cronjob_route.py
│   │   │
│   │   ├── metric_route.py
│   │   │
│   │   └── service_route.py
│   │
│   ├── schemas
│   │   │
│   │   ├── __init__.py
│   │   │
│   │   ├── auth_schemas.py
│   │   │
│   │   ├── cronjob_schemas.py
│   │   │
│   │   ├── metrics_schemas.py
│   │   │
│   │   └── services_schemas.py
│   │
│   ├── __init__.py
│   │
│   ├── settings.py
│   │
│   └── utils.py
│
├── __init__.py
│
├── .pass
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
