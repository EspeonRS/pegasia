package org.pegasia.util.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebUtil {
	/**
	 * Downloads a webpage and returns it as a string.
	 * 
	 * @param website Location of the webpage to load
	 * @return String version of the loaded webpage
	 * @throws IOException Thrown when webpage failed to load.
	 */
	public static String webToString(URL website) throws IOException {
		BufferedReader reader = createBufferedReader(website);
		StringBuilder lines = new StringBuilder();
		String line;
		
		while ((line = reader.readLine()) != null) {
			lines.append(line);
			lines.append('\n');
		}
		
		reader.close();
		return lines.toString();
	}
	
	/**
	 * Downloads a web file using the provided download listener.
	 * 
	 * @param url Location of the file to download
	 * @param file Location to save the file
	 * @param listener Listener used to keep track of the download
	 */
	public static void webToFile(URL url, File file, DownloadListener listener) {
		(new DownloadHandler(url, file, listener)).execute();
	}
	
	public static BufferedReader createBufferedReader(URL url) throws IOException {
		return new BufferedReader(
				new InputStreamReader(
						((HttpURLConnection) url.openConnection()).getInputStream() ));
	}
}
