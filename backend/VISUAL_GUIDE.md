# HR Platform Microservices - Visual & Reference Guide

## 🎯 System Overview at a Glance

```
┌─────────────────────────────────────────────────────────────────┐
│  HR PLATFORM - MICROSERVICES ARCHITECTURE                       │
│                                                                 │
│  Version: 1.0.0 | Setup Date: April 13, 2026 | Status: ✅    │
└─────────────────────────────────────────────────────────────────┘

CLIENT APPLICATIONS (Web, Mobile, Desktop)
             │
             ▼
    ┌─────────────────┐
    │ API GATEWAY     │ ◄─── Unified Entry Point
    │ Port: 8080      │      (Spring Cloud Gateway)
    └────┬────┬───────┘
         │    │
    ┌────▼──┐ │    ┌──────────────────┐
    │        │ │    │                  │
    ▼        ▼ ▼    ▼                  ▼
┌──────┐ ┌──────────────┐         ┌──────────────┐
│ IAM  │ │ RECRUITMENT  │         │  EMPLOYEE    │
│ 8081 │ │    8082      │         │    8083      │
└──────┘ └──────────────┘         └──────────────┘
    │            │                      │
    ▼            ▼                      ▼
┌────────┐  ┌────────────┐         ┌────────────┐
│ POSTGRES│  │  MONGO 1   │         │  MONGO 2   │
│ 5432    │  │  27017     │         │  27018     │
└────────┘  └────────────┘         └────────────┘

        ┌──────────────────────┐
        │  EUREKA SERVER 8761  │◄─ Service Discovery
        │  (Service Registry)  │   for all services
        └──────────────────────┘
```

## 📊 Services at a Glance

### 1️⃣ IAM Service (Port 8081)
```
Purpose:      Authentication & Authorization
Database:     PostgreSQL
Endpoints:
  • POST /auth/register       - Create user account
  • POST /auth/login          - Generate JWT token
  • GET  /health              - Health check

Models:
  • User (email, password, role, lastLoginDate)
  • Role (EMPLOYEE, RECRUITER, ADMIN)

Key Features:
  ✓ JWT token generation (3600s expiry)
  ✓ Password hashing with bcrypt
  ✓ Role-based access control
  ✓ Eureka service discovery
```

### 2️⃣ Recruitment Service (Port 8082)
```
Purpose:      Job Postings & Candidate Management
Database:     MongoDB (recruitment_db)
Endpoints:
  • /api/jobs          - Job CRUD operations
  • /api/candidates    - Candidate CRUD operations
  • /api/applications  - Application submission & tracking

Collections:
  • jobs              (Job postings with status)
  • candidates        (Candidate profiles)
  • job_applications  (Applications with scoring)

Key Features:
  ✓ Job posting management
  ✓ Candidate profile storage
  ✓ Application status tracking
  ✓ Scoring & ranking
  ✓ Experience-based filtering
```

### 3️⃣ Employee Service (Port 8083)
```
Purpose:      Employee Records & Department Management
Database:     MongoDB (employee_db)
Endpoints:
  • /api/employees     - Employee CRUD operations
  • /api/departments   - Department CRUD operations

Collections:
  • employees         (Employee records)
  • departments       (Department info)

Key Features:
  ✓ Employee profile management
  ✓ Department organization
  ✓ Salary information
  ✓ Employment type tracking
  ✓ Status management (ACTIVE, INACTIVE, ON_LEAVE)
```

### 4️⃣ API Gateway (Port 8080)
```
Purpose:      Single Entry Point for All Services
Type:         Spring Cloud Gateway
Routing:
  /auth/**        → IAM Service (8081)
  /recruitment/** → Recruitment Service (8082)
  /employees/**   → Employee Service (8083)

Features:
  ✓ Request routing
  ✓ Load balancing
  ✓ Service discovery integration
  ✓ Health checks
```

## 🗂️ Complete File Inventory

### Core Application Files
```
iam-service/
├── HrplatformApplication.java         ◄─ Main app class
├── config/
│   ├── JwtConfig.java                 ◄─ JWT configuration
│   └── SecurityConfig.java            ◄─ Spring Security setup
├── controller/
│   ├── AuthController.java            ◄─ /auth endpoints
│   ├── AuthResponse.java              ◄─ Response DTO
│   ├── LoginRequest.java              ◄─ Request DTO
│   └── RegisterRequest.java           ◄─ Request DTO
├── model/
│   ├── User.java                      ◄─ User entity
│   └── Role.java                      ◄─ Role enum
├── repository/
│   └── UserRepository.java            ◄─ JPA repository
├── service/
│   └── AuthService.java               ◄─ Business logic
└── application.yml                    ◄─ Configuration

recruitment-service/
├── RecruitmentServiceApplication.java ◄─ Main app class
├── controller/
│   ├── JobController.java             ◄─ /api/jobs
│   ├── CandidateController.java       ◄─ /api/candidates
│   └── JobApplicationController.java  ◄─ /api/applications
├── model/
│   ├── Job.java                       ◄─ Job posting
│   ├── Candidate.java                 ◄─ Candidate profile
│   └── JobApplication.java            ◄─ Job application
├── repository/
│   ├── JobRepository.java             ◄─ MongoDB repo
│   ├── CandidateRepository.java       ◄─ MongoDB repo
│   └── JobApplicationRepository.java  ◄─ MongoDB repo
├── service/
│   ├── JobService.java                ◄─ Job business logic
│   ├── CandidateService.java          ◄─ Candidate logic
│   └── JobApplicationService.java     ◄─ Application logic
└── application.yml                    ◄─ Configuration

employee-service/
├── EmployeeServiceApplication.java    ◄─ Main app class
├── controller/
│   ├── EmployeeController.java        ◄─ /api/employees
│   └── DepartmentController.java      ◄─ /api/departments
├── model/
│   ├── Employee.java                  ◄─ Employee entity
│   └── Department.java                ◄─ Department entity
├── repository/
│   ├── EmployeeRepository.java        ◄─ MongoDB repo
│   └── DepartmentRepository.java      ◄─ MongoDB repo
├── service/
│   ├── EmployeeService.java           ◄─ Employee logic
│   └── DepartmentService.java         ◄─ Department logic
└── application.yml                    ◄─ Configuration

api-gateway/
├── ApiGatewayApplication.java         ◄─ Main app class
├── controller/
│   └── HealthController.java          ◄─ Health endpoint
└── application.yml                    ◄─ Gateway routes

common/
├── src/main/java/                     ◄─ Shared DTOs (future)
└── build.gradle                       ◄─ Shared dependencies
```

### Configuration Files
```
Root:
├── build.gradle                       ◄─ Parent build config
├── settings.gradle                    ◄─ Module definitions
└── docker-compose.yml                 ◄─ Infrastructure

Each Service:
├── build.gradle                       ◄─ Service dependencies
└── src/main/resources/
    └── application.yml                ◄─ Spring Boot config
```

### Documentation Files
```
├── MICROSERVICES_README.md            ◄─ Main documentation
├── QUICKSTART.md                      ◄─ 5-minute setup
├── ARCHITECTURE.md                    ◄─ Architecture details
├── IMPLEMENTATION_SUMMARY.md          ◄─ Implementation checklist
└── VISUAL_GUIDE.md                    ◄─ This file
```

## 🚀 Startup Sequence

```
Step 1: Start Infrastructure (1 min)
  docker-compose up -d
  ├─ PostgreSQL (5432)
  ├─ MongoDB Recruitment (27017)
  ├─ MongoDB Employee (27018)
  └─ Eureka Server (8761)
         ↓
Step 2: Build Services (2 min)
  ./gradlew clean build -x test
         ↓
Step 3: Start Services (each in terminal)
  Service 1: ./gradlew :iam-service:bootRun         → 8081
  Service 2: ./gradlew :recruitment-service:bootRun → 8082
  Service 3: ./gradlew :employee-service:bootRun    → 8083
  Service 4: ./gradlew :api-gateway:bootRun         → 8080
         ↓
Step 4: Verify (2 min)
  ✓ Check Eureka: http://localhost:8761
  ✓ All 4 services should be registered
  ✓ Test endpoints
         ↓
✅ System Ready!
```

## 🔐 Security Architecture

```
┌────────────────────────────────────────────────┐
│ Client sends credentials                       │
│ POST /auth/register {email, password, role}    │
└────────────────────┬─────────────────────────┘
                     │
                     ▼
        ┌─────────────────────────────┐
        │ IAM Service validates       │
        │ Hash password with bcrypt   │
        │ Store in PostgreSQL         │
        └────────┬────────────────────┘
                 │
                 ▼
    ┌────────────────────────────────┐
    │ Generate JWT Token             │
    │ Algorithm: HS256               │
    │ Claims:                        │
    │  - sub: user email             │
    │  - uid: user id (UUID)         │
    │  - role: user role             │
    │  - iat: issued at              │
    │  - exp: 3600 seconds later     │
    └────────┬─────────────────────┘
             │
             ▼
    ┌────────────────────────────────┐
    │ Return JWT Token to Client     │
    │ {                              │
    │   "token": "eyJhbG...",        │
    │   "type": "Bearer",            │
    │   "expiresIn": 3600            │
    │ }                              │
    └────────┬─────────────────────┘
             │
             ▼
    ┌────────────────────────────────┐
    │ Client includes in next        │
    │ Authorization: Bearer eyJhbG..│
    └────────┬─────────────────────┘
             │
             ▼
    ┌────────────────────────────────┐
    │ API Gateway validates JWT      │
    │ Extracts user claims           │
    │ Passes to service              │
    └────────┬─────────────────────┘
             │
             ▼
    ┌────────────────────────────────┐
    │ Service processes request      │
    │ with authenticated user info   │
    └────────────────────────────────┘
```

## 📞 Port Reference Map

```
┌─────────────────────────────────────────┐
│  PORT  │  SERVICE            │ TYPE    │
├─────────────────────────────────────────┤
│ 8080   │ API Gateway         │ HTTP    │
│ 8081   │ IAM Service         │ HTTP    │
│ 8082   │ Recruitment Svc     │ HTTP    │
│ 8083   │ Employee Service    │ HTTP    │
│ 8761   │ Eureka Server       │ HTTP    │
│ 5432   │ PostgreSQL          │ DB      │
│ 27017  │ MongoDB (Recruit)   │ DB      │
│ 27018  │ MongoDB (Employee)  │ DB      │
└─────────────────────────────────────────┘

Access:
http://localhost:8080    ← API Gateway (Start here!)
http://localhost:8761    ← Eureka Dashboard (service status)
```

## 🧪 Quick Test Scenarios

### Scenario 1: User Registration & Login
```bash
# 1. Register new user
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@company.com",
    "password": "SecurePass123!",
    "role": "RECRUITER"
  }'
# Response: { "token": "eyJhbG...", "type": "Bearer", ... }

# 2. Login with credentials
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@company.com",
    "password": "SecurePass123!"
  }'
# Response: New JWT token
```

### Scenario 2: Recruitment Workflow
```bash
# 1. Create job posting
TOKEN="eyJhbG..." # from registration
curl -X POST http://localhost:8082/api/jobs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Senior Software Engineer",
    "description": "Looking for experienced engineers",
    "department": "Engineering",
    "location": "San Francisco",
    "employmentType": "FULL_TIME",
    "salaryMin": 120000,
    "salaryMax": 180000,
    "createdBy": "alice@company.com"
  }'
# Response: Job created with ID

# 2. Register candidate
curl -X POST http://localhost:8082/api/candidates \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Bob",
    "lastName": "Developer",
    "email": "bob@email.com",
    "phoneNumber": "+1-555-0001"
  }'
# Response: Candidate created with ID

# 3. Submit application
curl -X POST http://localhost:8082/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": "JOB_ID_HERE",
    "candidateId": "CANDIDATE_ID_HERE",
    "coverLetter": "I am interested in this position..."
  }'
# Response: Application submitted
```

### Scenario 3: Employee Onboarding
```bash
# 1. Create department
curl -X POST http://localhost:8083/api/departments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Engineering",
    "description": "Software Development",
    "location": "San Francisco"
  }'
# Response: Department created with ID

# 2. Create employee record
curl -X POST http://localhost:8083/api/employees \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Bob",
    "lastName": "Developer",
    "email": "bob.developer@company.com",
    "phoneNumber": "+1-555-0001",
    "departmentId": "DEPT_ID_HERE",
    "position": "Senior Software Engineer",
    "employmentType": "FULL_TIME",
    "salary": 150000,
    "createdBy": "alice@company.com"
  }'
# Response: Employee created with ID
```

## 📈 Data Flow Diagram

```
User Registration Flow:
Client
  ↓ POST /auth/register
API Gateway
  ↓ route to iam-service
IAM Service:AuthController
  ↓ call AuthService.register()
AuthService
  ↓ hash password, create User
UserRepository
  ↓ save to database
PostgreSQL
  ↓ insert user
PostgreSQL (response)
  ↓ return user record
AuthService
  ↓ generate JWT token
API Gateway
  ↓ return response
Client receives JWT

---

Job Creation Flow:
Client (with JWT)
  ↓ POST /recruitment/api/jobs
API Gateway
  ↓ validate JWT, route to recruitment-service
Recruitment Service:JobController
  ↓ call JobService.createJob()
JobService
  ↓ set timestamps, create Job
JobRepository
  ↓ save to MongoDB
MongoDB (recruitment_db)
  ↓ insert job document
MongoDB (response)
  ↓ return job with _id
JobRepository
  ↓ return Job object
JobService
  ↓ return Job
JobController
  ↓ return 201 Created
API Gateway
  ↓ return response
Client receives job details
```

## 🛠️ Helpful Commands

```bash
# Check all running containers
docker ps

# View logs of specific service
docker logs mongodb-recruitment
docker logs postgres-iam

# Access MongoDB
docker exec -it mongodb-recruitment mongosh
# Then: use recruitment_db; db.jobs.find();

# Access PostgreSQL
docker exec -it postgres-iam psql -U postgres -d iam_db
# Then: SELECT * FROM users;

# Build specific service
./gradlew :iam-service:build

# Run tests
./gradlew :recruitment-service:test

# View Gradle tasks
./gradlew :iam-service:tasks

# Clean build
./gradlew clean

# Stop all services
docker-compose down

# Remove all volumes (delete data)
docker-compose down -v
```

## 📊 Status Dashboard URL

```
View all services: http://localhost:8761

Expected to see:
✓ IAM-SERVICE (registered)
✓ RECRUITMENT-SERVICE (registered)
✓ EMPLOYEE-SERVICE (registered)
✓ API-GATEWAY (registered)
```

## 🎓 Learning Path

1. **Day 1: Understand Architecture**
   - Read: ARCHITECTURE.md
   - Understand: Microservices pattern, service discovery

2. **Day 2: Setup & Run**
   - Follow: QUICKSTART.md
   - Get system running locally
   - Access all services

3. **Day 3: API Exploration**
   - Test endpoints with curl
   - Create sample data
   - Understand request/response

4. **Day 4: Add Features**
   - Extend models
   - Add new endpoints
   - Modify services

5. **Day 5: Deploy**
   - Containerize services
   - Deploy to test environment
   - Monitor & troubleshoot

---

**🎉 You're all set! The HR Platform microservices are ready for development and production use.**

