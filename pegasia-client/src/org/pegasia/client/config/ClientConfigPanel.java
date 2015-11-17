package org.pegasia.client.config;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.pegasia.api.PluginConfigPanel;
import org.pegasia.api.runescape.World;
import org.pegasia.api.runescape.WorldList;
import org.pegasia.client.Pegasia;

public class ClientConfigPanel extends PluginConfigPanel implements PopupMenuListener {
	final Pegasia client;
	final ClientProperties properties;

	GridBagConstraints constraints;
	JCheckBox useHomeworld, limitSize;

	DefaultComboBoxModel<WorldInt> homeworldModel;
	JComboBox<WorldInt> homeworldList;
	boolean worldListLoaded;
	
	ButtonGroup group;

	public ClientConfigPanel(Pegasia client) {
		super(new GridBagLayout());
		this.client = client;
		this.properties = client.getPegasiaProperties();

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(8,0,0,0);

		// RuneScape settings
		JPanel runescapePanel = new JPanel();
		runescapePanel.setLayout(new BoxLayout(runescapePanel, BoxLayout.PAGE_AXIS));
		runescapePanel.setBorder(BorderFactory.createTitledBorder("RuneScape"));
		addComponent(runescapePanel);
		
		limitSize = new JCheckBox("Keep client larger than fixed's size", null, properties.limitSize);
		limitSize.setToolTipText("Does not allow the client to become smaller than the size of the game in fixed mode.");
		runescapePanel.add(limitSize);

		useHomeworld = new JCheckBox("Set default world on startup", null, properties.useHomeworld);
		useHomeworld.setToolTipText("Set RuneScape's world to the selected world on startup.");
		runescapePanel.add(useHomeworld);
		
		homeworldModel = new DefaultComboBoxModel<WorldInt>();
		homeworldModel.addElement(new WorldInt(properties.homeworld));
		worldListLoaded = false;

		WorldRenderer renderer = new WorldRenderer();
		renderer.setPreferredSize(new Dimension(0, 16));

		homeworldList = new JComboBox<WorldInt>(homeworldModel);
		homeworldList.setEditable(true);
		homeworldList.setEnabled(useHomeworld.isSelected());
		homeworldList.setRenderer(renderer);
		homeworldList.addPopupMenuListener(this);
		homeworldList.setAlignmentX(Component.LEFT_ALIGNMENT);
		runescapePanel.add(homeworldList);
		
		useHomeworld.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				homeworldList.setEnabled(useHomeworld.isSelected());
			}
		});
		
		// Theme settings
		JPanel themePanel = new JPanel();
		themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.PAGE_AXIS));
		themePanel.setBorder(BorderFactory.createTitledBorder("Theme"));
		addComponent(themePanel);
		
		themePanel.add(new JLabel("<html>The client's theme can be changed using a<br />theme selection prompt upon restarting Pegasia."));
		
		JRadioButton hideButton = new JRadioButton("Do not show theme selection.");
		hideButton.setSelected(properties.themeSelection == ClientProperties.HIDE_THEME_SELECTION);
		hideButton.setActionCommand(Integer.toString(ClientProperties.HIDE_THEME_SELECTION));
		themePanel.add(hideButton);

		JRadioButton showOnceButton = new JRadioButton("Show the theme selection once.");
		showOnceButton.setSelected(properties.themeSelection == ClientProperties.SHOW_THEME_SELECTION_ONCE);
		showOnceButton.setActionCommand(Integer.toString(ClientProperties.SHOW_THEME_SELECTION_ONCE));
		themePanel.add(showOnceButton);
		
		JRadioButton showButton = new JRadioButton("Always show the theme selection.");
		showButton.setSelected(properties.themeSelection == ClientProperties.SHOW_THEME_SELECTION);
		showButton.setActionCommand(Integer.toString(ClientProperties.SHOW_THEME_SELECTION));
		themePanel.add(showButton);
		
		group = new ButtonGroup();
		group.add(hideButton);
		group.add(showOnceButton);
		group.add(showButton);
	}

	protected void addComponent(Component c) {
		constraints.gridy++;
		add(c, constraints);
	}

	@Override
	public void save() {
		properties.limitSize = limitSize.isSelected();
		properties.useHomeworld = useHomeworld.isSelected();
		properties.themeSelection = Integer.valueOf(group.getSelection().getActionCommand());
		
		Object selection = homeworldList.getSelectedItem();
		if (selection instanceof WorldInt)
			properties.homeworld = ((WorldInt) selection).value;
		else try {
			properties.homeworld = Integer.parseInt(selection.toString());
		} catch (NumberFormatException e) {}
		
		// File is saved by PLList
	}

	@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		if (!worldListLoaded) {
			int homeworld = ((WorldInt) homeworldModel.getSelectedItem()).value;
			homeworldModel.removeAllElements();

			for(World w: WorldList.getWorldList()) {
				WorldInt addedWorld = new WorldInt(w);
				homeworldModel.addElement(addedWorld);

				if (w.world == homeworld)
					homeworldModel.setSelectedItem(addedWorld);
			}

			worldListLoaded = true;
		}
	}

	@Override public void popupMenuCanceled(PopupMenuEvent e) {}
	@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
}
