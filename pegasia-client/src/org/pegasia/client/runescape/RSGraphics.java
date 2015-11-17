package org.pegasia.client.runescape;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.text.AttributedCharacterIterator;

import org.pegasia.api.component.PegasiaOverlay;

public class RSGraphics extends Graphics {
	final RSClient client;
	final Canvas origCanvas;
	final HookedCanvas canvas;

	Image surface;
	Graphics2D graphics;
	final Dimension canvasSize, surfaceSize;
	int halfSurfaceHeight;

	boolean frameDrawn = false, imageStarted = false;
	boolean useVolatile = false;

	//CodeTimer t = new CodeTimer(); int tick = 0;
	//RSGraphicsDebug debug = new RSGraphicsDebug();

	RSGraphics(RSClient client, Canvas origCanvas) {
		this.client = client;
		this.origCanvas = origCanvas;
		this.canvas = new HookedCanvas();

		canvasSize = new Dimension(-1, -1);
		surfaceSize = new Dimension(-1, -1);
		halfSurfaceHeight = 0;
	}

	void nextFrame() {
		// If the game has not yet been drawn this frame, do so now
		update(null);

		// Remove the drawn flag for the next frame
		frameDrawn = false;
	}

	private void update(Image gameSurface) {
		// Do not draw the image if the game has already updated this frame
		if (frameDrawn)
			return;
		frameDrawn = true;

		// ****************************************************************
		// ****************************************************************
		// Stuff used to test sanic heghog gotta go fast
		/*t.time("overhead");
		tick++;
		if (tick >= 200) {
			tick = 0;
			t.setEnabled(true);

			//System.out.println("Surface: " + surfaceSize + " - half: " + halfSurfaceHeight);
			//System.out.println("Canvas: " + canvasSize);
		} else {
			t.setEnabled(false);
		}*/
		// ****************************************************************
		// ****************************************************************

		// Get the GraphicsConfiguration used by RuneScape's canvas
		GraphicsConfiguration gc = origCanvas.getGraphicsConfiguration();

		// Store the size of the canvas from last frame
		int prevWidth = canvasSize.width;
		int prevHeight = canvasSize.height;

		// Get the current canvas size
		if (gameSurface != null) {
			BufferedImage bimg = (BufferedImage) gameSurface;
			canvasSize.width = bimg.getWidth();
			canvasSize.height = bimg.getHeight();
		} else {
			canvasSize.width = origCanvas.getWidth();
			canvasSize.height = origCanvas.getHeight();
		}

		// If the game's canvas has changed size at all, verify that the surface is
		// still large enough to handle the game's new size
		if ( (canvasSize.width != prevWidth || canvasSize.height != prevHeight) 
			|| (canvasSize.width > surfaceSize.width || canvasSize.height * 2 > surfaceSize.height) )
				// If the game's canvas is now larger than the surface, recreate the surface
				recreateSurface(gc);

		// If the surface uses VolatileImage, check if the surface has been lost since
		// the previous frame.
		if (useVolatile && ((VolatileImage)surface).validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE)
			// If the surface could not be recovered automatically, recreate it.
			recreateSurface(gc);

		// ****************************************************************
		// ****************************************************************
		//t.time("game to surface");
		// ****************************************************************
		// ****************************************************************

		// If the frame is being drawn using an image from the game, copy that image
		// onto the surface.
		if (gameSurface != null)
			graphics.drawImage(gameSurface, 0, halfSurfaceHeight, canvasSize.width, halfSurfaceHeight + canvasSize.height, 0, 0, canvasSize.width, canvasSize.height, Color.BLACK, null);

		synchronized (this) {
			// ****************************************************************
			// ****************************************************************
			//t.time("surface copy");
			// ****************************************************************
			// ****************************************************************

			// Copy the bottom half of the surface up to the top half
			graphics.copyArea(0, halfSurfaceHeight, canvasSize.width, canvasSize.height, 0, -halfSurfaceHeight);

			// ****************************************************************
			// ****************************************************************
			//t.time("plugin render");
			// ****************************************************************
			// ****************************************************************

			// Draw any plugin overlays
			for (PegasiaOverlay overlay: client.overlays) {
				Graphics2D g = (Graphics2D) surface.getGraphics();
				g.setClip(0, 0, canvasSize.width, canvasSize.height);
				try {
					overlay.paintOverlay(g);
				} catch (Exception e) {}
				g.dispose();
			}
		}

		// ****************************************************************
		// ****************************************************************
		//t.time("surface to canvas");
		// ****************************************************************
		// ****************************************************************
		if ( !(useVolatile && ((VolatileImage)surface).contentsLost()) ) {
			Graphics gr = origCanvas.getGraphics();
			gr.drawImage(surface, 0, 0, canvasSize.width, canvasSize.height, 0, 0, canvasSize.width, canvasSize.height, Color.BLACK, null);
			gr.dispose();
		}

		// ****************************************************************
		// ****************************************************************
		//t.time("game render");
		// ****************************************************************
		// ****************************************************************
		if (!imageStarted) {
			if (gameSurface == null) {
				graphics.setColor(Color.BLACK);
				graphics.fillRect(0, halfSurfaceHeight, canvasSize.width, canvasSize.height);
			} else
				imageStarted = true;
		}
		
		//debug.updateImage(this);
		return;
	}
	
	int tick = 0;

	private void recreateSurface(GraphicsConfiguration gc) {
		if (graphics != null)
			graphics.dispose();
		if (surface != null)
			surface.flush();

		// Calculate the width and height of the new images needed
		surfaceSize.width = Math.max(canvasSize.width, 765);
		surfaceSize.width = Integer.highestOneBit(surfaceSize.width - 1) << 1;

		halfSurfaceHeight = Math.max(canvasSize.height, 503);
		halfSurfaceHeight = Integer.highestOneBit(halfSurfaceHeight - 1) << 1;
		surfaceSize.height = halfSurfaceHeight * 2; // Double the height to fit two images

		if (useVolatile)
			surface = gc.createCompatibleVolatileImage(surfaceSize.width, surfaceSize.height, VolatileImage.OPAQUE);
		else {
			if (gc != null)
				surface = gc.createCompatibleImage(surfaceSize.width, surfaceSize.height, BufferedImage.OPAQUE);
			else
				surface = new BufferedImage(surfaceSize.width, surfaceSize.height, BufferedImage.OPAQUE);
			surface.setAccelerationPriority(1f);
		}

		graphics = (Graphics2D) surface.getGraphics();
		graphics.setBackground(Color.BLACK);
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, surfaceSize.width, surfaceSize.height);
	}

	synchronized void renderSurface(Graphics g, int xOffset, int yOffset, int width, int height) {
		int sx2 = Math.max(xOffset + width, canvasSize.width);
		int sy2 = Math.max(yOffset + height, canvasSize.height);
		g.drawImage(surface, 0, 0, sx2 - xOffset, sy2 - yOffset, xOffset, yOffset, sx2, sy2, Color.BLACK, null);
	}

	class HookedCanvas extends Canvas {
		@Override
		public Graphics getGraphics() {
			nextFrame();
			return RSGraphics.this;
		}

		@Override
		public int hashCode() {
			return origCanvas.hashCode();
		}

		@Override
		public String toString() {
			return origCanvas.toString();
		}

		@Override
		public void setBounds(int x, int y, int width, int height) {
			super.setBounds(x, y, width, height);
			origCanvas.setBounds(x, y, width, height);
		}
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		update(img);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		update(img);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		update(img);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		update(img);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		update(img);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		update(img);
		return true;
	}


	@Override
	public Shape getClip() {
		return getClipBounds();
	}

	@Override
	public Rectangle getClipBounds() {
		return new Rectangle(0, 0, canvasSize.width, canvasSize.height);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		graphics.copyArea(x, halfSurfaceHeight + y, width, height, dx, halfSurfaceHeight + dy);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		graphics.drawArc(x, halfSurfaceHeight + y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		graphics.drawLine(x1, halfSurfaceHeight + y1, x2, halfSurfaceHeight + y2);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		graphics.drawOval(x, halfSurfaceHeight + y, width, height);
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		for (int i = 0; i < nPoints; i++)
			yPoints[i] += halfSurfaceHeight;
		graphics.drawPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		for (int i = 0; i < nPoints; i++)
			yPoints[i] += halfSurfaceHeight;
		graphics.drawPolyline(xPoints, yPoints, nPoints);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		graphics.drawRoundRect(x, halfSurfaceHeight + y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawString(String str, int x, int y) {
		graphics.drawString(str, x, halfSurfaceHeight + y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		graphics.drawString(iterator, x, halfSurfaceHeight + y);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		graphics.fillArc(x, halfSurfaceHeight + y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		graphics.fillOval(x, halfSurfaceHeight + y, width, height);
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		for (int i = 0; i < nPoints; i++)
			yPoints[i] += halfSurfaceHeight;
		graphics.fillPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		graphics.fillRect(x, halfSurfaceHeight + y, width, height);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		graphics.fillRoundRect(x, halfSurfaceHeight + y, width, height, arcWidth, arcHeight);
	}

	@Override
	public Color getColor() {
		return graphics.getColor();
	}

	@Override
	public Font getFont() {
		return graphics.getFont();
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return graphics.getFontMetrics(f);
	}

	@Override
	public void setColor(Color c) {
		graphics.setColor(c);
	}

	@Override
	public void setFont(Font font) {
		graphics.setFont(font);
	}

	@Override
	public void setPaintMode() {
		graphics.setPaintMode();
	}

	@Override
	public void setXORMode(Color c1) {
		graphics.setXORMode(c1);
	}

	@Override
	public Graphics create() {
		return this;
	}

	@Override public void clearRect(int x, int y, int width, int height) {}
	@Override public void clipRect(int x, int y, int width, int height) {}
	@Override public void dispose() {}
	@Override public void setClip(Shape clip) {}
	@Override public void setClip(int x, int y, int width, int height) {}
	@Override public void translate(int x, int y) {}
}
