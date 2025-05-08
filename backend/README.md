# Network Analyzer (NAn)

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Quick Launch](#quick-launch)
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

- One-off scans (Wi-Fi + optional GPS if hardware is available)
- Continuous monitoring with JSON/CSV/KML outputs
- Local (file) or remote (MongoDB) storage
- Interactive shell commands
- REST endpoints + Spring Boot Actuator
- Async execution and scheduling

## Quick Launch

Follow these step-by-step instruction to quickly get NAn running on a Windows 11 machine.

**Backend**
1. Download the NAn project from git and extract to the desired directory.
2. Download IntelliJ if you do not already have it.
3. Open IntelliJ and navigate to "Plugins" on the left of the IntelliJ start up window.
4. At the top of the Plugins panel there are two tabs, "Marketplace" and "Installed"; click "Installed".
5. Scroll down slight to the "Build Tools" section and ensure both "Maven" and "Maven Extension" are active.
6. Scroll back to the top and within the "Downloaded" section, look for "Spring Shell".
7. If "Spring Shell" is listed, make sure it is activated by checking the box to it's left.
8. If "Spring Shell" is not listed, click the "Marketplace" tab at the top of the panel
9. Search for "Spring Shell", download and enable the plugin.
10. On the left side of the panel, at the top, click "Projects" to return to the main starting window of IntelliJ.
11. Click "Open" and navigate to the directory where you extracted the Github project.
12. Select the second "NAn-main" directory, and click "Select Folder".
13. **IMPORTANT:** When the project opens, there will be several popups in the bottom right corner.<br>
Be sure to click "Load" on the popup stating "Maven build script found".
14. Allow the build script to run, there will be several loading bars in the right corner that rapidly complete.

The backend is now setup and can be run via IntelliJ's built-in run button, using "Interactive Default".

**Frontend**
1. In Project panel on the left, navigate to the "frontend" directory, beneath "docs", and expand it.
2. Navigate to it's "src" directory and expand it.
3. Double click "App.vue".
4. A prompt to download Node.js will appear in the top right of IntelliJ. Click "Download Node.js".
5. A popup will appear, click "Download".
6. In the bottom left corner of IntelliJ, click the terminal icon arranged vertically among several others.
7. In the new terminal window, type ```cd ./frontend```, or navigate the terminal to the frontend directory another way.
8. Type ```npm install```; now you will be able to locally host an instance of the frontend.
9. Refer to the frontend README.md to launch the frontend.

## Prerequisites

- **Java JDK 23+**
- **Maven 3.6+**
- **OS**: Windows (full functionality), Linux (partial functionality)
- **Optional**: IDE, cURL/Postman, MongoDB

## Installation

1. **Clone the repository**
   ```bash  
   git clone https://github.com/Conava/NAn.git  
   cd NAn
   ```
2. **Verify Java & Maven**
Java and Maven versions should be 23+ and 3.6+ respectively.  
   ```bash  
   java -version  
   mvn -v
   ```
3. **Optional**: Import as Maven project in your IDE.

4. **Prerequisites**: 
    - **Linux**: 
      - Ensure you have `iw` and `gpsd` installed.
      - Enable sudo-less `iw` access:
        ```bash  
        sudo setcap cap_net_admin+ep $(which iw)
        ```
    - **Windows**: Ensure you have `netsh` and `gpsd` installed.

5. **MongoDB** (optional)
   - Install MongoDB locally or use a remote instance.
   - Ensure MongoDB is running if using local storage.
   - Settings file must be updated with your unique MongoDB uri and password if not using the testing uri.

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
  default-use-gps: false
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
    gps-on: false
    kml-output: false
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

