package dota.ui;

import dota.core.ActionDescriptionGenerator;
import dota.model.BehaviorEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 行为描述生成器面板，允许用户通过下拉菜单选择参数来生成行为描述。
 */
public class BehaviorGeneratorPanel extends JPanel {
    private final Map<String, JComponent> dynamicComponents = new HashMap<>();

    // 主要组件
    private JComboBox<String> identityComboBox;
    private JTextField heroNameField;
    private JComboBox<String> actionTypeComboBox;
    private JPanel dynamicParamPanel;
    private JTextArea generatedDescriptionArea;
    private JButton generateButton;
    private JButton addToEntryButton;

    // 动态组件 - 技能相关
    private JComboBox<String> skillComboBox;
    private JComboBox<String> stateComboBox;
    private JComboBox<String> targetHeroTypeComboBox;
    private JTextField targetHeroField;

    /**
     * 获取目标英雄文本字段，供外部组件引用。
     * @return 目标英雄文本字段
     */
    public JTextField getTargetHeroField() {
        return targetHeroField;
    }
    
    public JTextField getHeroNameField() {
        return heroNameField;
    }
    
    private JComboBox<String> effectComboBox;

    public BehaviorGeneratorPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        setupDynamicParams();
        buildUI();
        attachListeners();
    }

    private void initializeComponents() {
        // 身份选择
        identityComboBox = new JComboBox<>(new String[]{"主角", "队友", "敌方"});

        // 英雄名输入
        heroNameField = new JTextField(10);

        // 行为类型选择
        actionTypeComboBox = new JComboBox<>(new String[]{"英雄技能", "道具使用", "攻击", "移动", "购买", "走位", "埋伏", "拆塔"});

        // 动态参数面板
        dynamicParamPanel = new JPanel();
        dynamicParamPanel.setLayout(new BoxLayout(dynamicParamPanel, BoxLayout.Y_AXIS));

        // 生成按钮
        generateButton = new JButton("生成行为描述");

        // 生成的描述显示区
        generatedDescriptionArea = new JTextArea(3, 40);
        generatedDescriptionArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(generatedDescriptionArea);

        // 添加到列表按钮
        addToEntryButton = new JButton("添加到当前片段");

        // 初始化动态组件
        skillComboBox = new JComboBox<>(new String[]{"Q", "W", "E", "R", "闪烁", "跳刀"});
        stateComboBox = new JComboBox<>(new String[]{"使用了", "预判释放了", "二段..."});
        targetHeroTypeComboBox = new JComboBox<>(new String[]{"敌方", "队友", "位置"});
        this.targetHeroField = new JTextField(10);
        effectComboBox = new JComboBox<>(new String[]{
            "造成伤害", "禁锢了", "减速", "沉默",
            "眩晕", "击退", "缠绕", "石化", "恐惧", "缴械", 
            "显形", "破隐一击", "降低护甲", "降低攻击力",
            "提升移速", "降低移速", "叠加负面状态", "施加持续伤害"
        });
    }

    private void setupDynamicParams() {
        // 默认显示技能相关的组件
        updateDynamicParams("英雄技能");
    }

    private void buildUI() {
        // 使用GridBagLayout进行更精确的布局
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：身份、英雄名、行为类型
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("身份:"), gbc);
        gbc.gridx = 1;
        add(identityComboBox, gbc);

        gbc.gridx = 2;
        add(new JLabel("英雄:"), gbc);
        gbc.gridx = 3;
        add(heroNameField, gbc);

        gbc.gridx = 4;
        add(new JLabel("行为:"), gbc);
        gbc.gridx = 5;
        add(actionTypeComboBox, gbc);

        // 第二行：动态参数面板
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 6;
        add(dynamicParamPanel, gbc);

        // 第三行：生成按钮和显示区
        gbc.gridy = 2; gbc.gridwidth = 2;
        add(generateButton, gbc);

        gbc.gridx = 2; gbc.gridwidth = 4;
        add(new JScrollPane(generatedDescriptionArea), gbc);

        // 第四行：意图描述


        // 第五行：添加到列表按钮
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 6;
        add(addToEntryButton, gbc);
    }

    private void attachListeners() {
        actionTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) actionTypeComboBox.getSelectedItem();
                updateDynamicParams(selectedType);
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateDescription();
            }
        });
    }

    /**
     * 根据选择的行为类型，更新动态参数面板中的组件。
     * @param actionType 选择的行为类型。
     */
    private void updateDynamicParams(String actionType) {
        dynamicParamPanel.removeAll();

        if ("英雄技能".equals(actionType)) {
            addDynamicSkillComponents();
        } else if ("道具使用".equals(actionType)) {
            addDynamicItemComponents();
        } else if ("攻击".equals(actionType)) {
            addDynamicAttackComponents();
        } else if ("移动".equals(actionType)) {
            addDynamicMoveComponents();
        } else if ("购买".equals(actionType)) {
            addDynamicBuyComponents();
        } else if ("走位".equals(actionType)) {
            addDynamicPositioningComponents();
        } else if ("埋伏".equals(actionType)) {
            addDynamicAmbushComponents();
        } else if ("拆塔".equals(actionType)) {
            addDynamicTowerDestructionComponents();
        }

        dynamicParamPanel.revalidate();
        dynamicParamPanel.repaint();
    }

    private void addDynamicSkillComponents() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("状态:"));
        panel.add(stateComboBox);
        panel.add(new JLabel("技能:"));
        panel.add(skillComboBox);
        panel.add(new JLabel("效果:"));
        panel.add(effectComboBox);
        panel.add(new JLabel("目标类型:"));
        panel.add(targetHeroTypeComboBox);
        panel.add(new JLabel("目标:"));
        panel.add(targetHeroField);
        dynamicParamPanel.add(panel);
    }

    private void addDynamicItemComponents() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("道具:"));
        JTextField itemField = new JTextField(10);
        panel.add(itemField);
        panel.add(new JLabel("地点:"));
        JTextField locationField = new JTextField(10);
        panel.add(locationField);
        dynamicComponents.put("itemField", itemField);
        dynamicComponents.put("locationField", locationField);
        dynamicParamPanel.add(panel);
    }

    private void addDynamicAttackComponents() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("攻击方式:"));
        JComboBox<String> attackTypeCombo = new JComboBox<>(new String[]{"平A", "连续平A", "走A", "强化普攻"});
        panel.add(attackTypeCombo);
        panel.add(new JLabel("目标:"));
        JTextField attackTargetField = new JTextField(10);
        panel.add(attackTargetField);
        dynamicComponents.put("attackTypeCombo", attackTypeCombo);
        dynamicComponents.put("attackTargetField", attackTargetField);
        dynamicParamPanel.add(panel);
    }

    private void addDynamicMoveComponents() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("方向:"));
        JTextField directionField = new JTextField(20);
        panel.add(directionField);
        dynamicComponents.put("directionField", directionField);
        dynamicParamPanel.add(panel);
    }

    private void addDynamicPositioningComponents() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("方位:"));
        JComboBox<String> positionCombo = new JComboBox<>(new String[]{"左", "右", "前", "后"});
        panel.add(positionCombo);
        dynamicComponents.put("positionCombo", positionCombo);
        dynamicParamPanel.add(panel);
    }

    private void addDynamicAmbushComponents() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("位置:"));
        JTextField ambushLocationField = new JTextField(10);
        panel.add(ambushLocationField);
        dynamicComponents.put("ambushLocationField", ambushLocationField);
        dynamicParamPanel.add(panel);
    }

    private void addDynamicTowerDestructionComponents() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("线路:"));
        JComboBox<String> laneCombo = new JComboBox<>(new String[]{"上路", "中路", "下路"});
        panel.add(laneCombo);
        panel.add(new JLabel("塔级:"));
        JComboBox<String> towerLevelCombo = new JComboBox<>(new String[]{"一塔", "二塔", "三塔", "高地塔"});
        panel.add(towerLevelCombo);
        dynamicComponents.put("laneCombo", laneCombo);
        dynamicComponents.put("towerLevelCombo", towerLevelCombo);
        dynamicParamPanel.add(panel);
    }

    private void addDynamicBuyComponents() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("道具类型:"));
        JTextField itemTypeField = new JTextField(10);
        panel.add(itemTypeField);
        dynamicComponents.put("itemTypeField", itemTypeField);
        dynamicParamPanel.add(panel);
    }

    /**
     * 根据当前表单的选择，调用核心逻辑生成行为描述。
     */
    private void generateDescription() {
        String actionType = (String) actionTypeComboBox.getSelectedItem();
        StringBuilder description = new StringBuilder();

        if ("英雄技能".equals(actionType)) {
            String skill = (String) skillComboBox.getSelectedItem();
            String state = (String) stateComboBox.getSelectedItem();
            String targetHero = targetHeroField.getText().trim();
            String effect = (String) effectComboBox.getSelectedItem();
            description.append(ActionDescriptionGenerator.generateSkillDescription(skill, state, targetHero, effect));
        } else if ("道具使用".equals(actionType)) {
            JTextField itemField = (JTextField) dynamicComponents.get("itemField");
            JTextField locationField = (JTextField) dynamicComponents.get("locationField");
            if (itemField != null && locationField != null) {
                description.append(ActionDescriptionGenerator.generateItemDescription(
                        itemField.getText(), locationField.getText()));
            }
        } else if ("攻击".equals(actionType)) {
            JComboBox<String> attackTypeCombo = (JComboBox<String>) dynamicComponents.get("attackTypeCombo");
            JTextField attackTargetField = (JTextField) dynamicComponents.get("attackTargetField");
            if (attackTypeCombo != null && attackTargetField != null) {
                description.append(ActionDescriptionGenerator.generateAttackDescription(
                        (String) attackTypeCombo.getSelectedItem(), attackTargetField.getText()));
            }
        } else if ("移动".equals(actionType)) {
            JTextField directionField = (JTextField) dynamicComponents.get("directionField");
            if (directionField != null) {
                description.append(ActionDescriptionGenerator.generateMoveDescription(directionField.getText()));
            }
        } else if ("购买".equals(actionType)) {
            JTextField itemTypeField = (JTextField) dynamicComponents.get("itemTypeField");
            if (itemTypeField != null) {
                description.append(ActionDescriptionGenerator.generateBuyDescription(itemTypeField.getText()));
            }
        } else if ("走位".equals(actionType)) {
            JComboBox<String> positionCombo = (JComboBox<String>) dynamicComponents.get("positionCombo");
            if (positionCombo != null) {
                description.append(ActionDescriptionGenerator.generatePositioningDescription((String) positionCombo.getSelectedItem()));
            }
        } else if ("埋伏".equals(actionType)) {
            JTextField ambushLocationField = (JTextField) dynamicComponents.get("ambushLocationField");
            if (ambushLocationField != null) {
                description.append(ActionDescriptionGenerator.generateAmbushDescription(ambushLocationField.getText()));
            }
        } else if ("拆塔".equals(actionType)) {
            JComboBox<String> laneCombo = (JComboBox<String>) dynamicComponents.get("laneCombo");
            JComboBox<String> towerLevelCombo = (JComboBox<String>) dynamicComponents.get("towerLevelCombo");
            if (laneCombo != null && towerLevelCombo != null) {
                description.append(ActionDescriptionGenerator.generateTowerDestructionDescription(
                        (String) laneCombo.getSelectedItem(), (String) towerLevelCombo.getSelectedItem()));
            }
        }

        generatedDescriptionArea.setText(description.toString());
    }

    /**
     * 获取生成的完整BehaviorEntry。
     * @return 当前表单生成的BehaviorEntry对象，如果信息不全则返回null。
     */
    public BehaviorEntry getGeneratedEntry() {
        String identityStr = (String) identityComboBox.getSelectedItem();
        BehaviorEntry.Identity identity = null;
        for (BehaviorEntry.Identity id : BehaviorEntry.Identity.values()) {
            if (id.toString().equals(identityStr)) {
                identity = id;
                break;
            }
        }
        String heroName = heroNameField.getText().trim();
        String actionDesc = generatedDescriptionArea.getText().trim();

        if (identity == null || heroName.isEmpty() || actionDesc.isEmpty()) {
            return null;
        }

        return new BehaviorEntry(identity, heroName, actionDesc, "无特殊意图");
    }

    /**
     * 获取“添加到列表”按钮，以便主窗口可以为其添加监听器。
     */
    public JButton getAddToEntryButton() {
        return addToEntryButton;
    }
}