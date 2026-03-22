package dota.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表示一组英雄信息的容器，最多可容纳十个英雄。
 */
public class HeroGroup {
    private static final int MAX_HEROES = 10;
    private final List<String> heroNames;

    public HeroGroup() {
        this.heroNames = new ArrayList<>();
    }

    /**
     * 添加一个英雄名称。
     * @param name 英雄名称
     * @return 如果成功添加则返回true，如果已达到最大数量则返回false。
     */
    public boolean addHero(String name) {
        if (heroNames.size() >= MAX_HEROES || name == null || name.trim().isEmpty()) {
            return false;
        }
        return heroNames.add(name.trim());
    }

    /**
     * 移除一个英雄名称。
     * @param name 要移除的英雄名称
     * @return 如果成功移除则返回true。
     */
    public boolean removeHero(String name) {
        return heroNames.remove(name);
    }

    /**
     * 获取当前组内的所有英雄名称。
     * @return 不可变的英雄名称列表。
     */
    public List<String> getHeroes() {
        return List.copyOf(heroNames);
    }

    /**
     * 获取当前组内英雄的数量。
     * @return 数量。
     */
    public int size() {
        return heroNames.size();
    }

    /**
     * 检查该组是否已满。
     * @return 如果已满则为true。
     */
    public boolean isFull() {
        return heroNames.size() >= MAX_HEROES;
    }

    /**
     * 清空所有英雄。
     */
    public void clear() {
        heroNames.clear();
    }

    @Override
    public String toString() {
        return "[" + String.join(", ", heroNames) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeroGroup heroGroup = (HeroGroup) o;
        return Objects.equals(heroNames, heroGroup.heroNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heroNames);
    }
}