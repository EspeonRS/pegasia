package org.pegasia.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public enum Webpage {
	GAME_PAGE("http://oldschool30.runescape.com/j1"),
	NEWS("http://services.runescape.com/m=news/latest_news.rss?oldschool=true"),
	WORLD_LIST("http://oldschool.runescape.com/slu"),
	;
	
	private final String address;
	private String content = null;
	
	Webpage(String address)
	{
		this.address = address;
	}
	
	/**
	 * Gets the content of a webpage in String form. If the
	 * website has been previously accessed, it will use the
	 * latest accessed version instead of redownloading it.
	 * 
	 * @return The contents of the web page in String format
	 * @throws IOException
	 */
	public String getContent() throws IOException
	{
		if (content == null)
			refreshContent();
		return content;
	}
	
	/**
	 * Refreshes the cached content of the web page.
	 * 
	 * @throws IOException
	 */
	public void refreshContent() throws IOException
	{
		content = WebUtil.webToString(createURL());
	}
	
	/**
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public URL createURL() throws MalformedURLException
	{
		return new URL(address);
	}
	
	public BufferedReader createBufferedReader() throws IOException	{
		return WebUtil.createBufferedReader(createURL());
	}
}
