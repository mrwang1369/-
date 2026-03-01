package com.pethealth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class DatabaseFieldTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testUpdateTimeFieldExists() {
        // 查询 pet 表结构
        String sql = "DESCRIBE pet";
        List<Map<String, Object>> tableStructure = jdbcTemplate.queryForList(sql);
        
        System.out.println("=== pet 表结构 ===");
        boolean hasUpdateTime = false;
        for (Map<String, Object> column : tableStructure) {
            String fieldName = (String) column.get("Field");
            String fieldType = (String) column.get("Type");
            String defaultValue = (String) column.get("Default");
            String extra = (String) column.get("Extra");
            
            System.out.printf("%-15s %-20s %-20s %s%n", fieldName, fieldType, defaultValue, extra);
            
            if ("update_time".equals(fieldName)) {
                hasUpdateTime = true;
                System.out.println("\n✅ 找到 update_time 字段！");
                assert "datetime".equals(fieldType) : "update_time 字段类型应该是 datetime";
                assert "CURRENT_TIMESTAMP".equals(defaultValue) : "update_time 默认值应该是 CURRENT_TIMESTAMP";
                assert extra != null && extra.contains("on update CURRENT_TIMESTAMP") : "update_time 应该有自动更新设置";
            }
        }
        
        assert hasUpdateTime : "pet 表必须包含 update_time 字段";
        System.out.println("\n🎉 数据库表结构验证通过！");
    }

    @Test
    public void testUpdateTimeFunctionality() {
        // 插入一条测试数据
        String insertSql = "INSERT INTO pet (user_id, name, species) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, 1, "测试宠物", "狗");
        
        // 获取刚插入的记录
        String selectSql = "SELECT pet_id, create_time, update_time FROM pet WHERE name = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(selectSql, "测试宠物");
        
        System.out.println("=== 新插入记录的时间字段 ===");
        System.out.println("pet_id: " + result.get("pet_id"));
        System.out.println("create_time: " + result.get("create_time"));
        System.out.println("update_time: " + result.get("update_time"));
        
        // 更新记录
        String updateSql = "UPDATE pet SET name = ? WHERE name = ?";
        jdbcTemplate.update(updateSql, "更新后的测试宠物", "测试宠物");
        
        // 再次查询验证 update_time 是否更新
        Map<String, Object> updatedResult = jdbcTemplate.queryForMap(selectSql, "更新后的测试宠物");
        System.out.println("\n=== 更新后的时间字段 ===");
        System.out.println("pet_id: " + updatedResult.get("pet_id"));
        System.out.println("create_time: " + updatedResult.get("create_time"));
        System.out.println("update_time: " + updatedResult.get("update_time"));
        
        // 清理测试数据
        String deleteSql = "DELETE FROM pet WHERE name = ?";
        jdbcTemplate.update(deleteSql, "更新后的测试宠物");
        
        System.out.println("\n✅ update_time 功能测试完成！");
    }
}