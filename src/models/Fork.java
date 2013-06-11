package models;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import util.FileUtil;
import util.FilenameGenerator;
import util.FragmentUtil;
import util.InventoriedSystem;
import util.SystemUtil;


public class Fork {
	
	//Inventory of the fork pre-modification
	private InventoriedSystem fork;
	
	//Variant tracking
	private List<Variant> variants;
	private List<FileVariant> filevariants;
	private List<LeafDirectoryVariant> directoryvariants;
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
	public List<LeafDirectoryVariant> getDirectoryVariants() {
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
		directoryvariants = new LinkedList<LeafDirectoryVariant>();
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
	
	private Path mutate_file_helper(Path file, Operator operator, int times, int numattempts) throws IOException, InterruptedException, MutationFailedException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(operator);
		if(times < 1) {
			throw new IllegalArgumentException("Operator times must be >= 1.");
		}
		if(numattempts < 1) {
			throw new IllegalArgumentException("Number of attempts must be >= 1.");
		}
		if(!Files.exists(file)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		
		//Create temporary files
		Path swap;
		Path source = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "injectFunctionFragment", null);
		Path target = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "injectFunctionFragment", null);
		
		try {
			Files.copy(file, source, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException | UnsupportedOperationException | SecurityException e) {
			Files.deleteIfExists(source);
			Files.deleteIfExists(target);
			throw e;
		}
		
		//Mutate function
		for(int i = 1; i <= times; i++) {
			//Perform Mutation
			if(0 != operator.performOperator(source, target, numattempts, fork.getLanguage())) {
				Files.deleteIfExists(source);
				Files.deleteIfExists(target);
				throw new MutationFailedException();
			}
			
			//Swap
			swap = source;
			source = target;
			target = swap;
		}
		
		//Get source/target fixed
		swap = source;
		source = target;
		target = swap;
		
		Files.delete(source);
		return target;
	}
	
	/**
	 * Injects a file into this fork, after mutating it a number of times using the specified operator.
	 * @param file Path to the file to inject.
	 * @param operator Operator to use to mutate the file.
	 * @param times The number of times to apply the operator.
	 * @param numattempts The number of times to attempt mutation before giving up.
	 * @param language The source language.
	 * @return A FileVariant describing the injection, or null if a injection site could not be found.
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws MutationFailedException 
	 * @throws InterruptedException 
	 */
	public FileVariant injectFile(Path file, Path rename, Operator operator, int times, int numattempts, String language) throws IllegalArgumentException, NullPointerException, IOException, InterruptedException, MutationFailedException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(operator);
		if(times < 1) {
			throw new IllegalArgumentException("Operator times must be >= 1.");
		}
		if(numattempts < 1) {
			throw new IllegalArgumentException("Number of attempts must be >= 1.");
		}
		if(!Files.exists(file)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		
		Path mutated = mutate_file_helper(file, operator, times, numattempts);
		FileVariant fv = this.injectFile(mutated, rename.getFileName());
		Files.deleteIfExists(mutated);
		
		return new FileVariant(file, fv.getInjectedFile(), operator, times);
	}
	
	/**
	 * Injects a file into this fork, after mutating it a number of times using the specified operator.
	 * @param file Path to the file to inject.
	 * @param operator Operator to use to mutate the file.
	 * @param times The number of times to apply the operator.
	 * @param numattempts The number of times to attempt mutation before giving up.
	 * @param language The source language.
	 * @return A FileVariant describing the injection, or null if a injection site could not be found.
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws MutationFailedException 
	 * @throws InterruptedException 
	 */
	public FileVariant injectFile(Path file, Operator operator, int times, int numattempts, String language) throws IllegalArgumentException, NullPointerException, IOException, InterruptedException, MutationFailedException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(operator);
		if(times < 1) {
			throw new IllegalArgumentException("Operator times must be >= 1.");
		}
		if(numattempts < 1) {
			throw new IllegalArgumentException("Number of attempts must be >= 1.");
		}
		if(!Files.exists(file)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		
		Path mutated = mutate_file_helper(file, operator, times, numattempts);
		FileVariant fv = this.injectFile(mutated, file.getFileName());
		Files.deleteIfExists(mutated);
		
		return new FileVariant(file, fv.getInjectedFile(), operator, times);
	}
	
	/**
	 * Injects a file into this fork, after mutating it a number of times using the specified operator, in the specified directory.
	 * @param file Path to the file to inject.
	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
	 * @param operator Operator to use to mutate the file.
	 * @param times The number of times to apply the operator.
	 * @param numattempts The number of times to attempt mutation before giving up.
	 * @param language The source language.
	 * @return A FileVariant describing the injection, or null if a injection site could not be found.
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws MutationFailedException 
	 * @throws InterruptedException 
	 */
	public FileVariant injectFileAt(Path file, Path renamed, Path injectin, Operator operator, int times, int numattempts, String language) throws IllegalArgumentException, NullPointerException, IOException, InterruptedException, MutationFailedException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(injectin);
		Objects.requireNonNull(operator);
		if(times < 1) {
			throw new IllegalArgumentException("Operator times must be >= 1.");
		}
		if(numattempts < 1) {
			throw new IllegalArgumentException("Number of attempts must be >= 1.");
		}
		if(!Files.exists(file)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		
		Path mutated = mutate_file_helper(file, operator, times, numattempts);
		FileVariant fv = this.injectFileAt(mutated, renamed.getFileName(), injectin);
		
		Files.deleteIfExists(mutated);
		
		return new FileVariant(file, fv.getInjectedFile(), operator, times);
	}
	
	/**
	 * Injects a file into this fork, after mutating it a number of times using the specified operator, in the specified directory.
	 * @param file Path to the file to inject.
	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
	 * @param operator Operator to use to mutate the file.
	 * @param times The number of times to apply the operator.
	 * @param numattempts The number of times to attempt mutation before giving up.
	 * @param language The source language.
	 * @return A FileVariant describing the injection, or null if a injection site could not be found.
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws MutationFailedException 
	 * @throws InterruptedException 
	 */
	public FileVariant injectFileAt(Path file, Path injectin, Operator operator, int times, int numattempts, String language) throws IllegalArgumentException, NullPointerException, IOException, InterruptedException, MutationFailedException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(injectin);
		Objects.requireNonNull(operator);
		if(times < 1) {
			throw new IllegalArgumentException("Operator times must be >= 1.");
		}
		if(numattempts < 1) {
			throw new IllegalArgumentException("Number of attempts must be >= 1.");
		}
		if(!Files.exists(file)) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		
		Path mutated = mutate_file_helper(file, operator, times, numattempts);
		FileVariant fv = this.injectFileAt(mutated, file.getFileName(), injectin);
		
		Files.deleteIfExists(mutated);
		
		return new FileVariant(file, fv.getInjectedFile(), operator, times);
	}
	
	/**
	 * Injects a file into this fork.
	 * @param file A path to the file to inject.
	 * @newName New name for the file.  Only the result of "getFileName()" is used.
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
				if(!Files.exists(Paths.get(injectin.toString(), newName.getFileName().toString()))) { //check file does not already exist with same name in the location
					break;
				}
			}
		}
		
		return injectFileAt(file, newName.getFileName(), injectin);
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
	 * @throws IllegalArgumnetException If file is invalid (does not exist, is not a regular file), or if injectin is invalid (does not exist, is not a directory, is not in fork), or if injection will override existing file.
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
		if(Files.exists(injectin.resolve(newName.getFileName()))) {
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
	
//	/**
//	 * Injects a directory into this fork.
//	 * @param directory The path to the directory to inject.
//	 * @return A DirectoryVariant describing the injection, or null if a injection site could not be found.
//	 * @throws IOException If an IO exception occurs during injection.  An IOException occurring may leave the Fork in a bad state!
//	 * @throws IllegalArgumentException If the directory path does not point to an existing directory.
//	 */
//	public LeafDirectoryVariant injectLeafDirectory(Path directory) throws IOException {
//		//Check input
//		Objects.requireNonNull(directory);
//		if(!Files.exists(directory)) {
//			throw new IllegalArgumentException("Directory does not exist.");
//		}
//		if(!Files.isDirectory(directory)) {
//			throw new IllegalArgumentException("Directory is not a directory.");
//		}
//		
//		return injectLeafDirectory(directory, directory.getFileName());
//	}
//	
//	public LeafDirectoryVariant injectLeafDirectory(Path directory, Operator operator, int times, int attempts, String language) throws IOException, InterruptedException, MutationFailedException {
//		return injectLeafDirectory(directory, directory.getFileName(), operator, times, attempts, language);
//	}
	
	/**
	 * Injects a specified leaf directory into this fork
	 * @param leafDirectory  The leaf directory to inject.
	 * @param injectIn  The directory in this fork to inject the leaf directory into.
	 * @return A LeafDirectoryVariant describing the injection.
	 * @throws FileNotFoundException  If leafDirectory or injectIn do not exist.
	 * @throws IllegalArgumentException  If leafDirectory is not a leaf directory.  If injectIn is not a directory of this fork.
	 * @throws NullPointerException  If leafDirectory, injectIn or operator are null.
	 * @throws IOException  If an IO error occurs during injection.
	 * @throws NoInjectionLocationsException If no injection location could be chosen (no directories in fork).
	 */
	public LeafDirectoryVariant injectLeafDirectory(Path leafDirectory) throws FileNotFoundException, IOException, NoInjectionLocationsException {
		try {
			return this.injectLeafDirectory(leafDirectory, false, null, 1, 1);
		} catch (MutationFailedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, mutation failed when not supposed to be mutating...");
			System.exit(-1);
			return null;
		} catch (RenameFailedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, rename failed when not supposed to be renaming...");
			System.exit(-1);
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, mutation was interrupted when not supposed to be mutating...");
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Injects a specified leaf directory into this fork, with renaming of the leaf directory and its files.
	 * @param leafDirectory  The leaf directory to inject.
	 * @param injectIn  The directory in this fork to inject the leaf directory into.
	 * @return A LeafDirectoryVariant describing the injection.
	 * @throws FileNotFoundException  If leafDirectory or injectIn do not exist.
	 * @throws IllegalArgumentException  If leafDirectory is not a leaf directory.  If injectIn is not a directory of this fork.
	 * @throws NullPointerException  If leafDirectory, injectIn or operator are null.
	 * @throws IOException  If an IO error occurs during injection.
	 * @throws NoInjectionLocationsException If no injection location could be chosen (no directories in fork).
	 * @throws RenameFailedException  If rename fails (could not generate a random name for the directory that did not already exist).
	 */
	public LeafDirectoryVariant injectLeafDirectoryAndRename(Path leafDirectory) throws FileNotFoundException, IOException, RenameFailedException, NoInjectionLocationsException {
		try {
			return this.injectLeafDirectory(leafDirectory, true, null, 1, 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, mutation was interrupted when not supposed to be mutating...");
			System.exit(-1);
			return null;
		} catch (MutationFailedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, mutation failed when not supposed to be mutating...");
			System.exit(-1);
			return null;
		}
	}
	
	/**
	/**
	 * Injects a specified leaf directory into this fork, with mutation of its files.
	 * @param leafDirectory  The leaf directory to inject.
	 * @param injectIn  The directory in this fork to inject the leaf directory into.
	 * @param operator  The operator to mutate the files in the leaf directory with.
	 * @param maxtimes  The maximum number of times to apply the mutator to one of the files.
	 * @param attempts  The number of times to attempt the mutation operator.
	 * @return A LeafDirectoryVariant describing the injection.
	 * @throws FileNotFoundException  If leafDirectory or injectIn do not exist.
	 * @throws IllegalArgumentException  If leafDirectory is not a leaf directory.  If injectIn is not a directory of this fork.  If maxtimes or attempts is <= 0.
	 * @throws NullPointerException  If leafDirectory, injectIn or operator are null.
	 * @throws IOException  If an IO error occurs during injection.
	 * @throws NoInjectionLocationsException If no injection location could be chosen (no directories in fork).
	 * @throws InterruptedException  If the mutation process is interrupted.
	 * @throws MutationFailedException  If the mutation fails.
	 */
	public LeafDirectoryVariant injectLeafDirectoryAndMutate(Path leafDirectory, Operator operator, int times, int attempts) throws FileNotFoundException, IOException, InterruptedException, MutationFailedException, NoInjectionLocationsException {
		try {
			return this.injectLeafDirectory(leafDirectory, false, operator, times, attempts);
		} catch (RenameFailedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, rename failed when not supposed to be renaming...");
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Injects a specified leaf directory into this fork, with renaming of the leaf directory and its files, and mutation of its files.
	 * @param leafDirectory  The leaf directory to inject.
	 * @param injectIn  The directory in this fork to inject the leaf directory into.
	 * @param operator  The operator to mutate the files in the leaf directory with.
	 * @param maxtimes  The maximum number of times to apply the mutator to one of the files.
	 * @param attempts  The number of times to attempt the mutation operator.
	 * @return A LeafDirectoryVariant describing the injection.
	 * @throws FileNotFoundException  If leafDirectory or injectIn do not exist.
	 * @throws IllegalArgumentException  If leafDirectory is not a leaf directory.  If injectIn is not a directory of this fork.  If maxtimes or attempts is <= 0.
	 * @throws NullPointerException  If leafDirectory, injectIn or operator are null.
	 * @throws IOException  If an IO error occurs during injection.
	 * @throws NoInjectionLocationsException If no injection location could be chosen (no directories in fork).
	 * @throws InterruptedException  If the mutation process is interrupted.
	 * @throws MutationFailedException  If the mutation fails.
	 * @throws RenameFailedException  If rename fails (could not generate a random name for the directory that did not already exist).
	 */
	public LeafDirectoryVariant injectLeafDirectoryAndRenameAndMutate(Path leafDirectory, Operator operator, int maxtimes, int attempts) throws FileNotFoundException, IOException, InterruptedException, MutationFailedException, RenameFailedException, NoInjectionLocationsException {
		return this.injectLeafDirectory(leafDirectory, true, operator, maxtimes, attempts);
	}
	
	/**
	 * Injects a specified leaf directory into a specified directory of this fork.
	 * @param leafDirectory  The leaf directory to inject.
	 * @param injectIn  The directory in this fork to inject the leaf directory into.
	 * @param operator  The operator to mutate the files in the leaf directory with.
	 * @param maxtimes  The maximum number of times to apply the mutator to one of the files.
	 * @param attempts  The number of times to attempt the mutation operator.
	 * @return A LeafDirectoryVariant describing the injection.
	 * @throws FileNotFoundException  If leafDirectory or injectIn do not exist.
	 * @throws IllegalArgumentException  If leafDirectory is not a leaf directory.  If injectIn is not a directory of this fork.
	 * @throws NullPointerException  If leafDirectory, injectIn or operator are null.
	 * @throws IOException  If an IO error occurs during injection.
	 */
	public LeafDirectoryVariant injectLeafDirectoryAt(Path leafDirectory, Path injectIn) throws FileNotFoundException, IllegalArgumentException, NullPointerException, IOException {
		try {
			return injectLeafDirectoryAt(leafDirectory, injectIn, false, null, 1, 1);
		} catch (MutationFailedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, mutation failed when not supposed to be mutating...");
			System.exit(-1);
			return null;
		} catch (RenameFailedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, rename failed when not supposed to be renaming...");
			System.exit(-1);
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, mutation was interrupted when not supposed to be mutating...");
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Injects a specified leaf directory into a specified directory of this fork, with renaming of the leaf directory and its files.
	 * @param leafDirectory  The leaf directory to inject.
	 * @param injectIn  The directory in this fork to inject the leaf directory into.
	 * @param operator  The operator to mutate the files in the leaf directory with.
	 * @param maxtimes  The maximum number of times to apply the mutator to one of the files.
	 * @param attempts  The number of times to attempt the mutation operator.
	 * @return A LeafDirectoryVariant describing the injection.
	 * @throws FileNotFoundException  If leafDirectory or injectIn do not exist.
	 * @throws IllegalArgumentException  If leafDirectory is not a leaf directory.  If injectIn is not a directory of this fork.
	 * @throws NullPointerException  If leafDirectory, injectIn or operator are null.
	 * @throws IOException  If an IO error occurs during injection.
	 * @throws RenameFailedException  If rename fails (could not generate a random name for the directory that did not already exist).
	 */
	public LeafDirectoryVariant injectLeafDirectoryAtAndRename(Path leafDirectory, Path injectin) throws FileNotFoundException, IllegalArgumentException, NullPointerException, IOException, RenameFailedException {
		try {
			return injectLeafDirectoryAt(leafDirectory, injectin, true, null, 1, 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, mutation was interrupted when not supposed to be mutating...");
			System.exit(-1);
			return null;
		} catch (MutationFailedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, mutation failed when not supposed to be mutating...");
			System.exit(-1);
			return null;
		}
	}

	/**
	 * Injects a specified leaf directory into a specified directory of this fork with mutation of its files.
	 * @param leafDirectory  The leaf directory to inject.
	 * @param injectIn  The directory in this fork to inject the leaf directory into.
	 * @param operator  The operator to mutate the files in the leaf directory with.
	 * @param maxtimes  The maximum number of times to apply the mutator to one of the files.
	 * @param attempts  The number of times to attempt the mutation operator.
	 * @return A LeafDirectoryVariant describing the injection.
	 * @throws FileNotFoundException  If leafDirectory or injectIn do not exist.
	 * @throws IllegalArgumentException  If leafDirectory is not a leaf directory.  If injectIn is not a directory of this fork.  If maxtimes or attempts is <= 0.
	 * @throws NullPointerException  If leafDirectory, injectIn or operator are null.
	 * @throws IOException  If an IO error occurs during injection.
	 * @throws InterruptedException  If the mutation process is interrupted.
	 * @throws MutationFailedException  If the mutation fails.
	 * @throws RenameFailedException  If rename fails (could not find new names for leaf directory and its files).
	 */
	public LeafDirectoryVariant injectLeafDirectoryAtAndMutate(Path leafDirectory, Path injectin, Operator operator, int maxtimes, int attempts) throws FileNotFoundException, IllegalArgumentException, NullPointerException, IOException, InterruptedException, MutationFailedException {
		try {
			return injectLeafDirectoryAt(leafDirectory, injectin, false, operator, maxtimes, attempts);
		} catch (RenameFailedException e) {
			e.printStackTrace();
			System.out.println("Critical bug, rename failed when not supposed to be renaming...");
			System.exit(-1);
			return null;
		}
	}

	/**
	 * Injects a specified leaf directory into a specified directory of this fork, with renaming of the leaf directory and its files, and mutation of its files.
	 * @param leafDirectory  The leaf directory to inject.
	 * @param injectIn  The directory in this fork to inject the leaf directory into.
	 * @param operator  The operator to mutate the files in the leaf directory with.
	 * @param maxtimes  The maximum number of times to apply the mutator to one of the files.
	 * @param attempts  The number of times to attempt the mutation operator.
	 * @return A LeafDirectoryVariant describing the injection.
	 * @throws FileNotFoundException  If leafDirectory or injectIn do not exist.
	 * @throws IllegalArgumentException  If leafDirectory is not a leaf directory.  If injectIn is not a directory of this fork.  If maxtimes or attempts is <= 0.
	 * @throws NullPointerException  If leafDirectory, injectIn or operator are null.
	 * @throws IOException  If an IO error occurs during injection.
	 * @throws InterruptedException  If the mutation process is interrupted.
	 * @throws MutationFailedException  If the mutation fails.
	 * @throws RenameFailedException  If rename fails (could not generate a random name for the directory that did not already exist).
	 */
	public LeafDirectoryVariant injectLeafDirectoryAtAndRenameAndMutate(Path leafDirectory, Path injectIn,  Operator operator, int maxtimes, int attempts) throws FileNotFoundException, IllegalArgumentException, NullPointerException, IOException, InterruptedException, MutationFailedException, RenameFailedException {
		return injectLeafDirectoryAt(leafDirectory, injectIn, true, operator, maxtimes, attempts);
	}
	
	
	
	private LeafDirectoryVariant injectLeafDirectory(Path leafDirectory, boolean rename, Operator operator, int maxtimes, int attempts) throws FileNotFoundException, IOException, InterruptedException, MutationFailedException, RenameFailedException, NoInjectionLocationsException, IllegalArgumentException, NullPointerException {
		//Limited check, remainder is checked in call to injectLeafDirectoryAt
		Objects.requireNonNull(leafDirectory, "leafDirectory can not be null.");
		
		//Check LeafDirectory Valid
		if(!Files.exists(leafDirectory)) {
			throw new IllegalArgumentException("leafDirectory does not exist.");
		}
		if(!Files.isDirectory(leafDirectory)) {
			throw new IllegalArgumentException("leafDirectory is not a directory.");
		}
		if(!FileUtil.isLeafDirectory(leafDirectory)) {
			throw new IllegalArgumentException("leafDirectory must be a leaf directory.");
		}
		
		//Find a directory to inject this file into
		Path injectin;
		List<Path> directories = new LinkedList<Path>(this.fork.getDirectories());
		
		while(true) {
			if(directories.isEmpty()) {
				throw new NoInjectionLocationsException("This fork contains no directories to inject into.");
			} else {
				injectin = directories.remove(random.nextInt(directories.size()));
				if(!rename) {
					if(!Files.exists(injectin.resolve(leafDirectory.getFileName()))) {
						break;
					} else {
						continue;
					}
				} else {
					break;
				}
			}
		}
		
		return injectLeafDirectoryAt(leafDirectory, injectin, rename, operator, maxtimes, attempts);
	}
	
	/**
	 * Injects the specified leaf directory at the specified injection location using the specified renaming and mutation options.
	 * @param leafDirectory The leaf directory to inject.
	 * @param injectIn The directory to inject into.  Must be in this fork.
	 * @param rename If the directory and its files should be renamed before injection.
	 * @param operator The operator to use for mutation.  Leave null to not mutate.
	 * @param maxtimes The number of times to apply the operator. (Just put to 0 if operator=null).
	 * @param attempts The number of times to attempt the operator. (Just put to 0 if operator=null).
	 * @return
	 * @throws FileNotFoundException If leafDirectory or injectIn do not exist.
	 * @throws IOException If an IO error occurs.
	 * @throws InterruptedException If mutation process is interrupted.
	 * @throws MutationFailedException If mutation was not possible.
	 * @throws RenameFailedException If rename was not possible.
	 * @throws NullPointerException If leafDirectory or injectIn are null.
	 * @throws IllegalArgumentException leafDirectory: if not a leaf directory, injectIn: if not in this fork, times: if <= 0, attempts: if <= 0.  times/attempts only enforced if operator != null
	 */
	private LeafDirectoryVariant injectLeafDirectoryAt(Path leafDirectory, Path injectIn, boolean rename, Operator operator, int maxtimes, int attempts) throws FileNotFoundException, IOException, InterruptedException, MutationFailedException, RenameFailedException, IllegalArgumentException, NullPointerException {
	//Input Validation
		//NotNulls
		Objects.requireNonNull(leafDirectory, "leafDirectory can not be null.");
		Objects.requireNonNull(injectIn, "injectIn can not be null.");
		
		//Check LeafDirectory Valid
		if(!Files.exists(leafDirectory)) {
			throw new FileNotFoundException("leafDirectory does not exist.");
		}
		if(!Files.isDirectory(leafDirectory)) {
			throw new IllegalArgumentException("leafDirectory is not a directory.");
		}
		if(!FileUtil.isLeafDirectory(leafDirectory)) {
			throw new IllegalArgumentException("leafDirectory must be a leaf directory.");
		}
		
		//Check injectIn Valid
		if(!Files.exists(injectIn)) {
			throw new FileNotFoundException("injectIn does not exist.");
		}
		if(!Files.isDirectory(injectIn)) {
			throw new IllegalArgumentException("injectIn is not a directory.");
		}
		if(!injectIn.toAbsolutePath().normalize().startsWith(this.fork.getLocation().toAbsolutePath().normalize())) {
			throw new IllegalArgumentException("injectIn must be in this fork.");
		}
		if(rename == false) { // check injection is possible given filename
			if(Files.exists(injectIn.resolve(leafDirectory.getFileName()))) {
				throw new IllegalArgumentException("leafDirectory can not be injected into injectIn because a file already exists with the same name.");
			}
		}
		
		//Check Operator/Times/Attempts (if mutation)
		if(operator != null) {
			if(maxtimes <= 0) {
				throw new IllegalArgumentException("times must be > 0.");
			}
			if(attempts <= 0) {
				throw new IllegalArgumentException("attempts must be > 0.");
			}
		}
		
	//Do Work
		Path injectDirectory;  //the path of the injected directory (pathToInjectInto/injectedDirectorysName)
		Path tinjectDirectory; //a temporary directory for building hte directory to inject
		List<FileVariant> fvariants = new LinkedList<FileVariant>();
		
		//Create Directory
		Path ldname = leafDirectory.getFileName();
		if(rename) {
			int maxRenameAttempt=1000;
			String newname;
			
			//Generate names until one found that is not already used in injectIn
			do {
				newname = FilenameGenerator.getRandomFilename(5, 15);
				maxRenameAttempt--;
			} while(Files.exists(injectIn.resolve(newname)) && maxRenameAttempt > 0);
			
			//If rename failed within 1000 attempts, give up!
			if(maxRenameAttempt == 0) {
				throw new RenameFailedException("Could not find a name that does not already exist in injectIn (1000 attempts).");
			}
			
			ldname = Paths.get(newname).getFileName();
		}
		injectDirectory = injectIn.resolve(ldname);
		tinjectDirectory = Files.createTempDirectory(SystemUtil.getTemporaryDirectory(), "InjectDirectory");
		tinjectDirectory.toFile().deleteOnExit();
		
		//Create Files
		for(File file : leafDirectory.toFile().listFiles()) {
			//File Paths
			Path ofile = file.toPath(); //The original file
			Path ifile = ofile; //The file to inject
			Path tfile = injectDirectory.resolve(ofile.getFileName()); //The target (injected) file
			Path ttfile = tinjectDirectory.resolve(ofile.getFileName());
			int times=0;
			
			//if mutate, change file to inject to mutated version
			if(operator!=null) {
				times = random.nextInt(maxtimes)+1;
				ifile = this.mutate_file_helper(ofile, operator, times, attempts);
			}
			
			//if rename, change target file
			if(rename) {
				int maxRenameAttempt=1000;
				do {
					maxRenameAttempt--;
					String rfilename = FilenameGenerator.getRandomFilenameWithExtention(5, 15, fork.getLanguage());
					tfile = injectDirectory.resolve(rfilename);
					ttfile = tinjectDirectory.resolve(rfilename);
				} while(Files.exists(tfile) && maxRenameAttempt > 0);
				if(maxRenameAttempt == 0) {
					throw new RenameFailedException("Could not find a name to rename one of the files to (1000 attempts).");
				}
			}
			
			//inject
			Files.copy(ifile, ttfile);
			
			//cleanup
			if(ifile != ofile) {
				Files.delete(ifile);
			}
			
			//create variant
			if(operator == null) {
				fvariants.add(new FileVariant(ofile, tfile));
			} else {
				fvariants.add(new FileVariant(ofile, tfile, operator, times));
			}
		}
		
		//inject built directory
		FileUtils.moveDirectory(tinjectDirectory.toFile(), injectDirectory.toFile());
		
		return new LeafDirectoryVariant(leafDirectory, injectDirectory, fvariants);
	}
	
//	public LeafDirectoryVariant injectLeafDirectoryAt(Path directory, Path injectin, Path rename, Operator operator, int times, int attempts, String language) throws FileNotFoundException, IOException, InterruptedException, MutationFailedException {
//		//Check Input
//		Objects.requireNonNull(directory);
//		Objects.requireNonNull(operator);
//		Objects.requireNonNull(language);
//		Objects.requireNonNull(rename);
//		if(!Files.exists(directory)) {
//			throw new IllegalArgumentException("directory does not exist.");
//		}
//		if(!Files.isDirectory(directory)) {
//			throw new IllegalArgumentException("directory is not a directory.");
//		}
//		if(!FileUtil.isLeafDirectory(directory)) {
//			throw new IllegalArgumentException("directory must be a leaf directory.");
//		}
//		if(times <= 0) {
//			throw new IllegalArgumentException("times must be > 0.");
//		}
//		if(attempts <= 0) {
//			throw new IllegalArgumentException("attempts must be > 0.");
//		}
//		
//		//Mutate into a temporary directory
//		Path cdir = Files.createTempDirectory(SystemUtil.getTemporaryDirectory(), null);
//		File files[] = directory.toFile().listFiles();
//		for(File f : files) {
//			Path pfile = f.toPath();
//			Path mutated = this.mutate_file_helper(pfile, operator, times, attempts);
//			Files.copy(mutated, cdir.resolve(pfile.getFileName()));
//		}
//		
//		//Inject mutated copy
//		LeafDirectoryVariant ldv = this.injectLeafDirectoryAt(directory, rename, injectin);
//		
//		//Return fixed variant
//		List<FileVariant> fvs = new LinkedList<FileVariant>();
//		for(FileVariant fv : ldv.getFileVariants()) {
//			fvs.add(new FileVariant(cdir, fv.getInjectedFile(), operator, times));
//		}
//		
//		return new LeafDirectoryVariant(directory, ldv.getInjectedDirectory(), fvs);
//	}
//	
//	public LeafDirectoryVariant injectLeafDirectory(Path directory, Path rename, Operator operator, int times, int attempts, String language) throws IOException, InterruptedException, MutationFailedException {
//		//Check Input
//		Objects.requireNonNull(directory);
//		Objects.requireNonNull(operator);
//		Objects.requireNonNull(language);
//		Objects.requireNonNull(rename);
//		if(!Files.exists(directory)) {
//			throw new IllegalArgumentException("directory does not exist.");
//		}
//		if(!Files.isDirectory(directory)) {
//			throw new IllegalArgumentException("directory is not a directory.");
//		}
//		if(!FileUtil.isLeafDirectory(directory)) {
//			throw new IllegalArgumentException("directory must be a leaf directory.");
//		}
//		if(times <= 0) {
//			throw new IllegalArgumentException("times must be > 0.");
//		}
//		if(attempts <= 0) {
//			throw new IllegalArgumentException("attempts must be > 0.");
//		}
//		
//		//Mutate into a temporary directory
//		Path cdir = Files.createTempDirectory(SystemUtil.getTemporaryDirectory(), null);
//		File files[] = directory.toFile().listFiles();
//		for(File f : files) {
//			Path pfile = f.toPath();
//			Path mutated = this.mutate_file_helper(pfile, operator, times, attempts);
//			Files.copy(mutated, cdir.resolve(pfile.getFileName()));
//		}
//		
//		//Inject mutated copy
//		LeafDirectoryVariant ldv = this.injectLeafDirectory(directory, rename);
//		
//		//Return fixed variant
//		List<FileVariant> fvs = new LinkedList<FileVariant>();
//		for(FileVariant fv : ldv.getFileVariants()) {
//			fvs.add(new FileVariant(cdir, fv.getInjectedFile(), operator, times));
//		}
//		
//		return new LeafDirectoryVariant(directory, ldv.getInjectedDirectory(), fvs);
//	}
//	
//	/**
//	 * Injects a directory into this fork.
//	 * @param directory The path to the directory to inject.
//	 * @param newName The new name for the injected directory. If path contains multiple elements, rightmost is used.
//	 * @return A DirectoryVariant describing the injection, or null if a injection site could not be found.
//	 * @throws IOException If an IO exception occurs during injection.  An IOException occurring may leave the Fork in a bad state!
//	 * @throws IllegalArgumentException If the directory path does not point to an existing directory.
//	 */
//	public LeafDirectoryVariant injectLeafDirectory(Path directory, Path newName) throws IOException {
//		//Check input
//		Objects.requireNonNull(directory);
//		Objects.requireNonNull(newName);
//		if(!Files.exists(directory)) {
//			throw new IllegalArgumentException("Directory does not exist.");
//		}
//		if(!Files.isDirectory(directory)) {
//			throw new IllegalArgumentException("Directory is not a directory.");
//		}
//		if(!FileUtil.isLeafDirectory(directory)) {
//			throw new IllegalArgumentException("Directory must be a leaf directory.");
//		}
//		
//		//Find a directory to inject this file into
//		Path injectin;
//		List<Path> directories = new LinkedList<Path>(this.fork.getDirectories());
//		
//		while(true) {
//			if(directories.isEmpty()) {
//				return null;
//			} else {
//				injectin = directories.remove(random.nextInt(directories.size()));
//				if(!Files.exists(Paths.get(injectin.toString(), newName.getFileName().toString()))) { //check file does not already exist with same name in the location
//					break;
//				}
//			}
//		}
//		
//		return injectLeafDirectoryAt(directory, newName.getFileName(), injectin);
//	}
//	
//	/**
//	 * Injects a directory into the fork into a specified directory of the fork.
//	 * @param directory The directory to inject.
//	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
//	 * @param newName The new name for the injected directory. If path contains multiple elements, rightmost is used.
//	 * @return The directory variant.
//	 * @throws IOException If an IO error occurs during file copy.  An IOException occurring may leave the Fork in a bad state!
//	 * @throws NullPointerException If directory or injectin are null.
//	 * @throws IllegalArgumnetException If directory is invalid (does not exist, is not a directory), or if injectin is invalid (does not exist, is not a directory, is not in fork).
//	 */
//	public LeafDirectoryVariant injectLeafDirectoryAt(Path directory, Path injectin) throws IOException, NullPointerException, IllegalArgumentException {
//		//Input is checked by next call
//		return injectLeafDirectoryAt(directory, directory.getFileName(), injectin);
//	}
//	
//	/**
//	 * Injects a directory into the fork into a specified directory of the fork.
//	 * @param directory The directory to inject.
//	 * @param injectin The directory to inject into.  Must be in the fork (has fork as root directory).
//	 * @param newName The new name for the injected directory. If path contains multiple elements, rightmost is used.
//	 * @return The directory variant.
//	 * @throws IOException If an IO error occurs during file copy.  An IOException occurring may leave the Fork in a bad state!
//	 * @throws NullPointerException If directory or injectin are null.
//	 * @throws IllegalArgumnetException If directory is invalid (does not exist, is not a directory), or if injectin is invalid (does not exist, is not a directory, is not in fork).
//	 */
//	public LeafDirectoryVariant injectLeafDirectoryAt(Path directory, Path newName, Path injectin) throws IOException, NullPointerException, IllegalArgumentException {
//		//Normalize Input
//		directory = directory.toAbsolutePath().normalize();
//		newName = newName.getFileName();
//		injectin = injectin.toAbsolutePath().normalize();
//		
//		
//		//Check input
//		Objects.requireNonNull(directory);
//		if(!Files.exists(directory)) {
//			throw new IllegalArgumentException("Directory does not exist.");
//		}
//		if(!Files.isDirectory(directory)) {
//			throw new IllegalArgumentException("Directory is not a directory.");
//		}
//		Objects.requireNonNull(injectin);
//		if(!Files.exists(injectin)) {
//			throw new IllegalArgumentException("Inject Directory does not exist.");
//		}
//		if(!Files.isDirectory(injectin)) {
//			throw new IllegalArgumentException("Inject Directory is not a directory.");
//		}
//		Objects.requireNonNull(newName);
//		if(!FileUtil.isLeafDirectory(directory)) {
//			throw new IllegalArgumentException("Directory must be a leaf directory.");
//		}
//		if(!Files.exists(directory)) {
//			throw new IllegalArgumentException("Directory does not exist.");
//		}
//		if(!Files.isDirectory(directory)) {
//			throw new IllegalArgumentException("Directory is not a directory.");
//		}
//		if(!Files.exists(injectin)) {
//			throw new IllegalArgumentException("Directory does not exist.");
//		}
//		if(!Files.isDirectory(injectin)) {
//			throw new IllegalArgumentException("directory is not a directory.");
//		}
//		if(!injectin.startsWith(this.fork.getLocation())) {
//			throw new IllegalArgumentException("directory is not from the fork.");
//		}
//		//Check injection will not overwrite anything
//		if(Files.exists(injectin.resolve(newName.getFileName()))) {
//			throw new IllegalArgumentException("Can not inject there, already exists file with same name.");
//		}
//		
//		//Inject the directory
//		FileUtils.copyDirectory(directory.toFile(), Paths.get(injectin.toString(), newName.getFileName().toString()).toFile());
//		
//		//Make a record of this interaction
//		LinkedList<FileVariant> fvs = new LinkedList<FileVariant>();
//		File files[] = directory.toFile().listFiles();
//		for(File file : files) {
//			fvs.add(new FileVariant(Paths.get(file.getAbsolutePath().toString()), Paths.get(injectin.toAbsolutePath().normalize().toString() + "/" + file.getName())));
//		}
//		LeafDirectoryVariant v = new LeafDirectoryVariant(directory.toAbsolutePath().normalize(), Paths.get(injectin.toString(), newName.getFileName().toString()).toAbsolutePath().normalize(), fvs);
//		variants.add(v);
//		directoryvariants.add(v);
//		
//		//Return success
//		return v;
//	}
	
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
		FragmentVariant fv = injectFunctionFragment_helper(mutatedfragment, injectafter, op, 1); //TODO change if times changes
		
		//Cleanup
		Files.delete(tmpfile1);
		Files.delete(tmpfile2);
		
		//Return variant
		return fv;
	}
	
	public FragmentVariant injectFunctionFragment(FunctionFragment fragment, FunctionFragment injectafter) throws FileNotFoundException, IOException {
		return injectFunctionFragment_helper(fragment, injectafter, null, 0);
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
	private FragmentVariant injectFunctionFragment_helper(FunctionFragment fragment, FunctionFragment injectafter, Operator op, int times) throws FileNotFoundException, IOException, IllegalArgumentException {
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
		FragmentVariant fv;
		if(op == null) {
			fv = new FragmentVariant(fragment, new FunctionFragment(injectafter.getSrcFile(), injectafter.getEndLine()+1, injectafter.getEndLine() + 1 + (fragment.getEndLine()-fragment.getStartLine())));
		} else {
			fv = new FragmentVariant(fragment, new FunctionFragment(injectafter.getSrcFile(), injectafter.getEndLine()+1, injectafter.getEndLine() + 1 + (fragment.getEndLine()-fragment.getStartLine())), op, times);
		}
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
		return injectFunctionFragment_helper(fragment, injectafter, op, 1);
	}
}