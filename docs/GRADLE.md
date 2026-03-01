# Gradle Documentation for Otoch Backend

This document provides a comprehensive explanation of Gradle and how it's used in this project, including its integration with Docker.

---

## Table of Contents

1. [What is Gradle?](#what-is-gradle)
2. [Why Gradle?](#why-gradle)
3. [Project Gradle Files](#project-gradle-files)
4. [The Gradle Wrapper](#the-gradle-wrapper)
5. [build.gradle Explained](#buildgradle-explained)
6. [settings.gradle Explained](#settingsgradle-explained)
7. [Gradle Build Lifecycle](#gradle-build-lifecycle)
8. [Gradle and Docker Integration](#gradle-and-docker-integration)
9. [Common Gradle Commands](#common-gradle-commands)
10. [Dependency Management](#dependency-management)
11. [Gradle vs Maven](#gradle-vs-maven)
12. [Troubleshooting](#troubleshooting)

---

## What is Gradle?

**Gradle** is a modern build automation tool used primarily for Java projects. It uses a Groovy or Kotlin DSL (Domain Specific Language) instead of XML, making build scripts more concise and readable.

### Key Features

| Feature | Description |
|---------|-------------|
| **Incremental Builds** | Only rebuilds what changed, making subsequent builds much faster |
| **Build Cache** | Caches build outputs locally and remotely for reuse |
| **Daemon Process** | Keeps a background process running for faster builds |
| **Dependency Management** | Automatically downloads and manages project dependencies |
| **Plugin Ecosystem** | Extensible through plugins (Spring Boot, Docker, etc.) |
| **Parallel Execution** | Runs independent tasks in parallel |
| **Groovy/Kotlin DSL** | Concise, readable build scripts with full programming language power |

### How Gradle Works

```
┌─────────────────────────────────────────────────────────────────┐
│                        build.gradle                              │
│  (Defines plugins, dependencies, tasks, and build configuration) │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Gradle Daemon                               │
│  (Long-running background process that executes builds)          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Task Execution                              │
│  compileJava → processResources → classes → bootJar → build     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Output                                   │
│  build/libs/otoch-backend.jar (executable JAR)                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Why Gradle?

### Advantages Over Other Build Tools

| Advantage | Details |
|-----------|---------|
| **Faster Builds** | Incremental builds and caching make Gradle 2-10x faster than Maven on large projects |
| **Concise Syntax** | Groovy DSL is more readable than XML (32 lines vs 56 lines for this project) |
| **Flexibility** | Full programming language in build scripts for custom logic |
| **Better Dependency Resolution** | Handles version conflicts more intelligently |
| **Modern Tooling** | Build scans, continuous builds, composite builds |

### Build Speed Comparison

| Scenario | Maven | Gradle |
|----------|-------|--------|
| Clean build | ~15s | ~12s |
| Incremental (no changes) | ~5-8s | **~1-3s** |
| Single file change | ~5-8s | **~2-4s** |

The speed difference comes from:
- **Incremental compilation** – Only recompiles changed files
- **Task output caching** – Skips tasks whose inputs haven't changed
- **Daemon process** – JVM stays warm between builds

---

## Project Gradle Files

This project contains the following Gradle-related files:

```
otoch_backend/
├── build.gradle                    # Main build configuration
├── settings.gradle                 # Project settings
├── gradlew                         # Gradle wrapper script (Unix)
├── gradlew.bat                     # Gradle wrapper script (Windows)
└── gradle/
    └── wrapper/
        ├── gradle-wrapper.jar      # Wrapper bootstrap JAR
        └── gradle-wrapper.properties  # Wrapper configuration
```

| File | Purpose |
|------|---------|
| `build.gradle` | Defines plugins, dependencies, and build tasks |
| `settings.gradle` | Defines project name and multi-project settings |
| `gradlew` / `gradlew.bat` | Wrapper scripts to run Gradle without installing it |
| `gradle-wrapper.jar` | Small JAR that downloads and runs the correct Gradle version |
| `gradle-wrapper.properties` | Specifies which Gradle version to use |

---

## The Gradle Wrapper

The **Gradle Wrapper** is a script that invokes a declared version of Gradle, downloading it first if necessary. It's the recommended way to run Gradle.

### Why Use the Wrapper?

| Benefit | Description |
|---------|-------------|
| **No Installation Required** | Developers don't need to install Gradle manually |
| **Version Consistency** | Everyone uses the exact same Gradle version |
| **Reproducible Builds** | CI/CD and developers build identically |
| **Easy Updates** | Change version in one file, everyone gets it |

### How It Works

```
┌─────────────────────────────────────────────────────────────────┐
│  Developer runs: ./gradlew build                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  gradlew script reads gradle-wrapper.properties                  │
│  → distributionUrl=...gradle-8.5-bin.zip                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  First run? Download Gradle 8.5 to ~/.gradle/wrapper/dists/     │
│  Already downloaded? Use cached version                          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Execute: gradle build                                           │
└─────────────────────────────────────────────────────────────────┘
```

### gradle-wrapper.properties Explained

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

| Property | Value | Purpose |
|----------|-------|---------|
| `distributionUrl` | `gradle-8.5-bin.zip` | Which Gradle version to download |
| `distributionBase` | `GRADLE_USER_HOME` | Base directory for Gradle (`~/.gradle/`) |
| `distributionPath` | `wrapper/dists` | Subdirectory for Gradle distributions |
| `networkTimeout` | `10000` | Download timeout in milliseconds |
| `validateDistributionUrl` | `true` | Verify the download URL is valid |

### Updating Gradle Version

To update to a newer Gradle version:

```bash
# If you have Gradle installed
gradle wrapper --gradle-version 8.6

# Or manually edit gradle-wrapper.properties
# Change: distributionUrl=https\://services.gradle.org/distributions/gradle-8.6-bin.zip
```

---

## build.gradle Explained

The `build.gradle` file is the heart of the project's build configuration.

### Complete File with Annotations

```groovy
// Plugins extend Gradle's functionality
plugins {
    id 'java'                                           // Java compilation and packaging
    id 'org.springframework.boot' version '3.2.2'       // Spring Boot tasks (bootRun, bootJar)
    id 'io.spring.dependency-management' version '1.1.4' // Manages dependency versions (BOM)
}

// Project coordinates (like Maven's groupId:artifactId:version)
group = 'com.otoch'
version = '0.0.1-SNAPSHOT'

// Java version configuration
java {
    sourceCompatibility = '17'   // Compile for Java 17
}

// Where to download dependencies from
repositories {
    mavenCentral()               // Maven Central Repository
}

// Project dependencies
dependencies {
    // Runtime dependencies (included in JAR)
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Test dependencies (not included in production JAR)
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

// Configure test task to use JUnit 5
tasks.named('test') {
    useJUnitPlatform()
}

// Customize the output JAR name
tasks.named('bootJar') {
    archiveFileName = 'otoch-backend.jar'
}
```

### Plugins Block

| Plugin | Purpose |
|--------|---------|
| `java` | Adds tasks for compiling Java, running tests, creating JARs |
| `org.springframework.boot` | Adds `bootRun`, `bootJar`, and Spring Boot configuration |
| `io.spring.dependency-management` | Imports Spring Boot's BOM for dependency version management |

### Dependencies Block

| Configuration | Purpose | In Production JAR? |
|---------------|---------|-------------------|
| `implementation` | Compile and runtime dependency | Yes |
| `compileOnly` | Compile-time only (e.g., Lombok) | No |
| `runtimeOnly` | Runtime only (e.g., JDBC drivers) | Yes |
| `testImplementation` | Test compile and runtime | No |
| `testRuntimeOnly` | Test runtime only | No |

### Task Configuration

Tasks are units of work. You can configure existing tasks:

```groovy
// Configure the 'test' task
tasks.named('test') {
    useJUnitPlatform()           // Use JUnit 5
    testLogging {
        events "passed", "skipped", "failed"  // Show test results
    }
}

// Configure the 'bootJar' task
tasks.named('bootJar') {
    archiveFileName = 'otoch-backend.jar'     // Custom JAR name
    // archiveVersion = ''                     // Remove version from filename
}
```

---

## settings.gradle Explained

The `settings.gradle` file defines project-level settings.

```groovy
rootProject.name = 'otoch-backend'
```

| Property | Purpose |
|----------|---------|
| `rootProject.name` | Project name (used in JAR filename, logs, etc.) |

For multi-project builds, you would also include:

```groovy
rootProject.name = 'otoch'
include 'otoch-backend'
include 'otoch-frontend'
```

---

## Gradle Build Lifecycle

Gradle organizes work into **tasks** with dependencies between them.

### Task Graph for This Project

```
                    clean
                      │
                      ▼
compileJava ──► processResources ──► classes
                                        │
                    ┌───────────────────┴───────────────────┐
                    ▼                                       ▼
                  jar                              compileTestJava
                    │                                       │
                    ▼                                       ▼
                bootJar                            processTestResources
                    │                                       │
                    ▼                                       ▼
                assemble                              testClasses
                    │                                       │
                    └───────────────┬───────────────────────┘
                                    ▼
                                  test
                                    │
                                    ▼
                                  check
                                    │
                                    ▼
                                  build
```

### Key Tasks

| Task | Description | Output |
|------|-------------|--------|
| `compileJava` | Compiles Java source code | `build/classes/java/main/` |
| `processResources` | Copies resources to build dir | `build/resources/main/` |
| `classes` | Combines compiled code and resources | - |
| `jar` | Creates plain JAR (no dependencies) | `build/libs/*-plain.jar` |
| `bootJar` | Creates executable "fat" JAR | `build/libs/otoch-backend.jar` |
| `test` | Runs unit tests | `build/reports/tests/` |
| `build` | Full build (compile, test, assemble) | - |
| `clean` | Deletes `build/` directory | - |
| `bootRun` | Runs the application | - |

### Incremental Builds

Gradle tracks inputs and outputs for each task:

```
> Task :compileJava UP-TO-DATE      # No changes, skipped
> Task :processResources UP-TO-DATE  # No changes, skipped
> Task :classes UP-TO-DATE
> Task :bootJar UP-TO-DATE
> Task :assemble UP-TO-DATE
> Task :test UP-TO-DATE
> Task :check UP-TO-DATE
> Task :build UP-TO-DATE

BUILD SUCCESSFUL in 1s
7 actionable tasks: 7 up-to-date
```

**UP-TO-DATE** means Gradle detected no changes to inputs, so it skipped the task entirely.

---

## Gradle and Docker Integration

This project uses Gradle inside Docker for building the application. Understanding this integration is crucial for efficient Docker builds.

### How the Dockerfile Uses Gradle

```dockerfile
# Build stage using Gradle
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy Gradle files first for dependency caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon

# Copy source code and build
COPY src ./src
RUN gradle bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/otoch-backend.jar app.jar
# ... rest of runtime configuration
```

### Layer Caching Strategy

Docker caches layers based on instruction order. We optimize by:

1. **Copying dependency files first** (`build.gradle`, `settings.gradle`)
2. **Downloading dependencies** (`gradle dependencies`)
3. **Copying source code last** (`COPY src ./src`)

This way, **dependency downloads are cached** unless `build.gradle` changes:

```
┌─────────────────────────────────────────────────────────────────┐
│  COPY build.gradle settings.gradle ./                            │
│  → Changes rarely, layer is cached                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  RUN gradle dependencies --no-daemon                             │
│  → Dependencies cached, ~30s saved on subsequent builds          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  COPY src ./src                                                  │
│  → Changes frequently, but previous layers are cached            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  RUN gradle bootJar --no-daemon                                  │
│  → Only this runs on code changes (fast, deps already cached)   │
└─────────────────────────────────────────────────────────────────┘
```

### The `--no-daemon` Flag

Inside Docker, we use `--no-daemon`:

```bash
RUN gradle bootJar --no-daemon
```

| Aspect | With Daemon | Without Daemon (`--no-daemon`) |
|--------|-------------|-------------------------------|
| Speed (first build) | ~30s | ~30s |
| Speed (subsequent) | ~5s (daemon warm) | ~30s |
| Memory | Daemon stays in memory | No residual processes |
| Docker suitability | Poor (daemon dies after RUN) | Good |

**Why `--no-daemon` in Docker:**
- Each `RUN` command is a new shell
- The daemon would start, do work, then be killed
- Starting a daemon that immediately dies wastes time
- Containers don't benefit from warm daemons

### Local Build vs Docker Build

| Aspect | Local (`./gradlew build`) | Docker (`docker build`) |
|--------|--------------------------|------------------------|
| Gradle version | From wrapper (8.5) | From image (`gradle:8.5-jdk17`) |
| Daemon | Yes (stays warm) | No (`--no-daemon`) |
| Cache location | `~/.gradle/caches/` | Docker layer cache |
| Incremental builds | Yes (very fast) | Yes (layer-based) |
| Output | `build/libs/*.jar` | Image with JAR inside |

### Build Comparison

```bash
# Local build (fast incremental builds, uses daemon)
./gradlew build

# Docker build (uses layer caching, no daemon)
docker build -t otoch-backend .

# Docker Compose (wraps docker build)
docker-compose up --build
```

### Matching Versions

Keep Gradle versions consistent:

| Location | Version | File |
|----------|---------|------|
| Wrapper | 8.5 | `gradle-wrapper.properties` |
| Docker image | 8.5 | `Dockerfile` (`gradle:8.5-jdk17`) |

If you update one, update the other to avoid "works locally but fails in Docker" issues.

---

## Common Gradle Commands

### Building

```bash
# Compile source code only
./gradlew compileJava

# Compile and run tests
./gradlew test

# Full build (compile, test, package)
./gradlew build

# Build without tests (faster)
./gradlew build -x test

# Clean build directory
./gradlew clean

# Clean and build
./gradlew clean build
```

### Running

```bash
# Run the application
./gradlew bootRun

# Run with arguments
./gradlew bootRun --args='--server.port=9090'

# Run with debug enabled (port 5005)
./gradlew bootRun --debug-jvm

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests ItemControllerTests

# Run specific test method
./gradlew test --tests "ItemControllerTests.getAllItems_returnsItemsList"

# Run tests matching pattern
./gradlew test --tests "*Controller*"

# Run tests with verbose output
./gradlew test --info

# Open test report in browser
# (after running tests)
xdg-open build/reports/tests/test/index.html
```

### Dependencies

```bash
# Show all dependencies
./gradlew dependencies

# Show dependencies for specific configuration
./gradlew dependencies --configuration runtimeClasspath

# Show why a dependency is included
./gradlew dependencyInsight --dependency spring-core

# Refresh dependencies (ignore cache)
./gradlew build --refresh-dependencies
```

### Information

```bash
# List all available tasks
./gradlew tasks

# List all tasks including hidden ones
./gradlew tasks --all

# Show project properties
./gradlew properties

# Show Gradle version
./gradlew --version
```

### Daemon Management

```bash
# Check daemon status
./gradlew --status

# Stop all daemons
./gradlew --stop

# Run without daemon (useful for CI)
./gradlew build --no-daemon
```

### Build Analysis

```bash
# Generate build scan (requires terms acceptance)
./gradlew build --scan

# Show timing information
./gradlew build --profile
# Open: build/reports/profile/profile-*.html
```

### Quick Reference Table

| Task | Command |
|------|---------|
| Compile | `./gradlew compileJava` |
| Test | `./gradlew test` |
| Build | `./gradlew build` |
| Build (skip tests) | `./gradlew build -x test` |
| Run | `./gradlew bootRun` |
| Clean | `./gradlew clean` |
| Clean + Build | `./gradlew clean build` |
| Dependencies | `./gradlew dependencies` |
| All tasks | `./gradlew tasks` |
| Stop daemons | `./gradlew --stop` |

---

## Dependency Management

### How Dependencies Work

1. **Declare dependencies** in `build.gradle`
2. **Gradle resolves** versions (handles conflicts)
3. **Downloads** from repositories (Maven Central)
4. **Caches** in `~/.gradle/caches/`
5. **Includes** in classpath for compilation/runtime

### Dependency Notation

```groovy
dependencies {
    // Full notation
    implementation group: 'org.springframework.boot', name: 'spring-boot-starter-web', version: '3.2.2'
    
    // Short notation (most common)
    implementation 'org.springframework.boot:spring-boot-starter-web:3.2.2'
    
    // Without version (managed by BOM)
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

### Spring Boot BOM

The `io.spring.dependency-management` plugin imports Spring Boot's Bill of Materials (BOM), which defines compatible versions:

```groovy
// You don't need to specify versions
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'        // Version managed
    implementation 'org.springframework.boot:spring-boot-starter-validation' // Version managed
}
```

### Viewing Dependencies

```bash
# Full dependency tree
./gradlew dependencies

# Runtime dependencies only
./gradlew dependencies --configuration runtimeClasspath

# Sample output:
# runtimeClasspath
# +--- org.springframework.boot:spring-boot-starter-web -> 3.2.2
# |    +--- org.springframework.boot:spring-boot-starter:3.2.2
# |    |    +--- org.springframework.boot:spring-boot:3.2.2
# |    |    |    +--- org.springframework:spring-core:6.1.3
# |    ...
```

### Cache Location

Dependencies are cached locally:

```
~/.gradle/
├── caches/
│   └── modules-2/
│       └── files-2.1/
│           ├── org.springframework.boot/
│           │   └── spring-boot-starter-web/
│           │       └── 3.2.2/
│           │           └── *.jar
│           └── ... (other dependencies)
└── wrapper/
    └── dists/
        └── gradle-8.5-bin/
            └── ... (Gradle distribution)
```

### Clearing Cache

```bash
# Remove all cached dependencies (re-downloads on next build)
rm -rf ~/.gradle/caches/

# Or just refresh on next build
./gradlew build --refresh-dependencies
```

---

## Gradle vs Maven

### Build File Comparison

**Maven (pom.xml) - 56 lines:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" ...>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.2</version>
    </parent>
    <groupId>com.otoch</groupId>
    <artifactId>otoch-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- ... more dependencies ... -->
    </dependencies>
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

**Gradle (build.gradle) - 32 lines:**

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

### Command Comparison

| Task | Maven | Gradle |
|------|-------|--------|
| Compile | `mvn compile` | `./gradlew compileJava` |
| Test | `mvn test` | `./gradlew test` |
| Package | `mvn package` | `./gradlew build` |
| Run | `mvn spring-boot:run` | `./gradlew bootRun` |
| Clean | `mvn clean` | `./gradlew clean` |
| Skip tests | `mvn package -DskipTests` | `./gradlew build -x test` |
| Dependencies | `mvn dependency:tree` | `./gradlew dependencies` |

### Feature Comparison

| Feature | Maven | Gradle |
|---------|-------|--------|
| Build file format | XML | Groovy/Kotlin DSL |
| Incremental builds | Limited | Full support |
| Build cache | Requires plugin | Built-in |
| Daemon | No | Yes |
| Parallel execution | Limited | Full support |
| Custom logic | Requires plugins | Native (Groovy code) |
| Learning curve | Lower | Higher |
| IDE support | Excellent | Excellent |

---

## Troubleshooting

### Common Issues

**"JAVA_HOME is not set"**

```bash
# Check current JAVA_HOME
echo $JAVA_HOME

# Set it (add to ~/.bashrc for persistence)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

**"Could not resolve dependencies"**

```bash
# Check network connectivity to Maven Central
curl -I https://repo.maven.apache.org/maven2/

# Refresh dependencies
./gradlew build --refresh-dependencies

# Clear cache and retry
rm -rf ~/.gradle/caches/
./gradlew build
```

**"Gradle daemon disappeared unexpectedly"**

```bash
# Stop all daemons and retry
./gradlew --stop
./gradlew build
```

**Build is slow**

```bash
# Check if daemon is running
./gradlew --status

# If not running, subsequent builds will be slower
# Ensure GRADLE_OPTS isn't disabling daemon

# Run with build scan for analysis
./gradlew build --scan
```

**"Unsupported class file major version"**

The compiled code requires a newer Java than what's running Gradle:

```bash
# Check Java version
java -version

# Ensure JAVA_HOME points to Java 17+
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./gradlew build
```

**Tests failing in Docker but passing locally**

- Check for hardcoded paths or localhost assumptions
- Ensure test resources are copied correctly
- Check timezone/locale differences

### Useful Debug Commands

```bash
# Verbose output
./gradlew build --info

# Debug output (very verbose)
./gradlew build --debug

# Stack trace on error
./gradlew build --stacktrace

# All of the above
./gradlew build --info --stacktrace
```

### Getting Help

```bash
# Help for a specific task
./gradlew help --task bootRun

# Gradle documentation
# https://docs.gradle.org/current/userguide/userguide.html
```

---

## Additional Resources

- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html)
- [Gradle DSL Reference](https://docs.gradle.org/current/dsl/)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/)
- [Gradle Build Scans](https://scans.gradle.com/)
- [Gradle vs Maven](https://gradle.org/maven-vs-gradle/)
