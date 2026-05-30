# Module 2 - Spring Boot Web MVC & RESTful APIs: Notes

---

## 2.1 Spring Boot Web & REST APIs

### What is a REST API?

REST (Representational State Transfer) APIs are a set of rules and conventions for building and interacting with web services.

REST follows a **resource-based** approach — every URL represents a resource, and HTTP methods define the action.

### Standard REST Endpoint Pattern

| HTTP Method | URL | Action |
|---|---|---|
| `GET` | `/users` | Retrieve all users |
| `GET` | `/users/{id}` | Retrieve a specific user by ID |
| `POST` | `/users` | Create a new user |
| `PUT` | `/users/{id}` | Fully update an existing user |
| `PATCH` | `/users/{id}` | Partially update an existing user |
| `DELETE` | `/users/{id}` | Delete a user |

### spring-boot-starter-web

Adding this one dependency gives you everything needed for building REST APIs:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

It bundles: `spring-boot-starter`, `jackson` (JSON conversion), `spring-core`, `spring-mvc`, `spring-boot-starter-tomcat`.

---

### Spring Boot MVC Architecture

Spring Boot follows a **Layered Architecture**:

```
Client → Controller → Service → Repository → Database
                  ↕          ↕
                 DTO       Entity
```

### Why Use MVC / Layered Architecture?

| Benefit | What It Means |
|---|---|
| **Separation of Concerns** | Each layer has one job — controller handles requests, service has logic, repository talks to DB |
| **Reusability** | Service and repository layers can be reused across different controllers |
| **Testability** | Each layer can be unit tested independently |
| **Scalability** | Layers can be scaled or replaced independently |

---

## 2.2 Presentation Layer (Controllers)

### @Controller vs @RestController

| Annotation | Use Case |
|---|---|
| `@Controller` | Returns a View (HTML page via template engine) |
| `@RestController` | Returns JSON/XML directly — shorthand for `@Controller` + `@ResponseBody` |

For REST APIs, always use `@RestController`.

### Request Mapping Annotations

`@RequestMapping` maps HTTP requests to controller methods. Shortcut variants:

```java
@GetMapping("/users")        // GET request
@PostMapping("/users")       // POST request
@PutMapping("/users/{id}")   // PUT request
@DeleteMapping("/users/{id}")// DELETE request
@PatchMapping("/users/{id}") // PATCH request
```

### Dynamic URL Paths

#### @PathVariable — Part of the URL
```java
// URL: /employees/123
@GetMapping("/employees/{id}")
public Employee getEmployee(@PathVariable Long id) { ... }
```
Use when the parameter **identifies a resource** — it's a core part of the URL path.

#### @RequestParam — Query String
```java
// URL: /employees?department=IT&active=true
@GetMapping("/employees")
public List<Employee> getEmployees(@RequestParam String department) { ... }
```
Use when the parameter is **optional** or used for filtering, sorting, or pagination.

### @RequestBody

Maps the HTTP request body (JSON/XML) to a Java object using Jackson.

```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) {
    // user object is auto-populated from the JSON body
}
```

Used in `POST`, `PUT`, and `PATCH` requests where the client sends data.

---

## 2.3 Persistence Layer & JPA

### DTO vs Entity

| | Entity | DTO (Data Transfer Object) |
|---|---|---|
| **Purpose** | Maps to a database table | Transfers data between layers |
| **Used in** | Repository layer | Controller / Service layer |
| **Annotation** | `@Entity` | Plain POJO |
| **Exposes** | All DB fields | Only what the client needs |

### H2 Database (for Development/Testing)

H2 is a lightweight in-memory database — great for development without needing a real DB setup.

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

Access the console at: `http://localhost:8080/h2-console`

### @Entity Annotation

Marks a class as a JPA entity — maps it to a database table.

```java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Boolean isActive;
    private Date createdAt;
}
```

Key points:
- **Class-level annotation** — applies to the whole class
- **Primary Key** — must have a field annotated with `@Id`
- **Automatic Table Mapping** — class name = table name (configurable)

### JpaRepository Interface

Provides all CRUD operations out of the box — no SQL needed.

```java
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // Built-in: findAll(), findById(), save(), deleteById(), count(), etc.
    // Custom queries can also be added here
}
```

Key points:
- **Generic Interface** — `JpaRepository<EntityType, IdType>`
- **Predefined Methods** — full CRUD without writing any implementation
- **Custom Queries** — add method names or `@Query` for custom SQL

---

## 2.4 Service Layer

### What Does the Service Layer Do?

```
Controller  →  Service  →  Repository  →  Database
```

The service layer is the **brain** of the application:

- Acts as a **bridge** between the controller (presentation layer) and repository (persistence layer)
- Contains all the **business logic** — validations, calculations, rules
- **Orchestrates** interactions between different components
- Keeps controllers **thin** — controllers just receive/send data, service decides what to do with it

### Benefits

- **Modularity** — logic is in one place
- **Maintainability** — easy to change business rules without touching controllers
- **Scalability** — service can be extracted into a microservice later

```java
@Service
public class DepartmentService {
    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    public Department getDepartment(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }
}
```

---

## 2.5 PUT, PATCH and DELETE Mappings

| Method | Purpose | Request Body |
|---|---|---|
| `PUT` | Full replacement of a resource | Full object required |
| `PATCH` | Partial update of a resource | Only changed fields |
| `DELETE` | Remove a resource | Usually none |

```java
@PutMapping("/{id}")
public ResponseEntity<Department> update(@PathVariable Long id, @RequestBody Department dept) { ... }

@PatchMapping("/{id}")
public ResponseEntity<Department> partialUpdate(@PathVariable Long id, @RequestBody Map<String, Object> updates) { ... }

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) { ... }
```

---

## 2.6 Input Validation

Add the validation starter:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Use `@Valid` on the `@RequestBody` parameter to trigger validation.

### Complete Validation Annotations Reference

#### Null Checks
| Annotation | Description |
|---|---|
| `@Null` | Field must be null |
| `@NotNull` | Field must not be null |
| `@NotEmpty` | Not null + size > 0 (strings, collections, arrays) |
| `@NotBlank` | Not null + trimmed length > 0 (strings only) |

#### Boolean
| Annotation | Description |
|---|---|
| `@AssertTrue` | Boolean field must be `true` |
| `@AssertFalse` | Boolean field must be `false` |

#### Number Constraints
| Annotation | Description |
|---|---|
| `@Min(value)` | Number must be ≥ value |
| `@Max(value)` | Number must be ≤ value |
| `@DecimalMin(value)` | Decimal number ≥ value |
| `@DecimalMax(value)` | Decimal number ≤ value |
| `@Positive` | Must be > 0 |
| `@PositiveOrZero` | Must be ≥ 0 |
| `@Negative` | Must be < 0 |
| `@NegativeOrZero` | Must be ≤ 0 |
| `@Digits(integer, fraction)` | Max integer and fraction digits |
| `@Range(min, max)` | Number within a range (Hibernate) |

#### String Constraints
| Annotation | Description |
|---|---|
| `@Size(min, max)` | Length within range |
| `@Length(min, max)` | Length within range (Hibernate) |
| `@Pattern(regexp)` | Must match regex |
| `@Email` | Valid email format |
| `@URL` | Valid URL format (Hibernate) |
| `@CreditCardNumber` | Valid credit card number (Hibernate) |

#### Date Constraints
| Annotation | Description |
|---|---|
| `@Past` | Date must be in the past |
| `@PastOrPresent` | Date must be past or today |
| `@Future` | Date must be in the future |
| `@FutureOrPresent` | Date must be future or today |

#### Nested Object
| Annotation | Description |
|---|---|
| `@Valid` | Triggers validation on a nested object recursively |

### Handling Validation Exceptions

When `@Valid` fails, Spring throws `MethodArgumentNotValidException`.

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
    Map<String, List<String>> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
        ));
    // return error response
}
```

### Custom Validation Annotations

When built-in annotations aren't enough, create your own:

**Step 1: Create the annotation interface**
```java
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MyValidator.class})
public @interface MyValidation {
    String message() default "Validation failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**Step 2: Create the validator class**
```java
public class MyValidator implements ConstraintValidator<MyValidation, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // your validation logic here
        return value != null && value.matches("your-regex");
    }
}
```

---

## 2.7 Exception Handling in Spring Boot MVC

### Why Handle Exceptions?

- Prevent application crashes
- Provide user-friendly error responses (not stack traces)
- Facilitate debugging and maintenance
- Ensure **consistent error handling** across the entire application

### How to Handle Exceptions

#### Option 1: @ExceptionHandler (in a specific controller)
```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}
```

#### Option 2: @RestControllerAdvice (global — recommended)
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError error = ApiError.builder()
            .message(ex.getMessage())
            .status(HttpStatus.NOT_FOUND)
            .build();
        return new ResponseEntity<>(new ApiResponse<>(error), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        ApiError error = ApiError.builder()
            .message(ex.getMessage())
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
        return new ResponseEntity<>(new ApiResponse<>(error), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### Custom Exception

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

---

## 2.8 Transforming API Response

### Why Use a Wrapper Response?

Instead of returning raw data or raw errors, wrap everything in a consistent structure:

```json
{
  "data": { ... },
  "error": null,
  "timeStamp": "10:30:00 31-05-2026"
}
```

### How to Implement

- Extend `@ResponseBodyAdvice<Object>` to define a custom return type for all responses
- Use `@RestControllerAdvice` for global response transformation
- Include `timestamp`, `data`, and `error` fields

```java
@Getter @Setter @NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private ApiError error;
    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private LocalDateTime timeStamp = LocalDateTime.now();

    public ApiResponse(T data) { this(); this.data = data; }
    public ApiResponse(ApiError error) { this(); this.error = error; }
}
```

---

## Annotations Learned in Module 2

### Controller & Mapping Annotations
| Annotation | Purpose |
|---|---|
| `@RestController` | Marks class as REST controller — returns JSON |
| `@Controller` | Marks class as MVC controller — returns View |
| `@RequestMapping` | Maps URL + HTTP method to a controller |
| `@GetMapping` | Maps HTTP GET requests |
| `@PostMapping` | Maps HTTP POST requests |
| `@PutMapping` | Maps HTTP PUT requests |
| `@DeleteMapping` | Maps HTTP DELETE requests |
| `@PatchMapping` | Maps HTTP PATCH requests |

### Request Data Annotations
| Annotation | Purpose |
|---|---|
| `@PathVariable` | Extracts value from URL path `/users/{id}` |
| `@RequestParam` | Extracts value from query string `/users?name=x` |
| `@RequestBody` | Binds JSON/XML request body to a Java object |
| `@ResponseBody` | Writes return value directly to HTTP response body |

### Persistence / JPA Annotations
| Annotation | Purpose |
|---|---|
| `@Entity` | Marks class as a JPA entity (database table) |
| `@Id` | Marks the primary key field |
| `@GeneratedValue` | Auto-generates the primary key value |

### Exception Handling Annotations
| Annotation | Purpose |
|---|---|
| `@RestControllerAdvice` | Global exception handler + response transformer |
| `@ExceptionHandler` | Handles a specific exception type |

### Validation Annotations
| Annotation | Purpose |
|---|---|
| `@Valid` | Triggers bean validation on the annotated object |
| `@Constraint` | Marks an annotation as a custom validation constraint |

### Lombok Annotations (used in project)
| Annotation | Purpose |
|---|---|
| `@Data` | Generates getters, setters, toString, equals, hashCode |
| `@Builder` | Generates a builder pattern |
| `@Getter` / `@Setter` | Generate getters/setters |
| `@NoArgsConstructor` | Generates no-arg constructor |
| `@AllArgsConstructor` | Generates all-arg constructor |

---

## Key Concepts Summary

| Concept | One Line |
|---|---|
| **REST API** | Resource-based web API using HTTP methods (GET/POST/PUT/DELETE) |
| **@RestController** | Returns JSON directly — no view rendering |
| **@PathVariable** | Extracts resource ID from URL path |
| **@RequestParam** | Extracts optional filters from query string |
| **@RequestBody** | Maps JSON request body to Java object |
| **DTO** | Data Transfer Object — what you expose to the client |
| **Entity** | JPA-mapped class — what gets stored in the database |
| **Service Layer** | Contains business logic, sits between controller and repository |
| **JpaRepository** | Interface with built-in CRUD methods — no SQL needed |
| **@Valid** | Triggers input validation on request body |
| **@RestControllerAdvice** | Global handler for exceptions and response formatting |
| **Custom Validation** | Create `@interface` + `ConstraintValidator` for custom rules |
| **ApiResponse wrapper** | Consistent response structure with data, error, timestamp |
