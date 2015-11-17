package org.pegasia.plugins.inputmanager.prompt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;

import org.pegasia.plugins.inputmanager.InputConfig;
import org.pegasia.plugins.inputmanager.InputManager;
import org.pegasia.plugins.inputmanager.config.FrameBindException;
import org.pegasia.plugins.inputmanager.config.InputConfigPanel;

//Cite: http://stackoverflow.com/questions/12094268/stop-code-until-a-condition-is-met

public class InputPrompt extends JPanel implements ActionListener, FocusListener, KeyEventDispatcher, MouseListener {
	private final InputPromptListener listener;
	private final KeyboardFocusManager focusManager;
	private final RootPaneContainer root;
	private final Component oldGlassPane;
	private final JLabel errorLabel, counterLabel;
	private final Timer timer;

	private volatile int count;
	private volatile int type, code;

	public InputPrompt(String title, JPanel owner, InputPromptListener listener) throws FrameBindException {
		super();

		// Check immediately to make sure it can bind to the window
		Window window = SwingUtilities.getWindowAncestor(owner);
		if (window == null || !(window instanceof RootPaneContainer))
			throw new FrameBindException("Unable to bind to GlassPane.");

		// Initialize variables
		this.listener = listener;
		this.focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		this.root = (RootPaneContainer) window;
		this.oldGlassPane = root.getGlassPane();

		this.count = 10;
		this.type = InputConfig.TYPE_UNDEFINED;
		this.code = 0;

		// Create a new JPanel to hold the visual
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(new CompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(4,4,4,4)));
		panel.setMaximumSize(new Dimension(400, 300));
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		this.add(panel, gbc);

		// Create a label for the prompt's title
		if ( title != null && title.length() > 0 ) {
			JLabel titleLabel = new JLabel(title);
			titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(titleLabel);
		}

		// Create a label for any error messages
		errorLabel = new JLabel("Please input key stroke or mouse button here.");
		errorLabel.setFont(errorLabel.getFont().deriveFont(Font.BOLD));
		errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(errorLabel);

		// Create a label for the countdown timer
		counterLabel = new JLabel();
		updateCounter();
		counterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(counterLabel);

		// Initiate the timer
		timer = new Timer(1000, this);
		timer.start();

		// Add listeners
		focusManager.addKeyEventDispatcher(this);
		addMouseListener(this);
		addFocusListener(this);

		// Configure this JPanel to handle focus
		setFocusTraversalKeysEnabled(false);
		setFocusable(true);
		setOpaque(false);

		// Show the glass pane
		root.setGlassPane(this);
		requestFocus();
		setVisible(true);
		revalidate();
	}

	private void setInputCode(int type, int code) {
		if (InputManager.isWhitelisted(type, code)) {
			this.type = type;
			this.code = code;
			finish();
		} else {
			errorLabel.setText("Cannot bind to " + InputConfigPanel.getInputName(type, code) + (type==InputConfig.TYPE_KEY?" key.":"."));
			errorLabel.setForeground(Color.RED);
		}
	}

	private void updateCounter() {
		counterLabel.setText("Prompt will automatically close in: " + count);

		if ( count <= 0 )
			finish();
	}

	private void finish() {
		timer.stop();
		focusManager.removeKeyEventDispatcher(this);
		root.setGlassPane(oldGlassPane);
		oldGlassPane.setVisible(false);
		this.setEnabled(false);

		listener.receiveValue(type, code);
	}

	@Override public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == timer ) {
			count--;
			updateCounter();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (SwingUtilities.getWindowAncestor((Component)e.getSource()) == root) {
			if ( e.getKeyCode() != KeyEvent.VK_UNDEFINED )
				setInputCode(InputConfig.TYPE_KEY, e.getKeyCode());
			return true;
		}
		return false;
	}

	@Override public void mouseClicked(MouseEvent e) {
		setInputCode(InputConfig.TYPE_MOUSE, e.getButton());
	}

	@Override public void mousePressed(MouseEvent e) {
		setInputCode(InputConfig.TYPE_MOUSE, e.getButton());
	}

	@Override public void focusLost(FocusEvent e) {
		requestFocus();
	}

	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void focusGained(FocusEvent e) {}
}
