package dota.core;

import java.util.Map;
import java.util.HashMap;
import java.util.function.BiFunction;

/**
 * 行为描述生成器，负责根据输入的参数拼接成通顺的中文句子。
 * 此类包含静态方法，可直接调用。
 */
public class ActionDescriptionGenerator {

    // 用于存储技能与其对应中文名称的映射
    private static final Map<String, String> SKILL_NAME_MAP = new HashMap<>();
    static {
        SKILL_NAME_MAP.put("Q", "Q技能");
        SKILL_NAME_MAP.put("W", "W技能");
        SKILL_NAME_MAP.put("E", "E技能");
        SKILL_NAME_MAP.put("R", "大招");
        SKILL_NAME_MAP.put("闪烁", "闪烁刀");
        SKILL_NAME_MAP.put("跳刀", "跳刀");
    }

    // 用于存储基础效果动词映射（适用于"技能+效果+目标"模式）
    private static final Map<String, String> BASIC_EFFECT_VERB_MAP = new HashMap<>();
    
    // 用于存储特殊效果的处理函数
    private static final Map<String, BiFunction<String, String, String>> SPECIAL_EFFECT_HANDLERS = new HashMap<>();
    
    static {
        // 基础效果：技能+效果+目标 模式
        BASIC_EFFECT_VERB_MAP.put("造成伤害", "打中了");
        BASIC_EFFECT_VERB_MAP.put("禁锢了", "禁锢了");
        BASIC_EFFECT_VERB_MAP.put("减速", "减速了");
        BASIC_EFFECT_VERB_MAP.put("沉默", "沉默了");
        BASIC_EFFECT_VERB_MAP.put("眩晕", "眩晕了");
        BASIC_EFFECT_VERB_MAP.put("击退", "击退了");
        BASIC_EFFECT_VERB_MAP.put("缠绕", "缠绕了");
        BASIC_EFFECT_VERB_MAP.put("石化", "石化了");
        BASIC_EFFECT_VERB_MAP.put("恐惧", "恐惧了");
        BASIC_EFFECT_VERB_MAP.put("缴械", "缴械了");
        BASIC_EFFECT_VERB_MAP.put("显形", "显形了");
        
        // 特殊效果处理函数
        SPECIAL_EFFECT_HANDLERS.put("破隐一击", (skillName, targetHero) -> 
            skillName + "对" + targetHero + "发动了破隐一击");
        
        SPECIAL_EFFECT_HANDLERS.put("降低护甲", (skillName, targetHero) -> 
            skillName + "降低了" + targetHero + "的护甲");
            
        SPECIAL_EFFECT_HANDLERS.put("降低攻击力", (skillName, targetHero) -> 
            skillName + "降低了" + targetHero + "的攻击力");
            
        SPECIAL_EFFECT_HANDLERS.put("提升移速", (skillName, targetHero) -> 
            skillName + "提升了" + targetHero + "的移动速度");
            
        SPECIAL_EFFECT_HANDLERS.put("降低移速", (skillName, targetHero) -> 
            skillName + "降低了" + targetHero + "的移动速度");
            
        SPECIAL_EFFECT_HANDLERS.put("叠加负面状态", (skillName, targetHero) -> 
            skillName + "给" + targetHero + "叠加了负面状态");
            
        SPECIAL_EFFECT_HANDLERS.put("施加持续伤害", (skillName, targetHero) -> 
            skillName + "给" + targetHero + "施加了持续伤害");
    }

    /**
     * 生成英雄技能的行为描述。
     * @param skill 技能（如 Q, W, E, R, 闪烁）
     * @param state 状态（如 使用了, 预判释放了, 二段...）
     * @param targetHero 目标英雄名
     * @param effect 效果（如 禁锢了, 减速）
     * @return 拼接好的行为描述字符串。
     */
    public static String generateSkillDescription(String skill, String state, String targetHero, String effect) {
        StringBuilder sb = new StringBuilder();

        // 添加状态
        if (state != null && !state.isEmpty()) {
            sb.append(state).append(' ');
        }

        // 添加技能
        String skillName = SKILL_NAME_MAP.getOrDefault(skill, skill);
        
        // 处理特殊效果
        if (effect != null && !effect.isEmpty() && SPECIAL_EFFECT_HANDLERS.containsKey(effect)) {
            if (targetHero != null && !targetHero.isEmpty()) {
                return SPECIAL_EFFECT_HANDLERS.get(effect).apply(skillName, targetHero);
            } else {
                sb.append(skillName);
            }
        } else {
            // 基础效果处理
            sb.append(skillName);
            if (targetHero != null && !targetHero.isEmpty() && effect != null && !effect.isEmpty()) {
                String verb = BASIC_EFFECT_VERB_MAP.getOrDefault(effect, effect);
                sb.append(' ').append(verb).append(' ').append(targetHero);
            }
        }

        return sb.toString().trim();
    }

    /**
     * 生成道具使用的行为描述。
     * @param item 道具名称（如 回城卷轴, 树枝）
     * @param location 地点（如 上路一塔, 肉山坑）
     * @return 拼接好的行为描述字符串。
     */
    public static String generateItemDescription(String item, String location) {
        StringBuilder sb = new StringBuilder();
        sb.append("使用了").append(item);
        if (location != null && !location.isEmpty()) {
            sb.append("传送到").append(location);
        }
        return sb.toString();
    }

    /**
     * 生成攻击的行为描述。
     * @param attackType 攻击类型（如 平A, 连续平A, 走A）
     * @param targetHero 目标英雄名
     * @return 拼接好的行为描述字符串。
     */
    public static String generateAttackDescription(String attackType, String targetHero) {
        StringBuilder sb = new StringBuilder();
        sb.append(attackType);
        if (targetHero != null && !targetHero.isEmpty()) {
            sb.append("攻击").append(targetHero);
        }
        return sb.toString();
    }

    /**
     * 生成移动的行为描述。
     * @param direction 方向（如 左边河道, 泉水往上路）
     * @return 拼接好的行为描述字符串。
     */
    public static String generateMoveDescription(String direction) {
        return "从" + direction + "移动";
    }

    /**
     * 生成购买的行为描述。
     * @param itemType 道具类型（如 树枝, 魔瓶）
     * @return 拼接好的行为描述字符串。
     */
    public static String generateBuyDescription(String itemType) {
        return "购买了" + itemType;
    }

    /**
     * 生成走位的行为描述。
     * @param direction 方位（如 左, 右）
     * @return 拼接好的行为描述字符串。
     */
    public static String generatePositioningDescription(String direction) {
        return "向" + direction + "走位";
    }

    /**
     * 生成埋伏的行为描述。
     * @param location 埋伏的位置（如 左侧树丛）
     * @return 拼接好的行为描述字符串。
     */
    public static String generateAmbushDescription(String location) {
        return "在" + location + "后埋伏";
    }

    /**
     * 生成拆塔的行为描述。
     * @param lane 线路（上路、中路、下路）
     * @param towerLevel 塔级（一塔、二塔、三塔、高地塔）
     * @return 拼接好的行为描述字符串。
     */
    public static String generateTowerDestructionDescription(String lane, String towerLevel) {
        return "正在拆" + lane + towerLevel;
    }

    /**
     * 一个通用的模板方法，可用于未来扩展其他行为类型。
     * @param parts 要连接的字符串部分
     * @return 用空格连接后的字符串。
     */
    public static String generateGenericDescription(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part != null && !part.trim().isEmpty()) {
                sb.append(part.trim()).append(' ');
            }
        }
        return sb.toString().trim();
    }
}