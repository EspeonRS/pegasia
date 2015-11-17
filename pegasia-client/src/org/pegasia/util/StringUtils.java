package org.pegasia.util;

import java.awt.Rectangle;
import java.util.Calendar;

public final class StringUtils {
	public static String formatName(String name) {
		if (name == null)
			return "";
		return name.trim().toLowerCase().replaceAll("[_\\s]", "-").replaceAll("[^a-z0-9-]", "");
	}
	
	public static String getYear() {
		return Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
	}

	public static String getMonth() {
		switch (Calendar.getInstance().get(Calendar.MONTH)) {
		case 0: return "January";
		case 1: return "February";
		case 2: return "March";
		case 3: return "April";
		case 4: return "May";
		case 5: return "June";
		case 6: return "July";
		case 7: return "August";
		case 8: return "September";
		case 9: return "October";
		case 10: return "November";
		case 11: return "December";
		default: return "Undefined";
		}
	}

	public static String fillNumber(int num, int digits) {
		String str = Integer.toString(num);
		if (str.length() < digits)
			str = (new String(new char[digits-str.length()]).replace("\0", "0")) + str;
		return str;
	}

	public static String shorten(String str, int length) {
		if (str.length() <= length)
			return str;

		return str.substring(0, length-3) + "...";
	}

	public static String rectangleToString(Rectangle r) {
		return r.x + "," + r.y + "," + r.width + "," + r.height;
	}

	public static Rectangle stringToRectangle(String s) {
		try {
			String[] split = s.split(",");

			if (split.length >= 4)
				return new Rectangle(
						Integer.parseInt(split[0]),
						Integer.parseInt(split[1]),
						Integer.parseInt(split[2]),
						Integer.parseInt(split[3]));
		} catch (Exception e) {}
		
		return null;
	}
}
