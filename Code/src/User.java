public class User {
    private int userId;
    private String name;
    private String email;
    private String role;
    private String department;
    
    // Constructor
    public User(int userId, String name, String email, String role, String department) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    @Override
    public String toString() {
        return name + " - " + role;
    }
}

