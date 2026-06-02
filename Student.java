package com.gahub.server.jpa_relationships.manytomany;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MANY TO MANY - Owner Side (Student)
 *
 * Real-World: Bahut saare Students EK Course le sakte hain, aur EK Course mein Bahut saare Students hain
 *
 * IMPORTANT POINTS:
 * - ManyToMany mein FK kisi bhi table mein nahi hota, ek alag JOIN TABLE banti hai
 * - Owner side pe @JoinTable define karte hain
 * - Inverse side pe mappedBy lagta hai
 * - JOIN TABLE automatically banti hai JPA dwara
 *
 * DATABASE:
 * student_table                          course_table
 * -------------                          ------------
 * id (PK)                                id (PK)
 * name                                   course_name
 *
 * student_course (JOIN TABLE - auto generated)
 * ------------------
 * student_id (FK → student_table.id)
 * course_id (FK → course_table.id)
 *
 * CONFUSION KILLER:
 * ManyToMany = dono sides pe "Many" hai
 * Isliye FK ek side pe nahi rakh sakte, alag table banti hai
 * Real projects mein ManyToMany avoid karo, instead ek intermediate Entity banao
 * (e.g., Enrollment entity jo Student aur Course ke beech mein aaye)
 */
@Entity
@Table(name = "student_table")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * @ManyToMany → Bahut saare Students bahut saare Courses le sakte hain
     * @JoinTable → JOIN TABLE ka structure define karta hai
     *   joinColumns → Student ki FK ka naam (student_id)
     *   inverseJoinColumns → Course ki FK ka naam (course_id)
     *
     * IMPORTANT: @JoinTable OWNER side pe hota hai
     * Owner side = jis side pe aap relationship manage karna chahte hain
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_course", // JOIN TABLE ka naam
        joinColumns = @JoinColumn(name = "student_id"), // Student ki FK
        inverseJoinColumns = @JoinColumn(name = "course_id") // Course ki FK
    )
    private List<Course> courses = new ArrayList<>();

    // Default constructor (required by JPA)
    public Student() {}

    public Student(String name) {
        this.name = name;
    }

    // Helper method - ALWAYS use this to add course (dono side set karta hai)
    public void addCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this); // Dono side set karna ZAROORI hai!
    }

    // Helper method - ALWAYS use this to remove course (dono side clear karta hai)
    public void removeCourse(Course course) {
        courses.remove(course);
        course.getStudents().remove(this); // Dono side clear karna ZAROORI hai!
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
