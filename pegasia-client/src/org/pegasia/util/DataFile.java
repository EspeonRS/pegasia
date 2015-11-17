package org.pegasia.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class DataFile {
	private final File file;
	private final Map<String, Map<String, String>> data;

	public DataFile(File file) {
		this.file = file;
		this.data = Collections.synchronizedMap(new LinkedHashMap<String, Map<String, String>>());

		try {
			load();
		} catch (IOException e) {}
	}

	public void load() throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			Map<String, String> section = createSection(null);

			for (String line; (line = reader.readLine()) != null;) {
				if (line.length() == 0)
					continue;

				if (line.charAt(0) == '[') {
					int end = line.indexOf(']');
					if (end > 0)
						section = createSection(sanitize(line.substring(1, end)));
					else
						section = createSection(sanitize(line.substring(1)));
				} else {
					int split = line.indexOf('=');
					String key;
					if (split < 0)
						key = sanitize(line);
					else
						key = sanitize(line.substring(0, split));

					if (key.length() > 0)
						section.put(key, split < 0 ? null : line.substring(split+1));
				}
			}
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {}
		}
	}

	public void save() throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));

			Map<String, String> nullSection = data.get(null);
			if (nullSection != null)
				writeSection(writer, null, nullSection);

			for (Entry<String, Map<String, String>> section: data.entrySet()) 
				if (section.getKey() != null)
					writeSection(writer, section.getKey(), section.getValue());
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	public boolean containsSection(String section) {
		return data.containsKey(sanitize(section));
	}

	public boolean containsKey(String section, String key) {
		Map<String, String> sect = data.get(sanitize(section));
		if (sect == null)
			return false;

		return sect.containsKey(sanitize(key));
	}

	public String get(String section, String key, String defaultValue) {
		String ret = get(section, key);
		return ret != null ? ret : defaultValue;
	}

	public int getInt(String section, String key, int defaultValue) {
		try {
			return Integer.valueOf(get(section, key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public boolean getBool(String section, String key, boolean defaultValue) {
		String value = get(section, key);
		if ("true".equalsIgnoreCase(value))
			return true;
		if ("false".equalsIgnoreCase(value))
			return false;
		return defaultValue;
	}

	public void put(String section, String key, String value) {
		if (key == null)
			return;

		section = sanitize(section);
		Map<String, String> sect = data.get(section);
		if (sect == null)
			sect = createSection(section);

		sect.put(sanitize(key), value);
	}

	public void put(String section, String key, int value) {
		put(section, key, Integer.toString(value));
	}

	public void put(String section, String key, boolean value) {
		put(section, key, Boolean.toString(value));
	}

	public boolean removeSection(String section) {
		return data.remove(sanitize(section)) != null;
	}

	public boolean removeKey(String section, String key) {
		Map<String, String> sect = data.get(sanitize(section));
		if (sect == null)
			return false;

		key = sanitize(key);
		boolean contains = sect.containsKey(key);
		if (contains)
			sect.remove(key);

		return contains;
	}

	public Set<String> sectionSet() {
		return data.keySet();
	}

	public Set<String> keySet(String section) {
		Map<String, String> sect = data.get(sanitize(section));
		if (sect == null)
			return null;
		return sect.keySet();
	}

	public Set<Entry<String, String>> entrySet(String section) {
		Map<String, String> sect = data.get(sanitize(section));
		if (sect == null)
			return null;
		return sect.entrySet();
	}

	public static String sanitize(String str) {
		return str == null ? null : str.trim().replaceAll("[_\\s]", "-").replaceAll("[=\\[\\]]", "").toLowerCase();
	}

	protected Map<String, String> createSection(String sectionName) {
		Map<String, String> section = Collections.synchronizedSortedMap(new TreeMap<String, String>());
		data.put(sectionName, section);
		return section;
	}

	protected String get(String section, String key) {
		section = sanitize(section);
		key = sanitize(key);

		Map<String, String> sect = data.get(section);
		if (sect == null)
			return null;

		return sect.get(key);
	}

	protected void writeSection(BufferedWriter writer, String sectionName, Map<String, String> section) throws IOException {
		if (sectionName != null) {
			writer.newLine();
			writer.write('[' + sectionName + ']');
			writer.newLine();
		}

		for (Entry<String, String> entry: section.entrySet()) {
			if (entry.getValue() == null)
				writer.write(entry.getKey());
			else
				writer.write(entry.getKey() + "=" + entry.getValue());
			writer.newLine();
		}
	}
}
