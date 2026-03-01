# Spring Boot Documentation for Otoch Backend

This document provides a comprehensive explanation of Spring Boot and all Spring-related code, patterns, and build processes in this project.

---

## Table of Contents

1. [What is Spring Boot?](#what-is-spring-boot)
2. [Project Structure](#project-structure)
3. [Local Development Setup (Ubuntu)](#local-development-setup-ubuntu)
4. [Build Process (Maven)](#build-process-maven)
5. [Dependencies Explained](#dependencies-explained)
6. [Application Entry Point](#application-entry-point)
7. [Architectural Patterns](#architectural-patterns)
8. [Code Structure and Layers](#code-structure-and-layers)
9. [Annotations Reference](#annotations-reference)
10. [Configuration](#configuration)
11. [Testing](#testing)
12. [Common Maven Commands](#common-maven-commands)

---

## What is Spring Boot?

**Spring Boot** is an opinionated framework built on top of the **Spring Framework** that simplifies the creation of production-ready applications. It eliminates most of the boilerplate configuration required by traditional Spring applications.

### Spring vs Spring Boot

| Aspect | Spring Framework | Spring Boot |
|--------|-----------------|-------------|
| **Configuration** | Extensive XML or Java config required | Auto-configuration with sensible defaults |
| **Server** | External application server needed | Embedded server (Tomcat, Jetty, Undertow) |
| **Dependencies** | Manual dependency management | Starter dependencies bundle related libraries |
| **Setup time** | Hours to days | Minutes |
| **Deployment** | WAR file to app server | Executable JAR ("fat JAR") |

### Key Features of Spring Boot

| Feature | Description |
|---------|-------------|
| **Auto-configuration** | Automatically configures beans based on classpath and properties |
| **Starter dependencies** | Pre-configured dependency bundles (e.g., `spring-boot-starter-web`) |
| **Embedded server** | Tomcat/Jetty/Undertow included—no external server needed |
| **Production-ready** | Health checks, metrics, externalized config out of the box |
| **Opinionated defaults** | Sensible defaults that can be overridden |
| **No code generation** | No XML configuration required |

### How Spring Boot Works

1. **Classpath scanning** – Spring scans packages for annotated classes (`@Component`, `@Service`, `@Controller`, etc.)
2. **Auto-configuration** – Based on dependencies on the classpath, Spring Boot automatically configures beans (e.g., if `spring-boot-starter-web` is present, it configures an embedded Tomcat and Spring MVC)
3. **Property binding** – Configuration from `application.properties` or environment variables is bound to beans
4. **Dependency injection** – Spring creates and wires beans together automatically

---

## Project Structure

```
otoch_backend/
├── pom.xml                                    # Maven build configuration
├── src/
│   ├── main/
│   │   ├── java/com/otoch/
│   │   │   ├── OtochApplication.java          # Application entry point
│   │   │   ├── controller/                    # REST API layer
│   │   │   │   ├── ItemController.java        # CRUD endpoints for items
│   │   │   │   └── HealthController.java      # Health check endpoint
│   │   │   ├── model/                         # Data models / DTOs
│   │   │   │   └── Item.java                  # Item entity
│   │   │   └── service/                       # Business logic layer
│   │   │       └── ItemService.java           # Item operations
│   │   └── resources/
│   │       └── application.properties         # Application configuration
│   └── test/
│       └── java/com/otoch/
│           ├── OtochApplicationTests.java     # Application context test
│           └── controller/
│               └── ItemControllerTests.java   # Controller unit tests
├── Dockerfile                                 # Docker build configuration
├── docker-compose.yml                         # Docker Compose configuration
└── README.md                                  # Project documentation
```

### Package Naming Convention

| Package | Purpose |
|---------|---------|
| `com.otoch` | Root package (must contain the main application class) |
| `com.otoch.controller` | REST controllers (HTTP request handling) |
| `com.otoch.service` | Business logic and service layer |
| `com.otoch.model` | Data models, entities, DTOs |
| `com.otoch.repository` | Data access layer (not used yet—would contain database repositories) |
| `com.otoch.config` | Configuration classes (not used yet) |

**Important:** The main application class (`OtochApplication.java`) must be in the root package (`com.otoch`) so that `@SpringBootApplication` can scan all sub-packages.

---

## Local Development Setup (Ubuntu)

You can compile, test, and run the application locally on Ubuntu without using Docker. This requires **Java JDK 17** and **Maven** installed on your machine.

### Prerequisites

#### Option 1: Using apt (Ubuntu packages)

```bash
# Update package index
sudo apt update

# Install OpenJDK 17
sudo apt install openjdk-17-jdk

# Install Maven
sudo apt install maven

# Verify installations
java -version    # Should show: openjdk version "17.x.x"
mvn -version     # Should show: Apache Maven 3.x.x
```

#### Option 2: Using SDKMAN (recommended for managing multiple Java versions)

[SDKMAN](https://sdkman.io/) is a tool for managing parallel versions of multiple SDKs. It's useful if you work on projects requiring different Java versions.

```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash

# Open a new terminal or run:
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 17 (Eclipse Temurin distribution)
sdk install java 17.0.10-tem

# Install Maven
sdk install maven

# Verify installations
java -version
mvn -version
```

**SDKMAN advantages:**

- Install multiple Java versions side-by-side
- Switch versions per project or globally
- No sudo required
- Easy updates

### Compile and Test Locally

Navigate to the project directory and run Maven commands:

```bash
cd /home/adndark/projects/otoch/otoch_backend

# Compile the source code only
mvn compile

# Compile and run all tests
mvn test

# Compile, test, and package into executable JAR
mvn package

# Clean previous builds, then compile and package
mvn clean package

# Package without running tests (faster)
mvn package -DskipTests
```

### Run the Application Locally

```bash
# Option 1: Run via Maven (compiles automatically if needed)
mvn spring-boot:run

# Option 2: Run the packaged JAR (requires mvn package first)
java -jar target/otoch-backend-0.0.1-SNAPSHOT.jar

# Run with a specific port
java -jar target/otoch-backend-0.0.1-SNAPSHOT.jar --server.port=9090

# Run with a specific profile
java -jar target/otoch-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

The application starts on `http://localhost:8080` by default.

### Verify the Application is Running

Open another terminal and test the endpoints:

```bash
# Health check
curl http://localhost:8080/api/health

# Get all items
curl http://localhost:8080/api/items

# Create a new item
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Item", "description": "Created locally", "price": 9.99}'
```

### Quick Reference: Local Development Commands

| Task | Command |
|------|---------|
| Install Java (apt) | `sudo apt install openjdk-17-jdk` |
| Install Maven (apt) | `sudo apt install maven` |
| Compile source | `mvn compile` |
| Run tests | `mvn test` |
| Run single test class | `mvn test -Dtest=ItemControllerTests` |
| Package JAR | `mvn package` |
| Package (skip tests) | `mvn package -DskipTests` |
| Clean and package | `mvn clean package` |
| Run via Maven | `mvn spring-boot:run` |
| Run JAR | `java -jar target/otoch-backend-0.0.1-SNAPSHOT.jar` |
| Stop application | `Ctrl+C` |

### Local vs Docker: When to Use Which

| Scenario | Recommended Approach |
|----------|---------------------|
| Quick code changes and testing | Local (`mvn spring-boot:run`) |
| Running tests during development | Local (`mvn test`) |
| IDE debugging | Local (run from IDE) |
| Ensuring reproducible builds | Docker (`docker-compose up --build`) |
| Testing production-like environment | Docker |
| Deploying to servers | Docker |
| CI/CD pipelines | Docker |

### Troubleshooting Local Setup

**Java not found after installation:**

```bash
# Check if JAVA_HOME is set
echo $JAVA_HOME

# If not set, add to ~/.bashrc:
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Reload shell
source ~/.bashrc
```

**Maven using wrong Java version:**

```bash
# Check which Java Maven is using
mvn -version

# Force Maven to use specific Java
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
mvn clean package
```

**Port 8080 already in use:**

```bash
# Find what's using port 8080
sudo lsof -i :8080

# Kill the process (replace PID with actual process ID)
kill -9 <PID>

# Or run on a different port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

---

## Build Process (Maven)

This project uses **Apache Maven** as the build tool. Maven handles dependency management, compilation, testing, and packaging.

### What is Maven?

Maven is a build automation and project management tool for Java projects. It uses a **Project Object Model** (`pom.xml`) to describe the project configuration, dependencies, and build process.

### The pom.xml File

The `pom.xml` is the heart of a Maven project. Here's our configuration explained:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Parent POM from Spring Boot -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.2</version>
        <relativePath/>
    </parent>

    <!-- Project coordinates -->
    <groupId>com.otoch</groupId>
    <artifactId>otoch-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>otoch-backend</name>
    <description>Otoch Backend RESTful Service</description>

    <!-- Properties -->
    <properties>
        <java.version>17</java.version>
    </properties>

    <!-- Dependencies -->
    <dependencies>
        <!-- ... dependencies listed below ... -->
    </dependencies>

    <!-- Build configuration -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### POM Elements Explained

| Element | Value | Purpose |
|---------|-------|---------|
| `<parent>` | `spring-boot-starter-parent:3.2.2` | Inherits dependency versions, plugin configs, and defaults from Spring Boot's parent POM. You don't need to specify versions for most Spring dependencies. |
| `<groupId>` | `com.otoch` | Organization/project identifier (like a namespace) |
| `<artifactId>` | `otoch-backend` | Project name (becomes the JAR filename) |
| `<version>` | `0.0.1-SNAPSHOT` | Project version. `SNAPSHOT` means it's a development version. |
| `<java.version>` | `17` | Tells Maven and Spring Boot to compile for Java 17 |
| `spring-boot-maven-plugin` | - | Creates executable JAR, runs the app, and provides other Spring Boot-specific goals |

### Maven Build Lifecycle

Maven has a defined build lifecycle with phases that execute in order:

```
validate → compile → test → package → verify → install → deploy
```

| Phase | What Happens |
|-------|--------------|
| `validate` | Validates the project is correct and all info is available |
| `compile` | Compiles the source code (`src/main/java` → `target/classes`) |
| `test` | Runs unit tests using a testing framework (JUnit) |
| `package` | Packages compiled code into a JAR/WAR (`target/*.jar`) |
| `verify` | Runs integration tests and checks |
| `install` | Installs the JAR to local Maven repository (`~/.m2/repository`) |
| `deploy` | Deploys to a remote repository (Nexus, Artifactory) |

### What `mvn package` Does

When you run `mvn package`, Maven:

1. **Resolves dependencies** – Downloads all dependencies from Maven Central (or other repos) to `~/.m2/repository`
2. **Compiles source code** – `src/main/java/**/*.java` → `target/classes/**/*.class`
3. **Processes resources** – Copies `src/main/resources/*` to `target/classes/`
4. **Compiles test code** – `src/test/java/**/*.java` → `target/test-classes/**/*.class`
5. **Runs tests** – Executes all test classes (unless `-DskipTests`)
6. **Packages the JAR** – Creates `target/otoch-backend-0.0.1-SNAPSHOT.jar`

### The Executable JAR ("Fat JAR")

The `spring-boot-maven-plugin` creates a special **executable JAR** (also called a "fat JAR" or "uber JAR"):

- Contains your compiled code
- Contains all dependencies (embedded inside the JAR)
- Contains an embedded Tomcat server
- Can be run with `java -jar app.jar`

**JAR structure:**

```
otoch-backend-0.0.1-SNAPSHOT.jar
├── BOOT-INF/
│   ├── classes/           # Your compiled code
│   │   ├── com/otoch/*.class
│   │   └── application.properties
│   └── lib/               # All dependency JARs
│       ├── spring-boot-3.2.2.jar
│       ├── spring-web-6.1.3.jar
│       ├── tomcat-embed-core-10.1.18.jar
│       └── ... (many more)
├── META-INF/
│   └── MANIFEST.MF        # JAR metadata, main class reference
└── org/springframework/boot/loader/  # Spring Boot's JAR loader
```

---

## Dependencies Explained

Spring Boot uses **starter dependencies**—curated bundles that pull in related libraries with compatible versions.

### Project Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Dependency Breakdown

#### 1. spring-boot-starter-web

**Purpose:** Build web applications, including RESTful services.

**What it includes:**

| Library | Purpose |
|---------|---------|
| Spring MVC | Web framework for handling HTTP requests |
| Spring Web | Core web functionality |
| Embedded Tomcat | Web server (no external server needed) |
| Jackson | JSON serialization/deserialization |
| Hibernate Validator | Bean validation implementation |

**What it enables:**

- `@RestController`, `@RequestMapping`, `@GetMapping`, etc.
- Automatic JSON conversion for request/response bodies
- Embedded web server on port 8080

#### 2. spring-boot-starter-validation

**Purpose:** Bean validation using Jakarta Validation (formerly javax.validation).

**What it includes:**

| Library | Purpose |
|---------|---------|
| Hibernate Validator | Reference implementation of Bean Validation |
| Jakarta Validation API | Validation annotations (`@NotBlank`, `@Positive`, etc.) |

**What it enables:**

- `@Valid` annotation to trigger validation on request bodies
- Validation annotations on model fields: `@NotBlank`, `@NotNull`, `@Positive`, `@Size`, `@Email`, etc.
- Automatic 400 Bad Request responses for validation failures

#### 3. spring-boot-starter-actuator

**Purpose:** Production-ready features for monitoring and managing the application.

**What it includes:**

| Feature | Purpose |
|---------|---------|
| Health endpoint | `/actuator/health` – Is the app running? |
| Info endpoint | `/actuator/info` – Application metadata |
| Metrics | `/actuator/metrics` – JVM, HTTP, and custom metrics |
| Environment | `/actuator/env` – Environment properties |

**What it enables:**

- Health checks for container orchestration (Kubernetes, Docker)
- Application monitoring and observability
- Integration with monitoring tools (Prometheus, Grafana)

#### 4. spring-boot-starter-test

**Purpose:** Testing support with common testing libraries.

**Scope:** `test` – Only available during testing, not included in the final JAR.

**What it includes:**

| Library | Purpose |
|---------|---------|
| JUnit 5 | Testing framework |
| Spring Test | Spring testing utilities |
| Mockito | Mocking framework |
| AssertJ | Fluent assertions |
| JSONPath | JSON assertions |
| MockMvc | Test Spring MVC controllers without a server |

### Why No Version Numbers?

Notice that dependencies don't specify versions:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- No <version> tag! -->
</dependency>
```

This is because the `spring-boot-starter-parent` POM defines a **BOM** (Bill of Materials) with compatible versions for all Spring Boot dependencies. This ensures all libraries work together without version conflicts.

---

## Application Entry Point

The main application class bootstraps the entire Spring Boot application.

### OtochApplication.java

```java
package com.otoch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OtochApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtochApplication.class, args);
    }
}
```

### How It Works

| Element | Explanation |
|---------|-------------|
| `@SpringBootApplication` | Meta-annotation combining three annotations (see below) |
| `SpringApplication.run(...)` | Bootstraps the application: creates ApplicationContext, starts embedded server, scans for components |
| `OtochApplication.class` | Tells Spring which class is the entry point |
| `String[] args` | Command-line arguments passed to the application |

### @SpringBootApplication Breakdown

`@SpringBootApplication` is equivalent to:

```java
@Configuration           // This class can define @Bean methods
@EnableAutoConfiguration // Enable Spring Boot's auto-configuration
@ComponentScan           // Scan this package and sub-packages for components
```

| Annotation | Purpose |
|------------|---------|
| `@Configuration` | Marks this class as a source of bean definitions |
| `@EnableAutoConfiguration` | Tells Spring Boot to automatically configure beans based on classpath dependencies |
| `@ComponentScan` | Scans `com.otoch` and all sub-packages for `@Component`, `@Service`, `@Controller`, `@Repository` |

### Startup Sequence

When `SpringApplication.run()` is called:

1. **Create ApplicationContext** – The IoC container that holds all beans
2. **Component scanning** – Find all classes with stereotype annotations
3. **Auto-configuration** – Configure beans based on classpath (e.g., configure Tomcat because `spring-boot-starter-web` is present)
4. **Bean creation** – Instantiate and wire all beans via dependency injection
5. **Start embedded server** – Start Tomcat on configured port (default 8080)
6. **Application ready** – Log "Started OtochApplication in X seconds"

---

## Architectural Patterns

This project follows well-established architectural patterns for maintainable, testable code.

### 1. Layered Architecture

The code is organized into horizontal layers, each with a specific responsibility:

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT (Browser, curl, etc.)            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼ HTTP Request
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                         │
│  - Handles HTTP requests/responses                          │
│  - Input validation                                         │
│  - Delegates to Service layer                               │
│  - @RestController, @RequestMapping                         │
│  Files: ItemController.java, HealthController.java          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼ Method Call
┌─────────────────────────────────────────────────────────────┐
│                     SERVICE LAYER                           │
│  - Business logic                                           │
│  - Transaction management                                   │
│  - Orchestrates data access                                 │
│  - @Service                                                 │
│  Files: ItemService.java                                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼ Method Call
┌─────────────────────────────────────────────────────────────┐
│                   REPOSITORY LAYER                          │
│  - Data access (CRUD operations)                            │
│  - Database queries                                         │
│  - @Repository                                              │
│  (Not implemented yet - using in-memory Map)                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE                               │
│  (Not implemented yet - using ConcurrentHashMap)            │
└─────────────────────────────────────────────────────────────┘
```

### Benefits of Layered Architecture

| Benefit | Description |
|---------|-------------|
| **Separation of Concerns** | Each layer has one responsibility |
| **Testability** | Layers can be tested in isolation (mock dependencies) |
| **Maintainability** | Changes in one layer don't affect others |
| **Reusability** | Services can be used by multiple controllers |
| **Flexibility** | Easy to swap implementations (e.g., change database) |

### 2. MVC Pattern (Model-View-Controller)

Spring MVC implements the MVC pattern for web applications:

| Component | In This Project | Responsibility |
|-----------|-----------------|----------------|
| **Model** | `Item.java` | Data representation |
| **View** | JSON responses | Presentation (REST APIs return JSON, not HTML) |
| **Controller** | `ItemController.java` | Handles requests, orchestrates response |

### 3. Dependency Injection (DI)

Spring manages object creation and wiring through **Dependency Injection**:

```java
@RestController
public class ItemController {

    private final ItemService itemService;  // Dependency

    // Constructor injection - Spring provides ItemService instance
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
}
```

**How it works:**

1. Spring scans for `@Service` classes and creates instances (beans)
2. When creating `ItemController`, Spring sees it needs an `ItemService`
3. Spring injects the `ItemService` bean into the constructor
4. The controller never creates `new ItemService()` itself

**Benefits:**

- **Loose coupling** – Classes depend on interfaces, not implementations
- **Testability** – Easy to inject mocks for testing
- **Single source of truth** – One instance shared across the application

### 4. RESTful API Design

The API follows REST principles:

| Principle | Implementation |
|-----------|----------------|
| **Resource-based URLs** | `/api/items` represents the items resource |
| **HTTP methods for actions** | GET (read), POST (create), PUT (update), DELETE (delete) |
| **Stateless** | Each request contains all information needed |
| **Standard status codes** | 200 OK, 201 Created, 204 No Content, 404 Not Found |
| **JSON representation** | Request/response bodies are JSON |

**REST endpoints in this project:**

| Method | URL | Action | Response |
|--------|-----|--------|----------|
| GET | `/api/items` | List all items | 200 + JSON array |
| GET | `/api/items/{id}` | Get one item | 200 + JSON object or 404 |
| POST | `/api/items` | Create item | 201 + created JSON |
| PUT | `/api/items/{id}` | Update item | 200 + updated JSON or 404 |
| DELETE | `/api/items/{id}` | Delete item | 204 No Content or 404 |

---

## Code Structure and Layers

### Controller Layer

Controllers handle HTTP requests and return responses. They should be thin—delegating business logic to services.

#### ItemController.java

```java
@RestController                              // Combines @Controller + @ResponseBody
@RequestMapping("/api/items")                // Base URL path
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {  // Constructor injection
        this.itemService = itemService;
    }

    @GetMapping                              // GET /api/items
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")                     // GET /api/items/123
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)     // If found: 200 OK
                .orElse(ResponseEntity.notFound().build());  // If not: 404
    }

    @PostMapping                             // POST /api/items
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item created = itemService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // 201
    }

    @PutMapping("/{id}")                     // PUT /api/items/123
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody Item item) {
        return itemService.updateItem(id, item)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")                  // DELETE /api/items/123
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemService.deleteItem(id)) {
            return ResponseEntity.noContent().build();  // 204
        }
        return ResponseEntity.notFound().build();  // 404
    }
}
```

**Key concepts:**

| Concept | Explanation |
|---------|-------------|
| `@RestController` | Marks class as a REST controller; methods return data (not view names) |
| `@RequestMapping` | Sets the base URL path for all methods in this controller |
| `@GetMapping`, `@PostMapping`, etc. | Maps HTTP methods to handler methods |
| `@PathVariable` | Extracts values from URL path (`/items/{id}` → `id`) |
| `@RequestBody` | Deserializes JSON request body into Java object |
| `@Valid` | Triggers bean validation on the request body |
| `ResponseEntity<T>` | Allows setting HTTP status, headers, and body |

### Service Layer

Services contain business logic. They're transactional boundaries and coordinate between controllers and repositories.

#### ItemService.java

```java
@Service                                     // Marks as a Spring-managed service bean
public class ItemService {

    // In-memory storage (would be a Repository in a real app)
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ItemService() {
        // Seed with sample data
        Item sample1 = new Item(idGenerator.getAndIncrement(), 
                "Sample Item 1", "A sample item for testing", 19.99);
        Item sample2 = new Item(idGenerator.getAndIncrement(), 
                "Sample Item 2", "Another sample item", 29.99);
        items.put(sample1.getId(), sample1);
        items.put(sample2.getId(), sample2);
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Item createItem(Item item) {
        item.setId(idGenerator.getAndIncrement());
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> updateItem(Long id, Item updatedItem) {
        if (!items.containsKey(id)) {
            return Optional.empty();
        }
        updatedItem.setId(id);
        items.put(id, updatedItem);
        return Optional.of(updatedItem);
    }

    public boolean deleteItem(Long id) {
        return items.remove(id) != null;
    }
}
```

**Key concepts:**

| Concept | Explanation |
|---------|-------------|
| `@Service` | Stereotype annotation; Spring creates a singleton bean |
| `ConcurrentHashMap` | Thread-safe in-memory storage (simulates a database) |
| `AtomicLong` | Thread-safe ID generator |
| `Optional<T>` | Indicates a value may or may not be present (avoids null) |

**In a real application:** The service would inject a `@Repository` (e.g., JPA repository) instead of using an in-memory Map.

### Model Layer

Models represent data structures. They may include validation constraints.

#### Item.java

```java
public class Item {

    private Long id;

    @NotBlank(message = "Name is required")          // Validation: not null/empty
    private String name;

    private String description;

    @Positive(message = "Price must be positive")    // Validation: > 0
    private Double price;

    public Item() {                                  // No-arg constructor for Jackson
    }

    public Item(Long id, String name, String description, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters and setters (required for Jackson JSON binding)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... other getters/setters
}
```

**Key concepts:**

| Concept | Explanation |
|---------|-------------|
| No-arg constructor | Required by Jackson for JSON deserialization |
| Getters/setters | Required by Jackson for JSON serialization/deserialization |
| `@NotBlank` | Validates the field is not null and not empty (whitespace-only fails) |
| `@Positive` | Validates the number is greater than 0 |
| `message` | Custom error message returned in validation errors |

**Available validation annotations:**

| Annotation | Purpose |
|------------|---------|
| `@NotNull` | Must not be null |
| `@NotBlank` | Must not be null, empty, or whitespace-only (strings) |
| `@NotEmpty` | Must not be null or empty (strings, collections) |
| `@Size(min=, max=)` | String/collection size constraints |
| `@Min(value)` | Minimum numeric value |
| `@Max(value)` | Maximum numeric value |
| `@Positive` | Must be > 0 |
| `@PositiveOrZero` | Must be >= 0 |
| `@Email` | Must be valid email format |
| `@Pattern(regexp=)` | Must match regex pattern |

---

## Annotations Reference

### Stereotype Annotations (Component Scanning)

These annotations mark classes as Spring-managed beans:

| Annotation | Purpose | Use Case |
|------------|---------|----------|
| `@Component` | Generic Spring bean | General-purpose components |
| `@Service` | Service layer bean | Business logic classes |
| `@Repository` | Data access bean | Database access classes |
| `@Controller` | Web controller (returns views) | Traditional MVC controllers |
| `@RestController` | REST API controller | `@Controller` + `@ResponseBody` |
| `@Configuration` | Configuration class | Classes with `@Bean` methods |

### Web/REST Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@RequestMapping` | Map URL to class/method | `@RequestMapping("/api")` |
| `@GetMapping` | Handle GET requests | `@GetMapping("/items")` |
| `@PostMapping` | Handle POST requests | `@PostMapping("/items")` |
| `@PutMapping` | Handle PUT requests | `@PutMapping("/items/{id}")` |
| `@DeleteMapping` | Handle DELETE requests | `@DeleteMapping("/items/{id}")` |
| `@PatchMapping` | Handle PATCH requests | `@PatchMapping("/items/{id}")` |
| `@PathVariable` | Extract URL path variable | `@PathVariable Long id` |
| `@RequestParam` | Extract query parameter | `@RequestParam String name` |
| `@RequestBody` | Deserialize request body | `@RequestBody Item item` |
| `@ResponseBody` | Serialize return value | (Implicit in `@RestController`) |
| `@ResponseStatus` | Set response status | `@ResponseStatus(HttpStatus.CREATED)` |

### Dependency Injection Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Autowired` | Inject dependency (field/method) | `@Autowired ItemService service` |
| Constructor injection | Inject via constructor (preferred) | `public Controller(Service s)` |
| `@Qualifier` | Specify which bean to inject | `@Qualifier("specificBean")` |
| `@Value` | Inject property value | `@Value("${server.port}")` |

### Validation Annotations

| Annotation | Purpose |
|------------|---------|
| `@Valid` | Trigger validation on parameter |
| `@NotNull` | Field must not be null |
| `@NotBlank` | String must not be null/empty/whitespace |
| `@NotEmpty` | Collection/String must not be null/empty |
| `@Size` | Size constraints for strings/collections |
| `@Min`, `@Max` | Numeric range constraints |
| `@Positive`, `@Negative` | Sign constraints |
| `@Email` | Valid email format |
| `@Pattern` | Regex pattern match |

---

## Configuration

### application.properties

Spring Boot uses `application.properties` (or `application.yml`) for configuration:

```properties
# Server Configuration
server.port=8080

# Application Info
spring.application.name=otoch-backend

# Actuator Endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

### Configuration Properties Explained

| Property | Value | Purpose |
|----------|-------|---------|
| `server.port` | `8080` | Port the embedded Tomcat listens on |
| `spring.application.name` | `otoch-backend` | Application name (used in logs, actuator) |
| `management.endpoints.web.exposure.include` | `health,info` | Which actuator endpoints to expose over HTTP |
| `management.endpoint.health.show-details` | `always` | Show detailed health info (components, status) |

### Common Configuration Properties

| Property | Description |
|----------|-------------|
| `server.port` | Server port (default: 8080) |
| `server.servlet.context-path` | URL prefix for all endpoints (e.g., `/api`) |
| `spring.profiles.active` | Active profile(s) (e.g., `dev`, `prod`) |
| `logging.level.root` | Root logging level (DEBUG, INFO, WARN, ERROR) |
| `logging.level.com.otoch` | Package-specific logging level |

### Environment Variable Override

Any property can be overridden via environment variables:

```bash
# Property: server.port=8080
# Environment variable (dots → underscores, uppercase):
export SERVER_PORT=9090

# Property: spring.application.name=otoch-backend
export SPRING_APPLICATION_NAME=my-app
```

### Profiles

Spring Boot supports profiles for environment-specific configuration:

```
application.properties         # Default config
application-dev.properties     # Dev-specific (activated with --spring.profiles.active=dev)
application-prod.properties    # Prod-specific
```

Activate a profile:

```bash
# Command line
java -jar app.jar --spring.profiles.active=prod

# Environment variable
export SPRING_PROFILES_ACTIVE=prod

# Docker Compose (as in our docker-compose.yml)
environment:
  - SPRING_PROFILES_ACTIVE=default
```

---

## Testing

Spring Boot provides comprehensive testing support via `spring-boot-starter-test`.

### Test Types

| Test Type | Annotation | Purpose |
|-----------|------------|---------|
| Unit test | `@Test` | Test individual classes in isolation |
| Slice test | `@WebMvcTest` | Test only web layer (controllers) |
| Integration test | `@SpringBootTest` | Test full application context |

### ItemControllerTests.java Explained

```java
@WebMvcTest(ItemController.class)           // Load only web layer for ItemController
class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;                // Simulates HTTP requests

    @MockBean                               // Create mock and register as bean
    private ItemService itemService;

    @Test
    void getAllItems_returnsItemsList() throws Exception {
        // Arrange: Set up mock behavior
        Item item = new Item(1L, "Test Item", "Description", 10.0);
        when(itemService.getAllItems()).thenReturn(List.of(item));

        // Act & Assert: Perform request and verify response
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void getItemById_nonExistingId_returnsNotFound() throws Exception {
        when(itemService.getItemById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItem_validItem_returnsCreated() throws Exception {
        Item item = new Item(1L, "New Item", "Description", 25.0);
        when(itemService.createItem(any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Item\", \"description\": \"Description\", \"price\": 25.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Item"));
    }
}
```

### Testing Annotations

| Annotation | Purpose |
|------------|---------|
| `@WebMvcTest(Controller.class)` | Loads only web layer; faster than full context |
| `@MockBean` | Creates a Mockito mock and registers it as a Spring bean |
| `@Autowired MockMvc` | Provides a mock MVC environment for testing controllers |
| `@SpringBootTest` | Loads full application context |
| `@Test` | Marks a method as a test case |

### MockMvc Methods

| Method | Purpose |
|--------|---------|
| `mockMvc.perform(get("/url"))` | Simulate a GET request |
| `mockMvc.perform(post("/url"))` | Simulate a POST request |
| `.contentType(MediaType.APPLICATION_JSON)` | Set Content-Type header |
| `.content("{json}")` | Set request body |
| `.andExpect(status().isOk())` | Assert HTTP 200 |
| `.andExpect(status().isCreated())` | Assert HTTP 201 |
| `.andExpect(status().isNotFound())` | Assert HTTP 404 |
| `.andExpect(jsonPath("$.field").value(x))` | Assert JSON field value |

---

## Common Maven Commands

### Building

```bash
# Compile source code
mvn compile

# Compile and run tests
mvn test

# Compile, test, and package into JAR
mvn package

# Clean build artifacts and rebuild
mvn clean package

# Package without running tests
mvn package -DskipTests
```

### Running

```bash
# Run the application via Maven
mvn spring-boot:run

# Run the packaged JAR
java -jar target/otoch-backend-0.0.1-SNAPSHOT.jar

# Run with specific profile
java -jar target/otoch-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Run with custom port
java -jar target/otoch-backend-0.0.1-SNAPSHOT.jar --server.port=9090
```

### Dependency Management

```bash
# Display dependency tree
mvn dependency:tree

# Download dependencies for offline use
mvn dependency:go-offline

# Check for dependency updates
mvn versions:display-dependency-updates
```

### Other Useful Commands

```bash
# Generate project documentation
mvn site

# Run only specific test class
mvn test -Dtest=ItemControllerTests

# Run in debug mode (port 5005)
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

---

## Additional Resources

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Framework Documentation](https://docs.spring.io/spring-framework/reference/)
- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
- [Maven Getting Started Guide](https://maven.apache.org/guides/getting-started/)
- [Bean Validation (Jakarta)](https://jakarta.ee/specifications/bean-validation/)
