package org.pegasia.util.ui.tabbedpane;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

class TabTransferable implements Transferable {
	private static final String NAME = "PegasiaPanel";
	private static final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
	private final Component tabbedPane;
	
	public TabTransferable(Component tabbedPane) {
		this.tabbedPane = tabbedPane;
	}
	
	@Override public Object getTransferData(DataFlavor flavor) {
		return tabbedPane;
	}
	
	@Override public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] f = new DataFlavor[1];
		f[0] = FLAVOR;
		return f;
	}
	
	@Override public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.getHumanPresentableName().equals(NAME);
	}
}