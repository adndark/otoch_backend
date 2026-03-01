# Spring Boot Documentation for Otoch Backend

This document provides a comprehensive explanation of Spring Boot and all Spring-related code, patterns, and build processes in this project.

---

## Table of Contents

1. [What is Spring Boot?](#what-is-spring-boot)
2. [Project Structure](#project-structure)
3. [Local Development Setup (Ubuntu)](#local-development-setup-ubuntu)
4. [Build Process (Gradle)](#build-process-gradle)
5. [Dependencies Explained](#dependencies-explained)
6. [Application Entry Point](#application-entry-point)
7. [Architectural Patterns](#architectural-patterns)
8. [Code Structure and Layers](#code-structure-and-layers)
9. [Annotations Reference](#annotations-reference)
10. [Configuration](#configuration)
11. [Testing](#testing)
12. [Common Gradle Commands](#common-gradle-commands)

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
├── build.gradle                               # Gradle build configuration
├── settings.gradle                            # Gradle project settings
├── gradlew                                    # Gradle wrapper (Unix)
├── gradlew.bat                                # Gradle wrapper (Windows)
├── gradle/wrapper/
│   ├── gradle-wrapper.jar                     # Wrapper bootstrap JAR
│   └── gradle-wrapper.properties              # Wrapper configuration
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

You can compile, test, and run the application locally on Ubuntu without using Docker. This requires **Java JDK 17** installed on your machine. Gradle is included via the wrapper.

### Prerequisites

#### Option 1: Using apt (Ubuntu packages)

```bash
# Update package index
sudo apt update

# Install OpenJDK 17
sudo apt install openjdk-17-jdk

# Verify installation
java -version    # Should show: openjdk version "17.x.x"

# Set JAVA_HOME (add to ~/.bashrc for persistence)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

#### Option 2: Using SDKMAN (recommended for managing multiple Java versions)

[SDKMAN](https://sdkman.io/) is a tool for managing parallel versions of multiple SDKs.

```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash

# Open a new terminal or run:
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 17 (Eclipse Temurin distribution)
sdk install java 17.0.10-tem

# Verify installation
java -version
```

### Compile and Test Locally

Navigate to the project directory and use the Gradle wrapper:

```bash
cd /home/adndark/projects/otoch/otoch_backend

# Compile the source code
./gradlew compileJava

# Compile and run all tests
./gradlew test

# Full build (compile, test, package JAR)
./gradlew build

# Clean previous builds, then build
./gradlew clean build

# Build without running tests (faster)
./gradlew build -x test
```

### Run the Application Locally

```bash
# Run via Gradle (compiles automatically if needed)
./gradlew bootRun

# Or run the packaged JAR (requires ./gradlew build first)
java -jar build/libs/otoch-backend.jar

# Run with a specific port
java -jar build/libs/otoch-backend.jar --server.port=9090

# Run with a specific profile
java -jar build/libs/otoch-backend.jar --spring.profiles.active=prod
```

The application starts on `http://localhost:8080` by default.

### Verify the Application is Running

Open another terminal and test the endpoints:

```bash
# Health check
curl http://localhost:8080/api/health

# Get all items
curl http://localhost:8080/api/items
```

### Quick Reference: Local Development Commands

| Task | Command |
|------|---------|
| Compile source | `./gradlew compileJava` |
| Run tests | `./gradlew test` |
| Run single test class | `./gradlew test --tests ItemControllerTests` |
| Build JAR | `./gradlew build` |
| Build (skip tests) | `./gradlew build -x test` |
| Clean and build | `./gradlew clean build` |
| Run via Gradle | `./gradlew bootRun` |
| Run JAR | `java -jar build/libs/otoch-backend.jar` |
| Stop application | `Ctrl+C` |

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

**Port 8080 already in use:**

```bash
# Find what's using port 8080
sudo lsof -i :8080

# Kill the process (replace PID with actual process ID)
kill -9 <PID>

# Or run on a different port
./gradlew bootRun --args='--server.port=9090'
```

---

## Build Process (Gradle)

This project uses **Gradle** as the build tool. Gradle handles dependency management, compilation, testing, and packaging.

### What is Gradle?

Gradle is a modern build automation tool that uses a Groovy or Kotlin DSL (Domain Specific Language) for build scripts. It's known for:

- **Fast builds** – Incremental compilation and build caching
- **Concise syntax** – Much shorter than XML-based tools
- **Flexibility** – Easy to customize with Groovy/Kotlin code
- **Daemon** – Background process for faster subsequent builds

### The Gradle Wrapper

The project includes a **Gradle Wrapper** (`gradlew`), which is the recommended way to run Gradle. Benefits:

- **No Gradle installation required** – The wrapper downloads Gradle automatically
- **Version consistency** – Everyone uses the same Gradle version
- **Reproducible builds** – CI/CD and developers use identical setups

```bash
# Unix/macOS
./gradlew build

# Windows
gradlew.bat build
```

### The build.gradle File

The `build.gradle` is the heart of a Gradle project:

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.2'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.otoch'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}

tasks.named('bootJar') {
    archiveFileName = 'otoch-backend.jar'
}
```

### build.gradle Elements Explained

| Element | Purpose |
|---------|---------|
| `plugins { }` | Applies Gradle plugins (java, spring-boot, dependency-management) |
| `id 'java'` | Enables Java compilation, testing, and packaging |
| `id 'org.springframework.boot'` | Adds Spring Boot tasks like `bootRun` and `bootJar` |
| `id 'io.spring.dependency-management'` | Manages dependency versions (BOM) |
| `group` | Organization/project identifier |
| `version` | Project version. `SNAPSHOT` means development version |
| `java { sourceCompatibility }` | Target Java version (17) |
| `repositories { mavenCentral() }` | Where to download dependencies from |
| `dependencies { }` | Project dependencies (see below) |
| `implementation` | Runtime dependency (included in JAR) |
| `testImplementation` | Test-only dependency (not in production JAR) |
| `useJUnitPlatform()` | Use JUnit 5 for testing |
| `bootJar { archiveFileName }` | Customize the output JAR name |

### settings.gradle File

The `settings.gradle` defines project-level settings:

```groovy
rootProject.name = 'otoch-backend'
```

| Element | Purpose |
|---------|---------|
| `rootProject.name` | The project name (used in output filenames) |

### Gradle Build Lifecycle

Gradle organizes work into **tasks**. Key tasks for this project:

```
compileJava → processResources → classes → jar → bootJar → assemble
                                                              ↓
                                        compileTestJava → test → check → build
```

| Task | What It Does |
|------|--------------|
| `compileJava` | Compiles `src/main/java` to `build/classes` |
| `processResources` | Copies `src/main/resources` to `build/resources` |
| `classes` | Combines compiled code and resources |
| `jar` | Creates a plain JAR (without dependencies) |
| `bootJar` | Creates executable "fat JAR" with all dependencies |
| `assemble` | Builds without running tests |
| `compileTestJava` | Compiles test code |
| `test` | Runs unit tests |
| `check` | Runs tests and verification tasks |
| `build` | Full build: compile, test, package |

### What `./gradlew build` Does

When you run `./gradlew build`, Gradle:

1. **Downloads dependencies** – From Maven Central to `~/.gradle/caches/`
2. **Compiles source code** – `src/main/java/**/*.java` → `build/classes/java/main/**/*.class`
3. **Processes resources** – Copies `src/main/resources/*` to `build/resources/main/`
4. **Compiles test code** – `src/test/java/**/*.java` → `build/classes/java/test/**/*.class`
5. **Runs tests** – Executes all test classes
6. **Packages the JAR** – Creates `build/libs/otoch-backend.jar`

### Incremental Builds

Gradle tracks inputs and outputs for each task. If nothing changed, tasks are **UP-TO-DATE** and skipped:

```
> Task :compileJava UP-TO-DATE
> Task :processResources UP-TO-DATE
> Task :classes UP-TO-DATE
> Task :bootJar UP-TO-DATE
...
BUILD SUCCESSFUL in 3s
7 actionable tasks: 7 up-to-date
```

This makes subsequent builds **much faster** than clean builds.

### The Executable JAR ("Fat JAR")

The Spring Boot Gradle plugin creates a special **executable JAR** (also called a "fat JAR" or "uber JAR"):

- Contains your compiled code
- Contains all dependencies (embedded inside the JAR)
- Contains an embedded Tomcat server
- Can be run with `java -jar otoch-backend.jar`

**JAR location:** `build/libs/otoch-backend.jar`

**JAR structure:**

```
otoch-backend.jar
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

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### Dependency Scopes in Gradle

| Scope | Purpose | Included in JAR? |
|-------|---------|------------------|
| `implementation` | Runtime dependency | Yes |
| `compileOnly` | Compile-time only (e.g., Lombok) | No |
| `runtimeOnly` | Runtime only (e.g., database drivers) | Yes |
| `testImplementation` | Test compile and runtime | No |
| `testRuntimeOnly` | Test runtime only | No |

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

**What it enables:**

- `@RestController`, `@RequestMapping`, `@GetMapping`, etc.
- Automatic JSON conversion for request/response bodies
- Embedded web server on port 8080

#### 2. spring-boot-starter-validation

**Purpose:** Bean validation using Jakarta Validation.

**What it includes:**

| Library | Purpose |
|---------|---------|
| Hibernate Validator | Reference implementation of Bean Validation |
| Jakarta Validation API | Validation annotations (`@NotBlank`, `@Positive`, etc.) |

**What it enables:**

- `@Valid` annotation to trigger validation on request bodies
- Validation annotations on model fields
- Automatic 400 Bad Request responses for validation failures

#### 3. spring-boot-starter-actuator

**Purpose:** Production-ready features for monitoring and managing the application.

**What it includes:**

| Feature | Purpose |
|---------|---------|
| Health endpoint | `/actuator/health` – Is the app running? |
| Info endpoint | `/actuator/info` – Application metadata |
| Metrics | `/actuator/metrics` – JVM, HTTP, and custom metrics |

#### 4. spring-boot-starter-test

**Purpose:** Testing support with common testing libraries.

**Scope:** `testImplementation` – Only available during testing, not in production JAR.

**What it includes:**

| Library | Purpose |
|---------|---------|
| JUnit 5 | Testing framework |
| Spring Test | Spring testing utilities |
| Mockito | Mocking framework |
| AssertJ | Fluent assertions |
| JSONPath | JSON assertions |
| MockMvc | Test Spring MVC controllers |

### Why No Version Numbers?

Dependencies don't specify versions:

```groovy
implementation 'org.springframework.boot:spring-boot-starter-web'
// No version specified!
```

The `io.spring.dependency-management` plugin imports Spring Boot's **BOM** (Bill of Materials), which defines compatible versions for all Spring Boot dependencies.

### View Dependency Tree

```bash
./gradlew dependencies --configuration runtimeClasspath
```

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
| `@EnableAutoConfiguration` | Tells Spring Boot to auto-configure beans based on classpath |
| `@ComponentScan` | Scans `com.otoch` and all sub-packages for components |

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
```

### Benefits of Layered Architecture

| Benefit | Description |
|---------|-------------|
| **Separation of Concerns** | Each layer has one responsibility |
| **Testability** | Layers can be tested in isolation (mock dependencies) |
| **Maintainability** | Changes in one layer don't affect others |
| **Reusability** | Services can be used by multiple controllers |

### 2. RESTful API Design

The API follows REST principles:

| Principle | Implementation |
|-----------|----------------|
| **Resource-based URLs** | `/api/items` represents the items resource |
| **HTTP methods for actions** | GET (read), POST (create), PUT (update), DELETE (delete) |
| **Stateless** | Each request contains all information needed |
| **Standard status codes** | 200 OK, 201 Created, 204 No Content, 404 Not Found |

### 3. Dependency Injection

Spring manages object creation through **Dependency Injection**:

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

---

## Code Structure and Layers

### Controller Layer

Controllers handle HTTP requests and return responses:

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
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping                             // POST /api/items
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item created = itemService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

### Service Layer

Services contain business logic:

```java
@Service                                     // Marks as a Spring-managed service bean
public class ItemService {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

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
}
```

### Model Layer

Models represent data structures with validation:

```java
public class Item {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Positive(message = "Price must be positive")
    private Double price;

    // Constructors, getters, setters...
}
```

---

## Annotations Reference

### Stereotype Annotations

| Annotation | Purpose |
|------------|---------|
| `@Component` | Generic Spring bean |
| `@Service` | Service layer bean |
| `@Repository` | Data access bean |
| `@Controller` | Web controller (returns views) |
| `@RestController` | REST API controller (`@Controller` + `@ResponseBody`) |
| `@Configuration` | Configuration class with `@Bean` methods |

### Web/REST Annotations

| Annotation | Purpose |
|------------|---------|
| `@RequestMapping` | Map URL to class/method |
| `@GetMapping` | Handle GET requests |
| `@PostMapping` | Handle POST requests |
| `@PutMapping` | Handle PUT requests |
| `@DeleteMapping` | Handle DELETE requests |
| `@PathVariable` | Extract URL path variable |
| `@RequestParam` | Extract query parameter |
| `@RequestBody` | Deserialize request body |
| `@ResponseStatus` | Set response status |

### Validation Annotations

| Annotation | Purpose |
|------------|---------|
| `@Valid` | Trigger validation on parameter |
| `@NotNull` | Field must not be null |
| `@NotBlank` | String must not be null/empty/whitespace |
| `@NotEmpty` | Collection/String must not be null/empty |
| `@Size` | Size constraints |
| `@Min`, `@Max` | Numeric range constraints |
| `@Positive` | Must be > 0 |
| `@Email` | Valid email format |
| `@Pattern` | Regex pattern match |

---

## Configuration

### application.properties

```properties
# Server Configuration
server.port=8080

# Application Info
spring.application.name=otoch-backend

# Actuator Endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

### Environment Variable Override

Properties can be overridden via environment variables:

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
application-dev.properties     # Dev-specific
application-prod.properties    # Prod-specific
```

Activate a profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
# or
java -jar build/libs/otoch-backend.jar --spring.profiles.active=prod
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

### Example: Controller Test

```java
@WebMvcTest(ItemController.class)
class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void getAllItems_returnsItemsList() throws Exception {
        Item item = new Item(1L, "Test Item", "Description", 10.0);
        when(itemService.getAllItems()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests ItemControllerTests

# Run with verbose output
./gradlew test --info

# View test report
# Open: build/reports/tests/test/index.html
```

---

## Common Gradle Commands

### Building

```bash
# Compile source code
./gradlew compileJava

# Compile and run tests
./gradlew test

# Full build (compile, test, package)
./gradlew build

# Clean build artifacts and rebuild
./gradlew clean build

# Build without running tests
./gradlew build -x test
```

### Running

```bash
# Run the application via Gradle
./gradlew bootRun

# Run with arguments
./gradlew bootRun --args='--server.port=9090'

# Run the packaged JAR
java -jar build/libs/otoch-backend.jar

# Run with specific profile
java -jar build/libs/otoch-backend.jar --spring.profiles.active=prod
```

### Dependency Management

```bash
# Display dependency tree
./gradlew dependencies

# Display runtime dependencies only
./gradlew dependencies --configuration runtimeClasspath

# Check for dependency updates
./gradlew dependencyUpdates  # (requires plugin)
```

### Other Useful Commands

```bash
# List all available tasks
./gradlew tasks

# Run with verbose output
./gradlew build --info

# Run with debug output
./gradlew build --debug

# Stop the Gradle daemon
./gradlew --stop

# Clean build directory
./gradlew clean
```

### Command Quick Reference

| Task | Command |
|------|---------|
| Compile | `./gradlew compileJava` |
| Test | `./gradlew test` |
| Build | `./gradlew build` |
| Clean | `./gradlew clean` |
| Run | `./gradlew bootRun` |
| Skip tests | `./gradlew build -x test` |
| Single test | `./gradlew test --tests ClassName` |
| Dependencies | `./gradlew dependencies` |
| All tasks | `./gradlew tasks` |

---

## Additional Resources

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Framework Documentation](https://docs.spring.io/spring-framework/reference/)
- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
