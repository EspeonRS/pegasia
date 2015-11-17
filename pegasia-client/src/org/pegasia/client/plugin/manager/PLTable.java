package org.pegasia.client.plugin.manager;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

class PLTable extends JTable {
	static final int BOOL_COLUMN = 0, ENTRY_COLUMN = 1;

	final PluginDialog dialog;
	final Color inactive;

	PLTable(PLList model, PluginDialog dialog) {
		super(model);
		this.dialog = dialog;

		setShowGrid(false);
		setTableHeader(null);
		setFillsViewportHeight(true);

		// Configure check box column
		TableColumn checkBox = getColumnModel().getColumn(BOOL_COLUMN);
		checkBox.setMinWidth(16);
		checkBox.setMaxWidth(16);
		checkBox.setResizable(false);

		PDBooleanEditorRenderer booleanEditorRenderer = new PDBooleanEditorRenderer();
		checkBox.setCellEditor(booleanEditorRenderer);
		checkBox.setCellRenderer(booleanEditorRenderer);

		// Configure entry column
		TableColumn entryColumn = getColumnModel().getColumn(ENTRY_COLUMN);
		entryColumn.setResizable(false);

		entryColumn.setCellRenderer(new PDStringRenderer());

		// Configure the list selection model
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		inactive = getInactiveColor(getForeground());
	}

	private Color getInactiveColor(Color color) {
		float[] rgb = color.getColorComponents(null);
		System.out.println(rgb.length);
		boolean lighter = (rgb[0] + rgb[1] + rgb[2]) < 1.5f;

		for (int i = 0; i <3; i++) {
			if (lighter)
				rgb[i] = 1.0f - rgb[i];

			rgb[i] = 0.7f * rgb[i];

			if (lighter)
				rgb[i] = 1.0f - rgb[i];
		}

		return new Color(color.getColorSpace(), rgb, 1.0f);
	}

	@Override public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);

		if (e.getSource() == getSelectionModel())
			dialog.refresh();
	}

	class PDStringRenderer implements TableCellRenderer {
		final TableCellRenderer renderer;

		PDStringRenderer() {
			this.renderer = getDefaultRenderer(String.class);
		}

		@Override public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel l = (JLabel) renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column == PLTable.ENTRY_COLUMN) {
				PLListEntry entry = (PLListEntry) getModel().getValueAt(row, column);

				l.setToolTipText(entry.getToolTipText());

				if (!isSelected ) {
					if (entry.isActive())
						l.setForeground(getForeground());
					else
						l.setForeground(inactive);
				}
			}

			return l;
		}

	}

	class PDBooleanEditorRenderer implements TableCellEditor, TableCellRenderer {
		final Color clear = new Color(0,0,0,0);
		final TableCellEditor editor;
		final TableCellRenderer renderer;

		PDBooleanEditorRenderer() {
			this.editor = getDefaultEditor(Boolean.class);
			this.renderer = getDefaultRenderer(Boolean.class);
		}

		@Override public boolean shouldSelectCell(EventObject e) {
			return false;
		}

		@Override public void addCellEditorListener(CellEditorListener l) {
			editor.addCellEditorListener(l);
		}

		@Override public void cancelCellEditing() {
			editor.cancelCellEditing();
		}

		@Override public Object getCellEditorValue() {
			return editor.getCellEditorValue();
		}

		@Override public boolean isCellEditable(EventObject e) {
			return editor.isCellEditable(e);
		}

		@Override public void removeCellEditorListener(CellEditorListener l) {
			editor.removeCellEditorListener(l);
		}

		@Override public boolean stopCellEditing() {
			return editor.stopCellEditing();
		}

		@Override public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (row == 0)
				return null;

			Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBackground(clear);
			return c;
		}

		@Override public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			if (row == 0)
				return null;

			return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
	}
}
