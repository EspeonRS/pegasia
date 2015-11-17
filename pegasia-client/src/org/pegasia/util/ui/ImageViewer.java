package org.pegasia.util.ui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class ImageViewer extends JComponent implements ComponentListener, MouseMotionListener, MouseWheelListener {
	public static final double SCROLL_RATE = 0.065;
	private BufferedImage image;
	private ImageViewerFillType fillType;
	private double scale;
	private Point offset, mousePrevious;

	public ImageViewer(BufferedImage image, ImageViewerFillType fill) {
		super();
		this.image = image;
		this.fillType = fill;
		this.offset = new Point(0, 0);

		fitToSize();
		addComponentListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public void changeImage(BufferedImage image) {
		this.image = image;
		this.offset = new Point(0, 0);
		fitToSize();
		repaint();
	}

	public void fitToSize() {
		scale = minScale();
		fitBounds();
	}

	private void fitBounds() {
		if (imageGetWidth() < getContainerWidth())
			offset.x = 0;
		else {
			boolean neg = offset.x < 0;
			offset.x = Math.min(Math.abs(offset.x), Math.abs(imageGetWidth()-getContainerWidth())/2);
			if (neg)
				offset.x = -offset.x;
		}

		if (imageGetHeight() < getContainerHeight())
			offset.y = 0;
		else {
			boolean neg = offset.y < 0;
			offset.y = Math.min(Math.abs(offset.y), Math.abs(imageGetHeight()-getContainerHeight())/2);
			if (neg)
				offset.y = -offset.y;
		}
	}

	/**
	 * Calculates the minimum scale the image should be to fit
	 * entirely into the given space.
	 * 
	 * @return The minimum scale of the image, from 0.05 to 1.
	 */
	private double minScale() {
		if ( image == null )
			return 1.0;

		try {
			switch (fillType) {
			case FILL_FRAME:
				return Math.min(1, Math.max(0.05, Math.max(
						(double)getContainerWidth() / image.getWidth(),
						(double)getContainerHeight() / image.getHeight())));
			case FIT_FRAME:
				return Math.min(1, Math.max(0.05, Math.min(
						(double)getContainerWidth() / image.getWidth(),
						(double)getContainerHeight() / image.getHeight())));
			default:
				return 1;
			}
		}
		catch (ArithmeticException e) {
			return 1;
		}
	}

	/**
	 * Renders the image within the component.
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (image != null) {
			int xPos = (int)(getContainerWidth()/2 - imageGetWidth()/2 + offset.x);
			int yPos = (int)(getContainerHeight()/2 - imageGetHeight()/2 + offset.y);

			g.drawImage(image, xPos, yPos, imageGetWidth(), imageGetHeight(), this);
		}
	}

	private int imageGetWidth() {
		if (image == null)
			return 0;
		return (int)(image.getWidth()*scale);
	}

	private int imageGetHeight() {
		if (image == null)
			return 0;
		return (int)(image.getHeight()*scale);
	}

	private int getContainerWidth() {
		return getWidth();
	}

	private int getContainerHeight() {
		return getHeight();
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		if ( scale < minScale()) {
			scale = minScale();
			repaint();
		}
		fitBounds();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePrevious = e.getPoint();
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point parentOnScreen = getParent().getLocationOnScreen();
		Point mouseOnScreen = e.getLocationOnScreen();

		offset.x += mouseOnScreen.x - parentOnScreen.x - mousePrevious.x;
		offset.y += mouseOnScreen.y - parentOnScreen.y - mousePrevious.y;
		fitBounds();

		mousePrevious = e.getPoint();
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point centerMouse = new Point(getContainerWidth()/2 - e.getPoint().x, getContainerHeight()/2 - e.getPoint().y);
		double oldScale = scale;
		scale = Math.max(minScale(), Math.min(1,scale-SCROLL_RATE*e.getPreciseWheelRotation()));

		offset.x = (int) Math.round((offset.x+centerMouse.x) / oldScale * scale)-centerMouse.x;
		offset.y = (int) Math.round((offset.y+centerMouse.y) / oldScale * scale)-centerMouse.y;

		fitBounds();
		repaint();
	}

	public static enum ImageViewerFillType {
		FILL_FRAME,
		FIT_FRAME,
		NO_ZOOM,
	}

	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentShown(ComponentEvent arg0) {}
}
