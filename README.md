# Ecomera Backend

[![Backend CI](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/backend-ci.yml)
[![Qodana](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/qodana.yml/badge.svg)](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/qodana.yml)
[![Docker Release](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/docker-release.yml/badge.svg)](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/docker-release.yml)
[![codecov](https://codecov.io/gh/ecomera-ecosystem/ecomera-backend/branch/master/graph/badge.svg)](https://codecov.io/gh/ecomera-ecosystem/ecomera-backend)

> **Modern e-commerce platform backend** built with Spring Boot 3, featuring JWT authentication, comprehensive API documentation, automated CI/CD, and production-ready infrastructure.

---

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Docker Setup](#-docker-setup)
- [API Documentation](#-api-documentation)
- [Development](#-development)
- [Testing](#-testing)
- [Database Migrations](#-database-migrations)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### Core Functionality
- 🔐 **JWT Authentication** - Secure user registration and login with access/refresh tokens
- 🛍️ **Product Catalog** - CRUD operations, search, filtering, and pagination
- 📦 **Order Management** - Complete order lifecycle with status tracking
- 💳 **Payment Processing** - Multiple payment methods (PayPal, Credit Card, Bank Transfer)
- 👤 **User Profiles** - Profile management with last login tracking

### Infrastructure & DevOps
- 🚀 **CI/CD Pipeline** - Automated testing, building, and Docker image publishing via GitHub Actions
- 🐳 **Docker Support** - Multi-stage Dockerfile and Docker Compose for local development
- 📊 **Code Quality** - Qodana static analysis and JaCoCo code coverage
- 🔍 **Security Scanning** - CodeQL vulnerability detection
- 📝 **Database Versioning** - Liquibase migrations for schema management
- 🕰️ **Audit Trail** - Hibernate Envers for entity change tracking

### Developer Experience
- 📖 **Interactive API Docs** - Swagger/OpenAPI 3.0 with try-it-out functionality
- 🎯 **Exception Handling** - Global error handling with consistent JSON responses
- ✅ **Input Validation** - Bean validation with detailed error messages
- 🔄 **Hot Reload** - Spring Boot DevTools for rapid development

---

## 🛠 Technology Stack

### Core Framework
- **Java 17** - LTS version with modern language features
- **Spring Boot 3.5.9** - Latest Spring Boot 3 with native support
- **Spring Security 6** - OAuth2 resource server with JWT

### Database & Persistence
- **PostgreSQL 16** - Primary database (production)
- **H2 Database** - In-memory database (testing)
- **Spring Data JPA** - Data access with Hibernate 6
- **Liquibase** - Database migration management
- **Hibernate Envers** - Entity auditing and versioning

### Caching & Performance
- **Redis 7** - Distributed caching (planned)
- **HikariCP** - High-performance JDBC connection pooling

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for isolated tests
- **Testcontainers** - Integration testing with real databases
- **JaCoCo** - Code coverage analysis

### Documentation & Code Quality
- **Swagger/OpenAPI 3.0** - Interactive API documentation
- **MapStruct** - Type-safe bean mapping
- **Lombok** - Boilerplate code reduction
- **Qodana** - JetBrains code quality analysis

### DevOps & Deployment
- **Docker** - Containerization with multi-stage builds
- **Docker Compose** - Local multi-service orchestration
- **GitHub Actions** - CI/CD automation
- **Maven** - Dependency management and build tool

---

## 🏗 Architecture

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                     Client Applications                      │
│          (Web Browser, Mobile App, Third-party)             │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTPS
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (Future)                      │
│              Load Balancer + Rate Limiting                   │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   Spring Boot Backend                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Controllers  │  │  Services    │  │ Repositories │      │
│  │  (REST API)  │→ │ (Business    │→ │  (Data       │      │
│  │              │  │  Logic)      │  │   Access)    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                   │             │
│         ▼                  ▼                   ▼             │
│  ┌──────────────────────────────────────────────────┐      │
│  │          Spring Security (JWT Filter)            │      │
│  └──────────────────────────────────────────────────┘      │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
         ▼               ▼               ▼
┌─────────────┐  ┌─────────────┐  ┌──────────────┐
│ PostgreSQL  │  │   Redis     │  │  External    │
│  Database   │  │  (Cache)    │  │  Payment     │
│             │  │             │  │  Gateway     │
└─────────────┘  └─────────────┘  └──────────────┘
```

### Entity-Relationship Diagram
```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│    User     │         │    Order     │         │   Payment   │
├─────────────┤         ├──────────────┤         ├─────────────┤
│ id (UUID)   │────────<│ user_id      │────────>│ order_id    │
│ email       │    1:N  │ id (UUID)    │   1:1   │ id (UUID)   │
│ password    │         │ status       │         │ amount      │
│ firstName   │         │ totalPrice   │         │ method      │
│ lastName    │         │ orderDate    │         │ status      │
│ role        │         └──────┬───────┘         │ txnId       │
└─────────────┘                │                 └─────────────┘
       │                       │
       │                       │ 1:N
       │ 1:N                   ▼
       │              ┌──────────────┐
       │              │  OrderItem   │
       │              ├──────────────┤
       │              │ order_id     │
       │              │ product_id   │───┐
       │              │ quantity     │   │
       │              │ unitPrice    │   │ N:1
       │              └──────────────┘   │
       │                                 │
       ▼                                 ▼
┌─────────────┐                 ┌──────────────┐
│    Token    │                 │   Product    │
├─────────────┤                 ├──────────────┤
│ user_id     │                 │ id (UUID)    │
│ token       │                 │ title        │
│ type        │                 │ description  │
│ expired     │                 │ price        │
│ revoked     │                 │ stock        │
└─────────────┘                 │ category     │
                                │ imageUrl     │
                                └──────────────┘
```

### Request Flow
```
1. Client Request
   │
   ▼
2. JWT Authentication Filter
   │
   ├─ Valid Token? ─> Yes ─> Extract User ─┐
   │                                        │
   └─ No ─> Return 401 Unauthorized         │
                                            ▼
3. Controller (REST Endpoint)
   │
   ├─ @Valid Request? ─> Yes ─> Continue ──┐
   │                                        │
   └─ No ─> Return 400 Bad Request          │
                                            ▼
4. Service Layer (Business Logic)
   │
   ├─ Business Rules OK? ─> Yes ─> Continue ─┐
   │                                          │
   └─ No ─> Throw BusinessException           │
                                              ▼
5. Repository Layer (Data Access)
   │
   ├─ Entity Found? ─> Yes ─> Return Data ───┐
   │                                          │
   └─ No ─> Throw ResourceNotFoundException   │
                                              ▼
6. Mapper (DTO Conversion)
   │
   ▼
7. Response to Client (JSON)
```

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher ([Download](https://adoptium.net/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **Docker** & **Docker Compose** ([Download](https://www.docker.com/products/docker-desktop))
- **Git** ([Download](https://git-scm.com/downloads))

**Optional:**
- **PostgreSQL 16** (if running without Docker)
- **Redis 7** (for caching, planned feature)
- **IntelliJ IDEA** or **VS Code** with Java extensions

---

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/ecomera-ecosystem/ecomera-backend.git
cd ecomera-backend
```

### 2. Run with Docker Compose (Recommended)
```bash
# Copy environment template
cp .env.docker.example .env.docker

# Edit .env.docker with your values (database password, JWT secret, etc.)
nano .env.docker

# Start all services (PostgreSQL, Redis, pgAdmin, Backend)
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

**Access the application:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- pgAdmin: http://localhost:5050 (admin@ecomera.local / admin)

### 3. Run Locally (Without Docker)
```bash
# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/ecomera-0.1.0.jar
```

**Default configuration uses H2 in-memory database - perfect for quick testing!**

---

## 🐳 Docker Setup

### Using Makefile Commands
```bash
# Start all services
make up

# Start in background
make up-d

# View logs
make logs

# Stop all services
make down

# Restart everything
make restart

# Clean up (remove volumes)
make clean

# Database shell
make db-shell

# Redis CLI
make redis-cli

# Backend container shell
make backend-shell
```

### Manual Docker Commands
```bash
# Build and start
docker-compose up --build

# Stop services
docker-compose down

# View running containers
docker-compose ps

# View logs
docker-compose logs -f backend

# Execute command in backend container
docker exec -it ecomera-backend sh
```

### Environment Variables

Create `.env.docker` from template:
```bash
# Database
DB_URL=jdbc:postgresql://postgres:5432/ecomera
DB_USER=postgres
DB_PASS=your_secure_password

# JWT
JWT_SECRET_KEY=your_base64_secret_key
JWT_EXPIRATION_TIME=3600000
REFRESH_EXPIRATION_TIME=604800000

# Redis
REDIS_HOST=redis
REDIS_PORT=6379
```

---

## 📖 API Documentation

### Swagger UI (Interactive)

Open your browser and navigate to:
```
http://localhost:8080/swagger-ui/index.html
```

### Authentication Flow

1. **Register a new user:**
```http
   POST /api/v1/auth/register
   Content-Type: application/json

   {
     "firstname": "John",
     "lastname": "Doe",
     "email": "john.doe@example.com",
     "password": "SecurePass123!",
     "role": "USER"
   }
```

2. **Login to get JWT token:**
```http
   POST /api/v1/auth/authenticate
   Content-Type: application/json

   {
     "email": "john.doe@example.com",
     "password": "SecurePass123!"
   }
```

**Response:**
```json
   {
     "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
```

3. **Use token in subsequent requests:**
```http
   GET /api/v1/auth/me
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### API Endpoints Summary

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| **Authentication** ||||
| POST | `/api/v1/auth/register` | Register new user | ❌ |
| POST | `/api/v1/auth/authenticate` | Login user | ❌ |
| GET | `/api/v1/auth/me` | Get current user | ✅ |
| POST | `/api/v1/auth/refresh-token` | Refresh access token | ✅ |
| **Products** ||||
| GET | `/api/v1/products` | List all products | ❌ |
| GET | `/api/v1/products/{id}` | Get product by ID | ❌ |
| POST | `/api/v1/products` | Create product | ✅ Admin |
| PUT | `/api/v1/products/{id}` | Update product | ✅ Admin |
| DELETE | `/api/v1/products/{id}` | Delete product | ✅ Admin |
| GET | `/api/v1/products/search` | Search products | ❌ |
| GET | `/api/v1/products/category/{category}` | Filter by category | ❌ |
| **Orders** ||||
| POST | `/api/v1/orders` | Create order | ✅ |
| GET | `/api/v1/orders` | List all orders | ✅ Admin |
| GET | `/api/v1/orders/{id}` | Get order details | ✅ |
| GET | `/api/v1/orders/user/{userId}` | Get user orders | ✅ |
| PUT | `/api/v1/orders/{id}/status` | Update order status | ✅ Admin |
| **Payments** ||||
| POST | `/api/v1/payments` | Create payment | ✅ |
| GET | `/api/v1/payments/{id}` | Get payment details | ✅ |
| PUT | `/api/v1/payments/{id}` | Update payment | ✅ Admin |

---

## 💻 Development

### Running the Application
```bash
# Development mode with hot reload
mvn spring-boot:run

# With custom profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Building the Project
```bash
# Clean and build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Build Docker image
docker build -t ecomera-backend:latest .
```

### Code Formatting
```bash
# Format code (if using Spotless)
mvn spotless:apply

# Check formatting
mvn spotless:check
```

---

## 🧪 Testing

### Run All Tests
```bash
# Unit + Integration tests
mvn test

# With coverage report
mvn clean verify

# View coverage report
open target/site/jacoco/index.html
```

### Test Categories

**Unit Tests:**
```bash
# Run only unit tests
mvn test -Dtest="*Test"
```

**Integration Tests:**
```bash
# Run only integration tests
mvn test -Dtest="*IT"
```

**Code Coverage:**
- Target: 70% line coverage
- Current: ~0% (work in progress)
- Tool: JaCoCo + Codecov

---

## 🗄 Database Migrations

### Liquibase Commands
```bash
# Apply pending migrations
mvn liquibase:update

# Rollback last changeset
mvn liquibase:rollback -Dliquibase.rollbackCount=1

# View migration history
mvn liquibase:history

# Generate database documentation
mvn liquibase:dbDoc
open target/liquibase-docs/index.html

# Validate changelogs
mvn liquibase:validate

# Tag current state
mvn liquibase:tag -Dliquibase.tag=v0.1.0

# Rollback to tag
mvn liquibase:rollback -Dliquibase.rollbackTag=v0.1.0
```

### Migration Files

Located in `src/main/resources/db/changelog/changes/`
```
changes/
└── v0.1.0/
    ├── 001-create-users-table.xml
    ├── 002-create-tokens-table.xml
    ├── 003-create-products-table.xml
    ├── 004-create-orders-table.xml
    ├── 005-create-order-items-table.xml
    └── 006-create-payments-table.xml
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflows

**Backend CI** (`backend-ci.yml`)
- Triggers: Push/PR to `master` or `development`
- Steps:
    1. Checkout code
    2. Set up JDK 17
    3. Build with Maven
    4. Run tests
    5. Upload coverage to Codecov
    6. Upload test results

**Qodana Code Quality** (`qodana.yml`)
- Static code analysis
- Code quality checks
- Security vulnerability scanning
- Coverage thresholds (50% fresh, 40% total)

**CodeQL Security** (`codeql.yml`)
- Security vulnerability detection
- Automatic PR comments on issues
- Sarif report generation

**Docker Release** (`docker-release.yml`)
- Triggers: Push to `master`, tags `v*.*.*`
- Steps:
    1. Build multi-stage Docker image
    2. Tag with version and `latest`
    3. Push to Docker Hub
    4. Cache layers for faster builds

**Auto Labeler** (`labeler.yml`)
- Automatically labels PRs/issues
- Based on file patterns and keywords
- Syncs label definitions

### Build Status

Check current build status:
- [Backend CI](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/backend-ci.yml)
- [Qodana Analysis](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/qodana.yml)
- [CodeQL Security](https://github.com/ecomera-ecosystem/ecomera-backend/actions/workflows/codeql.yml)

---

## 📁 Project Structure
```
ecomera-backend/
├── .github/
│   ├── workflows/          # GitHub Actions CI/CD
│   ├── labels.yml          # Label definitions
│   └── labeler.yml         # Auto-labeling rules
├── docs/                   # Documentation
│   └── LIQUIBASE.md       # Database migration guide
├── src/
│   ├── main/
│   │   ├── java/com/youssef/ecomera/
│   │   │   ├── auth/               # Authentication module
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   ├── security/       # JWT, filters
│   │   │   │   └── service/
│   │   │   ├── common/             # Shared components
│   │   │   │   ├── audit/
│   │   │   │   ├── entity/         # BaseEntity
│   │   │   │   └── exception/      # Global exception handling
│   │   │   ├── config/             # Spring configuration
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── AuditConfig.java
│   │   │   ├── domain/             # Business domains
│   │   │   │   ├── order/
│   │   │   │   ├── payment/
│   │   │   │   └── product/
│   │   │   └── user/               # User module
│   │   └── resources/
│   │       ├── db/changelog/       # Liquibase migrations
│   │       ├── application.properties
│   │       └── liquibase.properties.template
│   └── test/                       # Test files
├── target/                         # Build output
├── .env.docker.example            # Docker environment template
├── .gitignore
├── docker-compose.yml             # Multi-service orchestration
├── Dockerfile                     # Multi-stage build
├── Makefile                       # Development shortcuts
├── pom.xml                        # Maven configuration
└── README.md
```

---

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### Getting Started

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):
```
feat(auth): add user profile endpoint
fix(jwt): resolve expiration bug
docs(api): update API documentation
refactor(order): simplify service logic
test(payment): add unit tests
chore(deps): update dependencies
```
### Code Style

- Follow Google Java Style Guide
- Use Lombok for boilerplate reduction
- Write meaningful commit messages
- Add tests for new features
- Update documentation

### Pull Request Checklist

- [ ] Tests pass locally (`mvn test`)
- [ ] Code follows project style
- [ ] Documentation updated
- [ ] No merge conflicts
- [ ] Meaningful commit messages
- [ ] Swagger annotations added for new endpoints

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.
```
MIT License

Copyright (c) 2026 Youssef Ammari

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 📞 Contact & Support

**Author:** Youssef Ammari  
**Email:** youssef.ammari.795@gmail.com  
**GitHub:** [@Ammari-Youssef](https://github.com/Ammari-Youssef)

### Getting Help

- 📖 [API Documentation](http://localhost:8080/swagger-ui/index.html)
- 🐛 [Report Bug](https://github.com/ecomera-ecosystem/ecomera-backend/issues/new?template=bug_report.md)
- 💡 [Request Feature](https://github.com/ecomera-ecosystem/ecomera-backend/issues/new?template=feature_request.md)
- 💬 [Discussions](https://github.com/ecomera-ecosystem/ecomera-backend/discussions)

---

## 🎯 Roadmap

### Sprint 1 (Code Quality & Features)
- [ ] Refactor mappers with BaseMapper pattern
- [ ] Add DTO validation across all controllers
- [ ] Enable Redis caching for product catalog
- [ ] Fix Qodana code quality warnings
- [ ] Increase test coverage to 70%

### Sprint 2 (Testing & Features)
- [ ] Add Cart & CartItem entities
- [ ] Implement checkout workflow
- [ ] Write integration tests
- [ ] Add product search with Elasticsearch

### Sprint 3 (Production Ready)
- [ ] Add monitoring (Prometheus + Grafana)
- [ ] Implement rate limiting
- [ ] Add API versioning
- [ ] Performance optimization

### Future
- [ ] Migrate to microservices architecture
- [ ] Add Kafka for event-driven communication
- [ ] Kubernetes deployment
- [ ] GraphQL API alongside REST

---

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Liquibase](https://www.liquibase.org/) - Database migration tool
- [Swagger](https://swagger.io/) - API documentation
- [Docker](https://www.docker.com/) - Containerization platform
- [JetBrains Qodana](https://www.jetbrains.com/qodana/) - Code quality platform

---

**⭐ If you find this project helpful, please consider giving it a star!**

---

<div align="center">
  <sub>Built with ❤️ by Youssef Ammari</sub>
</div>