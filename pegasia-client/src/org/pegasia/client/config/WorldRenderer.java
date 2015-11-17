package org.pegasia.client.config;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.pegasia.api.runescape.World;

public class WorldRenderer extends JLabel implements ListCellRenderer<WorldInt> {
	public WorldRenderer() {
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}

	@Override public Component getListCellRendererComponent(JList<? extends WorldInt> list, WorldInt value, int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setFont(list.getFont());
		String str = value.toString();

		if (value.world != null) {
			if (value.world.dangerous)
				setForeground(Color.RED);
			if (!value.world.activity.equals("-"))
				str += " - " + value.world.activity;
			setIcon(value.world.members ? World.MEMBERS_ICON : World.FREE_ICON);
		}

		setText(str);
		setToolTipText(str);

		return this;
	}
}
