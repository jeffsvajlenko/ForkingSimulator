package util;

import java.nio.file.Path;
import java.util.List;

public class FileUtil {
	
	/**
	 * Returns a list of all files under the specified directory.  Symbolic links are not followed.
	 * @param directory the directory to inventory.
	 * @return a list of all files under the specified directory.
	 */
	public static List<Path> fileInventory(Path directory) {
		return null;
	}
	
	/**
	 * Returns a list of all files under the specified directory.  Symbolic links are not followed.  
	 * @param directory
	 * @param includeExtentions a list of the file extentions to 
	 * @param excludeExtentions
	 * @return
	 */
	public static List<Path> fileInventory(Path directory, List<String> includeExtentions, List<String> excludeExtentions) {
		return null;
	}
	
	/**
	 * Returns a list of all directories in the specified directory.  Symbolic links are ignored.
	 * @param directory the directory to inventory.
	 * @return a list of all directories in the specified directory.
	 */
	public static List<Path> directoryInventory(Path directory) {
		return null;
	}
	
	
}
