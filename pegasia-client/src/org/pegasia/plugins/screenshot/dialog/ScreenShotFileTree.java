/*
 * This example is from the book "Java Foundation Classes in a Nutshell".
 * Written by David Flanagan. Copyright (c) 1999 by O'Reilly & Associates.  
 * You may distribute this source code for non-commercial purposes only.
 * You may study, modify, and use this example for any purpose, as long as
 * this notice is retained.  Note that this example is provided "as is",
 * WITHOUT WARRANTY of any kind either expressed or implied.
 * 
 * Cite: http://www.java2s.com/Code/Java/File-Input-Output/FileTreeDemo.htm
 * Cite: http://stackoverflow.com/questions/5125242/list-only-subdirectory-from-directory-not-files
 */

package org.pegasia.plugins.screenshot.dialog;

import java.awt.Component;
import java.io.File;
import java.net.URI;

import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.pegasia.plugins.screenshot.ScreenShotPlugin;


public class ScreenShotFileTree extends JTree implements TreeSelectionListener {
	private ScreenShotFrame window;
	private File currentDirectory;

	public ScreenShotFileTree(ScreenShotFrame window, File currentDirectory) {
		super();
		this.window = window;

		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setCellRenderer(new FileTreeCellRenderer());
		this.addTreeSelectionListener(this);

		// Set the current screenshot directory
		File directory = currentDirectory;
		if ( directory.exists() && directory.isDirectory()
				|| directory.mkdirs() )
			this.currentDirectory = directory;
		else
			this.currentDirectory = ScreenShotPlugin.SCREENSHOT_DIRECTORY;
		this.refresh();
	}

	public void refresh() {
		// Create the new tree model
		File topDirectory = ScreenShotPlugin.SCREENSHOT_DIRECTORY;
		TreeModel fileTreeModel = new FileTreeModel(topDirectory);
		this.setModel(fileTreeModel);

		// Attempt to open the current screenshot directory
		URI directory = currentDirectory.toURI();
		URI relative = ScreenShotPlugin.SCREENSHOT_DIRECTORY.toURI().relativize(directory);
		if (!directory.equals(relative)) {
			String folders[] = relative.getPath().split("/");
			FileItem item = (FileItem) fileTreeModel.getRoot();
			TreePath path = new TreePath(item);
			for (String folder: folders) {
				// Do not do anything if this was a blank folder name
				if (folder == "")
					continue;

				// Try to find the folder within the currently opened directory
				boolean found = false;
				for (int i = fileTreeModel.getChildCount(item)-1; i >= 0; i-- ) {
					FileItem child = (FileItem) fileTreeModel.getChild(item, i);
					if (child.file.getName().equalsIgnoreCase(folder)) {
						item = child;
						path = path.pathByAddingChild(child);
						found = true;
						break;
					}
				}

				// If the folder wasn't found in this directory, give up
				if (!found)
					break;
			}
			this.setSelectionPath(path);
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (e.getSource() == this) {
			FileItem node = (FileItem)this.getLastSelectedPathComponent();
			if (node == null)
				return;
			this.currentDirectory = node.file;
			window.setDirectory(node.file);
		}
	}


	static class FileTreeModel implements TreeModel {
		protected final FileItem root; // We specify the root directory when we create the model.

		public FileTreeModel(FileItem root) {
			super();
			this.root = root;
		}

		public FileTreeModel(File root) {
			this(new FileItem(root));
		}

		@Override
		/**
		 * Returns the root from the current node of the tree.
		 */
		public Object getRoot() {
			return root;
		}

		@Override
		/**
		 * Returns if the file represents a leaf in the tree.
		 */
		public boolean isLeaf(Object node) {
			return ((FileItem)node).file.isFile() || getChildCount((FileItem)node) == 0;
		}

		@Override
		/**
		 * Tells the tree how many children a node has
		 */
		public int getChildCount(Object parent) {
			String[] children = ((FileItem)parent).getChildren();

			if (children == null)
				return 0;
			return children.length;
		}

		/**
		 * Fetch any numbered child of a node for the JTree.
		 * Our model returns File objects for all nodes in the tree.  The
		 * JTree displays these by calling the File.toString() method.
		 */
		public Object getChild(Object parent, int index) {
			String[] children = ((FileItem)parent).getChildren();

			if ((children == null) || (index >= children.length))
				return null;
			return new FileItem(new File(((FileItem)parent).file, children[index]));
		}

		// Figure out a child's position in its parent node.
		public int getIndexOfChild(Object parent, Object child) {
			String[] children = ((FileItem)parent).getChildren();
			if (children == null)
				return -1;
			String childname = ((FileItem)child).toString();
			for(int i = 0; i < children.length; i++)
				if (childname.equals(children[i])) return i;
			return -1;
		}



		// This method is only invoked by the JTree for editable trees.  
		// This TreeModel does not allow editing, so we do not implement 
		// this method.  The JTree editable property is false by default.
		public void valueForPathChanged(TreePath path, Object newvalue) {}

		// Since this is not an editable tree model, we never fire any events,
		// so we don't actually have to keep track of interested listeners.
		public void addTreeModelListener(TreeModelListener l) {}
		public void removeTreeModelListener(TreeModelListener l) {}
	}

	static class FileTreeCellRenderer extends DefaultTreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			setLeafIcon(getDefaultClosedIcon());
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

			return this;
		}
	}
}
