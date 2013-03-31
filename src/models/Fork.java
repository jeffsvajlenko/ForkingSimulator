package models;
import java.io.FileNotFoundException;
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
import java.util.Set;
import java.util.TreeSet;

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
	private Set<Path> changedFiles;
	
	//For random
	private Random random;
	
	/**
	 * @return An unmodifiable list of the original (not injected) files in this fork.  Paths are absolute and normalized.
	 */
	public List<Path> getOriginalFiles() {
		return fork.getFiles();
	}
	
	/**
	 * @return An unmodifiable list of the original (not injected) directories in this fork.  Paths are absolute and normalized.
	 */
	public List<Path> getOriginalDirectories() {
		return fork.getDirectories();
	}
	
	/**
	 * @return An unmodifiable list of the original (not injected) leaf directories in this fork.  Paths are absolute and normalized.
	 */
	public List<Path> getOriginalLeafDirectories() {
		return fork.getLeafDirectories();
	}
	
	/**
	 * @return A unmodified list of the changed (by injection) original files in this fork.  List does not stay up to date.  Paths are abolsute and normalized.
	 */
	public Set<Path> getOriginalModifiedFiles() {
		return Collections.unmodifiableSet(new TreeSet<Path>(this.changedFiles));
	}
	
	/**
	 * Returns if a original (not injected) file from the fork has been modified by an injection.
	 * @param file Original file from the fork to check if has been modified by an injection.
	 * @return If original file has been modified.
	 * @throws NullPointerException If file is null.
	 * @throws IllegalArgumentException If file is not original.
	 */
	public boolean isOriginalFileModified(Path file) throws NullPointerException, IllegalArgumentException{
		Objects.requireNonNull(file);
		if(!fork.getFiles().contains(file)) {
			throw new IllegalArgumentException("File is not original.");
		}
		return changedFiles.contains(file.toAbsolutePath().normalize());
	}
	
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
		changedFiles = new TreeSet<Path>();
		
		//Create referenced InventoriedSystem for this fork
		fork = new InventoriedSystem(forkdir, language);
	}
	
	/**
	 * Injects a file into this fork.
	 * @param file A path to the file to inject.
	 * @return A FileVariant describing the injection, or null if a injection site could not be found.
	 * @throws IOException If an IO exception occurs during injection.  An IOException occurring may leave the Fork in a bad state!
	 * @throws IllegalArgumentException If the file path does not point to an existing file.
	 * @throws NullPointerException If any of the arguments are null.
	 */
	public FileVariant injectFile(Path file) throws IOException, IllegalArgumentException, NullPointerException {
		return injectFile(file, file.getFileName());
	}
	
	/**
	 * Injects a file into this fork.
	 * @param file A path to the file to inject.
	 * @return A FileVariant describing the injection, or null if a injection site could not be found.
	 * @throws IOException If an IO exception occurs during injection.  An IOException occurring may leave the Fork in a bad state!
	 * @throws IllegalArgumentException If the file path does not point to an existing file.
	 * @throws NullPointerException If any of the arguments are null.
	 */
	public FileVariant injectFile(Path file, Path newName) throws IOException, IllegalArgumentException, NullPointerException {
		//Check input
		Objects.requireNonNull(file);
		Objects.requireNonNull(newName);
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
		
		return injectFileAt(file, newName, injectin);
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
	public FileVariant injectFileAt(Path file, Path injectin) throws IOException, NullPointerException, IllegalArgumentException {
		return injectFileAt(file, file.getFileName(), injectin);
	}
	
	/**
	 * Injects a file into the fork into a specified directory of the fork.
	 * @param file The file to inject.
	 * @param newName The new name for the injected file. If path contains multiple elements, rightmost is used.
	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
	 * @return The file variant.
	 * @throws IOException If an IO error occurs during file copy.  An IOException occurring may leave the Fork in a bad state!
	 * @throws NullPointerException If file or injectin are null.
	 * @throws IllegalArgumnetException If file is invalid (does not exist, is not a regular file), or if injectin is invalid (does not exist, is not a directory, is not in fork).
	 */
	public FileVariant injectFileAt(Path file, Path newName, Path injectin) throws IOException, NullPointerException, IllegalArgumentException {
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
		Objects.requireNonNull(newName);
		
		//Check injection will not overwrite anything
		if(Files.exists(injectin.resolve(file.getFileName()))) {
			throw new IllegalArgumentException("Can not inject there, already exists file with same name.");
		}
		
		//Inject the file
		Path injected = Files.copy(file, Paths.get(injectin.toString(), newName.getFileName().toString()));
				
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
		
		return injectDirectory(directory, directory.getFileName());
	}
	
	/**
	 * Injects a directory into this fork.
	 * @param directory The path to the directory to inject.
	 * @param newName The new name for the injected directory. If path contains multiple elements, rightmost is used.
	 * @return A DirectoryVariant describing the injection, or null if a injection site could not be found.
	 * @throws IOException If an IO exception occurs during injection.  An IOException occurring may leave the Fork in a bad state!
	 * @throws IllegalArgumentException If the directory path does not point to an existing directory.
	 */
	public DirectoryVariant injectDirectory(Path directory, Path newName) throws IOException {
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
		
		return injectDirectoryAtInjectionLocation(directory, newName, injectin);
	}
	
	/**
	 * Injects a directory into the fork into a specified directory of the fork.
	 * @param directory The directory to inject.
	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
	 * @param newName The new name for the injected directory. If path contains multiple elements, rightmost is used.
	 * @return The directory variant.
	 * @throws IOException If an IO error occurs during file copy.  An IOException occurring may leave the Fork in a bad state!
	 * @throws NullPointerException If directory or injectin are null.
	 * @throws IllegalArgumnetException If directory is invalid (does not exist, is not a directory), or if injectin is invalid (does not exist, is not a directory, is not in fork).
	 */
	public DirectoryVariant injectDirectoryAtInjectionLocation(Path directory, Path injectin) throws IOException, NullPointerException, IllegalArgumentException {
		//Check input
		Objects.requireNonNull(directory);
		if(!Files.exists(directory)) {
			throw new IllegalArgumentException("Directory does not exist.");
		}
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("Directory is not a directory.");
		}
		Objects.requireNonNull(injectin);
		if(!Files.exists(injectin)) {
			throw new IllegalArgumentException("Inject Directory does not exist.");
		}
		if(!Files.isDirectory(injectin)) {
			throw new IllegalArgumentException("Inject Directory is not a directory.");
		}
		
		return injectDirectoryAtInjectionLocation(directory, directory.getFileName(), injectin);
	}
	
	/**
	 * Injects a directory into the fork into a specified directory of the fork.
	 * @param directory The directory to inject.
	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
	 * @param newName The new name for the injected directory. If path contains multiple elements, rightmost is used.
	 * @return The directory variant.
	 * @throws IOException If an IO error occurs during file copy.  An IOException occurring may leave the Fork in a bad state!
	 * @throws NullPointerException If directory or injectin are null.
	 * @throws IllegalArgumnetException If directory is invalid (does not exist, is not a directory), or if injectin is invalid (does not exist, is not a directory, is not in fork).
	 */
	public DirectoryVariant injectDirectoryAtInjectionLocation(Path directory, Path newName, Path injectin) throws IOException, NullPointerException, IllegalArgumentException {
		//Check input
		Objects.requireNonNull(directory);
		if(!Files.exists(directory)) {
			throw new IllegalArgumentException("Directory does not exist.");
		}
		if(!Files.isDirectory(directory)) {
			throw new IllegalArgumentException("Directory is not a directory.");
		}
		Objects.requireNonNull(injectin);
		if(!Files.exists(injectin)) {
			throw new IllegalArgumentException("Inject Directory does not exist.");
		}
		if(!Files.isDirectory(injectin)) {
			throw new IllegalArgumentException("Inject Directory is not a directory.");
		}
		Objects.requireNonNull(newName);
		
		
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
		FileUtils.copyDirectory(directory.toFile(), Paths.get(injectin.toString(), newName.getFileName().toString()).toFile());
		
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
	 * @throws MutationFailedException 
	 */
	public FragmentVariant injectFunctionFragment(FunctionFragment fragment, Operator op, int numattempts, String language) throws IOException, InterruptedException, MutationFailedException {
	//Check Input
		//Check pointers
		Objects.requireNonNull(fragment);
		Objects.requireNonNull(op);
		
		//Check fragment file
		if(!Files.exists(fragment.getSrcFile())) { //exists
			new NoSuchFileException("Fragment's source file does not exist.");
		}
		if(!Files.isReadable(fragment.getSrcFile())) { //readable
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		if(!Files.isRegularFile(fragment.getSrcFile())) { //regular file
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		int numlines = FragmentUtil.countLines(fragment.getSrcFile()); //fragment slice valid
		if(fragment.getEndLine() > numlines) {
			new IllegalArgumentException("Fragment is invalid (endline proceeds ends of file).");
		}
		if(!FragmentUtil.isFunction(fragment, fork.getLanguage())) { //is a function
			new IllegalArgumentException("Fragment is invalid (does not specify a function).");
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
			throw new MutationFailedException();
		}
		
		//Create fragment representation of mutated function
		numlines = FragmentUtil.countLines(tmpfile2);
		FunctionFragment mutatedfragment = new FunctionFragment(tmpfile2, 1, numlines);
		
		//Perform Injection, collect variant
		FragmentVariant fv = injectFunctionFragment_helper(mutatedfragment, op);
		
		//Cleanup
		Files.delete(tmpfile1);
		Files.delete(tmpfile2);
		
		//Return variant
		return fv;
	}
	
	public FragmentVariant injectFunctionFragment(FunctionFragment fragment, FunctionFragment injectafter, Operator op, int numattempts, String language) throws IOException, InterruptedException, MutationFailedException {
	//Check Input
		//Check pointers
		Objects.requireNonNull(fragment);
		Objects.requireNonNull(injectafter);
		Objects.requireNonNull(op);
		
		//Normalize input
		fragment = new FunctionFragment(fragment.getSrcFile().toAbsolutePath().normalize(), fragment.getStartLine(), fragment.getEndLine());
		injectafter = new FunctionFragment(injectafter.getSrcFile().toAbsolutePath().normalize(), injectafter.getStartLine(), injectafter.getEndLine());
		
		//Check fragment file
		if(!Files.exists(fragment.getSrcFile())) { //exists
			new NoSuchFileException("Fragment's source file does not exist.");
		}
		if(!Files.isReadable(fragment.getSrcFile())) { //readable
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		if(!Files.isRegularFile(fragment.getSrcFile())) { //regular file
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		int numlines = FragmentUtil.countLines(fragment.getSrcFile()); //fragment slice valid
		if(fragment.getEndLine() > numlines) {
			new IllegalArgumentException("Fragment is invalid (endline proceeds ends of file).");
		}
		if(!FragmentUtil.isFunction(fragment, fork.getLanguage())) { //is a function
			new IllegalArgumentException("Fragment is invalid (does not specify a function).");
		}
		
		//Check Inject AFter
		if(!Files.exists(injectafter.getSrcFile())) { //srcfile exists
			new NoSuchFileException("injectafter source file does not exist.");
		}
		if(!Files.isReadable(injectafter.getSrcFile())) { //srcfile readable
			new IllegalArgumentException("injectafter source file is not readable.");
		}
		if(!Files.isRegularFile(injectafter.getSrcFile())) { //srcfile regular file
			new IllegalArgumentException("injectafter source file is not readable.");
		}
		numlines = FragmentUtil.countLines(injectafter.getSrcFile()); //fragment slice valid
		if(injectafter.getEndLine() > numlines) {
			new IllegalArgumentException("injectafter is invalid (endline proceeds ends of file).");
		}
		if(!FragmentUtil.isFunction(injectafter, fork.getLanguage())) { //is a function
			new IllegalArgumentException("injectafter is invalid (does not specify a function).");
		}
		if(!injectafter.getSrcFile().startsWith(fork.getLocation().toAbsolutePath().normalize())) {
			new IllegalArgumentException("injectafter source file is not from the fork.");
		}
		if(!fork.getFiles().contains(injectafter.getSrcFile())) {
			new IllegalArgumentException("injectafter source file is not from the fork.");
		}
		if(!changedFiles.contains(injectafter.getSrcFile())) {
			new IllegalArgumentException("injectafter source file has already been modified.");
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
			throw new MutationFailedException();
		}
		
	//Create fragment representation of mutated function
		numlines = FragmentUtil.countLines(tmpfile2);
		FunctionFragment mutatedfragment = new FunctionFragment(tmpfile2, 1, numlines);
		
	//Perform Injection, collect variant
		FragmentVariant fv = injectFunctionFragment_helper(mutatedfragment, injectafter, op);
		
		//Cleanup
		Files.delete(tmpfile1);
		Files.delete(tmpfile2);
		
		//Return variant
		return fv;
	}
	
	public FragmentVariant injectFunctionFragment(FunctionFragment fragment, FunctionFragment injectafter) throws FileNotFoundException, IOException {
		return injectFunctionFragment_helper(fragment, injectafter, null);
	}
	
	/**
	 * Helper function.  Injects specified fragment at specific location with the specified operator as part of the returned variant.
	 * All inject function fragments eventually call this.  This upkeeps the changed files list.
	 * @param fragment
	 * @param injectafter
	 * @param op
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private FragmentVariant injectFunctionFragment_helper(FunctionFragment fragment, FunctionFragment injectafter, Operator op) throws FileNotFoundException, IOException, IllegalArgumentException {
		//Check not null
		Objects.requireNonNull(fragment);
		Objects.requireNonNull(injectafter);
		
		//Normalize input
		fragment = new FunctionFragment(fragment.getSrcFile().toAbsolutePath().normalize(), fragment.getStartLine(), fragment.getEndLine());
		injectafter = new FunctionFragment(injectafter.getSrcFile().toAbsolutePath().normalize(), injectafter.getStartLine(), injectafter.getEndLine());
		
		//Check Fragment
		if(!Files.exists(fragment.getSrcFile())) { //srcfile exists
			new NoSuchFileException("Fragment's source file does not exist.");
		}
		if(!Files.isReadable(fragment.getSrcFile())) { //srcfile readable
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		if(!Files.isRegularFile(fragment.getSrcFile())) { //srcfile regular file
			new IllegalArgumentException("Fragment's source file is not readable.");
		}
		int numlines = FragmentUtil.countLines(fragment.getSrcFile()); //fragment slice valid
		if(fragment.getEndLine() > numlines) {
			new IllegalArgumentException("Fragment is invalid (endline proceeds ends of file).");
		}
		if(!FragmentUtil.isFunction(fragment, fork.getLanguage())) { //is a function
			new IllegalArgumentException("Fragment is invalid (does not specify a function).");
		}
		
		//Check inject after
		if(!Files.exists(injectafter.getSrcFile())) { //srcfile exists
			new NoSuchFileException("injectafter source file does not exist.");
		}
		if(!Files.isReadable(injectafter.getSrcFile())) { //srcfile readable
			new IllegalArgumentException("injectafter source file is not readable.");
		}
		if(!Files.isRegularFile(injectafter.getSrcFile())) { //srcfile regular file
			new IllegalArgumentException("injectafter source file is not readable.");
		}
		numlines = FragmentUtil.countLines(injectafter.getSrcFile()); //fragment slice valid
		if(injectafter.getEndLine() > numlines) {
			new IllegalArgumentException("injectafter is invalid (endline proceeds ends of file).");
		}
		if(!FragmentUtil.isFunction(injectafter, fork.getLanguage())) { //is a function
			new IllegalArgumentException("injectafter is invalid (does not specify a function).");
		}
		if(!injectafter.getSrcFile().startsWith(fork.getLocation().toAbsolutePath().normalize())) {
			new IllegalArgumentException("injectafter source file is not from the fork.");
		}
		if(!fork.getFiles().contains(injectafter.getSrcFile())) {
			new IllegalArgumentException("injectafter source file is not from the fork.");
		}
		if(!changedFiles.contains(injectafter.getSrcFile())) {
			new IllegalArgumentException("injectafter source file has already been modified.");
		}
		
		//Update Changed Files
		if(false == changedFiles.add(injectafter.getSrcFile())) {
			new IllegalArgumentException("injectafter source file has already been modified.");
		}
		
		//Inject into file
		FragmentUtil.injectFragment(injectafter.getSrcFile(), injectafter.getEndLine()+1, fragment);
				
		//Make record
		FragmentVariant fv = new FragmentVariant(fragment, new FunctionFragment(injectafter.getSrcFile(), injectafter.getEndLine()+1, injectafter.getEndLine() + 1 + (fragment.getEndLine()-fragment.getStartLine())), op);
		variants.add(fv);
		functionfragmentvariants.add(fv);
				
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
	public FragmentVariant injectFunctionFragment(FunctionFragment fragment) throws NoSuchFileException, IllegalArgumentException, IOException {
		return injectFunctionFragment_helper(fragment, null);
	}
	
	private FragmentVariant injectFunctionFragment_helper(FunctionFragment fragment, Operator op) throws NoSuchFileException, IllegalArgumentException, IOException {
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
		FunctionFragment injectafter;
		//continue to choose until a suitable location is found, or all exhausted
		while(true) {
			//pick a previously unchosen function fragment at random
			injectafter = fork.getRandomFunctionFragmentNoFileRepeats();
			
			//if all exhausted, return failure
			if(injectafter == null) {
				return null;
			}
			
			//ensure fragment perfectly frames a function
			if(!changedFiles.contains(injectafter.getSrcFile().toAbsolutePath().normalize())) {
				
				//ensure file has not been previously modified (this shouldn't occur)
				if(FragmentUtil.isFunction(injectafter, fork.getLanguage())) {
					break;
				}
			}
		}
	
	//Inject and return
		return injectFunctionFragment_helper(fragment, injectafter, op);
	}
}