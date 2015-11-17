package org.pegasia.client.runescape;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.pegasia.util.net.WebUtil;

public class RSParameters implements AppletStub {
	static final String URL_START = "http://oldschool",
			URL_END = ".runescape.com/jav_config.ws",
			PARAM_PREFIX = "param",
			MSG_PREFIX = "msg";
	final HashMap<String, String> parameters;

	static RSParameters loadParameters(int world) throws IOException {
		HashMap<String, String> parameters = new HashMap<String, String>();
		
		String url = URL_START + (world > 300 ? (world - 300) : "") + URL_END;
		BufferedReader reader = WebUtil.createBufferedReader(new URL(url));

		// Load the parameters line by line
		String line;
		while ((line = reader.readLine()) != null) {
			int splitLocation = line.indexOf('=');
			String key = line.substring(0, splitLocation);

			if (key.equals(MSG_PREFIX) || key.equals(PARAM_PREFIX)) {
				splitLocation = line.indexOf('=', splitLocation + 1);
				key = line.substring(0, splitLocation).replaceAll("=", "-");
			}

			parameters.put(key, line.substring(splitLocation + 1));
			
			// DEBUG:	System.out.println(key + ": "+ line.substring(splitLocation + 1));
		}
		
		return new RSParameters(parameters);
	}

	RSParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

	String getValue(String key) {
		return parameters.get(key);
	}

	@Override
	public void appletResize(int width, int height) {}

	@Override
	public AppletContext getAppletContext() {
		return null;
	}

	@Override
	public URL getCodeBase() {
		try {
			return new URL(parameters.get("codebase"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public URL getDocumentBase() {
		return getCodeBase();
	}

	@Override
	public String getParameter(String key) {
		// If the starting character of parameter begins with a number
		char startChar = key.charAt(0);
		if (startChar >= '0' && startChar <= '9')
			// Return the parameter as it is saved with a "param-" prefix
			return parameters.get("param-" + key);

		switch (key) {
		case "image": return "http://www.runescape.com/img/rsp777/oldschool_ani.gif";
		case "java_arguments": return "-Xmx256m -Xss2m -Dsun.java2d.noddraw=true -XX:CompileThreshold=1500 -Xincgc -XX:+UseConcMarkSweepGC -XX:+UseParNewGC";
		case "boxborder": return "false";
		case "separate_jvm": return "true";
		case "boxbgcolor": return "red";
		case "centerimage": return "true";
		default: return null;
		}
	}

	@Override
	public boolean isActive() {
		return false;
	}
}
