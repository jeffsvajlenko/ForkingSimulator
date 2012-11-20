package models;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import util.FragmentUtil;
import util.InventoriedSystem;
import util.SystemUtil;


public class Fork {
	
	//Inventory of the fork pre-modification
	private InventoriedSystem fork;
	
	//Variant tracking
	private List<Variant> variants;
	private List<FileVariant> filevariants;
	private List<DirectoryVariant> directoryvariants;
	private List<FragmentVariant> functionfragmentvariants;
	
	//track files modified (in order to prevent modifying the same file twice, which is more difficult to track since line numbers change)
	private List<Path> changedFiles;
	
	//For random
	private Random random;
	
	/**
	 * Returns the variants in an unmodifiable list.  Variants are in the order they were injected.
	 * @return the variants in an unmodifiable list.  Variants are in the order they were injected.
	 */
	public List<Variant> getVariants() {
		return Collections.unmodifiableList(variants);
	}
	
	/**
	 * Returns the file variants in an unmodifiable list.  Variants are in order they were injected.
	 * @return the file variants in an unmodifiable list.  Variants are in order they were injected.
	 */
	public List<FileVariant> getFileVariants() {
		return Collections.unmodifiableList(filevariants);
	}
	
	/**
	 * Returns the directory variants in an unmodifiable list.  Variants are in the order they were injected.
	 * @return the directory variants in an unmodifiable list.  Variants are in the order they were injected.
	 */
	public List<DirectoryVariant> getDirectoryVariants() {
		return Collections.unmodifiableList(directoryvariants);
	}
	
	/**
	 * Returns the fragment variants in an unmodifiable list.  Variants are in the order they were injected.
	 * @return the fragment variants in an unmodifiable list.  Variants are in the order they were injected.
	 */
	public List<FragmentVariant> getFunctionFragmentVariants() {
		return Collections.unmodifiableList(functionfragmentvariants);
	}
	
	/**
	 * Returns a path to the fork (absolute and normalized).
	 * @return a path to the fork.
	 */
	public Path getLocation() {
		return this.fork.getLocation().toAbsolutePath().normalize();
	}
	
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
		FileUtils.copyDirectory(systemdir.toFile(), forkdir.toFile());
		
		//init random
		random = new Random();
		
		//Initialize variant lists
		variants = new LinkedList<Variant>();
		filevariants = new LinkedList<FileVariant>();
		directoryvariants = new LinkedList<DirectoryVariant>();
		functionfragmentvariants = new LinkedList<FragmentVariant>();
		
		//initialize file modification tracker
		changedFiles = new LinkedList<Path>();
		
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
		List<Path> directories = new LinkedList<Path>(this.fork.getDirectories());
		
		while(true) {
			if(directories.isEmpty()) {
				return null;
			} else {
				injectin = directories.remove(random.nextInt(directories.size()));
				if(!Files.exists(Paths.get(injectin.toString(), file.getFileName().toString()))) { //check file does not already exist with same name in the location
					break;
				}
			}
		}
		
		return injectFile(file, injectin);
	}
	
	/**
	 * Injects a file into the fork into a specified directory of the fork.
	 * @param file The file to inject.
	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
	 * @return The file variant.
	 * @throws IOException If an IO error occurs during file copy.  An IOException occurring may leave the Fork in a bad state!
	 * @throws NullPointerException If file or injectin are null.
	 * @throws IllegalArgumnetException If file is invalid (does not exist, is not a regular file), or if injectin is invalid (does not exist, is not a directory, is not in fork).
	 */
	public FileVariant injectFile(Path file, Path injectin) throws IOException, NullPointerException, IllegalArgumentException {
		//check input
		Objects.requireNonNull(file);
		Objects.requireNonNull(injectin);
		injectin = injectin.toAbsolutePath().normalize();
		file = file.toAbsolutePath().normalize();
		if(!Files.exists(file)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		if(!Files.exists(injectin)) {
			throw new IllegalArgumentException("Directory does not exist.");
		}
		if(!Files.isDirectory(injectin)) {
			throw new IllegalArgumentException("directory is not a directory.");
		}
		if(!injectin.startsWith(this.fork.getLocation())) {
			throw new IllegalArgumentException("directory is not from the fork.");
		}
		
		//Check injection will not overwrite anything
		if(Files.exists(injectin.resolve(file.getFileName()))) {
			throw new IllegalArgumentException("Can not inject there, already exists file with same name.");
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
		Objects.requireNonNull(directory);
		if(!Files.exists(directory)) {
			throw new IllegalArgumentException("Directory does not exist.");
		}
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("Directory is not a directory.");
		}
		
		//Find a directory to inject this file into
		Path injectin;
		List<Path> directories = new LinkedList<Path>(this.fork.getDirectories());
		
		while(true) {
			if(directories.isEmpty()) {
				return null;
			} else {
				injectin = directories.remove(random.nextInt(directories.size()));
				if(!Files.exists(Paths.get(injectin.toString(), directory.getFileName().toString()))) { //check file does not already exist with same name in the location
					break;
				}
			}
		}
		
		return injectDirectory(directory, injectin);
	}
	
	/**
	 * Injects a file into the fork into a specified directory of the fork.
	 * @param directory The directory to inject.
	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
	 * @return The directory variant.
	 * @throws IOException If an IO error occurs during file copy.  An IOException occurring may leave the Fork in a bad state!
	 * @throws NullPointerException If directory or injectin are null.
	 * @throws IllegalArgumnetException If directory is invalid (does not exist, is not a directory), or if injectin is invalid (does not exist, is not a directory, is not in fork).
	 */
	public DirectoryVariant injectDirectory(Path directory, Path injectin) throws IOException, NullPointerException, IllegalArgumentException {
		//Check input
		Objects.requireNonNull(directory);
		Objects.requireNonNull(injectin);
		directory = directory.toAbsolutePath().normalize();
		injectin = injectin.toAbsolutePath().normalize();
		if(!Files.exists(directory)) {
			throw new IllegalArgumentException("Directory does not exist.");
		}
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("Directory is not a directory.");
		}
		if(!Files.exists(injectin)) {
			throw new IllegalArgumentException("Directory does not exist.");
		}
		if(!Files.isDirectory(injectin)) {
			throw new IllegalArgumentException("directory is not a directory.");
		}
		if(!injectin.startsWith(this.fork.getLocation())) {
			throw new IllegalArgumentException("directory is not from the fork.");
		}
		
		//Check injection will not overwrite anything
		if(Files.exists(injectin.resolve(directory.getFileName()))) {
			throw new IllegalArgumentException("Can not inject there, already exists file with same name.");
		}
		
		//Inject the directory
		FileUtils.copyDirectory(directory.toFile(), Paths.get(injectin.toString(), directory.getFileName().toString()).toFile());
		
		//Make a record of this interaction
		DirectoryVariant v = new DirectoryVariant(directory.toAbsolutePath().normalize(), Paths.get(injectin.toString(), directory.getFileName().toString()).toAbsolutePath().normalize());
		variants.add(v);
		directoryvariants.add(v);
		
		//Return success
		return v;
		
	}
	
	/**
	 * Injects a function fragment into the fork, after mutation.
	 * @param fragment The fragment to inject.
	 * @param op The operator to mutate the fragment with.
	 * @param numattempts The number of times to attempt mutation before giving up.
	 * @param language The source language.
	 * @return a FragmentVariant describing the variant created, or null if either an injection location can not be found or the mutation is unsuccessful.
	 * @throws IOException If an IOException occurs.  If this occurs, integrity of the Fork is not guaranteed.
	 * @throws InterruptedException If the mutation process is interrupted.
	 */
	public FragmentVariant injectFunctionFragment(FunctionFragment fragment, Operator op, int numattempts, String language) throws IOException, InterruptedException {
	//Check Input
		//Check pointers
		Objects.requireNonNull(fragment);
		Objects.requireNonNull(op);
		
		//Check fragment file
		if(!Files.exists(fragment.getSrcFile())) {
			new NoSuchFileException("Fragment's source file does not exist.");
		}
		if(!Files.isReadable(fragment.getSrcFile())) {
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		if(!Files.isRegularFile(fragment.getSrcFile())) {
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		
	//Check fragment validity
		int numlines = FragmentUtil.countLines(fragment.getSrcFile());
		if(fragment.getEndLine() > numlines) {
			new IllegalArgumentException("Fragment is invalid (endline proceeds ends of file).");
		}
		if(!FragmentUtil.isFunction(fragment, fork.getLanguage())) {
			new IllegalArgumentException("Fragment is invalid (does not specify a function).");
		}
		
	//Get location to inject at
		FunctionFragment f;
		//continue to choose until a suitable location is found, or all exhausted
		while(true) {
			//pick a previously unchosen function fragment at random
			f = fork.getRandomFunctionFragmentNoRepeats();
			
			//if all exhausted, return failure
			if(f == null) {
				return null;
			}
			
			//ensure fragment perfectly frames a function, and that the file it is hasn't previously been modified
			if(FragmentUtil.isFunction(f, fork.getLanguage())) {
				if(!changedFiles.contains(f.getSrcFile().toAbsolutePath().normalize())) {
					//This is the fragment to use, add its file to changed files and continue
					changedFiles.add(f.getSrcFile().toAbsolutePath().normalize());
					break;
				}
			}
		}
		
		//Mutate
			//Create temporary files
		Path tmpfile1 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "injectFunctionFragment", null);
		Path tmpfile2 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "injectFunctionFragment", null);
		
			//extract function to file
		FragmentUtil.extractFragment(fragment, tmpfile1);
		
			//Mutate function
		if(0 != op.performOperator(tmpfile1, tmpfile2, numattempts, language)) {
			Files.deleteIfExists(tmpfile1);
			Files.deleteIfExists(tmpfile2);
			return null;
		}
		
		//Create fragment representation of mutated function
		numlines = FragmentUtil.countLines(tmpfile2);
		FunctionFragment mutatedfragment = new FunctionFragment(tmpfile2, 1, numlines);
		
		//Inject into file
		FragmentUtil.injectFragment(f.getSrcFile(), f.getEndLine()+1, mutatedfragment);
		
		//Make record
		FragmentVariant fv = new FragmentVariant(fragment, new FunctionFragment(f.getSrcFile(), f.getEndLine()+1, (f.getEndLine() + 1) + (numlines - 1)), op);
		variants.add(fv);
		functionfragmentvariants.add(fv);
		
		Files.deleteIfExists(tmpfile1);
		Files.deleteIfExists(tmpfile2);
		
		//Return variant
		return fv;
	}
	
	/**
	 * Injects a function fragment into the fork.
	 * @param fragment The fragment to inject.  Must refer to an existing and readable regular file, fragment lines must be valid with respect to the file.  Source lines must perfectly frame a single function (i.e. no source within the lines except the function in its enterity and comments)
	 * @return A variant describing the change.
	 * @throws NoSuchFileException If the source file specified by the fragment does not exist.
	 * @throws IOException If the IO error occurs.  If this occurs then the integrity of this fork is no longer guarenteed.
	 * @throws NullPointerException If the argument is null.
	 * @throws IllegalArgumentException If the fragment is invalid: source file is invalid (not readable, not a regular file), or if endline proceeds end of source file.
	 */
	public FragmentVariant injectFunctionFragment(FunctionFragment fragment) throws NoSuchFileException, IOException {
	//Check Input
		//Check pointers
		Objects.requireNonNull(fragment);
		
		//Check fragment file
		if(!Files.exists(fragment.getSrcFile())) {
			new NoSuchFileException("Fragment's source file does not exist.");
		}
		if(!Files.isReadable(fragment.getSrcFile())) {
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		if(!Files.isRegularFile(fragment.getSrcFile())) {
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		
	//Check fragment validity
		int numlines = FragmentUtil.countLines(fragment.getSrcFile());
		if(fragment.getEndLine() > numlines) {
			new IllegalArgumentException("Fragment is invalid (endline proceeds ends of file).");
		}
		if(!FragmentUtil.isFunction(fragment, fork.getLanguage())) {
			new IllegalArgumentException("Fragment is invalid (does not specify a function).");
		}
		
	//Get location to inject at
		FunctionFragment f;
		//continue to choose until a suitable location is found, or all exhausted
		while(true) {
			//pick a previously unchosen function fragment at random
			f = fork.getRandomFunctionFragmentNoFileRepeats();
			
			//if all exhausted, return failure
			if(f == null) {
				return null;
			}
			
			//ensure fragment perfectly frames a function
			if(FragmentUtil.isFunction(f, fork.getLanguage())) {
				
				//ensure file has not been previously modified (this shouldn't occur)
				if(!changedFiles.contains(f.getSrcFile().toAbsolutePath().normalize())) {
					
					//This is the fragment to use, add its file to changed files and continue
					changedFiles.add(f.getSrcFile().toAbsolutePath().normalize());
					break;
				}
			}
		}
		
		//Inject into file
		FragmentUtil.injectFragment(f.getSrcFile(), f.getEndLine()+1, fragment);
		
		//Make record
		FragmentVariant fv = new FragmentVariant(fragment, new FunctionFragment(f.getSrcFile(), f.getEndLine()+1, f.getEndLine() + 1 + (fragment.getEndLine()-fragment.getStartLine())), null);
		variants.add(fv);
		functionfragmentvariants.add(fv);
		
		//Return variant
		return fv;
	}
}
