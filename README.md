# Spring Boot 0 to 100 — Cohort 5.0 🚀

> **Coding Shuttle · Spring Boot 0 to 100 Cohort 5.0 [AI + DevOps + System Design]**  
> A complete module-by-module journey from Spring fundamentals to production-grade backend engineering.

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.x-red?style=flat-square&logo=apachemaven)](https://maven.apache.org)
[![Modules](https://img.shields.io/badge/Modules%20Completed-2%2F20-blue?style=flat-square)]()
[![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)]()

---

## 📊 Progress Overview

```
Module  1  ██████████  ✅ Completed   — Introduction To Spring & Spring Boot
Module  2  ██████████  ✅ Completed   — Spring Boot MVC And RESTful APIs
Module  3  ▓░░░░░░░░░  🔄 In Progress — Hibernate And Spring Boot Data JPA
Module  4  ░░░░░░░░░░  ⏳ Upcoming    — Production Ready Spring Boot Features
Module  5  ░░░░░░░░░░  ⏳ Upcoming    — Spring Security Fundamentals
Module  6  ░░░░░░░░░░  ⏳ Upcoming    — Spring Security Advanced
Module  7  ░░░░░░░░░░  🔒 Locked      — Spring Boot Testing
Module  8  ░░░░░░░░░░  🔒 Locked      — Spring Boot Deployment With CI/CD
Module  9  ░░░░░░░░░░  🔒 Locked      — Spring AI, RAG And Tool Calling
Module 10  ░░░░░░░░░░  🔒 Locked      — Aspect-Oriented Programming
Module 11  ░░░░░░░░░░  🔒 Locked      — Caching And Concurrent Transaction Management
Module 12  ░░░░░░░░░░  🔒 Locked      — Introduction To Microservice Architecture
Module 13  ░░░░░░░░░░  🔒 Locked      — Advanced Microservice Concepts
Module 14  ░░░░░░░░░░  🔒 Locked      — Apache Kafka In Spring Boot
Module 15  ░░░░░░░░░░  🔒 Locked      — Docker With Spring Boot
Module 16  ░░░░░░░░░░  🔒 Locked      — Kubernetes Components
Module 17  ░░░░░░░░░░  🔒 Locked      — Kubernetes Advanced
Module 18  ░░░░░░░░░░  🔒 Locked      — Java Multithreading And Async Task Scheduling
Module 19  ░░░░░░░░░░  🔒 Locked      — Reactive Programming Basics
Module 20  ░░░░░░░░░░  🔒 Locked      — Reactive Programming Advanced
```

**Overall: 2 / 20 modules complete (10%)**

---

## 📂 Repository Structure

```
Spring-Boot-0to100/
│
├── module-1-spring-introduction/
│   ├── notes/
│   │   ├── Introduction_To_Spring_Boot_Full_Week_Notes.pdf
│   │   └── Spring_Boot_Intro_Homework.pdf
│   └── homework/
│       └── alice-and-her-bakery/
│
├── module-2-spring-boot-mvc-and-RESTful-APIs/
│   ├── notes/
│   │   ├── Spring_Boot_Web_MVC_Full_Notes.pdf
│   │   └── Web_MVC_Homework.pdf
│   └── homework/
│       └── src/main/java/.../
│           ├── controllers/
│           ├── dtos/
│           ├── entities/
│           ├── advices/
│           ├── exceptions/
│           ├── annotations/
│           └── mappers/
│
└── README.md
```

---

## 📦 What's in Each Module

| | What You Get |
|---|---|
| 📄 **Notes PDF** | Full class notes with concepts and diagrams |
| 📝 **Homework PDF** | Problem statements and questions |
| 💻 **Implementation** | Working Spring Boot project — homework solved |

---

## 📚 Module Breakdown

<details>
<summary><b>✅ Module 1 — Introduction To Spring & Spring Boot</b> &nbsp;|&nbsp; Deadline: 1/6/2026</summary>

<br>

**Topics Covered**

| Topic | What You Learn |
|---|---|
| Spring Framework | What it is, why it exists, Rod Johnson's POJO-based approach (2003) |
| IoC Container | How Spring creates, configures, and manages the full bean lifecycle |
| Beans | Bean scopes (singleton/prototype), `@PostConstruct`, `@PreDestroy` |
| Dependency Injection | Constructor injection vs field injection — why constructor is preferred |
| `@Qualifier` | Resolving ambiguity when multiple beans of the same type exist |
| Spring Boot vs Spring | Auto Configuration, Embedded Server, Starter Dependencies |
| Maven | `pom.xml`, dependency management, build lifecycle commands |

**Key Annotations**

```java
@SpringBootApplication   // Entry point
@Component               // Generic Spring-managed bean
@Autowired               // Inject a dependency
@Qualifier("name")       // Choose a specific bean when multiple exist
@Bean                    // Declares a bean inside a @Configuration class
@PostConstruct           // Runs after bean is initialized
@PreDestroy              // Runs before bean is destroyed
```

**🍰 Homework — Alice and Her Bakery**

Built a Spring Boot app demonstrating Dependency Injection end-to-end.

| Class / Interface | Type | Role |
|---|---|---|
| `Frosting` | Interface | Defines `getFrostingType()` |
| `Syrup` | Interface | Defines `getSyrupType()` |
| `ChocolateFrosting` | `@Component` | Implements `Frosting` |
| `StrawberryFrosting` | `@Component` | Implements `Frosting` |
| `CakeBaker` | `@Component` | Receives both via constructor injection + `@Qualifier` |

</details>

---

<details>
<summary><b>✅ Module 2 — Spring Boot MVC And RESTful APIs</b> &nbsp;|&nbsp; Deadline: 6/6/2026</summary>

<br>

**Topics Covered**

| Topic | What You Learn |
|---|---|
| REST API Design | Resource-based URLs, HTTP method semantics |
| MVC Layered Architecture | Controller → Service → Repository → Database |
| `@RestController` | Automatic JSON response via Jackson |
| Request Mappings | `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` |
| DTO vs Entity | What you expose to clients vs what gets persisted |
| Input Validation | 20+ built-in annotations: `@NotBlank`, `@Email`, `@Pattern`, etc. |
| Custom Validation | Building `@PrimeNumberValidation` and `@PasswordValidation` |
| Exception Handling | `@RestControllerAdvice` + `@ExceptionHandler` |
| API Response Wrapping | `ApiResponse<T>` with `data`, `error`, `timestamp` |
| MapStruct | Automatic Entity ↔ DTO mapping |

**Key Annotations**

```java
@RestController          // REST controller — returns JSON directly
@RequestMapping          // Base URL mapping for a controller
@PathVariable            // Extract from URL path: /departments/{id}
@RequestParam            // Extract from query string: ?active=true
@RequestBody             // Map JSON body to Java object
@Valid                   // Trigger bean validation on request body
@RestControllerAdvice    // Global exception handler
@ExceptionHandler        // Handles a specific exception type
```

**🏢 Homework — Department REST API**

| Method | URL | Action | Status |
|---|---|---|---|
| `GET` | `/departments` | Get all departments | 302 |
| `GET` | `/departments/{id}` | Get department by ID | 302 |
| `POST` | `/departments` | Create new department | 201 |
| `PUT` | `/departments` | Update department | 200 |
| `DELETE` | `/departments/{id}` | Delete department | 200 |

</details>

---

<details>
<summary><b>🔄 Module 3 — Hibernate And Spring Boot Data JPA</b> &nbsp;|&nbsp; Deadline: 11/6/2026</summary>

<br>

> 🚧 Currently in progress

**Topics to be covered:** ORM, Hibernate, JPQL, Entity Relationships (`@OneToMany`, `@ManyToOne`, `@ManyToMany`), Spring Data JPA repositories, Lazy vs Eager loading, Transactions.

</details>

---

<details>
<summary><b>⏳ Module 4 — Production Ready Spring Boot Features</b> &nbsp;|&nbsp; Deadline: 18/6/2026</summary>

<br>

> ⏳ Not yet started

**Topics to be covered:** Spring Boot Actuator, Application Profiles, Logging (SLF4J + Logback), Health checks, Metrics, Environment-based configuration.

</details>

---

<details>
<summary><b>⏳ Module 5 — Spring Security Fundamentals</b> &nbsp;|&nbsp; Deadline: 23/6/2026</summary>

<br>

> ⏳ Not yet started

**Topics to be covered:** Authentication vs Authorization, Security Filter Chain, `UserDetailsService`, Password encoding, Form-based login, HTTP Basic Auth.

</details>

---

<details>
<summary><b>⏳ Module 6 — Spring Security Advanced</b> &nbsp;|&nbsp; Deadline: 28/6/2026</summary>

<br>

> ⏳ Not yet started

**Topics to be covered:** JWT tokens, OAuth2, Role-based access control (RBAC), Method-level security (`@PreAuthorize`), Refresh tokens.

</details>

---

<details>
<summary><b>🔒 Module 7 — Spring Boot Testing</b> &nbsp;|&nbsp; Deadline: 8/7/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Unit testing with JUnit 5, Mockito, `@WebMvcTest`, `@DataJpaTest`, `@SpringBootTest`, Integration testing, Test slices.

</details>

---

<details>
<summary><b>🔒 Module 8 — Spring Boot Deployment With CI/CD</b> &nbsp;|&nbsp; Deadline: 13/7/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** GitHub Actions, Dockerizing Spring Boot apps, Deploy pipelines, Environment variables in CI, Automated testing in pipeline.

</details>

---

<details>
<summary><b>🔒 Module 9 — Spring AI, RAG And Tool Calling</b> &nbsp;|&nbsp; Deadline: 19/7/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Spring AI integration, LLM API calls, Retrieval-Augmented Generation (RAG), Tool/function calling, Embeddings, Vector stores.

</details>

---

<details>
<summary><b>🔒 Module 10 — Aspect-Oriented Programming</b> &nbsp;|&nbsp; Deadline: 23/7/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** AOP concepts, Pointcuts, Advices (`@Before`, `@After`, `@Around`), Cross-cutting concerns (logging, security, transactions).

</details>

---

<details>
<summary><b>🔒 Module 11 — Caching And Concurrent Transaction Management</b> &nbsp;|&nbsp; Deadline: 28/7/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Redis caching, `@Cacheable`, `@CacheEvict`, `@Transactional`, Isolation levels, Concurrency issues (dirty read, phantom read).

</details>

---

<details>
<summary><b>🔒 Module 12 — Introduction To Microservice Architecture</b> &nbsp;|&nbsp; Deadline: 1/8/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Monolith vs Microservices, Service decomposition, REST-based inter-service communication, Service boundaries.

</details>

---

<details>
<summary><b>🔒 Module 13 — Advanced Microservice Concepts</b> &nbsp;|&nbsp; Deadline: 6/8/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** API Gateway, Service Discovery (Eureka), Circuit Breaker (Resilience4j), Load balancing, Distributed tracing.

</details>

---

<details>
<summary><b>🔒 Module 14 — Apache Kafka In Spring Boot</b> &nbsp;|&nbsp; Deadline: 13/8/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Event-driven architecture, Kafka producers and consumers, Topics, Partitions, Consumer groups, Spring Kafka.

</details>

---

<details>
<summary><b>🔒 Module 15 — Docker With Spring Boot</b> &nbsp;|&nbsp; Deadline: 18/8/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Dockerfile for Spring Boot, docker-compose, Multi-stage builds, Container networking, Docker Hub.

</details>

---

<details>
<summary><b>🔒 Module 16 — Kubernetes Components</b> &nbsp;|&nbsp; Deadline: 20/8/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Pods, ReplicaSets, Deployments, Services, ConfigMaps, Secrets, Namespaces, `kubectl` commands.

</details>

---

<details>
<summary><b>🔒 Module 17 — Kubernetes Advanced</b> &nbsp;|&nbsp; Deadline: 25/8/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Helm charts, Ingress controllers, Horizontal Pod Autoscaler, StatefulSets, Persistent Volumes, Rolling deployments.

</details>

---

<details>
<summary><b>🔒 Module 18 — Java Multithreading And Async Task Scheduling</b> &nbsp;|&nbsp; Deadline: 30/8/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Java threads, `CompletableFuture`, `@Async`, `ThreadPoolTaskExecutor`, `@Scheduled`, Task scheduling in Spring Boot.

</details>

---

<details>
<summary><b>🔒 Module 19 — Reactive Programming Basics</b> &nbsp;|&nbsp; Deadline: 29/12/2026</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** Reactive paradigm, Project Reactor, `Mono`, `Flux`, Backpressure, WebFlux basics.

</details>

---

<details>
<summary><b>🔒 Module 20 — Reactive Programming Advanced</b> &nbsp;|&nbsp; Deadline: 8/1/2027</summary>

<br>

> 🔒 Not unlocked yet

**Topics to be covered:** R2DBC (reactive DB access), Advanced WebFlux, Reactive pipelines, Testing reactive streams.

</details>

---

## 🛠️ Tech Stack

| Tool | Purpose |
|---|---|
| **Java 17+** | Primary language |
| **Spring Boot 3.x** | Application framework |
| **Spring Web MVC** | REST API layer |
| **Spring Data JPA** | Database interaction |
| **Hibernate Validator** | Bean validation |
| **H2 Database** | In-memory DB for dev/testing |
| **MapStruct** | Entity ↔ DTO mapping |
| **Lombok** | Boilerplate reduction |
| **Maven** | Build tool |
| **Jackson** | JSON serialization |
| **Docker** | Containerization *(upcoming)* |
| **Kubernetes** | Orchestration *(upcoming)* |
| **Apache Kafka** | Event streaming *(upcoming)* |
| **Spring AI** | AI + RAG integration *(upcoming)* |

---

## 🚀 Getting Started

```bash
# 1. Clone the repo
git clone https://github.com/rahul22mrk/Spring-Boot-0to100.git
cd Spring-Boot-0to100

# 2. Go to any module's homework
cd module-2-spring-boot-mvc-and-RESTful-APIs/homework

# 3. Run
mvn spring-boot:run
```

> **Prerequisites:** Java 17+, Maven 3.x

---

**Course:** [Spring Boot 0 to 100 Cohort 5.0 — Coding Shuttle](https://app.codingshuttle.com)  
**[⬆ Back to Top](#spring-boot-0-to-100--cohort-50-)**
