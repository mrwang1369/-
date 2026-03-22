package dota.dao;

import dota.model.BehaviorSnippet;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 负责管理当前标注项目，包括加载、保存和导出BehaviorSnippet。
 */
public class ProjectManager {
    private final ObjectMapper objectMapper;
    private final Path projectPath;
    private BehaviorSnippet currentSnippet;

    public ProjectManager(String projectPath) {
        this.objectMapper = new ObjectMapper();
        this.projectPath = Path.of(projectPath);
        this.currentSnippet = null;
        // 确保文件存在
        createDefaultFileIfNotExists();
    }

    /**
     * 加载一个视频片段的标注数据。
     * @return BehaviorSnippet对象。
     */
    public BehaviorSnippet loadProject() {
        try {
            if (Files.exists(projectPath) && Files.size(projectPath) > 0) {
                currentSnippet = objectMapper.readValue(projectPath.toFile(), BehaviorSnippet.class);
            } else {
                currentSnippet = new BehaviorSnippet();
            }
        } catch (IOException e) {
            System.err.println("加载项目失败: " + e.getMessage());
            e.printStackTrace();
            currentSnippet = new BehaviorSnippet();
        }
        return currentSnippet;
    }

    /**
     * 保存当前的标注片段到文件。
     * @param snippet 要保存的片段。
     */
    public void saveProject(BehaviorSnippet snippet) {
        try {
            File parentDir = projectPath.toFile().getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(projectPath.toFile(), snippet);
            this.currentSnippet = snippet;
        } catch (IOException e) {
            System.err.println("保存项目失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 导出当前片段为JSON字符串（可用于复制或网络传输）。
     * @return JSON格式的字符串。
     */
    public String exportAsJson() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currentSnippet);
        } catch (IOException e) {
            System.err.println("导出为JSON失败: " + e.getMessage());
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * 创建一个新的空白片段。
     * @return 新的BehaviorSnippet对象。
     */
    public BehaviorSnippet createNewSnippet() {
        this.currentSnippet = new BehaviorSnippet();
        return currentSnippet;
    }

    /**
     * 如果文件不存在，则创建一个空的JSON对象文件。
     */
    private void createDefaultFileIfNotExists() {
        try {
            if (!Files.exists(projectPath)) {
                File parentDir = projectPath.toFile().getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                Files.writeString(projectPath, "{}");
            }
        } catch (IOException e) {
            System.err.println("创建默认项目文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}