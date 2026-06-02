package com.gahub.server.jpa_relationships.onetoone;

import jakarta.persistence.*;

/**
 * ONE TO ONE - Inverse Side (User)
 *
 * Real-World: Ek User ka sirf EK Passport hota hai
 *
 * IMPORTANT POINTS:
 * - User table mein FK NAHI hai
 * - Ye INVERSE side hai kyunki humne mappedBy use kiya hai
 * - mappedBy = "user" matlab Passport entity mein jo "user" field hai, wo FK manage karta hai
 * - CascadeType.ALL matlab: User delete hone pe Passport bhi delete hoga
 * - FetchType.LAZY matlab: Passport data tabhi load hoga jab explicitly access karo
 *
 * DATABASE:
 * user_table
 * -----------
 * id (PK)
 * name
 * email
 */
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    /**
     * @OneToOne → Ek User ka sirf ek Passport
     * mappedBy → Ye INVERSE side hai, FK is table mein NAHI hai
     *            Passport entity mein "user" field hai jo FK manage karta hai
     * cascade → User save/delete hone pe Passport bhi save/delete hoga
     * fetch → LAZY: Passport tabhi load hoga jab user.getPassport() call karo
     *
     * IMPORTANT: mappedBy lagaane ka matlab ye table FK manage NAHI karti
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Passport passport;

    // Default constructor (required by JPA)
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Helper method - ALWAYS use this to set both sides of relationship
    public void setPassport(Passport passport) {
        this.passport = passport;
        if (passport != null) {
            passport.setUser(this); // Dono side set karna ZAROORI hai!
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Passport getPassport() { return passport; }
}
