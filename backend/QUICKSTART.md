# HR Platform Microservices - Quick Start Guide

## 5-Minute Quick Start

### Prerequisites
- Java 25+
- Docker & Docker Compose installed
- Git (to navigate to project)

### Step 1: Start Infrastructure (1 minute)
```bash
cd /home/damblador13/Documents/hrmanagment/backend
docker-compose up -d
```

Wait for the database containers to be ready. Check status:
```bash
docker ps
```

### Step 2: Start Eureka Server (1 minute)
```bash
cd /home/damblador13/Documents/hrmanagment/backend
./gradlew :eureka-server:bootRun
# Runs on http://localhost:8761
```

### Step 3: Build All Services (2 minutes)
```bash
./gradlew clean build -x test
```

The `-x test` flag skips tests for faster build.

### Step 4: Start Services (Open 4 terminal windows)

**Terminal 1 - IAM Service**
```bash
cd /home/damblador13/Documents/hrmanagment/backend
./gradlew :iam-service:bootRun
# Runs on http://localhost:8081
```

**Terminal 2 - Recruitment Service**
```bash
cd /home/damblador13/Documents/hrmanagment/backend
./gradlew :recruitment-service:bootRun
# Runs on http://localhost:8082
```

**Terminal 3 - Employee Service**
```bash
cd /home/damblador13/Documents/hrmanagment/backend
./gradlew :employee-service:bootRun
# Runs on http://localhost:8083
```

**Terminal 4 - API Gateway**
```bash
cd /home/damblador13/Documents/hrmanagment/backend
./gradlew :api-gateway:bootRun
# Runs on http://localhost:8080
```

### Step 5: Test the Services (2 minutes)

Wait ~30 seconds for all services to register with Eureka.

**Check Eureka Dashboard:**
Visit: http://localhost:8761

You should see 4 services registered:
- IAM-SERVICE
- RECRUITMENT-SERVICE
- EMPLOYEE-SERVICE
- API-GATEWAY

**Test IAM Service:**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "role": "EMPLOYEE"
  }'
```

**Test Recruitment Service - Create Job:**
```bash
curl -X POST http://localhost:8082/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Software Engineer",
    "description": "We are hiring senior engineers",
    "department": "Engineering",
    "location": "New York",
    "employmentType": "FULL_TIME",
    "salaryMin": 100000,
    "salaryMax": 150000,
    "createdBy": "admin"
  }'
```

**Test Employee Service - Create Department:**
```bash
curl -X POST http://localhost:8083/api/departments \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Engineering",
    "description": "Engineering Department",
    "location": "New York"
  }'
```

**Test API Gateway:**
```bash
curl -X GET http://localhost:8080/recruitment/api/jobs
```

## Service URLs Reference

| Service | URL |
|---------|-----|
| **API Gateway** | http://localhost:8080 |
| **IAM Service** | http://localhost:8081 |
| **Recruitment Service** | http://localhost:8082 |
| **Employee Service** | http://localhost:8083 |
| **Eureka Dashboard** | http://localhost:8761 |
| **PostgreSQL** | localhost:5432 (password: `password`) |
| **MongoDB Recruitment** | localhost:27017 |
| **MongoDB Employee** | localhost:27018 |

## Common Endpoints

### Authentication (IAM Service - Port 8081)
```
POST   /auth/register
POST   /auth/login
```

### Jobs Management (Recruitment Service - Port 8082)
```
POST   /api/jobs
GET    /api/jobs
GET    /api/jobs/{jobId}
PUT    /api/jobs/{jobId}
DELETE /api/jobs/{jobId}
```

### Candidates (Recruitment Service - Port 8082)
```
POST   /api/candidates
GET    /api/candidates
GET    /api/candidates/{candidateId}
```

### Employees (Employee Service - Port 8083)
```
POST   /api/employees
GET    /api/employees
GET    /api/employees/{employeeId}
```

### Departments (Employee Service - Port 8083)
```
POST   /api/departments
GET    /api/departments
GET    /api/departments/{departmentId}
```

## Database Access

### PostgreSQL (IAM Database)
```bash
docker exec -it postgres-iam psql -U postgres -d iam_db
```

### MongoDB (Recruitment)
```bash
docker exec -it mongodb-recruitment mongosh
use recruitment_db
db.jobs.find()
```

### MongoDB (Employee)
```bash
docker exec -it mongodb-employee mongosh
use employee_db
db.employees.find()
```

## Troubleshooting

### Services won't start
1. Check if ports are already in use: `lsof -i :8080` (replace 8080 with port)
2. Kill process: `kill -9 <PID>`
3. Restart service

### Services can't connect to Eureka
1. Verify Eureka server is running: `./gradlew :eureka-server:bootRun`
2. Check the terminal output for startup errors
3. Verify port 8761 is accessible

### MongoDB connection error
1. Check containers: `docker ps | grep mongo`
2. Check logs: `docker logs mongodb-recruitment`
3. Verify credentials in `application.yml`

### Build fails
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies

# Or skip tests
./gradlew clean build -x test
```

## Stopping Services

**Stop all services:**
```bash
# Kill Java processes or press Ctrl+C in each terminal

# Stop Docker containers
docker-compose down
```

**Remove all data:**
```bash
docker-compose down -v  # -v removes volumes (databases)
```

## Project Structure Reminder

```
backend/
├── iam-service/              # Authentication & Authorization
├── recruitment-service/      # Jobs & Candidates
├── employee-service/         # Employees & Departments
├── api-gateway/              # API Gateway
├── eureka-server/             # Service discovery
├── common/                   # Shared code
└── docker-compose.yml        # Infrastructure
```

## Next Steps

1. **Add more features** - Extend models and services as needed
2. **Add messaging** - Integrate RabbitMQ/Kafka for async operations
3. **Add monitoring** - Integrate Prometheus & Grafana
4. **Containerize** - Create Dockerfiles for each service
5. **Deploy** - Push to Kubernetes or cloud provider

## Development Tips

### Run specific service
```bash
./gradlew :iam-service:bootRun
```

### Run tests
```bash
./gradlew test
```

### Build specific module
```bash
./gradlew :recruitment-service:build
```

### View Gradle tasks
```bash
./gradlew tasks
```

### Enable debug mode
Add to `application.yml`:
```yaml
logging:
  level:
    root: INFO
    com.hrplatform: DEBUG
```

## File Locations

- **Settings**: `/home/damblador13/Documents/hrmanagment/backend/settings.gradle`
- **Build Config**: `/home/damblador13/Documents/hrmanagment/backend/build.gradle`
- **IAM Config**: `/home/damblador13/Documents/hrmanagment/backend/iam-service/src/main/resources/application.yml`
- **Recruitment Config**: `/home/damblador13/Documents/hrmanagment/backend/recruitment-service/src/main/resources/application.yml`
- **Employee Config**: `/home/damblador13/Documents/hrmanagment/backend/employee-service/src/main/resources/application.yml`
- **Gateway Config**: `/home/damblador13/Documents/hrmanagment/backend/api-gateway/src/main/resources/application.yml`
- **Docker Compose**: `/home/damblador13/Documents/hrmanagment/backend/docker-compose.yml`

## For More Details

See: `/home/damblador13/Documents/hrmanagment/backend/MICROSERVICES_README.md`

