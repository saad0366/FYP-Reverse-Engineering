import java.time.LocalDate;
import java.util.*;

public class ReportController {
    private List<Report> reports;
    private UserController userController;
    private int nextReportId;
    
    // Constructor
    public ReportController(UserController userController) {
        this.userController = userController;
        this.reports = new ArrayList<>();
        this.nextReportId = 1;
    }
    
    // Generate User Report
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
        Report report = new Report(nextReportId++, title, content.toString(), reportType);
        reports.add(report);
        
        return report;
    }
    
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
    
    // Get all reports
    public List<Report> getAllReports() {
        return new ArrayList<>(reports);
    }
    
    // Delete report
    public boolean deleteReport(int reportId) {
        return reports.removeIf(report -> report.getReportId() == reportId);
    }
}
