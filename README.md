# Document Management System

## Use case
* User managment

## Quick Start with Docker Compose

1. **Clone the repository**
   ```bash
   git clone https://github.com/tomiella/DocumentManagementSystem.git
   cd DocumentManagmentSystem
   ```

2. **Start all services**
   ```bash
   docker compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost
   - REST API: http://localhost:8080
   - MinIO Console: http://localhost:9001
   - RabbitMQ Management: http://localhost:15672

## Default Credentials

**Frontend:**
- Username: `admin`
- Password: `admin`

**MinIO:**
- Username: `minioAdmin`
- Password: `minioPassword123`

**RabbitMQ:**
- Username: `guest`
- Password: `guest`

**PostgreSQL:**
- Database: `mydatabase`
- Username: `myuser`
- Password: `secret`