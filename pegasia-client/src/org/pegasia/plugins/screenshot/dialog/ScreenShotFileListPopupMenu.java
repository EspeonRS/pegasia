//Cite: http://stackoverflow.com/questions/14082770/how-to-copy-paste-and-cut-paste-file-or-folder-in-java
//Cite: http://www.rgagnon.com/javadetails/java-0370.html

package org.pegasia.plugins.screenshot.dialog;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public final class ScreenShotFileListPopupMenu extends JPopupMenu implements ActionListener {
	private ScreenShotFileList list;
	private Transferable pasteData;

	private JMenuItem edit, move, copy, paste, delete, rename;
	private JSeparator sep1, sep2;

	private JFileChooser chooser;

	public ScreenShotFileListPopupMenu(ScreenShotFileList list) {
		this.list = list;

		edit = new JMenuItem("Edit");
		edit.addActionListener(this);
		this.add(edit);

		sep1 = new JSeparator();
		this.add(sep1);

		move = new JMenuItem("Move");
		move.addActionListener(this);
		this.add(move);
		copy = new JMenuItem("Copy");
		copy.addActionListener(this);
		this.add(copy);
		paste = new JMenuItem("Paste");
		paste.addActionListener(this);
		this.add(paste);

		sep2 = new JSeparator();
		this.add(sep2);

		delete = new JMenuItem("Delete");
		delete.addActionListener(this);
		this.add(delete);
		rename = new JMenuItem("Rename");
		rename.addActionListener(this);
		this.add(rename);
	}

	private void update() {
		pasteData = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (pasteData == null || !pasteData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			paste.setEnabled(false);
		else
			paste.setEnabled(true);

		int values[] = list.getSelectedIndices();
		boolean active = values != null && values.length > 0;
		move.setVisible(active);
		copy.setVisible(active);
		delete.setVisible(active);
		rename.setVisible(active);
	}

	@Override
	public void show(Component invoker, int x, int y) {
		this.update();
		super.show(invoker, x, y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Move":
			chooser = new JFileChooser(); 
			chooser.setCurrentDirectory(list.getDirectory());
			chooser.setDialogTitle("Select Destination");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
				System.out.println("getCurrentDirectory(): " 
						+  chooser.getCurrentDirectory());
				System.out.println("getSelectedFile() : " 
						+  chooser.getSelectedFile());
			}
			else {
				System.out.println("No Selection ");
			}
			break;

		case "Copy": list.copy(); break;

		case "Paste":
			/*Transferable data = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			if (data != null) {
				if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					try {
						List<String> fileList = (List<String>)data.getTransferData(DataFlavor.javaFileListFlavor);*/
						/* 0 - Do not continue
						 * 1 - Always override
						 * 2 - Always rename
						 * 3 - Ignore
						 * 4 - Override
						 * 5 - Rename */
						/*int decision = 0;
						
						for (String str: fileList) {
							File source = new File(str);
							File dest = new File(list.getDirectory(), source.getName());
							
							if (decision < 0) {
								
							}
							
							ExternalUtil.copyFile(source, dest);
						}
					} catch (UnsupportedFlavorException | IOException ex) {
						ex.printStackTrace();
					}
				} else if (data.isDataFlavorSupported(DataFlavor.imageFlavor)) {
					
				}
			}
			//	for (DataFlavor flavor: data.getTransferDataFlavors())
			//		System.out.println(flavor.getHumanPresentableName()+": "+flavor.getDefaultRepresentationClassAsString());*/
			break;

		case "Delete":

			break;

		case "Rename":

			break;
		}
	}
	
	
}
