package org.pegasia.plugins.screenshot.dialog;

import java.io.File;
import java.io.FilenameFilter;

public class FileItem {
	public final File file;
	
	public FileItem(File file) {
		this.file = file;
	}
	
	public String[] getChildren() {
		return file.list(new FilenameFilter(){
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
	}
	
	@Override
	public String toString() {
		return file.getName();
	}
}
