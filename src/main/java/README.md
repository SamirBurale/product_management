# Product Management System

A secure and scalable RESTful backend application built with **Java, Spring Boot, Spring Data JPA, Spring Security, JWT, PostgreSQL, Redis, JUnit, and Mockito**.

The application provides product management functionality with role-based access control, validation, pagination, search, centralized exception handling, audit fields, unit testing, and Redis-based caching.

---

## Author

**Samir Burale**

---

## Features

- JWT-based authentication
- Role-based authorization
- Product CRUD operations
- Product search
- Pagination
- Request validation
- Global exception handling
- Audit fields
- Redis caching
- Cache update and eviction
- Unit testing using JUnit 5 and Mockito
- Layered architecture using Controller, Service, Repository and Entity layers

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot | Backend framework |
| Spring Web | REST APIs |
| Spring Data JPA | Database persistence |
| Spring Security | Authentication and authorization |
| JWT | Token-based authentication |
| PostgreSQL | Relational database |
| Redis | Caching |
| Maven | Build and dependency management |
| Lombok | Boilerplate reduction |
| JUnit 5 | Unit testing |
| Mockito | Mocking dependencies |

---

## Architecture

The project follows a layered architecture:

```text
Client / Postman
       |
       v
Controller Layer
       |
       v
Service Layer
       |
       v
Repository Layer
       |
       v
PostgreSQL Database
```

Redis is used as a caching layer for frequently accessed product data:

```text
Client
  |
  v
ProductController
  |
  v
ProductService
  |
  +----> Redis Cache
  |
  +----> ProductRepository
             |
             v
        PostgreSQL
```

---

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/zest/product/
│   │       ├── controller/
│   │       ├── dto/
│   │       │   ├── request/
│   │       │   └── response/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── service/
│   │       │   └── impl/
│   │       └── ProductManagementApplication.java
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/zest/product/
            ├── controller/
            ├── exception/
            └── service/
```

---

# API Documentation

Base URL:

```text
http://localhost:8080/api/v1
```

## Product APIs

| Method | Endpoint | Authorization | Description |
|---|---|---|---|
| POST | `/products` | ADMIN | Create product |
| GET | `/products` | USER / ADMIN | Get all products |
| GET | `/products/{id}` | USER / ADMIN | Get product by ID |
| GET | `/products/search?name=...` | USER / ADMIN | Search products |
| PUT | `/products/{id}` | ADMIN | Update product |
| DELETE | `/products/{id}` | ADMIN | Delete product |

---

## Create Product

### Request

```http
POST /api/v1/products
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

```json
{
  "productName": "Gaming Laptop",
  "description": "High performance gaming laptop",
  "price": 75000,
  "quantity": 10
}
```

### Access

```text
ADMIN
```

---

## Get All Products

```http
GET /api/v1/products?page=0&size=10
Authorization: Bearer <JWT_TOKEN>
```

Pagination is implemented using Spring Data `Pageable`.

Example:

```text
page=0
size=10
```

---

## Get Product By ID

```http
GET /api/v1/products/15
Authorization: Bearer <JWT_TOKEN>
```

Users with `USER` or `ADMIN` authority can access this endpoint.

---

## Search Products

```http
GET /api/v1/products/search?name=laptop&page=0&size=10
Authorization: Bearer <JWT_TOKEN>
```

The search uses case-insensitive product-name matching.

---

## Update Product

```http
PUT /api/v1/products/15
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

```json
{
  "productName": "Updated Gaming Laptop",
  "description": "Updated product description",
  "price": 80000,
  "quantity": 15
}
```

### Access

```text
ADMIN
```

---

## Delete Product

```http
DELETE /api/v1/products/15
Authorization: Bearer <JWT_TOKEN>
```

### Access

```text
ADMIN
```

---

# Authentication and Authorization

Spring Security and JWT are used to secure the APIs.

After successful login, the client receives a JWT token.

The token is sent with subsequent requests:

```text
Authorization: Bearer <JWT_TOKEN>
```

Role-based access is implemented using Spring Security method authorization.

Examples:

```java
@PreAuthorize("hasAuthority('ADMIN')")
```

and:

```java
@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
```

### Authorization Matrix

| Operation | USER | ADMIN |
|---|---:|---:|
| View products | ✅ | ✅ |
| Search products | ✅ | ✅ |
| Create product | ❌ | ✅ |
| Update product | ❌ | ✅ |
| Delete product | ❌ | ✅ |

---

# Redis Caching

Redis is integrated as a caching layer to improve performance for frequently requested product data.

The service uses Spring Cache annotations.

### Read Cache

```java
@Cacheable(value = "products", key = "#id")
```

When a product is requested:

1. Spring checks Redis.
2. If the product exists in the cache, the cached response is returned.
3. If it is not present, the database is queried.
4. The result is stored in Redis.

### Update Cache

```java
@CachePut(value = "products", key = "#id")
```

When a product is updated, the cache entry is refreshed with the latest product response.

### Delete Cache

```java
@CacheEvict(value = "products", key = "#id")
```

When a product is deleted, its corresponding Redis cache entry is removed.

### Redis Configuration

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379

spring.cache.type=redis
```

Redis can be run using Docker:

```bash
docker run -d   --name product-redis   -p 6379:6379   redis
```

Check Redis:

```bash
docker ps
```

---

# Database

The application uses PostgreSQL.

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/product_management
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Database credentials should be supplied through environment variables and should not be committed to GitHub.

---

# Validation

Product requests use Jakarta Bean Validation.

Example:

```java
@Valid
@RequestBody ProductRequest request
```

Invalid requests are handled centrally by the global exception handler.

Example response:

```json
{
  "timestamp": "2026-09-02T10:30:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "productName: Product name is required"
}
```

---

# Global Exception Handling

The application uses:

```java
@RestControllerAdvice
```

to provide centralized exception handling.

Handled exceptions include:

- `ProductNotFoundException`
- `ItemNotFoundException`
- `ItemProductMismatchException`
- `MethodArgumentNotValidException`
- General unexpected exceptions

Example:

```json
{
  "timestamp": "2026-09-02T10:30:00",
  "status": 404,
  "error": "PRODUCT_NOT_FOUND",
  "message": "Product not found with id: 15"
}
```

---

# Audit Information

Products maintain audit information:

```text
createdBy
createdOn
modifiedBy
modifiedOn
```

The currently authenticated username is used for audit information.

Example:

```java
.createdBy(getCurrentUsername())
.createdOn(LocalDateTime.now())
```

During updates:

```java
product.setModifiedBy(getCurrentUsername());
product.setModifiedOn(LocalDateTime.now());
```

---

# Pagination

Spring Data `Pageable` is used for pagination.

Example:

```http
GET /api/v1/products?page=0&size=10
```

The default page size is configured as:

```java
@PageableDefault(size = 10)
```

This avoids loading a large number of records into memory at once.

---

# Unit Testing

The project uses:

- JUnit 5
- Mockito
- Spring Boot Test

The main test areas include:

```text
ProductServiceImplTest
ProductControllerTest
GlobalExceptionHandlerTest
ProductManagementApplicationTests
```

Run all tests:

```bash
mvn clean test
```

Expected result:

```text
BUILD SUCCESS
```

---

# Running the Project

## Prerequisites

Install:

- Java 17+
- Maven
- PostgreSQL
- Docker (for Redis)

## Clone Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd product-management
```

## Start Redis

```bash
docker run -d --name product-redis -p 6379:6379 redis
```

## Configure Database

Update:

```text
src/main/resources/application.properties
```

with your local PostgreSQL configuration.

## Build

```bash
mvn clean install
```

## Run Tests

```bash
mvn test
```

## Run Application

```bash
mvn spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

---

# Git Configuration

Recommended `.gitignore`:

```gitignore
target/
.classpath
.project
.settings/
*.class
*.log

.idea/
.vscode/

.env
application-local.properties
```

Never commit:

- Database passwords
- JWT secrets
- API keys
- `.env` files
- Local configuration containing credentials

---

# GitHub Push

Initialize Git:

```bash
git init
```

Check files:

```bash
git status
```

Add files:

```bash
git add .
```

Commit:

```bash
git commit -m "Complete product management system"
```

Connect GitHub:

```bash
git remote add origin <YOUR_GITHUB_REPOSITORY_URL>
```

Set main branch:

```bash
git branch -M main
```

Push:

```bash
git push -u origin main
```

---

# Future Enhancements

Potential improvements include:

- Swagger / OpenAPI documentation
- Docker Compose for the complete application
- Integration testing with Testcontainers
- Redis monitoring
- Database indexing
- Advanced filtering and sorting
- CI/CD using GitHub Actions
- Rate limiting
- Microservices architecture
- Centralized logging
- API documentation and versioning

---

# Key Interview Concepts Demonstrated

This project demonstrates practical knowledge of:

- REST API design
- Spring Boot
- Dependency Injection
- Layered architecture
- Spring Data JPA
- PostgreSQL
- Spring Security
- JWT authentication
- Role-based authorization
- Bean validation
- Global exception handling
- Pagination
- Redis caching
- Cache invalidation
- JUnit
- Mockito
- Maven
- Git and GitHub

---

## Author

**Samir Burale**

Java Backend Developer
