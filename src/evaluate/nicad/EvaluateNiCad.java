package evaluate.nicad;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import models.CloneClass;
import models.Fragment;
import util.InventoriedSystem;
import util.SelectFunctionFragments;
import util.SystemUtil;

public class EvaluateNiCad {
	/**
	 * 1: generation log
	 * 2: function clones
	 * 3: file clones
	 * 4: path to output directory
	 * 5: path to evaluation directory
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException {
//Input
	//Check Number of Inputs
		if(args.length != 5) {
			System.out.println("Usage: GenerationLog NiCadFunctionClones NiCadFileClones OutputDirectory EvaluationDirectory");
			System.out.println(" GenerationLog: Path to the generation log for the fork data.");
			System.out.println(" NiCadFunctionClones: Path to NiCad's evaluation report for function clones.");
			System.out.println(" NiCadFileClones: Path to NiCad's evaluation report for file clones.");
			System.out.println(" OutputDirectory: Path to the current location of the fork generation output directory.");
			System.out.println(" EvaluationDirectory: Path to the location of the forks when analyzed by NiCad.  This string will be trimmed from the paths of NiCad's reported clones.");
			return;
		}
		
	//Store Inputs
		Path generationLog;
		Path niCadFunctionCloneReport;
		Path niCadFileCloneReport;
		Path outputDirectory;
		Path evaluationDirectory;
		
	//GenerationLog
		try {
			generationLog = Paths.get(args[0]).toAbsolutePath().normalize();
		} catch (InvalidPathException e) {
			System.out.println("GenerationLog path invalid.");
			return;
		}
		if(!Files.exists(generationLog)) {
			System.out.println("Generationlog does not exist.");
			return;
		}
		if(!Files.isRegularFile(generationLog)) {
			System.out.println("GenerationLog is not a regular file.");
			return;
		}
		
		try {
			niCadFunctionCloneReport = Paths.get(args[1]).toAbsolutePath().normalize();
		} catch (InvalidPathException e) {
			System.out.println("niCadFunctionCloneReport path invalid.");
			return;
		}
		if(!Files.exists(niCadFunctionCloneReport)) {
			System.out.println("niCadFunctionCloneReport does not exist.");
			return;
		}
		if(!Files.isRegularFile(niCadFunctionCloneReport)) {
			System.out.println("GenerationLog is not a regular file.");
			return;
		}
		
		try {
			niCadFileCloneReport = Paths.get(args[2]).toAbsolutePath().normalize();
		} catch (InvalidPathException e) {
			System.out.println("niCadFileCloneReport path invalid.");
			return;
		}
		if(!Files.exists(niCadFileCloneReport)) {
			System.out.println("niCadFileCloneReport does not exist.");
			return;
		}
		if(!Files.isRegularFile(niCadFileCloneReport)) {
			System.out.println("niCadFileCloneReport is not a regular file.");
			return;
		}
		
		try {
			outputDirectory = Paths.get(args[3]).toAbsolutePath().normalize();
		} catch (InvalidPathException e) {
			System.out.println("outputDirectory path invalid.");
			return;
		}
		if(!Files.exists(outputDirectory)) {
			System.out.println("outputDirectory does not exist.");
			return;
		}
		if(!Files.isDirectory(outputDirectory)) {
			System.out.println("outputDirectory is not a regular file.");
			return;
		}
		
		try {
			evaluationDirectory = Paths.get(args[4]).toAbsolutePath().normalize();
		} catch (InvalidPathException e) {
			System.out.println("evaluationDirectory path invalid.");
			return;
		}	
		
//Tracking Data
	//File Clones (converted from known similarities)
		LinkedHashSet<CloneClass<Path>> fileClones_originalNotAltered = new LinkedHashSet<CloneClass<Path>>();			//File clone classes of files from original system, not including ones with altered files (from original state)
		LinkedHashSet<CloneClass<Path>> fileClones_originalAltered_trimmed = new LinkedHashSet<CloneClass<Path>>();		//File clone classes of files from original system, in which the file from one or more of the forks is trimmed due to modification (injection of function)
		LinkedHashSet<CloneClass<Path>> fileClones_originalAltered_kept = new LinkedHashSet<CloneClass<Path>>();		//File clone classes of files from original system, in which one or more of the files in a class was modified by injection since original system
		//File clone classes due to injected files.  Iterable in FileVariant injection order.
		LinkedHashSet<CloneClass<Path>> fileClones_injectedFiles = new LinkedHashSet<CloneClass<Path>>();
		//FileCloneClasses due to injected directories.
		LinkedList<LinkedHashSet<CloneClass<Path>>> fileClones_injectedDirectories_byVariant = new LinkedList<LinkedHashSet<CloneClass<Path>>>();
	
	//Function Clones (converted from known similarities)
		LinkedList<LinkedHashSet<CloneClass<Fragment>>> functionClones_injectedFiles_byVariant = new LinkedList<LinkedHashSet<CloneClass<Fragment>>>();
		LinkedList<LinkedList<LinkedHashSet<CloneClass<Fragment>>>> functionClones_injectedDirectories_byVariantByFile = new LinkedList<LinkedList<LinkedHashSet<CloneClass<Fragment>>>>();
		LinkedHashSet<CloneClass<Fragment>> functionClones_injectedFunctions = new LinkedHashSet<CloneClass<Fragment>>();
		LinkedList<LinkedHashSet<CloneClass<Fragment>>> functionClones_original = new LinkedList<LinkedHashSet<CloneClass<Fragment>>>();
		
		LinkedList<CloneClass<Fragment>> functionClones_injectedFunctions_pairs = new LinkedList<CloneClass<Fragment>>();
		LinkedList<CloneClass<Fragment>> functionClones_original_pairs = new LinkedList<CloneClass<Fragment>>();
		
		
	//Compared to base data
		LinkedHashSet<Path> modifiedOriginalFiles = new LinkedHashSet<Path>(); //Files in base system which were modified in one or more forks
		LinkedHashSet<Path> modifiedFiles = new LinkedHashSet<Path>(); //Files in forks modified with respect to their original file (from base system)
		LinkedHashMap<Path,Fragment> modifiedFileInjectedFunction = new LinkedHashMap<Path,Fragment>();
	
	//NiCadResults
		LinkedHashSet<CloneClass<Path>> nicad_fileResults = new LinkedHashSet<CloneClass<Path>>();
		LinkedHashSet<CloneClass<Path>> nicad_fileResults_pairs_trimmedInternalClones = new LinkedHashSet<CloneClass<Path>>();
		LinkedHashSet<CloneClass<Path>> nicad_validatedFileClonePairs_usingKnownClones = new LinkedHashSet<CloneClass<Path>>();
		LinkedHashSet<CloneClass<Path>> nicad_unvalidatedFileClonePairs_usingKnownClones = new LinkedHashSet<CloneClass<Path>>();
		
		LinkedHashSet<CloneClass<Fragment>> nicad_functionResults = new LinkedHashSet<CloneClass<Fragment>>();
		LinkedHashSet<CloneClass<Fragment>> nicad_functionResults_pairs_trimmedInternalClones = new LinkedHashSet<CloneClass<Fragment>>();
		LinkedHashSet<CloneClass<Fragment>> nicad_validatedFunctionClonePairs_usingKnownClones = new LinkedHashSet<CloneClass<Fragment>>();
		LinkedHashSet<CloneClass<Fragment>> nicad_unvalidatedFunctionClonePairs_usingKnownClones = new LinkedHashSet<CloneClass<Fragment>>();
		
	//Properties
		Path oldOutputDirectory;
		Path systemDirectory = outputDirectory.resolve("originalSystem");
		String language;
		int numforks;
		
//Parse Objects
		Scanner in = new Scanner(generationLog.toFile());
		String line;
	
System.out.println(">> Parsing properties...");
//Get Properties
		line = in.nextLine(); //BEGIN PROPERTIES
		
		oldOutputDirectory = Paths.get(in.nextLine().replaceFirst("\toutput_directory=", ""));
		in.nextLine(); // systemdir
		in.nextLine(); // repositorydir
		language = in.nextLine().replaceFirst("\tlanguage=", "");
		numforks = Integer.parseInt(in.nextLine().replaceFirst("\tnumforks=", ""));
		in.nextLine(); // numfiles
		in.nextLine(); // numdirs
		in.nextLine(); // numfragments
		in.nextLine(); // function fragment min size
		in.nextLine(); // function fragment max size
		in.nextLine(); // maxinjects
		in.nextLine(); // injectionreptitionrate
		in.nextLine(); // fragment mutation rate
		in.nextLine(); // file mutation rate
		in.nextLine(); // directory mutation rate
		in.nextLine(); // file rename rate
		in.nextLine(); // directory rename rate
		in.nextLine(); // max file edit
		in.nextLine(); // mutation attempts
		in.nextLine();
		
		in.nextLine(); // END Properties.
		
System.out.println(">> Parsing file variants...");
//File Variants
		in.nextLine(); // BEGIN File Variants
		
	//Read each file variant until reach END
		while(true) {
			line = in.nextLine();
			
			if(line.startsWith("END: FileVariants")) {
				break;
			}
			
		//Parse File Variant Header
			Scanner fvh_in = new Scanner(line);
			char fvh_tmpchar;
			
			//Header Items
			int fvh_num;
			boolean fvh_isUniform;
			int fvh_numinject;
			Path fvh_originalFile;
			
			fvh_num = fvh_in.nextInt();
			fvh_tmpchar = fvh_in.next().charAt(0);
			if(fvh_tmpchar == 'U') {
				fvh_isUniform = true;
			} else {
				fvh_isUniform = false;
			}
			fvh_numinject = fvh_in.nextInt();
			fvh_originalFile = Paths.get(fvh_in.nextLine().replaceFirst("^\\s+", ""));
			fvh_in.close();
			
		//FileVariantInjection trackers
			LinkedHashSet<Path> injectedFileVariantClone = new LinkedHashSet<Path>(); //the file clone class of this file variant
			
		//Read Each Injection
			for(int i = 0; i < fvh_numinject; i++) {
				line = in.nextLine();
				
				Scanner fvih_in = new Scanner(line);
				char fvih_tmpchar;
				
				int fvih_forknum;
				boolean fvih_isRenamed;
				boolean fvih_isMutated;
				String fvih_mutator;
				int fvih_times;
				int fvih_cloneType;
				Path fvih_injectedFile;
				
				fvih_forknum = fvih_in.nextInt();
				fvih_tmpchar = fvih_in.next().charAt(0);
				if(fvih_tmpchar == 'R') {
					fvih_isRenamed = true;
				} else {
					fvih_isRenamed = false;
				}
				fvih_tmpchar = fvih_in.next().charAt(0);
				if(fvih_tmpchar == 'M') {
					fvih_isMutated = true;
					fvih_mutator = fvih_in.next();
					fvih_times = fvih_in.nextInt();
					fvih_cloneType = fvih_in.nextInt();
				} else {
					fvih_isMutated = false;
					fvih_mutator = null;
					fvih_times = 0;
					fvih_cloneType = 0;
				}
				fvih_injectedFile = oldOutputDirectory.relativize(Paths.get(fvih_in.nextLine().replaceFirst("^\\s+", "")));
				fvih_in.close();

				//Maintain Injection Trackers
				injectedFileVariantClone.add(fvih_injectedFile);
			}
			
		//Build file clone class from this variant, and add to tracker
			CloneClass<Path> fvi_cloneclass = new CloneClass<Path>(injectedFileVariantClone);
			fileClones_injectedFiles.add(fvi_cloneclass);
			
		//Build function clones classes from this variant, and add to tracker
			LinkedHashSet<CloneClass<Fragment>> fvi_function_CloneClasses = new LinkedHashSet<CloneClass<Fragment>>();
			List<List<Fragment>> fvi_filefunctions = new LinkedList<List<Fragment>>();
			for(Path fvi_file : fvi_cloneclass) {
				fvi_file = outputDirectory.resolve(fvi_file);
				LinkedList<Fragment> fvi_fragments = new LinkedList<Fragment>();
				for(Fragment fvi_fragment : EvaluateNiCad.getFunctionFragmentsInFile(fvi_file, language)) {
					fvi_fragments.add(new Fragment(outputDirectory.relativize(fvi_fragment.getSrcFile()), fvi_fragment.getStartLine(), fvi_fragment.getEndLine()));
				}
				fvi_filefunctions.add(fvi_fragments);
			}
			for(int j = 0; j < fvi_filefunctions.get(0).size(); j++) {
				LinkedHashSet<Fragment> fvi_fragments = new LinkedHashSet<Fragment>();
				for(int i = 0; i < fvi_filefunctions.size(); i++) {
					fvi_fragments.add(fvi_filefunctions.get(i).get(j));
				}
				if(fvi_fragments.size() > 1) {
					fvi_function_CloneClasses.add(new CloneClass<Fragment>(fvi_fragments));
				}
			}
			functionClones_injectedFiles_byVariant.add(fvi_function_CloneClasses);
		}
	
System.out.println(">> Parsing directory variants...");
//Directory Variants
		in.nextLine(); //BEGIN
		
	//Read each directory variant until see end
		while(true) {
			line = in.nextLine();
			
			if(line.startsWith("END: LeafDirectoryVariants")) {
				break;
			}
			
		//Parse Directory Variant header
			Scanner dvh_in = new Scanner(line);
			char dvh_tmpchar;
			
			int dvh_num;
			boolean dvh_isUniform;
			int dvh_numInject;
			Path dvh_originalDirectory;
			
			dvh_num = dvh_in.nextInt();
			dvh_tmpchar = dvh_in.next().charAt(0);
			if(dvh_tmpchar == 'U') {
				dvh_isUniform = true;
			} else {
				dvh_isUniform = false;
			}
			dvh_numInject = dvh_in.nextInt();
			dvh_originalDirectory = Paths.get(dvh_in.nextLine().replaceFirst("^\\s+", ""));
			
			dvh_in.close();
			
		//Per Directory Variant Tracker WOrk
			Map<Path, Set<Path>> dv_fileCloneClasses = new LinkedHashMap<Path, Set<Path>>();
			fileClones_injectedDirectories_byVariant.add(new LinkedHashSet<CloneClass<Path>>());
			
		//Read each injection
			for(int i = 0; i < dvh_numInject; i++) {
				line = in.nextLine();
				
				Scanner dvih_in = new Scanner(line);
				char dvih_tmpchar;
				
				int dvih_forknum;
				boolean dvih_isRenamed;
				int dvih_numFiles;
				Path dvih_injectedDirectory;
				
				dvih_forknum = dvih_in.nextInt();
				dvih_tmpchar = dvih_in.next().charAt(0);
				if(dvih_tmpchar == 'R') {
					dvih_isRenamed = true;
				} else {
					dvih_isRenamed = false;
				}
				dvih_numFiles = dvih_in.nextInt();
				dvih_injectedDirectory = oldOutputDirectory.relativize(Paths.get(dvih_in.nextLine().replaceFirst("^\\s+", "")));
				
				dvih_in.close();
				
			//Read Each Injected File
				for(int j = 0; j < dvih_numFiles; j++) {
					line = in.nextLine();
					
					Scanner dvfh_in = new Scanner(line);
					char dvfh_tmpchar;
					
					boolean dvfh_isRenamed;
					boolean dvfh_isMutated;
					String dvfh_mutator;
					int dvfh_times;
					int dvfh_cloneType;
					Path dvfh_originalFile;
					Path dvfh_injectedFile;
					
					dvfh_tmpchar = dvfh_in.next().charAt(0);
					if(dvfh_tmpchar == 'R') {
						dvfh_isRenamed = true;
					} else {
						dvfh_isRenamed = false;
					}
					dvfh_tmpchar = dvfh_in.next().charAt(0);
					if(dvfh_tmpchar == 'M') {
						dvfh_isMutated = true;
						dvfh_mutator = dvfh_in.next();
						dvfh_times = dvfh_in.nextInt();
						dvfh_cloneType = dvfh_in.nextInt();;
					} else {
						dvfh_mutator = null;
						dvfh_isMutated = false;
						dvfh_times = 0;
						dvfh_cloneType = 0;
					}
					dvfh_in.useDelimiter(";");
					dvfh_originalFile = Paths.get(dvfh_in.next().replaceFirst("^\\s+", "")); 
					dvfh_injectedFile = oldOutputDirectory.relativize(Paths.get(dvfh_in.nextLine().replaceFirst("^;","")));
					
					dvfh_in.close();
					
				// Maintain clone classes from this directory variant
					if(dv_fileCloneClasses.containsKey(dvfh_originalFile)) {
						dv_fileCloneClasses.get(dvfh_originalFile).add(dvfh_injectedFile);
					} else {
						dv_fileCloneClasses.put(dvfh_originalFile, new LinkedHashSet<Path>());
						dv_fileCloneClasses.get(dvfh_originalFile).add(dvfh_injectedFile);
					}
				}				
			}
			
			//Add this DV's file clone classes to global trackers
			for(Set<Path> dv_tmpset : dv_fileCloneClasses.values()) { //For each clone class from this directory variant, add to tracker
				fileClones_injectedDirectories_byVariant.getLast().add(new CloneClass<Path>(dv_tmpset));
			}
			
			//Build and add fragment clone classes to global trackers
			functionClones_injectedDirectories_byVariantByFile.add(new LinkedList<LinkedHashSet<CloneClass<Fragment>>>()); //new entry per directory variant
			for(Set<Path> dv_tmpset : dv_fileCloneClasses.values()) { //for each clone class from this directory variant
				LinkedHashSet<CloneClass<Fragment>> dv_function_CloneClasses = new LinkedHashSet<CloneClass<Fragment>>();
				List<List<Fragment>> dv_filefunctions = new LinkedList<List<Fragment>>();
				for(Path dv_file : dv_tmpset) {
					dv_file = outputDirectory.resolve(dv_file);
					LinkedList<Fragment> dv_fragments = new LinkedList<Fragment>();
					for(Fragment dv_fragment : EvaluateNiCad.getFunctionFragmentsInFile(dv_file, language)) {
						dv_fragments.add(new Fragment(outputDirectory.relativize(dv_fragment.getSrcFile()), dv_fragment.getStartLine(), dv_fragment.getEndLine()));
					}
					dv_filefunctions.add(dv_fragments);
				}
				for(int j = 0; j < dv_filefunctions.get(0).size(); j++) {
					LinkedHashSet<Fragment> dv_fragments = new LinkedHashSet<Fragment>();
					for(int i = 0; i < dv_filefunctions.size(); i++) {
						dv_fragments.add(dv_filefunctions.get(i).get(j));
					}
					if(dv_fragments.size() > 1) {
						dv_function_CloneClasses.add(new CloneClass<Fragment>(dv_fragments));
					}
				}
				functionClones_injectedDirectories_byVariantByFile.getLast().add(dv_function_CloneClasses);
			}
		}
		
System.out.println(">> Parsing function variants...");
//Function Variants
		line = in.nextLine(); //Begin
		
	//For Each Variant
		while(true) {
			line = in.nextLine();
			
			if(line.startsWith("END: FunctionFragmentVariants")) {
				break;
			}
			
		//Read in variant header
			Scanner fvh_in = new Scanner(line);
			char fvh_tmpchar;
			
			int fvh_num;
			boolean fvh_isUniform;
			int fvh_numInjects;
			int fvh_originalStartLine;
			int fvh_originalEndLine;
			Path fvh_originalFile;
			
			fvh_num = fvh_in.nextInt();
			fvh_tmpchar = fvh_in.next().charAt(0);
			if(fvh_tmpchar == 'U') {
				fvh_isUniform = true;
			} else {
				fvh_isUniform = false;
			}
			fvh_numInjects = fvh_in.nextInt();
			fvh_originalStartLine = fvh_in.nextInt();
			fvh_originalEndLine = fvh_in.nextInt();
			fvh_originalFile = Paths.get(fvh_in.nextLine().replaceFirst("^\\s+",""));
			fvh_in.close();
			
		//Local Trackers
			LinkedHashSet<Fragment> fvh_fragments = new LinkedHashSet<Fragment>();
			
		//For Each Injection
			for(int i = 0; i < fvh_numInjects; i++) {
				line = in.nextLine();
				
				Scanner fvih_in = new Scanner(line);
				char fvih_tmpchar;
				
				int fvih_forkNum;
				boolean fvih_isMutated;
				String fvih_mutator;
				int fvih_times;
				int fvih_cloneType;
				int fvih_injectedStartLine;
				int fvih_injectedEndLine;
				Path fvih_injectedFile;
				
				fvih_forkNum = fvih_in.nextInt();
				fvih_tmpchar = fvih_in.next().charAt(0);
				if(fvih_tmpchar == 'M') {
					fvih_isMutated = true;
					fvih_mutator = fvih_in.next();
					fvih_times = fvih_in.nextInt();
					fvih_cloneType = fvih_in.nextInt();
				} else {
					fvih_isMutated = false;
					fvih_mutator = null;
					fvih_times = 0;
					fvih_cloneType = 0;
				}
				fvih_injectedStartLine = fvih_in.nextInt();
				fvih_injectedEndLine = fvih_in.nextInt();
				fvih_injectedFile = oldOutputDirectory.relativize(Paths.get(fvih_in.nextLine().replaceFirst("^\\s+", "")));
				fvih_in.close();
				
				Fragment fvih_injectedFragment = new Fragment(fvih_injectedFile, fvih_injectedStartLine, fvih_injectedEndLine);
				
			//Maintain local trackers
				fvh_fragments.add(fvih_injectedFragment);
				
			//Maintain Global Trackers
				Path fvi_originalFile = systemDirectory.resolve(Paths.get(fvih_forkNum + "/").relativize(fvih_injectedFile));
				modifiedFiles.add(fvih_injectedFile);
				modifiedOriginalFiles.add(fvi_originalFile);
				modifiedFileInjectedFunction.put(fvih_injectedFile, fvih_injectedFragment);
			}
		//Maintain Global Trackers
			functionClones_injectedFunctions.add(new CloneClass<Fragment>(fvh_fragments));
		}

		
		
System.out.println(">> Parsing original system...");
// Original Files (Base System)
		InventoriedSystem originalSystemInventory = new InventoriedSystem(systemDirectory, language);
		
	// Original Files File Clone Classes
		for(Path o_path : originalSystemInventory.getFiles()) {
			
		//Clone Class of Unaltered Files
			if(!modifiedOriginalFiles.contains(o_path)) {
				LinkedHashSet<Path> cclass = new LinkedHashSet<Path>();
				for(int forknum = 0; forknum < numforks; forknum++) {
					Path originalInFork = Paths.get(forknum + "/").resolve(systemDirectory.relativize(o_path));
					cclass.add(originalInFork);
				}
				fileClones_originalNotAltered.add(new CloneClass<Path>(cclass));
		
		//Clone Classes Including Altered Files (trim+keep)
			} else {
				LinkedHashSet<Path> cclass_trim = new LinkedHashSet<Path>();
				LinkedHashSet<Path> cclass_kept = new LinkedHashSet<Path>();
				for(int forknum = 0; forknum < numforks; forknum++) {
					Path originalInFork = Paths.get(forknum + "/").resolve(systemDirectory.relativize(o_path));
					cclass_kept.add(originalInFork);
					if(!modifiedFiles.contains(originalInFork)) {
						cclass_trim.add(originalInFork);
					}
				}
				fileClones_originalAltered_trimmed.add(new CloneClass<Path>(cclass_trim));
				fileClones_originalAltered_kept.add(new CloneClass<Path>(cclass_kept));
			}
			
		}
		
	//Original Files Function Clone Classes
		for(Path o_path : originalSystemInventory.getFiles()) { //For each original file
			LinkedHashSet<CloneClass<Fragment>> o_cloneClassesFromFile = new LinkedHashSet<CloneClass<Fragment>>();
			o_path = systemDirectory.relativize(o_path);
			//System.out.println("File: " + o_path);
			LinkedList<LinkedList<Fragment>> o_originalFragmentsPerFork = new LinkedList<LinkedList<Fragment>>(); //list of fragments from each fork's version of the original file (order used to match fragments, despite shifting)
			//Build the lists, discard injected fragments
			for(int i = 0; i < numforks; i++) {
				//Get path of fork's version of the original file
				Path o_fileInFork = Paths.get(i + "/").resolve(o_path);
				//System.out.println("\tFFile: " + o_fileInFork);
				//Get function injected into fork's version (if any)
				Fragment injected = modifiedFileInjectedFunction.get(o_fileInFork);
				if(injected != null) {
					//System.out.println("\t\t" + injected.getSrcFile() + " " + injected.getStartLine() + " " + injected.getEndLine());
				}
				//Collect original fragments from fork's version of original file (skip if it was an injected one)
				LinkedList<Fragment> o_originalFragments = new LinkedList<Fragment>();
				for(Fragment o_fragment : EvaluateNiCad.getFunctionFragmentsInFile(outputDirectory.resolve(o_fileInFork), language)) {
					Fragment o_nfragment = new Fragment(outputDirectory.relativize(o_fragment.getSrcFile()), o_fragment.getStartLine(), o_fragment.getEndLine());
					//System.out.println("\t\t\t" + o_nfragment.getSrcFile() + " " + o_nfragment.getStartLine() + " " + o_nfragment.getEndLine());
					
					if(injected != null) {
						if(o_nfragment.getStartLine() >= injected.getStartLine() && o_nfragment.getStartLine() <= injected.getEndLine()) {
							//System.out.println("\t\t\t\tRejected, is/in injected.");
						} else {
							o_originalFragments.add(o_nfragment);
						}
					} else {
						o_originalFragments.add(o_nfragment);
					}
				}
				o_originalFragmentsPerFork.add(o_originalFragments);
				//System.out.println("\t\tnum_original_fragments:" + o_originalFragments.size());
			}
			
			//Build the clone classes, and add (but only if fragments exist)
			for(int i = 0; i < o_originalFragmentsPerFork.get(0).size(); i++) {
				LinkedHashSet<Fragment> o_function_cclass_fragments = new LinkedHashSet<Fragment>();
				for(int j = 0; j < numforks; j++) {
					o_function_cclass_fragments.add(o_originalFragmentsPerFork.get(j).get(i));
				}
				o_cloneClassesFromFile.add(new CloneClass<Fragment>(o_function_cclass_fragments));
			}
			
			//add built list of clone classes for this file to the list, but only if not empty
			if(o_cloneClassesFromFile.size() > 0) {
				//System.out.println("\tadded");
				functionClones_original.add(o_cloneClassesFromFile);
			} else {
				//System.out.println("\tnot added");
			}
		}
		
System.out.println(">> Converting to clone pairs");
	
	for(CloneClass<Fragment> cclass : functionClones_injectedFunctions) {
		LinkedList<Fragment> fragments = new LinkedList<Fragment>(cclass.getFragments());
		for(int i = 0; i < fragments.size(); i++) {
			for(int j = i+1; j < fragments.size(); j++) {
				LinkedHashSet<Fragment> set = new LinkedHashSet<Fragment>();
				set.add(fragments.get(i));
				set.add(fragments.get(j));
				functionClones_injectedFunctions_pairs.add(new CloneClass<Fragment>(set));
			}
		}
	}
	
	for(LinkedHashSet<CloneClass<Fragment>> lhs : functionClones_original) {
		for(CloneClass<Fragment> cclass : lhs) {
			LinkedList<Fragment> fragments = new LinkedList<Fragment>(cclass.getFragments());
			for(int i = 0; i < fragments.size(); i++) {
				for(int j = i+1; j < fragments.size(); j++) {
					LinkedHashSet<Fragment> set = new LinkedHashSet<Fragment>();
					set.add(fragments.get(i));
					set.add(fragments.get(j));
					functionClones_original_pairs.add(new CloneClass<Fragment>(set));
				}
			}
		}
	}
		
System.out.println(">> Parsing NiCad results...");
//Get NiCad Results
	//Get File Results
		for(CloneClass<Path> cclass : ParseNiCad.parseNiCadFiles(niCadFileCloneReport)) {
			LinkedHashSet<Path> paths = new LinkedHashSet<Path>();
			for(Path path : cclass) {
				paths.add(evaluationDirectory.relativize(path));
			}
			nicad_fileResults.add(new CloneClass<Path>(paths));
		}
		System.out.println("\t" + nicad_fileResults.size() + " file clone classes found.");
		
	//Get Path Results (clone pairs, trim internal clones)
		for(CloneClass<Path> cclass : nicad_fileResults) {
			LinkedList<Path> paths = new LinkedList<Path>(cclass.getFragments());
			int num = paths.size();
			for(int i =0; i < num; i++) {
				for(int j = i + 1; j < num; j++) {
					Set<Path> pair = new LinkedHashSet<Path>();
					Path p1 = paths.get(i);
					Path p2 = paths.get(j);
					pair.add(p1);
					pair.add(p2);
					if(!p1.getName(0).equals(p2.getName(0))) {
						nicad_fileResults_pairs_trimmedInternalClones.add(new CloneClass<Path>(pair));
					}
				}
			}
		}
		System.out.println("\t" + nicad_fileResults_pairs_trimmedInternalClones.size() + " file clone pairs found.");
		
	//Get Function Results
		for(CloneClass<Fragment> cclass : ParseNiCad.parseNiCadFragments(niCadFunctionCloneReport)) {
			LinkedHashSet<Fragment> fragments = new LinkedHashSet<Fragment>();
			for(Fragment fragment : cclass) {
				fragments.add(new Fragment(evaluationDirectory.relativize(fragment.getSrcFile()), fragment.getStartLine(), fragment.getEndLine()));
			}
			nicad_functionResults.add(new CloneClass<Fragment>(fragments));
		}
		System.out.println("\t" + nicad_functionResults.size() + " function clone classes found.");
	
	//Get Function Results (clone pairs, trim internal clones)
		for(CloneClass<Fragment> cclass : nicad_functionResults) {
			LinkedList<Fragment> fragments = new LinkedList<Fragment>(cclass.getFragments());
			int num = fragments.size();
			for(int i = 0; i < num; i++) {
				for(int j = i+1; j < num; j++) {
					Set<Fragment> pair = new LinkedHashSet<Fragment>();
					Fragment f1 = fragments.get(i);
					Fragment f2 = fragments.get(j);
					pair.add(f1);
					pair.add(f2);
					if(!f1.getSrcFile().getName(0).equals(f2.getSrcFile().getName(0))) {
						nicad_functionResults_pairs_trimmedInternalClones.add(new CloneClass<Fragment>(pair));
					}
				}
			}
		}
		System.out.println("\t" + nicad_functionResults_pairs_trimmedInternalClones.size() + " file clone pairs found.");
		
		if(0 == 0) return;
		
System.out.println(">>>> Evaluating NiCad for Files <<<<");
//Evaluate NiCad
//Evaluate File Clones
	//Injected File Clones
	{
		System.out.println("FileVariant File Clone Classes Recall Performance:");
		
		int efv_numVariant = 0;
		int efv_numCloneClasses = 0;
		int efv_numCloneClassesSubsumed = 0;
		int efv_numCloneClassesMatched = 0;
		double efv_numCloneClassesPartial = 0.0;
		CloneClass<Path> evf_bestPartialMatch;
		boolean evf_subsumeFound;

		
		double injectedFileRecallSubsumed;
		double injectedFileRecallExact;
		double injectedFileRecallPartial;
		
		System.out.println("FileVariant Detection:");
		
		//Iterate through each file variant clone class
		for(CloneClass<Path> evh_fvariant_cclass : fileClones_injectedFiles) {
			efv_numVariant++;
			System.out.print("\tFileVariant " + efv_numVariant + " ... ");
			
			//If size less than 2, skip (not a cross-fork similarity)
			if(evh_fvariant_cclass.getFragments().size() < 2) {
				System.out.println("Skipped.  Only one injection: not a source of clones.");
				continue;
				
			//Else, consider
			} else {
				efv_numCloneClasses++; //increment number of clone classes considered
				
				//If exact match
				if(nicad_fileResults.contains(evh_fvariant_cclass)) {
					//Update Statistics
					efv_numCloneClassesMatched++; //incremenet # exact matches
					efv_numCloneClassesSubsumed++; //exact match implies subsume
					efv_numCloneClassesPartial+=1.0; //exact match implies full "partial" match
					
					//Report
					System.out.println("Exact Match Found.");
					
				//If No Exact Match, search for subsume or partial matches
				} else {
					//Initialize Tracking
					evf_bestPartialMatch = null;
					evf_subsumeFound = false;
					
					//Search through each detected clone class
					for(CloneClass<Path> evh_nicad_cclass : nicad_fileResults) {
						//If subsumes clone class in question, successfully subsumed.
						if(evh_nicad_cclass.subsumes(evh_fvariant_cclass)) {
							//Update Statistics
							efv_numCloneClassesSubsumed++;
							efv_numCloneClassesPartial+=1.0; //subsume implies full "partial" match
							evf_subsumeFound = true;
							
							//Report
							System.out.println("Subsume Match Found.  Extra matches:");
							for(Path efv_path : evh_nicad_cclass) {
								if(!evh_fvariant_cclass.contains(efv_path)) {
									System.out.println("\t\t" + efv_path);
								}
							}
							
							//Found subsume, no reason to continue searching
							break;
							
						//If clone class in question subsumes detected clone class, candidate for best partial match
						} else if(evh_fvariant_cclass.subsumes(evh_nicad_cclass)) {
							//Count number matched
							int evf_numMatched = 0;
							for(Path nef_path : evh_fvariant_cclass) {
								if(evh_nicad_cclass.contains(nef_path)) {
									evf_numMatched++;
								}
							}
							
							//If first match, or better than previous match, remember it
							if(evf_bestPartialMatch == null) {
								if(evf_numMatched > 1) {
									evf_bestPartialMatch = evh_nicad_cclass;
								}
							} else {
								if(evf_bestPartialMatch.size() < evf_numMatched) {
									evf_bestPartialMatch = evh_nicad_cclass;
								}
							}
						}
					}
					
					//If not subsume matched, but a partial match was found, then was a partial match
					if(!evf_subsumeFound && evf_bestPartialMatch!=null) {
						//Calculate score + output details (missed files)
						int evf_total = evh_fvariant_cclass.size();
						int evf_found = 0;
						System.out.println("Partial match found.  Files missed:");
						for(Path evf_path : evh_fvariant_cclass) {
							if(evf_bestPartialMatch.contains(evf_path)) {
								evf_found++;
							} else {
								System.out.println("\t\t" + evf_path);
							}
						}
						//update statistic
						efv_numCloneClassesPartial += (double)evf_found / (double) evf_total;
					}
				}
			}
		}
		injectedFileRecallSubsumed = (double) efv_numCloneClassesSubsumed / (double) efv_numCloneClasses;
		injectedFileRecallExact = (double) efv_numCloneClassesMatched / (double) efv_numCloneClasses;
		injectedFileRecallPartial = efv_numCloneClassesPartial / (double) efv_numCloneClasses;
		
		System.out.println("FileVariant Detection Summary:");
		System.out.println("\t# Clone Classes: " + efv_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + efv_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + efv_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + efv_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + injectedFileRecallSubsumed);
		System.out.println("\tRecall (Exact): " + injectedFileRecallExact);
		System.out.println("\tRecall (Partial): " + injectedFileRecallPartial);
	}
	
	//Evaluate Directory File Clones
	{
		System.out.println("DirectoryVariant File Clone Classes Recall Performance:");
		
		int edv_numVariant = 0;
		int edv_numDirectoryVariant = 0;
		int edv_numCloneClasses = 0;
		int edv_numCloneClassesSubsumed = 0;
		int edv_numCloneClassesMatched = 0;
		double edv_numCloneClassesPartial = 0.0;
		CloneClass<Path> edv_bestPartialMatch;
		boolean edv_subsumeFound;
		
		double injectedDirectoryRecallSubsumed;
		double injectedDirectoryRecallExact;
		double injectedDirectoryRecallPartial;
		
		System.out.println("DirectoryVariant Detection:");
		//Iterate through each Directory Variant
		for(LinkedHashSet<CloneClass<Path>> edv_directoryVariantCloneClassSet : fileClones_injectedDirectories_byVariant) {
			edv_numDirectoryVariant++;
			System.out.println("\tDirectory Variant " + edv_numDirectoryVariant);
			edv_numVariant = 0;
			
			//Iterate through each file variant (clone class) of the directory variant
			for(CloneClass<Path> edv_fvariant_cclass : edv_directoryVariantCloneClassSet) {
				edv_numVariant++;
				System.out.print("\t\tFileVariant " + edv_numVariant + " ... ");
				
				//If size less than 2, skip
				if(edv_fvariant_cclass.size() < 2) {
					System.out.println("Skipped, only injected into one fork.");
				//Else, consider
				} else {
					edv_numCloneClasses++;
					
					//If Exact Match
					if(nicad_fileResults.contains(edv_fvariant_cclass)) {
						//Update Statistics
						edv_numCloneClassesMatched++;
						edv_numCloneClassesSubsumed++;
						edv_numCloneClassesPartial+=1.0;
						
						//Report
						System.out.println("Exact Match Found.");
						
					//If no exact match, search for subsume or partial matches
					} else {
						//Initialize Tracking
						edv_bestPartialMatch = null;
						edv_subsumeFound = false;
						
						//Search through each detected clone class
						for(CloneClass<Path> edv_nicad_cclass : nicad_fileResults) {
							//If subsumes clone class in question
							if(edv_nicad_cclass.subsumes(edv_fvariant_cclass)) {
								//Update Statistics
								edv_numCloneClassesSubsumed++;
								edv_numCloneClassesPartial+=1.0;
								edv_subsumeFound = true;
								
								//Report
								System.out.println("Subsume match found.  Extra matches:");
								for(Path edv_path : edv_nicad_cclass) {
									if(!edv_fvariant_cclass.contains(edv_path)) {
										System.out.println("\t\t\t" + edv_path);
									}
								}
								
								//Found subsume, no reason to continue searching
								break;
								
							// Else, if clone class in question subsumes detected clone class, candidate for best partial match
							} else if (edv_fvariant_cclass.subsumes(edv_nicad_cclass)) {
								//Count number matched
								int edv_numMatched = 0;
								for(Path edv_path : edv_fvariant_cclass) {
									if(edv_nicad_cclass.contains(edv_path)) {
										edv_numMatched++;
									}
								}
								
								//If first match, or better than previous match, remember it
								if(edv_bestPartialMatch == null) {
									if(edv_numMatched > 1) {
										edv_bestPartialMatch = edv_nicad_cclass;
									}
								} else {
									if(edv_bestPartialMatch.size() < edv_numMatched) {
										edv_bestPartialMatch = edv_nicad_cclass;
									}
								}
							}
						}
						
						//If not subsume matched, but a partial match was found, then was a partial match
						if(!edv_subsumeFound && edv_bestPartialMatch != null) {
							//Calculate Score + output details (missed files)
							int edv_total = edv_fvariant_cclass.size();
							int edv_found = 0;
							System.out.println("Partial match found.  Files missed:");
							for(Path edv_path : edv_fvariant_cclass) {
								if(edv_bestPartialMatch.contains(edv_path)) {
									edv_found++;
								} else {
									System.out.println("\t\t\t" + edv_path);
								}
							}
							//update statistic
							edv_numCloneClassesPartial += (double) edv_found / (double) edv_total;
						}
					}
				}
			}			
		}
		
		injectedDirectoryRecallSubsumed = (double) edv_numCloneClassesSubsumed / (double) edv_numCloneClasses;
		injectedDirectoryRecallExact = (double) edv_numCloneClassesMatched / (double) edv_numCloneClasses;
		injectedDirectoryRecallPartial = edv_numCloneClassesPartial / (double) edv_numCloneClasses;
		
		System.out.println("DirectoryVariant (File Variant) Detection Summary");
		System.out.println("\t# Clone Classes: " + edv_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + edv_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + edv_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + edv_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + injectedDirectoryRecallSubsumed);
		System.out.println("\tRecall (Exact): " + injectedDirectoryRecallExact);
		System.out.println("\tRecall (Partial): " + injectedDirectoryRecallPartial);
	}
	
	//Original Not Altered
	{
		int eona_numCloneClasses = 0;
		int eona_numCloneClassesSubsumed = 0;
		int eona_numCloneClassesMatched = 0;
		double eona_numCloneClassesPartial = 0.0;
		CloneClass<Path> eona_bestPartialMatch;
		boolean eona_subsumeFound;

		
		double originalNotAlteredFileRecallSubsumed;
		double originalNotAlteredFileRecallExact;
		double originalNotAlteredFileRecallPartial;
		
		System.out.println("FileVariant Detection:");
		
		//Iterate through each file variant clone class
		for(CloneClass<Path> eona_fvariant_cclass : fileClones_originalNotAltered) {
			System.out.print("\tFile: " + eona_fvariant_cclass.getFragments().iterator().next().toString().replaceFirst("^[^/]*/", "") + " ... ");
			
			//If size less than 2, skip (not a cross-fork similarity)
			if(eona_fvariant_cclass.getFragments().size() < 2) {
				System.out.println("Skipped.  File only in one instance.  What???");
				continue;
				
			//Else, consider
			} else {
				eona_numCloneClasses++; //increment number of clone classes considered
				
				//If exact match
				if(nicad_fileResults.contains(eona_fvariant_cclass)) {
					//Update Statistics
					eona_numCloneClassesMatched++; //incremenet # exact matches
					eona_numCloneClassesSubsumed++; //exact match implies subsume
					eona_numCloneClassesPartial+=1.0; //exact match implies full "partial" match
					
					//Report
					System.out.println("Exact Match Found.");
					
				//If No Exact Match, search for subsume or partial matches
				} else {
					//Initialize Tracking
					eona_bestPartialMatch = null;
					eona_subsumeFound = false;
					
					//Search through each detected clone class
					for(CloneClass<Path> eona_nicad_cclass : nicad_fileResults) {
						//If subsumes clone class in question, successfully subsumed.
						if(eona_nicad_cclass.subsumes(eona_fvariant_cclass)) {
							//Update Statistics
							eona_numCloneClassesSubsumed++;
							eona_numCloneClassesPartial+=1.0; //subsume implies full "partial" match
							eona_subsumeFound = true;
							
							//Report
							System.out.println("Subsume Match Found.  Extra matches:");
							for(Path eona_path : eona_nicad_cclass) {
								if(!eona_fvariant_cclass.contains(eona_path)) {
									System.out.println("\t\t" + eona_path);
								}
							}
							
							//Found subsume, no reason to continue searching
							break;
							
						//If clone class in question subsumes detected clone class, candidate for best partial match
						} else if(eona_fvariant_cclass.subsumes(eona_nicad_cclass)) {
							//Count number matched
							int eona_numMatched = 0;
							for(Path eona_path : eona_fvariant_cclass) {
								if(eona_nicad_cclass.contains(eona_path)) {
									eona_numMatched++;
								}
							}
							
							//If first match, or better than previous match, remember it
							if(eona_bestPartialMatch == null) {
								if(eona_numMatched > 1) {
									eona_bestPartialMatch = eona_nicad_cclass;
								}
							} else {
								if(eona_bestPartialMatch.size() < eona_numMatched) {
									eona_bestPartialMatch = eona_nicad_cclass;
								}
							}
						}
					}
					
					//If not subsume matched, but a partial match was found, then was a partial match
					if(!eona_subsumeFound && eona_bestPartialMatch!=null) {
						//Calculate score + output details (missed files)
						int eona_total = eona_fvariant_cclass.size();
						int eona_found = 0;
						System.out.println("Partial match found.  Files missed:");
						for(Path eona_path : eona_fvariant_cclass) {
							if(eona_bestPartialMatch.contains(eona_path)) {
								eona_found++;
							} else {
								System.out.println("\t\t" + eona_path);
							}
						}
						//update statistic
						eona_numCloneClassesPartial += (double)eona_found / (double) eona_total;
					}
				}
			}
		}
		originalNotAlteredFileRecallSubsumed = (double) eona_numCloneClassesSubsumed / (double) eona_numCloneClasses;
		originalNotAlteredFileRecallExact = (double) eona_numCloneClassesMatched / (double) eona_numCloneClasses;
		originalNotAlteredFileRecallPartial = eona_numCloneClassesPartial / (double) eona_numCloneClasses;
		
		System.out.println("Original And Not Altered Detection Summary:");
		System.out.println("\t# Clone Classes: " + eona_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + eona_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + eona_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + eona_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + originalNotAlteredFileRecallSubsumed);
		System.out.println("\tRecall (Exact): " + originalNotAlteredFileRecallExact);
		System.out.println("\tRecall (Partial): " + originalNotAlteredFileRecallPartial);	
	}
	
//Original Altered + Trimmed
	{
		int eoat_numCloneClasses = 0;
		int eoat_numCloneClassesSubsumed = 0;
		int eoat_numCloneClassesMatched = 0;
		double eoat_numCloneClassesPartial = 0.0;
		CloneClass<Path> eoat_bestPartialMatch;
		boolean eoat_subsumeFound;

		
		double originalAlteredTrimmedFileRecallSubsumed;
		double originalAlteredTrimmedFileRecallExact;
		double originalAlteredTrimmedFileRecallPartial;
		
		System.out.println("Original Altered & Trimmed File Detection:");
		
		//Iterate through each file variant clone class
		for(CloneClass<Path> eoat_fvariant_cclass : fileClones_originalAltered_trimmed) {
			if(eoat_fvariant_cclass.size() == 0) {
				continue;
			}
			
			System.out.print("\tFile: " + eoat_fvariant_cclass.getFragments().iterator().next().toString().replaceFirst("^[^/]*/", "") + " ... ");
			
			//If size less than 2, skip (not a cross-fork similarity)
			if(eoat_fvariant_cclass.getFragments().size() < 2) {
				System.out.println("Skipped.  File only in one instance.");
				continue;
				
			//Else, consider
			} else {
				eoat_numCloneClasses++; //increment number of clone classes considered
				
				//If exact match
				if(nicad_fileResults.contains(eoat_fvariant_cclass)) {
					//Update Statistics
					eoat_numCloneClassesMatched++; //increment # exact matches
					eoat_numCloneClassesSubsumed++; //exact match implies subsume
					eoat_numCloneClassesPartial+=1.0; //exact match implies full "partial" match
					
					//Report
					System.out.println("Exact Match Found.");
					
				//If No Exact Match, search for subsume or partial matches
				} else {
					//Initialize Tracking
					eoat_bestPartialMatch = null;
					eoat_subsumeFound = false;
					
					//Search through each detected clone class
					for(CloneClass<Path> eoat_nicad_cclass : nicad_fileResults) {
						//If subsumes clone class in question, successfully subsumed.
						if(eoat_nicad_cclass.subsumes(eoat_fvariant_cclass)) {
							//Update Statistics
							eoat_numCloneClassesSubsumed++;
							eoat_numCloneClassesPartial+=1.0; //subsume implies full "partial" match
							eoat_subsumeFound = true;
							
							//Report
							System.out.println("Subsume Match Found.  Extra matches:");
							for(Path eoat_path : eoat_nicad_cclass) {
								if(!eoat_fvariant_cclass.contains(eoat_path)) {
									System.out.println("\t\t" + eoat_path);
								}
							}
							
							//Found subsume, no reason to continue searching
							break;
							
						//If clone class in question subsumes detected clone class, candidate for best partial match
						} else if(eoat_fvariant_cclass.subsumes(eoat_nicad_cclass)) {
							//Count number matched
							int eoat_numMatched = 0;
							for(Path eona_path : eoat_fvariant_cclass) {
								if(eoat_nicad_cclass.contains(eona_path)) {
									eoat_numMatched++;
								}
							}
							
							//If first match, or better than previous match, remember it
							if(eoat_bestPartialMatch == null) {
								if(eoat_numMatched > 1) {
									eoat_bestPartialMatch = eoat_nicad_cclass;
								}
							} else {
								if(eoat_bestPartialMatch.size() < eoat_numMatched) {
									eoat_bestPartialMatch = eoat_nicad_cclass;
								}
							}
						}
					}
					
					//If not subsume matched, but a partial match was found, then was a partial match
					if(!eoat_subsumeFound && eoat_bestPartialMatch!=null) {
						//Calculate score + output details (missed files)
						int eoat_total = eoat_fvariant_cclass.size();
						int eoat_found = 0;
						System.out.println("Partial match found.  Files missed:");
						for(Path eoat_path : eoat_fvariant_cclass) {
							if(eoat_bestPartialMatch.contains(eoat_path)) {
								eoat_found++;
							} else {
								System.out.println("\t\t" + eoat_path);
							}
						}
						//update statistic
						eoat_numCloneClassesPartial += (double) eoat_found / (double) eoat_total;
					}
				}
			}
		}
		originalAlteredTrimmedFileRecallSubsumed = (double) eoat_numCloneClassesSubsumed / (double) eoat_numCloneClasses;
		originalAlteredTrimmedFileRecallExact = (double) eoat_numCloneClassesMatched / (double) eoat_numCloneClasses;
		originalAlteredTrimmedFileRecallPartial = eoat_numCloneClassesPartial / (double) eoat_numCloneClasses;
		
		System.out.println("Original And Altered And Trimmed Detection Summary:");
		System.out.println("\t# Clone Classes: " + eoat_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + eoat_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + eoat_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + eoat_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + originalAlteredTrimmedFileRecallSubsumed);
		System.out.println("\tRecall (Exact): " + originalAlteredTrimmedFileRecallExact);
		System.out.println("\tRecall (Partial): " + originalAlteredTrimmedFileRecallPartial);	
	}
		
//Original Altered + Kept
	{
		int eoak_numCloneClasses = 0;
		int eoak_numCloneClassesSubsumed = 0;
		int eoak_numCloneClassesMatched = 0;
		double eoak_numCloneClassesPartial = 0.0;
		CloneClass<Path> eoak_bestPartialMatch;
		boolean eoak_subsumeFound;

		
		double originalAlteredKeptFileRecallSubsumed;
		double originalAlteredKeptFileRecallExact;
		double originalAlteredKeptFileRecallPartial;
		
		System.out.println("Original Altered & Kept File Detection:");
		
		//Iterate through each file variant clone class
		for(CloneClass<Path> eoak_fvariant_cclass : fileClones_originalAltered_kept) {
			System.out.print("\tFile: " + eoak_fvariant_cclass.getFragments().iterator().next().toString().replaceFirst("^[^/]*/", "") + " ... ");
			
			//If size less than 2, skip (not a cross-fork similarity)
			if(eoak_fvariant_cclass.getFragments().size() < 2) {
				System.out.println("Skipped.  File only in one instance.");
				continue;
				
			//Else, consider
			} else {
				eoak_numCloneClasses++; //increment number of clone classes considered
				
				//If exact match
				if(nicad_fileResults.contains(eoak_fvariant_cclass)) {
					//Update Statistics
					eoak_numCloneClassesMatched++; //increment # exact matches
					eoak_numCloneClassesSubsumed++; //exact match implies subsume
					eoak_numCloneClassesPartial+=1.0; //exact match implies full "partial" match
					
					//Report
					System.out.println("Exact Match Found.");
					
				//If No Exact Match, search for subsume or partial matches
				} else {
					//Initialize Tracking
					eoak_bestPartialMatch = null;
					eoak_subsumeFound = false;
					
					//Search through each detected clone class
					for(CloneClass<Path> eoak_nicad_cclass : nicad_fileResults) {
						//If subsumes clone class in question, successfully subsumed.
						if(eoak_nicad_cclass.subsumes(eoak_fvariant_cclass)) {
							//Update Statistics
							eoak_numCloneClassesSubsumed++;
							eoak_numCloneClassesPartial+=1.0; //subsume implies full "partial" match
							eoak_subsumeFound = true;
							
							//Report
							System.out.println("Subsume Match Found.  Extra matches:");
							for(Path eoat_path : eoak_nicad_cclass) {
								if(!eoak_fvariant_cclass.contains(eoat_path)) {
									System.out.println("\t\t" + eoat_path);
								}
							}
							
							//Found subsume, no reason to continue searching
							break;
							
						//If clone class in question subsumes detected clone class, candidate for best partial match
						} else if(eoak_fvariant_cclass.subsumes(eoak_nicad_cclass)) {
							//Count number matched
							int eoak_numMatched = 0;
							for(Path eonk_path : eoak_fvariant_cclass) {
								if(eoak_nicad_cclass.contains(eonk_path)) {
									eoak_numMatched++;
								}
							}
							
							//If first match, or better than previous match, remember it
							if(eoak_bestPartialMatch == null) {
								if(eoak_numMatched > 1) {
									eoak_bestPartialMatch = eoak_nicad_cclass;
								}
							} else {
								if(eoak_bestPartialMatch.size() < eoak_numMatched) {
									eoak_bestPartialMatch = eoak_nicad_cclass;
								}
							}
						}
					}
					
					//If not subsume matched, but a partial match was found, then was a partial match
					if(!eoak_subsumeFound && eoak_bestPartialMatch!=null) {
						//Calculate score + output details (missed files)
						int eoak_total = eoak_fvariant_cclass.size();
						int eoak_found = 0;
						System.out.println("Partial match found.  Files missed:");
						for(Path eoak_path : eoak_fvariant_cclass) {
							if(eoak_bestPartialMatch.contains(eoak_path)) {
								eoak_found++;
							} else {
								System.out.println("\t\t" + eoak_path);
							}
						}
						//update statistic
						eoak_numCloneClassesPartial += (double) eoak_found / (double) eoak_total;
					}
				}
			}
		}
		originalAlteredKeptFileRecallSubsumed = (double) eoak_numCloneClassesSubsumed / (double) eoak_numCloneClasses;
		originalAlteredKeptFileRecallExact = (double) eoak_numCloneClassesMatched / (double) eoak_numCloneClasses;
		originalAlteredKeptFileRecallPartial = eoak_numCloneClassesPartial / (double) eoak_numCloneClasses;
		
		System.out.println("Original And Altered And Trimmed Detection Summary:");
		System.out.println("\t# Clone Classes: " + eoak_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + eoak_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + eoak_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + eoak_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + originalAlteredKeptFileRecallSubsumed);
		System.out.println("\tRecall (Exact): " + originalAlteredKeptFileRecallExact);
		System.out.println("\tRecall (Partial): " + originalAlteredKeptFileRecallPartial);	
	}
	
System.out.println(">> Evaluating NiCad Function Clones");
	//Injected File Clones
	{
		System.out.println("FileVariant Function Clone Classes Recall Performance:");
		
		int effv_numVariant = 0;
		int effv_numFragment = 0;
		int effv_numCloneClasses = 0;
		int effv_numCloneClassesSubsumed = 0;
		int effv_numCloneClassesMatched = 0;
		double effv_numCloneClassesPartial = 0.0;
		CloneClass<Fragment> effv_bestPartialMatch;
		boolean effv_subsumeFound;

		
		double injectedFileFunctionRecallSubsumed;
		double injectedFileFunctionRecallExact;
		double injectedFileFunctionRecallPartial;
		
		System.out.println("FileVariant Function Clone Detection:");
		
		//Iterate through each file variant
		for(LinkedHashSet<CloneClass<Fragment>> effv_cloneClasses : functionClones_injectedFiles_byVariant) {
			effv_numVariant++;
			effv_numFragment = 0;
			System.out.println("\tFile Variant " + effv_numVariant);
			
			//Iterate through each function clone class 
			for(CloneClass<Fragment> effv_cclass : effv_cloneClasses) {
				effv_numFragment++;
				System.out.print("\t\tFragment " + effv_numFragment + " ... ");
				
				//If size less than 2, skip (not a corss-fork similarity)
				if(effv_cclass.getFragments().size() < 2) {
					System.out.println("Skipped.  Only one injection: not a source of clones.");
					continue;
				
				//Else, consider
				} else {
					effv_numCloneClasses++;
					
					//If exact match
					if(nicad_functionResults.contains(effv_cclass)) {
						//Update Statistics
						effv_numCloneClassesMatched++;    // exact match
						effv_numCloneClassesSubsumed++;   // exact match implies subsume
						effv_numCloneClassesPartial+=1.0; // exact match implies full "partial" match
						
						//Report
						System.out.println("Exact match found.");
						
					//If no exact match, search for subsume or partial matches
					} else {
						//Initialize tracking
						effv_bestPartialMatch = null;
						effv_subsumeFound = false;
						
						//search through each detected clone class
						for(CloneClass<Fragment> effv_nicad_cclass : nicad_functionResults) {
							//if subsumes clone class in question, successfully subsumed
							if(effv_nicad_cclass.subsumes(effv_cclass)) {
								//Update Statistics
								effv_numCloneClassesSubsumed++;
								effv_numCloneClassesPartial+=1.0;
								effv_subsumeFound = true;
								
								//Report
								System.out.println("Subsume Match Found.  Extra matches:");
								for(Fragment effv_fragment: effv_nicad_cclass) {
									if(!effv_cclass.contains(effv_fragment)) {
										System.out.println("\t\t\t" + effv_fragment.getSrcFile() + " " + effv_fragment.getStartLine() + " " + effv_fragment.getEndLine());
									}
								}
								
								//found subsume, no reason to continue searching
								break;
							
							//If clone class in question subsumes detected clone class, candidate for best partial match
							} else if (effv_cclass.subsumes(effv_nicad_cclass)) {
								//count number matched
								int effv_numMatched = 0;
								for(Fragment effv_fragment : effv_cclass) {
									if(effv_nicad_cclass.contains(effv_fragment)) {
										effv_numMatched++;
									}
								}
								
								//if first match, or better than previous match, remember it
								if(effv_bestPartialMatch == null) {
									if(effv_numMatched > 1) {
										effv_bestPartialMatch = effv_nicad_cclass;
									}
								} else {
									if(effv_bestPartialMatch.size() < effv_numMatched) {
										effv_bestPartialMatch = effv_nicad_cclass;
									}
								}
							}	
						}
						
						//if not subsume matched, but a partial match was found, then was a partial match
						if(!effv_subsumeFound) {
							if(effv_bestPartialMatch != null) {
								//Calculate score + output details (missed files)
								int effv_total = effv_cclass.size();
								int effv_found = 0;
								System.out.println("Partial match found.  Files missed:");
								for(Fragment effv_fragment : effv_cclass) {
									if(effv_bestPartialMatch.contains(effv_fragment)) {
										effv_found++;
									} else {
										System.out.println("\t\t" + effv_fragment.getSrcFile() + " " + effv_fragment.getStartLine() + " " + effv_fragment.getEndLine());
									}
								}
								
								//update stats
								effv_numCloneClassesPartial += (double) effv_found / (double) effv_total;
							} else {
								System.out.println("No match found.");
							}
						}
					}
				}
			}
		}
		
		injectedFileFunctionRecallSubsumed = (double) effv_numCloneClassesSubsumed / (double) effv_numCloneClasses;
		injectedFileFunctionRecallExact = (double) effv_numCloneClassesMatched / (double) effv_numCloneClasses;
		injectedFileFunctionRecallPartial = effv_numCloneClassesPartial / (double) effv_numCloneClasses;
		
		System.out.println("FileVariant Function Clone Class Detection Summary:");
		System.out.println("\t# Clone Classes: " + effv_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + effv_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + effv_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + effv_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + injectedFileFunctionRecallSubsumed);
		System.out.println("\tRecall (Exact): " + injectedFileFunctionRecallExact);
		System.out.println("\tRecall (Partial): " + injectedFileFunctionRecallPartial);
	}
	
	//Injected File Clones
	{
		System.out.println(">>DirectoryVariant Function Clone Classes Recall Performance:");
		
		int ffedv_numVariant = 0;
		int ffedv_numFile = 0;
		int ffedv_numFragment = 0;
		int ffedv_numCloneClasses = 0;
		int ffedv_numCloneClassesSubsumed = 0;
		int ffedv_numCloneClassesMatched = 0;
		double ffedv_numCloneClassesPartial = 0.0;
		CloneClass<Fragment> ffedv_bestPartialMatch;
		boolean ffedv_subsumeFound;

		
		double injectedDirectoryFunctionRecallSubsumed;
		double injectedDirectoryFunctionRecallExact;
		double injectedDirectoryFunctionRecallPartial;
		
		System.out.println("DirectoryVariant Function Clone Detection:");
		
		//Iterate through each directory variant
		for(LinkedList<LinkedHashSet<CloneClass<Fragment>>> effv_cloneClasses_forDirectoryVariant : functionClones_injectedDirectories_byVariantByFile) {
			ffedv_numVariant++;
			ffedv_numFile = 0;
			System.out.println("\tDirectory Variant " + ffedv_numVariant);
			
			//Iterate through each file
			for(LinkedHashSet<CloneClass<Fragment>> effv_cloneClasses : effv_cloneClasses_forDirectoryVariant) {
				ffedv_numFile++;
				ffedv_numFragment = 0;
				System.out.println("\t\tFile " + ffedv_numFile);
				
				//Iterate through each function clone class 
				for(CloneClass<Fragment> effv_cclass : effv_cloneClasses) {
					ffedv_numFragment++;
					System.out.print("\t\t\tFragment " + ffedv_numFragment + " ... ");
					
					//If size less than 2, skip (not a corss-fork similarity)
					if(effv_cclass.getFragments().size() < 2) {
						System.out.println("Skipped.  Only one injection: not a source of clones.");
						continue;
					
					//Else, consider
					} else {
						ffedv_numCloneClasses++;
						
						//If exact match
						if(nicad_functionResults.contains(effv_cclass)) {
							//Update Statistics
							ffedv_numCloneClassesMatched++;    // exact match
							ffedv_numCloneClassesSubsumed++;   // exact match implies subsume
							ffedv_numCloneClassesPartial+=1.0; // exact match implies full "partial" match
							
							//Report
							System.out.println("Exact match found.");
							
						//If no exact match, search for subsume or partial matches
						} else {
							//Initialize tracking
							ffedv_bestPartialMatch = null;
							ffedv_subsumeFound = false;
							
							//search through each detected clone class
							for(CloneClass<Fragment> effv_nicad_cclass : nicad_functionResults) {
								//if subsumes clone class in question, successfully subsumed
								if(effv_nicad_cclass.subsumes(effv_cclass)) {
									//Update Statistics
									ffedv_numCloneClassesSubsumed++;
									ffedv_numCloneClassesPartial+=1.0;
									ffedv_subsumeFound = true;
									
									//Report
									System.out.println("Subsume Match Found.  Extra matches:");
									for(Fragment effv_fragment: effv_nicad_cclass) {
										if(!effv_cclass.contains(effv_fragment)) {
											System.out.println("\t\t\t\t" + effv_fragment.getSrcFile() + " " + effv_fragment.getStartLine() + " " + effv_fragment.getEndLine());
										}
									}
									
									//found subsume, no reason to continue searching
									break;
								
								//If clone class in question subsumes detected clone class, candidate for best partial match
								} else if (effv_cclass.subsumes(effv_nicad_cclass)) {
									//count number matched
									int effv_numMatched = 0;
									for(Fragment effv_fragment : effv_cclass) {
										if(effv_nicad_cclass.contains(effv_fragment)) {
											effv_numMatched++;
										}
									}
									
									//if first match, or better than previous match, remember it
									if(ffedv_bestPartialMatch == null) {
										if(effv_numMatched > 1) {
											ffedv_bestPartialMatch = effv_nicad_cclass;
										}
									} else {
										if(ffedv_bestPartialMatch.size() < effv_numMatched) {
											ffedv_bestPartialMatch = effv_nicad_cclass;
										}
									}
								}	
							}
							
							//if not subsume matched, but a partial match was found, then was a partial match
							if(!ffedv_subsumeFound) {
								if(ffedv_bestPartialMatch != null) {
									//Calculate score + output details (missed files)
									int effv_total = effv_cclass.size();
									int effv_found = 0;
									System.out.println("Partial match found.  Files missed:");
									for(Fragment effv_fragment : effv_cclass) {
										if(ffedv_bestPartialMatch.contains(effv_fragment)) {
											effv_found++;
										} else {
											System.out.println("\t\t\t" + effv_fragment.getSrcFile() + " " + effv_fragment.getStartLine() + " " + effv_fragment.getEndLine());
										}
									}
									
									//update stats
									ffedv_numCloneClassesPartial += (double) effv_found / (double) effv_total;
								} else {
									System.out.println("No match found.");
								}
							}
						}
					}
				}
			}
		}
		
		injectedDirectoryFunctionRecallSubsumed = (double) ffedv_numCloneClassesSubsumed / (double) ffedv_numCloneClasses;
		injectedDirectoryFunctionRecallExact = (double) ffedv_numCloneClassesMatched / (double) ffedv_numCloneClasses;
		injectedDirectoryFunctionRecallPartial = ffedv_numCloneClassesPartial / (double) ffedv_numCloneClasses;
		
		System.out.println("DirectoryVariant Function Clone Class Detection Summary:");
		System.out.println("\t# Clone Classes: " + ffedv_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + ffedv_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + ffedv_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + ffedv_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + injectedDirectoryFunctionRecallSubsumed);
		System.out.println("\tRecall (Exact): " + injectedDirectoryFunctionRecallExact);
		System.out.println("\tRecall (Partial): " + injectedDirectoryFunctionRecallPartial);
	}

	//Injected function clones
	{
		System.out.println(">>FunctionVariant Function Clone Class Recall Performance");
		int ffefv_numVariant = 0;
		int ffefv_numCloneClasses = 0;
		int ffefv_numCloneClassesSubsumed = 0;
		int ffefv_numCloneClassesMatched = 0;
		double ffefv_numCloneClassesPartial = 0.0;
		CloneClass<Fragment> ffefv_bestPartialMatch;
		boolean ffefv_subsumeFound;
		
		double injectedFunctionFunctionRecallSubsumed;
		double injectedFunctionFunctionRecallExact;
		double injectedFunctionFunctionRecallPartial;
		
		System.out.println("FunctionVariant Function Clone Detection:");
		
		//Iterate through each function variant (cclass)
		for(CloneClass<Fragment> ffefv_cclass : functionClones_injectedFunctions) {
			ffefv_numVariant++;
			System.out.print("\tFunction Variant " + ffefv_numVariant + " ... ");
			
			//if size less than 2, skip
			if(ffefv_cclass.size() < 2) {
				System.out.println("Skipped.  Only one injection: not a source of clones.");
				continue;
				
			//Else, consider
			} else {
				ffefv_numCloneClasses++;
				
				//if exact match
				if(nicad_functionResults.contains(ffefv_cclass)) {
					// Update Statistics
					ffefv_numCloneClassesMatched++;
					ffefv_numCloneClassesSubsumed++;
					ffefv_numCloneClassesPartial+=1.0;
					
					//Report
					System.out.println("Exact match found.");
					
				//If no exact match, search for subsume or partial match
				} else {
					//Initialize tracking
					ffefv_bestPartialMatch = null;
					ffefv_subsumeFound = false;
					
					//search through each detected clone class
					for(CloneClass<Fragment> ffefv_nicad_cclass : nicad_functionResults) {
						//if subsumes clone class in question, successfully subsumed
						if(ffefv_nicad_cclass.subsumes(ffefv_cclass)) {
							//Update statistics
							ffefv_numCloneClassesSubsumed++;
							ffefv_numCloneClassesPartial+=1.0;
							ffefv_subsumeFound = true;
							
							//Report
							System.out.println("Subsume Match Found.  Extra matches:");
							for(Fragment ffefv_fragment : ffefv_nicad_cclass) {
								if(ffefv_cclass.contains(ffefv_fragment)) {
									System.out.println("\t\t" + ffefv_fragment.getSrcFile() + " " + ffefv_fragment.getStartLine() + " " + ffefv_fragment.getEndLine());
								}
							}
							
							//Found subsume, no reason to continue searching
							break;
							
						//If clone class in question subsumes detected clone class, candidate for best partial match
						} else if(ffefv_cclass.subsumes(ffefv_nicad_cclass)) {
							//count number matched
							int ffefv_numMatched = 0;
							for(Fragment ffefv_fragment : ffefv_cclass) {
								if(ffefv_nicad_cclass.contains(ffefv_fragment)) {
									ffefv_numMatched++;
								}
							}
							
							//if first match, or better than previous match, remember it
							if(ffefv_bestPartialMatch == null) {
								if(ffefv_numMatched > 1) {
									ffefv_bestPartialMatch = ffefv_nicad_cclass;
								}
							} else {
								if(ffefv_bestPartialMatch.size() < ffefv_numMatched) {
									ffefv_bestPartialMatch = ffefv_nicad_cclass;
								}
							}
						}
					}
					
					//if not subsume matched, but a partial match was found, then was a partial match
					if(!ffefv_subsumeFound) {
						if(ffefv_bestPartialMatch != null) {
							//calculate score + output details (missed files)
							int ffefv_total = ffefv_cclass.size();
							int ffefv_found = 0;
							System.out.println("Partial match found.  Files Missed:");
							for(Fragment ffefv_fragment : ffefv_cclass) {
								if(ffefv_bestPartialMatch.contains(ffefv_fragment)) {
									ffefv_found++;
								} else {
									System.out.println("\t\t" + ffefv_fragment.getSrcFile() + " " + ffefv_fragment.getStartLine() + " " + ffefv_fragment.getEndLine());
								}
							}
							
							//update stats
							ffefv_numCloneClassesPartial += (double) ffefv_found / (double) ffefv_total;
						} else {
							System.out.println("No match found.");
						}
					}
				}
			}
		}
		
		injectedFunctionFunctionRecallSubsumed = (double) ffefv_numCloneClassesSubsumed / (double) ffefv_numCloneClasses;
		injectedFunctionFunctionRecallExact = (double) ffefv_numCloneClassesMatched / (double) ffefv_numCloneClasses;
		injectedFunctionFunctionRecallPartial = ffefv_numCloneClassesPartial / (double) ffefv_numCloneClasses;
		
		System.out.println("FunctionVariant Function Clone Class Recall Performance Summary");
		System.out.println("\t# Clone Classes: " + ffefv_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + ffefv_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + ffefv_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + ffefv_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + injectedFunctionFunctionRecallSubsumed);
		System.out.println("\tRecall (Exact): " + injectedFunctionFunctionRecallExact);
		System.out.println("\tRecall (Partial): " + injectedFunctionFunctionRecallPartial);
	}
	
	//Inject function clones (pairs)
	{
		int ffefvp_numClonePairs = 0;
		int ffefvp_numClonePairsDetected = 0;
		
		System.out.println("FunctionVariant Function Clone Detection (pair):");
		for(CloneClass<Fragment> cpair : functionClones_injectedFunctions_pairs) {
			if(cpair.size() != 2) {
				System.out.println("ClonePairError: Evaluate pair injected function clones!");
				return;
			}
			ffefvp_numClonePairs++;
			if(nicad_functionResults_pairs_trimmedInternalClones.contains(cpair)) {
				ffefvp_numClonePairsDetected++;
			}
		}
		System.out.println("FunctionVariant Function Clone Pair Recall Performance Summary");
		System.out.println("\tClone Pairs: " + ffefvp_numClonePairs);
		System.out.println("\t#Found: " + ffefvp_numClonePairsDetected);
	}
	
	//Original File Function Clones
	{
		System.out.println(">>Original File Function Clone Classes Recall Performance:");
		
		int ffeof_numOriginalFile = 0;
		int ffeof_numFragment = 0;
		int ffeof_numCloneClasses = 0;
		int ffeof_numCloneClassesSubsumed = 0;
		int ffeof_numCloneClassesMatched = 0;
		double ffeof_numCloneClassesPartial = 0.0;
		CloneClass<Fragment> ffeof_bestPartialMatch;
		boolean ffeof_subsumeFound;

		
		double originalFileFunctionRecallSubsumed;
		double originalFileFunctionRecallExact;
		double originalFileFunctionRecallPartial;
		
		System.out.println("Original File Function Clone Detection:");
		
		//Iterate through each original file
		for(LinkedHashSet<CloneClass<Fragment>> ffeof_cloneClasses : functionClones_original) {
			ffeof_numOriginalFile++;
			ffeof_numFragment = 0;
			System.out.println("\tOriginal File " + ffeof_numOriginalFile + " " + ffeof_cloneClasses.iterator().next().getFragments().iterator().next().getSrcFile());
			
			//Iterate through each original function clone class 
			for(CloneClass<Fragment> ffeof_cclass : ffeof_cloneClasses) {
				ffeof_numFragment++;
				System.out.print("\t\tFragment " + ffeof_numFragment + " ... ");
				
				//If size less than 2, skip (not a corss-fork similarity)
				if(ffeof_cclass.getFragments().size() < 2) {
					System.out.println("Skipped.  Only one injection: not a source of clones.");
					continue;
				
				//Else, consider
				} else {
					ffeof_numCloneClasses++;
					
					//If exact match
					if(nicad_functionResults.contains(ffeof_cclass)) {
						//Update Statistics
						ffeof_numCloneClassesMatched++;    // exact match
						ffeof_numCloneClassesSubsumed++;   // exact match implies subsume
						ffeof_numCloneClassesPartial+=1.0; // exact match implies full "partial" match
						
						//Report
						System.out.println("Exact match found.");
						
					//If no exact match, search for subsume or partial matches
					} else {
						//Initialize tracking
						ffeof_bestPartialMatch = null;
						ffeof_subsumeFound = false;
						
						//search through each detected clone class
						for(CloneClass<Fragment> ffeof_nicad_cclass : nicad_functionResults) {
							//if subsumes clone class in question, successfully subsumed
							if(ffeof_nicad_cclass.subsumes(ffeof_cclass)) {
								//Update Statistics
								ffeof_numCloneClassesSubsumed++;
								ffeof_numCloneClassesPartial+=1.0;
								ffeof_subsumeFound = true;
								
								//Report
								System.out.println("Subsume Match Found.  Extra matches:");
								for(Fragment ffeof_fragment: ffeof_nicad_cclass) {
									if(!ffeof_cclass.contains(ffeof_fragment)) {
										System.out.println("\t\t\t" + ffeof_fragment.getSrcFile() + " " + ffeof_fragment.getStartLine() + " " + ffeof_fragment.getEndLine());
									}
								}
								
								//found subsume, no reason to continue searching
								break;
							
							//If clone class in question subsumes detected clone class, candidate for best partial match
							} else if (ffeof_cclass.subsumes(ffeof_nicad_cclass)) {
								//count number matched
								int ffeof_numMatched = 0;
								for(Fragment ffeof_fragment : ffeof_cclass) {
									if(ffeof_nicad_cclass.contains(ffeof_fragment)) {
										ffeof_numMatched++;
									}
								}
								
								//if first match, or better than previous match, remember it
								if(ffeof_bestPartialMatch == null) {
									if(ffeof_numMatched > 1) {
										ffeof_bestPartialMatch = ffeof_nicad_cclass;
									}
								} else {
									if(ffeof_bestPartialMatch.size() < ffeof_numMatched) {
										ffeof_bestPartialMatch = ffeof_nicad_cclass;
									}
								}
							}	
						}
						
						//if not subsume matched, but a partial match was found, then was a partial match
						if(!ffeof_subsumeFound) {
							if(ffeof_bestPartialMatch != null) {
								//Calculate score + output details (missed files)
								int ffeof_total = ffeof_cclass.size();
								int ffeof_found = 0;
								System.out.println("Partial match found.  Files missed:");
								for(Fragment ffeof_fragment : ffeof_cclass) {
									if(ffeof_bestPartialMatch.contains(ffeof_fragment)) {
										ffeof_found++;
									} else {
										System.out.println("\t\t" + ffeof_fragment.getSrcFile() + " " + ffeof_fragment.getStartLine() + " " + ffeof_fragment.getEndLine());
									}
								}
								
								//update stats
								ffeof_numCloneClassesPartial += (double) ffeof_found / (double) ffeof_total;
							} else {
								System.out.println("No match found.");
							}
						}
					}
				}
			}
		}
		
		originalFileFunctionRecallSubsumed = (double) ffeof_numCloneClassesSubsumed / (double) ffeof_numCloneClasses;
		originalFileFunctionRecallExact = (double) ffeof_numCloneClassesMatched / (double) ffeof_numCloneClasses;
		originalFileFunctionRecallPartial = ffeof_numCloneClassesPartial / (double) ffeof_numCloneClasses;
		
		System.out.println("Original File Function Clone Class Detection Summary:");
		System.out.println("\t# Clone Classes: " + ffeof_numCloneClasses);
		System.out.println("\t# Exact Match Found: " + ffeof_numCloneClassesMatched);
		System.out.println("\t# Subsume Match Found: " + ffeof_numCloneClassesSubsumed);
		System.out.println("\t# Partial Match Found: " + ffeof_numCloneClassesPartial);
		
		System.out.println("\tRecall (Subsumed): " + originalFileFunctionRecallSubsumed);
		System.out.println("\tRecall (Exact): " + originalFileFunctionRecallExact);
		System.out.println("\tRecall (Partial): " + originalFileFunctionRecallPartial);
	}
	
	//Inject function clones (pairs)
	{
		int ffeofp_numClonePairs = 0;
		int ffeofp_numClonePairsDetected = 0;
		
		System.out.println("Original File Function Clone Detection (pair):");
		for(CloneClass<Fragment> cpair : functionClones_original_pairs) {
			if(cpair.size() != 2) {
				System.out.println("ClonePairError: Evaluate pair original file function clones!");
				return;
			}
			ffeofp_numClonePairs++;
			if(nicad_functionResults_pairs_trimmedInternalClones.contains(cpair)) {
				ffeofp_numClonePairsDetected++;
			}
		}
		System.out.println("Original File Function Clone Pair Recall Performance Summary");
		System.out.println("\tClone Pairs: " + ffeofp_numClonePairs);
		System.out.println("\t#Found: " + ffeofp_numClonePairsDetected);
	}
	
//Precision
//File
	//Make full collection of known fork file clone classes
		LinkedHashSet<CloneClass<Path>> pf_fileCloneClasses = new LinkedHashSet<CloneClass<Path>>();
		pf_fileCloneClasses.addAll(fileClones_injectedFiles); //From file injections
		for(LinkedHashSet<CloneClass<Path>> pf_cloneClassSets : fileClones_injectedDirectories_byVariant) { //From directory injections
			pf_fileCloneClasses.addAll(pf_cloneClassSets);
		}
		pf_fileCloneClasses.addAll(fileClones_originalNotAltered);
		pf_fileCloneClasses.addAll(fileClones_originalAltered_trimmed);
		pf_fileCloneClasses.addAll(fileClones_originalAltered_kept);
		
		
	//Validate using known file clone classes
		int pf_numCloneClasses = 0;
		int pf_numCloneClassesValidated = 0;
		int pf_numCloneClassesRejected = 0;
		boolean pf_validatedUsingKnown;
		
		System.out.println(">>NiCad File Clone Precision:");
		for(CloneClass<Path> pf_nicad_cclass : nicad_fileResults_pairs_trimmedInternalClones) {
			pf_numCloneClasses++;
			System.out.print("\tClonePair #" + pf_numCloneClasses + " ... ");
			pf_validatedUsingKnown = false;
			
			//If exact match, accept
			if(pf_fileCloneClasses.contains(pf_nicad_cclass)) {
				System.out.println("exactly matched by known file clone class.");
				pf_numCloneClassesValidated++;
				nicad_validatedFileClonePairs_usingKnownClones.add(pf_nicad_cclass);
				pf_validatedUsingKnown = true;
			
				//If subsumed
			} else {
				for(CloneClass<Path> pf_known_cclass : pf_fileCloneClasses) {
					if(pf_known_cclass.subsumes(pf_nicad_cclass)) {
						System.out.println("subsume matched by known file clone class");
						pf_numCloneClassesValidated++;
						nicad_validatedFileClonePairs_usingKnownClones.add(pf_nicad_cclass);
						pf_validatedUsingKnown = true;
						break;
					}
				}
			}
			if(!pf_validatedUsingKnown) {
				nicad_unvalidatedFileClonePairs_usingKnownClones.add(pf_nicad_cclass);
				System.out.println(" Could not validate using known file clone classes.");
			}
		}
		
		System.out.println("NiCad File Clone Class Precision Summary:");
		System.out.println("\t#ClonePairs: " + pf_numCloneClasses);
		System.out.println("\t#ValidatedPairs: " + pf_numCloneClassesValidated);
		System.out.println("\t#RejectedPairs: " + pf_numCloneClassesRejected);
		
		System.out.println("Unvalidated Clone Pairs:");
		int pf_numUnvalidatedCloneClasses = 0;
		for(CloneClass<Path> pf_unvalidated_cloneClass : nicad_unvalidatedFileClonePairs_usingKnownClones) {
			pf_numUnvalidatedCloneClasses++;
			System.out.println(pf_numUnvalidatedCloneClasses + ")");
			for(Path pf_path : pf_unvalidated_cloneClass) {
				System.out.println("\t" + pf_path);
			}
		}
		
//Function
	//Make full collection of known fork function clone classes
		LinkedHashSet<CloneClass<Fragment>> pff_functionCloneClasses = new LinkedHashSet<CloneClass<Fragment>>();
		//Add from injected files
		for(LinkedHashSet<CloneClass<Fragment>> pff_set_cclass : functionClones_injectedFiles_byVariant) {
			pff_functionCloneClasses.addAll(pff_set_cclass);
		}
		//add from directory injections
		for(LinkedList<LinkedHashSet<CloneClass<Fragment>>> pff_list_set_cclass : functionClones_injectedDirectories_byVariantByFile) {
			for(LinkedHashSet<CloneClass<Fragment>> pff_set_cclass : pff_list_set_cclass) {
				pff_functionCloneClasses.addAll(pff_set_cclass);
			}
		}
		//Add from functions
		pff_functionCloneClasses.addAll(functionClones_injectedFunctions);
		//Add from original files
		for(LinkedHashSet<CloneClass<Fragment>> pff_set_cclass : functionClones_original) {
			pff_functionCloneClasses.addAll(pff_set_cclass);
		}
		
	//Validate using known file clone classes
		int pff_numCloneClasses = 0;
		int pff_numCloneClassesValidated = 0;
		int pff_numCloneClassesRejected = 0;
		boolean pff_validatedUsingKnown;
		
		System.out.println(">> NiCad Function Clone Pair Precision:");
		for(CloneClass<Fragment> pff_nicad_cclass : nicad_functionResults_pairs_trimmedInternalClones) {
			pff_numCloneClasses++;
			System.out.print("\tClonePair #" + pff_numCloneClasses + " ... ");
			pff_validatedUsingKnown = false;
			
			//If exact match, accept
			if(pff_functionCloneClasses.contains(pff_nicad_cclass)) {
				System.out.println("exactly matched by known function clone class.");
				pf_numCloneClassesValidated++;
				nicad_validatedFunctionClonePairs_usingKnownClones.add(pff_nicad_cclass);
				pff_validatedUsingKnown = true;
			
			//If subsumed
			} else {
				for(CloneClass<Fragment> pff_known_cclass : pff_functionCloneClasses) {
					if(pff_known_cclass.subsumes(pff_nicad_cclass)) {
						System.out.println("subsumed by known function clone class.");
						pff_numCloneClassesValidated++;
						nicad_validatedFunctionClonePairs_usingKnownClones.add(pff_nicad_cclass);
						pff_validatedUsingKnown = true;
						break;
					}
				}
			}
			if(!pff_validatedUsingKnown) {
				nicad_unvalidatedFunctionClonePairs_usingKnownClones.add(pff_nicad_cclass);
				System.out.println("Could not validate using known file clone classes.");
			}
		}
		System.out.println("Unvalidated Clones Pairs:");
		int pff_numUnvaliadtedCloneClasses = 0;
		for(CloneClass<Fragment> pff_unvalidated_cloneClass : nicad_unvalidatedFunctionClonePairs_usingKnownClones) {
			pff_numUnvaliadtedCloneClasses++;
			System.out.println(pff_numUnvaliadtedCloneClasses + ")");
			for(Fragment pff_fragment : pff_unvalidated_cloneClass) {
				System.out.println("\t" + pff_fragment.getSrcFile() + " " + pff_fragment.getStartLine() + " " + pff_fragment.getEndLine());
			}
		}
		System.out.println("NiCad Function Clone Class Precision Summary:");
		System.out.println("\t#CloneClasses: " + pff_numCloneClasses);
		System.out.println("\t#ValidatedClasses: " + pff_numCloneClassesValidated);
		System.out.println("\t#Rejected: " + pff_numCloneClassesRejected);
		
//Test Collected Data
		int test_counter_1 = 0;
		int test_counter_2 = 0;
		int test_counter_3 = 0;
		System.out.println(">>>><<Parse Lists>><<<<");
//FileClones
		System.out.println(">>>>File Clones<<<<");
	//FileClones: File Variants
		System.out.println("");
		System.out.println(">>file clones from injected files<<");
		test_counter_1 = 0;
		for(CloneClass<Path> test_cclass : fileClones_injectedFiles) {
			test_counter_1++;
			System.out.println("FileVariant " + test_counter_1 + ")");
			for(Path test_path : test_cclass) {
				System.out.println("\t" + test_path);
			}
		}
	//FileClones: Directory Variants
		System.out.println("");
		System.out.println(">>file clones from injected directories by variant<<");
		test_counter_1 = 0;
		for(LinkedHashSet<CloneClass<Path>> test_lhs : fileClones_injectedDirectories_byVariant) {
			test_counter_1++;
			test_counter_2 = 0;
			System.out.println("DirectoryVariant " + test_counter_1 + ")");
			for(CloneClass<Path> test_cclass : test_lhs) {
				test_counter_2++;
				System.out.println("\tClass " + test_counter_2 + ")");
				for(Path test_path : test_cclass) {
					System.out.println("\t\t" + test_path);
				}
			}
		}
	//FileClones: Original Files Not Altered in any Fork
		System.out.println("");
		System.out.println(">>File Clone Classes From Original Files (Not Altered)<<");
		test_counter_1 = 0;
		for(CloneClass<Path> test_occ : fileClones_originalNotAltered) {
			test_counter_1++;
			System.out.println("Class " + test_counter_1 + ")");
			for(Path test_p : test_occ) {
				System.out.println("\t" + test_p);
			}
		}
	//FileClones: Original Files Altered in at Least One Fork (modified trimmed)
		System.out.println("");
		System.out.println(">>File Clone Classes From Original Files (Altered) With Modified Trimmed<<");
		test_counter_1 = 0;
		for(CloneClass<Path> test_occ : fileClones_originalAltered_trimmed) {
			test_counter_1++;
			System.out.println("Class " + test_counter_1 + ")");
			for(Path test_p : test_occ) {
				System.out.println("\t" + test_p);
			}
		}
	//FileClones: Original Files Altered in At Least One Fork (Modified Kept)
		System.out.println("");
		System.out.println(">>File Clone Classes From Original Files (Altered) With Modified Kept<<");
		test_counter_1 = 0;
		for(CloneClass<Path> test_occ : fileClones_originalAltered_kept) {
			test_counter_1++;
			System.out.println("Class " + test_counter_1 + ")");
			for(Path test_p : test_occ) {
				System.out.println("\t" + test_p);
			}
		}
//FunctionClones
		System.out.println(">>>>Function Clones<<<<");
	//FunctionClones: File Variants
		System.out.println("");
		System.out.println(">>function clone classes from injected files<<");
		test_counter_1 = 0;
		for(LinkedHashSet<CloneClass<Fragment>> test_cclass_forvariant : functionClones_injectedFiles_byVariant) {
			test_counter_1++;
			System.out.println("File Variant " + test_counter_1 + ")");
			test_counter_2 = 0;
			for(CloneClass<Fragment> test_cclass : test_cclass_forvariant) {
				test_counter_2++;
				System.out.println(test_counter_2 + ")");
				for(Fragment test_fragment : test_cclass) {
					System.out.println("\t\t" + test_fragment.getSrcFile() + " " + test_fragment.getStartLine() + " " + test_fragment.getEndLine());
				}
			}
		}	
	//FunctionClones: Directory Variants
		System.out.println("");
		System.out.println(">>function clones from injected directories by variant and file<<");
		test_counter_1 = 0;
		test_counter_2 = 0;
		test_counter_3 = 0;
		for(LinkedList<LinkedHashSet<CloneClass<Fragment>>> test_ll_lhs : functionClones_injectedDirectories_byVariantByFile) {
			test_counter_1++;
			test_counter_2 = 0;
			System.out.println("DirectoryVariant " + test_counter_1 + ")");
			for(LinkedHashSet<CloneClass<Fragment>> test_ll : test_ll_lhs) {
				test_counter_2++;
				test_counter_3 = 0;
				System.out.println("\tFile " + test_counter_2);
				for(CloneClass<Fragment> test_cclass : test_ll) {
					test_counter_3++;
					System.out.println("\t\tFragment " + test_counter_3);
					for(Fragment test_fragment : test_cclass) {
						System.out.println("\t\t\t" + test_fragment.getSrcFile() + " " + test_fragment.getStartLine() + " " + test_fragment.getEndLine());
					}
				}
			}
		}
	//FunctionClones: Function Variants
		System.out.println("");
		System.out.println(">>function clones from injected functions by variant<<");
		test_counter_1 = 0;
		for(CloneClass<Fragment> test_cclass : functionClones_injectedFunctions) {
			test_counter_1++;
			System.out.println("FragmentVariant " + test_counter_1 + ")");
			for(Fragment test_fragment : test_cclass) {
				System.out.println("\t" + test_fragment.getSrcFile() + " " + test_fragment.getStartLine() + " " + test_fragment.getEndLine());
			}
		}
	//FunctionClones: Original Files
		System.out.println("");
		System.out.println(">>function clones from original files<<");
		test_counter_1 = 0;
		test_counter_2 = 0;
		for(LinkedHashSet<CloneClass<Fragment>> test_lhs : functionClones_original) {
			test_counter_1++;
			test_counter_2 = 0;
			System.out.println("File " + test_counter_1 + ")");
			for(CloneClass<Fragment> test_cclass : test_lhs) {
				test_counter_2++;
				System.out.println("\tFragment " + test_counter_2 + ")");
				for(Fragment test_fragment : test_cclass) {
					System.out.println("\t\t" + test_fragment.getSrcFile() + " " + test_fragment.getStartLine() + " " + test_fragment.getEndLine());
				}
			}
		}
//NiCadResults
		System.out.println("");
		System.out.println(">>>>NiCad Results<<<<");
	//NiCad File Clone Class
		System.out.println("");
		System.out.println(">>NiCad File Clone Class<<");
		test_counter_1 = 0;
		for(CloneClass<Path> test_cclass : nicad_fileResults) {
			test_counter_1++;
			System.out.println(test_counter_1 + ")");
			for(Path test_path : test_cclass) {
				System.out.println("\t" + test_path);
			}
		}
	//NiCad File Clone Pairs, internal trimmed
		System.out.println("");
		System.out.println(">>NiCad File Clone Pairs, Internal Clones Trimmed<<");
		test_counter_1 = 0;
		for(CloneClass<Path> test_cclass : nicad_fileResults_pairs_trimmedInternalClones) {
			test_counter_1++;
			System.out.println(test_counter_1 + ")");
			for(Path test_path : test_cclass) {
				System.out.println("\t" + test_path);
			}
		}
	//NiCad Function Clone Classes
		System.out.println("");
		System.out.println(">>NiCad Function Clone Class<<");
		test_counter_1 = 0;
		for(CloneClass<Fragment> test_cclass : nicad_functionResults) {
			test_counter_1++;
			System.out.println(test_counter_1 + ")");
			for(Fragment test_fragment : test_cclass) {
				System.out.println("\t" + test_fragment.getSrcFile() + " " + test_fragment.getStartLine() + " " + test_fragment.getEndLine());
			}
		}
	//NiCad Function Clone Pairs, internal trimmed
		System.out.println("");
		System.out.println(">>NiCad Function Clone Pairs, Internal Clones Trimmed<<");
		test_counter_1 = 0;
		for(CloneClass<Fragment> test_cclass : nicad_functionResults_pairs_trimmedInternalClones) {
			test_counter_1++;
			System.out.println(test_counter_1 + ")");
			for(Fragment test_fragment : test_cclass) {
				System.out.println("\t" + test_fragment.getSrcFile() + " " + test_fragment.getStartLine() + " " + test_fragment.getEndLine());
			}
		}
	}
	
	public static List<Fragment> getFunctionFragmentsInFile(Path file, String language) throws IOException {
		//System.out.print  ("Analyzing: ");
		//System.out.println(file);
		
		List<Fragment> retval;
		List<Fragment> retval_fixed;
		file = file.toAbsolutePath().normalize();
		
		Path dir = Files.createTempDirectory(SystemUtil.getTemporaryDirectory(), "ForkSim_");
		Files.copy(file, dir.resolve(file.getFileName()));
		
		retval = SelectFunctionFragments.getFunctionFragments(dir.toFile(), language);
		
		retval_fixed = new LinkedList<Fragment>();
		for(Fragment f : retval) {
			retval_fixed.add(new Fragment(file, f.getStartLine(), f.getEndLine()));
			//System.out.println("\t" + file + " " + f.getStartLine() + " " + f.getEndLine());
		}
		
		FileUtils.deleteDirectory(dir.toFile());
		
		return retval_fixed;
	}
}
