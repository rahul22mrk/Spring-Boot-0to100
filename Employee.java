package com.gahub.server.jpa_relationships.onetomany;

import jakarta.persistence.*;

/**
 * MANY TO ONE - Owner Side (Employee)
 *
 * Real-World: Bahut saare Employees EK Department mein kaam kar sakte hain
 *
 * IMPORTANT POINTS:
 * - Employee table mein FK (department_id) HAI - isliye ye OWNER side hai
 * - @ManyToOne HAMESHA owner side hota hai (FK is side pe hota hai)
 * - @JoinColumn(name = "department_id") → FK column ka naam batata hai
 * - Many side = Owner side = FK wali side (YE GOLDEN RULE HAI!)
 *
 * DATABASE:
 * employee_table
 * --------------
 * id (PK)
 * emp_name
 * department_id (FK → department_table.id)  ← FK IS TABLE MEIN HAI
 *
 * CONFUSION KILLER:
 * @ManyToOne aur @OneToMany EK HI relationship ke 2 side hain!
 * Employee se dekho → ManyToOne (bahut employees ek department mein)
 * Department se dekho → OneToMany (ek department mein bahut employees)
 * FK hamesha Employee (Many wali) table mein hota hai
 */
@Entity
@Table(name = "employee_table")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_name")
    private String name;

    /**
     * @ManyToOne → Bahut saare Employees ek Department ko belong karte hain
     * @JoinColumn → FK column ka naam "department_id" hai employee_table mein
     * fetch → LAZY: Department tabhi load hoga jab employee.getDepartment() call karo
     *
     * IMPORTANT: @ManyToOne is ALWAYS the owner side!
     * Kyunki FK hamesha "Many" wali table mein hota hai
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id") // FK column in employee_table
    private Department department;

    // Default constructor (required by JPA)
    public Employee() {}

    public Employee(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}
