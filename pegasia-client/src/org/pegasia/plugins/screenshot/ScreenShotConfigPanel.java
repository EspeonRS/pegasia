package org.pegasia.plugins.screenshot;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.pegasia.api.PluginConfigPanel;

public class ScreenShotConfigPanel extends PluginConfigPanel {
	final ScreenShotProperties properties;
	
	GridBagConstraints constraints;
	JCheckBox sound, hotkey;
	JComboBox<String> sortType;
	
	public ScreenShotConfigPanel(ScreenShotProperties properties) {
		super(new GridBagLayout());
		this.properties = properties;
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(8,0,0,0);
		
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		main.setBorder(BorderFactory.createTitledBorder("Home World"));
		this.addComponent(main);
		
		sound = new JCheckBox("Play shutter sound", null, properties.useSound);
		sound.setToolTipText("Play a shutter sound when taking a screen shot.");
		main.add(sound);
		
		hotkey = new JCheckBox("PrintScreen takes screen shot", null, properties.useHotkey);
		hotkey.setToolTipText("Takes a screen shot when the PrintScreen button is released.");
		main.add(hotkey);
		
		JLabel sortTypeLabel = new JLabel("Sort screen shots by:");
		sortTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		main.add(sortTypeLabel);
		
		String screenshotSortTypes[] = {
				"Year and Month",
				"Year Only",
				"None"
		};
		sortType = new JComboBox<String>(screenshotSortTypes);
		sortType.setSelectedIndex(properties.sortType);
		sortType.setAlignmentX(Component.LEFT_ALIGNMENT);
		main.add(sortType);
	}
	
	protected void addComponent(Component c) {
		constraints.gridy++;
		add(c, constraints);
	}
	
	@Override
	public void save() {
		properties.useSound = sound.isSelected();
		properties.useHotkey = hotkey.isSelected();
		properties.sortType = sortType.getSelectedIndex();
		properties.save();
	}
}
