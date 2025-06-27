import java.time.LocalDate;

public class Report {
    private int reportId;
    private String title;
    private String content;
    private LocalDate generatedDate;
    private String reportType;
    
    // Constructor
    public Report(int reportId, String title, String content, String reportType) {
        this.reportId = reportId;
        this.title = title;
        this.content = content;
        this.reportType = reportType;
        this.generatedDate = LocalDate.now();
    }
    
    // Getters and Setters
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
}
