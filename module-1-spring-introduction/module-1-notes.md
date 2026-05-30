# Module 1 - Spring Boot Introduction: Notes

---

## 1.1 What is Spring Framework?

Spring is a **Dependency Injection framework** that makes Java applications **loosely coupled**.

- Makes development easier for JavaEE applications
- Builds applications from **"Plain Old Java Objects" (POJOs)** and applies enterprise services to them non-invasively
- Developed by **Rod Johnson in 2003**

### Important Components of Spring Framework

| Component | Purpose |
|---|---|
| **Core Container** | IoC container, bean management |
| **AOP** | Aspect-Oriented Programming |
| **JDBC** | Database connectivity |
| **Web** | Web layer (Spring MVC) |
| **Testing** | Unit/Integration testing support |

---

## 1.2 IoC Container (Inversion of Control)

> The IoC container is responsible for managing the components of an application and injecting dependencies into them.

What the IoC Container does:
- **Creates** objects (beans)
- **Wires** them together
- **Configures** them
- **Manages** their complete lifecycle

---

## 1.3 Beans

> A **bean** is a managed object that is instantiated, assembled, and managed by the Spring IoC container.

Beans are the **backbone** of a Spring application — they are the core building blocks wired together to create the application.

### How to Define Beans

**Method 1: Stereotype Annotations**
```java
@Component   // Generic bean
@Service     // Service layer
@Repository  // Data layer
@Controller  // Web layer
```

**Method 2: Explicit Bean Declaration (in a @Configuration class)**
```java
@Configuration
public class AppConfig {
    @Bean
    public MyService myService() {
        return new MyService();
    }
}
```

> **Note:** Spring originally used XML-based config. Java-based annotation config came later as a better alternative.

### Bean Lifecycle

```
Bean Created → Dependency Injected → Bean Initialized → Bean is Used → Bean Destroyed
```

| Stage | What happens |
|---|---|
| Bean Created | Instance created via factory method |
| Dependency Injected | Constructor/Setter/Field injection occurs |
| Bean Initialized | `@PostConstruct` method is called |
| Bean is Used | Application uses the bean |
| Bean Destroyed | `@PreDestroy` method called, context shuts down |

### Bean Lifecycle Hooks

```java
@PostConstruct
public void init() {
    // Called immediately after the bean is constructed and dependencies injected
}

@PreDestroy
public void cleanup() {
    // Called just before the bean is destroyed
}
```

### Bean Scopes

| Scope | Description |
|---|---|
| **singleton** | Default — one instance per Spring IoC container |
| **prototype** | A new instance every time the bean is requested |
| **request** | One instance per HTTP request (web context only) |
| **websocket** | One instance per WebSocket lifecycle (web context only) |

---

## 1.4 Dependency Injection (DI)

> **DI** is a design pattern where a component does not create its own dependencies — they are injected from an external source (managed by Spring).

### The Bakery Analogy (Alice, Bob, Frosting, Syrup)

```
Alice ──→ Frosting ──→ Bob
Alice ──→ Syrup    ──→ Bob
```

Alice needs Frosting and Syrup to bake — but she doesn't make them herself. Spring injects them.

### Benefits of Dependency Injection

- **Loose Coupling** — components are independent of their dependencies
- **Flexible Configuration** — dependencies can be configured externally and swapped easily
- **Improved Testability** — dependencies can be mocked or replaced during unit testing

### Types of DI

#### 1. Constructor Injection ✅ Recommended
```java
@Component
public class CakeBaker {
    private final Frosting frosting;
    private final Syrup syrup;

    @Autowired
    public CakeBaker(Frosting frosting, Syrup syrup) {
        this.frosting = frosting;
        this.syrup = syrup;
    }
}
```

#### 2. Field Injection (@Autowired directly on field)
```java
@Component
public class CakeBaker {
    @Autowired
    private Frosting frosting;

    @Autowired
    private Syrup syrup;
}
```

> Prefer Constructor Injection — gives immutable fields and makes testing easier.

---

## 1.5 Spring Boot vs Spring Framework

| Feature | Spring Framework | Spring Boot |
|---|---|---|
| Configuration | Manual XML/Java config | Auto Configuration |
| Server | Deploy to external server | Embedded Tomcat/Jetty |
| Dependencies | Manage manually | Starter dependencies |
| Boilerplate | More | Much less |
| Production features | Extra setup required | Built-in Actuator, Metrics, Health Checks |
| Development speed | Slow setup | Fast development |

### Spring Boot's 5 Key Features

1. **Starter Dependencies** → `spring-boot-starter-web`, `spring-boot-starter-data-jpa`
2. **Auto Configuration** → Detects classpath and configures automatically
3. **Externalized Configuration** → `application.properties` / `.yaml`
4. **Embedded Servers** → Tomcat, Jetty bundled in
5. **Built-in Metrics & Health Checks** → Spring Actuator

---

## 1.6 Auto-Configuration and Spring Boot Internal Flow

### What is Auto-Configuration?

> Automatically configuring a Spring application based on dependencies present on the classpath and other application-specific settings.

The goal: developers focus on **business logic**, not framework setup.

### How Auto-Configuration Works

```
Classpath Scanning → Configuration Classes → Conditional Beans
```

1. **Classpath Scanning** — Spring Boot scans the classpath for libraries and classes
2. **Configuration Classes** — Each autoconfiguration class is responsible for configuring one specific part of the app
3. **Conditional Beans** — Each class uses conditional checks to decide whether to apply itself

### Important Conditional Annotations

```java
@ConditionalOnBean(DataSource.class)      // True if user defined a DataSource @Bean
@ConditionalOnClass(DataSource.class)     // True if DataSource class is on the classpath
@ConditionalOnProperty("my.property")    // True if my.property is set
```

### Role of pom.xml

- **Maven** resolves and downloads dependencies
- `spring-boot-starter-parent` includes tons of third-party libraries
- `spring-boot-dependencies` has every 3rd party library version predefined — you don't need to specify versions

### Key Insight
> Spring Boot is essentially a collection of AutoConfiguration classes (normal `@Configuration` classes) that create `@Bean`s when certain `@Condition`s are met.

### Spring Boot Internal Flow (Step by Step)

1. **Initialization** — `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`
2. **Application Context Creation** — Classpath scanned, components/configs/auto-configs detected
3. **Auto-Configuration** — Conditional annotations checked, relevant beans configured
4. **Externalized Configuration** — `application.properties`, YAML, env variables loaded
5. **Embedded Web Server Init** — Tomcat/Jetty/Undertow initialized and started
6. **Application Startup** — `@PostConstruct` methods called, beans instantiated, DI performed
7. **Application Ready** — Context fully initialized, ready to serve HTTP requests

---

## 1.7 Maven

> Maven is a **build automation and project management tool** primarily used for Java projects.

### Maven as the Chef 🍳
> Maven "cooks" your project — fetches ingredients (dependencies), compiles, tests, and packages everything.

### Project & Dependency Management

- `pom.xml` defines project structure, dependencies, and build config
- Spring modules (`spring-core`, `spring-mvc`, `spring-boot`) are all Maven dependencies
- Versions are automatically managed via `spring-boot-dependencies`

### Maven Commands

| Command | Purpose |
|---|---|
| `mvn compile` | Compile the source code |
| `mvn clean` | Delete previous build files |
| `mvn test` | Run tests |
| `mvn package` | Build a JAR/WAR file |
| `mvn install` | Install to local repository |
| `mvn deploy` | Deploy to remote repository |
| `mvn spring-boot:run` | Run app directly from source (no packaging needed) |
| `mvn spring-boot:build-image` | Build a Docker image |

---

## Annotations Learned in Module 1

| Annotation | Purpose |
|---|---|
| `@SpringBootApplication` | Main class — Config + AutoConfig + ComponentScan combined |
| `@Component` | Mark a class as a Spring bean |
| `@Autowired` | Inject a dependency |
| `@Qualifier("name")` | Select a specific implementation when multiple exist |
| `@Configuration` | Mark a class as a configuration class |
| `@Bean` | Declare a bean explicitly inside a config class |
| `@PostConstruct` | Method to run after bean is initialized |
| `@PreDestroy` | Method to run before bean is destroyed |
| `@ConditionalOnClass` | AutoConfig — apply if class is on classpath |
| `@ConditionalOnBean` | AutoConfig — apply if bean exists |
| `@ConditionalOnProperty` | AutoConfig — apply if property is set |

---

## Key Concepts Summary

| Concept | One Line |
|---|---|
| **Spring Framework** | Java DI framework — makes apps loosely coupled |
| **IoC Container** | Creates, wires, configures, and manages beans |
| **Bean** | A Spring-managed object |
| **DI** | Dependencies injected externally — component doesn't create them |
| **Constructor Injection** | Best practice — immutable fields, easier testing |
| **Auto Configuration** | Detects classpath, automatically configures beans |
| **Maven** | Build tool — manages dependencies, compiles, tests, packages |
| **Spring Boot** | Spring + Auto Config + Embedded Server = Fast Development |
