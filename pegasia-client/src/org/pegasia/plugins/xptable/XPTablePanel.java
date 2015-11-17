package org.pegasia.plugins.xptable;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.pegasia.api.component.PegasiaPanel;
import org.pegasia.api.runescape.Skill;
import org.pegasia.util.FileUtils;

public class XPTablePanel extends PegasiaPanel implements ListSelectionListener {
	private static ImageIcon icon16, icon32;
	private static XPTablePanel instance = null;
	
	private JTable table;
	private JLabel text;
	
	public static void start() {
		if (instance == null)
			instance = new XPTablePanel();
		instance.install();
	}
	
	public static void stop() {
		instance.uninstall();
	}

	public XPTablePanel() {
		super("Experience Table", "xp-table-panel", PegasiaPanel.PANEL_RIGHT);
		setLayout(new BorderLayout());
		
		TableModel dataModel = new AbstractTableModel() {
			@Override public boolean isCellEditable(int row, int column) { return false; }
			@Override public int getColumnCount() { return 2; }
			@Override public int getRowCount() { return 99;}
			@Override public Object getValueAt(int row, int col) {
				if (col == 0)
					return new Integer(row+1);
				return new Integer(Skill.levelToExp(row+1));
			}
			@Override public String getColumnName(int col) {
				if (col == 0)
					return "Level";
				return "Experience";
			}
		};
		table = new JTable(dataModel);
		table.getSelectionModel().addListSelectionListener(this);
		
		JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(20);
		this.add(pane, BorderLayout.CENTER);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		TableColumn level = table.getColumnModel().getColumn(0);
		level.setMinWidth(64);
		level.setMaxWidth(64);
		level.setResizable(false);
		level.setCellRenderer(centerRenderer);
		
		TableColumn xp = table.getColumnModel().getColumn(1);
		xp.setMinWidth(80);
		xp.setResizable(false);
		xp.setCellRenderer(centerRenderer);

		text = new JLabel("XP Between Levels: 0");
		text.setAlignmentX(0.5f);
		JPanel textPanel = new JPanel();
		textPanel.add(text);
		this.add(textPanel, BorderLayout.PAGE_END);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == table.getSelectionModel()) {
			ListSelectionModel model = table.getSelectionModel();

			if (!model.isSelectionEmpty()) {
				int xp = Skill.levelToExp(model.getMaxSelectionIndex()+1)
						- Skill.levelToExp(model.getMinSelectionIndex()+1);
				text.setText("XP Between Levels: " + xp);
			}
		}
	}
	
	@Override
	public ImageIcon getIcon16() {
		if (icon16 == null)
			icon16 = new ImageIcon(FileUtils.getBufferedImage(getClass(), "resources/plugin/xptable/table16.png"));
		return icon16;
	}

	@Override
	public ImageIcon getIcon32() {
		if (icon32 == null)
			icon32 = new ImageIcon(FileUtils.getBufferedImage(getClass(), "resources/plugin/xptable/table32.png"));
		return icon32;
	}

}
