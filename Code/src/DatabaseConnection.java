import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/fyp_management_db";
    private static final String USERNAME = "root";  // Change this to your MySQL username
    private static final String PASSWORD = "";      // Change this to your MySQL password
    
    private static Connection connection = null;
    
    // Get database connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Connection properties
                Properties props = new Properties();
                props.setProperty("user", USERNAME);
                props.setProperty("password", PASSWORD);
                props.setProperty("useSSL", "false");
                props.setProperty("allowPublicKeyRetrieval", "true");
                props.setProperty("serverTimezone", "UTC");
                
                connection = DriverManager.getConnection(URL, props);
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
        return connection;
    }
    
    // Close database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Test database connection
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

// UserDAO.java - Data Access Object for Users
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;
    
    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    // Add new user
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (name, email, role, department) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getDepartment());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    // Update user
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, role = ?, department = ? WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getDepartment());
            stmt.setInt(5, user.getUserId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete user
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Find user by ID
    public User findUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Search users by name
    public List<User> searchUsersByName(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE name LIKE ? ORDER BY name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    // Get users by role
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql;
        
        if (role == null || role.equals("All")) {
            return getAllUsers();
        }
        
        sql = "SELECT * FROM users WHERE role = ? ORDER BY name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users by role: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    // Helper method to create User object from ResultSet
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("role"),
            rs.getString("department")
        );
    }
}

// ReportDAO.java - Data Access Object for Reports
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private Connection connection;
    
    public ReportDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    // Save report to database
    public boolean saveReport(Report report) {
        String sql = "INSERT INTO reports (title, content, report_type, generated_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, report.getTitle());
            stmt.setString(2, report.getContent());
            stmt.setString(3, report.getReportType());
            stmt.setDate(4, Date.valueOf(report.getGeneratedDate()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    report.setReportId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saving report: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get all reports
    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Report report = createReportFromResultSet(rs);
                reports.add(report);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reports: " + e.getMessage());
            e.printStackTrace();
        }
        return reports;
    }
    
    // Delete report
    public boolean deleteReport(int reportId) {
        String sql = "DELETE FROM reports WHERE report_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting report: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Find report by ID
    public Report findReportById(int reportId) {
        String sql = "SELECT * FROM reports WHERE report_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return createReportFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding report: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Helper method to create Report object from ResultSet
    private Report createReportFromResultSet(ResultSet rs) throws SQLException {
        Report report = new Report(
            rs.getInt("report_id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getString("report_type")
        );
        report.setGeneratedDate(rs.getDate("generated_date").toLocalDate());
        return report;
    }
}

// Updated UserController.java - Modified to use Database
import java.util.*;

public class UserController {
    private UserDAO userDAO;
    
    // Constructor - Initialize with database
    public UserController() {
        this.userDAO = new UserDAO();
    }
    
    // Add new user
    public boolean addUser(String name, String email, String role, String department) {
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty()) {
            return false;
        }
        
        User user = new User(0, name.trim(), email.trim(), role, department);
        return userDAO.addUser(user);
    }
    
    // Get all users
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    // Update user
    public boolean updateUser(int userId, String name, String email, String role, String department) {
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty()) {
            return false;
        }
        
        User user = new User(userId, name.trim(), email.trim(), role, department);
        return userDAO.updateUser(user);
    }
    
    // Delete user
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
    
    // Find user by ID
    public User findUserById(int userId) {
        return userDAO.findUserById(userId);
    }
    
    // Search users by name
    public List<User> searchUsersByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers();
        }
        return userDAO.searchUsersByName(searchTerm.trim());
    }
    
    // Get users by role
    public List<User> getUsersByRole(String role) {
        return userDAO.getUsersByRole(role);
    }
}

// Updated ReportController.java - Modified to use Database
import java.time.LocalDate;
import java.util.*;

public class ReportController {
    private ReportDAO reportDAO;
    private UserController userController;
    
    // Constructor
    public ReportController(UserController userController) {
        this.userController = userController;
        this.reportDAO = new ReportDAO();
    }
    
    // Generate User Report (same logic, but saves to database)
    public Report generateUserReport(String reportType) {
        StringBuilder content = new StringBuilder();
        List<User> users = userController.getAllUsers();
        
        content.append("=== USER MANAGEMENT REPORT ===\n");
        content.append("Generated on: ").append(LocalDate.now()).append("\n");
        content.append("Report Type: ").append(reportType).append("\n\n");
        
        if ("Summary".equals(reportType)) {
            generateSummaryReport(content, users);
        } else if ("Detailed".equals(reportType)) {
            generateDetailedReport(content, users);
        } else if ("Department-wise".equals(reportType)) {
            generateDepartmentReport(content, users);
        }
        
        String title = reportType + " User Report - " + LocalDate.now();
        Report report = new Report(0, title, content.toString(), reportType);
        
        // Save to database
        if (reportDAO.saveReport(report)) {
            return report;
        }
        return null;
    }
    
    // Get all reports from database
    public List<Report> getAllReports() {
        return reportDAO.getAllReports();
    }
    
    // Delete report from database
    public boolean deleteReport(int reportId) {
        return reportDAO.deleteReport(reportId);
    }
    
    // Find report by ID
    public Report findReportById(int reportId) {
        return reportDAO.findReportById(reportId);
    }
    
    // Same private methods for generating report content
    private void generateSummaryReport(StringBuilder content, List<User> users) {
        content.append("SUMMARY STATISTICS:\n");
        content.append("Total Users: ").append(users.size()).append("\n\n");
        
        // Count by role
        Map<String, Long> roleCount = new HashMap<>();
        for (User user : users) {
            roleCount.put(user.getRole(), roleCount.getOrDefault(user.getRole(), 0L) + 1);
        }
        
        content.append("Users by Role:\n");
        for (Map.Entry<String, Long> entry : roleCount.entrySet()) {
            content.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        content.append("\nUsers by Department:\n");
        Map<String, Long> deptCount = new HashMap<>();
        for (User user : users) {
            deptCount.put(user.getDepartment(), deptCount.getOrDefault(user.getDepartment(), 0L) + 1);
        }
        
        for (Map.Entry<String, Long> entry : deptCount.entrySet()) {
            content.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
    }
    
    private void generateDetailedReport(StringBuilder content, List<User> users) {
        content.append("DETAILED USER LIST:\n");
        content.append("=".repeat(50)).append("\n");
        
        for (User user : users) {
            content.append("ID: ").append(user.getUserId()).append("\n");
            content.append("Name: ").append(user.getName()).append("\n");
            content.append("Email: ").append(user.getEmail()).append("\n");
            content.append("Role: ").append(user.getRole()).append("\n");
            content.append("Department: ").append(user.getDepartment()).append("\n");
            content.append("-".repeat(30)).append("\n");
        }
    }
    
    private void generateDepartmentReport(StringBuilder content, List<User> users) {
        content.append("DEPARTMENT-WISE BREAKDOWN:\n");
        content.append("=".repeat(50)).append("\n");
        
        Map<String, List<User>> usersByDept = new HashMap<>();
        for (User user : users) {
            usersByDept.computeIfAbsent(user.getDepartment(), k -> new ArrayList<>()).add(user);
        }
        
        for (Map.Entry<String, List<User>> entry : usersByDept.entrySet()) {
            content.append("\n").append(entry.getKey().toUpperCase()).append(":\n");
            content.append("Total Users: ").append(entry.getValue().size()).append("\n");
            
            for (User user : entry.getValue()) {
                content.append("- ").append(user.getName())
                       .append(" (").append(user.getRole()).append(")\n");
            }
        }
    }
}