# HR Platform - Microservices Architecture

## Overview
This is a microservices-based HR Management System with the following services:
- **IAM Service** - Authentication and Authorization
- **Recruitment Service** - Job postings, candidates, and applications
- **Employee Service** - Employee records and department management
- **API Gateway** - Central entry point for all services
- **Eureka Server** - Service discovery

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                        │
│          Routes: /auth, /recruitment, /employees            │
└─────────────────────────────────────────────────────────────┘
           │                    │                    │
           ▼                    ▼                    ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  IAM Service     │  │ Recruitment Svc  │  │ Employee Service │
│  Port: 8081      │  │ Port: 8082       │  │ Port: 8083       │
│ PostgreSQL       │  │ MongoDB          │  │ MongoDB          │
└──────────────────┘  └──────────────────┘  └──────────────────┘
           │                    │                    │
           ▼                    ▼                    ▼
        Eureka Client      Eureka Client      Eureka Client
        
           ├──────────────────────────┤
           │                          │
           ▼                          ▼
    ┌─────────────────┐      ┌─────────────────┐
    │ Eureka Server   │      │ Service Discovery│
    │ Port: 8761      │      │                 │
    └─────────────────┘      └─────────────────┘
```

## Project Structure

```
backend/
├── common/                          # Shared DTOs and utilities
├── iam-service/                    # Authentication & Authorization
│   ├── src/main/java/com/hrplatform/iam/
│   │   ├── config/                 # Security & JWT configuration
│   │   ├── controller/             # REST endpoints
│   │   ├── model/                  # User, Role entities
│   │   ├── repository/             # Data access layer
│   │   ├── service/                # Business logic
│   │   └── HrplatformApplication.java
│   └── src/main/resources/
│       └── application.yml
│
├── recruitment-service/            # Jobs & Candidates Management
│   ├── src/main/java/com/hrplatform/recruitment/
│   │   ├── controller/             # /api/jobs, /api/candidates, /api/applications
│   │   ├── model/                  # Job, Candidate, JobApplication
│   │   ├── repository/             # MongoDB repositories
│   │   ├── service/                # Business logic
│   │   └── RecruitmentServiceApplication.java
│   └── src/main/resources/
│       └── application.yml
│
├── employee-service/               # Employee & Department Management
│   ├── src/main/java/com/hrplatform/employee/
│   │   ├── controller/             # /api/employees, /api/departments
│   │   ├── model/                  # Employee, Department
│   │   ├── repository/             # MongoDB repositories
│   │   ├── service/                # Business logic
│   │   └── EmployeeServiceApplication.java
│   └── src/main/resources/
│       └── application.yml
│
├── api-gateway/                    # Spring Cloud Gateway
│   ├── src/main/java/com/hrplatform/gateway/
│   │   └── ApiGatewayApplication.java
│   └── src/main/resources/
│       └── application.yml
│
├── docker-compose.yml              # Database & Eureka containers
├── build.gradle                    # Parent Gradle build
└── settings.gradle                 # Gradle modules configuration
```

## API Endpoints

### IAM Service (Port 8081)
```
POST   /auth/register       - Register new user
POST   /auth/login          - User login
GET    /health              - Health check
```

### Recruitment Service (Port 8082)
```
Jobs:
POST   /api/jobs            - Create job posting
GET    /api/jobs            - Get all jobs
GET    /api/jobs/{jobId}    - Get job by ID
PUT    /api/jobs/{jobId}    - Update job
DELETE /api/jobs/{jobId}    - Delete job
GET    /api/jobs/status/{status} - Get jobs by status
GET    /api/jobs/department/{department} - Get jobs by department

Candidates:
POST   /api/candidates      - Create candidate
GET    /api/candidates      - Get all candidates
GET    /api/candidates/{candidateId} - Get candidate by ID
PUT    /api/candidates/{candidateId} - Update candidate
DELETE /api/candidates/{candidateId} - Delete candidate
GET    /api/candidates/email/{email} - Get candidate by email
GET    /api/candidates/experience/{years} - Get candidates by experience
```

### Employee Service (Port 8083)
```
Employees:
POST   /api/employees       - Create employee
GET    /api/employees       - Get all employees
GET    /api/employees/{employeeId} - Get employee by ID
PUT    /api/employees/{employeeId} - Update employee
DELETE /api/employees/{employeeId} - Delete employee
GET    /api/employees/email/{email} - Get employee by email
GET    /api/employees/department/{departmentId} - Get employees by department
GET    /api/employees/status/{status} - Get employees by status
GET    /api/employees/position/{position} - Get employees by position

Departments:
POST   /api/departments     - Create department
GET    /api/departments     - Get all departments
GET    /api/departments/{departmentId} - Get department by ID
PUT    /api/departments/{departmentId} - Update department
DELETE /api/departments/{departmentId} - Delete department
GET    /api/departments/name/{name} - Get department by name
GET    /api/departments/status/{status} - Get departments by status
GET    /api/departments/location/{location} - Get departments by location
```

### API Gateway (Port 8080)
All requests go through the gateway:
```
/auth/**        → IAM Service
/recruitment/** → Recruitment Service
/employees/**   → Employee Service
```

## Setup Instructions

### Prerequisites
- Java 25 or higher
- Docker & Docker Compose
- Gradle 7.0 or higher
- PostgreSQL (or use Docker)
- MongoDB (or use Docker)

### Database Setup

#### Using Docker Compose
```bash
docker-compose up -d
```

This will start:
- PostgreSQL (port 5432) for IAM Service
- MongoDB instances (ports 27017, 27018) for Recruitment & Employee services

Start the Eureka server from the Gradle module:
```bash
./gradlew :eureka-server:bootRun
```

This exposes the Eureka dashboard on port `8761` for service discovery.

#### Manual Setup

**PostgreSQL (IAM Service):**
```sql
CREATE DATABASE iam_db;
```

**MongoDB (Recruitment Service):**
```bash
mongod --dbpath ./data/recruitment_db
# In mongo shell:
use recruitment_db
```

**MongoDB (Employee Service):**
```bash
mongod --dbpath ./data/employee_db
# In mongo shell:
use employee_db
```

### Building and Running

#### Build All Services
```bash
cd /home/damblador13/Documents/hrmanagment/backend
./gradlew build
```

#### Run Services (separate terminals)

**1. Start Eureka Server**
```bash
cd /home/damblador13/Documents/hrmanagment/backend
./gradlew :eureka-server:bootRun
```

**2. Start IAM Service**
```bash
./gradlew :iam-service:bootRun
```

**3. Start Recruitment Service**
```bash
./gradlew :recruitment-service:bootRun
```

**4. Start Employee Service**
```bash
./gradlew :employee-service:bootRun
```

**5. Start API Gateway**
```bash
./gradlew :api-gateway:bootRun
```

#### Or Use Docker Compose + Gradle
```bash
# Start infrastructure
docker-compose up -d

# Build all services
./gradlew clean build

# Run Eureka server and all services (in background)
./gradlew :eureka-server:bootRun &
./gradlew :iam-service:bootRun &
./gradlew :recruitment-service:bootRun &
./gradlew :employee-service:bootRun &
./gradlew :api-gateway:bootRun &
```

### Service URLs

| Service | URL | Port |
|---------|-----|------|
| API Gateway | http://localhost:8080 | 8080 |
| IAM Service | http://localhost:8081 | 8081 |
| Recruitment Service | http://localhost:8082 | 8082 |
| Employee Service | http://localhost:8083 | 8083 |
| Eureka Server | http://localhost:8761 | 8761 |
| PostgreSQL | localhost:5432 | 5432 |
| MongoDB (Recruitment) | localhost:27017 | 27017 |
| MongoDB (Employee) | localhost:27018 | 27017 |

## Configuration Files

### application.yml - IAM Service (8081)
- PostgreSQL database connection
- JWT configuration
- Eureka client registration

### application.yml - Recruitment Service (8082)
- MongoDB connection for recruitment_db
- Eureka client registration
- OAuth2 JWT validation

### application.yml - Employee Service (8083)
- MongoDB connection for employee_db
- Eureka client registration
- OAuth2 JWT validation

### application.yml - API Gateway (8080)
- Gateway route configuration
- Service discovery routing rules
- Load balancing

## Technologies Used

- **Spring Boot 4.0.5** - Application framework
- **Spring Cloud 2024.0.0** - Microservices patterns
- **Spring Data MongoDB** - Document store for recruitment & employee services
- **Spring Data JPA** - Relational database access for IAM
- **Spring Security & OAuth2** - Authentication & authorization
- **Spring Cloud Gateway** - API Gateway
- **Netflix Eureka** - Service discovery & registration
- **Java 25** - Programming language
- **Gradle** - Build tool

## Future Enhancements

1. **Message Queue Integration** - Add RabbitMQ/Kafka for async messaging
2. **Circuit Breaker** - Add Resilience4j for fault tolerance
3. **Distributed Tracing** - Add Spring Cloud Sleuth & Zipkin
4. **Configuration Server** - Centralized configuration management
5. **Service-to-Service Communication** - Add WebClient for REST calls
6. **Kubernetes Deployment** - Container orchestration
7. **Logging & Monitoring** - ELK Stack or similar
8. **API Documentation** - Swagger/SpringFox integration
9. **Event Sourcing** - For audit trails and data consistency

## Troubleshooting

### Services can't connect to Eureka
- Ensure Eureka server is running on port 8761
- Check firewall settings
- Verify network connectivity between containers

### MongoDB connection issues
- Check MongoDB is running on ports 27017 and 27018
- Verify authentication credentials
- Check database names match configuration

### PostgreSQL connection issues
- Ensure PostgreSQL is running on port 5432
- Verify database exists and user has permissions
- Check connection string in application.yml

### Port conflicts
- Change ports in individual application.yml files
- Update gateway routing configuration accordingly
- Update docker-compose.yml port mappings

## License
This project is proprietary and confidential.

