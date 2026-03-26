package dota.ui;

import dota.model.Hero;
import dota.model.Skill;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个英雄凹槽的面板，包含一个按钮和技能展示区域。
 */
class HeroSlotPanel extends JPanel implements ActionListener {
    private final JButton heroButton;
    private Hero selectedHero; // 当前选中的英雄，初始为 null
    private final List<HeroSelectionListener> listeners = new ArrayList<>();
    private final int slotIndex; // 槽位索引
    private final MainAppUI mainUI; // 对主界面的引用
    private BehaviorGeneratorPanel behaviorGeneratorPanel; // 引用行为生成器面板
    
    // 技能按钮容器
    private JPanel skillButtonsPanel;
    private JScrollPane skillScrollPane;

    public HeroSlotPanel(int slotIndex, MainAppUI mainUI) {
        this.slotIndex = slotIndex;
        this.mainUI = mainUI;
        this.selectedHero = null; // 初始未选择
        this.behaviorGeneratorPanel = mainUI.getBehaviorGeneratorPanel(); // 获取行为生成器引用
        
        // 使用 BoxLayout 纵向排列
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(260, 450)); // 增大尺寸，加宽到 260
        setMaximumSize(new Dimension(260, 550));

        // 英雄选择按钮
        heroButton = new JButton("未选择");
        heroButton.setBackground(Color.LIGHT_GRAY);
        heroButton.setForeground(Color.DARK_GRAY);
        heroButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        heroButton.setPreferredSize(new Dimension(220, 35));
        heroButton.setMaximumSize(new Dimension(220, 35));
        heroButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroButton.addActionListener(this);
        
        add(heroButton);
        add(Box.createRigidArea(new Dimension(0, 5))); // 5px 间距
        
        // 技能按钮容器（带滚动）
        skillButtonsPanel = new JPanel();
        skillButtonsPanel.setLayout(new BoxLayout(skillButtonsPanel, BoxLayout.Y_AXIS));
        skillButtonsPanel.setBackground(Color.WHITE);
        // 设置面板宽度与 JScrollPane 视口匹配
        skillButtonsPanel.setMaximumSize(new Dimension(245, Short.MAX_VALUE));
        
        skillScrollPane = new JScrollPane(skillButtonsPanel);
        skillScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "技能列表", 
            javax.swing.border.TitledBorder.LEFT, 
            javax.swing.border.TitledBorder.TOP));
        // 外部尺寸 260×350px，内部可用约 245px 宽
        skillScrollPane.setPreferredSize(new Dimension(260, 350));
        skillScrollPane.setMaximumSize(new Dimension(260, 350));
        skillScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        // 不显示滚动条，但可以滚动
        skillScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        skillScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(skillScrollPane);
    }
    
    public void addHeroSelectionListener(HeroSelectionListener listener) {
        listeners.add(listener);
    }

    /**
     * 显示英雄选择对话框
     */
    private void showHeroSelectionDialog() {
        // 获取所有可用英雄
        List<Hero> availableHeroes = mainUI.getAllHeroes();
        
        // 安全检查
        if (availableHeroes == null || availableHeroes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可用的英雄数据，请检查 dota_heroes.json 文件。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 创建选择对话框
        JDialog dialog = new JDialog(mainUI, "选择英雄", true);
        dialog.setLayout(new BorderLayout());
        
        // 创建英雄列表
        DefaultListModel<Hero> listModel = new DefaultListModel<>();
        for (Hero hero : availableHeroes) {
            if (hero != null && hero.getTitle() != null && !hero.getTitle().isEmpty()) {
                listModel.addElement(hero);
            }
        }
        
        JList<Hero> heroList = new JList<>(listModel);
        heroList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        heroList.setLayoutOrientation(JList.VERTICAL_WRAP);
        heroList.setVisibleRowCount(-1);
        JScrollPane scrollPane = new JScrollPane(heroList);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // 添加确认按钮
        JButton confirmButton = new JButton("确定");
        confirmButton.addActionListener(e -> {
            Hero selected = heroList.getSelectedValue();
            if (selected != null) {
                selectHero(selected);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "请先选择一个英雄", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(mainUI);
        dialog.setVisible(true);
    }
    
    /**
     * 选择英雄并更新 UI
     */
    private void selectHero(Hero hero) {
        this.selectedHero = hero;
        this.heroButton.setText(hero.getTitle());
        this.heroButton.setBackground(new Color(144, 238, 144)); // 浅绿色
        this.heroButton.setForeground(Color.BLACK);
            
        // 更新主界面中的选中数组
        mainUI.getSelectedHeroes()[slotIndex] = hero;
            
        // 加载技能按钮
        loadSkillButtons();
    }
        
    /**
     * 加载英雄的所有技能按钮
     */
    private void loadSkillButtons() {
        if (selectedHero == null) {
            return;
        }
            
        skillButtonsPanel.removeAll();
        List<Skill> skills = selectedHero.getSkills();
            
        if (skills == null || skills.isEmpty()) {
            skillButtonsPanel.setVisible(false);
            return;
        }
            
        for (Skill skill : skills) {
            JButton btn = createSkillButton(skill);
            skillButtonsPanel.add(btn);
            skillButtonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        // 不添加弹性空间，让按钮自然排列
            
        skillButtonsPanel.setVisible(true);
        skillButtonsPanel.revalidate();
        skillButtonsPanel.repaint();
    }
        
    /**
     * 创建自定义绘制的技能按钮
     */
    private JButton createSkillButton(Skill skill) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                    
                // 根据状态绘制背景
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(200, 230, 255)); // 按下深蓝色
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(230, 240, 255)); // 悬停浅蓝色
                } else {
                    g2d.setColor(Color.WHITE); // 默认白色
                }
                g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                // 绘制边框
                g2d.setColor(new Color(180, 180, 180));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    
                // 绘制中间分割线
                int splitY = getHeight() / 2;
                g2d.setColor(new Color(220, 220, 220));
                g2d.drawLine(5, splitY, getWidth() - 5, splitY);
                    
                // 绘制上部：快捷键 | 技能名
                g2d.setColor(new Color(60, 60, 60));
                g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
                String hotkey = skill.getHotkey() != null ? skill.getHotkey() : "被动";
                String skillName = skill.getName() != null ? skill.getName() : "未知技能";
                String topText = hotkey + " | " + skillName;
                g2d.drawString(topText, 10, 20);
                    
                // 绘制下部：技能描述（多行）
                g2d.setColor(new Color(80, 80, 80));
                g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
                String description = skill.getFull_text() != null ? skill.getFull_text() : "";
                // 充分利用按钮宽度，左右各留 10px 边距
                drawMultilineText(g2d, description, 10, 35, getWidth() - 20, 15);
                    
                g2d.dispose();
            }
        };
            
        // 设置按钮属性：宽度与容器内部匹配
        button.setPreferredSize(new Dimension(245, 70));
        button.setMaximumSize(new Dimension(245, 70));
        button.setAlignmentX(Component.LEFT_ALIGNMENT); // 左对齐，填满左侧
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 手型光标，表示可点击
            
        // 添加点击事件监听器：点击技能后填充到行为生成器
        button.addActionListener(e -> {
            System.out.println("点击了技能：" + skill.getName());
            // 将技能描述填充到行为生成器
            if (behaviorGeneratorPanel != null) {
                behaviorGeneratorPanel.fillFromSkill(skill);
            }
            // 触发回调
            for (HeroSelectionListener listener : listeners) {
                listener.onSkillSelected(skill); // 触发回调
            }
        });
            
        // 设置 Tooltip
        StringBuilder tooltip = new StringBuilder("<html>");
        tooltip.append("<b>").append(skill.getName()).append("</b><br>");
        tooltip.append("快捷键：").append(skill.getHotkey()).append("<br>");
        tooltip.append("描述：").append(skill.getFull_text()).append("</html>");
        button.setToolTipText(tooltip.toString());
            
        return button;
    }
        
    /**
     * 绘制多行文本，支持自动换行和截断
     */
    private void drawMultilineText(Graphics2D g2d, String text, int x, int y, int maxWidth, int lineHeight) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
            
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        int currentY = y;
        int maxLines = 3;
        int linesDrawn = 0;
            
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            int textWidth = g2d.getFontMetrics().stringWidth(testLine);
                
            if (textWidth > maxWidth) {
                // 当前行已满，绘制并换行
                if (linesDrawn >= maxLines - 1) {
                    // 已达最大行数 -1，最后一行加省略号
                    String lastLine = line.toString();
                    while (g2d.getFontMetrics().stringWidth(lastLine + "...") > maxWidth && lastLine.length() > 0) {
                        lastLine = lastLine.substring(0, lastLine.length() - 1);
                    }
                    g2d.drawString(lastLine + "...", x, currentY);
                    return;
                }
                    
                g2d.drawString(line.toString(), x, currentY);
                line = new StringBuilder(word);
                currentY += lineHeight;
                linesDrawn++;
            } else {
                line.append(line.length() == 0 ? "" : " ").append(word);
            }
        }
            
        // 绘制剩余的行
        if (line.length() > 0 && linesDrawn < maxLines) {
            String finalLine = line.toString();
            if (linesDrawn == maxLines - 1 && g2d.getFontMetrics().stringWidth(finalLine) > maxWidth) {
                while (g2d.getFontMetrics().stringWidth(finalLine + "...") > maxWidth && finalLine.length() > 0) {
                    finalLine = finalLine.substring(0, finalLine.length() - 1);
                }
                finalLine += "...";
            }
            g2d.drawString(finalLine, x, currentY);
        }
    }
    
    /**
     * 当英雄按钮被点击时调用。
     * 将英雄名称填入指定的文本字段。
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectedHero != null) {
            // 已选择的英雄，触发选择事件
            for (HeroSelectionListener listener : listeners) {
                listener.onHeroSelected(selectedHero);
            }
        } else {
            // 未选择状态，弹出选择对话框
            showHeroSelectionDialog();
        }
    }
    
    public void onHeroButtonClick() {
        // 此方法已过时，使用onHeroSelected事件代替
    }
    
    public JButton getButton() {
        return heroButton;
    }
    
    public MainAppUI getMainUI() {
        return mainUI;
    }
}
