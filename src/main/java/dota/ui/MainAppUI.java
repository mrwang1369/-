package dota.ui;

import dota.model.Hero;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主应用程序的图形用户界面。
 * 包含所有UI组件，并负责它们之间的简单交互。
 */
public class MainAppUI extends JFrame {
    // 顶部面板 - 项目管理
    private JPanel topPanel;
    private JButton newProjectButton;
    private JButton openProjectButton;
    private JButton saveProjectButton;
    private JButton exportProjectButton;

    // 左侧面板
    private JPanel leftPanel;
    // 左中：行为描述生成器面板
    private BehaviorGeneratorPanel generatorPanel;
    // 左下：常用模板库面板
    private JPanel templateLibraryPanel;
    private JList<String> templateList;
    private DefaultListModel<String> templateListModel;

    // 英雄槽位面板
    private JPanel heroSlotsPanel;

    // 底部面板 - 状态栏
    private JPanel bottomPanel;
    private JLabel statusLabel;
    
    // 英雄数据
    private List<Hero> allHeroes; // 所有可用英雄
    private Hero[] selectedHeroes; // 当前选中的10个英雄
    
    // 跟踪最后一个获得焦点的字段
    private JTextField lastFocusedField = null;

    public MainAppUI() {
        setTitle("DOTA2玩家行为标注工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // 居中显示
        initializeComponents();
        loadHeroesFromJson(); // 加载英雄数据
        addFocusListeners(); // 添加焦点监听器
        createHeroSlots();
        layoutComponents();
        // 初始化时不连接业务逻辑，由App主类负责
    }

    private void initializeComponents() {
        // 顶部面板
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newProjectButton = new JButton("新建");
        openProjectButton = new JButton("打开");
        saveProjectButton = new JButton("保存");
        exportProjectButton = new JButton("导出");
        topPanel.add(newProjectButton);
        topPanel.add(openProjectButton);
        topPanel.add(saveProjectButton);
        topPanel.add(exportProjectButton);

        // 左侧面板
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // 行为描述生成器
        generatorPanel = new BehaviorGeneratorPanel();
        leftPanel.add(generatorPanel);

        // 常用模板库面板
        templateLibraryPanel = new JPanel(new BorderLayout());
        templateLibraryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "常用模板库", TitledBorder.LEFT, TitledBorder.TOP));
        templateListModel = new DefaultListModel<>();
        templateList = new JList<>(templateListModel);
        templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        templateLibraryPanel.add(new JScrollPane(templateList), BorderLayout.CENTER);
        leftPanel.add(templateLibraryPanel);

        // 创建英雄槽位面板
        heroSlotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        heroSlotsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "英雄选择区", TitledBorder.CENTER, TitledBorder.TOP));
        heroSlotsPanel.setLayout(new GridLayout(2, 5, 5, 5)); // 2行5列布局，间距5px

        // 底部面板 - 状态栏
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("就绪");
        bottomPanel.add(statusLabel);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(heroSlotsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }



    // 提供对关键组件的访问器，以便App类可以添加事件监听器和更新数据
    public JButton getNewProjectButton() { return newProjectButton; }
    public JButton getOpenProjectButton() { return openProjectButton; }
    public JButton getSaveProjectButton() { return saveProjectButton; }
    public JButton getExportProjectButton() { return exportProjectButton; }
    public JButton getAddToEntryButton() { return generatorPanel.getAddToEntryButton(); }

    public BehaviorGeneratorPanel getGeneratorPanel() { return generatorPanel; }
    
    // 初始化组件时添加焦点监听器
    private void addFocusListeners() {
        JTextField heroNameField = generatorPanel.getHeroNameField();
        JTextField targetHeroField = generatorPanel.getTargetHeroField();
        
        // 为英雄名称输入框添加焦点监听器
        heroNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                lastFocusedField = (JTextField) e.getSource();
                System.out.println("焦点获得: 英雄输入框");
            }
        });
        
        // 为目标输入框添加焦点监听器
        targetHeroField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                lastFocusedField = (JTextField) e.getSource();
                System.out.println("焦点获得: 目标输入框");
            }
        });
    }

    public DefaultListModel<String> getTemplateListModel() { return templateListModel; }

    // 创建10个英雄槽位
    private void createHeroSlots() {
        for (int i = 0; i < 10; i++) {
            HeroSlotPanel slotPanel = new HeroSlotPanel(i, this);
            
                    // 创建基于焦点的智能监听器
            slotPanel.addHeroSelectionListener(new HeroSelectionListener() {
                @Override
                public void onHeroSelected(Hero hero) {
                    // 存储当前焦点信息
                    JTextField finalFocusedField;
                    
                    // 在事件发生时立即捕获焦点状态
                    if (generatorPanel.getHeroNameField().hasFocus()) {
                        finalFocusedField = generatorPanel.getHeroNameField();
                    } else if (generatorPanel.getTargetHeroField().hasFocus()) {
                        finalFocusedField = generatorPanel.getTargetHeroField();
                    } else {
                        // 如果都没有焦点，使用之前记录的最后一个焦点字段
                        finalFocusedField = lastFocusedField != null ? lastFocusedField : generatorPanel.getTargetHeroField();
                    }
                    
                    // 使用SwingUtilities.invokeLater确保在事件调度线程中更新UI
                    SwingUtilities.invokeLater(() -> {
                        insertHeroName(hero.getTitle(), finalFocusedField);
                    });
                }
            });
            
            heroSlotsPanel.add(slotPanel);
        }
        heroSlotsPanel.revalidate();
        heroSlotsPanel.repaint();
    }
    

    
    /**
     * 在指定的文本字段中插入英雄名称
     * @param heroName 英雄名称
     * @param field 文本字段
     */
    private void insertHeroName(String heroName, JTextField field) {
        if (field == null || heroName == null || heroName.isEmpty()) return;
        
        String currentText = field.getText();
        int caretPosition = field.getCaretPosition();
        
        // 如果有选中文本，用新英雄名替换
        if (field.getSelectedText() != null) {
            String newText = currentText.substring(0, field.getSelectionStart()) + 
                          heroName + 
                          currentText.substring(field.getSelectionEnd());
            field.setText(newText);
            field.setCaretPosition(field.getSelectionStart() + heroName.length());
        } else if (currentText.isEmpty()) {
            // 如果为空，直接设置
            field.setText(heroName);
            field.setCaretPosition(heroName.length());
        } else {
            // 如果已有内容，在光标位置插入，用顿号连接
            StringBuilder sb = new StringBuilder(currentText);
            sb.insert(caretPosition, (caretPosition > 0 && sb.charAt(caretPosition-1) != '、' ? "、" : "") + heroName);
            field.setText(sb.toString());
            field.setCaretPosition(caretPosition + heroName.length() + 1);
        }
    }

    /**
     * 从dota_heroes.json文件加载英雄数据
     */
    private void loadHeroesFromJson() {
        try {
            // 使用相对路径直接读取项目根目录下的dota_heroes.json文件
            File jsonFile = new File("dota_heroes.json");
            
            // 检查文件是否存在
            if (!jsonFile.exists()) {
                // 如果文件不存在，尝试从jar包同级目录查找
                jsonFile = new File(System.getProperty("user.dir"), "dota_heroes.json");
                if (!jsonFile.exists()) {
                    JOptionPane.showMessageDialog(this, "找不到 dota_heroes.json 文件，请确保它与程序在同一目录下。", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            allHeroes = objectMapper.readValue(jsonFile, new TypeReference<List<Hero>>(){});
            
            // 安全检查
            if (allHeroes == null) {
                allHeroes = new ArrayList<>();
                JOptionPane.showMessageDialog(this, "解析英雄数据失败，数据为空。", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 初始化选中的英雄数组
            selectedHeroes = new Hero[10];
            for (int i = 0; i < 10; i++) {
                selectedHeroes[i] = null; // 初始状态为未选择
            }
            
            System.out.println("成功加载 " + allHeroes.size() + " 个英雄数据");
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "加载英雄数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public List<Hero> getAllHeroes() {
        return allHeroes;
    }
    
    public Hero[] getSelectedHeroes() {
        return selectedHeroes;
    }
    
    public JLabel getStatusLabel() { return statusLabel; }
}