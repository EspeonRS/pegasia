package org.pegasia.client.ui.skin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.EventObject;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

final class ThemeSelector {
	static final Dimension SIZE = new Dimension(320, 480);
	
	static void showThemeSelection(Collection<ThemeEntry> lookAndFeels) {
		ThemeSelector selector = new ThemeSelector(lookAndFeels);

		while (!selector.finished) synchronized (selector) {
			try {
				selector.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	final DefaultListModel<ThemeEntry> model;
	volatile boolean tryDecoration, finished;
	protected ThemeSelectionFrame frame;

	protected ThemeSelector(Collection<ThemeEntry> lookAndFeels) {
		this.model = new DefaultListModel<ThemeEntry>();

		int selectedIndex = 0, i = 0;
		for (ThemeEntry laf: lookAndFeels) {
			model.addElement(laf);
			if (laf.getClassName().equals(UIManager.getLookAndFeel().getClass().getName()))
				selectedIndex = i;
			i++;
		}

		tryDecoration = JFrame.isDefaultLookAndFeelDecorated();
		finished = false;

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		recreateWindow(dim.width/2 - SIZE.width/2, dim.height/2 - SIZE.height/2, selectedIndex, null);
	}

	protected void recreateWindow(final int x, final int y, final int selectedIndex, final Rectangle listView) {
		SwingUtilities.invokeLater ( new Runnable() {
			public void run() {
				synchronized (ThemeSelector.this) {
					frame = new ThemeSelectionFrame(selectedIndex, listView);
					frame.setLocation(x, y);
					frame.setVisible(true);
					frame.requestFocus();
				}
			}
		} );
	}

	protected synchronized void finish() {
		finished = true;

		synchronized (this) {
			this.notifyAll();
		}
	}

	class ThemeSelectionFrame extends JFrame implements ActionListener, ListSelectionListener {
		private JList<ThemeEntry> list;
		private JCheckBox checkBox;

		ThemeSelectionFrame(int selectedIndex, Rectangle listView) {
			super("Theme Selection");
			this.getContentPane().setLayout(new BorderLayout());
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

			list = new JList<ThemeEntry>();
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setFixedCellHeight(16);
			list.setModel(model);
			list.setSelectedIndex(selectedIndex);
			list.addListSelectionListener(this);
			
			if (listView != null)
				list.scrollRectToVisible(listView);

			checkBox = new JCheckBox("Use theme's window decoration");
			if (!UIManager.getLookAndFeel().getSupportsWindowDecorations())
				checkBox.setEnabled(false);
			checkBox.setSelected(tryDecoration);
			checkBox.addActionListener(this);

			JButton button = new JButton("Use Theme");
			button.addActionListener(this);

			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
			bottomPanel.add(checkBox);
			bottomPanel.add(button);

			this.add(new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
			this.add(bottomPanel, BorderLayout.PAGE_END);
			this.setResizable(false);
			this.setSize(SIZE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == checkBox)
				processChangeEvent(e);
			else
				processCloseEvent(e);
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			processChangeEvent(e);
		}

		@Override
		protected void processWindowEvent(WindowEvent e) {
			if (e.getID() == WindowEvent.WINDOW_CLOSING)
				processCloseEvent(e);
			else
				super.processWindowEvent(e);
		}

		void processChangeEvent(EventObject e) {
			tryDecoration = checkBox.isSelected();

			// Attempt to get an instance of the LookAndFeel that is being switch to,
			// cancelling the event if one could not be created.
			ThemeEntry theme = list.getSelectedValue();

			// Create a flag for whether the window needs to be recreated at the end
			boolean recreate = false;
			
			// If the use of LaF decoration is going to change, always dispose and
			// recreate the selection window.
			if (tryDecoration != JFrame.isDefaultLookAndFeelDecorated()) {
				dispose();
				recreate = true;
			}

			// Attempt to change the LaF without recreating the window
			try {
				theme.apply(tryDecoration);
			} catch (Exception ex) {
				// If unsuccessful, the window must be recreated, so dispose of
				// the window if such hasn't already been done.
				if (!recreate) {
					dispose();
					recreate = true;
				}

				// Attempt to change the LookAndFeel again
				try {
					theme.apply(tryDecoration);
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			}

			// Finish by either recreating the window if necessary, or simply
			// updating the component tree.
			if (recreate)
				recreateWindow(getX(), getY(), list.getSelectedIndex(), list.getVisibleRect());
			else {
				SwingUtilities.updateComponentTreeUI(this);
				checkBox.setEnabled(UIManager.getLookAndFeel().getSupportsWindowDecorations());
			}
		}

		void processCloseEvent(EventObject e) {
			dispose();
			finish();
		}
	}
}
