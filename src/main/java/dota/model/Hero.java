package dota.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DOTA2 英雄数据模型，用于映射 JSON 文件中的英雄信息。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hero {
    @JsonProperty("name")
    private String name; // 英文名
    
    @JsonProperty("title")
    private String title; // 中文名
    
    @JsonProperty("skills")
    private List<Skill> skills; // 技能列表
    
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
    
    public List<Skill> getSkills() {
        return skills;
    }
    
    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
    
    @Override
    public String toString() {
        return title; // 在 UI 中显示中文名
    }
}