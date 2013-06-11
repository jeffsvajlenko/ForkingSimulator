package util;

public class FilenameGenerator {
	
	/**
	 * Lexicon of characters for generated filename.
	 */
	private final static String lexicon = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
	
	/**
	 * Random number generator.
	 */
	private final static java.util.Random rand = new java.util.Random();
	
	/**
	 * Returns a filename consisting of alpha-numeric characters, of a length between minSize (inclusive) and maxSize (inclusive).
	 * @param minSize Minimum size of the generated filename.
	 * @param maxSize Maximum size of the generated filename.
	 * @return The randomly generated filename.
	 */
	public static String getRandomFilename(int minSize, int maxSize) {
		StringBuilder builder = new StringBuilder();
		int length = rand.nextInt(maxSize - minSize + 1) + minSize;
		for(int i = 0; i < length; i++) {
			builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
		}
		return builder.toString();
	}
	
	/**
	 * Returns a filename consisting of alpha-numeric characters, of a length between minSize (inclusive) and maxSize (inclusive), with the given file extention.
	 * @param minSize
	 * @param maxSize
	 * @param ext
	 * @return
	 */
	public static String getRandomFilenameWithExtention(int minSize, int maxSize, String ext) {
		return getRandomFilename(minSize, maxSize) + "." + ext;
	}
}
