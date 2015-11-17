package org.pegasia.util;

// http://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
public class Version implements Comparable<Version> {
	private final String version;

	public Version(String version) {
		if (version == null)
			throw new IllegalArgumentException("Version can not be null");
		if (!version.matches("[0-9]+(\\.[0-9]+)*"))
			throw new IllegalArgumentException("Invalid version format");
		this.version = version;
	}

	@Override public int compareTo(Version other) {
		if (other == null)
            return 1;
		
        String[] thisParts = version.split("\\.");
        String[] thatParts = other.version.split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart)
                return -1;
            if (thisPart > thatPart)
                return 1;
        }
        return 0;
	}
	
	@Override public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        return this.compareTo((Version) other) == 0;
    }
	
	/**
	 * Will return true if the provided mask is null.
	 * 
	 * @param mask
	 * @return
	 */
	public boolean matches(VersionMask mask) {
		if (mask == null)
			return true;

		return compareTo(mask.lowerBound) >= 0 && (mask.upperBound == null || compareTo(mask.upperBound) <= 0);
	}
	
	@Override public String toString() {
		return version;
	}
}
