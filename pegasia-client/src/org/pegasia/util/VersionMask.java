package org.pegasia.util;

// http://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
public class VersionMask {
	public final Version lowerBound, upperBound;

	public VersionMask(String mask) {
		String[] split = mask.split("-");
		
		if (split.length > 0 && split[0] != null && split[0] != "")
			lowerBound = new Version(split[0]);
		else
			lowerBound = null;
		
		if (split.length > 1 && split[1] != null && split[1] != "")
			upperBound = new Version(split[1]);
		else
			upperBound = null;
	}
	
	@Override public String toString() {
		String lowerString = "", upperString = "";
		
		if (lowerBound != null)
			lowerString = lowerBound.toString();
		if (upperBound != null)
			upperString = '-' + upperBound.toString();
		
		return lowerString + upperString;
	}
}
