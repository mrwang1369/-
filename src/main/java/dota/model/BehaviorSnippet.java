package dota.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个视频片段的所有标注数据模型。
 */
public class BehaviorSnippet {
    private String bvid;
    private String startTime;
    private String endTime;
    private List<HeroGroup> heroGroups;

    public BehaviorSnippet() {
        this.heroGroups = new ArrayList<>();
    }

    public BehaviorSnippet(String bvid, String startTime, String endTime) {
        this.bvid = bvid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.heroGroups = new ArrayList<>();
    }

    // Getters and Setters
    public String getBvid() { return bvid; }
    public void setBvid(String bvid) { this.bvid = bvid; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public List<HeroGroup> getHeroGroups() { return heroGroups; }
    public void setHeroGroups(List<HeroGroup> heroGroups) { this.heroGroups = heroGroups; }

    /**
     * 添加一个英雄组。
     */
    public void addHeroGroup(HeroGroup group) {
        this.heroGroups.add(group);
    }

    /**
     * 移除一条行为记录。
     */

}