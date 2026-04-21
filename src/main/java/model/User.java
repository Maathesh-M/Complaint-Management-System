package model;

/**
 * User - POJO class representing a user in the system.
 * Maps to the 'users' table in the database.
 */
public class User {

    private int    id;
    private String name;
    private String email;
    private String password;
    private String role; // "admin" or "user"

    // Default constructor
    public User() {}

    // Parameterized constructor (without id, for registration)
    public User(String name, String email, String password, String role) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // Full constructor (with id, for retrieval)
    public User(int id, String name, String email, String password, String role) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setId(int id)             { this.id       = id; }
    public void setName(String name)       { this.name     = name; }
    public void setEmail(String email)     { this.email    = email; }
    public void setPassword(String pass)   { this.password = pass; }
    public void setRole(String role)       { this.role     = role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email +
               "', role='" + role + "'}";
    }
}