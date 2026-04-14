# HR Platform Microservices - Architecture & Deployment Guide

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Client Applications                         │
│                     (Web, Mobile, Desktop)                          │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    API Gateway (Port 8080)                          │
│              Spring Cloud Gateway with Load Balancing              │
│                                                                     │
│  Routes:                                                            │
│  ├─ /auth/**        → IAM Service                                  │
│  ├─ /recruitment/** → Recruitment Service                         │
│  └─ /employees/**   → Employee Service                            │
└─────────────────────────────────────────────────────────────────────┘
           │                    │                    │
           ▼                    ▼                    ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  IAM Service     │  │ Recruitment Svc  │  │ Employee Service │
│  Port: 8081      │  │ Port: 8082       │  │ Port: 8083       │
│                  │  │                  │  │                  │
│ Features:        │  │ Features:        │  │ Features:        │
│ ├─ Register User │  │ ├─ Job Postings  │  │ ├─ Employee Mgmt │
│ ├─ Login         │  │ ├─ Candidates    │  │ ├─ Department    │
│ ├─ JWT Tokens    │  │ ├─ Applications  │  │ ├─ Salary Info   │
│ └─ OAuth2        │  │ └─ Scoring       │  │ └─ Attendance    │
└──────────────────┘  └──────────────────┘  └──────────────────┘
           │                    │                    │
           ▼                    ▼                    ▼
        PostgreSQL          MongoDB-1            MongoDB-2
       (iam_db)         (recruitment_db)      (employee_db)
      Port: 5432         Port: 27017          Port: 27018
           │                    │                    │
           └────────────────────┼────────────────────┘
                                │
                                ▼
                    ┌─────────────────────┐
                    │  Eureka Server      │
                    │  (Service Discovery)│
                    │  Port: 8761         │
                    └─────────────────────┘
```

## Service Communication Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ User Request to API Gateway                                     │
│ POST /auth/register                                             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
        ┌───────────────────────────────────────┐
        │ Gateway Route Matching                │
        │ Pattern: /auth/** → IAM Service       │
        └───────────────────────────────────────┘
                              │
                              ▼
        ┌───────────────────────────────────────┐
        │ Service Discovery (Eureka)            │
        │ Find: iam-service instances           │
        └───────────────────────────────────────┘
                              │
                              ▼
        ┌───────────────────────────────────────┐
        │ Load Balancing                        │
        │ Select instance: iam-service:8081     │
        └───────────────────────────────────────┘
                              │
                              ▼
        ┌───────────────────────────────────────┐
        │ Forward Request to IAM Service        │
        │ POST http://iam-service:8081/auth/... │
        └───────────────────────────────────────┘
                              │
                              ▼
        ┌───────────────────────────────────────┐
        │ IAM Service Process Request           │
        │ Validate → Store → Generate Token     │
        └───────────────────────────────────────┘
                              │
                              ▼
        ┌───────────────────────────────────────┐
        │ Response back to API Gateway          │
        │ 201 Created + JWT Token               │
        └───────────────────────────────────────┘
                              │
                              ▼
        ┌───────────────────────────────────────┐
        │ Gateway returns to Client             │
        │ 201 Created + JWT Token + Headers     │
        └───────────────────────────────────────┘
```

## Data Flow Diagram - Job Posting to Application

```
┌──────────────────────────────────────────────────────────────────┐
│ Step 1: HR Creates Job Posting                                   │
│ POST /recruitment/api/jobs                                       │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ API Gateway                         │
        │ Routes to Recruitment Service       │
        └─────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ Recruitment Service                 │
        │ JobController.createJob()           │
        │ → JobService.createJob()            │
        │ → JobRepository.save()              │
        └─────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ MongoDB (recruitment_db)            │
        │ Collections:                        │
        │ ├─ jobs                             │
        │ ├─ candidates                       │
        │ └─ job_applications                 │
        └─────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│ Step 2: Candidate Applies for Job                                │
│ POST /recruitment/api/applications                               │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ API Gateway                         │
        │ Routes to Recruitment Service       │
        └─────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ Recruitment Service                 │
        │ ApplicationController.submit()      │
        │ → ApplicationService.submit()       │
        │ → ApplicationRepository.save()      │
        └─────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ MongoDB                             │
        │ Insert: job_applications collection │
        │ Status: PENDING                     │
        └─────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│ Step 3: Recruiter Reviews & Scores                               │
│ PATCH /recruitment/api/applications/{id}/score                   │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ Application Status Updated          │
        │ MongoDB: Update Score Field         │
        └─────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│ Step 4: If Hired, Create Employee Record                         │
│ POST /employees/api/employees                                    │
└──────────────────────────────────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ Employee Service (Port 8083)        │
        │ EmployeeController.create()         │
        │ → EmployeeService.create()          │
        │ → EmployeeRepository.save()         │
        └─────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ MongoDB (employee_db)               │
        │ Insert: employees collection        │
        │ Status: ACTIVE                      │
        └─────────────────────────────────────┘
```

## Deployment Architecture

### Development Environment
```
Local Machine
├── Docker Desktop
│   ├── PostgreSQL Container (5432)
│   ├── MongoDB-1 Container (27017)
│   ├── MongoDB-2 Container (27018)
│   └── Eureka Container (8761)
│
└── Java Applications (localhost)
    ├── iam-service (8081)
    ├── recruitment-service (8082)
    ├── employee-service (8083)
    └── api-gateway (8080)
```

### Production Environment (Kubernetes)
```
Kubernetes Cluster
├── Namespace: hrplatform
│
├── Deployments
│   ├── iam-service-deployment
│   │   ├── Replicas: 2
│   │   └── Resources: CPU 500m, Memory 1Gi
│   ├── recruitment-service-deployment
│   │   ├── Replicas: 3
│   │   └── Resources: CPU 500m, Memory 1Gi
│   ├── employee-service-deployment
│   │   ├── Replicas: 3
│   │   └── Resources: CPU 500m, Memory 1Gi
│   └── api-gateway-deployment
│       ├── Replicas: 2
│       └── Resources: CPU 1000m, Memory 2Gi
│
├── Services
│   ├── iam-service-service (LoadBalancer/ClusterIP)
│   ├── recruitment-service-service (ClusterIP)
│   ├── employee-service-service (ClusterIP)
│   ├── api-gateway-service (LoadBalancer)
│   └── eureka-service (ClusterIP)
│
├── StatefulSets
│   ├── postgresql-statefulset (1 replica)
│   ├── mongodb-recruitment-statefulset (1 replica)
│   └── mongodb-employee-statefulset (1 replica)
│
├── PersistentVolumes
│   ├── postgresql-pv (50Gi)
│   ├── mongodb-recruitment-pv (100Gi)
│   └── mongodb-employee-pv (100Gi)
│
└── ConfigMaps
    ├── iam-config
    ├── recruitment-config
    ├── employee-config
    └── gateway-config
```

## Network Architecture

### Internal Communication (Service-to-Service)
```
API Gateway (8080)
├── Calls IAM Service via: http://iam-service:8081
│   (using Eureka service discovery)
├── Calls Recruitment via: http://recruitment-service:8082
│   (using Eureka service discovery)
└── Calls Employee via: http://employee-service:8083
    (using Eureka service discovery)
```

### External Communication (Client-to-Services)
```
Client Requests
└── http://localhost:8080 (API Gateway)
    ├── /auth/** → routed to iam-service
    ├── /recruitment/** → routed to recruitment-service
    └── /employees/** → routed to employee-service
```

## Security Architecture

### Authentication Flow
```
1. User sends credentials to /auth/register or /auth/login
   ↓
2. IAM Service validates credentials
   ↓
3. IAM Service generates JWT token (HS256 algorithm)
   ↓
4. Client stores JWT token
   ↓
5. Client includes JWT in Authorization header for subsequent requests
   ↓
6. API Gateway validates JWT via JWT Filter
   ↓
7. Request forwarded to appropriate service with user info in claims
```

### Authorization Strategy
```
JWT Token Claims:
├── sub (subject): user email
├── uid (user id): UUID
├── role: USER_ROLE (e.g., EMPLOYEE, RECRUITER, ADMIN)
├── iat (issued at): timestamp
└── exp (expiration): 3600 seconds (1 hour)

Services verify JWT and extract user info from claims
before processing requests.
```

## Data Consistency Strategy

### Immediate Consistency (Within Service)
```
Actions within same database:
- Create job posting
- Update candidate profile
- Register user

Handled by: MongoDB/PostgreSQL transactions
```

### Eventual Consistency (Across Services)
```
Example: Employee hired from recruitment
- Recruitment Service updates ApplicationStatus → ACCEPTED
- Recruitment Service publishes event "EmployeeHired"
- Employee Service subscribes and creates Employee record
- Both services eventually consistent

Handled by: Message Queue (future: RabbitMQ/Kafka)
```

## Performance Considerations

### Database Indexing
```
MongoDB Recruitment:
- jobs: indexed on {status, department}
- candidates: indexed on {email}
- job_applications: indexed on {jobId, candidateId, status}

PostgreSQL IAM:
- users: indexed on {email}
```

### Caching Strategy (Future)
```
Can implement Redis for:
- JWT token blacklist
- Job posting cache
- Department cache
- Employee records cache
```

### Load Balancing
```
API Gateway uses round-robin load balancing:
- 2 IAM Service instances
- 3 Recruitment Service instances
- 3 Employee Service instances
- 2 API Gateway instances (behind external LB)
```

## Monitoring & Observability

### Health Checks
```
Each service exposes:
- /actuator/health - Spring Boot Actuator
- /health - Custom health endpoint

Eureka monitors service health every 30 seconds
```

### Logging Strategy
```
Centralized logging (future implementation):
- ELK Stack (Elasticsearch, Logstash, Kibana)
- Spring Cloud Sleuth for distributed tracing
- Each service logs to stdout (Docker/Kubernetes captures)
```

### Metrics
```
Prometheus metrics (future):
- Request count per endpoint
- Response time percentiles
- Error rates
- Database connection pool stats
- JVM metrics
```

## Disaster Recovery & High Availability

### Backup Strategy
```
Databases:
- PostgreSQL: Daily snapshots, 7-day retention
- MongoDB: Replica sets for replication
- Backup location: Cloud storage (S3/GCS)

Applications:
- Blue-Green deployments
- Canary deployments
- Automated rollback on errors
```

### Failover Strategy
```
Service Failure:
- Eureka detects unhealthy instance (30 seconds)
- API Gateway stops routing to failed instance
- Requests directed to healthy instances

Database Failure:
- MongoDB replica sets auto-failover
- PostgreSQL failover to standby (manual or automated)
```

## Cost Optimization

### Resource Allocation
```
Development:
- Minimal resources for testing
- Single instance per service
- Shared database servers

Production:
- Multiple replicas for availability
- Dedicated database servers
- Auto-scaling based on load
```

### Storage Optimization
```
Implement data archival:
- Move old job applications to archive MongoDB
- Archive completed recruitment cycles
- Compress logs after 30 days
```

## Scalability Roadmap

### Phase 1: Current (Multi-Service)
- 3 microservices
- Eureka service discovery
- Single database per service

### Phase 2: Event-Driven (3-6 months)
- Add RabbitMQ/Kafka message broker
- Implement event sourcing
- Add service-to-service async communication

### Phase 3: Resilience (6-12 months)
- Add Resilience4j circuit breaker
- Implement retry policies
- Add bulkhead pattern

### Phase 4: Kubernetes (12+ months)
- Containerize all services
- Deploy to Kubernetes
- Implement auto-scaling
- Add service mesh (Istio)

## Conclusion

The HR Platform is architected as a modern microservices system with:
- Independent deployment and scaling
- Clear separation of concerns
- Resilient communication patterns
- Comprehensive service discovery
- Security through JWT tokens
- Foundation for enterprise growth

This architecture supports future enhancements like event streaming, advanced monitoring, and Kubernetes deployment while maintaining simplicity for current development.

