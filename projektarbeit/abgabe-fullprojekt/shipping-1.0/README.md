# Shipping Service

A Spring Boot application for handling shipping orders via JMS messaging with ActiveMQ.

## Prerequisites

- Java 25
- Maven
- Docker & Docker Compose

## Running Locally

### 1. Start ActiveMQ Server

```bash
docker compose up -d
```

The ActiveMQ web console will be available at: http://localhost:8161
- Username: `admin`
- Password: `admin`

### 2. Run the Application

```bash
mvn spring-boot:run
```

Or on Windows:
```bash
mvnw.cmd spring-boot:run
```

### 3. Stop ActiveMQ

```bash
docker compose down
```

## Configuration

The application connects to ActiveMQ via JMS on port `61616`. Configuration can be found in `src/main/resources/application.properties`.

