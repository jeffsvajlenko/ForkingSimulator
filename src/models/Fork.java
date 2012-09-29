package models;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import util.FileUtil;


public class Fork {
	
	//Inventory of the fork pre-modification
	private InventoriedSystem fork;
	
	//Max number of attempts to inject before fail
	private final int MAX_ATTEMPTS = 100;
	
	//Variant tracking
	private List<Variant> variants;
	private List<FileVariant> filevariants;
	private List<DirectoryVariant> directoryvariants;
	
	/**
	 * Creates a fork of the system contained in systemdir of the specified source language, and stores it in the specified forkdir.
	 * @param systemdir Path to the directory containing the system to fork.
	 * @param forkdir Path to the directory in which to store the fork.  Directory must not currently exist.
	 * @param language The language of the system.
	 * @throws IOException If an IOException occurs during the copying of the original system, or the inventorying of the copied fork.
	 */
	public Fork(Path systemdir, Path forkdir, String language) throws IOException {	
		//Check input validity
		Objects.requireNonNull(systemdir);
		Objects.requireNonNull(forkdir);
		Objects.requireNonNull(language);
		if(!Files.exists(systemdir)) {
			throw new IllegalArgumentException("System directory does not exist.");
		}
		if(!Files.isDirectory(systemdir)) {
			throw new IllegalArgumentException("System directory is not a directory.");
		}
		if(Files.exists(forkdir)) {
			throw new IllegalArgumentException("Fork directory already exists, will not overwrite.");
		}
		
		//Copy the original directory
		FileUtil.copyDirectory(systemdir, forkdir);
		
		//Initialize variant lists
		variants = new LinkedList<Variant>();
		filevariants = new LinkedList<FileVariant>();
		directoryvariants = new LinkedList<DirectoryVariant>();
		
		//Create referenced InventoriedSystem for this fork
		fork = new InventoriedSystem(forkdir, language);
	}
	
	/**
	 * Injects a file into this fork.
	 * @param file A path to the file to inject.
	 * @return A FileVariant describing the injection, or null if a injection site could not be found.
	 * @throws IOException If an IO exception occurs during injection.  An IOException occurring may leave the Fork in a bad state!
	 * @throws IllegalArgumentException If the file path does not point to an existing file.
	 */
	public FileVariant injectFile(Path file) throws IOException {
		//Check input
		if(!Files.exists(file)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		
		//Find a directory to inject this file into
		Path injectin;
		int attempts = 0;
		
		while(true) {
			attempts++;
			if(attempts == MAX_ATTEMPTS) {
				return null;
			}
			injectin = fork.getRandomDirectory();
			if(!Files.exists(Paths.get(injectin.toString(), file.getFileName().toString()))) {
				break;
			}
		}
		
		//Inject the file
		Path injected = Files.copy(file, Paths.get(injectin.toString(), file.getFileName().toString()));
		
		//Make a record of this interaction
		FileVariant v = new FileVariant(file.toAbsolutePath().normalize(), injected.toAbsolutePath().normalize());
		variants.add(v);
		filevariants.add(v);
		
		//Return success
		return v;
	}
	
	/**
	 * Injects a directory into this fork.
	 * @param directory The path to the directory to inject.
	 * @return A DirectoryVariant describing the injection, or null if a injection site could not be found.
	 * @throws IOException If an IO exception occurs during injection.  An IOException occurring may leave the Fork in a bad state!
	 * @throws IllegalArgumentException If the directory path does not point to an existing directory.
	 */
	public DirectoryVariant injectDirectory(Path directory) throws IOException {
		//Check input
		if(!Files.exists(directory)) {
			throw new IllegalArgumentException("Directory does not exist.");
		}
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("Directory is not a directory.");
		}
		
		//Find a directory to inject this directory into
		Path injectin;
		int attempts = 0;
		
		while(true) {
			attempts++;
			if(attempts == MAX_ATTEMPTS) {
				return null;
			}
			injectin = fork.getRandomDirectory();
			if(!Files.exists(Paths.get(injectin.toString(), directory.getFileName().toString()))) {
				break;
			}
		}
		
		//Inject the directory
		FileUtil.copyDirectory(directory, Paths.get(injectin.toString(), directory.getFileName().toString()));
		
		//Make a record of this interaction
		DirectoryVariant v = new DirectoryVariant(directory.toAbsolutePath().normalize(), Paths.get(injectin.toString(), directory.getFileName().toString()).toAbsolutePath().normalize());
		variants.add(v);
		directoryvariants.add(v);
		
		
		//Return success
		return v;
	}
	
	public boolean injectFragment(Fragment fragment) {
		return false;
	}
}
