package org.pegasia.client.runescape;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class RSGraphicsDebug extends JFrame {
	final JPanel panel;
	Image image;
	
	RSGraphicsDebug() {
		panel = new ImagePanel();
		JScrollPane pane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(pane);
		
		setResizable(true);
		setVisible(true);
	}
	
	void updateImage(RSGraphics graphics) {
		panel.setSize(graphics.surfaceSize.width, graphics.surfaceSize.height);
		image = graphics.surface;
		panel.repaint();
	}
	
	class ImagePanel extends JPanel {
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			g.drawImage(image, 0, 0, null);
		}
	}
}
