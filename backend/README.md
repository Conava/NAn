# Network Analyzer

A Spring Boot application for network analysis built with Java and Maven.

## Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Debugging](#debugging)
- [Deploying](#deploying)
- [API Usage](#api-usage)
- [Environment & Spring Variables](#environment--spring-variables)
- [Logging Configuration](#logging-configuration)
- [Dependencies](#dependencies)

## Overview
This project uses **Spring Boot** for the main application framework and integrates:
- **Spring Shell** for interactive command support.
- **Spring Boot Actuator** for providing health checks and bean inspection.
- **Logback** for customizable logging via a dedicated configuration file.

## Prerequisites
- **Java (JDK 11 or later)** — for building and running the project.
- **Maven** — for dependency management and build tasks.
- **Linux operating system** — tested on Linux-based environments.
- **IDE (optional)** — such as IntelliJ IDEA 2024.3.3, useful for debugging or code inspection.

## Installation
1. **Clone the repository**:
```bash
git clone https://github.com/your-username/your-repo.git
cd your-repo
```
2. Make sure you have the correct Java version and Maven installed. Verify them:
```bash
java -version
mvn -v
```
3. (Optional) Open the project in your IDE for advanced features.

## Running the Application
1. From the project root, run the application with Maven:
```bash
mvn spring-boot:run
```
2. By default, the service listens for HTTP requests on port **8080**.  
3. To see the interactive commands, type any recognized Spring Shell command in the same console.

## Testing
1. Unit and integration tests can be run with:
```bash
mvn test
```
2. Check the test reports in the `target/surefire-reports` directory.

## Debugging
- **In an IDE**: Configure a remote debugging port or run with the “Debug” configuration.  
- **In the terminal**: Attach the Java debugger by adding JVM flags:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

## Deploying
1. **Build a JAR**:
```bash
mvn clean package
```
2. The final JAR is located in `target/backend-0.0.1-SNAPSHOT.jar` (name may vary).  
3. Deploy or copy the JAR to your server, then run:
```bash
java -jar backend-0.0.1-SNAPSHOT.jar
```

## API Usage
- The application may expose REST endpoints under `/api` for network analysis.
- Use `curl` or your preferred tool to send requests:
```bash
curl http://localhost:8080/api/your-endpoint
```

## Environment & Spring Variables
- **Interactive Shell**: `spring.shell.interactive.enabled=true` in `application.properties`
- **Actuator Endpoints**: `management.endpoints.web.exposure.include=*` or fine-tune via properties
- **Server Port**: `server.port=8080`  
- **Log File**: `logging.file.name` points to the location of the Logback file output

## Logging Configuration
- Uses **Logback** as configured in `logback.xml` (or `logback-spring.xml`):
  - Rolling logs with a max size of 10MB (time and size based rollover).
  - Update `<pattern>...</pattern>` inside `<encoder>` for custom log formatting.

## Dependencies
- **Spring Boot** (Actuator, Web, Data REST, Security)
- **Spring Shell** (for interactive command-line usage)
- **Maven Compiler Plugin**
- **Lombok** (for boilerplate reduction)
- **Logback** (for rolling file logging)
