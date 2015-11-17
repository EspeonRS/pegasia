package org.pegasia.plugins.xpdrops;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.pegasia.api.component.PegasiaPanel;
import org.pegasia.util.FileUtils;

public class XPDropsPanel extends PegasiaPanel {
	private static final Font font = new Font("Helvetica", 1, 18);
	private static ImageIcon icon16, icon32;

	private final XPDropsProperties properties;	
	private final ArrayList<Drop> drops, toRemove;
	private final JButton button;
	
	private int totalAmount, offset;
	private String totalAmountText;

	public XPDropsPanel(XPDropsProperties properties) {
		super("XP Drops", "xp-drops-panel", PegasiaPanel.PANEL_RIGHT);
		this.properties = properties;

		drops = new ArrayList<Drop>();
		toRemove = new ArrayList<Drop>();
		button = new JButton();
		button.setText("0");
		
		offset = 0;
		setTotalAmount(0);
		
		setLayout(new BorderLayout());
		JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		subPanel.setOpaque(false);
		subPanel.add(button);
		add(subPanel, BorderLayout.SOUTH);
		
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (button.getText().equals("0")) {
					offset = totalAmount;
					button.setText("R");
					button.setToolTipText("Reset the counter offset.");
				} else {
					offset = 0;
					button.setText("0");
					button.setToolTipText("Zeros out the counter.");
				}
				setTotalAmount(totalAmount);
			}
		});
	}

	public synchronized void addDrop(int amount, boolean highlight) {
		drops.add( new Drop(amount, highlight) );
	}
	
	private void setTotalAmount(int newTotal) {
		totalAmount = newTotal;
		totalAmountText = NumberFormat.getIntegerInstance().format(totalAmount - offset);
	}

	@Override public synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);

		final long currTime = System.currentTimeMillis();
		final int durationMilli = properties.duration * 1000;
		for (Drop drop: drops) {
			if (currTime <= drop.startTime + durationMilli)
				drop.paint(g, currTime, durationMilli);
			else
				toRemove.add(drop);
		}
		
		for (Drop drop: toRemove) {
			setTotalAmount(totalAmount + drop.amount);
			drops.remove(drop);
		}
		toRemove.clear();
		
		g.setFont(font);
		g.setColor(getForeground());
		Rectangle2D textSize = g.getFontMetrics().getStringBounds(totalAmountText, g);
		g.drawString(totalAmountText,
				getWidth() - 8 - (int) textSize.getWidth(),
				getHeight() - 24 );
	}

	@Override
	public ImageIcon getIcon16() {
		if (icon16 == null)
			icon16 = new ImageIcon(FileUtils.getBufferedImage(getClass(), "resources/plugin/xpdrops/xp16.png"));
		return icon16;
	}

	@Override
	public ImageIcon getIcon32() {
		if (icon32 == null)
			icon32 = new ImageIcon(FileUtils.getBufferedImage(getClass(), "resources/plugin/xpdrops/xp32.png"));
		return icon32;
	}

	class Drop {
		final int amount;
		final boolean highlight;
		final String text;
		final long startTime;

		Drop(int amount, boolean highlight) {
			this.amount = amount;
			this.highlight = highlight;

			text = NumberFormat.getIntegerInstance().format(amount);
			startTime = System.currentTimeMillis();
		}

		void paint(Graphics g, long currTime, int durationMilli) {
			// Get the percentage of time that the drop has lasted
			double percent = (currTime-startTime) / ((double)durationMilli);
			
			int r, gr, b, a = 255;
			
			// Set the drop color to RED depending on whether it is a highlight or not
			if (highlight) {
				r = 255;
				gr = 0;
				b = 0;
			} else {
				Color c = getForeground();
				r = c.getRed();
				gr = c.getGreen();
				b = c.getBlue();
			}
			
			// If the drop is over 80% complete, start decreasing its alpha value
			if (percent > 0.8)
				a = (int) ( (1-percent) * 5 * 255);
			
			g.setColor(new Color(r, gr, b, a));
			g.setFont(font);
			Rectangle2D textSize = g.getFontMetrics().getStringBounds(text, g);

			g.drawString(text,
					getWidth() - 8 - (int) textSize.getWidth(),
					(int) ( percent * ( getHeight() - textSize.getHeight() - 24 ) + textSize.getHeight() ) );
		}
	}
}
