package org.pegasia.plugins.xpdrops;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.pegasia.api.PluginConfigPanel;
import org.pegasia.util.ui.LimitedJTextField;

public class XPDropsConfigPanel extends PluginConfigPanel implements ActionListener {
	final XPDropsProperties properties;;

	GridBagConstraints constraints;
	LimitedJTextField durationField, thresholdField;
	JCheckBox check;

	public XPDropsConfigPanel(XPDropsProperties properties) {
		super(new GridBagLayout());
		this.properties = properties;

		Dimension d = getPreferredSize();
		d.width = 200;
		setPreferredSize(d);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(8,0,0,0);

		int threshold = properties.threshold;

		// Initialize all of the GUI elements
		JPanel durationPanel = new JPanel();
		durationField = new LimitedJTextField(4, true);
		durationField.setColumns(4);
		durationField.setText(Integer.toString(properties.duration));
		durationPanel.add(durationField);
		durationPanel.add(new JLabel(" seconds."));

		check = new JCheckBox("Highlight large XP drops");
		check.setAlignmentX(Component.LEFT_ALIGNMENT);
		check.addActionListener(this);
		check.setSelected(threshold != -1);

		JPanel thresholdPanel = new JPanel();
		thresholdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		thresholdField = new LimitedJTextField(5, true);
		thresholdField.setColumns(3);
		if (threshold == -1)
			thresholdField.setEnabled(false);
		else
			thresholdField.setText(Integer.toString(threshold));
		thresholdPanel.add(thresholdField);
		thresholdPanel.add(new JLabel(" XP or higher."));

		// Build the panel
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		main.setBorder(BorderFactory.createTitledBorder("Drop Duration"));
		main.add(durationPanel);
		this.addComponent(main);

		JPanel highlights = new JPanel();
		highlights.setLayout(new BoxLayout(highlights, BoxLayout.PAGE_AXIS));
		highlights.setBorder(BorderFactory.createTitledBorder("Highlights"));
		highlights.add(check);
		highlights.add(thresholdPanel);
		this.addComponent(highlights);
	}

	protected void addComponent(Component c) {
		constraints.gridy++;
		add(c, constraints);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == check)
			thresholdField.setEnabled(check.isSelected());
	}

	@Override
	public void save() {
		try {
			properties.duration = Integer.valueOf(durationField.getText());
			properties.threshold = check.isSelected()? Integer.valueOf(thresholdField.getText()) : -1;
		} catch (NumberFormatException e) {}

		try {
			properties.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
