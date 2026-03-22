package dota.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于将文本格式的英雄技能数据解析并转换为符合 dota_heroes.json 结构的 JSON 对象。
 */
public class HeroSkillParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 将用户提供的文本块解析为一个或多个英雄的JSON对象列表。
     * @param textBlock 包含一个或多个英雄技能描述的完整文本。
     * @return 包含解析后英雄JSON节点的列表。
     */
    public static List<ObjectNode> parseTextToHeroes(String textBlock) {
        List<ObjectNode> heroes = new ArrayList<>();
        // 使用正则表达式按英雄条目（以数字编号开始）进行分割
        Pattern heroPattern = Pattern.compile("^\\s*\\d+\\.\\s*英雄：(.+?)$", Pattern.MULTILINE);
        Matcher matcher = heroPattern.matcher(textBlock);

        int start = 0;
        while (matcher.find(start)) {
            int heroStart = matcher.start();
            int heroEnd = findNextHeroOrEnd(textBlock, matcher.end());
            String heroText = textBlock.substring(heroStart, heroEnd).trim();
            ObjectNode heroNode = parseSingleHero(heroText);
            if (heroNode != null) {
                heroes.add(heroNode);
            }
            start = matcher.end();
        }

        return heroes;
    }

    /**
     * 查找下一个英雄条目的开始位置，或返回文本末尾。
     * @param text 完整的文本。
     * @param fromIndex 开始搜索的位置。
     * @return 下一个英雄条目开始的索引，如果未找到，则返回文本长度。
     */
    private static int findNextHeroOrEnd(String text, int fromIndex) {
        Pattern nextHeroPattern = Pattern.compile("^\\s*\\d+\\.\\s*英雄：", Pattern.MULTILINE);
        Matcher m = nextHeroPattern.matcher(text);
        if (m.find(fromIndex)) {
            return m.start();
        }
        return text.length();
    }

    /**
     * 解析单个英雄的文本，并返回其对应的JSON对象。
     * @param heroText 单个英雄的完整文本。
     * @return 表示该英雄的JSON对象，如果解析失败则返回null。
     */
    private static ObjectNode parseSingleHero(String heroText) {
        ObjectNode heroNode = mapper.createObjectNode();
        ArrayNode skillsArray = mapper.createArrayNode();
        ArrayNode facetsArray = mapper.createArrayNode();

        String[] lines = heroText.split("[\r\n]+|");
        String heroName = "";

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("英雄：")) {
                heroName = extractHeroName(line);
                heroNode.put("name", normalizeName(heroName));
                heroNode.put("title", heroName);
            } else if (line.startsWith("命石：")) {
                String[] facetNames = line.substring(4).split(",\\s*|、\\s*|\\s+and\\s+|&|；\\s*|;\\s*");
                for (String name : facetNames) {
                    if (!name.trim().isEmpty()) {
                        ObjectNode facetNode = mapper.createObjectNode();
                        facetNode.put("name", name.trim());
                        facetNode.put("description", "");
                        facetsArray.add(facetNode);
                    }
                }
            } else if (line.startsWith("技能：")) {
                continue; // 技能标题行，跳过
            } else if (line.contains("：") && !line.startsWith("简化后的") && !line.startsWith("列表未完")) {
                parseSkillLine(line, skillsArray);
            }
        }

        heroNode.set("facets", facetsArray);
        heroNode.set("skills", skillsArray);

        return heroNode;
    }

    /**
     * 从“英雄：X”格式的字符串中提取英雄名称。
     */
    private static String extractHeroName(String line) {
        return line.substring(3).trim();
    }

    /**
     * 将中文英雄名转换为标准化的小写英文标识符。
     * 在实际应用中，这可能需要一个完整的映射表。
     * 这里提供一个简化的实现，仅处理已知的几个英雄。
     */
    private static String normalizeName(String chineseName) {
        switch (chineseName) {
            case "干扰者": return "disruptor";
            case "末日使者": return "doom-bringer";
            case "龙骑士": return "dragon-knight";
            case "卓尔游侠": return "drow-ranger";
            case "大地之灵": return "earth-spirit";
            case "撼地者": return "earthshaker";
            case "上古巨神": return "elder-titan";
            case "灰烬之灵": return "ember-spirit";
            case "谜团": return "enigma";
            case "虚空假面": return "faceless-void";
            case "天涯墨客": return "grimstroke";
            case "矮人直升机": return "goblin-shredder";
            case "森海飞霞": return "hoodwink";
            case "哈斯卡": return "huskar";
            case "祈求者": return "invoker";
            case "艾欧": return "io";
            case "杰奇洛": return "jakiro";
            case "主宰": return "juggernaut";
            case "光之守卫": return "keeper-of-the-light";
            case "凯": return "kunkka";
            default: return chineseName.toLowerCase().replace(' ', '-');
        }
    }

    /**
     * 解析包含技能名称和效果的一行文本，并将其添加到技能数组中。
     * @param line 格式如 "风雷之击 (Q)：对前方敌人造成伤害。"
     * @param skillsArray 目标技能数组。
     */
    private static void parseSkillLine(String line, ArrayNode skillsArray) {
        // 支持多种分隔符，如“：”、“：”或“-”
        String[] parts = line.split(":[^-—]*|-|—", 2);
        if (parts.length < 2) return;

        String skillHeader = parts[0].trim();
        String fullText = parts[1].trim();

        // 提取技能名称和快捷键，例如 “风雷之击 (Q)” -> name="风雷之击", hotkey="Q"
        Pattern headerPattern = Pattern.compile("^(.+?)\\s*\\((.*)\\)$");
        Matcher headerMatcher = headerPattern.matcher(skillHeader);

        String name = skillHeader; // 默认使用整个头作为名称
        String hotkey = "被动"; // 默认快捷键

        if (headerMatcher.matches()) {
            name = headerMatcher.group(1).trim();
            hotkey = headerMatcher.group(2).trim();
            // 处理像“碎片/神杖技能”这样的特殊情况
            if (hotkey.contains("/")) {
                hotkey = hotkey.split("/")[1]; // 取斜杠后的部分
            }
        } else if (skillHeader.endsWith("(先天技能)")) {
            name = skillHeader.replace("(先天技能)", "").trim();
            hotkey = "先天技能";
        }

        ObjectNode skillNode = mapper.createObjectNode();
        skillNode.put("name", name);
        skillNode.put("hotkey", hotkey);
        skillNode.put("full_text", fullText);

        skillsArray.add(skillNode);
    }
}
