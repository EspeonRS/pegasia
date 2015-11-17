package org.pegasia.plugins.screenshot.dialog;
//Cite: http://stackoverflow.com/questions/12094268/stop-code-until-a-condition-is-met

/*package org.pegasia.client.screenshot;

import javax.swing.*;
import javax.swing.border.CompoundBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class FileOverrideDialog extends JDialog implements ActionListener, KeyListener, MouseListener {
	private volatile JLabel errorLabel;
	private volatile Timer timer;
	private volatile int counter;
	private volatile JLabel counterLabel;

	private volatile InputSetting code;
	private volatile boolean finished;

	public static InputSetting showInputCodeDialog(Window owner, String title) {
		FileOverrideDialog dialog = new FileOverrideDialog(owner, title);

		while (!dialog.isFinished()) synchronized (dialog) {
			try {
				dialog.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return dialog.getValue();
	}

	private FileOverrideDialog(Window owner, String title) {
		super(owner, title, Dialog.ModalityType.DOCUMENT_MODAL);
		this.setResizable(false);
		this.setUndecorated(true);
		this.code = InputSetting.NULL_INPUT;
		this.counter = 10;
		this.finished = false;

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(new CompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(4,4,4,4)));

		if ( title != null && title.length() > 0 ) {
			JLabel titleLabel = new JLabel(title);
			titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(titleLabel);
		}
		
		errorLabel = new JLabel("Please input key stroke or mouse button here.");
		errorLabel.setFont(errorLabel.getFont().deriveFont(Font.BOLD));
		errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(errorLabel);

		counterLabel = new JLabel();
		updateCounter();
		counterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(counterLabel);

		timer = new Timer(1000, this);
		timer.start();

		panel.setFocusable(true);
		panel.requestFocus();
		panel.setFocusTraversalKeysEnabled(false);
		panel.addKeyListener(this);
		panel.addMouseListener(this);

		this.add(panel);
		this.pack();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	private void updateCounter() {
		counterLabel.setText("Prompt will automatically close in: "+counter);

		if ( counter <= 0 )
			finish();
	}
	
	private void setInputCode(InputSetting code) {
		if (1 == 1) {//Hotkey.isWhitelisted(code)) {
			this.code = code;
			finish();
		} else {
			errorLabel.setText("Cannot bind to "+code.getName()+(code.type==InputSetting.TYPE_KEY?" key.":"."));
			errorLabel.setForeground(Color.RED);
			
			
			timer.restart();
			counter = 10;
			updateCounter();
		}
	}

	private synchronized void finish() {
		if (finished)
			return;
		finished = true;
		timer.stop();
		this.dispose();

		synchronized (this) {
			this.notifyAll();
		}
	}

	private InputSetting getValue() {
		return code;
	}

	public boolean isFinished() {
		return finished;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == timer ) {
			counter--;
			updateCounter();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ( e.getKeyCode() != KeyEvent.VK_UNDEFINED )
			setInputCode(new InputSetting(e.getKeyCode(), InputSetting.TYPE_KEY));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if ( e.getKeyCode() != KeyEvent.VK_UNDEFINED )
			setInputCode(new InputSetting(e.getKeyCode(), InputSetting.TYPE_KEY));
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if ( e.getKeyCode() != KeyEvent.VK_UNDEFINED )
			setInputCode(new InputSetting(e.getKeyCode(), InputSetting.TYPE_KEY));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		setInputCode(new InputSetting(e.getButton(), InputSetting.TYPE_MOUSE));
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		setInputCode(new InputSetting(e.getButton(), InputSetting.TYPE_MOUSE));
	}
	
	@Override public void mouseEntered(MouseEvent e) { }
	@Override public void mouseExited(MouseEvent e) { }
	@Override public void mouseReleased(MouseEvent e) { }
}
*/