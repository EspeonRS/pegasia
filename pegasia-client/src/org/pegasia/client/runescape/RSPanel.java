package org.pegasia.client.runescape;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class RSPanel extends JPanel implements LayoutManager{
	public static final Dimension SMALLEST_MINIMUM_SIZE = new Dimension(280, 280),
			STANDARD_MINIMUM_SIZE = new Dimension(765, 503);

	public RSPanel() {
		super();
		setLayout(this);
		setMinimumSize(SMALLEST_MINIMUM_SIZE);
		
		setBackground(Color.BLACK);
		setOpaque(true);
	}

	@Override
	public Component add(Component comp) {
		removeAll();
		super.add(comp);
		revalidate();
		return comp;
	}

	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			if (parent.getComponentCount() > 0) {
				Insets insets = parent.getInsets();
				Dimension minimum = getMinimumSize();
				
				parent.getComponent(0).setBounds(
						insets.left,
						insets.top,
						Math.max(parent.getWidth(), minimum.width) - insets.left - insets.right,
						Math.max(parent.getHeight(), minimum.height) - insets.top - insets.bottom);
			}
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return getMinimumSize();
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return STANDARD_MINIMUM_SIZE;
	}

	@Override public void addLayoutComponent(String name, Component comp) {}
	@Override public void removeLayoutComponent(Component comp) {}
}
