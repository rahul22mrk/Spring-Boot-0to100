package com.gahub.server.jpa_relationships.manytomany;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MANY TO MANY - Inverse Side (Course)
 *
 * Real-World: EK Course mein Bahut saare Students hain, aur Bahut saare Courses ek Student le sakta hai
 *
 * IMPORTANT POINTS:
 * - Course table mein FK NAHI hai
 * - Ye INVERSE side hai (mappedBy use kiya hai)
 * - mappedBy = "courses" → Student entity mein "courses" field JOIN TABLE manage karta hai
 * - JOIN TABLE (student_course) mein dono ki FKs hoti hain
 *
 * DATABASE:
 * course_table
 * ------------
 * id (PK)
 * course_name
 */
@Entity
@Table(name = "course_table")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name")
    private String name;

    /**
     * @ManyToMany → Bahut saare Courses mein Bahut saare Students
     * mappedBy → Ye INVERSE side hai, JOIN TABLE is side se manage NAHI hota
     *            Student entity mein "courses" field JOIN TABLE manage karta hai
     *
     * IMPORTANT: mappedBy lagaane ka matlab ye entity JOIN TABLE manage NAHI karti
     */
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();

    // Default constructor (required by JPA)
    public Course() {}

    public Course(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
}
