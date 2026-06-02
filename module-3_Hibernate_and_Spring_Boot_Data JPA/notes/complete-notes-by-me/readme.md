## GitHub Repository Structure for JPA Notes

Repository naam rakho: `Hibernate-SpringDataJPA-Complete-Notes`

```
Hibernate-SpringDataJPA-Complete-Notes/
│
│── 📄 README.md                          ← Start here (reading order + index)
│
│── 01-Basics/
│   ├── README_JPA_RELATIONSHIPS.md       ← OneToOne, OneToMany, ManyToOne, ManyToMany
│   ├── onetoone/
│   │   ├── User.java
│   │   └── Passport.java
│   ├── onetomany/
│   │   ├── Department.java
│   │   ├── Employee.java
│   │   ├── EmployeeInsertExample.java
│   │   ├── DepartmentRepository.java
│   │   └── EmployeeRepository.java
│   └── manytomany/
│       ├── Student.java
│       └── Course.java
│
│── 02-Core-Deep-Dive/
│   └── JPA_CORE_DEEP_DIVE.md            ← Hibernate vs JPA, Lifecycle, EntityManager, Pagination, Projection, Interfaces
│
│── 03-Advanced-Concepts/
│   └── JPA_ADVANCED_CONCEPTS.md          ← CascadeType, @Transactional, FetchType, orphanRemoval, N+1 Problem
│
│── 04-Dynamic-Queries/
│   └── JPA_DYNAMIC_QUERIES.md            ← Method Naming, @Query, Specification, Criteria API, QueryDSL
│
│── 05-Interview-Master/
│   └── JPA_INTERVIEW_MASTER.md           ← Inheritance, Caching, Locking, Soft Delete, Auditing, 40 Q&A
│
│── 06-Remaining-Topics/
│   └── JPA_REMAINING_TOPICS.md           ← @Embeddable, Custom Repo, Testing, Validation, Flyway, REST, Complete Checklist
│
└── .gitkeep                              ← (if any empty folder)
```

### GitHub pe banana:
1. New repo banao: `Hibernate-SpringDataJPA-Complete-Notes`
2. `01-Basics/README_JPA_RELATIONSHIPS.md` path likho → folder auto-banega
3. `01-Basics/onetoone/User.java` path likho → subfolder auto-banega
4. Aise sab files ek ek karke banao

### README.md content (repo ka main index):
```markdown
# Hibernate & Spring Data JPA - Complete Notes

## 📖 Reading Order:
1. [01-Basics - Relationships](01-Basics/README_JPA_RELATIONSHIPS.md)
2. [02-Core Deep Dive](02-Core-Deep-Dive/JPA_CORE_DEEP_DIVE.md)
3. [03-Advanced Concepts](03-Advanced-Concepts/JPA_ADVANCED_CONCEPTS.md)
4. [04-Dynamic Queries](04-Dynamic-Queries/JPA_DYNAMIC_QUERIES.md)
5. [05-Interview Master](05-Interview-Master/JPA_INTERVIEW_MASTER.md)
6. [06-Remaining Topics](06-Remaining-Topics/JPA_REMAINING_TOPICS.md)

**Total: 6 MD files + 8 Java files | 50+ topics | 40 Interview Q&A**
```
