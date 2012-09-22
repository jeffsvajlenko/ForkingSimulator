package util;

import java.io.File;
import java.util.List;

public class FileUtil {
	
	/**
	 * Returns a list of all files under the specified directory.  Symbolic links are not followed.
	 * @param directory the directory to inventory.
	 * @return a list of all files under the specified directory.
	 */
	public static List<File> fileInventory(File directory) {
		return null;
	}
	
	/**
	 * Returns a list of all files under the specified directory.  Symbolic links are not followed.  
	 * @param directory
	 * @param includeExtentions a list of the file extentions to 
	 * @param excludeExtentions
	 * @return
	 */
	public static List<File> fileInventory(File directory, List<String> includeExtentions, List<String> excludeExtentions) {
		return null;
	}
	
	/**
	 * Returns a list of all directories in the specified directory.  Symbolic links are ignored.
	 * @param directory the directory to inventory.
	 * @return a list of all directories in the specified directory.
	 */
	public static List<File> directoryInventory(File directory) {
		return null;
	}
	
	
}
