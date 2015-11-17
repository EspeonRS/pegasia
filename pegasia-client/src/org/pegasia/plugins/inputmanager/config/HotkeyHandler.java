package org.pegasia.plugins.inputmanager.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.pegasia.plugins.inputmanager.InputConfig;
import org.pegasia.plugins.inputmanager.prompt.InputPrompt;
import org.pegasia.plugins.inputmanager.prompt.InputPromptListener;

public class HotkeyHandler extends JPanel implements ActionListener, InputPromptListener {
	InputConfigPanel panel;
	InputConfig input;
	JLabel currentKey;
	int type, code;

	HotkeyHandler(InputConfigPanel panel, InputConfig input) {
		super(new BorderLayout());
		this.panel = panel;
		this.input = input;

		JLabel name = new JLabel(' ' + input.name);
		name.setPreferredSize(new Dimension(84, -1));
		this.add(name, BorderLayout.LINE_START);

		currentKey = new JLabel();
		currentKey.setHorizontalAlignment(JLabel.CENTER);
		update(input.type, input.code);
		this.add(currentKey, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		this.add(buttonPanel, BorderLayout.LINE_END);

		JButton button = new JButton("Change");
		button.setToolTipText("Redefines the hotkey for " + input.name + ".");
		button.addActionListener(this);
		buttonPanel.add(button);

		button = new JButton("Clear");
		button.setToolTipText("Clears the " + input.name + " hotkey.");
		button.addActionListener(this);
		buttonPanel.add(button);
	}

	public void update(int type, int code) {
		this.type = type;
		this.code = code;
		currentKey.setText(InputConfigPanel.getInputName(type, code));
	}

	void clear() {
		update(InputConfig.TYPE_UNDEFINED, 0);
	}

	void save() {
		input.type = type;
		input.code = code;
	}

	@Override public void receiveValue(int type, int code) {
		// If a key was returned, set it and call an update for all other keys
		if (type != InputConfig.TYPE_UNDEFINED) {
			update(type, code);
			panel.updateHandlers(this);
		}
	}

	@Override public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Change")) {
			try {
				// Prompt the user for a new key
				new InputPrompt("Enter " + input.name + " hotkey...", panel, this);
			} catch (FrameBindException ex) {
				ex.printStackTrace();
			}
		} else if (e.getActionCommand().equals("Clear"))
			// Clear the pre-existing option
			clear();
	}
}