package dota.dao;

import dota.model.ActionTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 负责管理常用模板库的加载和保存。
 */
public class TemplateLibraryManager {
    private final ObjectMapper objectMapper;
    private final Path libraryPath;

    public TemplateLibraryManager(String libraryPath) {
        this.objectMapper = new ObjectMapper();
        this.libraryPath = Path.of(libraryPath);
        // 确保文件存在
        createDefaultFileIfNotExists();
    }

    /**
     * 从JSON文件中加载所有模板。
     * @return 模板列表。
     */
    public List<ActionTemplate> loadTemplates() {
        try {
            if (Files.exists(libraryPath) && Files.size(libraryPath) > 0) {
                return objectMapper.readValue(libraryPath.toFile(), new TypeReference<List<ActionTemplate>>() {});
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("加载模板库失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 将模板列表保存到JSON文件。
     * @param templates 要保存的模板列表。
     */
    public void saveTemplates(List<ActionTemplate> templates) {
        try {
            File parentDir = libraryPath.toFile().getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(libraryPath.toFile(), templates);
        } catch (IOException e) {
            System.err.println("保存模板库失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 添加一个新模板。
     * @param template 要添加的模板。
     */
    public void addTemplate(ActionTemplate template) {
        List<ActionTemplate> templates = loadTemplates();
        templates.add(template);
        saveTemplates(templates);
    }

    /**
     * 删除一个模板。
     * @param templateId 要删除的模板ID。
     */
    public void deleteTemplate(String templateId) {
        List<ActionTemplate> templates = loadTemplates();
        templates.removeIf(t -> t.getId().equals(templateId));
        saveTemplates(templates);
    }

    /**
     * 根据标签查找模板。
     * @param tag 查找的标签。
     * @return 匹配的模板列表。
     */
    public List<ActionTemplate> findTemplatesByTag(String tag) {
        return loadTemplates().stream()
                .filter(t -> t.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    /**
     * 如果文件不存在，则创建一个空的JSON数组文件。
     */
    private void createDefaultFileIfNotExists() {
        try {
            if (!Files.exists(libraryPath)) {
                File parentDir = libraryPath.toFile().getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                Files.writeString(libraryPath, "[]");
            }
        } catch (IOException e) {
            System.err.println("创建默认模板库文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}