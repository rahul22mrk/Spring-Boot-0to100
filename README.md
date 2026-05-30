<div align="center">

# Spring Boot 0 to 100 🚀

**A structured, module-by-module journey through Spring Framework and Spring Boot**
*From core fundamentals to production-grade backend development*

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)
![Maven](https://img.shields.io/badge/Maven-3.x-red?style=flat-square&logo=apachemaven)
![Modules](https://img.shields.io/badge/Modules%20Completed-2-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)

</div>

---

## 📌 Table of Contents

- [What's in Each Module](#-whats-in-each-module)
- [Repository Structure](#-repository-structure)
- [Module 1 — Spring Introduction](#-module-1--introduction-to-spring--spring-boot)
- [Module 2 — Spring MVC & REST APIs](#-module-2--spring-boot-web-mvc--restful-apis)
- [Tech Stack](#️-tech-stack)
- [Progress Tracker](#-progress-tracker)
- [Getting Started](#-getting-started)

---

## 📦 What's in Each Module

| | What You Get |
|---|---|
| 📄 **Notes PDF** | Full class notes with concepts and diagrams |
| 📝 **Homework PDF** | Problem statements and questions |
| 💻 **Implementation** | Working Spring Boot project — homework solved |

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
│       └── alice-and-her-bakery/          ← Spring Boot project
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

## 📚 Module 1 — Introduction to Spring & Spring Boot

> Core concepts of the Spring ecosystem — understanding how Spring manages objects, wires dependencies, and bootstraps an application automatically.

<details>
<summary><strong>📖 Topics Covered</strong></summary>

<br>

| Topic | What You Learn |
|---|---|
| Spring Framework | What it is, why it exists, Rod Johnson's POJO-based approach (2003) |
| IoC Container | How Spring creates, configures, and manages the full bean lifecycle |
| Beans | Bean scopes (singleton/prototype), `@PostConstruct`, `@PreDestroy` |
| Dependency Injection | Constructor injection vs field injection — why constructor is preferred |
| Interfaces & Implementations | Loose coupling, multiple implementations of the same interface |
| `@Qualifier` | Resolving ambiguity when multiple beans of the same type exist |
| Spring Boot vs Spring | Auto Configuration, Embedded Server, Starter Dependencies |
| Auto Configuration | How classpath scanning + conditional annotations wire everything up |
| Spring Boot Internal Flow | 7-step startup sequence from `main()` to "Application Ready" |
| Maven | `pom.xml`, dependency management, build lifecycle commands |

</details>

<details>
<summary><strong>🏷️ Key Annotations</strong></summary>

<br>

```java
@SpringBootApplication   // Entry point: @Configuration + @EnableAutoConfiguration + @ComponentScan
@Component               // Generic Spring-managed bean
@Autowired               // Inject a dependency
@Qualifier("name")       // Choose a specific bean when multiple exist
@Configuration           // Marks a configuration class
@Bean                    // Declares a bean inside a @Configuration class
@PostConstruct           // Runs after bean is initialized
@PreDestroy              // Runs before bean is destroyed
```

</details>

<details>
<summary><strong>🍰 Homework — Alice and Her Bakery</strong></summary>

<br>

Built a Spring Boot application demonstrating Dependency Injection end-to-end.

**What was built:**

| Class / Interface | Type | Role |
|---|---|---|
| `Frosting` | Interface | Defines `getFrostingType()` |
| `Syrup` | Interface | Defines `getSyrupType()` |
| `ChocolateFrosting` | `@Component` | Implements `Frosting` |
| `StrawberryFrosting` | `@Component` | Implements `Frosting` |
| `ChocolateSyrup` | `@Component` | Implements `Syrup` |
| `StrawberrySyrup` | `@Component` | Implements `Syrup` |
| `CakeBaker` | `@Component` | Receives both via constructor injection + `@Qualifier` |

**Key concept demonstrated:** Swapping from chocolate to strawberry only requires changing `@Qualifier` — zero changes to `CakeBaker`'s logic.

```
Output:
Cake Baker...
Chocolate Frosting
Chocolate Syrup
```

</details>

---

## 📚 Module 2 — Spring Boot Web MVC & RESTful APIs

> Building a complete REST API layer with proper layered architecture — controllers, DTOs, validation, exception handling, and consistent API responses.

<details>
<summary><strong>📖 Topics Covered</strong></summary>

<br>

| Topic | What You Learn |
|---|---|
| REST API Design | Resource-based URLs, HTTP method semantics (GET/POST/PUT/PATCH/DELETE) |
| MVC Layered Architecture | Controller → Service → Repository → Database, why separation matters |
| `@RestController` | Difference from `@Controller`, automatic JSON response via Jackson |
| Request Mappings | `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping` |
| `@PathVariable` vs `@RequestParam` | Path params for resource identity, query params for filtering |
| `@RequestBody` | Mapping incoming JSON to a Java object automatically |
| DTO vs Entity | What you expose to clients vs what gets persisted to the database |
| JPA & H2 | `@Entity`, `JpaRepository`, in-memory H2 for dev/testing |
| Service Layer | Business logic layer — keeps controllers thin |
| Input Validation | 20+ built-in annotations: `@NotBlank`, `@Email`, `@Pattern`, `@Past`, etc. |
| Custom Validation | Building `@PrimeNumberValidation` and `@PasswordValidation` from scratch |
| Exception Handling | `@RestControllerAdvice` + `@ExceptionHandler` for global error handling |
| API Response Wrapping | `ApiResponse<T>` with `data`, `error`, and `timestamp` fields |
| MapStruct | Automatic Entity ↔ DTO mapping with `@Mapper` |

</details>

<details>
<summary><strong>🏷️ Key Annotations</strong></summary>

<br>

```java
// Controller layer
@RestController          // REST controller — returns JSON directly
@RequestMapping          // Base URL mapping for a controller
@GetMapping              // GET /resource
@PostMapping             // POST /resource
@PutMapping              // PUT /resource/{id}
@DeleteMapping           // DELETE /resource/{id}
@PathVariable            // Extract from URL path: /departments/{id}
@RequestParam            // Extract from query string: ?active=true
@RequestBody             // Map JSON body to Java object

// Validation
@Valid                   // Trigger bean validation on request body
@NotNull / @NotBlank     // Null and blank checks
@Min / @Max              // Number range
@Email / @Pattern / @URL // Format checks
@Past / @Future          // Date constraints
@Constraint              // Marks a custom validation annotation

// Exception handling & response
@RestControllerAdvice    // Global exception handler + response transformer
@ExceptionHandler        // Handles a specific exception type
```

</details>

<details>
<summary><strong>🏢 Homework — Department REST API</strong></summary>

<br>

Built a complete REST API for a `Department` entity with validation, exception handling, and a consistent response wrapper.

**REST Endpoints:**

| Method | URL | Action | Status |
|---|---|---|---|
| `GET` | `/departments` | Get all departments | 302 |
| `GET` | `/departments/{id}` | Get department by ID | 302 |
| `POST` | `/departments` | Create new department | 201 |
| `PUT` | `/departments` | Update department | 200 |
| `DELETE` | `/departments/{id}` | Delete department | 200 |

**Project Structure:**

```
src/main/java/.../
├── controllers/DepartmentController.java     ← 5 REST endpoints
├── dtos/DepartmentDTO.java                   ← All 20+ validation annotations
├── entities/Department.java                  ← JPA Entity
├── advices/
│   ├── ApiResponse.java                      ← Wrapper: { data, error, timestamp }
│   ├── ApiError.java                         ← Structured error object
│   └── GlobalExceptionHandler.java           ← Handles 3 exception types globally
├── exceptions/ResourceNotFoundException.java ← Custom 404 exception
├── annotations/
│   ├── PrimeNumberValidation.java            ← Custom annotation interface
│   ├── PrimeNumberValidator.java             ← 6k±1 prime checking algorithm
│   ├── PasswordValidation.java               ← Custom annotation interface
│   └── PasswordValidator.java                ← Regex: uppercase + lowercase + special + length
└── mappers/DepartmentMapper.java             ← MapStruct: Entity ↔ DTO
```

**Custom Validators built:**

| Annotation | Rule |
|---|---|
| `@PrimeNumberValidation` | Integer must be a prime number (2, 3, 5, 7, 11...) |
| `@PasswordValidation` | Min 10 chars + at least one uppercase + lowercase + special character |

</details>

---

## 🛠️ Tech Stack

| Tool | Purpose |
|---|---|
| **Java 17+** | Primary language |
| **Spring Boot 3.x** | Application framework |
| **Spring Web MVC** | REST API layer |
| **Spring Data JPA** | Database interaction |
| **Hibernate Validator** | Bean validation (`@Valid`, `@NotBlank`, etc.) |
| **H2 Database** | In-memory DB for development and testing |
| **MapStruct** | Compile-time Entity ↔ DTO mapping |
| **Lombok** | Reduces boilerplate (`@Getter`, `@Builder`, `@Data`) |
| **Maven** | Build tool and dependency management |
| **Jackson** | JSON serialization / deserialization |

---

## 📈 Progress Tracker

### ✅ Completed

| # | Module | Key Topics |
|---|---|---|
| 1 | Spring Introduction | IoC, DI, Beans, Auto Configuration, Maven |
| 2 | Spring MVC & REST APIs | REST, Validation, Exception Handling, Custom Annotations |

### 🔄 Upcoming

| # | Topic | Description |
|---|---|---|
| 3 | Spring Data JPA | Full DB integration with JPA and Hibernate |
| 4 | Database Integration | MySQL / PostgreSQL with Spring Boot |
| 5 | Spring Security | Authentication and authorization |
| 6 | JWT Authentication | Stateless token-based auth |
| 7 | Docker | Containerizing Spring Boot applications |
| 8 | Microservices | Service decomposition, inter-service communication |
| 9 | System Design | Designing scalable backend systems |

**Overall Progress:**

```
Module 1  ██████████  ✅ Complete
Module 2  ██████████  ✅ Complete
Module 3  ░░░░░░░░░░  🔄 Upcoming
Module 4  ░░░░░░░░░░  🔄 Upcoming
Module 5  ░░░░░░░░░░  🔄 Upcoming
```

---

## 🚀 Getting Started

Each module's homework is a standalone Spring Boot project.

```bash
# 1. Clone the repository
git clone https://github.com/rahul22mrk/Spring-Boot-0to100.git
cd Spring-Boot-0to100

# 2. Navigate to any module's homework
cd module-2-spring-boot-mvc-and-RESTful-APIs/homework

# 3. Run with Maven
mvn spring-boot:run
```

> **Prerequisites:** Java 17+, Maven 3.x

---

<div align="center">

**[⬆ Back to Top](#spring-boot-0-to-100-)**

</div>
