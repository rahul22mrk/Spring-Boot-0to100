package com.gahub.server.jpa_relationships.onetoone;

import jakarta.persistence.*;

/**
 * ONE TO ONE - Owner Side (Passport)
 *
 * Real-World: Ek Passport sirf EK User ka hota hai
 *
 * IMPORTANT POINTS:
 * - Passport table mein FK (user_id) HAI - isliye ye OWNER side hai
 * - @JoinColumn(name = "user_id") → FK column ka naam batata hai
 * - Is side mappedBy NAHI hai - kyunki ye owner side hai
 * - Owner side = jis table mein FK column hai
 *
 * DATABASE:
 * passport_table
 * --------------
 * id (PK)
 * passport_number
 * user_id (FK → user_table.id)  ← FK IS TABLE MEIN HAI
 */
@Entity
@Table(name = "passport_table")
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String passportNumber;

    /**
     * @OneToOne → Ek Passport sirf ek User ka
     * @JoinColumn → FK column ka naam "user_id" hai passport_table mein
     *
     * IMPORTANT: Kyunki @JoinColumn is side hai, isliye ye OWNER side hai
     * Owner side = FK jis table mein hai, wo entity owner hoti hai
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK column in passport_table
    private User user;

    // Default constructor (required by JPA)
    public Passport() {}

    public Passport(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
