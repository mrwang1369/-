package dota.ui;

import dota.model.HeroGroup;

import javax.swing.*;
import java.awt.*;

/**
 * 用于在JList中美观地显示HeroGroup。
 */
public class HeroGroupRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof HeroGroup group) {
            setText(group.toString());
        }
        return this;
    }
}