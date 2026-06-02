# JPA Dynamic Queries & Custom Method Names - Complete Guide (Hindi + English)

---

## 📌 TOPICS COVERED

1. **Spring Data JPA Custom Method Naming** - Query banao bina SQL likhe
2. **@Query** - JPQL aur Native SQL custom queries
3. **Specification** - Dynamic queries banao (filter combinations)
4. **Criteria API** - Programmatic query building
5. **QueryDSL** - Type-safe dynamic queries (Best for complex!)

---

# 1️⃣ CUSTOM METHOD NAMING CONVENTION

## Spring Data JPA automatically generates SQL from method names!

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ═══ FIND BY ═══
    List<Employee> findByName(String name);
    // SQL: SELECT * FROM employee WHERE name = ?

    List<Employee> findByDepartment(String dept);
    // SQL: SELECT * FROM employee WHERE department = ?

    Employee findByEmail(String email);
    // SQL: SELECT * FROM employee WHERE email = ? (single result)

    // ═══ MULTIPLE CONDITIONS ═══
    List<Employee> findByNameAndDepartment(String name, String dept);
    // SQL: WHERE name = ? AND department = ?

    List<Employee> findByNameOrEmail(String name, String email);
    // SQL: WHERE name = ? OR email = ?

    // ═══ COMPARISON ═══
    List<Employee> findBySalaryGreaterThan(BigDecimal salary);     // >
    List<Employee> findBySalaryLessThan(BigDecimal salary);        // <
    List<Employee> findBySalaryBetween(BigDecimal min, BigDecimal max); // BETWEEN
    List<Employee> findByAgeGreaterThanEqual(int age);             // >=
    List<Employee> findByAgeLessThanEqual(int age);               // <=

    // ═══ LIKE ═══
    List<Employee> findByNameContaining(String keyword);    // %keyword%
    List<Employee> findByNameStartingWith(String prefix);   // prefix%
    List<Employee> findByNameEndingWith(String suffix);     // %suffix
    List<Employee> findByNameLike(String pattern);          // Custom pattern

    // ═══ IN / NOT IN ═══
    List<Employee> findByDepartmentIn(List<String> depts);   // IN (...)
    List<Employee> findByDepartmentNotIn(List<String> depts); // NOT IN (...)

    // ═══ NULL CHECK ═══
    List<Employee> findByManagerIsNull();       // IS NULL
    List<Employee> findByManagerIsNotNull();    // IS NOT NULL

    // ═══ ORDERING ═══
    List<Employee> findByDepartmentOrderByNameAsc(String dept);
    List<Employee> findBySalaryGreaterThanOrderBySalaryDesc(BigDecimal salary);

    // ═══ DISTINCT ═══
    List<Employee> findDistinctByDepartment(String dept);

    // ═══ TOP / FIRST (Limit results) ═══
    List<Employee> findTop10ByOrderBySalaryDesc();    // Top 10 highest salary
    Employee findFirstByOrderBySalaryDesc();           // Highest salary employee
    List<Employee> findTop5ByDepartment(String dept);  // Top 5 in dept

    // ═══ DATE COMPARISON ═══
    List<Employee> findByJoiningDateAfter(LocalDate date);   // >
    List<Employee> findByJoiningDateBefore(LocalDate date);  // <
    List<Employee> findByJoiningDateBetween(LocalDate start, LocalDate end);

    // ═══ BOOLEAN ═══
    List<Employee> findByActiveTrue();    // WHERE active = true
    List<Employee> findByActiveFalse();   // WHERE active = false

    // ═══ COUNT ═══
    long countByDepartment(String dept);   // SELECT COUNT(*)

    // ═══ EXISTS ═══
    boolean existsByEmail(String email);   // SELECT COUNT(*) > 0

    // ═══ DELETE ═══
    void deleteByDepartment(String dept);  // DELETE WHERE ...
    long deleteByActiveFalse();            // Delete + return count
}
```

## Keyword Quick Reference:

| Keyword | SQL | Example |
|---|---|---|
| `And` | WHERE x = ? AND y = ? | `findByNameAndDept` |
| `Or` | WHERE x = ? OR y = ? | `findByNameOrEmail` |
| `Between` | WHERE x BETWEEN ? AND ? | `findBySalaryBetween` |
| `LessThan` | WHERE x < ? | `findBySalaryLessThan` |
| `GreaterThan` | WHERE x > ? | `findBySalaryGreaterThan` |
| `Like` | WHERE x LIKE ? | `findByNameLike` |
| `Containing` | WHERE x LIKE %?% | `findByNameContaining` |
| `StartingWith` | WHERE x LIKE ?% | `findByNameStartingWith` |
| `EndingWith` | WHERE x LIKE %? | `findByNameEndingWith` |
| `In` | WHERE x IN (?) | `findByDeptIn` |
| `NotIn` | WHERE x NOT IN (?) | `findByDeptNotIn` |
| `IsNull` | WHERE x IS NULL | `findByManagerIsNull` |
| `IsNotNull` | WHERE x IS NOT NULL | `findByManagerIsNotNull` |
| `True` | WHERE x = true | `findByActiveTrue` |
| `False` | WHERE x = false | `findByActiveFalse` |
| `OrderBy` | ORDER BY x ASC/DESC | `findByNameOrderByIdDesc` |

---

# 2️⃣ @QUERY - Custom JPQL & Native SQL

## Jab method naming se kaam na chale:

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ═══ JPQL (Java Persistence Query Language) ═══
    // Entity names + field names use karo (not table/column names)

    @Query("SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :minSalary")
    List<Employee> findByDeptAndMinSalary(@Param("dept") String dept, 
                                           @Param("minSalary") BigDecimal minSalary);

    // JOIN in JPQL
    @Query("SELECT e FROM Employee e JOIN e.department d WHERE d.name = :deptName")
    List<Employee> findByDepartmentName(@Param("deptName") String deptName);

    // JOIN FETCH (solve N+1)
    @Query("SELECT e FROM Employee e JOIN FETCH e.department")
    List<Employee> findAllWithDepartment();

    // UPDATE query
    @Modifying
    @Query("UPDATE Employee e SET e.salary = :newSalary WHERE e.department = :dept")
    int updateSalaryByDepartment(@Param("newSalary") BigDecimal salary, 
                                  @Param("dept") String dept);

    // DELETE query
    @Modifying
    @Query("DELETE FROM Employee e WHERE e.active = false")
    int deleteInactiveEmployees();

    // ═══ NATIVE SQL (Direct database query) ═══
    // Table names + column names use karo (not entity names)

    @Query(value = "SELECT * FROM employees WHERE salary > :minSalary", 
           nativeQuery = true)
    List<Employee> findHighEarnersNative(@Param("minSalary") BigDecimal minSalary);

    // Complex native query with JOIN
    @Query(value = "SELECT e.*, d.name as dept_name " +
           "FROM employees e " +
           "JOIN departments d ON e.dept_id = d.id " +
           "WHERE e.salary BETWEEN :min AND :max",
           nativeQuery = true)
    List<Object[]> findEmployeesInSalaryRange(@Param("min") BigDecimal min,
                                               @Param("max") BigDecimal max);

    // ═══ SPeL (Spring Expression Language) ═══
    // Dynamic entity name
    @Query("SELECT e FROM #{#entityName} e WHERE e.active = :active")
    List<Employee> findByActiveStatus(@Param("active") boolean active);
}
```

## @Modifying Rules:

```java
// @Modifying REQUIRED for UPDATE/DELETE queries
// MUST be used with @Transactional

@Service
public class EmployeeService {

    @Transactional // MUST have transaction for modifying queries
    public void giveRaise(String dept, BigDecimal newSalary) {
        int updated = employeeRepository.updateSalaryByDepartment(newSalary, dept);
        System.out.println(updated + " employees updated");
    }
}
```

---

# 3️⃣ SPECIFICATION - Dynamic Queries

## Specification kyun chahiye?

```
Problem: Search form mein 5 filters hain, user koi bhi combination use kar sakta hai
- Name (optional)
- Department (optional)  
- Min Salary (optional)
- Max Salary (optional)
- Status (optional)

Total combinations: 2^5 = 32! 
32 alag methods likhoge? NO! Specification use karo!
```

## How to implement:

```java
// Step 1: Repository extend JpaSpecificationExecutor
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
                                           JpaSpecificationExecutor<Employee> {
    // Now you can use: findAll(Specification), count(Specification), etc.
}

// Step 2: Create Specification class
public class EmployeeSpecifications {

    public static Specification<Employee> hasName(String name) {
        return (root, query, cb) -> 
            name == null ? null : cb.equal(root.get("name"), name);
    }

    public static Specification<Employee> hasDepartment(String dept) {
        return (root, query, cb) -> 
            dept == null ? null : cb.equal(root.get("department"), dept);
    }

    public static Specification<Employee> salaryGreaterThan(BigDecimal min) {
        return (root, query, cb) -> 
            min == null ? null : cb.greaterThan(root.get("salary"), min);
    }

    public static Specification<Employee> salaryLessThan(BigDecimal max) {
        return (root, query, cb) -> 
            max == null ? null : cb.lessThan(root.get("salary"), max);
    }

    public static Specification<Employee> hasStatus(EmployeeStatus status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);
    }
}

// Step 3: Use in Service - Combine any filters dynamically!
@Service
public class EmployeeService {

    @Transactional(readOnly = true)
    public List<Employee> searchEmployees(String name, String dept, 
                                           BigDecimal minSalary, BigDecimal maxSalary,
                                           EmployeeStatus status) {
        
        // Start with no filter
        Specification<Employee> spec = Specification.where(null);

        // Add filters ONLY if provided (non-null)
        if (name != null)     spec = spec.and(EmployeeSpecifications.hasName(name));
        if (dept != null)     spec = spec.and(EmployeeSpecifications.hasDepartment(dept));
        if (minSalary != null) spec = spec.and(EmployeeSpecifications.salaryGreaterThan(minSalary));
        if (maxSalary != null) spec = spec.and(EmployeeSpecifications.salaryLessThan(maxSalary));
        if (status != null)    spec = spec.and(EmployeeSpecifications.hasStatus(status));

        // Single query with dynamic WHERE clause!
        return employeeRepository.findAll(spec);
    }
}

// Usage examples:
// searchEmployees("Rahul", null, null, null, null) 
//   → WHERE name = 'Rahul'

// searchEmployees(null, "IT", 50000, 100000, null) 
//   → WHERE dept = 'IT' AND salary > 50000 AND salary < 100000

// searchEmployees(null, null, null, null, null) 
//   → No WHERE clause (all employees)
```

---

# 4️⃣ CRITERIA API - Programmatic Queries

## When to use:

```
Method Naming  → Simple queries (1-2 conditions)
@Query         → Fixed JPQL/Native SQL
Specification  → Dynamic filter combinations
Criteria API   → Complex dynamic queries with joins, aggregations
QueryDSL       → Same as Criteria but type-safe and readable!
```

## Example:

```java
@Service
public class EmployeeReportService {

    @Autowired
    private EntityManager entityManager;

    public List<Employee> generateReport(String name, String dept, 
                                          BigDecimal minSalary, BigDecimal maxSalary) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);

        List<Predicate> predicates = new ArrayList<>();

        // Dynamically add conditions
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), 
                         "%" + name.toLowerCase() + "%"));
        }
        if (dept != null) {
            predicates.add(cb.equal(root.get("department"), dept));
        }
        if (minSalary != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), minSalary));
        }
        if (maxSalary != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("salary"), maxSalary));
        }

        // Apply all predicates
        cq.where(predicates.toArray(new Predicate[0]));

        // Order by
        cq.orderBy(cb.asc(root.get("name")));

        return entityManager.createQuery(cq).getResultList();
    }
}
```

---

# 5️⃣ QUERYDSL - Type-Safe Dynamic Queries (Best!)

## Why QueryDSL over Criteria API?

```
Criteria API → String-based field names, typos possible, hard to read
QueryDSL     → Type-safe, compile-time checking, fluent API, easy to read!

Criteria:  cb.equal(root.get("departmnt"), dept)  ← TYPO! No error at compile time!
QueryDSL:  QEmployee.employee.department.eq(dept)  ← Compile-time safe!
```

## Setup:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <scope>provided</scope>
</dependency>
```

## Usage:

```java
// Step 1: Repository extend QuerydslPredicateExecutor
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
                                           JpaSpecificationExecutor<Employee>,
                                           QuerydslPredicateExecutor<Employee> {
}

// Step 2: Use in Service
@Service
public class EmployeeSearchService {

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<Employee> search(String name, String dept, 
                                  BigDecimal minSalary, BigDecimal maxSalary) {
        
        QEmployee employee = QEmployee.employee;

        // Build query dynamically - Type-safe!
        BooleanBuilder builder = new BooleanBuilder();

        if (name != null) {
            builder.and(employee.name.containsIgnoreCase(name));
        }
        if (dept != null) {
            builder.and(employee.department.eq(dept));
        }
        if (minSalary != null) {
            builder.and(employee.salary.goe(minSalary)); // goe = >=
        }
        if (maxSalary != null) {
            builder.and(employee.salary.loe(maxSalary)); // loe = <=
        }

        return queryFactory
            .selectFrom(employee)
            .where(builder)
            .orderBy(employee.name.asc())
            .fetch();
    }

    // Complex query with JOIN
    public List<Employee> findByDepartmentWithHighSalary(String deptName) {
        QEmployee employee = QEmployee.employee;
        QDepartment department = QDepartment.department;

        return queryFactory
            .selectFrom(employee)
            .join(employee.department, department)
            .where(department.name.eq(deptName)
                   .and(employee.salary.goe(new BigDecimal(50000))))
            .orderBy(employee.salary.desc())
            .limit(10)
            .fetch();
    }
}
```

---

## 🏆 DYNAMIC QUERY COMPARISON

```
┌──────────────────────────────────────────────────────────────┐
│           WHICH APPROACH TO USE WHEN?                         │
├──────────────────┬───────────────────────────────────────────┤
│ Method Naming    │ Simple fixed queries (1-2 conditions)     │
│                  │ Fastest to write, no custom code needed   │
├──────────────────┼───────────────────────────────────────────┤
│ @Query (JPQL)    │ Fixed complex queries with JOINs         │
│                  │ You know the query at development time    │
├──────────────────┼───────────────────────────────────────────┤
│ @Query (Native)  │ DB-specific features, complex SQL        │
│                  │ JPQL can't handle it                     │
├──────────────────┼───────────────────────────────────────────┤
│ Specification    │ Dynamic filter combinations (search form) │
│                  │ Moderate complexity, built-in Spring      │
├──────────────────┼───────────────────────────────────────────┤
│ Criteria API     │ Very complex dynamic queries              │
│                  │ Aggregations, subqueries, but verbose     │
├──────────────────┼───────────────────────────────────────────┤
│ QueryDSL         │ BEST for dynamic queries!                 │
│                  │ Type-safe, readable, powerful             │
│                  │ Needs setup but worth it for big projects │
└──────────────────┴───────────────────────────────────────────┘
