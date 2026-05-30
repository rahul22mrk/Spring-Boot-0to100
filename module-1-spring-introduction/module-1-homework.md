# Module 1 - Homework

---

## Question 1: List of All Annotations Learned So Far

### Spring Annotations

| Annotation | Purpose |
|---|---|
| `@SpringBootApplication` | Main entry point — combines `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan` |
| `@Component` | Marks a class as a Spring-managed bean (generic stereotype) |
| `@Autowired` | Injects a dependency (on constructor, field, or setter) |
| `@Qualifier("name")` | Specifies which implementation to inject when multiple exist |
| `@Configuration` | Marks a class as a Spring configuration class |
| `@Bean` | Declares a bean explicitly inside a configuration class |
| `@PostConstruct` | Method called immediately after the bean is initialized |
| `@PreDestroy` | Method called just before the bean is destroyed |

### Additional Concepts Learned

- **Dependency Injection** — components don't create their own dependencies
- **Beans** — Spring-managed objects
- **Constructor Injection** — best practice for DI
- **Interfaces & Implementations** — enables loose coupling
- **Auto Configuration** — automatic setup based on classpath
- **Maven Basics** — build and dependency management tool

---

## Question 2: Scenarios Where Spring Framework is More Useful Than Node.js

### ✅ Enterprise Applications

Spring is widely used in enterprise-level systems that require:
- **Scalability**
- **Security**
- **Maintainability**

Examples: Banking Systems, ERP Systems, E-commerce Backends.

---

### ✅ Strong Type Safety

Java provides:
- **Compile-time error checking** — bugs caught before runtime
- **Better type safety** — critical in large codebases
- **Better maintainability** — easier for large teams to work on

Node.js with TypeScript can achieve this partially, but Java is natively and strictly typed.

---

### ✅ Better Multithreading Support

Java has strong support for:
- **Thread management** — mature and well-tested
- **Concurrency** — built into the language
- **Parallel processing** — ideal for high-performance backends

Node.js is single-threaded (event loop) — CPU-intensive tasks are better handled by Java.

---

### ✅ Mature Ecosystem

Spring provides built-in support for:
- **Security** (Spring Security)
- **Transactions** (Spring Transaction Management)
- **REST APIs** (Spring MVC)
- **Microservices** (Spring Cloud)
- **Dependency Injection** (Spring Core)

---

### ✅ Large Scale Backend Systems

Spring Framework is the right choice when:
- The application is **very large in scope**
- **Multiple developers** are working together
- **Long-term maintainability** is a priority

---

## Question 3: Scenarios Where Spring Boot is More Useful Than Spring Framework

### ✅ Faster Development

Spring Boot eliminates boilerplate configuration — developers can build and ship applications quickly without spending time on manual setup.

---

### ✅ Auto Configuration

Spring Boot automatically configures:
- **Database connections**
- **Server setup**
- **Dependency management**

It inspects the classpath and decides what to configure — no manual XML or Java config needed.

---

### ✅ Embedded Server

Spring Boot includes:
- **Tomcat / Jetty** bundled in by default
- **No WAR file deployment** needed — just run the JAR
- Faster development and deployment cycles

---

### ✅ Easier Dependency Management

Starter dependencies handle everything in one line:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

All related dependencies are pulled in automatically, with versions managed for you.

---

### ✅ Production Ready Features (Actuator)

Spring Boot provides out of the box:
- **Actuator** — exposes application info and health
- **Monitoring** — metrics collection
- **Health Checks** — `/actuator/health` endpoint
- **Metrics** — memory usage, request counts, etc.

---

### ✅ Better for REST APIs & Microservices

Spring Boot is the preferred choice for:
- **REST APIs**
- **Microservices architecture**
- **Cloud-based backends**

Minimal setup, fast configuration, and production features built in.

---

## Question 4: Alice and Her Bakery 🍰

### Problem Statement

> Alice runs a bakery. Her bakery has a `CakeBaker` that bakes cakes. To bake a cake, it needs `Frosting` and `Syrup`.
> Implement this using Spring Boot with Dependency Injection.

---

### Design

```
        ┌─────────────┐
        │  CakeBaker  │
        └──────┬──────┘
               │ depends on
       ┌───────┴────────┐
       ▼                ▼
  ┌──────────┐     ┌──────────┐
  │ Frosting │     │  Syrup   │
  │(Interface)     │(Interface)
  └────┬─────┘     └────┬─────┘
       │                │
  ┌────┴──────────┐ ┌───┴──────────┐
  │ Chocolate     │ │ Chocolate    │
  │ Frosting      │ │ Syrup        │
  ├───────────────┤ ├──────────────┤
  │ Strawberry    │ │ Strawberry   │
  │ Frosting      │ │ Syrup        │
  └───────────────┘ └──────────────┘
```

---

### Code Implementation

#### Interface: Frosting.java
```java
public interface Frosting {
    String getFrostingType();
}
```

#### Interface: Syrup.java
```java
public interface Syrup {
    String getSyrupType();
}
```

---

#### Implementation: ChocolateFrosting.java
```java
@Component
@Qualifier("chocolateFrosting")
public class ChocolateFrosting implements Frosting {
    @Override
    public String getFrostingType() {
        return "Chocolate Frosting";
    }
}
```

#### Implementation: StrawberryFrosting.java
```java
@Component
@Qualifier("strawberryFrosting")
public class StrawberryFrosting implements Frosting {
    @Override
    public String getFrostingType() {
        return "Strawberry Frosting";
    }
}
```

---

#### Implementation: ChocolateSyrup.java
```java
@Component
@Qualifier("chocolateSyrup")
public class ChocolateSyrup implements Syrup {
    @Override
    public String getSyrupType() {
        return "Chocolate Syrup";
    }
}
```

#### Implementation: StrawberrySyrup.java
```java
@Component
@Qualifier("strawberrySyrup")
public class StrawberrySyrup implements Syrup {
    @Override
    public String getSyrupType() {
        return "Strawberry Syrup";
    }
}
```

---

#### Main Class: CakeBaker.java
```java
@Component
public class CakeBaker {

    private final Frosting frosting;
    private final Syrup syrup;

    @Autowired
    public CakeBaker(
        @Qualifier("chocolateFrosting") Frosting frosting,
        @Qualifier("chocolateSyrup") Syrup syrup
    ) {
        this.frosting = frosting;
        this.syrup = syrup;
    }

    public void bakeCake() {
        System.out.println("Cake Baker...");
        System.out.println(frosting.getFrostingType());
        System.out.println(syrup.getSyrupType());
    }
}
```

---

#### Application Entry Point: Module1HomeworkApplication.java
```java
@SpringBootApplication
public class Module1HomeworkApplication {

    public static void main(String[] args) {
        ApplicationContext context =
            SpringApplication.run(Module1HomeworkApplication.class, args);

        CakeBaker cakeBaker = context.getBean(CakeBaker.class);
        cakeBaker.bakeCake();
    }
}
```

---

### Output

```
Cake Baker...
Chocolate Frosting
Chocolate Syrup
```

---

### Concepts Used

| Concept | Where It Was Applied |
|---|---|
| `@Component` | `CakeBaker`, `ChocolateFrosting`, `StrawberryFrosting`, `ChocolateSyrup`, `StrawberrySyrup` |
| `@Qualifier` | Inside `CakeBaker` constructor — to select a specific implementation |
| **Constructor Injection** | `CakeBaker` receives its dependencies via constructor |
| **Interfaces** | `Frosting` and `Syrup` — enables loose coupling |
| **Spring Beans** | All implementations are Spring-managed beans |
| **Dependency Injection** | `CakeBaker` never creates `Frosting` or `Syrup` — Spring injects them |

---

### Switching to Strawberry Cake

Just change the `@Qualifier` in `CakeBaker`:

```java
@Autowired
public CakeBaker(
    @Qualifier("strawberryFrosting") Frosting frosting,  // ← change here
    @Qualifier("strawberrySyrup") Syrup syrup            // ← and here
) { ... }
```

**Output:**
```
Cake Baker...
Strawberry Frosting
Strawberry Syrup
```

This is the power of DI — `CakeBaker`'s logic never changed, only the wiring did.

---

## Conclusion

This module covered the foundations of Spring Boot:

1. **Spring Framework** — makes Java apps loosely coupled via DI
2. **IoC Container** — creates, wires, and manages beans
3. **Beans** — Spring-managed objects with a defined lifecycle
4. **Dependency Injection** — Constructor Injection is the best practice
5. **Interfaces** — allow multiple implementations, `@Qualifier` selects the right one
6. **Spring Boot** — Auto Config + Embedded Server = fast, production-ready development
7. **Maven** — build tool that manages dependencies, compilation, testing, and packaging
