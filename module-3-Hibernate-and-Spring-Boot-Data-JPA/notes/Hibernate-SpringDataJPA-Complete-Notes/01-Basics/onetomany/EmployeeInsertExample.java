package com.gahub.server.jpa_relationships.onetomany;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================================================
 * PRACTICAL EXAMPLE: Employee Insert karna Department mein
 * ============================================================================
 *
 * Question: D1 mein 10 employees aur D2 mein 5 employees kaise insert karein?
 *
 * ============================================================================
 * WAY 1: Parent (Department) se child (Employee) add karo - RECOMMENDED
 * ============================================================================
 * Jab cascade = CascadeType.ALL hai, toh sirf Department save karo,
 * Employees automatically save ho jayenge!
 *
 * DB mein kya hoga:
 * department_table                    employee_table
 * ----------------                    --------------
 * 1 | IT Department                   1 | Emp-1  | 1  (department_id = 1)
 * 2 | HR Department                   2 | Emp-2  | 1
 *                                     3 | Emp-3  | 1
 *                                     ... (10 total with dept_id=1)
 *                                     11| Emp-11 | 2
 *                                     12| Emp-12 | 2
 *                                     ... (5 total with dept_id=2)
 *
 * ============================================================================
 * WAY 2: Child (Employee) se parent (Department) set karo
 * ============================================================================
 * Pehle Department save karo (without cascade on employee side),
 * phir har Employee mein department set karke save karo
 *
 * ============================================================================
 * WAY 3: Already saved Department mein employees add karo
 * ============================================================================
 * Department already DB mein hai, usme naye employees add karo
 */
@Service
public class EmployeeInsertExample {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeInsertExample(DepartmentRepository departmentRepository,
                                  EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    // =========================================================================
    // WAY 1: Parent se Child Add (BEST WAY when using cascade)
    // =========================================================================
    // Isme sirf Department save karo, Employees automatically save honge
    // kyunki cascade = CascadeType.ALL hai Department entity mein
    // =========================================================================
    @Transactional
    public void way1_ParentSideInsert() {
        // Step 1: Department D1 banao
        Department d1 = new Department("IT Department");

        // Step 2: D1 mein 10 employees add karo (helper method use karo!)
        for (int i = 1; i <= 10; i++) {
            Employee emp = new Employee("IT-Employee-" + i);
            d1.addEmployee(emp); // Helper method dono side set karta hai!
        }

        // Step 3: Department D2 banao
        Department d2 = new Department("HR Department");

        // Step 4: D2 mein 5 employees add karo
        for (int i = 1; i <= 5; i++) {
            Employee emp = new Employee("HR-Employee-" + i);
            d2.addEmployee(emp); // Helper method dono side set karta hai!
        }

        // Step 5: Sirf Department save karo - Employees auto save honge (cascade!)
        departmentRepository.save(d1); // 1 insert in department + 10 inserts in employee
        departmentRepository.save(d2); // 1 insert in department + 5 inserts in employee

        // TOTAL: 2 department inserts + 15 employee inserts = 17 inserts
        System.out.println("✅ Way 1 Done! D1 has 10 employees, D2 has 5 employees");
    }

    // =========================================================================
    // WAY 2: Child Side se Insert (when NO cascade on parent)
    // =========================================================================
    // Isme pehle Department save karo, phir Employee mein department set karke save karo
    // Ye tab use karo jab cascade NAHI hai parent side pe
    // =========================================================================
    @Transactional
    public void way2_ChildSideInsert() {
        // Step 1: Pehle Departments save karo
        Department d1 = new Department("IT Department");
        Department d2 = new Department("HR Department");
        departmentRepository.save(d1); // Pehle department DB mein jaye
        departmentRepository.save(d2);

        // Step 2: D1 ke liye 10 employees banao aur unka department set karo
        for (int i = 1; i <= 10; i++) {
            Employee emp = new Employee("IT-Employee-" + i);
            emp.setDepartment(d1); // Department set karo (FK set ho jayega)
            employeeRepository.save(emp); // Employee save karo
        }

        // Step 3: D2 ke liye 5 employees banao aur unka department set karo
        for (int i = 1; i <= 5; i++) {
            Employee emp = new Employee("HR-Employee-" + i);
            emp.setDepartment(d2); // Department set karo (FK set ho jayega)
            employeeRepository.save(emp); // Employee save karo
        }

        // TOTAL: 2 department inserts + 15 employee inserts = 17 inserts
        System.out.println("✅ Way 2 Done! D1 has 10 employees, D2 has 5 employees");
    }

    // =========================================================================
    // WAY 3: Already existing Department mein Employees add karo
    // =========================================================================
    // Department pehle se DB mein hai, sirf naye employees add karne hain
    // =========================================================================
    @Transactional
    public void way3_AddToExistingDepartment(Long existingDeptId1, Long existingDeptId2) {
        // Step 1: Existing Department fetch karo
        Department d1 = departmentRepository.findById(existingDeptId1)
                .orElseThrow(() -> new RuntimeException("Department not found!"));
        Department d2 = departmentRepository.findById(existingDeptId2)
                .orElseThrow(() -> new RuntimeException("Department not found!"));

        // Step 2: D1 mein 10 naye employees add karo
        for (int i = 1; i <= 10; i++) {
            Employee emp = new Employee("IT-Employee-" + i);
            d1.addEmployee(emp); // Helper method use karo!
        }

        // Step 3: D2 mein 5 naye employees add karo
        for (int i = 1; i <= 5; i++) {
            Employee emp = new Employee("HR-Employee-" + i);
            d2.addEmployee(emp); // Helper method use karo!
        }

        // Step 4: Save karo (cascade se employees bhi save honge)
        departmentRepository.save(d1);
        departmentRepository.save(d2);

        System.out.println("✅ Way 3 Done! Added employees to existing departments");
    }

    // =========================================================================
    // BONUS: Employee ko ek Department se dusre Department move karna
    // =========================================================================
    @Transactional
    public void moveEmployeeToAnotherDepartment(Long employeeId, Long newDeptId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found!"));
        Department newDept = departmentRepository.findById(newDeptId)
                .orElseThrow(() -> new RuntimeException("Department not found!"));

        // Step 1: Purane department se hatao
        if (emp.getDepartment() != null) {
            emp.getDepartment().getEmployees().remove(emp);
        }

        // Step 2: Naye department mein add karo
        newDept.addEmployee(emp); // Helper method - dono side set karta hai

        // Step 3: Save karo
        employeeRepository.save(emp);

        System.out.println("✅ Employee moved to new department!");
    }

    // =========================================================================
    // COMMON MISTAKES (AVOID THESE!)
    // =========================================================================
    @Transactional
    public void commonMistakes() {
        Department d1 = new Department("IT Department");

        // ❌ MISTAKE 1: Sirf employee.setDepartment() kiya, department.getEmployees() nahi kiya
        //    Result: Memory mein inconsistent state, bugs aayenge
        Employee emp1 = new Employee("Wrong Way");
        emp1.setDepartment(d1);
        // d1.getEmployees().add(emp1); // YE MISS KAR DIYA! Bug hoga!

        // ✅ CORRECT: Helper method use karo (dono side set hota hai)
        Employee emp2 = new Employee("Right Way");
        d1.addEmployee(emp2); // Dono side set ho jata hai!

        // ❌ MISTAKE 2: Cascade ke bina sirf parent save kiya, child miss ho gaya
        //    Agar cascade nahi hai toh child ko EXPLICITLY save karna padega
        // departmentRepository.save(d1); // Sirf department save hoga, employee NAHI!

        // ✅ CORRECT: Either use cascade ya phir dono ko separately save karo
        departmentRepository.save(d1); // With cascade, employees bhi save honge

        // ❌ MISTAKE 3: FetchType.EAGER use karna (N+1 problem)
        //    EAGER se saare employees turant load ho jayenge, performance slow hoga
        // ✅ CORRECT: FetchType.LAZY use karo, data tabhi load karo jab chahiye
    }
}
