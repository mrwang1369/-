package dota.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 保存到常用库的模板数据模型。
 */
public class ActionTemplate {
    private String id; // 可以是UUID或简单字符串
    private String heroName;
    private String actionDescription;
    private String intentionDescription;
    private List<String> tags;

    public ActionTemplate() {
        this.tags = new ArrayList<>();
    }

    public ActionTemplate(String id, String heroName, String actionDescription, String intentionDescription, List<String> tags) {
        this.id = id;
        this.heroName = heroName;
        this.actionDescription = actionDescription;
        this.intentionDescription = intentionDescription;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHeroName() { return heroName; }
    public void setHeroName(String heroName) { this.heroName = heroName; }

    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }

    public String getIntentionDescription() { return intentionDescription; }
    public void setIntentionDescription(String intentionDescription) { this.intentionDescription = intentionDescription; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    /**
     * 添加一个标签。
     */
    public void addTag(String tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionTemplate that = (ActionTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}