# Module 2 - Homework

---

## Question 1: List of All Annotations Learned in Spring Web Framework

### Controller & Mapping
| Annotation | Purpose |
|---|---|
| `@RestController` | Marks a class as a REST controller — all methods return JSON/XML |
| `@Controller` | Marks a class as an MVC controller — returns a View |
| `@RequestMapping` | Maps a URL + HTTP method to a controller/method |
| `@GetMapping` | Shortcut for GET requests |
| `@PostMapping` | Shortcut for POST requests |
| `@PutMapping` | Shortcut for PUT requests |
| `@DeleteMapping` | Shortcut for DELETE requests |
| `@PatchMapping` | Shortcut for PATCH requests |

### Request Data
| Annotation | Purpose |
|---|---|
| `@PathVariable` | Extracts value from URL path `/departments/{id}` |
| `@RequestParam` | Extracts value from query string `/departments?active=true` |
| `@RequestBody` | Binds JSON/XML from request body to a Java object |
| `@ResponseBody` | Writes return value directly to HTTP response |

### Persistence / JPA
| Annotation | Purpose |
|---|---|
| `@Entity` | Marks class as a JPA entity mapped to a DB table |
| `@Id` | Marks the primary key field |
| `@GeneratedValue` | Auto-generates primary key value |

### Exception Handling & Response
| Annotation | Purpose |
|---|---|
| `@RestControllerAdvice` | Global exception handler and response transformer |
| `@ExceptionHandler` | Handles a specific exception type |

### Validation
| Annotation | Purpose |
|---|---|
| `@Valid` | Triggers bean validation on request body |
| `@Constraint` | Marks an annotation as a custom validation constraint |
| `@NotNull`, `@NotBlank`, `@NotEmpty` | Null/empty checks |
| `@Min`, `@Max`, `@DecimalMin`, `@DecimalMax` | Number range checks |
| `@Positive`, `@PositiveOrZero`, `@Negative`, `@NegativeOrZero` | Sign checks |
| `@Size`, `@Length`, `@Digits` | Size/length checks |
| `@Pattern`, `@Email`, `@URL`, `@CreditCardNumber` | Format checks |
| `@Past`, `@PastOrPresent`, `@Future`, `@FutureOrPresent` | Date checks |
| `@AssertTrue`, `@AssertFalse` | Boolean checks |
| `@Range` | Number within a range (Hibernate) |
| `@Null` | Field must be null |

### Lombok
| Annotation | Purpose |
|---|---|
| `@Data` | Generates getters, setters, toString, equals, hashCode |
| `@Builder` | Generates builder pattern |
| `@Getter` / `@Setter` | Generate getters/setters |
| `@NoArgsConstructor` | No-argument constructor |
| `@AllArgsConstructor` | All-argument constructor |

---

## Question 2: REST Endpoints for Department Entity

### Entity: Department

```java
public class Department {
    private String id;
    private String title;
    private Boolean isActive;
    private Date createdAt;
}
```

### DepartmentController.java

```java
@RestController
@RequestMapping("/departments")
public class DepartmentController {

    // GET /departments — get all departments
    @GetMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> departments() {
        DepartmentDTO dto = new DepartmentDTO();
        return new ResponseEntity<>(new ApiResponse<>(dto), HttpStatus.FOUND);
    }

    // GET /departments/{deptId} — get one department by ID
    @GetMapping("/{deptId}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartment(
            @PathVariable("deptId") String id) {
        return new ResponseEntity<>(new ApiResponse<>(new DepartmentDTO()), HttpStatus.FOUND);
    }

    // POST /departments — create a new department
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(
            @Valid @RequestBody DepartmentDTO dto) {
        return new ResponseEntity<>(new ApiResponse<>(dto), HttpStatus.CREATED);
    }

    // PUT /departments — update a department
    @PutMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(
            @Valid @RequestBody DepartmentDTO dto) {
        return new ResponseEntity<>(new ApiResponse<>(dto), HttpStatus.OK);
    }

    // DELETE /departments/{deptId} — delete a department
    @DeleteMapping("/{deptId}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(
            @PathVariable("deptId") String id) {
        return new ResponseEntity<>(new ApiResponse<>("Deleted: " + id), HttpStatus.OK);
    }
}
```

### Endpoints Summary

| Method | URL | Action | Status Code |
|---|---|---|---|
| `GET` | `/departments` | Get all departments | 302 Found |
| `GET` | `/departments/{id}` | Get department by ID | 302 Found |
| `POST` | `/departments` | Create new department | 201 Created |
| `PUT` | `/departments` | Update department | 200 OK |
| `DELETE` | `/departments/{id}` | Delete department | 200 OK |

---

## Question 3: Exception Handling for Department

### Custom Exception: ResourceNotFoundException.java

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### Structured Error Response: ApiError.java

```java
@Data
@Builder
public class ApiError {
    private HttpStatus status;
    private String message;
    private Map<String, List<String>> subErrors;  // for validation errors
    private Integer errorCount;
}
```

### Wrapper Response: ApiResponse.java

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

### Global Exception Handler: GlobalExceptionHandler.java

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles custom ResourceNotFoundException (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();
        return buildErrorResponse(apiError);
    }

    // Handles input validation failures (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidation(
            MethodArgumentNotValidException ex) {

        Map<String, List<String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Input validation failed")
                .subErrors(errors)
                .errorCount(ex.getBindingResult().getErrorCount())
                .build();

        return buildErrorResponse(apiError);
    }

    // Handles all other exceptions (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        return buildErrorResponse(apiError);
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponse(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }
}
```

### How It Works

```
Request comes in → Controller called → Exception thrown
        ↓
@RestControllerAdvice intercepts
        ↓
Matching @ExceptionHandler runs
        ↓
Returns ApiResponse<ApiError> with appropriate HTTP status
```

---

## Question 4: All Validation Annotations on DepartmentDTO

DepartmentDTO is designed to demonstrate every validation annotation. Each field is chosen to match the constraint it demonstrates.

```java
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class DepartmentDTO {

    @NotBlank
    @Length(min = 1, max = 10)
    @Min(value = 5)
    @Max(value = 10)
    private String id;

    @NotBlank
    private String title;

    @NotNull
    private Boolean isActive;

    @CreditCardNumber
    private String creditCardNumber;

    @URL
    private String url;

    @Email
    private String email;

    @Digits(integer = 8, fraction = 2)
    private Integer digit;

    @Range(min = 1L, max = 1000L)
    private Double amount;

    @Null
    private Long tempId;

    @PastOrPresent
    @NotNull
    private Date doj;           // Date of Joining

    @Past
    @NotNull
    private Date dob;           // Date of Birth

    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String mobileNumber;

    @Future
    private LocalDate contractEndDate;

    @FutureOrPresent
    private LocalDate joiningDate;

    @AssertFalse
    private Boolean terminated;

    @PrimeNumberValidation
    private Integer primeNumber;

    @PasswordValidation
    private String password;
}
```

### Annotation to Field Mapping

| Annotation | Field | Why This Field |
|---|---|---|
| `@NotBlank` | `id`, `title` | Cannot be empty string |
| `@NotNull` | `isActive`, `doj`, `dob` | Must be provided |
| `@Null` | `tempId` | Server-generated, client must not send |
| `@Min` / `@Max` | `id` | Value range constraint |
| `@Length` | `id` | String length range (Hibernate) |
| `@Range` | `amount` | Numeric range (Hibernate) |
| `@Email` | `email` | Must be valid email format |
| `@URL` | `url` | Must be a valid URL |
| `@CreditCardNumber` | `creditCardNumber` | Luhn algorithm validation |
| `@Pattern` | `mobileNumber` | Indian mobile: starts with 6-9, 10 digits |
| `@Digits` | `digit` | Max 8 integer digits, 2 fraction digits |
| `@Past` | `dob` | Date of birth must be in the past |
| `@PastOrPresent` | `doj` | Joining date can't be future |
| `@Future` | `contractEndDate` | Contract must end in the future |
| `@FutureOrPresent` | `joiningDate` | Joining must be today or later |
| `@AssertFalse` | `terminated` | Department should not be terminated |
| `@PrimeNumberValidation` | `primeNumber` | Custom — must be a prime number |
| `@PasswordValidation` | `password` | Custom — strong password rules |

---

## Question 5: Custom Annotation — @PrimeNumberValidation

Validates that an integer field is a prime number.

### Step 1: Annotation Interface — PrimeNumberValidation.java

```java
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PrimeNumberValidator.class})
public @interface PrimeNumberValidation {
    String message() default "Number should be a Prime Number!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### Step 2: Validator Logic — PrimeNumberValidator.java

```java
public class PrimeNumberValidator
        implements ConstraintValidator<PrimeNumberValidation, Integer> {

    @Override
    public boolean isValid(Integer number, ConstraintValidatorContext context) {
        return isPrimeNumber(number);
    }

    private boolean isPrimeNumber(Integer number) {
        if (number < 2) return false;
        if (number == 2 || number == 3) return true;
        if (number % 2 == 0) return false;

        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}
```

### How Prime Checking Works

```
number < 2          → false (1, 0, negatives are not prime)
number == 2 or 3    → true
number % 2 == 0     → false (even numbers, except 2)
Loop from 5 to √n   → check divisibility by 6k ± 1 pattern
                       (all primes > 3 are of this form)
```

### Usage

```java
@PrimeNumberValidation
private Integer primeNumber;
```

Valid: `2, 3, 5, 7, 11, 13...` | Invalid: `1, 4, 6, 8, 9, 10...`

---

## Question 6: Custom Annotation — @PasswordValidation

Validates that a password meets strong security criteria.

### Criteria
- At least one **uppercase** letter (A-Z)
- At least one **lowercase** letter (a-z)
- At least one **special character** (!@#$%^&*...)
- Minimum length of **10 characters**

### Step 1: Annotation Interface — PasswordValidation.java

```java
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PasswordValidator.class})
public @interface PasswordValidation {
    String message() default "Password must contain at least one uppercase letter, " +
                              "one lowercase letter, one special character, " +
                              "and be at least 10 characters long";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### Step 2: Validator Logic — PasswordValidator.java

```java
public class PasswordValidator
        implements ConstraintValidator<PasswordValidation, String> {

    private static final String PASSWORD_REGEX =
        "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>/?]).{10,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) return false;
        return password.matches(PASSWORD_REGEX);
    }
}
```

### Regex Breakdown

| Part | What It Checks |
|---|---|
| `(?=.*[A-Z])` | At least one uppercase letter (lookahead) |
| `(?=.*[a-z])` | At least one lowercase letter (lookahead) |
| `(?=.*[!@#$%...])` | At least one special character (lookahead) |
| `.{10,}` | Minimum 10 characters total |
| `^` / `$` | Match the entire string |

### Usage

```java
@PasswordValidation
private String password;
```

Valid: `MyPass@word1` | Invalid: `password`, `Password1`, `PASS@WORD`

---

## Project Structure

```
src/main/java/module2_homework/spring_web_mvc/
│
├── Module2HomeworkApplication.java        ← Spring Boot entry point
│
├── controllers/
│   └── DepartmentController.java          ← REST endpoints
│
├── dtos/
│   └── DepartmentDTO.java                 ← Request/Response DTO with all validations
│
├── entities/
│   └── Department.java                    ← JPA Entity
│
├── advices/
│   ├── ApiResponse.java                   ← Wrapper for all API responses
│   ├── ApiError.java                      ← Structured error details
│   └── GlobalExceptionHandler.java        ← Global exception handling
│
├── exceptions/
│   └── ResourceNotFoundException.java     ← Custom 404 exception
│
├── annotations/
│   ├── PrimeNumberValidation.java         ← Custom annotation interface
│   ├── PrimeNumberValidator.java          ← Prime number validation logic
│   ├── PasswordValidation.java            ← Custom annotation interface
│   └── PasswordValidator.java             ← Password strength validation logic
│
└── mappers/
    └── DepartmentMapper.java              ← MapStruct mapper: Entity ↔ DTO
```

---

## Conclusion

This module covered building a complete REST API layer in Spring Boot:

1. **REST API design** — resource-based URLs with HTTP method semantics
2. **MVC Layered Architecture** — Controller → Service → Repository → Database
3. **Controller layer** — `@RestController`, `@GetMapping`, `@PathVariable`, `@RequestBody`
4. **DTO vs Entity** — what you expose vs what you store
5. **Input Validation** — 20+ built-in annotations + custom validators
6. **Exception Handling** — `@RestControllerAdvice` + `@ExceptionHandler` for consistent errors
7. **API Response Wrapping** — `ApiResponse<T>` gives every endpoint a consistent shape
8. **Custom Annotations** — `@PrimeNumberValidation` and `@PasswordValidation` built from scratch
