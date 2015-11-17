package org.pegasia.api.component;


public abstract class PegasiaCloseablePanel extends PegasiaPanel {

	public PegasiaCloseablePanel(String name, String id, int defaultPosition) {
		super(name, id, defaultPosition);
	}

	public void close() {
		
	}
}
