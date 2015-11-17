package org.pegasia.plugins.inputmanager.config;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.pegasia.api.PluginConfigPanel;
import org.pegasia.plugins.inputmanager.InputConfig;
import org.pegasia.plugins.inputmanager.InputProperties;

public class InputConfigPanel extends PluginConfigPanel implements ActionListener {
	private final InputProperties properties;
	
	private ArrayList<HotkeyHandler> hotkeyHandlers;
	private JPanel panel;

	public InputConfigPanel(InputProperties properties) {
		super(new BorderLayout());
		this.properties = properties;

		// Create a new panel for the hotkeys
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		// Add the panel into a scrolling pane
		JScrollPane pane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(20);
		this.add(pane, BorderLayout.CENTER);

		// Populate the panel with hotkey settings
		hotkeyHandlers = new ArrayList<HotkeyHandler>();
		for (InputConfig input: InputConfig.values()) {
			HotkeyHandler handler = new HotkeyHandler(this, input);
			hotkeyHandlers.add(handler);
			panel.add(handler, constraints);
			constraints.gridy++;
		}

		// Add the default settings buttons
		JPanel bottomPanel = new JPanel();
		this.add(bottomPanel, BorderLayout.PAGE_END);

		JButton button = new JButton("OSRS Default");
		button.setToolTipText("Sets all hotkeys to the Old School RuneScape default.");
		button.addActionListener(this);
		bottomPanel.add(button);

		button = new JButton("RSHD Default");
		button.setToolTipText("Sets all hotkeys to the RuneScape HD default.");
		button.addActionListener(this);
		bottomPanel.add(button);

		button = new JButton("Clear All");
		button.setToolTipText("Clears all hotkeys.");
		button.addActionListener(this);
		bottomPanel.add(button);
	}

	@Override public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OSRS Default")) {
			// Set all hotkeys to their default key
			for (HotkeyHandler handler: hotkeyHandlers)
				handler.update(InputConfig.TYPE_KEY, handler.input.osrsDefaultCode);
		} else if (e.getActionCommand().equals("RSHD Default")) {
			// Set all hotkeys to their default modern key, using null if undefined
			for (HotkeyHandler handler: hotkeyHandlers) {
				if (handler.input.preeocDefaultCode == -1)
					handler.clear();
				else
					handler.update(InputConfig.TYPE_KEY, handler.input.preeocDefaultCode);
			}
		} else if (e.getActionCommand().equals("Clear All")) {
			// Set all hotkeys to null
			for (HotkeyHandler handler: hotkeyHandlers)
				handler.clear();
		}
	}

	@Override public void save() {
		for (HotkeyHandler handler: hotkeyHandlers)
			handler.save();

		properties.save();
	}

	public void updateHandlers(HotkeyHandler source) {
		for (HotkeyHandler handler: hotkeyHandlers)
			if (handler != source &&
			handler.type == source.type &&
			handler.code == source.code)
				handler.clear();
	}

	public static String getInputName(int type, int code) {
		String text = "Undefined";
		if ( type == InputConfig.TYPE_KEY )
			text = KeyEvent.getKeyText(code);
		else if ( type == InputConfig.TYPE_MOUSE )
			switch (code) {
			case MouseEvent.BUTTON1: text = "Mouse Left"; break;
			case MouseEvent.BUTTON2: text = "Mouse Middle"; break;
			case MouseEvent.BUTTON3: text = "Mouse Right"; break;
			default: text = "Mouse Button " + code;
			}
		return text;
	}
}
