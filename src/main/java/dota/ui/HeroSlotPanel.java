package dota.ui;

import dota.model.Hero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个英雄凹槽的面板，包含一个按钮。
 */
class HeroSlotPanel extends JPanel implements ActionListener {
    private final JButton heroButton;
    private Hero selectedHero; // 当前选中的英雄，初始为null
    private final List<HeroSelectionListener> listeners = new ArrayList<>();
    private final int slotIndex; // 槽位索引
    private final MainAppUI mainUI; // 对主界面的引用

    public HeroSlotPanel(int slotIndex, MainAppUI mainUI) {
        this.slotIndex = slotIndex;
        this.mainUI = mainUI;
        this.selectedHero = null; // 初始未选择
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(100, 50)); // 设置固定大小

        heroButton = new JButton("未选择");
        heroButton.setBackground(Color.LIGHT_GRAY);
        heroButton.setForeground(Color.DARK_GRAY);
        
        heroButton.addActionListener(this);

        add(heroButton, BorderLayout.CENTER);
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
     * 选择英雄并更新UI
     */
    private void selectHero(Hero hero) {
        this.selectedHero = hero;
        this.heroButton.setText(hero.getTitle());
        
        // 保持按钮的默认外观，不改变颜色
        // this.heroButton.setBackground(Color.GREEN);
        // this.heroButton.setForeground(Color.WHITE);
        
        // 更新主界面中的选中数组
        mainUI.getSelectedHeroes()[slotIndex] = hero;
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
