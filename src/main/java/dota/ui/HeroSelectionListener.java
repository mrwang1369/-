package dota.ui;

import dota.model.Hero;

/**
 * 英雄选择事件监听器接口
 * 用于处理从英雄槽位选择英雄的事件
 */
public interface HeroSelectionListener {
    /**
     * 当英雄被选中时调用
     * @param hero 被选中的英雄
     */
    void onHeroSelected(Hero hero);
}