# Document Management System

A full-stack document management solution with OCR processing, full-text search, and AI-powered document summarization.

## Features

- **Document Upload & Storage** – Upload PDFs and images with secure storage in MinIO
- **OCR Processing** – Automatic text extraction from documents using Tesseract
- **AI Summarization** – Intelligent document summaries powered by Google Gemini
- **Full-Text Search** – Fast document search with Elasticsearch
- **Access Analytics** – Track document access statistics via XML batch processing
- **User Management** – Authentication and user administration

## Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Frontend  │────▶│   REST API  │────▶│  PostgreSQL │
│   (React)   │     │ (Spring Boot)│     │             │
└─────────────┘     └──────┬──────┘     └─────────────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
    ┌──────────┐    ┌──────────────┐  ┌───────────┐
    │  MinIO   │    │   RabbitMQ   │  │Elasticsearch│
    │ (Storage)│    │  (Messaging) │  │  (Search)  │
    └──────────┘    └──────┬───────┘  └───────────┘
                           │
          ┌────────────────┴────────────────┐
          ▼                                 ▼
    ┌─────────────┐                 ┌──────────────┐
    │  OCR Worker │                 │ Batch Service│
    │             │                 │ (Spring Boot)│
    └─────────────┘                 └──────────────┘
```

## Technology Stack

| Component       | Technology                          |
|-----------------|-------------------------------------|
| Frontend        | React, Vite, TailwindCSS            |
| Backend API     | Java 21, Spring Boot 3              |
| OCR Worker      | Tesseract, Google Gemini            |
| Batch Service   | Java, Spring Batch                  |
| Database        | PostgreSQL 16                       |
| Search Engine   | Elasticsearch 8.14                  |
| Message Broker  | RabbitMQ 3.13                       |
| Object Storage  | MinIO                               |
| Reverse Proxy   | Nginx                               |

## Quick Start with Docker Compose

### Prerequisites

- Docker & Docker Compose
- Google Gemini API key for AI summarization

### 1. Clone the repository

```bash
git clone https://github.com/tomiella/DocumentManagementSystem.git
cd DocumentManagmentSystem
```

### 2. Configure environment

Create a `.env` file (see `.env_example`):

```env
GEMINI_API_KEY=your_api_key_here
```

### 3. Start all services

```bash
docker compose up -d
```

### 4. Access the application

| Service              | URL                          |
|----------------------|------------------------------|
| Frontend             | http://localhost             |
| REST API             | http://localhost:8080        |
| MinIO Console        | http://localhost:9001        |
| RabbitMQ Management  | http://localhost:15672       |
| Kibana               | http://localhost:5601        |
| Elasticsearch        | http://localhost:9200        |

## Default Credentials

| Service     | Username       | Password            |
|-------------|----------------|---------------------|
| Frontend    | `admin`        | `admin`             |
| MinIO       | `minioAdmin`   | `minioPassword123`  |
| RabbitMQ    | `guest`        | `guest`             |
| PostgreSQL  | `myuser`       | `secret`            |

## API Endpoints

### Documents

| Method | Endpoint                  | Description              |
|--------|---------------------------|--------------------------|
| GET    | `/api/documents`          | List all documents       |
| GET    | `/api/documents/{id}`     | Get document by ID       |
| POST   | `/api/documents`          | Upload new document      |
| PUT    | `/api/documents/{id}`     | Update document metadata |
| DELETE | `/api/documents/{id}`     | Delete document          |

### Search

| Method | Endpoint                  | Description              |
|--------|---------------------------|--------------------------|
| GET    | `/api/search?q={query}`   | Full-text search         |

## Project Structure

```
DocumentManagementSystem/
├── rest/              # Spring Boot REST API
├── frontend/          # React frontend application
├── ocr-worker/        # OCR processing service
├── batch-service/     # Spring Batch for XML processing
├── compose.yaml       # Docker Compose configuration
└── README.md
```

## License

This project is developed as part of a university course at FH Technikum Wien.
