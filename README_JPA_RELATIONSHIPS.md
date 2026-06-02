# Spring Data JPA Relationships - Complete Guide (Hindi + English)

---

## 📌 QUICK SUMMARY TABLE

| Relationship | Annotation (Owner Side) | Example |
|---|---|---|
| **One To One** | `@OneToOne` | 1 User = 1 Passport |
| **One To Many** | `@OneToMany` | 1 Department → Many Employees |
| **Many To One** | `@ManyToOne` | Many Employees → 1 Department |
| **Many To Many** | `@ManyToMany` | Many Students ↔ Many Courses |

---

## 1️⃣ ONE TO ONE (@OneToOne)

### Real-World Example:
- **Ek User ka sirf EK Passport hota hai**
- **Ek Passport sirf EK User ka hota hai**
- User (1) ←→ (1) Passport

### Key Points:
- `@OneToOne` annotation dono side pe lagta hai
- Foreign Key sirf EK table mein hota hai (Owner side)
- `mappedBy` us side pe lagta hai jo FK nahi rakhta
- `cascade = CascadeType.ALL` matlab parent delete hone pe child bhi delete

### Database Tables:
```
user_table                    passport_table
-----------                   --------------
id (PK)                       id (PK)
name                          passport_number
email                         user_id (FK → user_table.id)
```

### Java Classes:
- **User.java** → Owner side (FK nahi hai, but relationship define karta hai)
- **Passport.java** → Inverse side (FK hai isme, `mappedBy` se link)

---

## 2️⃣ MANY TO ONE (@ManyToOne)

### Real-World Example:
- **Bahut saare Employees EK Department mein kaam kar sakte hain**
- **Par EK Employee sirf EK Department mein hota hai**
- Employee (Many) → (1) Department

### Key Points:
- `@ManyToOne` HAMESHA owner side hota hai (FK is side pe hota hai)
- `@JoinColumn` batata hai ki FK ka column kya naam hai
- Ye sabse simple relationship hai - FK child table mein aata hai
- Many side = Owner side = FK wali side

### Database Tables:
```
department_table              employee_table
--------------                --------------
id (PK)                       id (PK)
name                          emp_name
                              department_id (FK → department_table.id)
```

### Java Classes:
- **Department.java** → Inverse side (koi FK nahi)
- **Employee.java** → Owner side (FK isme hai)

---

## 3️⃣ ONE TO MANY (@OneToMany)

### Real-World Example:
- **EK Department mein Bahut saare Employees ho sakte hain**
- Department (1) → (Many) Employees

### Key Points:
- Ye `@ManyToOne` ka ULTA hai - same relationship, opposite perspective
- `@OneToMany` side pe `mappedBy` lagta hai (ye inverse side hai)
- FK hamesha "Many" wali table mein hota hai
- **IMPORTANT**: `@OneToMany` alone (without `@ManyToOne`) ek JOIN TABLE banata hai
- Agar JOIN TABLE nahi chahiye toh `@ManyToOne` + `@OneToMany` pair use karo

### Database Tables (Same as ManyToOne):
```
department_table              employee_table
--------------                --------------
id (PK)                       id (PK)
name                          emp_name
                              department_id (FK → department_table.id)
```

### Java Classes:
- **Department.java** → Inverse side (`mappedBy` se link)
- **Employee.java** → Owner side (`@ManyToOne` FK isme hai)

---

## 4️⃣ MANY TO MANY (@ManyToMany)

### Real-World Example:
- **Bahut saare Students EK Course le sakte hain**
- **EK Course mein Bahut saare Students ho sakte hain**
- Student (Many) ↔ (Many) Course

### Key Points:
- Third table (JOIN TABLE) automatically banti hai
- `@ManyToMany` dono side pe lagta hai
- Owner side pe `@JoinTable` define karte hain
- Inverse side pe `mappedBy` lagta hai
- **PRO TIP**: Real projects mein ManyToMany avoid karo, instead ek intermediate Entity banao

### Database Tables:
```
student_table                 course_table               student_course (JOIN TABLE)
-------------                 ------------               -------------------------
id (PK)                       id (PK)                    student_id (FK → student_table.id)
name                          course_name                course_id (FK → course_table.id)
```

---

## 🔑 CONFUSION KILLER - GOLDEN RULES

### Rule 1: FK (Foreign Key) hamesha "Many" wali side pe hota hai
```
OneToOne    → FK kisi bhi side pe ho sakta hai (choose owner)
ManyToOne   → FK "Many" side pe hai (Employee table)
OneToMany   → FK "Many" side pe hai (Employee table) - same as ManyToOne!
ManyToMany  → FK dono side pe nahi, ek alag JOIN TABLE banti hai
```

### Rule 2: `mappedBy` hamesha INVERSE (non-owner) side pe lagta hai
```
mappedBy = "field name of the opposite side"
Jab bhi mappedBy lagao, ye batata hai ki "ye side FK manage nahi karta,
dusri side karta hai"
```

### Rule 3: Owner Side = jis table mein FK column hai
```
OneToOne    → Owner = jis table mein FK hai (Passport)
ManyToOne   → Owner = HAMESHA "Many" wali entity (Employee)
OneToMany   → Inverse side = "One" wali entity (Department)
ManyToMany  → Owner = jis side pe @JoinTable hai (Student)
```

### Rule 4: Cascade aur FetchType
```
cascade    → parent operation child tak propagate karna
fetchType  → data kab load karna hai (EAGER vs LAZY)

EAGER → data turath load hota hai (default for @ManyToOne, @OneToOne)
LAZY  → data sirf access karne pe load hota hai (default for @OneToMany, @ManyToMany)

BEST PRACTICE: Always use LAZY, fetch manually ya @EntityGraph se
```

---

## 🧠 MEMORY TRICK

```
@ManyToOne  = "Mai (employee) kisiko (department) belong karta hoon"
              → FK meri table mein hai
              → Mai owner hoon

@OneToMany  = "Mere (department) bahut saare (employees) hain"
              → FK meri table mein NAHI hai
              → mappedBy mere pe hai
              → Mai inverse hoon

@OneToOne   = "Mera (user) sirf ek (passport) hai"
              → FK kisi ek ki table mein hai
              → Jis table mein FK, wo owner

@ManyToMany = "Hum sab (students) sab (courses) le sakte hain"
              → Ek alag table banti hai
              → @JoinTable owner side pe
```

---
