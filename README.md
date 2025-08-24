# PromoQuoter - Cart Management & Promotion Engine

A Spring Boot application that provides cart management capabilities with dynamic promotion application, built using Hexagonal Architecture principles.

## 🏗️ Architecture Overview

This application follows **Hexagonal Architecture (Ports & Adapters)** with clear separation of concerns:

- **Domain Layer**: Core business logic (Product, Order, Promotion models)
- **Application Layer**: Use cases and business orchestration
- **Infrastructure Layer**: External adapters (REST APIs, JPA repositories)

## 📋 Key Features

- **Cart Quote Calculation**: Calculate cart totals with dynamic promotion application
- **Cart Confirmation**: Convert quotes to confirmed orders with stock reservation
- **Promotion Engine**: Flexible promotion system supporting multiple discount types
- **Idempotency Support**: Prevent duplicate order creation using idempotency keys
- **Concurrency Control**: Pessimistic locking for stock management
- **Stock Management**: Real-time stock validation and reservation

## 🔧 Technology Stack

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **H2 Database** (in-memory for development)
- **Maven 3.x**
- **JUnit 5** for testing
- **Mockito** for mocking

## 📚 Business Assumptions

### Products
- Each product has a unique UUID identifier
- Products belong to categories (e.g., "electronics", "furniture")
- Stock levels are managed at the product level
- Products have version control for optimistic locking

### Promotions
- Promotions are applied based on customer segments (e.g., "premium", "vip")
- Promotions have priorities (lower number = higher priority)
- Multiple promotions can be applied to the same cart
- Promotion rules are configurable via Map<String, String> configuration

### Cart Operations
- **Quote**: Calculate totals without affecting stock or creating orders
- **Confirm**: Create actual orders, reserve stock, and apply final pricing
- Orders are immutable once created
- Idempotency keys prevent duplicate order creation

### Concurrency & Consistency
- **Pessimistic Locking**: Used during cart confirmation for stock updates
- **Optimistic Locking**: Used for Product and Order entities via JPA @Version
- **Transactional Boundaries**: Each cart confirmation is a single transaction
- **Idempotency**: Duplicate requests with same idempotency key return existing orders

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd promoquoter
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

4. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Database Access
- **H2 Console**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:prmoquoter`
- **Username**: `sa`
- **Password**: `123456`

## 📡 API Endpoints

### 1. Calculate Cart Quote

**Endpoint**: `POST /cart/quote`

**Description**: Calculate cart totals with promotions applied (no stock reservation)

**Request Body**:
```json
{
  "items": [
    {
      "productId": "9c999771-195f-4b40-8a8f-3dbadf67b2d6",
      "qty": 2
    }
  ],
  "customerSegment": "premium"
}
```

**Response**:
```json
{
  "quoteId": "123e4567-e89b-12d3-a456-426614174000",
  "lineItems": [
    {
      "productId": "9c999771-195f-4b40-8a8f-3dbadf67b2d6",
      "productName": "Gaming Laptop",
      "quantity": 2,
      "unitPrice": 1200.00,
      "lineDiscount": 240.00,
      "lineTotal": 2160.00
    }
  ],
  "appliedPromotions": [
    {
      "promotionId": "promo-123",
      "type": "PERCENTAGE",
      "description": "10% off electronics",
      "amount": 240.00,
      "priority": 1
    }
  ],
  "subtotal": 2400.00,
  "totalDiscount": 240.00,
  "total": 2160.00
}
```

### 2. Confirm Cart Order

**Endpoint**: `POST /cart/confirm`

**Description**: Create confirmed order with stock reservation

**Headers**:
- `Idempotency-Key: unique-key-123` (optional, for duplicate prevention)

**Request Body**:
```json
{
  "items": [
    {
      "productId": "9c999771-195f-4b40-8a8f-3dbadf67b2d6",
      "qty": 2
    }
  ],
  "customerSegment": "premium"
}
```

**Response** (201 Created):
```json
{
  "orderId": "456e7890-e89b-12d3-a456-426614174000",
  "lineItems": [
    {
      "productId": "9c999771-195f-4b40-8a8f-3dbadf67b2d6",
      "productName": "Gaming Laptop",
      "quantity": 2,
      "unitPrice": 1200.00,
      "lineDiscount": 240.00,
      "lineTotal": 2160.00
    }
  ],
  "appliedPromotions": [
    {
      "promotionId": "promo-123",
      "type": "PERCENTAGE",
      "description": "10% off electronics",
      "amount": 240.00,
      "priority": 1
    }
  ],
  "subtotal": 2400.00,
  "totalDiscount": 240.00,
  "total": 2160.00,
  "confirmedAt": "2025-08-24T10:30:00Z",
  "status": "CONFIRMED"
}
```

## 🧪 Sample cURL Commands

### Calculate Quote
```bash
curl -X POST http://localhost:8080/cart/quote \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "9c999771-195f-4b40-8a8f-3dbadf67b2d6",
        "qty": 2
      }
    ],
    "customerSegment": "premium"
  }'
```

### Confirm Cart (without idempotency)
```bash
curl -X POST http://localhost:8080/cart/confirm \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "9c999771-195f-4b40-8a8f-3dbadf67b2d6",
        "qty": 2
      }
    ],
    "customerSegment": "premium"
  }'
```

### Confirm Cart (with idempotency key)
```bash
curl -X POST http://localhost:8080/cart/confirm \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: my-unique-key-12345" \
  -d '{
    "items": [
      {
        "productId": "9c999771-195f-4b40-8a8f-3dbadf67b2d6",
        "qty": 1
      }
    ],
    "customerSegment": "vip"
  }'
```

### Test with Multiple Products
```bash
curl -X POST http://localhost:8080/cart/quote \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "9c999771-195f-4b40-8a8f-3dbadf67b2d6",
        "qty": 1
      },
      {
        "productId": "8d888660-084e-4b3f-9a9e-2c9ace56a1d5",
        "qty": 2
      }
    ],
    "customerSegment": "premium"
  }'
```

## 🔒 Concurrency & Data Consistency

### Pessimistic Locking
- Applied during cart confirmation via `findByIdsForUpdate()`
- Prevents concurrent stock modifications
- Ensures atomic stock reservation

### Optimistic Locking
- JPA `@Version` annotations on Product and Order entities
- Handles concurrent updates at entity level
- Automatic retry on version conflicts

### Idempotency
- Prevents duplicate order creation
- Uses unique constraint on `idempotency_key` column
- Returns existing order for duplicate requests

## 🏗️ Project Structure

```
src/
├── main/java/com/backend/promoquoter/
│   ├── application/
│   │   ├── port/in/          # Inbound ports (use cases)
│   │   ├── port/out/         # Outbound ports (repositories)
│   │   └── service/          # Application services
│   ├── domain/
│   │   └── model/            # Domain entities
│   └── infrastructure/
│       └── adapter/
│           ├── in/web/       # REST controllers
│           └── out/          # JPA adapters
└── test/java/                # Unit and integration tests
```

## 🧪 Testing

### Running All Tests
```bash
mvn test
```

### Running Specific Test Class
```bash
mvn test -Dtest=CartApplicationServiceTest
```

### Test Coverage
- **Unit Tests**: 38 tests covering business logic
- **Integration Tests**: Full application context tests
- **Mock Testing**: Comprehensive mocking of external dependencies

## 🚨 Error Handling

### Common Error Responses

#### Product Not Found (404)
```json
{
  "error": "Product not found",
  "message": "Product not found: invalid-uuid"
}
```

#### Insufficient Stock (400)
```json
{
  "error": "Insufficient stock",
  "message": "Insufficient stock for product 9c999771-195f-4b40-8a8f-3dbadf67b2d6. Requested: 5, Available: 2"
}
```

#### Validation Error (400)
```json
{
  "error": "Validation failed",
  "message": "qty must be greater than 0"
}
```

## 🔧 Configuration

### Application Properties
Key configurations in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:prmoquoter
    username: sa
    password: 123456
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Development vs Production
- **Development**: Uses H2 in-memory database
- **Production**: Configure external database in application-prod.yml