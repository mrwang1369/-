package dota.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DOTA2 技能数据模型，用于映射 JSON 文件中的技能信息。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Skill {
    @JsonProperty("name")
    private String name;        // 技能名称
    
    @JsonProperty("hotkey")
    private String hotkey;      // 快捷键（Q/W/E/R/被动/神杖技能等）
    
    @JsonProperty("full_text")
    private String full_text;   // 技能完整描述
    
    public Skill() {}
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getHotkey() {
        return hotkey;
    }
    
    public void setHotkey(String hotkey) {
        this.hotkey = hotkey;
    }
    
    public String getFull_text() {
        return full_text;
    }
    
    public void setFull_text(String full_text) {
        this.full_text = full_text;
    }
}
