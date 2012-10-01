package main;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import util.FileUtil;
import util.FragmentUtil;

import models.FileVariant;
import models.DirectoryVariant;
import models.Fork;
import models.FragmentVariant;
import models.FunctionFragment;
import models.InventoriedSystem;


public class ForkingSimulator {
	public static Path installdir;
	
	public static void main(String args[]) {
	//Get installation directory
		installdir = Paths.get(ForkingSimulator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		
	//Handle Input Parameters
		if(args.length != 1 && args.length != 2) {
			printUsage();
			return;
		}
		Path propertiesfile;
		try {
			propertiesfile = Paths.get(args[0]);
		} catch (Exception e) {
			System.out.println("Path to properties file is invalid.");
			printUsage();
			return;
		}
		
	//Get Properties
		Properties properties;
		try {
			properties = new Properties(propertiesfile);
		} catch (Exception e) {
			System.out.println("Error in properties file.");
			return;
		}
		
	//Initial check of properties
		//repository
		if(!Files.isDirectory(properties.getRepository())) {
			System.out.println("Repository path specified in properties file does not point to a valid directory.");
		}
		//system
		if(!Files.isDirectory(properties.getSystem())) {
			System.out.println("System path specified in properties file does not point to a valid directory.");
		}
		
	//Initialize output directory
		Path outputdir;
		if(args.length == 1) {
			try {
				outputdir = Files.createTempDirectory(Paths.get(""), "ForkSimulation");
			} catch (IOException e) {
				System.out.println("Could not create an output directory in the current working directory.");
				return;
			}
		} else {
			try {
				outputdir = Paths.get(args[1]).toAbsolutePath().normalize();
			} catch (Exception e) {
				System.out.println("Specified path for output directory is not valid.");
				printUsage();
				return;
			}
			if(Files.exists(outputdir)) {
				System.out.println("Specified output directory already exists.");
				printUsage();
				return;
			}
			try {
				Files.createDirectories(outputdir);
			} catch (IOException e) {
				System.out.println("Could not create output directory.");
				printUsage();
				return;
			}
		}
		
	//Set up repository
		InventoriedSystem repository;
		try {
			repository = new InventoriedSystem(properties.getRepository(), properties.getLanguage());
		} catch (IOException e) {
			System.out.println("Failed to inventory the repository.  Did you modify the files?");
			return;
		}
		
	//Create fork bases
		List<Fork> forks = new ArrayList<Fork>(properties.getNumforks());
		for(int i = 0; i < properties.getNumforks(); i++) {
			try {
				forks.add(new Fork(properties.getSystem(), outputdir.resolve(Paths.get("" + i + "")), properties.getLanguage()));
			} catch (IOException e) {
				System.out.println("Failed to create and/or inventory a fork.  Could be a permission error?");
				return;
			}
		}
	
	//Random number generator for number injections
		Random random = new Random();
		
	// Create File Variants
		System.out.println("BEGIN: FileVariants");
		
		//prep
		int numf = 0;
		try {
			Files.createDirectory(outputdir.resolve("files"));
		} catch (IOException e1) {
			System.err.println("Failed to create directory to store file variants.");
			System.exit(-1);
		}
		
		while (numf < properties.getNumfiles()) {
		
		//Get file to inject (from repository) without repeats
			Path file = repository.getRandomFileNoRepeats();
			
		//Out of options before goal?
			if(file == null) {
				break;
			}
			
		//Randomly select number of forks to inject into
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			
		//Chose the forks to inject into (the numbers in the forks list)
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumforks());
			Collections.sort(injects);
			
		//Inject the file into the forks
			//track the foks effected / variants created
			List<Integer> t_forks = new LinkedList<Integer>();
			List<FileVariant> t_variants = new LinkedList<FileVariant>();
			
			//perform injections
			for(int forkn : injects) {
				try {
					FileVariant fv = forks.get(forkn).injectFile(file);
					if(fv != null) {
						t_forks.add(forkn);
						t_variants.add(fv);
					}
				} catch (IOException e) {
					System.out.println("Failed to inject file into a fork (IOException).  Could be a permission error, or something else is interacting with the fork's files.");
					return;
				}
			}
			assert(t_forks.size() == t_variants.size()) : "t_forks and t_variants not same size... debug";
			
		//Check success (increment counter) and report effects
			if(t_variants.size() > 0) {
				numf++;
				System.out.println(numf + " : " + file.toAbsolutePath().normalize().toString());
				for(int i = 0; i < t_forks.size(); i++) {
					System.out.println("\t" + t_forks.get(i) + " : " + t_variants.get(i).getInjectedFile());
				}
				try {
					Files.copy(file, outputdir.resolve("files/" + numf));
				} catch (IOException e) {
					System.err.println("Failed to save injected file record...");
					System.exit(-1);
				}
			}
		}
		System.out.println("END: FileVariants");
		
	// Create Leaf Directory Variants
		System.out.println("BEGIN: DirVariants");
		
		//prep
		int numd = 0;
		Path dirVariantDir = null;
		try {
			dirVariantDir = Files.createDirectory(outputdir.resolve("dirs"));
		} catch (IOException e1) {
			System.err.println("Failed to create directory to store directory variants.");
			System.exit(-1);
		}
		
		while (numd < properties.getNumdirectories()) {
		
		//Get directory to inject (from repository) without repeats
			Path dir = repository.getRandomLeafDirectoryNoRepeats();
			
		//Out of options before goal?
			if(dir == null) {
				break;
			}
			
		//Randomly select number of forks to inject into
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			
		//Chose the forks to inject into (the numbers in the forks list)
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumforks());
			Collections.sort(injects);
			
		//Inject the file into the forks
			//track the forks effected / variants created
			List<Integer> t_forks = new LinkedList<Integer>();
			List<DirectoryVariant> t_variants = new LinkedList<DirectoryVariant>();
			
			//perform injections
			for(int forkn : injects) {
				try {
					DirectoryVariant dv = forks.get(forkn).injectDirectory(dir);
					if(dv != null) {
						t_forks.add(forkn);
						t_variants.add(dv);
					}
				} catch (IOException e) {
					System.out.println("Failed to inject directory into a fork (IOException).  Could be a permission error, or something else is interacting with the fork's files.");
					return;
				}
			}
			assert(t_forks.size() == t_variants.size()) : "t_forks and t_variants not same size... debug";
			
		//Check success (increment counter) and report effects
			if(t_variants.size() != 0) {
				numd++;
				System.out.println(numd + " : " + dir.toAbsolutePath().normalize().toString());
				for(int i = 0; i < t_forks.size(); i++) {
					System.out.println("\t" + t_forks.get(i) + " : " + t_variants.get(i).getInjectedDirectory());
				}
				try {
					FileUtil.copyDirectory(dir, dirVariantDir.resolve("" + numd));
				} catch (IOException e) {
					System.err.println("Failed to save injected dir record...");
					System.exit(-1);
				}
			}
		}
		System.out.println("END: DirectoryVariants");
	
		
	// Create Fragment Variants
		System.out.println("BEGIN: FunctionFragmentVariants");
		
		//prep
		int numff = 0;
		try {
			Files.createDirectory(outputdir.resolve("function_fragments"));
		} catch (IOException e) {
			System.err.println("Failed to create directory to store function fragment variant records.");
		}
		
		while(numff < properties.getNumfragments()) {
		// Get function fragment to inject
			FunctionFragment functionfragment = repository.getRandomFunctionFragmentNoFileRepeats();
			
		//Out of options before goal?
			if(functionfragment == null) {
				break;
			}
		
		//Randomly select number of forks to inject into;
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			
		//Chose the forks to inject into (the numbers in the forks list)
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumforks());
			Collections.sort(injects);
		
		//Inject the function fragment into the forks
			//track the forks effected/variants created
			List<Integer> t_forks = new LinkedList<Integer>();
			List<FragmentVariant> t_variants = new LinkedList<FragmentVariant>();
			
			//perform injections
			for(int forkn : injects) {
				try {
					FragmentVariant ffv = forks.get(forkn).injectFunctionFragment(functionfragment);
					if(ffv != null) {
						t_forks.add(forkn);
						t_variants.add(ffv);
					} 
				} catch (IOException e) {
						System.out.println("Failed to inject function fragment into fork (IOException).  Could be a permission error, or something else is interacting with the fork's files.");
						System.exit(-1);
				}
			}
			assert(t_forks.size() == t_variants.size()) : "t_forks and t_variants not same size... debug";
			
		// Check success (increment counter) and report efforts
			if(t_variants.size() > 0) {
				numff++;
				System.out.println(numff + " : " + functionfragment.getSrcFile() + ":" + functionfragment.getStartLine() + "-" + functionfragment.getEndLine());
				for(int i = 0; i < t_forks.size(); i++) {
					System.out.println("\t" + t_forks.get(i) + " : " + t_variants.get(i).getInjectedFragment().getSrcFile() + ":" + t_variants.get(i).getInjectedFragment().getStartLine() + "-" + t_variants.get(i).getInjectedFragment().getEndLine());
				}
				try {
					FragmentUtil.extractFragment(functionfragment, outputdir.resolve("function_fragments/" + numff));
				} catch (IOException e) {
					System.err.println("Failed to save injected function fragment record...");
					System.exit(-1);
				}
			}
		}
		System.out.println("END: FunctionFragmentVariants");
		
	}
	
	public static void printUsage() {
		System.out.println("Usage: forksim propertiesfile [outputdir]");
		System.out.println("\toutputdir - optional, must not be an existing directory.  If not specified a directory is created in current working directory.");
	}
	
	/**
	 * Returns a list of randomly selected numbers (no repeats) ranging from 0 (inclusive) to max (exclusive).
	 * @param num The number of values to select (must be <= max).
	 * @param max The maximum value of number to pick (exclusive).
	 * @return A list of randomly selected numbers (no repeats) ranging from 0 (inclusive) to max (exclusive).
	 */
	private static List<Integer> pickRandomNumbers(int num, int max) {
		//Check input parameters
		if(num > max) {
			throw new IllegalArgumentException("'num' must not be greater than 'max'.");
		}
		
		//Random number generator
		Random random = new Random();
		
		//Initialize selectFrom list with all integers [0,max) (no repeats)
		List<Integer> selectFrom = new LinkedList<Integer>();
		for(int i = 0; i < max; i++) {
			selectFrom.add(i);
		}
		
		//Create list of randomly selected (non-repeating) integers of size num
		List<Integer> selected = new LinkedList<Integer>();
		for(int i = 0; i <= num; i++) {
			selected.add(selectFrom.remove(random.nextInt(selectFrom.size())));
		}
		
		//Return the list
		return selected;
	}
	
}
