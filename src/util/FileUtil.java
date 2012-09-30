package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 
 * A collection of utility functions for file IO.
 * 
 * Directory copy was taken from: https://github.com/bbejeck/Java-7
 *
 */
public class FileUtil {
	
	/**
	 * Returns a list of all files under the specified directory.  Symbolic links are neither included in the results, or followed in the search.
	 * Paths are absolute and normalised.
	 * @param directory the directory to inventory.
	 * @return a list of all files under the specified directory.  Each is specified by its absolute path.  (List is ArrayList).
	 * @throws IOException if an IOException occurs.
	 * @throws FileNotFoundException If the specified directory does not exist.
	 * @throws IllegalArgumentException If the specified path points to a file not a directory.
	 */
	public static List<Path> fileInventory(Path directory) throws IOException {
		if(!Files.exists(directory)) {
			throw new FileNotFoundException("Specified directory does not exist.");
		}
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("Specified path is not a directory.");
		}
		InventoryFiles visitor = new InventoryFiles();
		EnumSet<FileVisitOption> opts = EnumSet.noneOf(FileVisitOption.class);
		Files.walkFileTree(directory, opts, Integer.MAX_VALUE, visitor);
		return visitor.getInventory();
	}
	
	public static class InventoryFiles extends SimpleFileVisitor<Path> {
		List<Path> inventory;
		
		InventoryFiles() {
			inventory = new ArrayList<Path>();
		}
		
		public List<Path> getInventory() {
			return this.inventory;
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
			if (!attr.isSymbolicLink() && attr.isRegularFile()) {
				inventory.add(file.toAbsolutePath().normalize());
			}
			return FileVisitResult.CONTINUE;
		}
	}
	
	/**
	 * Returns a list of all directories in the specified directory.  Symbolic links are neither included in the results, or followed in the search.
	 * Paths are absolute and normalised.
	 * @param directory the directory to inventory.
	 * @return a list of all directories in the specified directory.  Each is specified by its absolute path.  (List is ArrayList).
	 * @throws IOException if an IOException occurs.
	 * @throws FileNotFoundException If the specified directory does not exist.
	 * @throws IllegalArgumentException If the specified path points to a file not a directory.
	 */
	public static List<Path> directoryInventory(Path directory) throws IOException {
		if(!Files.exists(directory)) {
			throw new FileNotFoundException("Specified directory does not exist.");
		}
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("Specified path is not a directory.");
		}
		InventoryDirectories visitor = new InventoryDirectories(directory);
		EnumSet<FileVisitOption> opts = EnumSet.noneOf(FileVisitOption.class);
		Files.walkFileTree(directory, opts, Integer.MAX_VALUE, visitor);
		return visitor.getInventory();
	}
	//Class used by directoryInventory to collect all found directories which are not symbolic links.
	public static class InventoryDirectories extends SimpleFileVisitor<Path> {
		List<Path> inventory;
		Path root;
		
		InventoryDirectories(Path root) {
			inventory = new ArrayList<Path>();
			this.root = root.toAbsolutePath().normalize();
		}
		
		public List<Path> getInventory() {
			return this.inventory;
		}
		
		@Override
		public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attr) {
			if (!attr.isSymbolicLink() && attr.isDirectory()) {
				if(!file.toAbsolutePath().equals(root)) {
					inventory.add(file.toAbsolutePath());
				}
			}
			return FileVisitResult.CONTINUE;
		}
	}
	
	/**
	 * Returns if specified path denotes a leaf directory (a directory which contains no directories).
	 * Paths are absolute and normalised.
	 * @param directory Path of directory to test for leaf status.
	 * @return if specified path denotes a leaf directory.
	 * @throws FileNotFoundException If the specified directory does not exist.
	 * @throws IOException If an IOException occurs when investigating the directory and its children.
	 */
	public static boolean isLeafDirectory(Path directory) throws FileNotFoundException, IOException {
		if(!Files.exists(directory)) {
			throw new FileNotFoundException("Specified directory does not exist.");
		}
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("Specified path is not a directory.");
		}
		
		DirectoryStream<Path> ds = null;
		try {
			ds = Files.newDirectoryStream(directory);
			for(Path p : ds) {
				if(Files.isDirectory(p)) {
					ds.close();
					return false;
				}
			}
			ds.close();
		} catch (IOException e) {
			if(ds != null) {
				try {ds.close();} catch (IOException ee){};
			}
			throw e;
		}
		return true;
	}
	
	/**
	 * Copies a directory from src to dest.  Destination directory must not exist before call.
	 * @param src The directory to copy.
	 * @param dest The destination of the copy, specified as the root of the copied file tree.  Copied root may have different name from source.
	 * @throws IOException If an IOException occurs during the copy operation.
	 * @throws IllegalArgumentException if dest specifies an existing file or directory.
	 */
	public static void copyDirectory(Path src, Path dest) throws IOException {
		//Check input
		Objects.requireNonNull(src);
		Objects.requireNonNull(dest);
		FileUtil.requireDirectory(src);
		
		if(Files.exists(dest)) {
			throw new IllegalArgumentException("Destination already exists.");
		}
		
		//Create destination directory
		Files.createDirectories(dest);
		
		//Copy contents
		DirectoryStream<Path> ds = null;
		try {
			ds = Files.newDirectoryStream(src);
			for(Path p : ds) {
				if(Files.isDirectory(p)) {
					FileUtil.copyDirectory(p, dest.resolve(p.getFileName()));
				} else {
					Files.copy(p, dest.resolve(p.getFileName()));
				}
			}
			ds.close();
		} catch(IOException e) {
			if(ds != null) {
				try {ds.close();} catch (IOException ee){};
			}
			throw e;
		}
	}
	
	/**
	 * Deletes the specified directory and its contents.  If function fails it may leave the directory partially deleted.
	 * @param directory The directory to delete.
	 * @throws IOException If an IO error occurs, such as a file/directory being impossible to delete (ex: permissions).
	 */
	public static void deleteDirectory(Path directory) throws IOException {
		//Check input
		Objects.requireNonNull(directory);
		FileUtil.requireDirectory(directory);
		
		//Delete Contents
		DirectoryStream<Path> ds = null;
		try {
			ds = Files.newDirectoryStream(directory);
			for(Path p : ds) {
				if(Files.isDirectory(p)) {
					deleteDirectory(p);
				} else {
					Files.delete(p);
				}
			}
			ds.close();
		} catch (IOException e) {
			if(ds != null) {
				try {ds.close();} catch(IOException ee){};
			}
			throw e;
		}
		
		//Delete Self
		Files.delete(directory);
	}
	
	private static void requireDirectory(Path directory) {
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException(directory.toString() + " is not a directory.");
		}
	}
	
	//private static void requireRegularFile(Path file) {
	//	if(!Files.isRegularFile(file)) {
	//		throw new IllegalArgumentException(file.toString() + " is not a directory.");
	//	}
	//}
}
