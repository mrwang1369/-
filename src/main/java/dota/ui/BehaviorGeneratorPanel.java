package dota.ui;

import dota.model.Skill;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 行为描述生成器 - 全新简化版
 */
public class BehaviorGeneratorPanel extends JPanel {
    
    private JTextField heroNameField;           // 英雄名称
    private JPanel contentPanel;                // 内容面板（标签 + 文本框）
    private JTextArea resultArea;               // 结果展示区
    private JButton generateButton;             // 生成按钮
    private List<JTextField> inputFields;       // 所有输入框
    private JTextField currentFocusedField;     // 当前获得焦点的输入框
    
    public BehaviorGeneratorPanel() {
        inputFields = new ArrayList<>();
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("行为描述生成器"));
        
        // 1. 顶部：英雄名称
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("英雄:"));
        heroNameField = new JTextField(15);
        topPanel.add(heroNameField);
        add(topPanel, BorderLayout.NORTH);
        
        // 2. 中部：内容区域（标签和文本框横向排列）
        contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(-1, 60));
        add(scrollPane, BorderLayout.CENTER);
        
        // 3. 底部：生成按钮 + 结果区
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        
        generateButton = new JButton("生成完整版");
        generateButton.addActionListener(e -> generateResult());
        bottomPanel.add(generateButton, BorderLayout.NORTH);
        
        resultArea = new JTextArea(3, 40);
        resultArea.setLineWrap(true);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(240, 240, 240));
        bottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 填充技能描述
     */
    public void fillFromSkill(Skill skill) {
        if (skill == null || skill.getFull_text() == null) return;
            
        // 清空
        contentPanel.removeAll();
        inputFields.clear();
        resultArea.setText("");
            
        String description = skill.getFull_text();
        System.out.println("原始描述：" + description);
        System.out.println("描述长度：" + description.length());
                
        // 手动解析“目标”和“位置”
        int lastEnd = 0;
        int matchCount = 0;
                
        while (true) {
            // 查找下一个“目标”或“位置”
            int targetIndex = description.indexOf("目标", lastEnd);
            int positionIndex = description.indexOf("位置", lastEnd);
                    
            // 都找不到，结束
            if (targetIndex == -1 && positionIndex == -1) {
                break;
            }
                    
            // 找到最近的匹配
            int nextMatchIndex;
            String matchedKeyword;
            if (targetIndex != -1 && positionIndex != -1) {
                if (targetIndex < positionIndex) {
                    nextMatchIndex = targetIndex;
                    matchedKeyword = "目标";
                } else {
                    nextMatchIndex = positionIndex;
                    matchedKeyword = "位置";
                }
            } else if (targetIndex != -1) {
                nextMatchIndex = targetIndex;
                matchedKeyword = "目标";
            } else {
                nextMatchIndex = positionIndex;
                matchedKeyword = "位置";
            }
                    
            matchCount++;
            System.out.println("找到匹配：" + matchedKeyword + " 位置：" + nextMatchIndex);
                    
            // 添加前面的文本
            if (lastEnd < nextMatchIndex) {
                String text = description.substring(lastEnd, nextMatchIndex);
                JLabel label = new JLabel(text);
                contentPanel.add(label);
                System.out.println("添加文本：" + text);
            }
                    
            // 添加文本框（替代“目标”或“位置”）
            JTextField field = new JTextField(10);
            field.setMaximumSize(new Dimension(150, 30));
            
            // 为每个动态文本框添加焦点监听器
            field.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    currentFocusedField = field;
                    System.out.println("动态文本框获得焦点：" + matchedKeyword);
                }
            });
            
            inputFields.add(field);
            contentPanel.add(field);
            System.out.println("添加文本框");
                    
            lastEnd = nextMatchIndex + matchedKeyword.length();
        }
                
        System.out.println("总共找到 " + matchCount + " 个匹配");
            
        // 添加剩余文本
        if (lastEnd < description.length()) {
            String text = description.substring(lastEnd);
            contentPanel.add(new JLabel(text));
            System.out.println("添加剩余文本：" + text);
        }
            
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 生成最终结果
     */
    private void generateResult() {
        StringBuilder result = new StringBuilder();
        
        // 遍历内容面板的所有组件
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JLabel) {
                result.append(((JLabel) comp).getText());
            } else if (comp instanceof JTextField) {
                result.append(((JTextField) comp).getText().trim());
            }
        }
        
        String finalText = result.toString();
        resultArea.setText(finalText);
        
        // 自动复制到剪贴板
        copyToClipboard(finalText);
    }
    
    /**
     * 复制文本到系统剪贴板
     */
    private void copyToClipboard(String text) {
        if (text == null || text.isEmpty()) return;
        
        try {
            java.awt.datatransfer.StringSelection stringSelection = 
                new java.awt.datatransfer.StringSelection(text);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(stringSelection, null);
            System.out.println("已复制到剪贴板：" + text);
        } catch (Exception e) {
            System.err.println("复制到剪贴板失败：" + e.getMessage());
        }
    }
    
    public JTextField getHeroNameField() {
        return heroNameField;
    }
    
    /**
     * 获取当前获得焦点的输入框
     */
    public JTextField getCurrentFocusedField() {
        return currentFocusedField;
    }
    
    /**
     * 插入文本到当前焦点所在的输入框
     */
    public void insertTextToFocusedField(String text) {
        JTextField targetField = currentFocusedField != null ? currentFocusedField : heroNameField;
        if (targetField == null || text == null || text.isEmpty()) return;
        
        String currentText = targetField.getText();
        int caretPosition = targetField.getCaretPosition();
        
        // 如果有选中文本，用新文本替换
        if (targetField.getSelectedText() != null) {
            String newText = currentText.substring(0, targetField.getSelectionStart()) + 
                          text + 
                          currentText.substring(targetField.getSelectionEnd());
            targetField.setText(newText);
            targetField.setCaretPosition(targetField.getSelectionStart() + text.length());
        } else {
            // 否则在光标位置插入
            String newText = currentText.substring(0, caretPosition) + text + 
                          currentText.substring(caretPosition);
            targetField.setText(newText);
            targetField.setCaretPosition(caretPosition + text.length());
        }
        
        System.out.println("插入文本到焦点字段：" + text);
    }
    
    public String getDescription() {
        return resultArea.getText().trim();
    }
}
