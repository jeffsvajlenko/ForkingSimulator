package main;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import models.FileVariant;
import models.Fork;
import models.Fragment;
import models.FunctionVariant;
import models.LeafDirectoryVariant;
import models.Operator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.TeeOutputStream;

import util.FragmentUtil;
import util.InventoriedSystem;
import util.StreamGobbler;
import util.SystemUtil;


public class ForkingSimulator {
	public static Path installdir;
	
	public static void main(String args[]) throws IOException {
	//Get installation directory
		installdir = Paths.get(ForkingSimulator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	
	//Setup Method Mutation Operators
		Operator[] method_mutation_operators = new Operator[15];
		method_mutation_operators[0] = new Operator("mCC_BT", "Random change in comments.  /* */ style added between two tokens..", 1, Paths.get("operators/mCC_BT"));
		method_mutation_operators[1] = new Operator("mCC_EOL", "Random change in comments.  // style added at the end of a line.", 1, Paths.get("operators/mCC_EOL"));
		method_mutation_operators[2] = new Operator("mCF_A", "Random change in formatting.  A newline is randomly added.", 1, Paths.get("operators/mCF_A"));
		method_mutation_operators[3] = new Operator("mCF_R", "Random change in formatting.  A newline is removed.", 1, Paths.get("operators/mCF_R"));
		method_mutation_operators[4] = new Operator("mCW_A", "Random change in whitespace.  A space is added.", 1, Paths.get("operators/mCW_A"));
		method_mutation_operators[5] = new Operator("mCW_R", "Random change in whitesapce.  A space is removed.", 1, Paths.get("operators/mCW_R"));
		method_mutation_operators[6] = new Operator("mRL_N", "Random change in literal.  A number is changed.", 2, Paths.get("operators/mRL_N"));
		method_mutation_operators[7] = new Operator("mRL_S", "Random change in literal.  A string is changed.", 2, Paths.get("operators/mRL_S"));
		method_mutation_operators[8] = new Operator("mSRI", "Random renaming of a all instances of a single identifier.", 2, Paths.get("operators/mSRI"));
		method_mutation_operators[9] = new Operator("mARI", "Random renaming of a single identifier instance.", 2, Paths.get("operators/mARI"));
		method_mutation_operators[10] = new Operator("mDL", "Random deletion of a line of source code (simple lines only).", 3, Paths.get("operators/mDL"));
		method_mutation_operators[11] = new Operator("mIL", "Random insertion of a line of source code (simple line)", 3, Paths.get("operators/mIL"));
		method_mutation_operators[12] = new Operator("mML", "Random modification of a line of source code (entire line).", 3, Paths.get("operators/mML"));
		method_mutation_operators[13] = new Operator("mSDL", "Random small deletion within a line.", 3, Paths.get("operators/mSDL"));
		method_mutation_operators[14] = new Operator("mSIL", "Random small insertion within a line.", 3, Paths.get("operators/mSIL"));
	
	//Setup File Mutation Operators
		Operator[] file_mutation_operators = new Operator[15];
		file_mutation_operators[0] = new Operator("mCC_BT", "Random change in comments.  /* */ style added between two tokens..", 1, Paths.get("operators/mCC_BT"));
		file_mutation_operators[1] = new Operator("mCC_EOL", "Random change in comments.  // style added at the end of a line.", 1, Paths.get("operators/mCC_EOL"));
		file_mutation_operators[2] = new Operator("mCF_A", "Random change in formatting.  A newline is randomly added.", 1, Paths.get("operators/mCF_A"));
		file_mutation_operators[3] = new Operator("mCF_R", "Random change in formatting.  A newline is removed.", 1, Paths.get("operators/mCF_R"));
		file_mutation_operators[4] = new Operator("mCW_A", "Random change in whitespace.  A space is added.", 1, Paths.get("operators/mCW_A"));
		file_mutation_operators[5] = new Operator("mCW_R", "Random change in whitesapce.  A space is removed.", 1, Paths.get("operators/mCW_R"));
		file_mutation_operators[6] = new Operator("mRL_N", "Random change in literal.  A number is changed.", 2, Paths.get("operators/mRL_N"));
		file_mutation_operators[7] = new Operator("mRL_S", "Random change in literal.  A string is changed.", 2, Paths.get("operators/mRL_S"));
		file_mutation_operators[8] = new Operator("mSRI", "Random renaming of a all instances of a single identifier.", 2, Paths.get("operators/mSRI"));
		file_mutation_operators[9] = new Operator("mARI", "Random renaming of a single identifier instance.", 2, Paths.get("operators/mARI"));
		file_mutation_operators[10] = new Operator("mDL", "Random deletion of a line of source code (simple lines only).", 3, Paths.get("operators/mDL"));
		file_mutation_operators[11] = new Operator("mIL", "Random insertion of a line of source code (simple line)", 3, Paths.get("operators/mIL"));
		file_mutation_operators[12] = new Operator("mML", "Random modification of a line of source code (entire line).", 3, Paths.get("operators/mML"));
		file_mutation_operators[13] = new Operator("mSDL", "Random small deletion within a line.", 3, Paths.get("operators/mSDL"));
		file_mutation_operators[14] = new Operator("mSIL", "Random small insertion within a line.", 3, Paths.get("operators/mSIL"));
		
	//Setup Directory Mutation Operator
		Operator[] dir_mutation_operators = new Operator[15];
		dir_mutation_operators[0] = new Operator("mCC_BT", "Random change in comments.  /* */ style added between two tokens..", 1, Paths.get("operators/mCC_BT"));
		dir_mutation_operators[1] = new Operator("mCC_EOL", "Random change in comments.  // style added at the end of a line.", 1, Paths.get("operators/mCC_EOL"));
		dir_mutation_operators[2] = new Operator("mCF_A", "Random change in formatting.  A newline is randomly added.", 1, Paths.get("operators/mCF_A"));
		dir_mutation_operators[3] = new Operator("mCF_R", "Random change in formatting.  A newline is removed.", 1, Paths.get("operators/mCF_R"));
		dir_mutation_operators[4] = new Operator("mCW_A", "Random change in whitespace.  A space is added.", 1, Paths.get("operators/mCW_A"));
		dir_mutation_operators[5] = new Operator("mCW_R", "Random change in whitesapce.  A space is removed.", 1, Paths.get("operators/mCW_R"));
		dir_mutation_operators[6] = new Operator("mRL_N", "Random change in literal.  A number is changed.", 2, Paths.get("operators/mRL_N"));
		dir_mutation_operators[7] = new Operator("mRL_S", "Random change in literal.  A string is changed.", 2, Paths.get("operators/mRL_S"));
		dir_mutation_operators[8] = new Operator("mSRI", "Random renaming of a all instances of a single identifier.", 2, Paths.get("operators/mSRI"));
		dir_mutation_operators[9] = new Operator("mARI", "Random renaming of a single identifier instance.", 2, Paths.get("operators/mARI"));
		dir_mutation_operators[10] = new Operator("mDL", "Random deletion of a line of source code (simple lines only).", 3, Paths.get("operators/mDL"));
		dir_mutation_operators[11] = new Operator("mIL", "Random insertion of a line of source code (simple line)", 3, Paths.get("operators/mIL"));
		dir_mutation_operators[12] = new Operator("mML", "Random modification of a line of source code (entire line).", 3, Paths.get("operators/mML"));
		dir_mutation_operators[13] = new Operator("mSDL", "Random small deletion within a line.", 3, Paths.get("operators/mSDL"));
		dir_mutation_operators[14] = new Operator("mSIL", "Random small insertion within a line.", 3, Paths.get("operators/mSIL"));
		
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
			properties = new Properties(propertiesfile, method_mutation_operators, file_mutation_operators, dir_mutation_operators);
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
		
	//Initialize tmp dir
		try {
			Path newtmpdir = Files.createDirectories(outputdir.resolve("tmp"));
			SystemUtil.setTemporaryDirectory(newtmpdir);
		} catch (IOException e) {
			System.out.println("Could not initialize temporary directory.");
			e.printStackTrace();
			return;
		}
		
	//Initialize logging
		TeeOutputStream logs;
		try {
			logs = new TeeOutputStream(System.out, new FileOutputStream(Files.createFile(outputdir.resolve("log")).toFile()));
		} catch (FileNotFoundException e3) {
			System.out.println("Failed to create log file.");
			e3.printStackTrace();
			return;
		} catch (IOException e3) {
			System.out.println("Failed to create log file.");
			e3.printStackTrace();
			return;
		}
		PrintWriter log = new PrintWriter(logs);
		
	//Make copy of properties in output
		Files.copy(propertiesfile, outputdir.resolve("properties"));
		
	//Set up repository
		try {
			FileUtils.copyDirectory(properties.getRepository().toFile(), outputdir.resolve("sourceRepository").toFile());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to copy source repository into output directory.  Did you modify the files?");
			return;
		}
		properties.setRepository(outputdir.resolve("sourceRepository").toAbsolutePath().normalize());
		System.err.println("Importing and normalizing the source repository:");
		try {
			ForkingSimulator.normalizeSystem(properties.getRepository(), properties.getLanguage());
		} catch (IOException | InterruptedException e2) {
			System.out.println("Failed to normalize source repository.");
			e2.printStackTrace();
			return;
		}
		System.out.println("Inventorying source repository.");
		InventoriedSystem repository;
		try {
			repository = new InventoriedSystem(properties.getRepository(), properties.getLanguage());
		} catch (IOException e) {
			System.out.println("Failed to inventory the repository.  Did you modify the files?");
			return;
		}
	
	//Set Up Subject System
		try {
			FileUtils.copyDirectory(properties.getSystem().toFile(), outputdir.resolve("originalSystem").toFile());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to copy original system into output directory.  Did you modify the files?");
			return;
		}
		properties.setSystem(outputdir.resolve("originalSystem").toAbsolutePath().normalize());
		System.err.println("Importing and normalizing the subject system:");
		try {
			ForkingSimulator.normalizeSystem(properties.getSystem(), properties.getLanguage());
		} catch (IOException | InterruptedException e2) {
			System.out.println("Failed to normalize subject system.");
			e2.printStackTrace();
			return;
		}
		System.out.println("Inventorying subject system.");
		InventoriedSystem originalSystem;
		try {
			originalSystem = new InventoriedSystem(properties.getSystem(), properties.getLanguage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to inventory the original system.  Did you modify the files?");
			return;
		}
		
	// Output Properties
		log.println("BEGIN: Properties");
		log.println("\t" + "output_directory=" + outputdir.toAbsolutePath().normalize().toString());
		log.println("\t" + "system_directory=" + properties.getSystem().toAbsolutePath().normalize());
		log.println("\t" + "repository_directory=" + properties.getRepository().toAbsolutePath().normalize());
		log.println("\t" + "language=" + properties.getLanguage());
		log.println("\t" + "numforks=" + properties.getNumForks());
		log.println("\t" + "numfiles=" + properties.getNumFiles());
		log.println("\t" + "numdirs=" + properties.getNumDirectories());
		log.println("\t" + "numfragments=" + properties.getNumFragments());
		log.println("\t" + "functionfragmentminsize=" + properties.getFunctionFragmentMinSize());
		log.println("\t" + "functionfragmentmaxsize=" + properties.getFunctionFragmentMaxSize());
		log.println("\t" + "maxinjectnum=" + properties.getMaxinjectnum());
		log.println("\t" + "injectionrepititionrate=" + properties.getInjectionReptitionRate());
		log.println("\t" + "fragmentmutationrate=" + properties.getFragmentMutationRate());
		log.println("\t" + "filemutationrate=" + properties.getFileMutationRate());
		log.println("\t" + "dirmutationrate=" + properties.getDirectoryMutationRate());
		log.println("\t" + "filerenamerate=" + properties.getFileRenameRate());
		log.println("\t" + "dirrenamerate=" + properties.getDirRenameRate());
		log.println("\t" + "maxfileedits=" + properties.getMaxFileEdits());
		log.println("\t" + "maxfunctionedits=" + properties.getMaxFunctionEdits());
		log.println("\t" + "mutationattempts=" + properties.getNumMutationAttempts());
		log.println("END: Properties");
		
	//Create the forks
		List<Fork> forks = new ArrayList<Fork>(properties.getNumForks());
		for(int i = 0; i < properties.getNumForks(); i++) {
			try {
				forks.add(new Fork(properties.getSystem(), outputdir.resolve(Paths.get("" + i + "")), properties.getLanguage(), properties));
			} catch (IOException e) {
				System.out.println("Failed to create and/or inventory a fork.  Could be a permission error?");
				return;
			}
		}
	
//Random number generator for number injections
		Random random = new Random();
		
// Create File Variants --------------------------------------------------------------------------------------------------------------------------------
log.println("BEGIN: FileVariants");
		
	//Prep Operator Scrolling
		int cur_opnum = 0;
		
	//Create log directory for files
		int numf = 0;
		try {
			Files.createDirectory(outputdir.resolve("files"));
		} catch (IOException e1) {
			System.err.println("Failed to create directory to store file variants.");
			System.exit(-1);
		}
		
	//Create files and inject (and mutate?)
		fileloop:
		while (numf < properties.getNumFiles()) {
		
		//Get file to inject (from repository) without repeats
			Path file = repository.getRandomFileNoRepeats();
			
		//Out of options before goal?
			if(file == null) {
				break;
			}
			
		//Randomly select number of forks to inject into
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			
		//Chose the forks to inject into
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumForks()); //pick random numbers of the fork numbers
			Collections.sort(injects);
			
		//Inject the file into the forks
			//track the forks affected / variants created by this injection (lists in sync)
			List<Integer> t_forks = new LinkedList<Integer>();
			List<FileVariant> t_variants = new LinkedList<FileVariant>();
			
			//choose uniform or non-uniform injections
			Path injectin = originalSystem.getRandomDirectory();
			injectin = originalSystem.getLocation().toAbsolutePath().normalize().relativize(injectin).normalize();
			boolean doUniform;
			if(numf < (int) Math.round((properties.getNumFiles() * properties.getInjectionReptitionRate()) / 100)) {
				doUniform = true;
			} else {
				doUniform = false;
			}
						
			//Inject
			fileInjectLoop:
			for(int forkn : injects) {
				
			//Inject
				try {
					Fork fork = forks.get(forkn);
					FileVariant fv = null;
					Path thisinjectin = fork.getLocation().toAbsolutePath().normalize().resolve(injectin).toAbsolutePath().normalize();
					
					if(doUniform) {
						try {
							fv = fork.injectFileAt(file, thisinjectin);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Error: A mutation was interrupted.  Did you kill the mutation process?");
							System.exit(-1);
						}
					} else {
						try {
							fv = fork.injectFile(file);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Error: A mutation was interrupted.  Did you kill the mutation process?");
							System.exit(-1);
						}
					}
					
					//Record variants added and forks mutated
					if(fv != null) {
						t_forks.add(forkn);
						t_variants.add(fv);
					}
				} catch (IOException e) {
					System.out.println("Failed to inject file into a fork (IOException).  Could be a permission error, or something else is interacting with the fork's files.");
					return;
				}
			}
			
		//If successful (at least one variant introduced), increment counter, and report effects (output)
			if(t_variants.size() > 0) {
				//Increment counter
				numf++;
				
				//Make Referenced Data Directory
				try {
					Files.createDirectories(outputdir.resolve("files/" + numf));
				} catch (IOException e1) {
					System.err.println("Failed to create directory to store file injection records...");
					System.exit(-1);
				}
				
			//File Injection Header and Refereced Data
				if(doUniform) {
					log.println(numf + " U " + t_variants.size() + " " + outputdir.relativize(file.toAbsolutePath().normalize()).toString());
				} else {
					log.println(numf + " V " + t_variants.size() + " " + outputdir.relativize(file.toAbsolutePath().normalize()).toString());
				}
				//Save Original Copy
				try {
					Files.copy(file, outputdir.resolve("files/" + numf + "/original"));
				} catch (IOException e) {
					System.err.println("Failed to save injected file record (original copy)...");
					System.exit(-1);
				}
				
			//Per Injection Information and Referenced Data
				for(int i = 0; i < t_forks.size(); i++) {
					log.print("\t" + t_forks.get(i));
					if(t_variants.get(i).isNameMutated()) {
						log.print(" R");
					} else {
						log.print(" O");
					}
					if(t_variants.get(i).isSourceMutated()) {
						log.print(" M " + t_variants.get(i).getMutationOperator().getId() + " " + t_variants.get(i).getMutationTimes() + " " + t_variants.get(i).getMutationOperator().getTargetCloneType());
					} else {
						log.print(" O");
					}
					log.println(" " + outputdir.relativize(t_variants.get(i).getInjectedFile()));
					
					//Save copy of possibly mutated
					try {
						Files.copy(t_variants.get(i).getInjectedFile(), outputdir.resolve("files/" + numf + "/" + t_forks.get(i)));
					} catch (IOException e) {
						System.err.println("Failed to save injected file record (injected copy)...");
						e.printStackTrace();
						System.exit(-1);
					}
				}
				
				log.flush();
			}
		}
log.println("END: FileVariants");
		
// Create Leaf Directory Variants ------------------------------------------------------------------------------------------------------------------------------------------
log.println("BEGIN: LeafDirectoryVariants");
		
	// Prep Operator Scrolling
		cur_opnum = 0;

		//create log directory for dirs
		int numd = 0;
		Path dirVariantDir = null;
		try {
			dirVariantDir = Files.createDirectory(outputdir.resolve("dirs"));
		} catch (IOException e1) {
			System.err.println("Failed to create directory to store directory variants.");
			System.exit(-1);
		}
		
	//Create Leaf Directories and Inject (and mutate?)
		leafDirectoryLoop:
		while (numd < properties.getNumDirectories()) {
			//System.out.println("DEBUG:Start LeafDirectoryLoop");
			
		//Get directory to inject (from repository) without repeats
			Path leafDirectory = repository.getRandomLeafDirectoryNoRepeats();
			
		//Out of options before goal?
			if(leafDirectory == null) {
				break;
			}
			
		//Randomly select number of forks to inject into
			int numinjections = random.nextInt(properties.getMaxinjectnum()) + 1;
			//System.out.println("DEBUG:\t" + numinjections + " forks chosen.");
			
		//Chose the forks to inject into (the numbers in the forks list)
			List<Integer> injects = pickRandomNumbers(numinjections,properties.getNumForks());
			Collections.sort(injects);
			String debug_picks = "";
			for(int i : injects) {
				debug_picks += i + " ";
			}
			//System.out.println("DEBUG:\tChosen: " + debug_picks);
			
		//Inject the file into the forks
			//track the forks effected / variants created
			List<Integer> t_forks = new LinkedList<Integer>();
			List<LeafDirectoryVariant> t_variants = new LinkedList<LeafDirectoryVariant>();
			
			//perform injections
			Path injectin  = originalSystem.getRandomDirectory();
			injectin = originalSystem.getLocation().toAbsolutePath().normalize().relativize(injectin).normalize();
			boolean doUniform;
			if(numd < (int) Math.round((properties.getNumDirectories() * properties.getInjectionReptitionRate()) / 100)) {
				doUniform = true;
			} else {
				doUniform = false;
			}
			
			//inject
			leafDirectoryInjectLoop:
			for(int forkn : injects) {
				
			//Inject
				try {
					Fork fork = forks.get(forkn);
					LeafDirectoryVariant ldv = null;
					Path thisinjectin = fork.getLocation().toAbsolutePath().normalize().resolve(injectin).toAbsolutePath().normalize();
										
					if(doUniform) {
						try {
							ldv = fork.injectLeafDirectoryAt(leafDirectory, thisinjectin);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Error: A mutation was interrupted.  Did you kill the mutation process?");
							System.exit(-1);
						}
					} else {
						try {
							ldv = fork.injectLeafDirectory(leafDirectory);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Error: A mutation was interrupted.  Did you kill the mutation process?");
							System.exit(-1);
						}
					}
					
					//Record Variants added and forks mutated
					if(ldv != null) {
						t_forks.add(forkn);
						t_variants.add(ldv);
					} else {
						//System.out.println("DEBUG:\t\t\tldv is null?");
					}
				} catch (IOException e) {
					System.out.println("Failed to inject file into a fork (IOException).  Could be a permission error, or something else is interacting with the fork's files.");
					return;
				}
			}
			
			
		//Check success (increment counter) and report effects
			if(t_variants.size() != 0) {
				numd++;
				
				//Create ref data folder
				try {
					Files.createDirectories(dirVariantDir.resolve("" + numd));
				} catch (IOException e) {
					System.err.println("Failed to create directory variant ref folder...");
					System.exit(-1);
				}
				
			//Dir Injection Header and Referenced Data
				if(doUniform) {
					log.println(numd + " U " + t_forks.size() + " " + outputdir.relativize(leafDirectory.toAbsolutePath().normalize()).toString());
				} else {
					log.println(numd + " V " + t_forks.size() + " " + outputdir.relativize(leafDirectory.toAbsolutePath().normalize()).toString());
				}
				
				//Track original folder
				try {
					FileUtils.copyDirectory(leafDirectory.toFile(), dirVariantDir.resolve("" + numd).resolve("original").toFile());
				} catch (IOException e) {
					System.err.println("Failed to save original dir record...");
					System.exit(-1);
				}
				
				//Per Injection Information and Referenced Data
				for(int i = 0; i < t_forks.size(); i++) {
					//document
					log.print("\t" + t_forks.get(i));
					if(t_variants.get(i).isRenamed()) {
						log.print(" R ");
					} else {
						log.print(" O ");
					}
					log.println(t_variants.get(i).getFileVariants().size() + " " + outputdir.relativize(t_variants.get(i).getInjectedDirectory()));
					
					for(FileVariant fv : t_variants.get(i).getFileVariants()) {
						log.print("\t\t\t");
						if(fv.isNameMutated()) {
							log.print(" R");
						} else {
							log.print(" O");
						}
						if(fv.isSourceMutated()) {
							log.print(" M " + fv.getMutationOperator().getId() + " " + fv.getMutationTimes() + " " + fv.getMutationOperator().getTargetCloneType());
						} else {
							log.print(" O");
						}
						log.println(" " + outputdir.relativize(fv.getOriginalFile()) + ";" + outputdir.relativize(fv.getInjectedFile()));
					}
					
					//make copy
					try {
						FileUtils.copyDirectory(t_variants.get(i).getInjectedDirectory().toFile(), dirVariantDir.resolve("" + numd).resolve("" + t_forks.get(i)).toFile());
					} catch (IOException e) {
						System.err.println("Failed to save injected dir record...");
						e.printStackTrace();
						System.exit(-1);
					}
				}
				log.flush();
			}
		}
log.println("END: LeafDirectoryVariants");
	
// Create Fragment Variants ------------------------------------------------------------------------------------------------------------------------------------------------
log.println("BEGIN: FunctionFragmentVariants");
		cur_opnum = 0;
		
		//prep
		int numff = 0;
		int uniformCutoff = (int) Math.round((properties.getNumFragments() * properties.getInjectionReptitionRate()) / 100);
		try {
			Files.createDirectory(outputdir.resolve("function_fragments"));
		} catch (IOException e) {
			System.err.println("Failed to create directory to store function fragment variant records.");
			return;
		}
		
		while(numff < properties.getNumFragments()) {
		// Get function fragment to inject
			Fragment functionfragment = null;
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
			List<FunctionVariant> t_variants = new LinkedList<FunctionVariant>();
			
			//perform injections
			
			//is this injection uniform? (do uniform first so injection location isent a problem)
			boolean isInjectionUniform;
			if(numff < uniformCutoff) {
				isInjectionUniform = true;
			} else {
				isInjectionUniform = false;
			}
			
			//Arrange Injection Location if uniform
			Fragment injectafter = null; 
			Path srcfilerelative = null;
			if(isInjectionUniform) {
				injectafter = originalSystem.getRandomFunctionFragmentNoFileRepeats();
				if(injectafter != null) {
					srcfilerelative = originalSystem.getLocation().toAbsolutePath().normalize().relativize(injectafter.getSrcFile().toAbsolutePath().normalize());
					injectafter = new Fragment(srcfilerelative, injectafter.getStartLine(), injectafter.getEndLine());
				} else { //used up all the locations already
					break;
				}
			}

functionfragmentinjectloop:
			for(int forkn : injects) {
				try {
					Fork fork = forks.get(forkn);
					FunctionVariant ffv = null;
					Fragment thisInjectAfter = null;
					if(isInjectionUniform) {
						thisInjectAfter = new Fragment(fork.getLocation().toAbsolutePath().normalize().resolve(injectafter.getSrcFile()), injectafter.getStartLine(), injectafter.getEndLine());
					}
					
					if(isInjectionUniform) {
						try {
							ffv = fork.injectFunctionAt(functionfragment, thisInjectAfter);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Error: A mutation was interrupted.  Did you kill the mutation process?");
							System.exit(-1);
						}
					} else {
						try {
							ffv = fork.injectFunction(functionfragment);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println("Error: A mutation was interrupted.  Did you kill the mutation process?");
							System.exit(-1);
						}
					}
					
					//if success, remember for later
					if(ffv != null) {
						t_forks.add(forkn);
						t_variants.add(ffv);
					} 
				} catch (IOException e) {
						System.out.println("Failed to inject function fragment into fork (IOException).  Could be a permission error, or something else is interacting with the fork's files.");
						e.printStackTrace();
						System.exit(-1);
				}
			}
			assert(t_forks.size() == t_variants.size()) : "t_forks and t_variants not same size... debug";
			
		// Check success (increment counter) and report efforts
			if(t_variants.size() > 0) {
				numff++;
				if(isInjectionUniform) {
					log.println(numff + " U " +  t_forks.size() + " " + functionfragment.getStartLine() + " " + functionfragment.getEndLine() + " " + outputdir.relativize(functionfragment.getSrcFile()));
				} else {
					log.println(numff + " V " +  t_forks.size() + " " + functionfragment.getStartLine() + " " + functionfragment.getEndLine() + " " + outputdir.relativize(functionfragment.getSrcFile()));
				}
				for(int i = 0; i < t_forks.size(); i++) {
					if(t_variants.get(i).getOperator() == null) {
						log.println("\t" + t_forks.get(i) + " O " + t_variants.get(i).getInjectedFragment().getStartLine() + " " + t_variants.get(i).getInjectedFragment().getEndLine() + " " + outputdir.relativize(t_variants.get(i).getInjectedFragment().getSrcFile()));
					} else {
						log.println("\t" + t_forks.get(i) + " M " + t_variants.get(i).getOperator().getId() + " " + t_variants.get(i).getTimes() + " " +t_variants.get(i).getOperator().getTargetCloneType() + " " + t_variants.get(i).getInjectedFragment().getStartLine() + " " + t_variants.get(i).getInjectedFragment().getEndLine()  + " " + outputdir.relativize(t_variants.get(i).getInjectedFragment().getSrcFile()));
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
log.println("END: FunctionFragmentVariants");
		

		
	//Check simulation
		//CheckSimulation.main(new String[]{propertiesfile.toAbsolutePath().normalize().toString(), outputdir.resolve("log").toAbsolutePath().normalize().toString()});
		
	//Remove tmp
		FileUtils.deleteDirectory(outputdir.resolve("tmp").toFile());
		
		//Flush Log
		log.flush();
		log.close();
	}
	
	public static void printUsage() {
		System.out.println("Usage: forksim propertiesfile [outputdir]");
		System.out.println("\toutputdir - optional, must not be an existing directory.  If not specified a directory is created in current working directory.");
	}
	
	/**
	 * Returns a list of randomly selected numbers (no repeats) ranging from 0 (inclusive) to max (exclusive).
	 * @param num The number of values to select (must be <= max and > 0).
	 * @param max The maximum value of number to pick (exclusive).
	 * @return A list of randomly selected numbers (no repeats) ranging from 0 (inclusive) to max (exclusive).
	 */
	protected static List<Integer> pickRandomNumbers(int num, int max) {
		//Check input parameters
		if(num > max) {
			throw new IllegalArgumentException("'num' must not be greater than 'max'.");
		}
		if(max < 0) {
			throw new IllegalArgumentException("max must be >= 0.");
		}
		if(num < 1) {
			throw new IllegalArgumentException("num must be > 0.");
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
		for(int i = 0; i < num; i++) {
			selected.add(selectFrom.remove(random.nextInt(selectFrom.size())));
		}
		
		//Return the list
		return selected;
	}
	
	/**
	 * Increments an integer from 0 to maximum (inclusive), with roll around.
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
	
	protected static int normalizeSystem(Path path, String language) throws IOException, InterruptedException {
		Runtime rt = Runtime.getRuntime();
		Process process = rt.exec("./prepsystem " + path.toAbsolutePath().normalize() + " " + language);
		new StreamGobbler(process.getErrorStream()).start();
		int retval = process.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		line = br.readLine();
		while(line != null) {
			System.out.println("\t" + line);
			line = br.readLine();
		}
		try {process.getErrorStream().close();} catch (IOException e) {}
		return retval;
	}
}
