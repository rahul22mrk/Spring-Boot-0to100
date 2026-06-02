# JPA Advanced Concepts - Complete Guide (Hindi + English)

---

## 📌 TOPICS COVERED

1. **CascadeType** - Parent operations child tak kaise propagate hote hain
2. **@Transactional** - Transactions kaise kaam karte hain relationships mein
3. **FetchType** - Data kab load hoga (EAGER vs LAZY)
4. **orphanRemoval** - Bachha (child) parent se hatne pe delete ho ya nahi
5. **N+1 Query Problem** - Sabse bada performance killer aur uska solution

---

# 1️⃣ CASCADE TYPE

## Kya hai Cascade?

```
Cascade = "Prapaat karna" ya "Aage bhejna"

Jab Parent pe koi operation karo, wo Child tak bhi propagate ho!
Example: Department delete karo → Employees bhi delete ho jayen
```

## Sabhi CascadeType ke Types:

| CascadeType | Kya karta hai | Real-World Example |
|---|---|---|
| **ALL** | Sab operations propagate karo | Department delete = Employees delete |
| **PERSIST** | Save/Insert propagate karo | Department save = Employees bhi save |
| **MERGE** | Update propagate karo | Department update = Employees bhi update |
| **REMOVE** | Delete propagate karo | Department delete = Employees bhi delete |
| **REFRESH** | DB se fresh data reload karo | Department refresh = Employees bhi refresh |
| **DETACH** | Entity ko persistence context se hatao | Department detach = Employees bhi detach |

## Visual Explanation:

```
┌─────────────────────────────────────────────────────┐
│              CASCADE FLOW - Parent → Child           │
├─────────────────────────────────────────────────────┤
│                                                       │
│  Department (Parent)                                  │
│  ├── save()    ──PERSIST──→  Employees bhi save       │
│  ├── delete()  ──REMOVE───→  Employees bhi delete     │
│  ├── update()  ──MERGE────→  Employees bhi update     │
│  ├── refresh() ──REFRESH──→  Employees bhi refresh    │
│  └── detach()  ──DETACH───→  Employees bhi detach     │
│                                                       │
│  cascade = CascadeType.ALL → Sab operations propagate │
└─────────────────────────────────────────────────────┘
```

## Code Examples:

### CascadeType.PERSIST (Save/Insert only)
```java
// Sirf save operation propagate hoga
@OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST)
private List<Employee> employees = new ArrayList<>();

// Kya hoga:
Department d = new Department("IT");
d.addEmployee(new Employee("Rahul"));
departmentRepository.save(d); // ✅ Department + Employee dono save

// Kya NAHI hoga:
departmentRepository.delete(d); // ❌ Sirf Department delete hoga, Employee NAHI!
                                 // Employee DB mein rahega, FK null ho jayega
```

### CascadeType.MERGE (Update only)
```java
// Sirf update operation propagate hoga
@OneToMany(mappedBy = "department", cascade = CascadeType.MERGE)
private List<Employee> employees = new ArrayList<>();

// Kya hoga:
Department d = departmentRepository.findById(1L).get();
d.setName("IT-Updated");
d.getEmployees().get(0).setName("Rahul-Updated");
departmentRepository.save(d); // ✅ Department + Employee dono update

// Kya NAHI hoga:
Department newD = new Department("HR");
newD.addEmployee(new Employee("New Emp"));
departmentRepository.save(newD); // ❌ Sirf Department save, Employee NAHI!
```

### CascadeType.REMOVE (Delete only)
```java
// Sirf delete operation propagate hoga
@OneToMany(mappedBy = "department", cascade = CascadeType.REMOVE)
private List<Employee> employees = new ArrayList<>();

// Kya hoga:
Department d = departmentRepository.findById(1L).get();
departmentRepository.delete(d); // ✅ Department + saare Employees delete

// Kya NAHI hoga:
Department newD = new Department("IT");
newD.addEmployee(new Employee("Rahul"));
departmentRepository.save(newD); // ❌ Sirf Department save, Employee NAHI save!
```

### CascadeType.ALL (Sab operations)
```java
// SAB operations propagate honge
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
private List<Employee> employees = new ArrayList<>();

// ✅ Save, Update, Delete, Refresh, Detach - sab propagate hoga
```

### ⚠️ DANGER: CascadeType.REMOVE aur CascadeType.ALL in ManyToMany

```java
// ❌ DANGEROUS in ManyToMany!
@ManyToMany(cascade = CascadeType.ALL) // MAT KARO!
private List<Course> courses = new ArrayList<>();

// Kyun dangerous?
// Agar Student delete karo → Course bhi delete ho jayega!
// Lekin Course dusre Students ka bhi hai! Unka data bhi ud jayega!
// ManyToMany mein sirf PERSIST aur MERGE use karo

// ✅ SAFE in ManyToMany
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private List<Course> courses = new ArrayList<>();
```

### Cascade Direction samjho:

```
CASCADE HAMESHA EK DIRECTION MEIN KAAM KARTA HAI!

Case 1: Department → Employee (cascade on Department side)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Department pe cascade hai → Department ka operation Employee tak jayega
Employee ka operation Department tak NAHI jayega!

Case 2: Agar Employee pe bhi cascade lagao (DONO SIDE PE CASCADE!)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// ❌ AISA MAT KARO - Infinite recursion danger!
@ManyToOne(cascade = CascadeType.ALL)  // Employee pe cascade
private Department department;

@OneToMany(cascade = CascadeType.ALL)  // Department pe bhi cascade
private List<Employee> employees;

// Employee delete → Department delete → Baaki Employees delete → ...
// Yeh infinite loop ban sakta hai! CRASH!
```

### CASCADE QUICK REFERENCE:

```
┌──────────────────────────────────────────────────────────────┐
│              KAUNSA CASCADE KABHAN USE KARO?                  │
├──────────────────┬───────────────────────────────────────────┤
│ OneToOne         │ cascade = CascadeType.ALL                 │
│ (User-Passport)  │ (User delete = Passport delete, OK!)     │
├──────────────────┼───────────────────────────────────────────┤
│ OneToMany        │ cascade = CascadeType.ALL                 │
│ (Dept-Employee)  │ orphanRemoval = true                      │
│                  │ (Dept delete = Employees delete, OK!)     │
├──────────────────┼───────────────────────────────────────────┤
│ ManyToOne        │ cascade = ❌ NAHI LAGANA!                 │
│ (Employee-Dept)  │ (Employee delete se Dept mat delete karo!)│
├──────────────────┼───────────────────────────────────────────┤
│ ManyToMany       │ cascade = {PERSIST, MERGE}                │
│ (Student-Course) │ ❌ ALL ya REMOVE mat lagana!              │
│                  │ (Student delete se Course mat delete!)     │
└──────────────────┴───────────────────────────────────────────┘
```

---

# 2️⃣ @TRANSACTIONAL IN RELATIONSHIPS

## Transaction kya hai?

```
Transaction = "Ek unit of work" - ya toh POORA complete ho ya KUCH bhi na ho

Example: Bank Transfer
- Rahul se 500₹ deduct karo
- Amit ko 500₹ add karo
- Agar beech mein error aaye → DONO operations undo ho jayen (ROLLBACK)
- Agar sab successful → DONO operations commit ho jayen (COMMIT)
```

## @Transactional kyun zaroori hai relationships mein?

```java
// Bina @Transactional ke kya hota hai:
public void addEmployeesToDepartment() {
    Department d = new Department("IT");
    d.addEmployee(new Employee("Rahul"));  // Operation 1
    d.addEmployee(new Employee("Amit"));   // Operation 2
    
    departmentRepository.save(d);          // Save Department
    
    // Agar yahan error aaye???
    // Department save ho gaya, lekin Employees nahi!
    // DATA INCONSISTENT ho gaya!
}

// @Transactional se kya hota hai:
@Transactional
public void addEmployeesToDepartment() {
    Department d = new Department("IT");
    d.addEmployee(new Employee("Rahul"));  // Operation 1
    d.addEmployee(new Employee("Amit"));   // Operation 2
    
    departmentRepository.save(d);          // Sab ek saath commit hoga
    
    // Agar error aaye → POORA transaction ROLLBACK hoga
    // Na Department save hoga, na Employees!
    // DATA CONSISTENT rahega!
}
```

## @Transactional ke Important Properties:

| Property | Kya karta hai | Default Value |
|---|---|---|
| **readOnly** | Sirf read karo, no write | false |
| **timeout** | Kitne seconds mein complete hona chahiye | -1 (no timeout) |
| **rollbackFor** | Kis exception pe rollback kare | RuntimeException |
| **noRollbackFor** | Kis exception pe rollback NAHI kare | - |
| **propagation** | Transaction kaise propagate ho | REQUIRED |
| **isolation** | Data isolation level | DEFAULT |

## Propagation Types (Sabse Important):

```
┌──────────────────────────────────────────────────────────────┐
│                  PROPAGATION TYPES                            │
├──────────────────┬───────────────────────────────────────────┤
│ REQUIRED         │ Agar transaction hai toh usme chalo,      │
│ (DEFAULT)        │ nahi toh naya banao                       │
├──────────────────┼───────────────────────────────────────────┤
│ REQUIRES_NEW     │ Hamesha NAYA transaction banao,           │
│                  │ purana suspend karo                        │
├──────────────────┼───────────────────────────────────────────┤
│ SUPPORTS         │ Agar transaction hai toh chalo,           │
│                  │ nahi toh bina transaction ke chalo         │
├──────────────────┼───────────────────────────────────────────┤
│ NOT_SUPPORTED    │ Bina transaction ke chalo,                │
│                  │ agar hai toh suspend karo                  │
├──────────────────┼───────────────────────────────────────────┤
│ MANDATORY        │ Transaction HONA zaroori hai,              │
│                  │ nahi toh error aayega                      │
├──────────────────┼───────────────────────────────────────────┤
│ NEVER            │ Transaction NAHI hona chahiye,             │
│                  │ hai toh error aayega                       │
├──────────────────┼───────────────────────────────────────────┤
│ NESTED           │ Nested transaction banao                   │
│                  │ (parent fail ho toh child bhi fail)        │
└──────────────────┴───────────────────────────────────────────┘
```

## Real-World Code Examples:

### Example 1: Basic @Transactional (Most Common)
```java
@Service
public class EmployeeService {

    @Transactional // Sab operations ek unit mein - ya sab honge ya kuch nahi
    public void createDepartmentWithEmployees() {
        Department dept = new Department("IT");
        
        Employee e1 = new Employee("Rahul");
        Employee e2 = new Employee("Amit");
        
        dept.addEmployee(e1);
        dept.addEmployee(e2);
        
        departmentRepository.save(dept);
        // Agar yahan tak sab theek → COMMIT (sab DB mein save)
        // Agar error aaye → ROLLBACK (kuch bhi save nahi)
    }
}
```

### Example 2: readOnly = true (Optimization)
```java
@Service
public class EmployeeService {

    @Transactional(readOnly = true) // Sirf read karo, write mat karo
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
        // readOnly = true se:
        // - Hibernate dirty checking nahi karta (fast!)
        // - DB ko pata hai ye read-only hai (optimization)
        // - Write karne ki koshish pe error aayega
    }
}
```

### Example 3: rollbackFor (Checked Exceptions ke liye)
```java
@Service
public class EmployeeService {

    // By default sirf RuntimeException pe rollback hota hai
    // Checked exceptions (IOException etc.) pe rollback NAHI hota
    // rollbackFor se explicitly batate hain kis exception pe rollback karna hai
    
    @Transactional(rollbackFor = Exception.class) // SAB exceptions pe rollback
    public void transferEmployee(Long empId, Long newDeptId) throws IOException {
        Employee emp = employeeRepository.findById(empId).get();
        Department newDept = departmentRepository.findById(newDeptId).get();
        
        emp.setDepartment(newDept);
        employeeRepository.save(emp);
        
        // Agar yahan IOException aaye → bhi ROLLBACK hoga!
        // Bina rollbackFor ke → IOException pe commit ho jata (DATA CORRUPT!)
        someOperationThatMayThrowIOException();
    }
}
```

### Example 4: REQUIRES_NEW (Independent Transaction)
```java
@Service
public class EmployeeService {

    @Transactional
    public void processAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        
        for (Employee emp : employees) {
            try {
                processEmployee(emp); // Naya transaction banega
            } catch (Exception e) {
                // Ye employee fail hua, lekin baaki employees process honge!
                logError(emp, e); // Ye bhi alag transaction mein
            }
        }
        // REQUIRES_NEW se: Ek employee fail = sirf uska transaction rollback
        // Baaki employees ka transaction safe!
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEmployee(Employee emp) {
        // Hamesha NAYA transaction banega
        // Parent transaction suspend hoga
        // Agar yahan error → sirf ye transaction rollback, parent SAFE
        emp.setProcessed(true);
        employeeRepository.save(emp);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(Employee emp, Exception e) {
        // Error log bhi alag transaction mein
        // Taaki error logging kabhi fail na ho
        ErrorLog log = new ErrorLog(emp.getName(), e.getMessage());
        errorLogRepository.save(log);
    }
}
```

### ⚠️ COMMON MISTAKE: Self-Invocation (Same class mein method call)

```java
@Service
public class EmployeeService {

    @Transactional
    public void method1() {
        // Ye kaam karega - Spring proxy intercept karega
    }

    public void method2() {
        // ❌ YE @Transactional KAAM NAHI KAREGA!
        this.method1(); // Self-invocation - proxy bypass ho jata hai!
        
        // Kyun? Spring @Transactional ko proxy ke through implement karta hai
        // Jab tum this.method1() karte ho, proxy bypass ho jata hai
        // Isliye @Transactional kaam nahi karta!
    }
}

// ✅ SOLUTION: Different beans mein call karo ya AopContext use karo
```

---

# 3️⃣ FETCH TYPE

## FetchType kya hai?

```
FetchType = "Data KAB load karna hai"

Jab parent entity load ho, toh child data TURANT load ho (EAGER)
ya jab ACTUALLY access karo tab load ho (LAZY)?
```

## Dono Types:

| FetchType | Kya karta hai | Default ( kis pe ) | SQL Query |
|---|---|---|---|
| **EAGER** | Data TURANT load hota hai | @ManyToOne, @OneToOne | JOIN / subselect |
| **LAZY** | Data sirf ACCESS karne pe load hota hai | @OneToMany, @ManyToMany | Separate query jab access karo |

## Visual Explanation:

```
┌──────────────────────────────────────────────────────────────┐
│                    EAGER vs LAZY                              │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  EAGER (Turant Load):                                         │
│  ─────────────────────                                         │
│  departmentRepository.findById(1L);                           │
│  SQL: SELECT * FROM department d                               │
│       JOIN employee e ON d.id = e.department_id               │
│       WHERE d.id = 1                                          │
│  → Department + SAARE Employees ek hi query mein load!        │
│  → Problem: Agar 1000 employees hain toh SAB load honge!     │
│                                                                │
│  LAZY (Sirf jab chahiye):                                    │
│  ───────────────────────────                                  │
│  Department d = departmentRepository.findById(1L);            │
│  SQL: SELECT * FROM department WHERE id = 1                   │
│  → Sirf Department load hua, Employees NAHI! (Fast!)          │
│                                                                │
│  d.getEmployees(); // AB employees load honge                  │
│  SQL: SELECT * FROM employee WHERE department_id = 1          │
│  → Sirf ab employees load hue! (On-demand!)                   │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## Code Examples:

### FetchType.EAGER (Default for @ManyToOne, @OneToOne)
```java
// Employee entity mein
@ManyToOne(fetch = FetchType.EAGER) // DEFAULT for ManyToOne
@JoinColumn(name = "department_id")
private Department department;

// Kya hota hai:
Employee emp = employeeRepository.findById(1L).get();
// SQL executed: SELECT e.*, d.* 
//              FROM employee e 
//              LEFT JOIN department d ON e.department_id = d.id
//              WHERE e.id = 1
// Department bhi turant load ho gaya! (Chahiye ya nahi!)
```

### FetchType.LAZY (Default for @OneToMany, @ManyToMany)
```java
// Department entity mein
@OneToMany(fetch = FetchType.LAZY) // DEFAULT for OneToMany
private List<Employee> employees = new ArrayList<>();

// Kya hota hai:
Department d = departmentRepository.findById(1L).get();
// SQL executed: SELECT * FROM department WHERE id = 1
// Employees ABHI load NAHI hue! Sirf proxy object hai

d.getEmployees().size(); // AB employees load honge
// SQL executed: SELECT * FROM employee WHERE department_id = 1
// Sirf ab employees load hue! On-demand!
```

### ⚠️ LAZY Initialization Exception (Sabse Common Error!)

```java
@Service
public class DepartmentService {

    @Transactional
    public Department getDepartment(Long id) {
        Department d = departmentRepository.findById(id).get();
        d.getEmployees().size(); // ✅ Works! Transaction active hai
        return d;
    }

    // ❌ PROBLEM: Transaction ke bahar lazy data access karna
    public DepartmentDTO getDepartmentDTO(Long id) {
        Department d = departmentRepository.findById(id).get();
        // Transaction commit ho gaya (no @Transactional)
        
        return new DepartmentDTO(
            d.getName(),
            d.getEmployees().size()  // ❌ LazyInitializationException!
            // Error: "could not initialize proxy - no Session"
            // Kyunki transaction end ho gaya, Hibernate Session closed hai
            // Employees load nahi ho paye!
        );
    }
}
```

### ✅ Solutions for LazyInitializationException:

```java
// SOLUTION 1: @Transactional (Simple)
@Transactional(readOnly = true)
public DepartmentDTO getDepartmentDTO(Long id) {
    Department d = departmentRepository.findById(id).get();
    return new DepartmentDTO(
        d.getName(),
        d.getEmployees().size()  // ✅ Works! Transaction abhi active hai
    );
}

// SOLUTION 2: JOIN FETCH (Best for specific queries)
@Query("SELECT d FROM Department d JOIN FETCH d.employees WHERE d.id = :id")
Department findByIdWithEmployees(@Param("id") Long id);

// SOLUTION 3: @EntityGraph (Best for multiple fetch groups)
@EntityGraph(attributePaths = {"employees"})
@Query("SELECT d FROM Department d WHERE d.id = :id")
Department findByIdWithEmployeesEntityGraph(@Param("id") Long id);

// SOLUTION 4: DTO Projection (Best practice - no entity leakage)
@Query("SELECT new com.gahub.server.dto.DepartmentDTO(d.name, COUNT(e)) " +
       "FROM Department d LEFT JOIN d.employees e WHERE d.id = :id " +
       "GROUP BY d.name")
DepartmentDTO getDepartmentDTO(@Param("id") Long id);
```

### BEST PRACTICE for FetchType:

```
┌──────────────────────────────────────────────────────┐
│           FETCH TYPE BEST PRACTICES                   │
├──────────────────────────────────────────────────────┤
│                                                        │
│  ✅ ALWAYS use LAZY (for all relationships)            │
│  ✅ Fetch data explicitly using JOIN FETCH              │
│  ✅ Use @EntityGraph for complex fetch plans            │
│  ✅ Use DTOs to avoid unnecessary data loading          │
│                                                        │
│  ❌ NEVER use EAGER (performance killer)                │
│  ❌ NEVER access lazy data outside transaction          │
│  ❌ NEVER use hibernate.enable_lazy_load_no_trans=true │
│     (This is a hack, not a solution!)                   │
│                                                        │
└──────────────────────────────────────────────────────┘
```

---

# 4️⃣ ORPHAN REMOVAL

## orphanRemoval kya hai?

```
Orphan = "Anaath" (Child jo parent se hat gaya ho)

orphanRemoval = true matlab:
Agar child ko parent se hata do, toh child DB se bhi DELETE kar do!

Parent bina child ke reh sakta hai,
Lekin child bina parent ke NAHI reh sakta!
```

## Visual Explanation:

```
┌──────────────────────────────────────────────────────────────┐
│              orphanRemoval = true                             │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  BEFORE:                                                      │
│  Department "IT" → [Emp1, Emp2, Emp3]                         │
│                                                                │
│  OPERATION:                                                   │
│  department.removeEmployee(emp3);                              │
│                                                                │
│  AFTER (orphanRemoval = true):                                │
│  Department "IT" → [Emp1, Emp2]                               │
│  Emp3 → DELETED from DB! (Anaath hai, delete kar do)          │
│                                                                │
│  AFTER (orphanRemoval = false):                               │
│  Department "IT" → [Emp1, Emp2]                               │
│  Emp3 → Still in DB! (department_id = NULL)                   │
│  Emp3 ab kisi department mein nahi hai, lekin DB mein hai     │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## Code Example:

```java
@Entity
public class Department {

    // orphanRemoval = true → Removed employees will be DELETED from DB
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public void removeEmployee(Employee emp) {
        employees.remove(emp);
        emp.setDepartment(null); // Parent se hata diya
        // Agar orphanRemoval = true → emp DB se DELETE ho jayega!
        // Agar orphanRemoval = false → emp DB mein rahega, department_id = NULL
    }
}
```

## Service Example:

```java
@Service
public class DepartmentService {

    @Transactional
    public void removeEmployeeFromDepartment(Long deptId, Long empId) {
        Department dept = departmentRepository.findById(deptId).get();
        Employee emp = employeeRepository.findById(empId).get();

        dept.removeEmployee(emp); // Helper method call
        
        // NO NEED to call employeeRepository.delete(emp)!
        // orphanRemoval = true se automatically delete ho jayega!
        
        departmentRepository.save(dept); // Save parent, child auto-delete
    }
}
```

## orphanRemoval vs CascadeType.REMOVE:

```
┌──────────────────────────────────────────────────────────────┐
│        orphanRemoval vs CascadeType.REMOVE                    │
├────────────────────────┬─────────────────────────────────────┤
│                        │                                      │
│  CascadeType.REMOVE    │  orphanRemoval = true                │
│                        │                                      │
├────────────────────────┼─────────────────────────────────────┤
│  Parent delete →       │  Child parent se hataya →            │
│  Child bhi delete      │  Child delete                        │
│                        │                                      │
├────────────────────────┼─────────────────────────────────────┤
│  Department delete =   │  department.getEmployees()           │
│  Saare employees       │    .remove(emp) =                    │
│  delete                │  Sirf wo ek employee delete           │
│                        │                                      │
├────────────────────────┼─────────────────────────────────────┤
│  Bulk delete           │  Selective delete                    │
│  (Sabko ek saath)      │  (Jisse hataya usko)                 │
│                        │                                      │
└────────────────────────┴─────────────────────────────────────┘

DONO use karna safe hai:
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)

CascadeType.ALL → Parent delete pe sab children delete
orphanRemoval → Ek ek karke child hatao toh wo delete
```

---

# 5️⃣ N+1 QUERY PROBLEM

## N+1 Problem kya hai?

```
N+1 Problem = 1 query se N extra queries ban jati hain!

Jab tum 1 query se N records laate ho,
aur har record ke liye ek alag query chalti hai related data ke liye,
toh total queries = 1 + N

Example: 100 departments = 1 + 100 = 101 queries! 😱
```

## Visual Explanation:

```
┌──────────────────────────────────────────────────────────────┐
│                    N+1 PROBLEM                                │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  CODE:                                                        │
│  List<Department> depts = departmentRepository.findAll();     │
│  for (Department d : depts) {                                 │
│      d.getEmployees().size(); // LAZY - har department ke     │
│                                // liye alag query!             │
│  }                                                             │
│                                                                │
│  QUERIES EXECUTED:                                            │
│  ─────────────────                                            │
│  Query 1: SELECT * FROM department                    (1)     │
│  Query 2: SELECT * FROM employee WHERE dept_id = 1    (N=1)  │
│  Query 3: SELECT * FROM employee WHERE dept_id = 2    (N=2)  │
│  Query 4: SELECT * FROM employee WHERE dept_id = 3    (N=3)  │
│  ...                                                           │
│  Query 101: SELECT * FROM employee WHERE dept_id = 100       │
│                                                                │
│  TOTAL: 1 + 100 = 101 QUERIES! 💀                             │
│  Agar 1000 departments = 1001 queries!                         │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

## N+1 Problem kahan aata hai?

```
N+1 tab aata hai jab:
1. FetchType.LAZY hai aur loop mein access karte ho
2. FetchType.EAGER hai (automatic N+1 ban jata hai!)

EAGER se bhi N+1 hota hai:
━━━━━━━━━━━━━━━━━━━━━━━━━━
@ManyToOne(fetch = FetchType.EAGER) // Default bhi EAGER hai!
private Department department;

List<Employee> employees = employeeRepository.findAll();
// Query 1: SELECT * FROM employee
// Query 2: SELECT * FROM department WHERE id = 1
// Query 3: SELECT * FROM department WHERE id = 2 (even if same!)
// Query 4: SELECT * FROM department WHERE id = 1 (DUPLICATE!)

Lekin EAGER mein Hibernate optimization kar sakta hai,
but LAZY + loop mein access = GUARANTEED N+1!
```

## ✅ Solutions for N+1 Problem:

### Solution 1: JOIN FETCH (Most Common)

```java
// Repository mein custom query likho

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // ❌ N+1 Problem wali query
    // findAll() se N+1 hoga

    // ✅ Solution: JOIN FETCH - Ek hi query mein sab data!
    @Query("SELECT d FROM Department d JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();

    // ✅ Specific department ke liye
    @Query("SELECT d FROM Department d JOIN FETCH d.employees WHERE d.id = :id")
    Department findByIdWithEmployees(@Param("id") Long id);
}

// Kya hota hai JOIN FETCH se:
// SQL: SELECT d.*, e.* 
//      FROM department d 
//      JOIN employee e ON d.id = e.department_id
// 
// Sirf 1 QUERY! Sab data ek saath! No N+1!
```

### Solution 2: @EntityGraph (No JPQL needed)

```java
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // ✅ @EntityGraph - Declarative way to fetch associations
    @EntityGraph(attributePaths = {"employees"})
    List<Department> findAll();

    // Multiple associations
    @EntityGraph(attributePaths = {"employees", "employees.department"})
    List<Department> findAllWithDetails();
}

// @EntityGraph vs JOIN FETCH:
// @EntityGraph → LEFT JOIN (employees na ho toh bhi department aayega)
// JOIN FETCH    → INNER JOIN (sirf wahi departments jinke employees hain)
```

### Solution 3: Batch Fetching (For large collections)

```yaml
# application.properties mein

# Batch size = Kitne employees ek saath fetch karo
spring.jpa.properties.hibernate.default_batch_fetch_size=50
```

```java
@Entity
@BatchSize(size = 50) // 50 employees ek saath fetch karo (IN clause)
public class Department {
    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>();
}

// Batch Fetching se kya hota hai:
// Pehle: 100 departments ke liye = 101 queries (N+1)
// Batch ke baad: 100 departments ke liye = 2 queries!
//   Query 1: SELECT * FROM department
//   Query 2: SELECT * FROM employee 
//            WHERE department_id IN (1,2,3,...,50)  ← 50 ek saath!
//   Query 3: SELECT * FROM employee 
//            WHERE department_id IN (51,52,...,100) ← baaki 50!
// Total: 1 + ceil(100/50) = 3 queries! 🎉
```

### Solution 4: DTO Projection (Best Practice)

```java
// Interface-based DTO Projection (Simplest)
public interface DepartmentWithEmployeeCount {
    String getName();
    Long getEmployeeCount();
}

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // ✅ DTO Projection - Sirf wahi data lo jo chahiye!
    @Query("SELECT d.name AS name, COUNT(e) AS employeeCount " +
           "FROM Department d LEFT JOIN d.employees e " +
           "GROUP BY d.name")
    List<DepartmentWithEmployeeCount> findAllWithEmployeeCount();
    
    // ✅ Class-based DTO Projection
    @Query("SELECT new com.gahub.server.dto.DepartmentDTO(d.name, COUNT(e)) " +
           "FROM Department d LEFT JOIN d.employees e " +
           "GROUP BY d.name")
    List<DepartmentDTO> findAllAsDTO();
}

// DTO Projection se kya hota hai:
// SQL: SELECT d.name, COUNT(e.id) 
//      FROM department d 
//      LEFT JOIN employee e ON d.id = e.department_id
//      GROUP BY d.name
//
// Sirf 1 QUERY! Sirf wahi columns jo chahiye! No N+1!
// No entity loading overhead! Direct DTO!
```

### N+1 Solutions Comparison:

```
┌──────────────────────────────────────────────────────────────┐
│              N+1 SOLUTIONS COMPARISON                         │
├──────────────────┬───────────────────────────────────────────┤
│ Solution         │ Best For                                  │
├──────────────────┼───────────────────────────────────────────┤
│ JOIN FETCH       │ Specific queries where you know           │
│                  │ exactly what associations you need         │
├──────────────────┼───────────────────────────────────────────┤
│ @EntityGraph     │ When you want to override fetch plan      │
│                  │ without writing JPQL                       │
├──────────────────┼───────────────────────────────────────────┤
│ Batch Fetching   │ Global optimization for all queries       │
│                  │ Good default safety net                    │
├──────────────────┼───────────────────────────────────────────┤
│ DTO Projection   │ Best performance - only needed data       │
│                  │ No entity overhead at all                  │
└──────────────────┴───────────────────────────────────────────┘
```

---

## 🏆 ULTIMATE CHEAT SHEET - All Concepts Together

```
┌──────────────────────────────────────────────────────────────┐
│           JPA RELATIONSHIPS - ULTIMATE CHEAT SHEET            │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  CASCADE:                                                     │
│  OneToOne   → CascadeType.ALL                                 │
│  OneToMany  → CascadeType.ALL + orphanRemoval = true          │
│  ManyToOne  → ❌ No cascade                                   │
│  ManyToMany → {PERSIST, MERGE} only                           │
│                                                                │
│  FETCH:                                                       │
│  Always LAZY → Fetch explicitly via JOIN FETCH/@EntityGraph   │
│  Never EAGER → Causes N+1 and unnecessary loading             │
│                                                                │
│  TRANSACTION:                                                 │
│  Always @Transactional on service methods                     │
│  readOnly=true for read operations                            │
│  rollbackFor=Exception.class for checked exceptions           │
│                                                                │
│  ORPHAN REMOVAL:                                              │
│  Use with @OneToMany when child can't exist without parent    │
│  orphanRemoval=true + CascadeType.ALL = best combo            │
│                                                                │
│  N+1 PREVENTION:                                              │
│  1. JOIN FETCH for specific queries                           │
│  2. @EntityGraph for declarative fetching                     │
│  3. Batch size for global optimization                        │
│  4. DTO Projection for best performance                       │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```
