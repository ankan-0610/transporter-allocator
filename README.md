# transporter-allocator

### 1. Build and Run
```bash
    ./ci-cd.sh
```

### 2. Test the endpoints

#### 2.1 Request Input: 
```bash
    curl -X POST "http://localhost:8080/api/v1/transporters/input" -H \ 
    "Content-type: application/json" -d '
        {
            "lanes": [
            {"id": 1, "origin": "Mumbai", "destination": "Delhi"},
            {"id": 2, "origin": "Delhi", "destination": "Bangalore"},
            {"id": 3, "origin": "Chennai", "destination": "Kolkata"},
            {"id": 4, "origin": "Pune", "destination": "Hyderabad"},
            {"id": 5, "origin": "Ahmedabad", "destination": "Jaipur"},
            {"id": 6, "origin": "Guwahati", "destination": "Shillong"},
            {"id": 7, "origin": "Lucknow", "destination": "Patna"},
            {"id": 8, "origin": "Ranchi", "destination": "Bhubaneswar"},
            {"id": 9, "origin": "Surat", "destination": "Indore"},
            {"id": 10, "origin": "Nagpur", "destination": "Raipur"}
            ],
            "transporters": [
            {"id": 1, "name": "Transporter T1", "laneQuotes": [{"laneId": 1,
            "quote": 5000}, {"laneId": 2, "quote": 6000}]},
            {"id": 2, "name": "Transporter T2", "laneQuotes": [{"laneId": 3,
            "quote": 7000}, {"laneId": 4, "quote": 4000}]},
            {"id": 3, "name": "Transporter T3", "laneQuotes": [{"laneId": 5,
            "quote": 3000}, {"laneId": 6, "quote": 2000}]},
            {"id": 4, "name": "Transporter T4", "laneQuotes": [{"laneId": 7,
            "quote": 8000}, {"laneId": 8, "quote": 5000}]},
            {"id": 5, "name": "Transporter T5", "laneQuotes": [{"laneId": 9,
            "quote": 4500}, {"laneId": 10, "quote": 3500}]},
            {"id": 6, "name": "Transporter T6", "laneQuotes": [{"laneId": 1,
            "quote": 5500}, {"laneId": 2, "quote": 6200}]},
            {"id": 7, "name": "Transporter T7", "laneQuotes": [{"laneId": 3,
            "quote": 7500}, {"laneId": 4, "quote": 4500}]}
            ]
        }
    '
```

response:
```json
    {
        "status": "success",
        "message": "Input processed successfully"
    }
```

#### 2.2 Request assignment: 

Success Scenario: 
```bash
    curl -X POST "http://localhost:8080/api/v1/transporters/assignment" -H \ 
    "Content-type: application/json" -d '
        { "maxTransporters": 5 }
    '
```

response:
```json
    {
        "status": "success",
        "totalCost": 48000,
        "assignments": [
            {
                "laneId": 1,
                "transporterId": 1
            },
            {
                "laneId": 2,
                "transporterId": 1
            },
            {
                "laneId": 3,
                "transporterId": 2
            },
            {
                "laneId": 4,
                "transporterId": 2
            },
            {
                "laneId": 5,
                "transporterId": 3
            },
            {
                "laneId": 6,
                "transporterId": 3
            },
            {
                "laneId": 7,
                "transporterId": 4
            },
            {
                "laneId": 8,
                "transporterId": 4
            },
            {
                "laneId": 9,
                "transporterId": 5
            },
            {
                "laneId": 10,
                "transporterId": 5
            }
        ],
        "selectedTransporters": [
            1,
            2,
            3,
            4,
            5
        ]
    }
```

Fail Scenario: 
```bash
    curl -X POST "http://localhost:8080/api/v1/transporters/assignment" -H \ 
    "Content-type: application/json" -d '
        { "maxTransporters": 3 }
    '
```

response:
```json
    {
        "status": "error",
        "message": "Failed to assign transporters: error: insufficient transporters to cover lanes",
        "path": "/api/v1/transporters/assignment"
    }
```