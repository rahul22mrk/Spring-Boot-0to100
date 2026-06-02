package com.gahub.server.jpa_relationships.onetomany;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ONE TO MANY - Inverse Side (Department)
 *
 * Real-World: EK Department mein Bahut saare Employees ho sakte hain
 *
 * IMPORTANT POINTS:
 * - Department table mein FK NAHI hai
 * - Ye INVERSE side hai (mappedBy use kiya hai)
 * - mappedBy = "department" → Employee entity mein "department" field FK manage karta hai
 * - FK hamesha "Many" wali table (employee_table) mein hota hai
 * - Ye @OneToMany hai lekin iske saath @ManyToOne pair mein chalta hai
 *
 * DATABASE:
 * department_table
 * ----------------
 * id (PK)
 * name
 *
 * NOTE: employee_table mein department_id (FK) hai, yahan NAHI
 */
@Entity
@Table(name = "department_table")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * @OneToMany → Ek Department mein bahut saare Employees
     * mappedBy → Ye INVERSE side hai, FK is table mein NAHI hai
     *            Employee entity mein "department" field hai jo FK manage karta hai
     * cascade → Department save/delete hone pe Employees bhi save/delete honge
     * fetch → LAZY: Employees tabhi load honge jab department.getEmployees() call karo
     * orphanRemoval → Agar employee ko department se hatao, wo DB se bhi delete ho jayega
     *
     * CONFUSION KILLER:
     * @OneToMany alone = JOIN TABLE banti hai (avoid this!)
     * @OneToMany + @ManyToOne pair = FK child table mein aati hai (use this!)
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    // Default constructor (required by JPA)
    public Department() {}

    public Department(String name) {
        this.name = name;
    }

    // Helper method - ALWAYS use this to add employee (dono side set karta hai)
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this); // Dono side set karna ZAROORI hai!
    }

    // Helper method - ALWAYS use this to remove employee (dono side clear karta hai)
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null); // Dono side clear karna ZAROORI hai!
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}
