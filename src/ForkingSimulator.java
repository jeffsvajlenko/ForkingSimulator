import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import models.FileVariant;
import models.Fork;
import models.InventoriedSystem;


public class ForkingSimulator {
	public static Path installdir;
	
	public static void main(String args[]) {
	//Get installation directory
		installdir = Paths.get(ForkingSimulator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		
	//Handle Input Parameters
		if(args.length != 1 || args.length != 2) {
			printUsage();
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
		for(int i = 0; i < properties.getNumfiles(); i++) {
			//Get file to inject (from repository) without repeats
			Path file = repository.getRandomFileNoRepeats();
			
			//Randomly select number of forks to inject into
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			
			//Chose the forks to inject into (the numbers in the forks list)
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumforks());
			
			//Inject the file into the forks
			for(int forkn : injects) {
				try {
					FileVariant fv = forks.get(forkn).injectFile(file);
				} catch (IOException e) {
					System.out.println("Failed to inject file into a fork (IOException).  Could be a permission error, or something else is interacting with the fork's files.");
					return;
				}
			}
		}
		
	// Create Leaf Directory Variants
		
	// Create Fragment Variants
		
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
		for(int i = 0; i <= max; i++) {
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
