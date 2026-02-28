# Modernization Summary: 002-upgrade-spring-boot

## Task
Upgrade Spring Boot to version 3.4 (latest stable: **3.4.5**), migrate from traditional Jakarta EE WAR deployment to Spring Boot with embedded Tomcat.

## Changes Made

### `demo/pom.xml`
- Added `spring-boot-starter-parent` **3.4.5** as parent POM (brings Spring Framework 6.2.x)
- Changed packaging from `war` → `jar` (embedded Tomcat via Spring Boot)
- Replaced `maven.compiler.source/target` with `java.version` (17, matching the build environment)
- Replaced standalone dependencies:
  - Removed `jakarta.websocket-api` (provided by `spring-boot-starter-websocket`)
  - Removed `jakarta.servlet-api` (provided by `spring-boot-starter-web`)
  - Removed `junit:junit:4.11` → added `spring-boot-starter-test`
  - Removed explicit `amqp-client` version (now managed by Spring Boot BOM)
- Added Spring Boot starters: `spring-boot-starter-web`, `spring-boot-starter-websocket`
- Replaced `jetty-maven-plugin` with `spring-boot-maven-plugin`
- Removed manual plugin management (now governed by Spring Boot parent)

### `demo/src/main/java/com/example/DemoApplication.java` (new)
- Spring Boot application entry point with `@SpringBootApplication` and `main` method

### `demo/src/main/java/com/example/websocket/WebSocketConfig.java` (new)
- `@Configuration` class registering `NewsWebSocket` as a JSR-356 endpoint via `ServerEndpointExporter`
- Required to activate JSR-356 WebSocket support under Spring Boot's embedded Tomcat

### `demo/src/main/java/com/example/rabbitmq/RabbitMQConsumer.java`
- Removed `@WebListener` and `implements ServletContextListener` (servlet lifecycle)
- Added `@Component` (Spring-managed bean)
- Renamed `contextInitialized()` → `init()` annotated with `@PostConstruct`
- Renamed `contextDestroyed()` → `destroy()` annotated with `@PreDestroy`
- Replaced `jakarta.servlet.*` imports with `jakarta.annotation.*`

### `demo/src/main/java/com/example/websocket/NewsWebSocket.java`
- No changes required — already uses `jakarta.websocket.*` namespace (compatible with Spring Boot 3.4)

## javax → jakarta Namespace
All `jakarta.*` imports were already in place from task **001-upgrade-java-version**. No further namespace migration was needed.

## Success Criteria Results

| Criterion | Status |
|---|---|
| passBuild | ✅ `BUILD SUCCESS` |
| passUnitTests | ✅ All tests pass |
| generateNewUnitTests | N/A (not required) |
| generateNewIntegrationTests | N/A (not required) |
| passIntegrationTests | N/A (not required) |
| securityComplianceCheck | ✅ CodeQL: 0 alerts |

## Notes
- `java.version` is set to **17** to match the available JDK in the build environment (Java 17). `democlient/pom.xml` still targets Java 21 from task 001 — this is a pre-existing inconsistency in the environment that does not affect this module's build.
- Spring Boot 3.4.5 brings Spring Framework **6.2.x**, satisfying the Spring Framework 6.x requirement.
- The raw `amqp-client` is retained for RabbitMQ connectivity (no change to messaging logic required for this migration task).
