package org.pegasia.client.plugin.manager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.pegasia.client.Pegasia;
import org.pegasia.util.FileUtils;

public final class PluginDialog extends JDialog implements ActionListener {
	private static final String NULL_PANEL = "NULL";
	private static BufferedImage icon;
	private static PluginDialog instance = null;	

	private final PLList list;
	private final PLTable table;
	private final JPanel centerPanel;
	private final JLabel textLabel;

	public static void openDialog(Pegasia client) {
		
		
		SwingUtilities.invokeLater( new Runnable() {

			@Override
			public void run() {
				if (PluginDialog.instance == null)
					PluginDialog.instance = new PluginDialog(client, client.getUIManager().getFrame());
				else
					PluginDialog.instance.toFront();
			}

		});
	}

	private PluginDialog(Pegasia client, Window window) {
		super(window, "Plugin Management", Dialog.ModalityType.MODELESS);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		this.setSize(500, 400);

		if (icon == null)
			icon = FileUtils.getBufferedImage("resources/config-icon.png");
		this.setIconImage(icon);

		// Create a separate list of plugins for this dialog to use
		list = new PLList(client, this);

		// Initialize the center panel
		CardLayout cardLayout = new CardLayout();
		centerPanel = new JPanel(cardLayout);
		this.add(centerPanel, BorderLayout.CENTER);

		// Initialize the text label
		JPanel nullPanel = new JPanel(new BorderLayout());
		textLabel = new JLabel("", JLabel.CENTER);
		nullPanel.add(textLabel, BorderLayout.CENTER);
		centerPanel.add(nullPanel, NULL_PANEL);

		// Initialize the left panel
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(140, 0));
		leftPanel.setBorder(new EmptyBorder(4, 4, 4, 4) );
		this.add(leftPanel, BorderLayout.LINE_START);

		// Add the "Plugin:" label to the left panel
		leftPanel.add(new JLabel("Plugins:"), BorderLayout.PAGE_START);

		// Add the table of plugins to the left panel
		table = new PLTable(list, this);
		JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(20);
		leftPanel.add(pane, BorderLayout.CENTER);

		// Set the default selected value for the table
		if (table.getRowCount() > 0)
			table.setRowSelectionInterval(0, 0);

		// Initialize the bottom panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setPreferredSize(new Dimension(0, 32));
		this.add(buttonPanel, BorderLayout.PAGE_END);

		// Create the cancel button
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		// Create the OK button
		JButton ok = new JButton("OK");
		ok.addActionListener(this);
		ok.setPreferredSize(cancel.getPreferredSize());

		// Add the save and cancel buttons to the bottom panel
		buttonPanel.add(ok);
		buttonPanel.add(cancel);

		// Make the dialog visible
		this.setLocationRelativeTo(window);
		this.setVisible(true);
	}

	void refresh() {
		table.repaint();

		int index = table.getSelectionModel().getLeadSelectionIndex();
		if (index != -1) {
			PLListEntry entry = (PLListEntry) list.getValueAt(index, PLTable.ENTRY_COLUMN);

			CardLayout cardLayout = (CardLayout) centerPanel.getLayout();
			Component c = entry.isActive() ? entry.getPanel() : null;

			String card;
			if (c != null) {
				card = c.getClass().getName();
				centerPanel.add(c, card);
			} else {
				card = NULL_PANEL;
				textLabel.setText(list.getPluginStatus(entry));
			}

			cardLayout.show(centerPanel, card);
		}
	}

	@Override public void dispose() {
		instance = null;
		super.dispose();
	}

	@Override public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Cancel")) {
			dispose();
		} else if (e.getActionCommand().equals("OK")) {
			list.applyChanges();
			dispose();
		}
	}
}
