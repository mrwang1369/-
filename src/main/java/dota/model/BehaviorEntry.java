package dota.model;

import java.util.Objects;

/**
 * 单条行为记录的数据模型。
 */
public class BehaviorEntry {
    public enum Identity {
        PROTAGONIST("主角"),
        TEAMMATE("队友"),
        ENEMY("敌方");

        private final String displayName;

        Identity(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private Identity identity;
    private String heroName;
    private String actionDescription;
    private String intentionDescription;

    public BehaviorEntry() {}

    public BehaviorEntry(Identity identity, String heroName, String actionDescription, String intentionDescription) {
        this.identity = identity;
        this.heroName = heroName;
        this.actionDescription = actionDescription;
        this.intentionDescription = intentionDescription;
    }

    // Getters and Setters
    public Identity getIdentity() { return identity; }
    public void setIdentity(Identity identity) { this.identity = identity; }

    public String getHeroName() { return heroName; }
    public void setHeroName(String heroName) { this.heroName = heroName; }

    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }

    public String getIntentionDescription() { return intentionDescription; }
    public void setIntentionDescription(String intentionDescription) { this.intentionDescription = intentionDescription; }

    /**
     * 返回格式化为 "[身份] [英雄名] [行为描述] [意图描述]" 的字符串。
     */
    @Override
    public String toString() {
        return identity + " " + heroName + " " + actionDescription + " " + intentionDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehaviorEntry that = (BehaviorEntry) o;
        return identity == that.identity &&
                Objects.equals(heroName, that.heroName) &&
                Objects.equals(actionDescription, that.actionDescription) &&
                Objects.equals(intentionDescription, that.intentionDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, heroName, actionDescription, intentionDescription);
    }
}