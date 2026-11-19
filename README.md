# StockFlow - SaaS Inventory Management System

A full-stack, multi-tenant inventory management system built with React.js and Spring Boot.

## Tech Stack

### Frontend
- React.js 18 with Vite
- Tailwind CSS v4
- React Router v6
- Axios
- React Icons

### Backend
- Java 21
- Spring Boot 3.2.5
- Spring Security with JWT
- Spring Data JPA
- Maven

### Database
- TiDB Cloud (MySQL-compatible)
- MySQL Connector/J with TLS

## Project Structure

```
StockFlow/
├── frontend/          # React.js SPA
│   ├── src/
│   │   ├── api/       # Axios API clients
│   │   ├── components/# Reusable UI components
│   │   ├── context/   # Auth context provider
│   │   └── pages/     # Page components
│   └── ...
├── backend/           # Spring Boot REST API
│   ├── src/main/java/com/stockflow/
│   │   ├── config/    # Security & CORS config
│   │   ├── controller/# REST controllers
│   │   ├── dto/       # Request/Response DTOs
│   │   ├── entity/    # JPA entities
│   │   ├── exception/ # Exception handling
│   │   ├── repository/# Data repositories
│   │   ├── security/  # JWT & auth
│   │   └── service/   # Business logic
│   └── ...
└── README.md
```

## Features

- **Authentication**: Signup/Login with JWT, BCrypt password hashing
- **Multi-Tenant**: Organization-based data isolation
- **Product Management**: Full CRUD with search, SKU validation
- **Dashboard**: Real-time metrics, low-stock alerts
- **Settings**: Configurable low-stock thresholds per organization

## Getting Started

### Prerequisites
- Java 21
- Node.js 18+
- Maven 3.9+ (or use the included Maven wrapper)
- TiDB Cloud account (or any MySQL-compatible database)

### Environment Variables

#### Backend
| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `4000` |
| `DB_NAME` | Database name | `stockflow_db` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | (empty) |
| `JWT_SECRET` | JWT signing secret (min 64 chars) | dev default |
| `PORT` | Server port | `8080` |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:5173` |

#### Frontend
| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_URL` | Backend API URL | (empty, uses proxy) |

### Setup & Run

#### 1. Database
Create a database named `stockflow_db` on your TiDB Cloud instance (or local MySQL):
```sql
CREATE DATABASE IF NOT EXISTS stockflow_db;
```

#### 2. Backend
```bash
cd backend

# Set environment variables
export DB_HOST=your-tidb-host
export DB_PORT=4000
export DB_USERNAME=your-username
export DB_PASSWORD=your-password
export DB_NAME=stockflow_db
export JWT_SECRET=your-secure-64-char-minimum-secret-key-here-change-this-in-production!!

# Run with Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The backend starts on http://localhost:8080.

#### 3. Frontend
```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

The frontend starts on http://localhost:5173.

### Running Tests

```bash
cd backend
./mvnw test
# Tests use H2 in-memory database (no external DB needed)
```

### Building for Production

#### Backend
```bash
cd backend
./mvnw package -DskipTests
java -jar target/stockflow-backend-1.0.0.jar
```

#### Frontend
```bash
cd frontend
npm run build
# Output in dist/ directory
```

## Deployment

### Backend (Render)
1. Create a new **Web Service** on Render
2. Connect your repository
3. Set **Root Directory** to `backend`
4. Set **Build Command**: `./mvnw package -DskipTests -B`
5. Set **Start Command**: `java -jar target/stockflow-backend-1.0.0.jar`
6. Add environment variables: `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `DB_NAME`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`, `PORT`

Alternatively, use the included Dockerfile:
- Set **Docker Build Context** to `backend`
- Add the same environment variables

### Frontend (Vercel)
1. Create a new project on Vercel
2. Connect your repository
3. Set **Root Directory** to `frontend`
4. Set **Build Command**: `npm run build`
5. Set **Output Directory**: `dist`
6. Add environment variable: `VITE_API_URL=https://your-backend-url.onrender.com`

### TiDB Cloud TLS
For secure TLS connections to TiDB Cloud:
1. Download the CA certificate from TiDB Cloud console
2. Place it in a secure location on your server
3. The connection string uses `sslMode=VERIFY_IDENTITY` by default
4. For Render: set the `JAVA_TOOL_OPTIONS` env var if custom CA is needed

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user + organization |
| POST | `/api/auth/login` | Login and get JWT token |

### Products (authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List all products |
| GET | `/api/products?search=query` | Search products |
| GET | `/api/products/:id` | Get product by ID |
| POST | `/api/products` | Create product |
| PUT | `/api/products/:id` | Update product |
| DELETE | `/api/products/:id` | Delete product |

### Dashboard (authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard` | Get dashboard metrics |

### Settings (authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/settings` | Get organization settings |
| PUT | `/api/settings` | Update organization settings |

## Security
- Passwords hashed with BCrypt
- JWT tokens with 24-hour expiry
- Multi-tenant isolation enforced at the service layer
- CORS configured for production origins
- No credentials in source code or logs
- TLS for database connections
