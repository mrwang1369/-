package dota.model;

/**
 * 应用配置数据模型。
 */
public class AppConfig {
    private String templateLibraryPath;
    private String projectSavePath;

    public AppConfig() {}

    public AppConfig(String templateLibraryPath, String projectSavePath) {
        this.templateLibraryPath = templateLibraryPath;
        this.projectSavePath = projectSavePath;
    }

    // Getters and Setters
    public String getTemplateLibraryPath() { return templateLibraryPath; }
    public void setTemplateLibraryPath(String templateLibraryPath) { this.templateLibraryPath = templateLibraryPath; }

    public String getProjectSavePath() { return projectSavePath; }
    public void setProjectSavePath(String projectSavePath) { this.projectSavePath = projectSavePath; }
}