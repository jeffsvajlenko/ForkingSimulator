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
import util.SystemUtil;

import models.FileVariant;
import models.DirectoryVariant;
import models.Fork;
import models.FragmentVariant;
import models.FunctionFragment;
import models.InventoriedSystem;
import models.Operator;


public class ForkingSimulator {
	public static Path installdir;
	
	public static void main(String args[]) {
	//Get installation directory
		installdir = Paths.get(ForkingSimulator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	
	//Setup Operators
		Operator[] operators = new Operator[15];
		operators[0] = new Operator("mCC_BT", "Random change in comments.  /* */ style added between two tokens..", 1, Paths.get("operators/mCC_BT"));
		operators[1] = new Operator("mCC_EOL", "Random change in comments.  // style added at the end of a line.", 1, Paths.get("operators/mCC_EOL"));
		operators[2] = new Operator("mCF_A", "Random change in formatting.  A newline is randomly added.", 1, Paths.get("operators/mCF_A"));
		operators[3] = new Operator("mCF_R", "Random change in formatting.  A newline is removed.", 1, Paths.get("operators/mCF_R"));
		operators[4] = new Operator("mCW_A", "Random change in whitespace.  A space is added.", 1, Paths.get("operators/mCW_A"));
		operators[5] = new Operator("mCW_R", "Random change in whitesapce.  A space is removed.", 1, Paths.get("operators/mCW_R"));
		
		operators[6] = new Operator("mRL_N", "Random change in literal.  A number is changed.", 2, Paths.get("operators/mRL_N"));
		operators[7] = new Operator("mRL_S", "Random change in literal.  A string is changed.", 2, Paths.get("operators/mRL_S"));
		operators[8] = new Operator("mSRI", "Random renaming of a all instances of a single identifier.", 2, Paths.get("operators/mSRI"));
		operators[9] = new Operator("mARI", "Random renaming of a single identifier instance.", 2, Paths.get("operators/mARI"));
		
		operators[10] = new Operator("mDL", "Random deletion of a line of source code (simple lines only).", 3, Paths.get("operators/mDL"));
		operators[11] = new Operator("mIL", "Random insertion of a line of source code (simple line)", 3, Paths.get("operators/mIL"));
		operators[12] = new Operator("mML", "Random modification of a line of source code (entire line).", 3, Paths.get("operators/mML"));
		operators[13] = new Operator("mSDL", "Random small deletion within a line.", 3, Paths.get("operators/mSDL"));
		operators[14] = new Operator("mSIL", "Random small insertion within a line.", 3, Paths.get("operators/mSIL"));
		
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
			System.out.println("Error in properties file: " + e.getMessage());
			e.printStackTrace();
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
				outputdir = Files.createTempDirectory(Paths.get(""), "ForkSimulation_");
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
		
	// Output Properties
		System.out.println("BEGIN: Properties");
		System.out.println("\t" + "output_directory=" + outputdir.toAbsolutePath().normalize().toString());
		System.out.println("\t" + "system_directory=" + properties.getSystem().toAbsolutePath().normalize());
		System.out.println("\t" + "repository_directory=" + properties.getRepository().toAbsolutePath().normalize());
		System.out.println("\t" + "language=" + properties.getLanguage());
		System.out.println("\t" + "#forks=" + properties.getNumForks());
		System.out.println("\t" + "max#injects=" + properties.getMaxinjectnum());
		System.out.println("\t" + "#files=" + properties.getNumFiles());
		System.out.println("\t" + "#dirs=" + properties.getNumDirectories());
		System.out.println("\t" + "#fragments=" + properties.getNumFragments());
		System.out.println("\t" + "mutationrate=" + properties.getMutationRate());
		System.out.println("END: Properties");
		
	//Set up repository
		InventoriedSystem repository;
		try {
			repository = new InventoriedSystem(properties.getRepository(), properties.getLanguage());
		} catch (IOException e) {
			System.out.println("Failed to inventory the repository.  Did you modify the files?");
			return;
		}
		
	//Create fork bases
		List<Fork> forks = new ArrayList<Fork>(properties.getNumForks());
		for(int i = 0; i < properties.getNumForks(); i++) {
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
		
		while (numf < properties.getNumFiles()) {
		
		//Get file to inject (from repository) without repeats
			Path file = repository.getRandomFileNoRepeats();
			
		//Out of options before goal?
			if(file == null) {
				break;
			}
			
		//Randomly select number of forks to inject into
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			
		//Chose the forks to inject into (the numbers in the forks list)
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumForks());
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
				System.out.println(numf + " " + t_variants.size() + " " + file.toAbsolutePath().normalize().toString());
				for(int i = 0; i < t_forks.size(); i++) {
					System.out.println("\t" + t_forks.get(i) + " " + t_variants.get(i).getInjectedFile());
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
		System.out.println("BEGIN: DirectoryVariants");
		
		//prep
		int numd = 0;
		Path dirVariantDir = null;
		try {
			dirVariantDir = Files.createDirectory(outputdir.resolve("dirs"));
		} catch (IOException e1) {
			System.err.println("Failed to create directory to store directory variants.");
			System.exit(-1);
		}
		
		while (numd < properties.getNumDirectories()) {
		
		//Get directory to inject (from repository) without repeats
			Path dir = repository.getRandomLeafDirectoryNoRepeats();
			
		//Out of options before goal?
			if(dir == null) {
				break;
			}
			
		//Randomly select number of forks to inject into
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			
		//Chose the forks to inject into (the numbers in the forks list)
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumForks());
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
				System.out.println(numd + " " + t_forks.size() + " " + dir.toAbsolutePath().normalize().toString());
				for(int i = 0; i < t_forks.size(); i++) {
					System.out.println("\t" + t_forks.get(i) + " " + t_variants.get(i).getInjectedDirectory());
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
		int cur_opnum = 0;
		
		//prep
		int numff = 0;
		try {
			Files.createDirectory(outputdir.resolve("function_fragments"));
		} catch (IOException e) {
			System.err.println("Failed to create directory to store function fragment variant records.");
		}
		
		while(numff < properties.getNumFragments()) {
		// Get function fragment to inject
			FunctionFragment functionfragment = null;
			int functionfragment_length;
			do {
				functionfragment = repository.getRandomFunctionFragmentNoFileRepeats();
				if(functionfragment == null) {
					break;
				}
				functionfragment_length = functionfragment.getEndLine() - functionfragment.getStartLine() + 1;
			} while(functionfragment_length < properties.getFunctionFragmentMinSize() || functionfragment_length > properties.getFunctionFragmentMaxSize());
			
		//Out of options before goal?
			if(functionfragment == null) {
				break;
			}
		
		//Randomly select number of forks to inject into;
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			
		//Chose the forks to inject into (the numbers in the forks list)
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumForks());
			Collections.sort(injects);
		
		//Inject the function fragment into the forks
			//track the forks effected/variants created
			List<Integer> t_forks = new LinkedList<Integer>();
			List<FragmentVariant> t_variants = new LinkedList<FragmentVariant>();
			
			//perform injections
			for(int forkn : injects) {
				try {
					FragmentVariant ffv = null;
					
					//mutate case
					if(random.nextInt(100) < properties.getMutationRate()) {
						
						boolean success = false;//keep track if works
						
						//prep alternative operator list
						List<Integer> opids = new LinkedList<Integer>();
						for(int i = 0; i < operators.length; i++) {
							if(i != cur_opnum) {
								opids.add(i);
							}
						}
						int opnum = cur_opnum;
						
						//First attempt queued op, then if fail try the others randomly, if all fails don't mutate
						do {
							try {
								ffv = forks.get(forkn).injectFunctionFragment(functionfragment, operators[opnum], properties.getNumMutationAttempts(), properties.getLanguage());
							} catch (InterruptedException e) {
								e.printStackTrace();
								System.exit(-1);
							}
							if(ffv != null) {
								success = true;
								if(opnum == cur_opnum) {
									//System.out.println(opnum);
									cur_opnum = ForkingSimulator.nextRollingNumber(cur_opnum, operators.length-1);
								} else {
									//System.out.println("--");
								}
								break;
							}
							if(opids.size() != 0) {
								opnum = opids.remove(random.nextInt(opids.size()));
							}
						} while(opids.size() != 0);
						
						//if all mutation attempt fail, just inject as is
						if(!success) {
							//System.out.println("-");
							ffv = forks.get(forkn).injectFunctionFragment(functionfragment);
						}
						
					//no mutate case
					} else {
						ffv = forks.get(forkn).injectFunctionFragment(functionfragment);
					}
					
					//if success, remember for later
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
				System.out.println(numff + " " +  t_forks.size() + " " + functionfragment.getSrcFile() + " " + functionfragment.getStartLine() + " " + functionfragment.getEndLine());
				for(int i = 0; i < t_forks.size(); i++) {
					if(t_variants.get(i).getOperator() == null) {
						System.out.println("\t" + t_forks.get(i) + " " + t_variants.get(i).getInjectedFragment().getSrcFile() + " " + t_variants.get(i).getInjectedFragment().getStartLine() + " " + t_variants.get(i).getInjectedFragment().getEndLine() + " none 1");
					} else {
						System.out.println("\t" + t_forks.get(i) + " " + t_variants.get(i).getInjectedFragment().getSrcFile() + " " + t_variants.get(i).getInjectedFragment().getStartLine() + " " + t_variants.get(i).getInjectedFragment().getEndLine() + " " + t_variants.get(i).getOperator().getId() + " " + t_variants.get(i).getOperator().getTargetCloneType());
					}
				}
				try {
					Files.createDirectory(outputdir.resolve("function_fragments").resolve("" + numff));
					FragmentUtil.extractFragment(functionfragment, outputdir.resolve("function_fragments/" + numff + "/original"));
					for(int i = 0; i < t_forks.size(); i++) {
						FragmentUtil.extractFragment(t_variants.get(i).getInjectedFragment(), outputdir.resolve("function_fragments/" + numff + "/" + t_forks.get(i)));
					}
				} catch (IOException e) {
					System.err.println("Failed to save injected function fragment record...");
					e.printStackTrace();
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
	protected static List<Integer> pickRandomNumbers(int num, int max) {
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
	
	/**
	 * Increments an integer from 0 to maximum, with roll around.
	 * @param current The current value.
	 * @param maximum The maximum value.
	 * @return the next value.
	 * @throws IllegalArgumentException if current is greater than maximum or less than 0.
	 */
	protected static int nextRollingNumber(int current, int maximum) {
		if(current > maximum || current < 0) {
			throw new IllegalArgumentException();
		}
		int next = current+1;
		if(next > maximum) {
			next = 0;
		}
		return next;
	}
}
