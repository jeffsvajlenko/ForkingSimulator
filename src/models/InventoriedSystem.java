package models;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import util.FileUtil;


public class InventoriedSystem {
	
	//Location of system
	Path location;
	
	//System Inventory
	List<Path> files;
	List<Path> directories;
	List<Path> leafDirectories;
	
	//For random selection without repeats
	List<Path> selectFiles;
	List<Path> selectDirectories;
	List<Path> selectLeafDirectories;
	
	//Random Number Generator
	Random random;
	
	//Language
	String language;
	
	/**
	 * Creates an inventoried system from the specified directory.
	 * @param systemdir The directory containing the system.
	 * @throws IOException
	 */
	public InventoriedSystem(Path systemdir, String language) throws IOException {
		// Check input
		if(!Files.exists(systemdir)) {
			throw new IllegalArgumentException("System does not exist.");
		}
		if(!Files.isDirectory(systemdir)) {
			throw new IllegalArgumentException("System is not a directory.");
		}
		
		//Language
		this.language = language;
		
		//Files
		files = FileUtil.fileInventory(systemdir);
		selectFiles = new ArrayList<Path>(files);
		
		//Directories
		directories = FileUtil.directoryInventory(systemdir);
		selectDirectories = new ArrayList<Path>(directories);
		
		//LeafDirectories
		leafDirectories = new ArrayList<Path>();
		for(Path p : directories) {
			if(FileUtil.isLeafDirectory(p)) {
				leafDirectories.add(p);
			}
		}
		selectLeafDirectories = new ArrayList<Path>(leafDirectories);
		
		//Function Fragments
		
		//Random Number Generator
		random = new Random();
	}

//--- Query Features
	
	/**
	 * Returns the number of files in the system.
	 * @return the number of files in the system.
	 */
	public int numFiles() {
		return this.files.size();
	}
	
	/**
	 * Returns the number of directories in the system.
	 * @return the number of directories in the system.
	 */
	public int numDirectories() {
		return this.directories.size();
	}
	
	/**
	 * Returns the number of leaf directories in the system.
	 * @return the number of leaf directories in the system.
	 */
	public int numLeafDirectories() {
		return this.leafDirectories.size();
	}
	
	/**
	 * Returns an unmodifiable list of all files in the system.
	 * @return an unmodifiable list of all files in the system.
	 */
	public List<Path> getFiles() {
		return Collections.unmodifiableList(this.files);
	}
	
	/**
	 * Returns an unmodifiable list of all directories in the system.
	 * @return an unmodifiable list of all directories in the system.
	 */
	public List<Path> getDirectories() {
		return Collections.unmodifiableList(this.directories);
	}
	
	/**
	 * Returns an unmodifiable list of all the leaf directories in the system.
	 * @return an unmodifiable list of all the leaf directories in the system.
	 */
	public List<Path> getLeafDirectories() {
		return Collections.unmodifiableList(this.leafDirectories);
	}
	
//--- Select Random Features, repeats	
	
	/**
	 * Returns a random file (as a Path object) from the system.  Repeats may occur in subsequent calls.
	 * @return a random file (as a path object) from the system, or null if there is no files to choose from.
	 */
	public Path getRandomFile() {
		if(files.size() == 0) {
			return null;
		} else {
			int index = random.nextInt(files.size());
			return files.get(index);
		}
	}
	
	/**
	 * Returns a random directory (as a Path object) from the system.  Repeats may occur in subsequent calls.
	 * @return a random directory (as a path object) from the system, or null if there is no directories to choose from.
	 */
	public Path getRandomDirectory() {
		if(directories.size() == 0) {
			return null;
		} else {
			int index = random.nextInt(directories.size());
			return directories.get(index);
		}	
	}
	
	/**
	 * Returns a random leaf directory (as a Path object) from the system.  Repeats may occur in subsequent calls.
	 * @return a random leaf directory (as a path object) from the system, or null if there is no directories to choose from.
	 */
	public Path getRandomLeafDirectory() {
		if(leafDirectories.size() == 0) {
			return null;
		} else {
			int index = random.nextInt(leafDirectories.size());
			return leafDirectories.get(index);
		}
	}
	
//---- Select random features, no repeats.	
	
	/**
	 * Returns a random file (as a Path object) from the system.  Repeats do not occur with subsequent calls unless it is reset (see resetRandomFileRepeat).
	 * @return a random file (as a Path object) from the system, or null if no files left to chose from (due to no repeats).
	 */
	public Path getRandomFileNoRepeats() {
		if(selectFiles.size() == 0) {
			return null;
		}	else {
			int index = random.nextInt(selectFiles.size());
			Path p = selectFiles.remove(index);
			return p;
		}
	}
	
	/**
	 * Resets getRandomFileNoRepeats so that any file in the system may be chosen (again without repeats).
	 */
	public void resetRandomFileRepeat() {
		this.selectFiles = new ArrayList<Path>(this.files);
	}
	
	/**
	 * Returns a random directory (as a Path object) from the system.  Repeats do not occur with subsequent calls unless it is reset (see resetRandomDirectoryRepeat).
	 * @return a random directory (as a Path object) from the system, or null if no directory left to chose from (due to no repeats).
	 */
	public Path getRandomDirectoryNoRepeats() {
		if(selectDirectories.size() == 0) {
			return null;
		} else {
			int index = random.nextInt(selectDirectories.size());
			Path p = selectDirectories.remove(index);
			return p;
		}
	}
	
	/**
	 * Resets getRandomDirectory so that any directory in the system may be chosen (again without repeats).
	 */
	public void resetRandomDirectoryRepeat() {
		this.selectDirectories = new ArrayList<Path>(this.directories);
	}
	
	/**
	 * Returns a random leaf directory (as a Path object) from the system.  Repeats do not occur with subsequent calls unless it is reset (see resetRandomLeafDirectoryRepeat).
	 * @return a random leaf directory (as a Path object) from the system, or null if no leaf directory left to chose from (due to no repeats).
	 */
	public Path getRandomLeafDirectoryNoRepeats() {
		if(selectLeafDirectories.size() == 0) {
			return null;
		} else {
			int index = random.nextInt(selectLeafDirectories.size());
			Path p = selectDirectories.remove(index);
			return p;
		}
	}
	
	/**
	 * Resets getRandomLeafDirectory so that any leaf directory in the system may be chosen (again without repeats).
	 */
	public void resetRandomLeafDirectoryRepeat() {
		this.selectLeafDirectories = new ArrayList<Path>(this.leafDirectories);
	}
}
