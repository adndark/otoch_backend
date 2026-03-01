# Otoch Backend

A simple Java Spring Boot RESTful service with Docker support.

## Prerequisites

- **Java 17+** (for local development)
- **Maven 3.9+** (for local development)
- **Docker** (for containerized deployment)

## Project Structure

```
otoch_backend/
├── src/main/java/com/otoch/
│   ├── OtochApplication.java       # Main application entry point
│   ├── controller/
│   │   ├── ItemController.java     # REST API for items
│   │   └── HealthController.java   # Health check endpoint
│   ├── model/
│   │   └── Item.java               # Item data model
│   └── service/
│       └── ItemService.java        # Business logic
├── src/main/resources/
│   └── application.properties      # Application configuration
├── pom.xml                         # Maven dependencies
├── Dockerfile                      # Docker image configuration
├── docker-compose.yml              # Docker Compose configuration
└── README.md
```

## Running the Application

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start the container
docker-compose up --build

# Run in detached mode
docker-compose up --build -d

# Stop the container
docker-compose down
```

### Option 2: Using Docker Directly

```bash
# Build the Docker image
docker build -t otoch-backend .

# Run the container
docker run -p 8080:8080 otoch-backend
```

### Option 3: Running Locally with Maven

```bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR directly
java -jar target/otoch-backend-0.0.1-SNAPSHOT.jar
```

## API Documentation

The service runs on `http://localhost:8080` by default.

### Health Check

Check if the service is running.

```bash
curl http://localhost:8080/api/health
```

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2026-02-28T12:00:00Z",
  "service": "otoch-backend"
}
```

### Items API

#### Get All Items

```bash
curl http://localhost:8080/api/items
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Sample Item 1",
    "description": "A sample item for testing",
    "price": 19.99
  },
  {
    "id": 2,
    "name": "Sample Item 2",
    "description": "Another sample item",
    "price": 29.99
  }
]
```

#### Get Item by ID

```bash
curl http://localhost:8080/api/items/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Sample Item 1",
  "description": "A sample item for testing",
  "price": 19.99
}
```

#### Create a New Item

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Item",
    "description": "A brand new item",
    "price": 49.99
  }'
```

**Response (201 Created):**
```json
{
  "id": 3,
  "name": "New Item",
  "description": "A brand new item",
  "price": 49.99
}
```

#### Update an Item

```bash
curl -X PUT http://localhost:8080/api/items/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Item",
    "description": "This item has been updated",
    "price": 24.99
  }'
```

**Response:**
```json
{
  "id": 1,
  "name": "Updated Item",
  "description": "This item has been updated",
  "price": 24.99
}
```

#### Delete an Item

```bash
curl -X DELETE http://localhost:8080/api/items/1
```

**Response:** `204 No Content`

### Error Responses

**Item Not Found (404):**
```bash
curl http://localhost:8080/api/items/999
```
Returns `404 Not Found`

**Validation Error (400):**
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "price": -10
  }'
```
Returns `400 Bad Request` with validation error details.

## API Endpoints Summary

| Method | Endpoint           | Description          |
|--------|-------------------|----------------------|
| GET    | `/api/health`     | Health check         |
| GET    | `/api/items`      | Get all items        |
| GET    | `/api/items/{id}` | Get item by ID       |
| POST   | `/api/items`      | Create a new item    |
| PUT    | `/api/items/{id}` | Update an item       |
| DELETE | `/api/items/{id}` | Delete an item       |

## Configuration

Environment variables can be set to configure the application:

| Variable                | Default | Description            |
|------------------------|---------|------------------------|
| `SERVER_PORT`          | 8080    | Server port            |
| `SPRING_PROFILES_ACTIVE` | default | Active Spring profile |

## Actuator Endpoints

Spring Boot Actuator provides additional monitoring endpoints:

- `http://localhost:8080/actuator/health` - Application health
- `http://localhost:8080/actuator/info` - Application info
