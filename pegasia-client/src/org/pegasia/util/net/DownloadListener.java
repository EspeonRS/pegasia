package org.pegasia.util.net;

public interface DownloadListener {
	/**
	 * Tells the download handler how many milliseconds between
	 * each progress update it sends to the listener.
	 * 
	 * @return Period between notifications in milliseconds
	 */
	int getNotificationInterval();
	
	/**
	 * Method called at the interval defined in getNotificationInterval.
	 * 
	 * @param progress The number of bytes that have been downloaded
	 * @param total The total number of bytes of the file
	 */
	void notifyDownloadUpdate(int progress, int total);
	
	/**
	 * Method called when the file download has completed.
	 * 
	 * @param success Whether the entire file was downloaded successfully
	 */
	void notifyDownloadEnd(boolean success);
}
