package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * A collection of utility functions for file IO.
 *
 */
public class FileUtil {
	
	/**
	 * Returns a list of all files under the specified directory.  Symbolic links are neither included in the results, or followed in the search.
	 * @param directory the directory to inventory.
	 * @return a list of all files under the specified directory.  Each is specified by its absolute path.
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
			inventory = new LinkedList<Path>();
		}
		
		public List<Path> getInventory() {
			return this.inventory;
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
			if (!attr.isSymbolicLink() && attr.isRegularFile()) {
				inventory.add(file.toAbsolutePath());
			}
			return FileVisitResult.CONTINUE;
		}
	}
	
	/**
	 * Returns a list of all directories in the specified directory.  Symbolic links are neither included in the results, or followed in the search.
	 * @param directory the directory to inventory.
	 * @return a list of all directories in the specified directory.  Each is specified by its absolute path.
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
			inventory = new LinkedList<Path>();
			this.root = root.toAbsolutePath();
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
	
}
