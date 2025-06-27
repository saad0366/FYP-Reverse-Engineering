import java.util.*;

public class UserController {
    private List<User> users;
    private int nextUserId;
    
    // Constructor - Initialize with sample data
    public UserController() {
        users = new ArrayList<>();
        nextUserId = 1;
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        addUser("John Smith", "john@university.edu", "Student", "Computer Science");
        addUser("Dr. Sarah Johnson", "sarah@university.edu", "Supervisor", "Computer Science");
        addUser("Mike Wilson", "mike@university.edu", "Student", "Engineering");
        addUser("Prof. David Brown", "david@university.edu", "Coordinator", "Engineering");
    }
    
    // Add new user
    public boolean addUser(String name, String email, String role, String department) {
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty()) {
            return false;
        }
        
        User user = new User(nextUserId++, name.trim(), email.trim(), role, department);
        users.add(user);
        return true;
    }
    
    // Get all users
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    // Update user
    public boolean updateUser(int userId, String name, String email, String role, String department) {
        User user = findUserById(userId);
        if (user != null && name != null && !name.trim().isEmpty() && 
            email != null && !email.trim().isEmpty()) {
            user.setName(name.trim());
            user.setEmail(email.trim());
            user.setRole(role);
            user.setDepartment(department);
            return true;
        }
        return false;
    }
    
    // Delete user
    public boolean deleteUser(int userId) {
        User user = findUserById(userId);
        if (user != null) {
            users.remove(user);
            return true;
        }
        return false;
    }
    
    // Find user by ID
    public User findUserById(int userId) {
        return users.stream()
                   .filter(user -> user.getUserId() == userId)
                   .findFirst()
                   .orElse(null);
    }
    
    // Search users by name
    public List<User> searchUsersByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        return users.stream()
                   .filter(user -> user.getName().toLowerCase().contains(lowerSearchTerm))
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // Get users by role
    public List<User> getUsersByRole(String role) {
        if (role == null || role.equals("All")) {
            return getAllUsers();
        }
        
        return users.stream()
                   .filter(user -> user.getRole().equals(role))
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
