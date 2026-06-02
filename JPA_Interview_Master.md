# JPA/Hibernate Interview Master Guide (3.5+ Yrs Exp)

---

## 📌 TOPICS COVERED (Interview-Critical for 3.5+ yrs)

1. **Inheritance Mapping** - SINGLE_TABLE, JOINED, TABLE_PER_CLASS
2. **Hibernate Caching** - L1, L2, Query Cache
3. **Optimistic vs Pessimistic Locking** - Concurrency control
4. **Soft Delete** - Logical delete patterns
5. **Spring Data JPA Auditing** - @CreatedDate, @CreatedBy, Envers
6. **Performance Tuning** - Batch inserts, read-only, connection pool
7. **Custom Types & Converters** - JSON columns, @Converter
8. **Top 50 Interview Q&A** - Rapid fire for 3.5+ yrs

---

# 1️⃣ INHERITANCE MAPPING

## 3 Strategies:

```
┌──────────────────────────────────────────────────────────────┐
│              INHERITANCE STRATEGIES                           │
├──────────────────┬───────────────────────────────────────────┤
│ SINGLE_TABLE     │ 1 table for entire hierarchy              │
│ (DEFAULT)        │ + DISCRIMINATOR column                    │
│                  │ Best performance, nullable columns issue  │
├──────────────────┼───────────────────────────────────────────┤
│ JOINED           │ 1 table per class, FK joins               │
│                  │ Normalized, no nullable issue, slower     │
├──────────────────┼───────────────────────────────────────────┤
│ TABLE_PER_CLASS  │ 1 table per concrete class                │
│                  │ No joins, duplicate columns, UNION needed │
└──────────────────┴───────────────────────────────────────────┘
```

### SINGLE_TABLE (Default - Best Performance)

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type")  // Type column
public abstract class Vehicle {
    @Id
    private Long id;
    private String manufacturer;
}

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {
    private Integer numberOfDoors;  // NULL for Bike rows
}

@Entity
@DiscriminatorValue("BIKE")
public class Bike extends Vehicle {
    private Integer engineCC;  // NULL for Car rows
}

// DB: single "vehicle" table
// | id | manufacturer | vehicle_type | number_of_doors | engine_cc |
// | 1  | Toyota       | CAR          | 4               | NULL      |
// | 2  | Honda        | BIKE         | NULL            | 150       |
```

### JOINED (Normalized - No NULL issue)

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle {
    @Id private Long id;
    private String manufacturer;
}

@Entity
public class Car extends Vehicle {
    private Integer numberOfDoors;
}

@Entity
public class Bike extends Vehicle {
    private Integer engineCC;
}

// DB: 3 tables
// vehicle:  | id | manufacturer |
// car:      | id (FK) | number_of_doors |
// bike:     | id (FK) | engine_cc |
// 
// SELECT car: JOIN vehicle + car (2 queries)
// More normalized but slower (joins needed)
```

### TABLE_PER_CLASS (Independent tables)

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Vehicle {
    @Id private Long id;
    private String manufacturer;
}

@Entity
public class Car extends Vehicle {
    private Integer numberOfDoors;
}

@Entity
public class Bike extends Vehicle {
    private Integer engineCC;
}

// DB: 2 independent tables (no vehicle table!)
// car:  | id | manufacturer | number_of_doors |
// bike: | id | manufacturer | engine_cc |
// 
// SELECT all vehicles: UNION of both tables (SLOW!)
```

---

# 2️⃣ HIBERNATE CACHING

## Cache Architecture:

```
┌──────────────────────────────────────────────────────────────┐
│              HIBERNATE CACHE LEVELS                           │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  L1 Cache (First Level) - ALWAYS ON, can't disable           │
│  ────────────────────────────────────────────                 │
│  - Per Session/EntityManager                                  │
│  - Same transaction mein same entity → DB hit NAHI           │
│  - Transaction end pe clear                                   │
│                                                                │
│  L2 Cache (Second Level) - Optional, need config             │
│  ────────────────────────────────────────────                 │
│  - Per SessionFactory (shared across sessions)                │
│  - Same entity across DIFFERENT transactions → DB hit NAHI   │
│  - Need: @Cacheable + @Cache + provider (Ehcache/Redis)      │
│                                                                │
│  Query Cache - Optional                                       │
│  ────────────────────────────────────────────                 │
│  - Caches query results (not entities)                        │
│  - Works WITH L2 cache                                        │
│  - Cache key = query + parameters                             │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## L2 Cache Setup (Ehcache):

```yaml
# application.properties
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory
spring.jpa.properties.javax.persistence.sharedCache.mode=ALL
```

```java
@Entity
@Cacheable                    // Enable L2 cache for this entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)  // Strategy
public class Department {
    @Id private Long id;
    private String name;
}

// L2 Cache scenarios:
// Tx1: em.find(Department.class, 1L) → DB HIT, stored in L2
// Tx2: em.find(Department.class, 1L) → L2 HIT! No DB query!
```

## When to use L2 Cache:

```
✅ USE L2 Cache when:
- Entity rarely changes (Country, Config, Master data)
- Read-heavy, write-light
- Same data accessed across transactions

❌ DON'T use L2 Cache when:
- Entity changes frequently (Orders, Transactions)
- Write-heavy tables
- Real-time data needed
```

---

# 3️⃣ OPTIMISTIC vs PESSIMISTIC LOCKING

## Why Locking? Concurrent access problem:

```
Thread A: Read employee (salary=50000)
Thread B: Read employee (salary=50000)
Thread A: Set salary=60000, save → DB: salary=60000
Thread B: Set salary=55000, save → DB: salary=55000 ← WRONG!
Thread A's update is LOST! (Lost Update Problem)
```

## Optimistic Locking (Most Common - No actual DB lock!)

```java
@Entity
public class Employee {
    @Id private Long id;
    private String name;
    private BigDecimal salary;
    
    @Version                    // ✅ Magic annotation!
    private Long version;       // Auto-managed by Hibernate
}

// How it works:
// 1. Read: Employee {id=1, salary=50000, version=0}
// 2. Thread A updates → UPDATE employee SET salary=60000, version=1 WHERE id=1 AND version=0 ✅
// 3. Thread B updates → UPDATE employee SET salary=55000, version=1 WHERE id=1 AND version=0 ❌
//    → 0 rows updated! → OptimisticLockException!
//    → Version mismatch! Someone else changed the data!

// Handle the exception:
@Transactional
public void updateSalary(Long id, BigDecimal newSalary) {
    Employee emp = employeeRepository.findById(id).get();
    emp.setSalary(newSalary);
    // On commit, if version mismatch → OptimisticLockException
}

// ✅ BEST for: Low contention, web applications, most cases
```

## Pessimistic Locking (Actual DB row lock!)

```java
// Lock the row so NO other transaction can read/modify it!

// Shared Lock (Read Lock) - Others can READ but not WRITE
@Lock(LockModeType.PESSIMISTIC_READ)
@Query("SELECT e FROM Employee e WHERE e.id = :id")
Employee findByIdWithReadLock(@Param("id") Long id);

// Exclusive Lock (Write Lock) - Others can't READ or WRITE
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT e FROM Employee e WHERE e.id = :id")
Employee findByIdWithWriteLock(@Param("id") Long id);

// SQL generated: SELECT * FROM employee WHERE id = 1 FOR UPDATE
// Row is locked until transaction commits/rollbacks!

// ✅ BEST for: High contention, financial transactions, inventory
```

## Comparison:

| Feature | Optimistic | Pessimistic |
|---|---|---|
| **Lock type** | Version check | DB row lock |
| **Performance** | Better (no actual lock) | Slower (DB lock) |
| **Conflict handling** | Exception on conflict | Prevents conflict |
| **Deadlock risk** | None | Possible |
| **Use when** | Low contention | High contention |
| **Implementation** | @Version annotation | @Lock annotation |

---

# 4️⃣ SOFT DELETE

## Hard Delete vs Soft Delete:

```java
// Hard Delete: DELETE FROM employee WHERE id = 1  ← Data gone forever!
// Soft Delete: UPDATE employee SET deleted = true WHERE id = 1  ← Data hidden

// ✅ BEST for: Audit trail, data recovery, legal requirements

@Entity
@Where(clause = "deleted = false")  // ← MAGIC! All queries auto-filter!
@SQLDelete(sql = "UPDATE employee SET deleted = true WHERE id = ?")  // DELETE → UPDATE
public class Employee {
    @Id private Long id;
    private String name;
    
    private boolean deleted = false;  // Soft delete flag
}

// Now:
// employeeRepository.findAll() → Only non-deleted employees (auto-filtered!)
// employeeRepository.deleteById(1L) → UPDATE employee SET deleted = true (not DELETE!)
// employeeRepository.findById(1L) → Empty (filtered by @Where)
```

---

# 5️⃣ SPRING DATA JPA AUDITING

## Auto-track who created/modified and when:

```java
// Step 1: Enable Auditing
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // Get current logged-in user from SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return Optional.of(auth != null ? auth.getName() : "system");
        };
    }
}

// Step 2: Base Entity (extend all entities from this)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
}

// Step 3: Use in entities
@Entity
public class Employee extends BaseEntity {
    @Id private Long id;
    private String name;
    // createdAt, updatedAt, createdBy, updatedBy auto-managed!
}
```

## Hibernate Envers (Full Audit Log):

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-envers</artifactId>
</dependency>
```

```java
@Entity
@Audited  // ← All changes tracked in audit table!
public class Employee {
    @Id private Long id;
    private String name;
    private BigDecimal salary;
}

// Creates: employee_aud table
// | id | rev | revtype | name   | salary |
// | 1  | 1   | 0(ADD)  | Rahul  | 50000  |
// | 1  | 2   | 1(MOD)  | Rahul  | 60000  |
// | 1  | 3   | 2(DEL)  | NULL   | NULL   |

// Query audit history:
AuditReader reader = AuditReaderFactory.get(entityManager);
List<Number> revisions = reader.getRevisions(Employee.class, 1L);
Employee oldState = reader.find(Employee.class, 1L, revisions.get(0));
```

---

# 6️⃣ PERFORMANCE TUNING

## Batch Insert/Update (Critical for bulk operations!)

```yaml
# application.properties
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

```java
@Service
public class BulkService {
    
    @Transactional
    public void bulkInsert(List<Employee> employees) {
        for (int i = 0; i < employees.size(); i++) {
            employeeRepository.save(employees.get(i));
            if (i % 50 == 0) {
                entityManager.flush();  // Push to DB
                entityManager.clear();  // Clear PC to free memory!
            }
        }
    }
}
```

## Read-Only Optimization:

```java
// For reports/exports where you only READ data
@Transactional(readOnly = true)
public List<Employee> generateReport() {
    // - No dirty checking (fast!)
    // - No PC snapshot saved (less memory!)
    // - DB can optimize (read-only transaction)
}

// Entity-level read-only hint
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
```

## Important Properties:

```yaml
# Show SQL with parameters (dev only!)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# SQL logging with bind parameters
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Statistics (dev only - performance overhead!)
spring.jpa.properties.hibernate.generate_statistics=true

# Connection pool (HikariCP - default in Spring Boot)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.connection-timeout=20000
```

---

# 7️⃣ CUSTOM TYPES & CONVERTERS

## JPA @Converter (Store custom types):

```java
// Store List<String> as comma-separated string
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    
    @Override
    public String convertToDatabaseColumn(List<String> list) {
        return list == null ? null : String.join(",", list);
    }
    
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Arrays.asList(dbData.split(","));
    }
}

@Entity
public class Employee {
    @Id private Long id;
    
    @Convert(converter = StringListConverter.class)
    @Column(name = "skills")
    private List<String> skills;  // DB: "Java,Spring,React"
}
```

## JSON Column with Hibernate:

```java
// PostgreSQL JSONB column
@Type(type = "jsonb")
@Column(columnDefinition = "jsonb")
private Map<String, Object> metadata;

// Or use custom converter
@Converter(autoApply = true)
public class JsonConverter implements AttributeConverter<Object, String> {
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try { return mapper.writeValueAsString(attribute); }
        catch (JsonProcessingException e) { throw new RuntimeException(e); }
    }
    
    @Override
    public Object convertToEntityAttribute(String dbData) {
        try { return mapper.readValue(dbData, Object.class); }
        catch (JsonProcessingException e) { throw new RuntimeException(e); }
    }
}
```

---

# 8️⃣ TOP 50 INTERVIEW Q&A (3.5+ Yrs Level)

## Basic-Medium (Must Know):

**Q1: JPA vs Hibernate vs Spring Data JPA?**
> JPA = Specification (interface), Hibernate = JPA implementation, Spring Data JPA = Abstraction over JPA (auto-queries, pagination, reduces boilerplate)

**Q2: What is Persistence Context?**
> First-level cache (L1) where Hibernate tracks managed entities. Auto-detects changes via dirty checking. Same entity in same transaction = no duplicate DB queries.

**Q3: Difference between persist() and merge()?**
> persist() = INSERT new entity (throws exception if detached), merge() = INSERT or UPDATE (copies detached entity into PC, returns managed instance)

**Q4: What is N+1 problem and solutions?**
> 1 query loads N parent records, then N queries load children. Solutions: JOIN FETCH, @EntityGraph, @BatchSize, DTO Projection

**Q5: FetchType.LAZY vs EAGER?**
> LAZY = load on access (default for @OneToMany/@ManyToMany), EAGER = load immediately (default for @ManyToOne/@OneToOne). Always prefer LAZY.

**Q6: What is Dirty Checking?**
> Hibernate compares current entity state with original snapshot in PC. If changed, auto-generates UPDATE on flush. No explicit save() needed for managed entities.

**Q7: @Transactional propagation types?**
> REQUIRED (default - join existing or create new), REQUIRES_NEW (always new, suspends existing), SUPPORTS, NOT_SUPPORTED, MANDATORY, NEVER, NESTED

**Q8: CascadeType.ALL safe for ManyToMany?**
> NO! Deleting one side will cascade REMOVE to other side, deleting shared data. Use {PERSIST, MERGE} only for ManyToMany.

**Q9: orphanRemoval vs CascadeType.REMOVE?**
> CascadeType.REMOVE = parent delete → all children delete. orphanRemoval = child removed from parent's collection → that specific child deleted (selective).

**Q10: Difference between find() and getReference()?**
> find() = hits DB immediately, returns null if not found. getReference() = returns lazy proxy, hits DB only on field access, throws EntityNotFoundException.

## Medium-Advanced (3.5+ Yrs Expected):

**Q11: How does @Version work for optimistic locking?**
> @Version adds version column. On UPDATE, Hibernate adds "WHERE version=X". If version changed by another transaction → 0 rows updated → OptimisticLockException.

**Q12: PESSIMISTIC_READ vs PESSIMISTIC_WRITE?**
> PESSIMISTIC_READ = shared lock (SELECT ... LOCK IN SHARE MODE) - others can read. PESSIMISTIC_WRITE = exclusive lock (SELECT ... FOR UPDATE) - others blocked.

**Q13: L1 vs L2 cache?**
> L1 = per Session, always on, cleared on transaction end. L2 = per SessionFactory, optional, shared across sessions, needs @Cacheable + provider (Ehcache).

**Q14: When to use Specification vs @Query?**
> @Query = fixed query at development time. Specification = dynamic filters at runtime (search forms with optional parameters). Use Specification when filter combinations vary.

**Q15: How to handle batch inserts in JPA?**
> Set hibernate.jdbc.batch_size=50, use flush()+clear() in loop to prevent OOM, set order_inserts=true. For massive data, consider Spring Batch or native SQL.

**Q16: save() vs saveAndFlush()?**
> save() = persist to PC, SQL executed on flush/commit. saveAndFlush() = persist + immediate flush to DB (SQL executed immediately).

**Q17: deleteAll() vs deleteAllInBatch()?**
> deleteAll() = loads all entities, deletes one by one (N queries + dirty checking). deleteAllInBatch() = single DELETE query (fast, no entity loading, but no cascading/callbacks).

**Q18: How to implement soft delete?**
> Add boolean deleted field. Use @Where(clause="deleted=false") for auto-filtering. Use @SQLDelete for converting DELETE to UPDATE.

**Q19: What is @EntityGraph?**
> Declarative way to override LAZY fetching. Fetches specified associations in single query. Uses LEFT JOIN. Alternative to JOIN FETCH without writing JPQL.

**Q20: Spring Data JPA Projection types?**
> Interface-based (proxy, simplest), Class-based DTO (constructor expression in JPQL), Dynamic (generic method with Class<T> parameter).

**Q21: How does Hibernate generate SQL from method names?**
> Spring Data JPA parses method name (findByNameAndDept → WHERE name=? AND dept=?), builds JPA Criteria query, generates SQL at runtime via query builder strategy.

**Q22: @Query JPQL vs Native SQL?**
> JPQL = entity/field names, portable, returns entities. Native SQL = table/column names, DB-specific, returns Object[] or entities with mapping.

**Q23: What is @Modifying?**
> Required for @Query with UPDATE/DELETE. Must be used with @Transactional. Returns number of affected rows.

**Q24: How to prevent LazyInitializationException?**
> 1. @Transactional (keep session open), 2. JOIN FETCH (eager load in query), 3. @EntityGraph, 4. DTO projection, 5. Hibernate.initialize()

**Q25: Inheritance mapping strategies?**
> SINGLE_TABLE (1 table + discriminator, fastest, nullable columns), JOINED (1 table per class + FK joins, normalized), TABLE_PER_CLASS (1 table per concrete class, UNION queries)

## Advanced (Senior Level):

**Q26: How does Hibernate dirty checking work internally?**
> At load time, Hibernate stores a snapshot of entity state. On flush, it compares current state with snapshot using bytecode enhancement or reflection. Only changed fields generate UPDATE.

**Q27: What is the difference between Session and EntityManager?**
> Session = Hibernate-native API. EntityManager = JPA standard API. Spring Data JPA uses EntityManager internally. Session can be unwrapped from EntityManager.

**Q28: How to handle concurrent updates without @Version?**
> Use Pessimistic locking (@Lock), or manually check a last_updated timestamp, or use database triggers, or compare all fields (full optimistic lock).

**Q29: What is Hibernate bytecode enhancement?**
> Compile-time instrumentation that enhances entity classes for: lazy loading at field level (not just collections), dirty tracking at field level, bi-directional association management.

**Q30: When to avoid JPA and use JDBC/JdbcTemplate?**
> Complex reporting queries with many joins/aggregations, batch processing of millions of records, DB-specific features not supported by JPA, performance-critical paths with fine-grained SQL control.

**Q31: How to implement multi-tenant in Spring Data JPA?**
> Schema per tenant (Hibernate multi-tenant), Database per tenant, Discriminator column. Use TenantIdentifierResolver + connection provider.

**Q32: What is @DynamicUpdate?**
> Default Hibernate updates ALL columns. @DynamicUpdate makes it update ONLY changed columns. Useful for large entities with few changes. Reduces DB write overhead.

**Q33: @Immutable entity?**
> @Immutable tells Hibernate the entity will never be modified. Enables optimizations: no dirty checking, read-only PC operations. Used for reference/lookup tables.

**Q34: How to debug N+1 in production?**
> Enable hibernate.generate_statistics, use interceptors to count queries, logging.level.org.hibernate.SQL=DEBUG, Spring Actuator metrics, New Relic/DataDog APM monitoring.

**Q35: Connection pool best practices?**
> HikariCP (default in Spring Boot): max-pool-size = (core_count * 2) + effective_spindle_count, minimum-idle = 5, connection-timeout = 20000ms, idle-timeout = 300000ms.

**Q36: How does @NamedEntityGraph work?**
> Defines fetch plan at entity level (not query level). Can be overridden at query time. Reusable across multiple queries. Declared on entity class.

**Q37: What is DTO projection performance benefit?**
> No entity lifecycle management, no dirty checking, no PC overhead, only needed columns fetched, no lazy loading issues, serializable by default.

**Q38: How to handle large result sets?**
> Pagination (Pageable), Stream<T> with @Transactional, Cursor-based iteration, Spring Batch ChunkOrientedTasklet, Keyset pagination (seek method).

**Q39: @Transactional on class vs method?**
> Class level = applies to all public methods. Method level overrides class level. Only public methods are proxied. Self-invocation bypasses proxy.

**Q40: What happens if no @Transactional?**
> Each repository call = separate transaction (auto-commit mode). No transaction boundaries. Lazy loading may fail. Multiple DB operations not atomic.
