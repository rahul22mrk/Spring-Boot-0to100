# JPA Core Deep Dive - Complete Guide (Hindi + English)

---

## 📌 TOPICS COVERED

1. **Hibernate ORM vs JPA vs Spring Data JPA** - Ye sab kya hain aur kaise relate karte hain
2. **Entities and Tables** - Entity kaise bante hain, Table se kaise map hote hain
3. **Hibernate Entity Lifecycle** - Entity ke 4 states (Transient, Persistent, Detached, Removed)
4. **EntityManager & Persistence Context** - First Level Cache, Dirty Checking, Flush
5. **Sorting & Pagination** - Pageable, Sort, Page Object
6. **Projection** - Interface-based, Class-based, Dynamic Projection
7. **Spring Data JPA Interfaces** - JpaRepository, CrudRepository, PagingAndSortingRepository
8. **Dynamic Queries** - Specification, Criteria API, QueryDSL

---

# 1️⃣ HIBERNATE ORM vs JPA vs SPRING DATA JPA

## Ye sab kya hain? Confusion clear karo!

```
┌──────────────────────────────────────────────────────────────┐
│           LAYER ARCHITECTURE                                  │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌─────────────────────────────────────────┐                 │
│  │  YOUR CODE (Service/Controller)          │                 │
│  └────────────────┬────────────────────────┘                 │
│                   │ calls                                      │
│  ┌────────────────▼────────────────────────┐                 │
│  │  SPRING DATA JPA                         │ ← High Level   │
│  │  (JpaRepository, @Query, Pageable)       │   Easy to use  │
│  └────────────────┬────────────────────────┘                 │
│                   │ uses internally                            │
│  ┌────────────────▼────────────────────────┐                 │
│  │  JPA (Jakarta Persistence API)           │ ← Specification│
│  │  (EntityManager, @Entity, @Table)        │   Interface    │
│  └────────────────┬────────────────────────┘                 │
│                   │ implemented by                             │
│  ┌────────────────▼────────────────────────┐                 │
│  │  HIBERNATE ORM                           │ ← Implementation│
│  │  (Session, Criteria, Cache)              │   Actual code  │
│  └────────────────┬────────────────────────┘                 │
│                   │ talks to                                   │
│  ┌────────────────▼────────────────────────┐                 │
│  │  DATABASE (MySQL, PostgreSQL, etc.)      │                 │
│  └─────────────────────────────────────────┘                 │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## Comparison Table:

| Feature | JDBC | Hibernate ORM | JPA | Spring Data JPA |
|---|---|---|---|---|
| **Kya hai?** | Direct DB connection | ORM implementation | Specification/Interface | Abstraction over JPA |
| **SQL likhna** | Haan, manual | HQL/Criteria | JPQL | Auto-generated + custom |
| **Boilerplate** | Bahut zyada | Medium | Medium | Minimal |
| **Entity mapping** | Manual RS→Object | @Entity | @Entity | Inherited from JPA |
| **Repository** | DAO pattern | Session | EntityManager | JpaRepository |
| **Pagination** | Manual SQL | Manual | SetMaxResults | Pageable (1 line!) |
| **Caching** | No | L1, L2 | L1 (via PC) | Same as JPA |

## Simple Analogy:

```
JDBC        = Manual gear car (sab khud karo)
Hibernate   = Automatic gear car (engine khud shift kare)
JPA         = Car interface (define karta hai gear hona chahiye)
Spring Data JPA = Self-driving car (sab auto, bas destination batao)
```

## Code Comparison - Same operation in all:

```java
// ═══ JDBC (Bahut code!) ═══
public Employee findById(Long id) {
    Connection conn = DriverManager.getConnection(url, user, pass);
    PreparedStatement ps = conn.prepareStatement("SELECT * FROM employee WHERE id = ?");
    ps.setLong(1, id);
    ResultSet rs = ps.executeQuery();
    Employee emp = null;
    if (rs.next()) {
        emp = new Employee();
        emp.setId(rs.getLong("id"));
        emp.setName(rs.getString("name"));
        // ... har column manually map karo
    }
    rs.close(); ps.close(); conn.close(); //cleanup!
    return emp;
}

// ═══ Hibernate ORM (Medium code) ═══
public Employee findById(Long id) {
    Session session = sessionFactory.openSession();
    Employee emp = session.get(Employee.class, id);
    session.close();
    return emp;
}

// ═══ JPA (Medium code) ═══
public Employee findById(Long id) {
    EntityManager em = entityManagerFactory.createEntityManager();
    Employee emp = em.find(Employee.class, id);
    em.close();
    return emp;
}

// ═══ Spring Data JPA (1 line!) ═══
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // findById() already inherited! No code needed!
}
// Usage: employeeRepository.findById(1L);
```

---

# 2️⃣ ENTITIES AND TABLES IN SPRING DATA JPA

## Entity kya hai?

```
Entity = Java Class jo Database Table ko represent karti hai
Table  = Database mein actual table

Entity (Java)          ←→          Table (Database)
Class                  ←→          Table
Field/Property         ←→          Column
Object/Instance        ←→          Row
Annotation             ←→          Constraint (PK, FK, NOT NULL)
```

## All Important Annotations:

```java
@Entity                              // Ye class ek DB table hai
@Table(name = "employees")           // Table ka naam (optional, default = class name)
public class Employee {

    @Id                              // Primary Key
    @GeneratedValue(                 // Auto-increment strategy
        strategy = GenerationType.IDENTITY  // AUTO, IDENTITY, SEQUENCE, TABLE
    )
    private Long id;

    @Column(                         // Column customization
        name = "emp_name",           // DB column ka naam
        nullable = false,            // NOT NULL constraint
        unique = true,               // UNIQUE constraint
        length = 100                 // VARCHAR length
    )
    private String name;

    @Column(name = "salary", precision = 10, scale = 2)  // DECIMAL(10,2)
    private BigDecimal salary;

    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)     // Enum ko STRING store karo (not INT)
    @Column(name = "status")
    private EmployeeStatus status;   // ACTIVE, INACTIVE

    @Temporal(TemporalType.DATE)     // Sirf DATE (no time)
    @Column(name = "joining_date")
    private Date joiningDate;

    @Temporal(TemporalType.TIMESTAMP)// DATE + TIME
    @Column(name = "created_at")
    private Date createdAt;

    @Lob                             // Large Object (BLOB/CLOB)
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Transient                       // Ye field DB mein NAHI store hoga
    private String tempData;

    @CreatedDate                     // Auto-set on creation (need Auditing)
    private LocalDateTime createdAt;

    @LastModifiedDate                // Auto-update on modification (need Auditing)
    private LocalDateTime updatedAt;
}
```

## GenerationType Strategies:

```
┌──────────────────────────────────────────────────────────────┐
│           PRIMARY KEY GENERATION STRATEGIES                   │
├──────────────┬───────────────────────────────────────────────┤
│ IDENTITY     │ Database auto-increment (MySQL, SQL Server)   │
│              │ Most common! DB generates ID                  │
├──────────────┼───────────────────────────────────────────────┤
│ SEQUENCE     │ Database sequence (PostgreSQL, Oracle)        │
│              │ Best for batch inserts! Pre-allocates IDs     │
├──────────────┼───────────────────────────────────────────────┤
│ TABLE        │ Separate table for ID generation              │
│              │ Portable but slower                           │
├──────────────┼───────────────────────────────────────────────┤
│ AUTO         │ JPA provider decides (default)                │
│              │ Unpredictable - avoid in production!          │
├──────────────┼───────────────────────────────────────────────┤
│ UUID         │ Application generates UUID                    │
│              │ Good for distributed systems                  │
└──────────────┴───────────────────────────────────────────────┘
```

## Composite Primary Key:

```java
// WAY 1: @IdClass (Separate class)
@Entity
@IdClass(EmployeeProjectId.class)
public class EmployeeProject {
    @Id
    private Long employeeId;
    
    @Id
    private Long projectId;
    
    private LocalDate assignedDate;
}

// Composite Key Class
public class EmployeeProjectId implements Serializable {
    private Long employeeId;
    private Long projectId;
    // MUST implement equals() and hashCode()!
}

// WAY 2: @EmbeddedId (Embedded in entity)
@Entity
public class EmployeeProject {
    @EmbeddedId
    private EmployeeProjectId id;
    
    private LocalDate assignedDate;
}

@Embeddable
public class EmployeeProjectId implements Serializable {
    private Long employeeId;
    private Long projectId;
    // MUST implement equals() and hashCode()!
}
```

---

# 3️⃣ HIBERNATE ENTITY LIFECYCLE

## Entity ke 4 States:

```
┌──────────────────────────────────────────────────────────────┐
│              ENTITY LIFECYCLE STATES                          │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│                    new Employee()                              │
│                        │                                       │
│                        ▼                                       │
│  ┌──────────────────────────────────┐                         │
│  │  1. TRANSIENT (New)              │                         │
│  │  - Object created, not in DB     │                         │
│  │  - Not in Persistence Context    │                         │
│  │  - No ID assigned                │                         │
│  └──────────┬───────────────────────┘                         │
│             │ session.save() / persist()                      │
│             ▼                                                  │
│  ┌──────────────────────────────────┐                         │
│  │  2. PERSISTENT (Managed)         │                         │
│  │  - In Persistence Context        │                         │
│  │  - Tracked by Hibernate          │                         │
│  │  - Changes auto-synced to DB     │                         │
│  │  - Has ID                        │                         │
│  └──────────┬───────────────────────┘                         │
│             │ session.evict() / clear() / close()            │
│             ▼                                                  │
│  ┌──────────────────────────────────┐                         │
│  │  3. DETACHED                     │                         │
│  │  - Was persistent, now removed   │                         │
│  │    from Persistence Context      │                         │
│  │  - Changes NOT auto-synced       │                         │
│  │  - Still has ID                  │                         │
│  └──────────┬───────────────────────┘                         │
│             │ session.merge() / update()                     │
│             ▼                                                  │
│        Back to PERSISTENT                                      │
│                                                                │
│  From PERSISTENT:                                              │
│             │ session.remove() / delete()                     │
│             ▼                                                  │
│  ┌──────────────────────────────────┐                         │
│  │  4. REMOVED                      │                         │
│  │  - Marked for deletion           │                         │
│  │  - Will be deleted on flush      │                         │
│  │  - Still in Persistence Context  │                         │
│  └──────────────────────────────────┘                         │
│             │ flush/commit                                     │
│             ▼                                                  │
│        Deleted from DB                                         │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## Code Examples for Each State:

```java
// ═══ STATE 1: TRANSIENT ═══
Employee emp = new Employee("Rahul"); 
// Transient: Object bana, DB mein nahi hai, PC mein nahi hai
// No ID, no tracking

// ═══ STATE 2: PERSISTENT ═══
entityManager.persist(emp);   // JPA way
// OR
session.save(emp);            // Hibernate way
// Persistent: DB mein save hoga, PC mein tracked hai, ID assigned

// DIRTY CHECKING - Auto update without save()!
emp.setName("Rahul-Updated");
// No need to call save() again! Hibernate auto-detects change!
// On flush/commit → UPDATE query auto-chalegi!

// ═══ STATE 3: DETACHED ═══
entityManager.detach(emp);    // JPA way
// OR
session.evict(emp);           // Hibernate way
// Detached: PC se bahar, changes tracked NAHI honge

emp.setName("Rahul-Detached-Update");
// This change will NOT go to DB! (Not tracked)

// Re-attach:
entityManager.merge(emp);     // JPA way - returns managed copy
// OR
session.update(emp);          // Hibernate way

// ═══ STATE 4: REMOVED ═══
entityManager.remove(emp);    // JPA way
// OR
session.delete(emp);          // Hibernate way
// Removed: Marked for deletion, flush pe DELETE query chalegi
```

## Entity Lifecycle Callbacks:

```java
@Entity
public class Employee {

    @PrePersist   // Jab entity PEHLI baar save ho (INSERT se pehle)
    public void beforeInsert() {
        this.createdAt = LocalDateTime.now();
        System.out.println("About to INSERT: " + this.name);
    }

    @PostPersist  // Jab entity PEHLI baar save ho (INSERT ke baad)
    public void afterInsert() {
        System.out.println("Inserted with ID: " + this.id);
    }

    @PreUpdate    // Jab entity update ho (UPDATE se pehle)
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
        System.out.println("About to UPDATE: " + this.name);
    }

    @PostUpdate   // Jab entity update ho (UPDATE ke baad)
    public void afterUpdate() {
        System.out.println("Updated: " + this.name);
    }

    @PreRemove    // Jab entity delete ho (DELETE se pehle)
    public void beforeDelete() {
        System.out.println("About to DELETE: " + this.id);
    }

    @PostRemove   // Jab entity delete ho (DELETE ke baad)
    public void afterDelete() {
        System.out.println("Deleted: " + this.id);
    }

    @PostLoad     // Jab entity DB se load ho (SELECT ke baad)
    public void afterLoad() {
        System.out.println("Loaded from DB: " + this.name);
    }
}
```

---

# 4️⃣ ENTITYMANAGER & PERSISTENCE CONTEXT

## Persistence Context kya hai?

```
Persistence Context = "Ek box" jisme Hibernate tracked entities rakhta hai

Jab bhi tu entity ko load/save karta hai, wo PC mein aati hai
PC mein jo entities hain, unka track Hibernate rakhta hai
Koi bhi change karo → flush() pe auto-UPDATE query!

PC = First Level Cache (L1 Cache)
- Per EntityManager/Session
- Same transaction mein same entity dobara DB se nahi aayegi
- Transaction end hone pe PC clear ho jata hai
```

## Visual Explanation:

```
┌──────────────────────────────────────────────────────────────┐
│              PERSISTENCE CONTEXT (L1 CACHE)                   │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  EntityManager em = emf.createEntityManager();                │
│                                                                │
│  ┌─────────────────────────────────┐                          │
│  │  Persistence Context             │                          │
│  │                                   │                          │
│  │  Employee@1 {id=1, name="Rahul"} │ ← Tracked!             │
│  │  Employee@2 {id=2, name="Amit"}  │ ← Tracked!             │
│  │                                   │                          │
│  │  Changes auto-detected!           │                          │
│  └─────────────────────────────────┘                          │
│                                                                │
│  Step 1: Employee e1 = em.find(Employee.class, 1L);          │
│  → SELECT * FROM employee WHERE id = 1                        │
│  → e1 is now in PC (tracked)                                  │
│                                                                │
│  Step 2: e1.setName("Rahul-Updated");                         │
│  → Just changed in PC, NO SQL yet!                            │
│                                                                │
│  Step 3: em.flush(); // OR transaction commit                 │
│  → Dirty checking: PC vs original snapshot                    │
│  → Name changed! → UPDATE employee SET name='Rahul-Updated'  │
│  → SQL executed NOW!                                           │
│                                                                │
│  Step 4: Employee e1Again = em.find(Employee.class, 1L);     │
│  → NO SQL! Returns from PC (L1 Cache hit!)                    │
│  → e1Again == e1 (same reference!)                            │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## EntityManager Important Methods:

```java
// ═══ FIND (Read) ═══
Employee emp = entityManager.find(Employee.class, 1L);
// SQL: SELECT * FROM employee WHERE id = 1
// Returns null if not found (no exception)

// ═══ GETREFERENCE (Lazy Read) ═══
Employee empProxy = entityManager.getReference(Employee.class, 1L);
// NO SQL yet! Returns proxy object
// SQL only when empProxy.getName() is called
// EntityNotFoundException if not found (on access)

// ═══ PERSIST (Insert) ═══
Employee newEmp = new Employee("New Guy");
entityManager.persist(newEmp);
// SQL: INSERT INTO employee (name) VALUES ('New Guy')
// On flush/commit

// ═══ MERGE (Update or Insert) ═══
Employee detachedEmp = new Employee("Merged");
detachedEmp.setId(1L);
Employee managed = entityManager.merge(detachedEmp);
// If exists → UPDATE
// If not exists → INSERT
// Returns MANAGED copy (original stays detached!)

// ═══ REMOVE (Delete) ═══
Employee emp = entityManager.find(Employee.class, 1L);
entityManager.remove(emp);
// SQL: DELETE FROM employee WHERE id = 1
// On flush/commit

// ═══ DETACH ═══
entityManager.detach(emp);
// Entity PC se bahar, changes tracked nahi honge

// ═══ CLEAR ═══
entityManager.clear();
// PC mein se SAARI entities detach kar do

// ═══ FLUSH ═══
entityManager.flush();
// PC ke changes DB mein sync karo (but don't commit transaction)
// Dirty checking → generates INSERT/UPDATE/DELETE queries

// ═══ REFRESH ═══
entityManager.refresh(emp);
// PC se hatao, DB se fresh data lao
// Overwrites any local changes!

// ═══ CONTAINS ═══
boolean isManaged = entityManager.contains(emp);
// Check if entity is in PC (Persistent state)
```

## Dirty Checking - How it works:

```
┌──────────────────────────────────────────────────────────────┐
│                    DIRTY CHECKING                              │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  Step 1: em.find(Employee.class, 1L)                          │
│  DB → Employee {id=1, name="Rahul", salary=50000}            │
│  PC Snapshot → {id=1, name="Rahul", salary=50000}            │
│                                                                │
│  Step 2: emp.setName("Rahul-Updated");                        │
│  PC Entity   → {id=1, name="Rahul-Updated", salary=50000}    │
│  PC Snapshot → {id=1, name="Rahul", salary=50000}            │
│                                                                │
│  Step 3: flush() / commit()                                   │
│  Hibernate compares: Entity vs Snapshot                        │
│  name changed! → "Rahul" vs "Rahul-Updated"                   │
│  → SQL: UPDATE employee SET name='Rahul-Updated' WHERE id=1  │
│  → Only changed columns in UPDATE! (Not all columns)          │
│                                                                │
│  PRO TIP: Dirty checking = No need to call save() explicitly! │
│  Just modify managed entity, Hibernate will auto-update!      │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

---

# 5️⃣ SORTING & PAGINATION

## Pagination kyun zaroori hai?

```
Agar DB mein 1 lakh employees hain aur sab ek saath load kare:
- Memory full ho jayegi! 💀
- Network slow ho jayega
- UI hang ho jayega
- API timeout ho jayega

Solution: PAGINATION - Thoda thoda data lo (10-20 per page)
```

## Spring Data JPA Pagination:

```java
// ═══ BASIC PAGINATION ═══
// Page 0 (first page), 10 records per page
Pageable pageable = PageRequest.of(0, 10);
Page<Employee> page = employeeRepository.findAll(pageable);

// Page object ke important methods:
List<Employee> employees = page.getContent();     // Data list
int totalPages = page.getTotalPages();             // Total pages
long totalElements = page.getTotalElements();      // Total records
int currentPage = page.getNumber();                // Current page (0-indexed)
int pageSize = page.getSize();                     // Page size
boolean hasNext = page.hasNext();                  // Next page exists?
boolean hasPrev = page.hasPrevious();              // Previous page exists?

// ═══ PAGINATION WITH SORTING ═══
// Page 0, 10 per page, sorted by name ascending
Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
Page<Employee> page = employeeRepository.findAll(pageable);

// ═══ MULTIPLE SORT ═══
// Sort by department ASC, then by name DESC
Sort sort = Sort.by("department").ascending()
               .and(Sort.by("name").descending());
Pageable pageable = PageRequest.of(0, 10, sort);

// ═══ SORTING WITHOUT PAGINATION ═══
Sort sort = Sort.by("name").ascending();
List<Employee> employees = employeeRepository.findAll(sort);

// ═══ CUSTOM REPOSITORY METHOD WITH PAGINATION ═══
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Page<Employee> findByDepartment(String department, Pageable pageable);
    
    Page<Employee> findBySalaryGreaterThan(BigDecimal salary, Pageable pageable);
    
    List<Employee> findByNameContaining(String name, Sort sort);
}

// Usage:
Page<Employee> itEmployees = employeeRepository.findByDepartment("IT", 
    PageRequest.of(0, 10, Sort.by("name")));
```

## Slice vs Page:

```java
// Page → COUNT query bhi chalti hai (total elements jaanne ke liye)
// Slice → COUNT query NAHI chalti (faster, but no total count)

// Page use karo jab: UI pe page numbers dikhane hain (1,2,3...10)
// Slice use karo jab: Sirf "Load More" button hai (next page hai ya nahi)

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Slice<Employee> findByDepartment(String dept, Pageable pageable);
    // Slice has: getContent(), hasNext(), hasPrevious()
    // Slice does NOT have: getTotalPages(), getTotalElements()
}
```

## API Endpoint Example:

```java
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @GetMapping
    public Page<Employee> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {
        
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sortOrder = Sort.by(direction, sort[0]);
        
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return employeeRepository.findAll(pageable);
    }
}

// API call: GET /api/employees?page=0&size=10&sort=name,asc
// Response:
// {
//   "content": [...10 employees...],
//   "totalPages": 50,
//   "totalElements": 500,
//   "number": 0,
//   "size": 10,
//   "first": true,
//   "last": false
// }
```

---

# 6️⃣ PROJECTION

## Projection kya hai?

```
Projection = "Sirf wahi data lo jo chahiye, poora entity mat lo"

Example: Employee table mein 20 columns hain
Lekin dropdown ke liye sirf id aur name chahiye
Projection se sirf 2 columns aayenge → Fast, Less Memory!
```

## 3 Types of Projection:

### Type 1: Interface-Based Projection (Simplest!)

```java
// Step 1: Define interface
public interface EmployeeNameOnly {
    Long getId();           // Must match entity field name
    String getName();       // Must match entity field name
}

// Step 2: Use in repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    List<EmployeeNameOnly> findByDepartment(String department);
    
    @Query("SELECT e.id AS id, e.name AS name FROM Employee e WHERE e.salary > :minSalary")
    List<EmployeeNameOnly> findHighEarners(@Param("minSalary") BigDecimal minSalary);
}

// Usage:
List<EmployeeNameOnly> names = employeeRepository.findByDepartment("IT");
names.forEach(n -> System.out.println(n.getId() + " - " + n.getName()));

// SQL: SELECT id, name FROM employee WHERE department = 'IT'
// Only 2 columns fetched! Not all 20!
```

### Type 2: Class-Based Projection (DTO)

```java
// Step 1: Define DTO class
public class EmployeeDTO {
    private Long id;
    private String name;
    private String departmentName;
    
    public EmployeeDTO(Long id, String name, String departmentName) {
        this.id = id;
        this.name = name;
        this.departmentName = departmentName;
    }
    
    // Getters...
}

// Step 2: Use in repository with JPQL constructor expression
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    @Query("SELECT new com.gahub.server.dto.EmployeeDTO(e.id, e.name, d.name) " +
           "FROM Employee e JOIN e.department d")
    List<EmployeeDTO> findAllEmployeeDTOs();
}

// Usage:
List<EmployeeDTO> dtos = employeeRepository.findAllEmployeeDTOs();
```

### Type 3: Dynamic Projection (Runtime pe decide karo!)

```java
// Step 1: Multiple projection interfaces
public interface EmployeeNameOnly {
    String getName();
}

public interface EmployeeSalaryOnly {
    String getName();
    BigDecimal getSalary();
}

// Step 2: Generic repository method
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    <T> List<T> findByDepartment(String department, Class<T> projectionType);
}

// Usage: Runtime pe decide karo kaunsa projection chahiye
List<EmployeeNameOnly> names = employeeRepository.findByDepartment("IT", EmployeeNameOnly.class);
List<EmployeeSalaryOnly> salaries = employeeRepository.findByDepartment("IT", EmployeeSalaryOnly.class);

// Same method, different projections! 🎉
```

---

# 7️⃣ SPRING DATA JPA INTERFACES

## Interface Hierarchy:

```
┌──────────────────────────────────────────────────────────────┐
│           SPRING DATA JPA INTERFACE HIERARCHY                │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  Repository (Marker interface - no methods)                   │
│       │                                                        │
│       ├── CrudRepository (CRUD methods)                       │
│       │       │                                                │
│       │       ├── PagingAndSortingRepository (Sort + Page)    │
│       │       │       │                                        │
│       │       │       ├── JpaRepository (JPA extras)          │
│       │       │       │       │                                │
│       │       │       │       ← USE THIS ONE!                 │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## What Each Interface Provides:

| Interface | Methods | Use When |
|---|---|---|
| **Repository** | None (marker) | Custom base |
| **CrudRepository** | save, findById, findAll, delete, count, exists | Basic CRUD |
| **PagingAndSortingRepository** | + findAll(Sort), findAll(Pageable) | Pagination needed |
| **JpaRepository** | + flush, saveAndFlush, deleteInBatch, findAll(Sort), findAll(Pageable) | **Always use this!** |

## JpaRepository - All Methods:

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // ═══ INHERITED METHODS (No code needed!) ═══
    
    // --- Save ---
    Employee save(Employee emp);            // INSERT or UPDATE
    List<Employee> saveAll(Iterable<Employee> emps); // Batch save
    Employee saveAndFlush(Employee emp);    // Save + immediate flush
    
    // --- Read ---
    Optional<Employee> findById(Long id);   // Find by PK
    List<Employee> findAll();               // Get all
    List<Employee> findAllById(Iterable<Long> ids); // By multiple IDs
    
    // --- Delete ---
    void deleteById(Long id);               // Delete by PK
    void delete(Employee emp);              // Delete entity
    void deleteAll();                       // Delete all (one by one)
    void deleteAllInBatch();                // Delete all (1 query, faster!)
    void deleteAllInBatch(Iterable<Employee> emps); // Batch delete
    
    // --- Check ---
    boolean existsById(Long id);            // Exists?
    long count();                           // Total records
    
    // --- Pagination & Sorting ---
    List<Employee> findAll(Sort sort);      // Sorted list
    Page<Employee> findAll(Pageable pageable); // Paginated
    
    // --- Flush ---
    void flush();                           // Sync PC to DB
}
```

## Custom Query Methods (Method Naming Convention):

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // ═══ FIND BY ═══
    List<Employee> findByName(String name);
    List<Employee> findByDepartment(String dept);
    Employee findByEmail(String email);           // Single result
    
    // ═══ FIND BY MULTIPLE FIELDS ═══
    List<Employee> findByNameAndDepartment(String name, String dept);
    List<Employee> findByNameOrEmail(String name, String email);
    
    // ═══ COMPARISON ═══
    List<Employee> findBySalaryGreaterThan(BigDecimal salary);    // >
    List<Employee> findBySalaryLessThan(BigDecimal salary);       // <
    List<Employee> findBySalaryBetween(BigDecimal min, BigDecimal max); // BETWEEN
    List<Employee> findByAgeGreaterThanEqual(int age);            // >=
    
    // ═══ LIKE ═══
    List<Employee> findByNameContaining(String keyword);   // %keyword%
    List<Employee> findByNameStartingWith(String prefix);  // prefix%
    List<Employee> find
