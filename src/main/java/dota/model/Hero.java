package dota.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DOTA2英雄数据模型，用于映射JSON文件中的英雄信息。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hero {
    @JsonProperty("name")
    private String name; // 英文名
    
    @JsonProperty("title")
    private String title; // 中文名
    
    public Hero() {}
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String toString() {
        return title; // 在UI中显示中文名
    }
}