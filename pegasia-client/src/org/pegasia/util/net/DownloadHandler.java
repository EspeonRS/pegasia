package org.pegasia.util.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.SwingWorker;
import javax.swing.Timer;

class DownloadHandler extends SwingWorker<Void,Void> implements ActionListener {
	private final URL url;
	private final File file;
	private final DownloadListener listener;
	private final Timer timer;

	private volatile int progress, total;
	private volatile boolean success;

	DownloadHandler(URL url, File file, DownloadListener listener) {
		this.url = url;
		this.file = file;
		this.listener = listener;
		this.timer = new Timer(listener.getNotificationInterval(), this);

		progress = 0;
		total = 0;
		success = false;
	}

	@Override
	protected Void doInBackground() {
		try {
			// Connect to web server
			URLConnection connection = url.openConnection();
			total = connection.getContentLength();
			if (listener != null) {
				listener.notifyDownloadUpdate(0, total);
				timer.start();
			}

			// Create file
			final InputStream reader = url.openStream();
			final File tempFile = new File(file.getAbsolutePath() + ".temp");
			final FileOutputStream writer = new FileOutputStream(tempFile);
			byte[] buffer = new byte[2048];
			int bytesRead = 0;

			// Stream from web to file
			while ((bytesRead = reader.read(buffer)) > 0) {
				writer.write(buffer, 0 , bytesRead);
				progress += bytesRead;
			}

			// Finish up
			writer.close();
			if (file.exists() && file.delete())
				tempFile.renameTo(file);
			else if (!file.exists())
				tempFile.renameTo(file);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void done() {
		if (listener != null)
			listener.notifyDownloadEnd(success);
		timer.stop();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (listener != null)
			listener.notifyDownloadUpdate(progress, total);
	}
}