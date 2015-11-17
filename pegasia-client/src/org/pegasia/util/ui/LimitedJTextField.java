package org.pegasia.util.ui;

import javax.swing.JTextField;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

//SRC: http://stackoverflow.com/questions/3519151/how-to-limit-the-number-of-characters-in-jtextfield

public class LimitedJTextField extends JTextField {
	private int limit;
	private boolean numeric;
	
	public LimitedJTextField(int limit, boolean numeric) {
		super();
		this.limit = limit;
		this.numeric = numeric;
	}

	public LimitedJTextField(int limit) {
		this(limit, false);
	}
	
	public LimitedJTextField(boolean numeric) {
		this(-1, numeric);
	}

	@Override protected Document createDefaultModel() {
		return new LimitDocument();
	}

	private class LimitDocument extends PlainDocument {
		@Override public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
			if (str == null) return;
			
			if (numeric)
				str = str.replaceAll("[^\\d]", "");

			if (limit < 0 || (getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}       
	}
}