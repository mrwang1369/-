package com.pethealth.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {
    public static void main(String[] args) {
        // 数据库配置
        String url = "jdbc:mysql://localhost:3306/bishe?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
        String username = "root";
        String password = "123456";

        // 获取所有表名
        List<String> tableNames = getAllTableNames(url, username, password);

        // 生成代码
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("Mr wang") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 输出目录
                            .disableOpenDir(); // 不打开输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.pethealth") // 父包名
                            .moduleName("") // 模块名（可为空）
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // Mapper XML 路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tableNames.toArray(new String[0])) // 添加需要生成的表名
                            .entityBuilder()
                            .enableLombok() // 使用 Lombok
                            .logicDeleteColumnName("deleted") // 逻辑删除字段
                            .naming(NamingStrategy.underline_to_camel) // 数据库表名映射到实体类名
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .idType(com.baomidou.mybatisplus.annotation.IdType.AUTO) // 主键类型
                            .controllerBuilder()
                            .enableRestStyle() // 使用 REST 风格
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // Service 类名后缀
                            .formatServiceImplFileName("%sServiceImpl"); // ServiceImpl 类名后缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute();
    }

    /**
     * 获取数据库中所有表名
     */
    private static List<String> getAllTableNames(String url, String username, String password) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(url, username, password).build();
        List<String> tableNames = new ArrayList<>();
        try {
            ResultSet tables = dataSourceConfig.getConn().getMetaData().getTables(null, null, null, new String[]{"TABLE"});
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
            tables.close();
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库表名失败", e);
        }
        return tableNames;
    }
}
