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

import main.OperatorChooser;
import main.Properties;

import org.apache.commons.io.FileUtils;

import util.FileUtil;
import util.FilenameGenerator;
import util.FragmentUtil;
import util.InventoriedSystem;
import util.SystemUtil;


public class Fork {
	
	Properties properties;
	
	//Inventory of the fork pre-modification
	private InventoriedSystem fork;
	
	//Variant tracking
	private List<Variant> variants;
	private List<FileVariant> filevariants;
	private List<LeafDirectoryVariant> directoryvariants;
	private List<FunctionVariant> functionfragmentvariants;
	
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
	public List<FunctionVariant> getFunctionFragmentVariants() {
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
	public Fork(Path systemdir, Path forkdir, String language, Properties properties) throws IOException {	
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
		
		//properties
		this.properties = properties;
		
		//Initialize variant lists
		variants = new LinkedList<Variant>();
		filevariants = new LinkedList<FileVariant>();
		directoryvariants = new LinkedList<LeafDirectoryVariant>();
		functionfragmentvariants = new LinkedList<FunctionVariant>();
		
		//initialize file modification tracker
		changedFiles = new TreeSet<Path>();
		
		//Create referenced InventoriedSystem for this fork
		fork = new InventoriedSystem(forkdir, language);
	}
	
	private Path mutate_function_helper(Fragment function, Operator operator, int times, int numattempts) throws IOException, InterruptedException, MutationFailedException {
		Path extracted = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "injectFunctionFragment", null);
		Path mutated;
		FragmentUtil.extractFragment(function, extracted);
		
		try {
			mutated = mutate_file_helper(extracted, operator, times, numattempts);
		} catch (InterruptedException | MutationFailedException e) {
			Files.deleteIfExists(extracted);
			throw e;
		}
		
		//cleanup
		Files.deleteIfExists(extracted);
		
		return mutated;
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
	
	public FileVariant injectFile(Path file) throws IllegalArgumentException, IOException, InterruptedException {
		Objects.requireNonNull(file);
		if(!Files.exists(file)) {
			throw new FileNotFoundException("File does not exist.");
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
		
		return injectFileAt(file, injectin);
	}
	
	public FileVariant injectFileAt(Path file, Path injectin) throws IllegalArgumentException, IOException, InterruptedException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(injectin);
		if(!Files.exists(file)) {
			throw new FileNotFoundException("File does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		if(!Files.exists(injectin)) {
			throw new FileNotFoundException("injectin does not exist.");
		}
		if(!Files.isDirectory(injectin)) {
			throw new IllegalArgumentException("injectin is not a directory.");
		}
		if(!injectin.startsWith(this.fork.getLocation())) {
			throw new IllegalArgumentException("injectin is not an original directory from this fork as inherited from subject system.");
		}
		
		//decide if rename
		boolean rename;
		if(random.nextInt(100) + 1 < properties.getFileRenameRate()) {
			rename = true;
		} else {
			rename = false;
		}
		
		//get name
		Path injectName;
		if(rename) {
			int maxRenameAttempt=1000;
			String newname;
			
			do {
				newname = FilenameGenerator.getRandomFilenameWithExtention(5, 20, properties.getLanguage());
				maxRenameAttempt--;
			} while(Files.exists(injectin.resolve(newname)) && maxRenameAttempt > 0);
			
			//if found one, use it
			if(!Files.exists(injectin.resolve(newname))) {
				injectName = Paths.get(newname).getFileName();
				
			//else default to not renaming
			} else {
				injectName = file.getFileName();
			}
		} else {
			injectName = file.getFileName();
		}
		
		//decide if mutate
		boolean mutate;
		if(random.nextInt(100) + 1 < properties.getFileMutationRate()) {
			mutate = true;
		} else {
			mutate = false;
		}
		
		//prepare version to inject
		Path toInject = file; //default to original (no mutate)
		Operator operator = null;
		int times = 0;
		if(mutate) {
			//pick times to edit
			int maxedits = (int)((double) FileUtil.countPrettyLines(file, properties.getLanguage())* (double) properties.getMaxFileEdit() / 100.0);
			if(maxedits == 0) maxedits = 1;
			times = random.nextInt(maxedits) + 1;
			
			//get operator
			OperatorChooser oc = properties.getFileMutationOperatorChooser();
			operator = oc.getCurrent();
			
			//try mutation
			while(operator != null) {
				try {
					toInject = this.mutate_file_helper(file, operator, times, properties.getMutationAttempts());
					break;
				} catch (MutationFailedException e) {
					operator = oc.getRandom();
					continue;
				}
			}
			
			//if fail to mutate, revert to no mutate
			if(operator == null) {
				toInject = file;
				operator = null; times = 0;
				
			//if successful, and used current operator, increment
			} else {
				if(operator.equals(oc.getCurrent())) {
					properties.incrementCurrentFileOperator();
				}
			}
		}
		
		//inject
		Path injected = Files.copy(toInject, injectin.resolve(injectName.getFileName()));
		
		//cleanup
		if(operator != null) {
			Files.deleteIfExists(toInject);
		}
		
		//make a record
		FileVariant v;
		if(operator != null) {
			v = new FileVariant(file.toAbsolutePath().normalize(), injected.toAbsolutePath().normalize(), operator, times);
		} else {
			v = new FileVariant(file.toAbsolutePath().normalize(), injected.toAbsolutePath().normalize());
		}
		variants.add(v);
		filevariants.add(v);
		
		//return success
		return v;
	}

	public LeafDirectoryVariant injectLeafDirectory(Path leafDirectory) throws FileNotFoundException, IOException, InterruptedException, IllegalArgumentException, NullPointerException {
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
				return null;
			} else {
				injectin = directories.remove(random.nextInt(directories.size()));
				
				//Check not already a directory with the same name (in case not renamed)
				if(!Files.exists(injectin.resolve(leafDirectory.getFileName()))) {
					break;
				} else {
					continue;
				}
			}
		}
		
		return injectLeafDirectoryAt(leafDirectory, injectin);
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
	public LeafDirectoryVariant injectLeafDirectoryAt(Path leafDirectory, Path injectIn) throws FileNotFoundException, IOException, InterruptedException, IllegalArgumentException, NullPointerException {
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
		// check injection is possible given filename (in case not renamed)
		if(Files.exists(injectIn.resolve(leafDirectory.getFileName()))) {
			throw new IllegalArgumentException("leafDirectory can not be injected into injectIn because a file already exists with the same name.");
		}
		
	//Do Work
		Path injectDirectory;  //the path of the injected directory (pathToInjectInto/injectedDirectorysName)
		Path tinjectDirectory; //a temporary directory for building hte directory to inject
		List<FileVariant> fvariants = new LinkedList<FileVariant>(); //file variants for the files injected with the directory
		
		//Decide if rename directory
		boolean dirrename;
		if(random.nextInt(100) + 1 < properties.getDirRenameRate()) {
			dirrename = true;
		} else {
			dirrename = false;
		}
		
		//Create Directory
		Path ldname = leafDirectory.getFileName();
		if(dirrename) { //if rename, generate a name and change path
			int maxRenameAttempt=1000;
			String newname;
			
			//Generate names until one found that is not already used in injectIn
			do {
				newname = FilenameGenerator.getRandomFilename(5, 20);
				maxRenameAttempt--;
			} while(Files.exists(injectIn.resolve(newname)) && maxRenameAttempt > 0);
			
			//if successful, assign new name
			if(!Files.exists(injectIn.resolve(newname))) {
				ldname = Paths.get(newname).getFileName();
			}
		}
		injectDirectory = injectIn.resolve(ldname);
		tinjectDirectory = Files.createTempDirectory(SystemUtil.getTemporaryDirectory(), "InjectDirectory");
		tinjectDirectory.toFile().deleteOnExit();
		
		//Create Files
		for(File file : leafDirectory.toFile().listFiles()) {
			boolean fileRenamed;
			boolean fileMutate;
			Operator operator = null;
			int times = 0;
			//dedcide if rename and/or mutate
			if(random.nextInt(100)+1 < properties.getFileRenameRate()) {
				fileRenamed = true;
			} else {
				fileRenamed = false;
			}
			if(random.nextInt(100)+1 < properties.getFileMutationRate()) {
				fileMutate = true;
			} else {
				fileMutate = false;
			}
			
			//File Paths
			Path ofile = file.toPath(); //The original file
			Path ifile = ofile; //The file to inject
			Path tfile = injectDirectory.resolve(ofile.getFileName()); //The target (injected) file
			Path ttfile = tinjectDirectory.resolve(ofile.getFileName());
			
			//if mutate, change file to inject to mutated version
			if(fileMutate) {
				
				//pick times to edit
				int maxedits = (int)((double) FileUtil.countPrettyLines(file.toPath(), properties.getLanguage())* (double) properties.getMaxFileEdit() / 100.0);
				if(maxedits == 0) maxedits = 1;
				times = random.nextInt(maxedits) + 1;
				
				//Get Operator
				OperatorChooser oc = properties.getDirectoryMutationOperatorChooser();
				operator = oc.getCurrent();
				
				//try mutation
				while(operator != null) {
					try {
						ifile = this.mutate_file_helper(ofile, operator, times, properties.getMutationAttempts());
						break;
					} catch (MutationFailedException e) {
						operator = oc.getRandom();
						continue;
					}
				}
				
				//revert to default if mutation was not possible
				if(operator == null) {
					ifile = ofile;
					
				//if successful, and used current operator, increment
				} else {
					if(operator.equals(oc.getCurrent())) {
						properties.incrementCurrentDirectoryOperator();
					}
				}
			}
			
			//if rename, change target file
			if(fileRenamed) {
				int maxRenameAttempt=1000;
				do {
					maxRenameAttempt--;
					String rfilename = FilenameGenerator.getRandomFilenameWithExtention(5, 15, fork.getLanguage());
					tfile = injectDirectory.resolve(rfilename);
					ttfile = tinjectDirectory.resolve(rfilename);
				} while(Files.exists(tfile) && maxRenameAttempt > 0);
				//if failed after 100 attempts, give up on renaming
				if(maxRenameAttempt == 0) {
					tfile = injectDirectory.resolve(ofile.getFileName());
					ttfile = tinjectDirectory.resolve(ofile.getFileName());
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
		
		//MakeRecord
		LeafDirectoryVariant v = new LeafDirectoryVariant(leafDirectory, injectDirectory, fvariants);
		variants.add(v);
		directoryvariants.add(v);
		
		return v;
	}
	
	public FunctionVariant injectFunction(Fragment function) throws FileNotFoundException, IOException, InterruptedException {
		Objects.requireNonNull(function, "function can not be null.");
		if(!Files.exists(function.getSrcFile())) {
			new NoSuchFileException("File containing function does not exist.");
		}
		if(!Files.isReadable(function.getSrcFile())) {
			new IllegalArgumentException("File containing function is not readable.");
		}
		if(!Files.isRegularFile(function.getSrcFile())) {
			new IllegalArgumentException("File containing function is not a file.");
		}
		int numlines = FragmentUtil.countLines(function.getSrcFile());
		if(function.getEndLine() > numlines) {
			new IllegalArgumentException("Function is invalid.  Endline proceeds end of file.");
		}
		if(!FragmentUtil.isFunction(function, fork.getLanguage())) {
			new IllegalArgumentException("Function is invalid, does not specify a function.");
		}
		
		//Get location to inject at
		Fragment injectafter;
		//continue to choose until a suitable location is found, or all exhausted
		while(true) {
			//pick a previously unchosen function fragment at random
			injectafter = fork.getRandomFunctionFragmentNoFileRepeats();
			
			//if all exhausted, return failure
			if(injectafter == null) {
				return null;
			}
					
			//ensure file has not been previously modified (this shouldn't occur)
			if(!changedFiles.contains(injectafter.getSrcFile().toAbsolutePath().normalize())) {
				
				//ensure fragment perfectly frames a function
				if(FragmentUtil.isFunction(injectafter, fork.getLanguage())) {
					break;
				}
			}
		}
		
		//inject and return variant
		return injectFunctionAt(function, injectafter);
	}
	
	public FunctionVariant injectFunctionAt(Fragment function, Fragment injectafter) throws FileNotFoundException, IOException, InterruptedException {
			//check objects
		Objects.requireNonNull(function, "Function is null.");
		Objects.requireNonNull(injectafter, "injectafter is null.");
			//check function
		if(!Files.exists(function.getSrcFile())) {
			new NoSuchFileException("File containing function does not exist.");
		}
		if(!Files.isReadable(function.getSrcFile())) {
			new IllegalArgumentException("File containing function is not readable.");
		}
		if(!Files.isRegularFile(function.getSrcFile())) {
			new IllegalArgumentException("File containing function is not a file.");
		}
		int numlines = FragmentUtil.countLines(function.getSrcFile());
		if(function.getEndLine() > numlines) {
			new IllegalArgumentException("Function is invalid.  Endline proceeds end of file.");
		}
		if(!FragmentUtil.isFunction(function, fork.getLanguage())) {
			new IllegalArgumentException("Function is invalid, does not specify a function.");
		}
			//check injectafter
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
		if(!injectafter.getSrcFile().startsWith(fork.getLocation().toAbsolutePath().normalize())) { //is in this fork
			new IllegalArgumentException("injectafter source file is not from the fork.");
		}
		if(!fork.getFiles().contains(injectafter.getSrcFile())) { //double cehck?
			new IllegalArgumentException("injectafter source file is not from the fork.");
		}
		if(!changedFiles.contains(injectafter.getSrcFile())) { //check not already injected into
			new IllegalArgumentException("injectafter source file has already been modified.");
		}
		
		//Track what to inject (mutate will change this)
		Fragment toInject = function;
		
		//Decide if mutate and times
		boolean mutate = false;
		Operator operator = null;
		int times = 0;
		if(random.nextInt(100) + 1 < properties.getFragmentMutationRate()) {
			mutate = true;
		} else {
			mutate = false;
		}
		
		Path extracted = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "extracted", "");
		FragmentUtil.extractFragment(function, extracted);
		
		int maxedits = (int)((double) (FileUtil.countPrettyLines(extracted, properties.getLanguage())) * (double) properties.getMaxFunctionEdit() / 100.0);
		if(maxedits == 0) maxedits = 1;
		times = random.nextInt(maxedits) + 1;
		
		Files.delete(extracted);
		
		//If mutate
		if(mutate) {
			OperatorChooser oc = properties.getFunctionMutationOperatorChooser();
			operator = oc.getCurrent();
			Path mutated;
			
			//try mutation
			while(operator != null) {
				try {
					mutated = mutate_function_helper(function, operator, times, properties.getMutationAttempts());
					toInject = new Fragment(mutated, 1, FileUtil.countLines(mutated));
					break;
				} catch (MutationFailedException e) {
					operator = oc.getRandom();
					continue;
				}
			}
			
			//if fail to mutate, revert to no mutate
			if(operator == null) {
				toInject = function;
				mutate = false;
				operator = null; times = 0;
				
			//if successful, and used current operator, increment
			} else {
				if(operator.equals(oc.getCurrent())) {
					properties.incrementCurrentFunctionOperator();
				}
			}
		}
		
		//Inject into file
		FragmentUtil.injectFragment(injectafter.getSrcFile(), injectafter.getEndLine() + 1, toInject);
		
		//Update Changed Files
		if(false == changedFiles.add(injectafter.getSrcFile())) {
			new IllegalArgumentException("injectafter source file has already been modified.");
		}
		
		//Make record
		FunctionVariant fv;
		if(operator == null) {
			fv = new FunctionVariant(function, new Fragment(injectafter.getSrcFile(), injectafter.getEndLine()+1, injectafter.getEndLine() + 1 + (toInject.getEndLine()-toInject.getStartLine())));
		} else {
			fv = new FunctionVariant(function, new Fragment(injectafter.getSrcFile(), injectafter.getEndLine()+1, injectafter.getEndLine() + 1 + (toInject.getEndLine()-toInject.getStartLine())), operator, times);
		}
		variants.add(fv);
		functionfragmentvariants.add(fv);
		
		//cleanup
		if(operator != null) {
			Files.deleteIfExists(toInject.getSrcFile());
		}
		
		//Return record
		return fv;
	}
	
}