# Network Analyzer (NAn)

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Interactive Shell](#interactive-shell)
- [API Usage](#api-usage)
- [Testing](#testing)
- [Debugging](#debugging)
- [Deploying](#deploying)
- [Logging](#logging)
- [Dependencies](#dependencies)

## Overview

**Network Analyzer (NAn)** is a Spring Boot application for Wi-Fi and GPS scanning, continuous monitoring, data export,
and analysis. It offers:

- A **RESTful API**
- An **interactive shell**
- Configurable settings via YAML

## Features

- One-off scans (Wi-Fi + optional GPS)
- Continuous monitoring with JSON/CSV/KML outputs
- Local (file) or remote (MongoDB) storage
- Interactive shell commands
- REST endpoints + Spring Boot Actuator
- Async execution and scheduling

## Prerequisites

- **Java JDK 23+**
- **Maven 3.6+**
- **OS**: Windows, Linux, or macOS
- **Optional**: IDE, cURL/Postman, MongoDB

## Installation

1. **Clone the repository**
   ```bash  
   git clone https://github.com/Conava/NAn.git  
   cd NAn
   ```
2. **Verify Java & Maven**
   ```bash  
   java -version  
   mvn -v
   ```
3. **Optional**: Import as Maven project in your IDE.

## Configuration

Default user config location:

- **Linux/macOS**: `~/.config/NAn/settings.yml`
- **Windows**: `%LOCALAPPDATA%\NAn\settings.yml`

Default `application.yml` (classpath:/config/):

```yaml
app:
  # ─── Base directory for ALL files ───────────────────────────────────────
  # On Windows this picks up LOCALAPPDATA (e.g. C:\Users\Name\AppData\Local)
  # On Linux/macOS it falls back to ~/.config
  base-dir: ${LOCALAPPDATA:${user.home}/.config}/NAn

  # ─── General settings ──────────────────────────────────────────────────
  data-storage: ${app.base-dir}/data
  default-use-gps: true
  keep-history: true
  activate-gui: true
  log-file: ${app.base-dir}/log.txt

  # ─── Database settings ─────────────────────────────────────────────────
  db:
    remote-enabled: false
    remote-url: mongodb://localhost

  # ─── Monitoring defaults ───────────────────────────────────────────────
  monitor:
    scan-interval: 60        # seconds
    gps-on: true
    kml-output: true
    csv-output: false

    # filenames only (your code can prefix them with data-storage or base-dir)
    json-file-name: monitorJson
    kml-file-name: monitorKML
    csv-file-name: monitorCSV

spring:
  config:
    name: settings
    additional-location: "optional:file:${app.base-dir}/"
logging:
  file:
    name: ${app.log-file}
```

To override, copy this into your user config directory, edit values, then restart.

## Running the Application

```bash  
mvn spring-boot:run  
```

- HTTP port: `8080` (configurable via `server.port`)
- Interactive shell appears automatically—type `help`.

## Interactive Shell

- `shutdown|exit|quit|stop` — terminate
- `monitor start` / `monitor stop`
- `scan run-default`
- `scan run [--scanInterval X] [--gpsOn true] …`
- `monitor export [fileName]`
- `settings show`
- `settings update [--dataStorage path] [--monitor.scanInterval X] …`

## API Usage

All endpoints under `/api`. Example:

```bash  
curl -X POST http://localhost:8080/api/monitor/start  
```

### Example: Start Monitoring

**Request**

```bash
curl -X POST http://localhost:8080/api/monitor/start
```

| Endpoint                    | Method | Description                          |
|-----------------------------|--------|--------------------------------------|
| `/api/monitor/start`        | POST   | Begin continuous monitoring          |
| `/api/monitor/stop`         | POST   | Stop continuous monitoring           |
| `/api/scan`                 | GET    | Single scan (default settings)       |
| `/api/scan`                 | POST   | Single scan (override settings)      |
| `/api/monitor/data`         | GET    | Retrieve collected monitoring data   |
| `/api/monitor/export`       | GET    | Export monitoring data to JSON file  |
| `/api/state`                | GET    | Get application state                |
| `/api/application/shutdown` | POST   | Gracefully shut down the application |
| `/api/settings`             | GET    | Get current settings                 |
| `/api/settings`             | POST   | Update all settings                  |

## Testing

```bash  
mvn test  
```  

Reports: `target/surefire-reports/`

## Debugging

- **IDE**: Run in Debug mode.
- **Remote debug**:
  ```bash  
  mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"  
  ```

## Deploying

1. **Build JAR**:
   ```bash  
   mvn clean package  
   ```
2. **Run**:
   ```bash  
   java -jar target/NAn-<version>.jar  
   ```

## Logging

Uses Logback configured in `logback-spring.xml`:

## Dependencies

- Spring Boot (Web, Actuator, Async, Scheduling)
- Spring Shell
- Lombok
- Logback
- JUnit 5 + Mockito
- MongoDB Java Driver (optional)

