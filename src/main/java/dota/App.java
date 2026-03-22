package dota;

import dota.dao.ProjectManager;
import dota.dao.TemplateLibraryManager;
import dota.model.AppConfig;
import dota.model.BehaviorSnippet;
import dota.model.HeroGroup;
import dota.ui.MainAppUI;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * 应用程序主类。
 * 负责集成所有模块，处理启动逻辑和用户交互。
 */
public class App {
    // 首选项节点，用于存储配置路径
    private static final Preferences PREFS = Preferences.userNodeForPackage(App.class);
    private static final String KEY_TEMPLATE_PATH = "template_library_path";
    private static final String KEY_PROJECT_PATH = "project_save_path";

    private AppConfig config;
    private TemplateLibraryManager templateLibraryManager;
    private ProjectManager projectManager;
    private MainAppUI ui;
    private BehaviorSnippet currentSnippet;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new App().start();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "应用启动失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    /**
     * 启动应用程序。
     * 初始化配置、数据管理器，并显示主界面。
     */
    public void start() {
        loadOrCreateConfig();
        initializeManagers();
        createAndShowUI();
        loadInitialData();
        updateStatus("应用已就绪。欢迎使用DOTA2玩家行为标注工具！");
    }

    /**
     * 加载用户配置，如果不存在则创建默认配置。
     */
    private void loadOrCreateConfig() {
        // 尝试从首选项中读取路径
        String templatePath = PREFS.get(KEY_TEMPLATE_PATH, null);
        String projectPath = PREFS.get(KEY_PROJECT_PATH, null);

        if (templatePath == null || projectPath == null) {
            // 首次运行，创建默认路径
            Path userHome = Paths.get(System.getProperty("user.home"));
            Path appDir = userHome.resolve(".dota2annotator");
            try {
                Files.createDirectories(appDir);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "无法创建应用目录: " + e.getMessage(), "警告", JOptionPane.WARNING_MESSAGE);
            }

            templatePath = appDir.resolve("templates.json").toString();
            projectPath = appDir.resolve("current_project.json").toString();

            // 保存到首选项
            PREFS.put(KEY_TEMPLATE_PATH, templatePath);
            PREFS.put(KEY_PROJECT_PATH, projectPath);
        }

        this.config = new AppConfig(templatePath, projectPath);
    }

    /**
     * 根据配置初始化数据管理器。
     */
    private void initializeManagers() {
        this.templateLibraryManager = new TemplateLibraryManager(config.getTemplateLibraryPath());
        this.projectManager = new ProjectManager(config.getProjectSavePath());
    }

    /**
     * 创建并显示主用户界面。
     */
    private void createAndShowUI() {
        this.ui = new MainAppUI();
        attachEventListeners();
        this.ui.setVisible(true);
    }

    /**
     * 将事件监听器连接到UI组件。
     */
    private void attachEventListeners() {
        // 新建项目按钮
        ui.getNewProjectButton().addActionListener(e -> handleNewProject());

        // 打开项目按钮
        ui.getOpenProjectButton().addActionListener(e -> handleOpenProject());

        // 保存项目按钮
        ui.getSaveProjectButton().addActionListener(e -> handleSaveProject());

        // 导出项目按钮
        ui.getExportProjectButton().addActionListener(e -> handleExportProject());

        // “添加到列表”按钮
        ui.getAddToEntryButton().addActionListener(e -> handleAddToEntryList());
    }

    /**
     * 处理“新建项目”操作。
     */
    private void handleNewProject() {
        int result = JOptionPane.showConfirmDialog(ui, "新建项目将清空当前所有未保存的数据，是否继续？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            this.currentSnippet = projectManager.createNewSnippet();
            updateUIWithCurrentSnippet();
            updateStatus("已创建新项目。");
        }
    }

    /**
     * 处理“打开项目”操作。
     */
    private void handleOpenProject() {
        // 简化：直接加载配置的文件
        this.currentSnippet = projectManager.loadProject();
        updateUIWithCurrentSnippet();
        updateStatus("已加载项目。");
    }

    /**
     * 处理“保存项目”操作。
     */
    private void handleSaveProject() {
        BehaviorSnippet snippet = getCurrentSnippetFromUI();
        if (snippet != null) {
            projectManager.saveProject(snippet);
            updateStatus("项目已保存。");
        } else {
            updateStatus("保存失败：项目信息不完整。");
        }
    }

    /**
     * 处理“导出项目”操作。
     */
    private void handleExportProject() {
        String json = projectManager.exportAsJson();
        // 简单的导出方式：复制到剪贴板
        ClipboardUtils.copyToClipboard(json);
        JOptionPane.showMessageDialog(ui, "项目已导出为JSON并复制到剪贴板。", "导出成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 处理“添加到列表”操作。
     */
    private void handleAddToEntryList() {
        var entry = ui.getGeneratorPanel().getGeneratedEntry();
        if (entry != null) {
            HeroGroup newGroup = new HeroGroup();
            newGroup.addHero(entry.getHeroName());
            currentSnippet.addHeroGroup(newGroup);
            // 重构后不再使用JList，直接更新数据模型
            // currentSnippet.addHeroGroup(newGroup) 已在前一行执行
            updateStatus("已添加行为条目。");
        } else {
            updateStatus("添加失败：请填写完整的身份、英雄名、生成描述和意图。");
            JOptionPane.showMessageDialog(ui, "请确保身份、英雄名、行为描述和意图均已填写。", "输入不完整", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 从UI表单中获取当前的视频片段信息，构建BehaviorSnippet对象。
     * @return 构建好的BehaviorSnippet，如果必填字段为空则返回null。
     */
    private BehaviorSnippet getCurrentSnippetFromUI() {
        if (this.currentSnippet == null) {
            this.currentSnippet = new BehaviorSnippet();
        }
        // 行为条目由单独的“添加”按钮管理，这里不修改
        return this.currentSnippet;
    }

    /**
     * 将当前的BehaviorSnippet数据更新到UI上。
     */
    private void updateUIWithCurrentSnippet() {
        if (currentSnippet == null) return;



        // 更新英雄组列表
        // 重构后不再使用JList显示英雄组
        // 更新UI的其他部分（如有需要）
    }

    /**
     * 在首次启动时加载初始数据（如模板库）。
     */
    private void loadInitialData() {
        // 加载模板库到UI
        List<String> templateNames = templateLibraryManager.loadTemplates().stream()
                .map(t -> t.getHeroName() + ": " + t.getActionDescription())
                .toList();
        DefaultListModel<String> templateModel = ui.getTemplateListModel();
        templateModel.clear();
        for (String name : templateNames) {
            templateModel.addElement(name);
        }
    }

    /**
     * 更新状态栏文本。
     * @param message 要显示的消息。
     */
    private void updateStatus(String message) {
        ui.getStatusLabel().setText(message);
    }

    /**
     * 工具类，用于处理系统剪贴板。
     */
    private static class ClipboardUtils {
        public static void copyToClipboard(String text) {
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        }
    }
}