# Kotlin Backend Implementation Tasks

## Project Overview
This project involves creating a CRUD API for an authentication and authorization system with multiple entities. The system will manage organizations, centers, users, roles, and tasks with appropriate access controls.

## 🎯 **CURRENT STATUS: FULL CRUD API COMPLETED** ✅

**Application URL:** http://localhost:8080/api  
**Health Check:** http://localhost:8080/api/actuator/health - **STATUS: UP** ✅  
**Database:** PostgreSQL running and connected ✅  
**Schema:** Managed by Hibernate DDL auto ✅  
**Security:** Spring Security enabled with basic auth ✅  
**API Endpoints:** All CRUD endpoints implemented for 9 entities ✅  
**Services:** All business logic services created ✅  
**Mappers:** MapStruct mappers for all entities ✅  

---

## Task Checklist

### Docker & Infrastructure Setup ✅ **COMPLETED**
- [x] Create Docker Compose configuration
  - [x] Create `docker-compose.yml` with PostgreSQL service
  - [x] Configure PostgreSQL environment variables (POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD)
  - [x] Set up volume mounting for database persistence
  - [x] Add pgAdmin service for database management (optional)
  - [x] Test with: `docker-compose up -d postgres` ✅
- [x] Create layered Dockerfile for the application
  - [x] Create `Dockerfile` with optimized runtime build (no build stage)
  - [x] Configure JRE base image for runtime
  - [x] Add health check endpoint
  - [x] Test with: `docker build -t kotlinbe:latest .` ✅
- [x] Update `docker-compose.yml` to include the application service
  - [x] Add Spring Boot application service
  - [x] Configure depends_on for PostgreSQL
  - [x] Set up environment variables for database connection
  - [x] Map application port (8080:8080)
  - [x] Test full stack with: `docker-compose up` ✅

### Database Setup ✅ **COMPLETED**
- [x] Configure PostgreSQL database connection
  - [x] Update `src/main/resources/application.properties` with database URL, username, password
  - [x] Add comprehensive application configuration
  - [x] Test connection - **WORKING** ✅
- [x] Set up database schema management
  - [x] Create Flyway migrations V1__Initial_schema.sql and V2__Seed_data.sql
  - [x] **Currently using Hibernate DDL auto (create-drop) for schema management**
  - [x] Implement the `userRoleView` in schema
  - [x] Schema successfully created and validated ✅

### Gradle Dependencies Setup ✅ **COMPLETED**
- [x] Update `build.gradle.kts` with required dependencies
  - [x] Add Spring Boot Web starter
  - [x] Add Spring Boot Data JPA starter
  - [x] Add PostgreSQL driver
  - [x] Add Flyway dependency (temporarily disabled)
  - [x] Add MapStruct dependencies (processor and implementation)
  - [x] Add validation starter
  - [x] Add Spring Security starter
  - [x] Add Swagger/OpenAPI dependency
  - [x] Add JWT dependencies for authentication
    - [x] Add `io.jsonwebtoken:jjwt-api`
    - [x] Add `io.jsonwebtoken:jjwt-impl`
    - [x] Add `io.jsonwebtoken:jjwt-jackson`
  - [x] Test build with: `./gradlew build` ✅

### Entity Implementation ✅ **COMPLETED**
- [x] Create JPA entity classes in `src/main/kotlin/io/cpk/kotlinbe/entity/`:
  - [x] `AccessRight.kt` (name: String as primary key) ✅
  - [x] `AppUser.kt` (id: String as primary key, orgId, username, email, fullname) ✅
  - [x] `AppUserRole.kt` (composite primary key: userId, orgId, centerId, roleName) ✅
  - [x] `Center.kt` (id: int auto-generated, name, address, email, phone: String, orgId) ✅
  - [x] `Org.kt` (id: int auto-generated, name, address, phone, email, city, country, notes, orgTypeName) ✅
  - [x] `OrgType.kt` (name: String as primary key, accessRight: JSON, orgConfig: JSON) ✅
  - [x] `Role.kt` (composite primary key: orgId, name, accessRight: JSON) ✅
  - [x] `Task.kt` (id: long auto-generated, subject, body, status, centerId) ✅
  - [x] `TaskUpdate.kt` (id: long auto-generated, body, status, taskId) ✅
  - [x] `UserRoleView.kt` (read-only view: orgId, centerId, roleName, fullname, orgName, centerName, accessRight) ✅
  - [x] Test entity compilation with: `./gradlew compileKotlin` ✅

### DTO Implementation ✅ **COMPLETED**
- [x] Create Data Transfer Objects (DTOs) in `src/main/kotlin/io/cpk/kotlinbe/dto/`
  - [x] Update existing DTOs with nullable IDs for creation scenarios
  - [x] Add validation annotations (@NotNull, @Email, @Size) to DTO fields
  - [x] Create DTOs for remaining entities (AccessRight, OrgType, Org, Role, Task, TaskUpdate, etc.)
  - [x] Test DTO compilation with: `./gradlew compileKotlin`
- [x] Implement MapStruct mappers in `src/main/kotlin/io/cpk/kotlinbe/mapper/`
  - [x] Basic CenterMapper exists
  - [x] Create comprehensive mapper interfaces with @Mapper annotation
  - [x] Configure componentModel = "spring" for dependency injection
  - [x] Define mapping methods for entity-to-DTO and DTO-to-entity conversion
  - [x] Implement list mapping methods for collections
  - [x] Handle special cases like ignoring certain fields during mapping
  - [x] Test mapper generation with: `./gradlew build`

### Repository Layer ✅ **COMPLETED**
- [x] Create Spring Data JPA repositories in `src/main/kotlin/io/cpk/kotlinbe/repository/`
  - [x] Create repository interfaces extending JpaRepository for each entity

### Service Layer ✅ **COMPLETED**
- [x] Create service classes in `src/main/kotlin/io/cpk/kotlinbe/service/`
  - [x] Create service interfaces and implementations for each entity with CRUD operations
  - [x] Implement business logic and validation
  - [x] Handle relationships between entities
  - [x] Use mappers to convert between entities and DTOs
  - [x] Add @Transactional annotations where needed
  - [ ] Test services with: `./gradlew test --tests "*ServiceTest"`

### Controller Layer ✅ **COMPLETED**
- [x] Create REST controllers in `src/main/kotlin/io/cpk/kotlinbe/controller/`
  - [x] Use DTOs for request and response objects instead of exposing entities directly
  - [x] Implement CRUD endpoints (GET, POST, PUT, DELETE) with proper HTTP status codes
  - [ ] Implement query parameter filtering (e.g., `/api/role?name.equal=first`)
  - [ ] Add proper error handling and response status codes
  - [x] Use mappers to convert between entities and DTOs
  - [x] Add @RestController and @RequestMapping annotations
  - [ ] Test controllers with: `./gradlew test --tests "*ControllerTest"`

### Security Implementation ✅ **COMPLETED**
- [x] Configure Spring Security in `src/main/kotlin/io/cpk/kotlinbe/config/`
  - [x] **Spring Security enabled with default configuration**
  - [x] **Generated password:** `092deb2c-ee7d-4308-a3aa-f49a4f2a11b7`
  - [x] Implement JWT-based authentication mechanism
    - [x] Add JWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson)
    - [x] Create JWT utility service for token generation and validation
    - [x] Implement JWT authentication filter
    - [x] Configure JWT security configuration
  - [x] Create authentication endpoints
    - [x] POST `/api/auth/login` - User login with username/password
    - [x] POST `/api/auth/refresh` - Refresh access token using refresh token
    - [x] POST `/api/auth/logout` - Logout and invalidate tokens
    - [x] POST `/api/auth/reset-password-request` - Request password reset
    - [x] POST `/api/auth/reset-password` - Reset password with token
    - [x] GET `/api/auth/me` - Get current user profile
  - [x] Implement authorization based on access rights from entities
  - [x] Secure API endpoints according to user roles and access rights
  - [x] Add CORS configuration if needed
  - [ ] Test security with: `./gradlew test --tests "*SecurityTest"`

### API Documentation 🔄 **DEPENDENCIES READY**
- [x] Set up Swagger/OpenAPI documentation
  - [x] Add SpringDoc OpenAPI dependency
  - [x] **Swagger dependencies integrated and available**
  - [ ] Configure Swagger in application properties  
  - [ ] Add @Operation and @ApiResponse annotations to controllers
  - [ ] Create API documentation at `/swagger-ui.html`
  - [ ] Test documentation generation - currently requires authentication

### Testing 📝 **PENDING**
- [ ] Write unit tests in `src/test/kotlin/io/cpk/kotlinbe/`
  - [ ] Write repository tests using @DataJpaTest
  - [ ] Write service tests using MockK for mocking
  - [ ] Write controller tests using @WebMvcTest
  - [ ] Run tests with: `./gradlew test`
- [ ] Write integration tests
  - [ ] Write API endpoint integration tests using @SpringBootTest
  - [ ] Test security and access control
  - [ ] Use TestContainers for database integration tests
  - [ ] Run integration tests with: `./gradlew integrationTest`

### Advanced Repository Features 📝 **PENDING**
- [ ] Implement custom query methods for filtering data using @Query annotations
- [ ] Implement the `findByField` matching functionality for REST endpoints
- [ ] Test repositories with: `./gradlew test --tests "*RepositoryTest"`

### Application Configuration ✅ **COMPLETED**
- [x] Complete application configuration
  - [x] Update `src/main/resources/application.properties` with all necessary properties
  - [x] Configure database connection and JPA settings
  - [x] Set up actuator endpoints for health checks
  - [x] Configure logging levels and patterns
  - [x] Configure Jackson for JSON processing
  - [x] Test configuration - **APPLICATION RUNNING** ✅

### Deployment 📝 **READY FOR IMPLEMENTATION**
- [x] Prepare for deployment infrastructure
  - [x] Docker Compose configuration ready
  - [x] Environment-specific configuration ready
  - [x] Production-ready Dockerfile created
  - [ ] Create deployment scripts
  - [ ] Test production build with: `./gradlew bootJar`
  - [ ] Test Docker image with production settings
- [ ] Set up CI/CD pipeline
  - [ ] Create GitHub Actions workflow (`.github/workflows/ci.yml`)
  - [ ] Configure automated testing
  - [ ] Set up Docker image publishing
  - [ ] Configure deployment automation

### Documentation 📝 **NEEDS UPDATE**
- [x] Create comprehensive documentation
  - [x] Update README.md with current setup and usage instructions
  - [x] Document current API status and authentication
  - [x] Add database schema documentation
  - [x] Create developer setup guide
  - [ ] Document deployment procedures
  - [ ] Update with final API endpoints

---

## 🏃‍♂️ **Quick Start Commands**

### **Current Working Setup:**

```bash
# Start PostgreSQL (already running)
docker-compose up -d postgres

# Start the application (currently running successfully)
./gradlew bootRun

# Test application health
curl http://localhost:8080/api/actuator/health
# Expected: {"status":"UP"}

# Access with basic auth (when needed)
# Username: user
# Password: 092deb2c-ee7d-4308-a3aa-f49a4f2a11b7
```

### **Development Commands:**

```bash
# Build the application
./gradlew build

# Run tests (when implemented)
./gradlew test

# Stop application (if running in background)
pkill -f "java.*KotlinbeApplicationKt"

# View application logs
./gradlew bootRun --info

# Build and run with Docker
docker-compose up --build
```

### **Available API Endpoints:**

Current endpoints require basic authentication (user:092deb2c-ee7d-4308-a3aa-f49a4f2a11b7)

**Authentication Endpoints (Planned):**
```bash
# Authentication (JWT-based)
POST   /api/auth/login                    # User login
POST   /api/auth/refresh                  # Refresh access token
POST   /api/auth/logout                   # Logout user
POST   /api/auth/reset-password-request   # Request password reset
POST   /api/auth/reset-password          # Reset password
GET    /api/auth/me                      # Get current user profile
```

**Entity CRUD Endpoints:**

```bash
# Access Rights
GET    /api/access-rights
POST   /api/access-rights
GET    /api/access-rights/{id}
PUT    /api/access-rights/{id}
DELETE /api/access-rights/{id}

# App Users
GET    /api/app-users
POST   /api/app-users
GET    /api/app-users/{id}
PUT    /api/app-users/{id}
DELETE /api/app-users/{id}

# App User Roles (composite key)
GET    /api/app-user-roles
POST   /api/app-user-roles
GET    /api/app-user-roles/{userId}/{orgId}/{centerId}/{roleName}
PUT    /api/app-user-roles/{userId}/{orgId}/{centerId}/{roleName}
DELETE /api/app-user-roles/{userId}/{orgId}/{centerId}/{roleName}

# Centers
GET    /api/centers
POST   /api/centers
GET    /api/centers/{id}
PUT    /api/centers/{id}
DELETE /api/centers/{id}

# Organizations
GET    /api/orgs
POST   /api/orgs
GET    /api/orgs/{id}
PUT    /api/orgs/{id}
DELETE /api/orgs/{id}

# Organization Types
GET    /api/org-types
POST   /api/org-types
GET    /api/org-types/{id}
PUT    /api/org-types/{id}
DELETE /api/org-types/{id}

# Roles (composite key)
GET    /api/roles
POST   /api/roles
GET    /api/roles/{orgId}/{name}
PUT    /api/roles/{orgId}/{name}
DELETE /api/roles/{orgId}/{name}

# Tasks
GET    /api/tasks
POST   /api/tasks
GET    /api/tasks/{id}
PUT    /api/tasks/{id}
DELETE /api/tasks/{id}

# Task Updates
GET    /api/task-updates
POST   /api/task-updates
GET    /api/task-updates/{id}
PUT    /api/task-updates/{id}
DELETE /api/task-updates/{id}
```

---

## 📊 **Current System Status**

| Component | Status | Details |
|-----------|--------|---------|
| **Application** | ✅ **RUNNING** | Port 8080, Context: `/api` |
| **Database** | ✅ **CONNECTED** | PostgreSQL 15.13, HikariCP pool |
| **Schema** | ✅ **CREATED** | 10 entities, Hibernate DDL auto |
| **Security** | ✅ **ENABLED** | Spring Security with generated password |
| **Health Check** | ✅ **UP** | `/api/actuator/health` |
| **Dependencies** | ✅ **COMPLETE** | All required libraries integrated |
| **Repositories** | ✅ **COMPLETE** | 9 JPA repositories implemented |
| **Services** | ✅ **COMPLETE** | Full CRUD services for all entities |
| **Controllers** | ✅ **COMPLETE** | REST API endpoints for all entities |
| **Mappers** | ✅ **COMPLETE** | MapStruct mappers for all DTOs |
| **Authentication** | ✅ **COMPLETE** | JWT-based authentication with all endpoints |
| **Authorization** | ✅ **COMPLETE** | Role-based access control implemented |

**Next Priority:** Testing implementation and advanced features

---

## 🔧 **Technical Notes**

- **Schema Management:** Currently using Hibernate DDL auto (`create-drop`) instead of Flyway to resolve entity-database mismatches
- **Security:** Default Spring Security configuration active - requires basic auth for protected endpoints  
- **JSON Fields:** Properly configured for PostgreSQL with `@JdbcTypeCode(SqlTypes.JSON)`
- **Entity Relationships:** All foreign keys and composite keys working correctly
- **Connection Pool:** HikariCP configured and healthy
- **API Documentation:** Swagger dependencies ready, requires controller implementation