import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FYPManagementGUI extends JFrame {
    private UserController userController;
    private ReportController reportController;
    private JTabbedPane tabbedPane;
    
    // User Management Components
    private DefaultTableModel userTableModel;
    private JTable userTable;
    private JTextField nameField, emailField;
    private JComboBox<String> roleCombo, departmentCombo, filterRoleCombo;
    private JTextField searchField;
    private JButton addUserBtn, updateUserBtn, deleteUserBtn, clearBtn;
    
    // Report Generation Components
    private JComboBox<String> reportTypeCombo;
    private JTextArea reportArea;
    private DefaultTableModel reportTableModel;
    private JTable reportTable;
    private JButton generateBtn, viewReportBtn, deleteReportBtn;
    
    public FYPManagementGUI() {
        initializeControllers();
        initializeGUI();
        loadUserData();
        loadReportData();
    }
    
    private void initializeControllers() {
        userController = new UserController();
        reportController = new ReportController(userController);
    }
    
    private void initializeGUI() {
        setTitle("FYP Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add tabs
        tabbedPane.addTab("👥 Manage Users", createUserManagementPanel());
        tabbedPane.addTab("📊 Generate Reports", createReportPanel());
        
        add(tabbedPane);
    }
    
    private JPanel createUserManagementPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("User Management System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(new Color(0, 100, 200));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Form Panel
        JPanel formPanel = createUserFormPanel();
        
        // Table Panel
        JPanel tablePanel = createUserTablePanel();
        
        // Button Panel
        JPanel buttonPanel = createUserButtonPanel();
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createUserFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(300, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"Student", "Supervisor", "Coordinator", "Admin"});
        formPanel.add(roleCombo, gbc);
        
        // Department
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        departmentCombo = new JComboBox<>(new String[]{"Computer Science", "Engineering", "Business", "Mathematics"});
        formPanel.add(departmentCombo, gbc);
        
        // Search
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        searchField = new JTextField(15);
        searchField.addActionListener(e -> searchUsers());
        formPanel.add(searchField, gbc);
        
        // Filter
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Filter Role:"), gbc);
        gbc.gridx = 1;
        filterRoleCombo = new JComboBox<>(new String[]{"All", "Student", "Supervisor", "Coordinator", "Admin"});
        filterRoleCombo.addActionListener(e -> filterUsers());
        formPanel.add(filterRoleCombo, gbc);
        
        return formPanel;
    }
    
    private JPanel createUserTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Users List"));
        
        // Create table
        String[] columns = {"ID", "Name", "Email", "Role", "Department"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedUser();
            }
        });
        
        // Style table
        userTable.setRowHeight(25);
        userTable.getTableHeader().setBackground(new Color(0, 100, 200));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }
    
    private JPanel createUserButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        addUserBtn = createStyledButton("Add User", new Color(34, 139, 34));
        updateUserBtn = createStyledButton("Update User", new Color(255, 140, 0));
        deleteUserBtn = createStyledButton("Delete User", new Color(220, 20, 60));
        clearBtn = createStyledButton("Clear Fields", new Color(70, 130, 180));
        
        addUserBtn.addActionListener(e -> addUser());
        updateUserBtn.addActionListener(e -> updateUser());
        deleteUserBtn.addActionListener(e -> deleteUser());
        clearBtn.addActionListener(e -> clearFields());
        
        buttonPanel.add(addUserBtn);
        buttonPanel.add(updateUserBtn);
        buttonPanel.add(deleteUserBtn);
        buttonPanel.add(clearBtn);
        
        return buttonPanel;
    }
    
    private JPanel createReportPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("Report Generation System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(new Color(0, 100, 200));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder("Report Controls"));
        
        controlPanel.add(new JLabel("Report Type:"));
        reportTypeCombo = new JComboBox<>(new String[]{"Summary", "Detailed", "Department-wise"});
        controlPanel.add(reportTypeCombo);
        
        generateBtn = createStyledButton("Generate Report", new Color(34, 139, 34));
        generateBtn.addActionListener(e -> generateReport());
        controlPanel.add(generateBtn);
        
        // Split pane for reports list and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        
        // Reports list panel
        JPanel reportsListPanel = new JPanel(new BorderLayout());
        reportsListPanel.setBorder(BorderFactory.createTitledBorder("Generated Reports"));
        
        String[] reportColumns = {"ID", "Title", "Type", "Date"};
        reportTableModel = new DefaultTableModel(reportColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportTable = new JTable(reportTableModel);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                viewSelectedReport();
            }
        });
        
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setBackground(new Color(0, 100, 200));
        reportTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane reportScrollPane = new JScrollPane(reportTable);
        reportsListPanel.add(reportScrollPane, BorderLayout.CENTER);
        
        JPanel reportButtonPanel = new JPanel(new FlowLayout());
        reportButtonPanel.setBackground(Color.WHITE);
        
        viewReportBtn = createStyledButton("View Report", new Color(70, 130, 180));
        deleteReportBtn = createStyledButton("Delete Report", new Color(220, 20, 60));
        
        viewReportBtn.addActionListener(e -> viewSelectedReport());
        deleteReportBtn.addActionListener(e -> deleteSelectedReport());
        
        reportButtonPanel.add(viewReportBtn);
        reportButtonPanel.add(deleteReportBtn);
        
        reportsListPanel.add(reportButtonPanel, BorderLayout.SOUTH);
        
        // Report content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createTitledBorder("Report Content"));
        
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane contentScrollPane = new JScrollPane(reportArea);
        contentPanel.add(contentScrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(reportsListPanel);
        splitPane.setRightComponent(contentPanel);
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Adjust layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.remove(headerLabel);
        mainPanel.remove(controlPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        return mainPanel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }
    
    // User Management Methods
    private void addUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();
        String department = (String) departmentCombo.getSelectedItem();
        
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (userController.addUser(name, email, role, department)) {
            JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (Integer) userTableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();
        String department = (String) departmentCombo.getSelectedItem();
        
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (userController.updateUser(userId, name, email, role, department)) {
            JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (Integer) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete user: " + userName + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userController.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUserData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        roleCombo.setSelectedIndex(0);
        departmentCombo.setSelectedIndex(0);
    }
    
    private void loadSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            nameField.setText((String) userTableModel.getValueAt(selectedRow, 1));
            emailField.setText((String) userTableModel.getValueAt(selectedRow, 2));
            roleCombo.setSelectedItem(userTableModel.getValueAt(selectedRow, 3));
            departmentCombo.setSelectedItem(userTableModel.getValueAt(selectedRow, 4));
        }
    }
    
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        List<User> users = userController.searchUsersByName(searchTerm);
        displayUsers(users);
    }
    
    private void filterUsers() {
        String selectedRole = (String) filterRoleCombo.getSelectedItem();
        List<User> users = userController.getUsersByRole(selectedRole);
        displayUsers(users);
    }
    
    private void loadUserData() {
        List<User> users = userController.getAllUsers();
        displayUsers(users);
    }
    
    private void displayUsers(List<User> users) {
        userTableModel.setRowCount(0);
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getDepartment()
            };
            userTableModel.addRow(row);
        }
    }
    
    // Report Methods
    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        Report report = reportController.generateUserReport(reportType);
        
        JOptionPane.showMessageDialog(this, "Report generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        loadReportData();
        
        // Select the newly generated report
        for (int i = 0; i < reportTableModel.getRowCount(); i++) {
            if ((Integer) reportTableModel.getValueAt(i, 0) == report.getReportId()) {
                reportTable.setRowSelectionInterval(i, i);
                break;
            }
        }
        
        viewSelectedReport();
    }
    
    private void viewSelectedReport() {
        int selectedRow = reportTable.getSelectedRow();
        if (selectedRow != -1) {
            int reportId = (Integer) reportTableModel.getValueAt(selectedRow, 0);
            List<Report> reports = reportController.getAllReports();
            
            for (Report report : reports) {
                if (report.getReportId() == reportId) {
                    reportArea.setText(report.getContent());
                    reportArea.setCaretPosition(0);
                    break;
                }
            }
        }
    }
    
    private void deleteSelectedReport() {
        int selectedRow = reportTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a report to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reportId = (Integer) reportTableModel.getValueAt(selectedRow, 0);
        String reportTitle = (String) reportTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete report: " + reportTitle + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (reportController.deleteReport(reportId)) {
                JOptionPane.showMessageDialog(this, "Report deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadReportData();
                reportArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete report.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadReportData() {
        reportTableModel.setRowCount(0);
        List<Report> reports = reportController.getAllReports();
        
        for (Report report : reports) {
            Object[] row = {
                report.getReportId(),
                report.getTitle(),
                report.getReportType(),
                report.getGeneratedDate().toString()
            };
            reportTableModel.addRow(row);
        }
    }
    
    // Main method
    public static void main(String[] args) {
        // Set Look and Feel
      
        
        // Create and display the GUI
        SwingUtilities.invokeLater(() -> {
            new FYPManagementGUI().setVisible(true);
        });
    }
}