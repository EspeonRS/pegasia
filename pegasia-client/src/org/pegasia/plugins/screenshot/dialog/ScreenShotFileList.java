package org.pegasia.plugins.screenshot.dialog;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class ScreenShotFileList extends JList<FileItem> implements ListSelectionListener, MouseListener {
	private ScreenShotFrame window;
	private ScreenShotFileListPopupMenu menu;
	private File directory;

	public ScreenShotFileList(ScreenShotFrame window) {
		super(new DefaultListModel<FileItem>());
		this.window = window;

		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.addListSelectionListener(this);
		this.addMouseListener(this);
		menu = new ScreenShotFileListPopupMenu(this);
		
		getActionMap().put("copy", new AbstractAction("copy"){
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		});
	}

	public File getDirectory() {
		return directory;
	}

	public void changeDirectory(File dir) {
		if (dir.exists() && dir.isDirectory()) {
			File files[] = dir.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File current, String name) {
					return name.endsWith(".png");
				}
			});
			Arrays.sort(files);
			
			DefaultListModel<FileItem> model = (DefaultListModel<FileItem>) this.getModel();
			model.clear();
			for (File file: files)
				model.addElement(new FileItem(file));
			
			this.directory = dir;
			this.setSelectedIndex(this.getLastVisibleIndex());
		}
	}
	
	public void copy() {
		final Transferable transferable = new Transferable() {
			public boolean isDataFlavorSupported(final DataFlavor flavor) {
				return ( flavor.equals(DataFlavor.imageFlavor) ||
						flavor.equals(DataFlavor.javaFileListFlavor) );
			}
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor, DataFlavor.javaFileListFlavor };
			}
			public Object getTransferData(final DataFlavor flavor) {
				if (flavor.equals(DataFlavor.imageFlavor)) {
					if (getSelectedIndices().length == 1 )
						return Toolkit.getDefaultToolkit().createImage(getSelectedValue().file.getAbsolutePath());
					return null;
				}
				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					final List<String> fileList = new ArrayList<>();
					for (FileItem item: getSelectedValuesList())
						fileList.add(item.file.getAbsolutePath());
					return fileList;
				}
				return null;
			}
		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
	}
	
	@Override
	public void setModel(ListModel<FileItem> model) {
		super.setModel(model);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == this) {
			FileItem node = getSelectedValue();
			if (node != null) {
				File file = node.file;
				BufferedImage image = null;
				InputStream is = getClass().getClassLoader().getResourceAsStream(file.getAbsolutePath());
				if (is == null) {
					try {
						image = ImageIO.read(file);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					try {
						image = ImageIO.read(is);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				window.setImage(image);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == this && SwingUtilities.isRightMouseButton(e)) {
			System.out.println("2");
			int index = locationToIndex(e.getPoint());

			if (!isSelectedIndex(index))
				setSelectedIndex(index);

			menu.show(this, e.getX(), e.getY());
		}
	}

	@Override public void mouseClicked(MouseEvent arg0) {}
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseExited(MouseEvent arg0) {}
	@Override public void mousePressed(MouseEvent arg0) {}
	
	static class FileListModel implements ListModel<FileItem> {
		private final ArrayList<ListDataListener> listeners;
		private final FileItem elements[];
		
		public FileListModel(File files[], ListDataListener listener) {
			elements = new FileItem[files.length];
			listeners = new ArrayList<ListDataListener>();
			
			for (int i = 0; i < files.length; i++)
				elements[i] = new FileItem(files[i]);
			listeners.add(listener);
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			if (!listeners.contains(l))
				listeners.add(l);
		}

		@Override
		public FileItem getElementAt(int index) {
			return elements[index];
		}

		@Override
		public int getSize() {
			return elements.length;
		}

		
		@Override
		public void removeListDataListener(ListDataListener l) {
			if (listeners.contains(l))
				listeners.remove(l);
		}
		
	}
}
