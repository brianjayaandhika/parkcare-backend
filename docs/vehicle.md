# Ticket API Spec

## Check In

Endpoint : POST /api/check-in

Request Body:
```json
{
  "plateNumber": "B 1234 XYZ"
}
```
Response Body:

```json
{
  "id": "random string uuid",
  "plateNumber": "B 1234 XYZ",
  "checkInAt": "2025-10-21T09:00:00Z"
}
```


## Check Out
Endpoint : POST /api/check-out

Request Body:
```json
{
  "plateNumber": "B 1234 XYZ"
}
```


Response Body:
```json
{
  "id": "random string uuid",
  "plateNumber": "B 1234 XYZ",
  "checkInAt": "2025-10-21T09:00:00Z",
  "checkOutAt": "2025-10-21T011:00:00Z",
  "price": 6000
}
```

## Get All Active Tickets
Endpoint : GET /api/tickets

Response Body:

```json
[
  {
    "id": "random string uuid",
    "plateNumber": "B 1234 XYZ",
    "checkInAt": "2025-10-21T09:00:00Z",
    "checkOutAt": "-",
    "price": 0
  },
  {
    "id": "random string uuid",
    "plateNumber": "B 1234 XYZ",
    "checkInAt": "2025-10-21T09:00:00Z",
    "checkOutAt": "-",
    "price": 0
  }
]
```

## Get All History Tickets
Endpoint : GET /api/tickets-history

Response Body:

```json
[
  {
    "id": "random string uuid",
    "plateNumber": "B 1234 XYZ",
    "checkInAt": "2025-10-21T09:00:00Z",
    "checkOutAt": "2025-10-21T19:00:00Z",
    "price": 30000,
    "idReferences": "random string uuid"
  },
  {
    "id": "random string uuid",
    "plateNumber": "B 1234 XYZ",
    "checkInAt": "2025-10-21T09:00:00Z",
    "checkOutAt": "2025-10-21T19:00:00Z",
    "price": 30000,
    "idReferences": "random string uuid"
  }
]
```

