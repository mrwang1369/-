package dota.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 临时工具类：用于从JSON文件中查找指定英雄的详细信息
 */
public class HeroJsonComparator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 从JSON文件中查找指定英雄的详细信息
     * 
     * @param filePath JSON文件路径
     * @param heroNames 要查找的英雄英文名列表
     * @return 包含英雄详细信息的Map，键为英雄名，值为对应的英雄数据Map
     * @throws Exception 读取或解析文件时可能抛出异常
     */
    public Map<String, Map<String, Object>> findHeroDetails(String filePath, List<String> heroNames) throws Exception {
        // 读取JSON文件
        List<Map<String, Object>> heroes = readHeroFile(filePath);
        
        // 创建英雄名到详细信息的映射
        Map<String, Map<String, Object>> heroMap = heroes.stream()
                .collect(Collectors.toMap(
                    hero -> (String) hero.get("name"),
                    hero -> hero,
                    (existing, replacement) -> existing
                ));
        
        // 查找指定英雄的详细信息
        Map<String, Map<String, Object>> result = new HashMap<>();
        for (String heroName : heroNames) {
            if (heroMap.containsKey(heroName)) {
                result.put(heroName, heroMap.get(heroName));
            } else {
                System.err.println("警告: 未找到英雄 '" + heroName + "' 的信息");
            }
        }
        
        return result;
    }
    
    /**
     * 读取JSON文件并解析为英雄列表
     */
    private List<Map<String, Object>> readHeroFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + filePath);
        }
        
        return objectMapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
    }
    
    /**
     * 主方法，提供执行入口
     */
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.err.println("Usage: java HeroJsonComparator <json_file> <hero_name1> <hero_name2> ...");
                System.err.println("Example: java HeroJsonComparator dota_heroes_no_duplicates_20260322_105010.json bounty-hunter brewmaster zeus");
                return;
            }
            
            // 提取JSON文件路径和英雄名称列表
            String jsonFile = args[0];
            List<String> heroNames = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                heroNames.add(args[i]);
            }
            
            HeroJsonComparator comparator = new HeroJsonComparator();
            Map<String, Map<String, Object>> heroDetails = new HashMap<>();
            try {
                heroDetails = comparator.findHeroDetails(args[0], heroNames);
            } catch (Exception e) {
                System.err.println("读取文件时发生错误: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            if (heroDetails.isEmpty()) {
                System.out.println("未找到任何指定英雄的信息。");
            } else {
                System.out.println("成功找到 " + heroDetails.size() + " 个英雄的详细信息:");
                for (Map.Entry<String, Map<String, Object>> entry : heroDetails.entrySet()) {
                    Map<String, Object> hero = entry.getValue();
                    System.out.println("\n--- " + hero.get("title") + " (" + entry.getKey() + ") ---");
                    System.out.println("胜率: " + hero.get("win_rate"));
                    System.out.println("出场率: " + hero.get("pick_rate"));
                    
                    // 输出命石信息
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> facets = (List<Map<String, Object>>) hero.get("facets");
                    if (facets != null && !facets.isEmpty()) {
                        System.out.println("命石:");
                        for (Map<String, Object> facet : facets) {
                            System.out.println("  - " + facet.get("name") + ": " + facet.get("description"));
                        }
                    }
                    
                    // 输出技能信息
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> skills = (List<Map<String, Object>>) hero.get("skills");
                    if (skills != null && !skills.isEmpty()) {
                        System.out.println("技能:");
                        for (Map<String, Object> skill : skills) {
                            System.out.println("  - " + skill.get("name") + " (" + skill.get("hotkey") + "): " + skill.get("full_text"));
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}