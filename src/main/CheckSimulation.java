package main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import models.Fragment;
import models.Operator;

import org.apache.commons.io.FileUtils;

import util.FileUtil;
import util.FragmentUtil;
import util.InventoriedSystem;
import util.SystemUtil;

public class CheckSimulation {
	
	@SuppressWarnings("resource")
	public static void main(String args[]) throws IOException {
// Check Inputs
		if (args.length != 2) {
			System.out.println("Input Parameters: properties output");
			return;
		}

		Path propertiesf = Paths.get(args[0]);
		Path output = Paths.get(args[1]);
		if (!Files.isReadable(propertiesf) || !Files.isRegularFile(propertiesf)) {
			System.out.println("Properties file is invalid.");
			return;
		}
		if (!Files.isReadable(output) || !Files.isRegularFile(output)) {
			System.out.println("Output file is invalid.");
			return;
		}
		
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
		
		
// Open Log
		Scanner in = new Scanner(output.toFile());
		String line;	
		
// --- CHECK PROPERTIES ---------------------------------------------------------------------------
		System.out.println("Checking properties.");
		
		Properties properties = new Properties(propertiesf, method_mutation_operators, file_mutation_operators, dir_mutation_operators);
		
	//Header
		line = in.nextLine();
		if(!line.equals("BEGIN: Properties")) {
			System.out.println("Expected 'Begin: Properties' but saw: " + line);
			System.exit(-1);
		}
		
	//Properties
		//Output Directory
			//Read
		line = in.nextLine();
		if(!line.startsWith("\toutput_directory=")) {
			System.out.println("Expected '\toutput_directory=' but saw: " + line);
			System.exit(-1);
		}
		Path outputdir = Paths.get(line.replaceFirst("\toutput_directory=", ""));

		//System Directory
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tsystem_directory=")) {
			System.out.println("Expected '\tsystem_directory=' but saw: " + line);
			System.exit(-1);
		}
		Path systemdir = Paths.get(line.replaceFirst("\tsystem_directory=", ""));
			//Check
		if(!systemdir.toAbsolutePath().normalize().equals(properties.getSystem().toAbsolutePath().normalize())) {
			System.out.println("System directory does not match. Properties: " + properties.getSystem() + ", Output: " + systemdir);
			System.exit(-1);
		}
				
		//Repository Directory
		line = in.nextLine();
		if(!line.startsWith("\trepository_directory=")) {
			System.out.println("Expected '\trepository_directory=' but saw: " + line);
			System.exit(-1);
		}
		Path repositorydir = Paths.get(line.replaceFirst("\trepository_directory=", ""));
			//Check
		if(!repositorydir.toAbsolutePath().normalize().equals(properties.getRepository().toAbsolutePath().normalize())) {
			System.out.println("Repository directory does not match. Properties: " + properties.getRepository() + ", Output: " + repositorydir);
			System.exit(-1);
		}
		
		//Language
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tlanguage=")) {
			System.out.println("Expected '\tlanguage=' but saw: " + line);
			System.exit(-1);
		}
		String language = line.replaceFirst("\tlanguage=", "");
			//Check
		if(!language.equals(properties.getLanguage())) {
			System.out.println("Language does not match. Properties: " + properties.getLanguage() + ", Output: " + language);
			System.exit(-1);
		}
		
		//NumForks
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tnumforks=")) {
			System.out.println("Expected '\tnumforks=' but saw: " + line);
			System.exit(-1);
		}
		int numforks = Integer.parseInt(line.replaceFirst("\tnumforks=", ""));
			//Check
		if(numforks != properties.getNumForks()) {
			System.out.println("NumForks does not match.  Properties: " + properties.getNumForks() + ", Output: " + numforks);
			System.exit(-1);
		}
		
		//Num Files
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tnumfiles=")) {
			System.out.println("Expected '\tnumfiles=' but saw: " + line);
			System.exit(-1);
		}
		int numfiles = Integer.parseInt(line.replaceFirst("\tnumfiles=", ""));
			//Check
		if(numfiles != properties.getNumFiles()) {
			System.out.println("NumFiles does not match.  Properties: " + properties.getNumFiles() + ", Output: " + numfiles);
			System.exit(-1);
		}
		
		//Num Dirs
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tnumdirs=")) {
			System.out.println("Expected '\tnumdirs=' but saw: " + line);
			System.exit(-1);
		}
		int numdirs = Integer.parseInt(line.replaceFirst("\tnumdirs=", ""));
			//Check
		if(numdirs != properties.getNumDirectories()) {
			System.out.println("NumDirs does not match.  Properties: " + properties.getNumDirectories() + ", Output: " + numdirs);
			System.exit(-1);
		}
		
		//Num Fragments
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tnumfragments=")) {
			System.out.println("Expected '\tnumfragments=' but saw: " + line);
			System.exit(-1);
		}
		int numfragments = Integer.parseInt(line.replace("\tnumfragments=", ""));
			//Check
		if(numfragments != properties.getNumFragments()) {
			System.out.println("NumFragments does not match.  Properties: " + properties.getNumFragments() + ", Output: " + numfragments);
			System.exit(-1);
		}
		
		//Function Fragment Min Size
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tfunctionfragmentminsize=")) {
			System.out.println("Expected '\tfunctionfragmentminsize=' but saw: " + line);
			System.exit(-1);
		}
		int functionFragmentMinSize = Integer.parseInt(line.replaceFirst("\tfunctionfragmentminsize=", ""));
			//Check
		if(functionFragmentMinSize != properties.getFunctionFragmentMinSize()) {
			System.out.println("Function fragment min size does not match.  Properties: " + properties.getFunctionFragmentMinSize() + ", Output: " + functionFragmentMinSize);
			System.exit(-1);
		}
				
		//Function Fragment Max Size
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tfunctionfragmentmaxsize=")) {
			System.out.println("Expected '\tfunctionfragmentmaxsize=' but saw: " + line);
			System.exit(-1);
		}
		int functionFragmentMaxSize = Integer.parseInt(line.replaceFirst("\tfunctionfragmentmaxsize=", ""));
			//Check
		if(functionFragmentMaxSize != properties.getFunctionFragmentMaxSize()) {
			System.out.println("Function fragment max size does not match.  Properties: " + properties.getFunctionFragmentMaxSize() + ", Output: " + functionFragmentMaxSize);
			System.exit(-1);
		}
		
		//Max Injects
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tmaxinjectnum=")) {
			System.out.println("Expected '\tmaxinjectnum=' but saw: " + line);
			System.exit(-1);
		}
		int maxinjects = Integer.parseInt(line.replaceFirst("\tmaxinjectnum=", ""));
			//Check
		if(maxinjects != properties.getMaxinjectnum()) {
			System.out.println("NumForks does not match.  Properties: " + properties.getMaxinjectnum() + ", Output: " + maxinjects);
			System.exit(-1);
		}
		
		//Injection Repetition Rate
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tinjectionrepititionrate=")) {
			System.out.println("Expected '\tinjectionrepititionrate=' but saw: " + line);
			System.exit(-1);
		}
		int injectionrepititionrate = Integer.parseInt(line.replace("\tinjectionrepititionrate=", ""));
			//Check
		if(injectionrepititionrate != properties.getInjectionReptitionRate()) {
			System.out.println("Injection reptition rate does not match.  Properties: " + properties.getInjectionReptitionRate() + ", Output: " + injectionrepititionrate);
			System.exit(-1);
		}
		
		//Fragment Mutation Rate
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tfragmentmutationrate=")) {
			System.out.println("Expected '\tfragmentmutationrate=' but saw: " + line);
			System.exit(-1);
		}
		int fragmentmutationrate = Integer.parseInt(line.replace("\tfragmentmutationrate=", ""));
			//Check
		if(fragmentmutationrate != properties.getFragmentMutationRate()) {
			System.out.println("Fragment mutation rate does not match.  Properties: " + properties.getFragmentMutationRate() + ", Output: " + fragmentmutationrate);
			System.exit(-1);
		}
		
		//File Mutation Rate
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tfilemutationrate=")) {
			System.out.println("Expected '\tfilemutationrate=' but saw: " + line);
			System.exit(-1);
		}
		int filemutationrate = Integer.parseInt(line.replace("\tfilemutationrate=", ""));
			//Check
		if(filemutationrate != properties.getFileMutationRate()) {
			System.out.println("File mutation rate does not match.  Properties: " + properties.getFileMutationRate() + ", Output: " + filemutationrate);
			System.exit(-1);
		}
		
		//Directory Mutation Rate
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tdirmutationrate=")) {
			System.out.println("Expected '\tdirmutationrate=' but saw: " + line);
			System.exit(-1);
		}
		int dirmutationrate = Integer.parseInt(line.replace("\tdirmutationrate=", ""));
			//Check
		if(dirmutationrate != properties.getDirectoryMutationRate()) {
			System.out.println("Directory mutation rate does not match.  Properties: " + properties.getDirectoryMutationRate() + ", Output: " + dirmutationrate);
			System.exit(-1);
		}
		
		//File Rename Rate
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tfilerenamerate=")) {
			System.out.println("Expected '\tfilerenamerate=' but saw: " + line);
			System.exit(-1);
		}
		int filerenamerate = Integer.parseInt(line.replace("\tfilerenamerate=", ""));
			//Check
		if(filerenamerate != properties.getFileRenameRate()) {
			System.out.println("File rename rate does not match.  Properties: " + properties.getFileRenameRate() + ", Output: " + filerenamerate);
			System.exit(-1);
		}
		
		//Directory Rename Rate
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tdirrenamerate=")) {
			System.out.println("Expected '\tdirrenamerate=' but saw: " + line);
			System.exit(-1);
		}
		int dirrenamerate = Integer.parseInt(line.replace("\tdirrenamerate=", ""));
			//Check
		if(dirrenamerate != properties.getDirRenameRate()) {
			System.out.println("Directory rename rate does not match.  Properties: " + properties.getDirRenameRate() + ", Output: " + dirrenamerate);
			System.exit(-1);
		}
		
		//Max File Edit
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tmaxfileedits=")) {
			System.out.println("Expected '\tmaxfileedits=' but saw: " + line);
			System.exit(-1);
		}
		int maxfileedits = Integer.parseInt(line.replace("\tmaxfileedits=", ""));
			//Check
		if(maxfileedits != properties.getMaxFileEdits()) {
			System.out.println("Max file edits does not match.  Properties: " + properties.getMaxFileEdits() + ", Output: " + maxfileedits);
			System.exit(-1);
		}
		
		//Max File Edit
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tmaxfunctionedits=")) {
			System.out.println("Expected '\tmaxfunctionedits=' but saw: " + line);
			System.exit(-1);
		}
		int maxfunctionedits = Integer.parseInt(line.replace("\tmaxfunctionedits=", ""));
			//Check
		if(maxfunctionedits != properties.getMaxFunctionEdits()) {
			System.out.println("Max function edits does not match.  Properties: " + properties.getMaxFileEdits() + ", Output: " + maxfileedits);
			System.exit(-1);
		}
				
		//Mutation Attempts
			//Read
		line = in.nextLine();
		if(!line.startsWith("\tmutationattempts=")) {
			System.out.println("Expected '\tmutationattempts=' but saw: " + line);
			System.exit(-1);
		}
		int mutationattempts = Integer.parseInt(line.replace("\tmutationattempts=", ""));
			//Check
		if(mutationattempts != properties.getNumMutationAttempts()) {
			System.out.println("Mutation attempts does not match.  Properties: " + properties.getNumMutationAttempts() + ", Output: " + mutationattempts);
			System.exit(-1);
		}

		//Footer
		line = in.nextLine();
		if(!line.equals("END: Properties")) {
			System.out.println("Expected 'End: Properties' but saw: " + line);
			System.exit(-1);
		}
		
// --- TRACKING DATA ------------------------------------------------------------------------------
		List<List<Path>> file_tracker = new LinkedList<List<Path>>(); //Maintain a list of all files in each fork
		for (int i = 0; i < numforks; i++) {
			file_tracker.add(new LinkedList<Path>());
		}

		List<List<Path>> directory_tracker = new LinkedList<List<Path>>(); //Maintain a list of all directories in each fork
		for (int i = 0; i < numforks; i++) {
			directory_tracker.add(new LinkedList<Path>());
		}
		
		List<List<Fragment>> functionfragment_tracker = new LinkedList<List<Fragment>>(); //Maintain a list of all function fragments in each fork
		for(int i = 0; i < numforks; i++) {
			functionfragment_tracker.add(new LinkedList<Fragment>());
		}
				
// --- File Variants ------------------------------------------------------------------------------
{
		System.out.println("Checking file variants.");

		//BEGIN header
		line = in.nextLine();
		if (!line.startsWith("BEGIN: FileVariants")) {
			System.out.println("Expected 'BEGIN: FileVariants' but saw " + line + ".");
			in.close();
			return;
		}
		
		//Read file variants and check them, until encounter END
		int numExpected = 0;
		while (true) {
		//read next line from log
			line = in.nextLine();
			
		//if encounter end, done
			if (line.startsWith("END: FileVariants")) {
				break;
			}
			
		//Number of file variant on
			numExpected++;
			
	//CHECK FILE VARIANT
		//Parse File Variant Header: FileVariant#, Uniform/Non-Uniform, #Forks, original file path & Check Format
			Scanner lin;
			int filenum;
			String uniformity;
			boolean isUniform;
			int numinject;
			Path originalfile;
			try {
				lin = new Scanner(line);
				filenum = lin.nextInt(); 
				uniformity = lin.next();
				if(uniformity.equals("U")) {
					isUniform = true;
				} else if (uniformity.equals("V")) {
					isUniform = false;
				} else {
					System.out.println("FileVariant " + numExpected + " has invalid Uniform/Non-Uniform indicator.  Line: " + line);
					return;
				}
				numinject = lin.nextInt();
				originalfile = Paths.get(lin.next());
				lin.close();
			} catch (Exception e) {
				System.out.println("FileVariant " + numExpected + " has invalid header.  Line: " + line);
				e.printStackTrace();
				return;
			}
			
		//Check FileVariant #
			if (filenum != numExpected) {
				System.out.println("FileVariant " + numExpected + " has incorrect FileVariant#.  Expected: " + numExpected + " but found " + filenum + ".  Line: " + line);
				return;
			}

		//Check Original File
			//Check exists
			if (!Files.exists(originalfile)) {
				System.out.println("FileVariant " + numExpected + " original source file does not exist." + " Line:" + line);
				return;
			}
			//Check is a regular file
			if (!Files.isRegularFile(originalfile)) {
				System.out.println("FileVariant " + numExpected + " original source file is not a regular file." + " Line:" + line);
				return;
			}
			//Check from repository
			if(!originalfile.toAbsolutePath().normalize().startsWith(repositorydir.toAbsolutePath().normalize())) {
				System.out.println("FileVariant " + numExpected + " is not from the source repository." + " Line:" + line);
				return;
			}
			
		//Check logging of original file
			if (!Files.exists(outputdir.resolve("files/" + filenum + "/original"))) {
				System.out.println("FileVariant " + numExpected + " original source file was not logged in files/." + " Line:" + line);
				return;
			}
			if(!Files.isRegularFile(outputdir.resolve("files/" + filenum + "/original"))) {
				System.out.println("FileVariant " + numExpected + " original source file was not logged properly in files/ (is not a regular file)." + " Line:" + line);
				return;
			}
			if (!filesEqual(originalfile, outputdir.resolve("files/" + filenum + "/original"))) {
				System.out.println("FileVariant " + numExpected + " original source file was not recoreded properly in files/ (record does not match original)." + " Line:" + line);
				return;
			}

		//Parse and Check Variant Injections
			Path uniformpathstorage = null;
			for (int i = 0; i < numinject; i++) {
				line = in.nextLine();
				
			//If not correct number of variants
				if (!line.startsWith("\t")) {
					System.out.println("FileVariant " + numExpected + " the number of variants is less than specified in header, or error in output format.  Expected " + numinject + " but saw " + i + " Line:" + line);
					return;
				}

			//Parse Variant Header: Fork#, isRenamed, MutatorUsed&Times, injected file 
				int forknum;
				
				char cIsRenamed;
				boolean isRenamed;
				
				char cIsMutated;
				boolean isMutated;
				String mutator;
				int times;
				int ctype;
				
				Path injectedfile;
				
				try {
					lin = new Scanner(line);
					
					forknum = lin.nextInt();
					
					cIsRenamed = lin.next().charAt(0);
					if(cIsRenamed == 'O') {
						isRenamed = false;
					} else if (cIsRenamed == 'R'){
						isRenamed = true;
					} else {
						System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " has invalid 'rename' indicator.  Line: " + line);
						lin.close();
						return;
					}
					
					cIsMutated = lin.next().charAt(0);
					if(cIsMutated == 'M') {
						isMutated = true;
						mutator = lin.next();
						times = lin.nextInt();
						ctype = lin.nextInt();
					} else if (cIsMutated == 'O') {
						times = -1;
						ctype = -1;
						mutator = "";
						isMutated = false;
					} else {
						System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " has invalid 'mutated' indicator.  Line: " + line);
						lin.close();
						return;
					}
					
					injectedfile = Paths.get(lin.next());
					
					lin.close();
				} catch (Exception e) {
					System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " has invalid header.  Line: " + line);
					e.printStackTrace();
					return;
				}

				Path forkpath = outputdir.resolve("" + forknum).toAbsolutePath().normalize();
				
			//Check Uniform Injection
				if(isUniform) {
					//if first seen, record it
					if(i == 0) {
						uniformpathstorage = forkpath.relativize(injectedfile.getParent()).normalize();
					//if not first, check its the same as previous
					} else {
						if(!forkpath.relativize(injectedfile.getParent()).normalize().equals(uniformpathstorage)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " is not uniform.  Line: " + line);
							return;
						}
					}
				}
				
			//Maintain Track Added Files
				file_tracker.get(forknum).add(injectedfile);

			//Check Injection
				if(!injectedfile.toAbsolutePath().normalize().startsWith(forkpath)) {
					System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " file was not injected into the correct fork." + " Line: " + line);
					return;
				}
				if (!Files.exists(injectedfile)) {
					System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " injected file does not exist." + " Line: " + line);
					return;
				}
				if(!Files.isRegularFile(injectedfile)) {
					System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " injected file is not a regular file." + " Line: " + line);
					return;
				}	
			
			//Check Name (Renamed or Not Renamed)
				if(isRenamed) {
					if(injectedfile.getFileName().equals(originalfile.getFileName())) {
						System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " injected file should be renamed but wasn't." + " Line: " + line);
						return;
					}
				} else {
					if(!injectedfile.getFileName().equals(originalfile.getFileName())) {
						System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " injected file should not be renamed but was." + " Line: " + line);
						return;
					}
				}
				
			//Check File Log
				if(!Files.exists(outputdir.resolve("files/" + filenum + "/" + forknum))) {
					System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " injected file was not logged in files/." + " Line: " + line);
					return;
				}
				if(!Files.isRegularFile(outputdir.resolve("files/" + filenum + "/" + forknum))) {
					System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " injected file was not logged properly (is not a regular file) files/." + " Line: " + line);
					return;
				}
				if (!filesEqual(injectedfile, outputdir.resolve("files/" + filenum + "/" + forknum))) {
					System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " injected file was not logged properly (content does not match injected) in files/." + " Line: " + line);
					return;
				}
			
			//Check Mutation & Content
				if(isMutated) {
					Path ofile_tmp = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation_CheckFiles_original", null);
					Path ifile_tmp = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation_CheckFiles_injected", null);
					Files.copy(originalfile, ofile_tmp, StandardCopyOption.REPLACE_EXISTING);
					Files.copy(injectedfile, ifile_tmp, StandardCopyOption.REPLACE_EXISTING);
				//Check Times
					if(times < 0) {
						System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " times is invalid, < 0." + " Line: " + line);
						return;
					}
					
					if(!(times == 1 || times <= (int)((double)FileUtil.countLines(originalfile) * (double) maxfileedits / (double) 100))) {
						System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " was mutated more than maximum times." + " Line: " + line);
						return;
					}
					
				// Check Type 1 Case
					if(ctype == 1) {
						//If injected/original are equal, type 1 mutation was not applied
						if(FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  No difference between original and injected fragments." + " Line: " + line);
							return;
						}
						
						//PrettyPrint
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), ofile_tmp, ofile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  PrettyPrint of injected file failed." + " Line: " + line);
							return;
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), ifile_tmp, ifile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  PrettyPrint of injected file failed." + " Line: " + line);
							return;
						}
						
						//Pretty-Printed should be identical
						if(!FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Expected type 1, but the fragments are not the same after pretty-print." + " Line: " + line);
							return;
						}
				// Check type 2 case
					} else if (ctype == 2) {
						//If injected/original are equal, type 2 mutation was not applied
						if(FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  No difference between original and injected fragments." + " Line: " + line);
							return;
						}
						
						//Pretty-Print
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), ofile_tmp, ofile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Normalizing original fragment failed." + " Line: " + line);
							return;
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), ifile_tmp, ifile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Fragment syntax not valid/parsable." + " Line: " + line);
							return;
						}
						
						//If injected/original are equal after pretty-print, than type1 instead of type2?
						if(FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Expected type 2, but fragments are the same after type 1 normalization.." + " Line: " + line);
							return;
						}
						
						//BlindRenameVersion (no pretty-print)
						Files.copy(originalfile, ofile_tmp, StandardCopyOption.REPLACE_EXISTING);
						Files.copy(injectedfile, ifile_tmp, StandardCopyOption.REPLACE_EXISTING);
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), ofile_tmp, ofile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Could not type2 normalize original file." + " Line: " + line);
							return;
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), ifile_tmp, ifile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Could not type2 normalize injected file." + " Line: " + line);
							return;
						}
						
						//Blind-Rename should be identical
						if(!FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Expected type 2, but the fragments are not the same after type 2 normalization." + " Line: " + line);
							return;
						}
					} else if (ctype == 3) {
						//If injected/original are equal, type 3 mutation was not applied
						if(FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  No difference between original and injected fragments." + " Line: " + line);
							return;
						}
						
						//Pretty-Print
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), ofile_tmp, ofile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Normalizing original fragment failed." + " Line: " + line);
							return;
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), ifile_tmp, ifile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Fragment syntax not valid/parsable." + " Line: " + line);
							return;
						}
						
						//If injected/original are equal after pretty-print, than type1 instead of type3?
						if(FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Expected type 3, but fragments are the same after type 1 normalization." + " Line: " + line);
							return;
						}
						
						//BlindRenameVersion (no pretty-print)
						Files.copy(originalfile, ofile_tmp, StandardCopyOption.REPLACE_EXISTING);
						Files.copy(injectedfile, ifile_tmp, StandardCopyOption.REPLACE_EXISTING);
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), ofile_tmp, ofile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Could not type2 normalize original file." + " Line: " + line);
							return;
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), ifile_tmp, ifile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Could not type2 normalize injected file." + " Line: " + line);
							return;
						}
						
						//If injected/original are equal after blind-rename, than type2 instead of type3?
						if(FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Expected type 3, but fragments are the same after type 2 normalization." + " Line: " + line);
							return;
						}
						
						//PrettyPrint+BlindRename
						Files.copy(originalfile, ofile_tmp, StandardCopyOption.REPLACE_EXISTING);
						Files.copy(injectedfile, ifile_tmp, StandardCopyOption.REPLACE_EXISTING);
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), ofile_tmp, ofile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Normalizing original fragment failed." + " Line: " + line);
							return;
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), ifile_tmp, ifile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Fragment syntax not valid/parsable." + " Line: " + line);
							return;
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), ofile_tmp, ofile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Could not type2 normalize original file." + " Line: " + line);
							return;
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), ifile_tmp, ifile_tmp)) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Could not type2 normalize injected file." + " Line: " + line);
							return;
						}
						
						//If injectnumExpecteded/original are equal after blind-rename, than type2 instead of type3?
						if(FileUtils.contentEquals(ofile_tmp.toFile(), ifile_tmp.toFile())) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " Mutation " + mutator + " was not applied properly.  Expected type 3, but fragments are the same after type 1+2 normalization." + " Line: " + line);
							return;
						}					
					} else {
						System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " clone type is invalid (not one of 1,2, or 3) (mutation case)." + " Line: " + line);
						return;
					}
					Files.delete(ofile_tmp);
					Files.delete(ifile_tmp);
					
			//Check Not Mutation & Content
				} else {
					if(!filesEqual(originalfile, injectedfile)) {
						System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " injected file's content does not match the original (no mutation case)." + " Line: " + line);
						return;
					}
				}
			}
		}
		
		//Check Numbers of File Variants Does Not Exceed numfiles
		if (numExpected > numfiles) {
			System.out.println("Number of file variants created exceeds specified.  Expected: " +  numfiles + " Saw: " + numExpected);
			in.close();
			return;
		}
}
// --- Directory Variants -------------------------------------------------------------------------
{
		System.out.println("Checking directory variants.");
		
		//BEGIN header
		line = in.nextLine(); // BEGIN;
		if (!line.startsWith("BEGIN: LeafDirectoryVariants")) {
			System.out.println("Expected 'BEGIN: DirectoryVariants' but saw " + line + ".");
			in.close();
			return;
		}
		
		//Read Directory variants and check them, until encounter END
		int numExpected = 0;
		while (true) {
			line = in.nextLine();
			
		//END header (eventually)
			if (line.startsWith("END: LeafDirectoryVariants")) {
				break;
			}
		
		//Number of directory varaints
			numExpected++;

	//CHECK DIRECTORY VARIANT
		//Parse Directory Variant header: DirectoryVariant#, Uniform/Non-Uniform, #Forks, originalDirectory
			Scanner lin;
			
			int dirnum;
			String uniformity;
			boolean isUniform;
			Path originaldir;
			int numinject;
			
			try {
				lin = new Scanner(line);
				dirnum = lin.nextInt();
				uniformity = lin.next();
				if(uniformity.equals("U")) {
					isUniform = true;
				} else if (uniformity.equals("V")) {
					isUniform = false;
				} else {
					System.out.println("DirectoryVariant " + numExpected + " has invalid Uniform/Non-Uniform indicator.  Line: " + line);
					lin.close();
					return;
				}
				numinject = lin.nextInt();
				originaldir = Paths.get(lin.next());
				lin.close();
			} catch (Exception e) {
				System.out.println("DirectoryVariant " + numExpected + " has invalid header.  Line: " + line);
				e.printStackTrace();
				return;
			}

		// Check Directory Variant #
			if (dirnum != numExpected) {
				System.out.println("DirectoryVariant " + numExpected + " has incorrect DirectoryVariant#.  Expected: " + numExpected + " but found " + dirnum + ".  Line: " + line);
				return;
			}

		//Check Original Directory
			//Check original exists
			if (!Files.exists(originaldir)) {
				System.out.println("DirectoryVariant " + numExpected + " original directory does not exist.  Line: " + line);
				return;
			}
			//Check original is a directory
			if (!Files.isDirectory(originaldir)) {
				System.out.println("DirectoryVariant " + numExpected + " original directory is not a directory.  Line: " + line);
				return;
			}
			//Check original is a leaf directory
			if (!FileUtil.isLeafDirectory(originaldir)) {
				System.out.println("DirectoryVariant " + numExpected + " original directory is not a leaf directory.  Line: " + line);
				return;
			}
			//Check is from repository
			if(!originaldir.toAbsolutePath().normalize().startsWith(repositorydir.toAbsolutePath().normalize())) {
				System.out.println("DirectoryVariant " + numExpected + "  original directory is not from the source repository.  Line: " + line);
				return;
			}
			
		//Check logging of original directory
			//Logged original directory exists
			if (!Files.exists(outputdir.resolve("dirs/" + dirnum + "/original/"))) {
				System.out.println("DirectoryVariant " + numExpected + " original direcetory was not logged in dirs/.  Line: " + line);
				return;
			}
			//Logged original directory is a directory
			if(!Files.isDirectory(outputdir.resolve("dirs/" + dirnum + "/original/"))) {
				System.out.println("DirectoryVariant " + numExpected + " original direcetory was not logged in dirs/ properly (not a directory).  Line: " + line);
				return;
			}
			//Logged original directory is a leaf directory
			if(!FileUtil.isLeafDirectory(outputdir.resolve("dirs/" + dirnum + "/original/"))) {
				System.out.println("DirectoryVariant " + numExpected + " original direcetory was not logged in dirs/ properly (not a leaf directory).  Line: " + line);
				return;
			}
			//Logged original directory contents matches original
			if (!leafDirectoryEqual(originaldir, outputdir.resolve("dirs/" + dirnum + "/original/"))) {
				System.out.println("Original directory for dir variant " + dirnum + " was not logged properly in dirs/.");
				return;
			}
			
		//Parse and Check Variant Injections
			Path uniformpathstorage = null;
			for (int i = 0; i < numinject; i++) {
				line = in.nextLine();

			//Check correct number of variants
				if (!line.startsWith("\t")) {
					System.out.println("DirectoryVariant " + numExpected + " the number of variants is less than specified in header, or error in output format.  Expected " + numinject + " but saw " + i + " Line:" + line);
					return;
				}

			//Parse Variant Header: Fork#, isRenamed, injectedDirectory
				int forknum;
				char cIsRenamed;
				boolean isRenamed;
				Path injecteddir;
				int dir_numfiles;
				
				try {
					lin = new Scanner(line);
					forknum = lin.nextInt();
					cIsRenamed = lin.next().charAt(0);
					if(cIsRenamed == 'R') {
						isRenamed = true;
					} else if (cIsRenamed == 'O') {
						isRenamed = false;
					} else {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " has invalid 'renamed' indicator.  Line: " + line);
						lin.close();
						return;
					}
					dir_numfiles = lin.nextInt();
					injecteddir = Paths.get(lin.nextLine().trim()).toAbsolutePath().normalize();
					lin.close();
				} catch (Exception e) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " has invalid header.  Line: " + line);
					e.printStackTrace();
					return;
				}

				Path forkpath = outputdir.resolve("" + forknum).toAbsolutePath().normalize();			
				
			//Check Uniform Injection
				if(isUniform) {
					//if first seen, record location
					if(i == 0) {
						//System.out.println(injecteddir);
						//System.out.println(forkpath);
						uniformpathstorage = forkpath.relativize(injecteddir.getParent()).normalize();
					//if not first, check its the same as previous
					} else {
						if(!forkpath.relativize(injecteddir.getParent()).normalize().equals(uniformpathstorage)) {
							System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " was supposed to be uniform, but wasn't.  Line: " + line);
							return;
						}
					}
				}
				
			//Check Injection
				//injected into correct fork
				if(!injecteddir.toAbsolutePath().normalize().startsWith(forkpath.toAbsolutePath().normalize())) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " directory was not injected into the correct fork.  Line: " + line);
					
					return;
				}
				//injected directory exists
				if(!Files.exists(injecteddir)) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected directory does not exist..  Line: " + line);
					return;
				}
				//is a directory
				if(!Files.isDirectory(injecteddir)) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected directory is not a directory.  Line: " + line);
					return;
				}
				//is a leaf directory
				if(!FileUtil.isLeafDirectory(injecteddir)) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected directory is not a leaf directory.  Line: " + line);
					return;
				}
				
			//Maintain Track Added directories and files
				directory_tracker.get(forknum).add(injecteddir);
				DirectoryStream<Path> ds = Files.newDirectoryStream(injecteddir);
				for(Path p : ds) {
					file_tracker.get(forknum).add(p);
				}
				
				//Check Number of Files
				if(dir_numfiles != injecteddir.toFile().listFiles().length) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " file number indicator is wrong.  Line: " + line);
					return;
				}
				
			//Check Name
				if(isRenamed) {
					if(injecteddir.getFileName().equals(originaldir.getFileName())) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected directory should be renamed but wasn't.  Line: " + line);
						return;
					}
				} else {
					if(!injecteddir.getFileName().equals(originaldir.getFileName())) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected directory should not be renamed but was.  Line: " + line);
						return;
					}
				}
				
			//Check Directory log
				if(!Files.exists(outputdir.resolve("dirs/" + dirnum + "/" + forknum))) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected directory was not logged in dirs/.  Line: " + line);
					return;
				}
				if(!Files.isDirectory(outputdir.resolve("dirs/" + dirnum + "/" + forknum))) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected directory was not properly logged in dirs/ (is not a directory).  Line: " + line);
					return;
				}
				if(!FileUtil.isLeafDirectory(outputdir.resolve("dirs/" + dirnum + "/" + forknum))) {
					System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected directory was not properly logged in dirs/ (is not a leaf directory).  Line: " + line);
					return;
				}
				//contents checked in next section
				
			
			//Check files of injected directory
				Set<Path> originalfiles = new HashSet<Path>();
				Set<Path> injectedfiles = new HashSet<Path>();
				
				// Parse File Headers:
				for (int j = 0; j < dir_numfiles; j++) {
					line = in.nextLine();
					
				//Check Correct Numbers of Files
					if(!line.startsWith("\t\t")) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " the number if files is less than specified in header.  Expected " + dir_numfiles + " but saw " + j + "  Line: " + line);
						return;
					}
					
					//dvifh - directory variant injected file header
					char dvifh_cIsRenamed;
					boolean dvifh_isRenamed;
					char dvifh_cIsMutated;
					boolean dvifh_isMutated;
					String dvifh_mutator;
					int dvifh_times;
					int dvifh_cloneType;
					Path dvifh_originalFile;
					Path dvifh_injectedFile;
					
					try {
						lin = new Scanner(line);
						
						dvifh_cIsRenamed = lin.next().charAt(0);
						if(dvifh_cIsRenamed == 'R') {
							dvifh_isRenamed = true;
						} else if (dvifh_cIsRenamed == 'O') {
							dvifh_isRenamed = false;
						} else {
							System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " has invalid 'renamed' indicator.  Line: " + line);
							return;
						}
						
						dvifh_cIsMutated = lin.next().charAt(0);
						if(dvifh_cIsMutated == 'M') {
							dvifh_isMutated = true;
							dvifh_mutator = lin.next();
							dvifh_times = lin.nextInt();
							dvifh_cloneType = lin.nextInt();
						} else if(dvifh_cIsMutated == 'O') {
							dvifh_isMutated = false;
							dvifh_mutator = null;
							dvifh_times = 0;
							dvifh_cloneType = 0;
						} else {
							System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " has invalid 'mutated' indicator.  Line: " + line);
							return;
						}
						
						lin.useDelimiter(";");
						dvifh_originalFile = Paths.get(lin.next().trim());
						dvifh_injectedFile = Paths.get(lin.next().trim());
					} catch (Exception e) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " has invalid header.  Line: " + line);
						e.printStackTrace();
						return;
					}
					
				//Add original/injected to tracker
					originalfiles.add(dvifh_originalFile.toAbsolutePath().normalize());
					injectedfiles.add(dvifh_injectedFile.toAbsolutePath().normalize());
					
				//Check Original File
					//Exists
					if(!Files.exists(dvifh_originalFile)) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " original file doesn't exist.  Line: " + line);
						return;
					}
					//Is a regular file
					if(!Files.isRegularFile(dvifh_originalFile)) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " is not a regular file.  Line: " + line);
						return;
					}
					//Is in the original directory
					if(!dvifh_originalFile.toAbsolutePath().normalize().startsWith(originaldir.toAbsolutePath().normalize())) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " file is not from the original directory.  Line: " + line);
						return;
					}
					
				//Check Injected File
					//Exists
					if(!Files.exists(dvifh_originalFile)) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " original file doesn't exist.  Line: " + line);
						return;
					}
					//Is a regular file
					if(!Files.isRegularFile(dvifh_originalFile)) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " is not a regular file.  Line: " + line);
						return;
					}
					//Is in the injected directory
					if(!dvifh_originalFile.toAbsolutePath().normalize().startsWith(originaldir.toAbsolutePath().normalize())) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " file is not from the original directory.  Line: " + line);
						return;
					}
					
				//Check File Name
					//If renamed, was renamed
					if(dvifh_isRenamed) {
						if(dvifh_injectedFile.getFileName().equals(dvifh_originalFile.getFileName())) {
							System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " injected file should be renamed but wasn't.  Line: " + line);
							return;
						}
					//If not renamed, wasn't renamed
					} else {
						if(!dvifh_injectedFile.getFileName().equals(dvifh_originalFile.getFileName())) {
							System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " injected file shouldn't be renamed but was.  Line: " + line);
							return;
						}
					}
					
				//Check log
					if(!Files.exists(outputdir.resolve("dirs/" + dirnum + "/" + forknum + "/" + dvifh_injectedFile.getFileName()))) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected file was not logged in its directory in dirs/.  Line: " + line);
						return;
					}
					if(!Files.isRegularFile(outputdir.resolve("dirs/" + dirnum + "/" + forknum + "/" + dvifh_injectedFile.getFileName()))) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected file was not logged properly in directory in dirs/, not a regular file.  Line: " + line);
						return;
					}
					if(!FileUtils.contentEquals(dvifh_injectedFile.toFile(), outputdir.resolve("dirs/" + dirnum + "/" + forknum + "/" + dvifh_injectedFile.getFileName()).toFile())) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " injected file was not logged properly in directory in dirs/, content does not match actual injected file.  Line: " + line);
						return;
					}
					
				//Check Content (mutation)
					if(dvifh_isMutated) {
						Path dvifh_ofile_tmp = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation_CheckFiles_original", null);
						Path dvifh_ifile_tmp = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation_CheckFiles_injected", null);
						Files.copy(dvifh_originalFile, dvifh_ofile_tmp, StandardCopyOption.REPLACE_EXISTING);
						Files.copy(dvifh_injectedFile, dvifh_ifile_tmp, StandardCopyOption.REPLACE_EXISTING);
					//Check Times
						if(dvifh_times < 0) {
							System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " times is invalid, < 0.  Line: " + line);
							return;
						}
						if(!(dvifh_times == 1 || dvifh_times <= (int)((double)FileUtil.countLines(dvifh_originalFile) * (double) maxfileedits / (double) 100))) {
							System.out.println("FileVariant " + numExpected + " Injection " + (i+1) + " was mutated more than maximum times." + " Line: " + line);
							return;
						}
						
					//Check Type 1 Case
						if(dvifh_cloneType == 1) {
							//If injected/original are equal, type 1 mutation was not applied
							if(FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  No difference between original and injected file..  Line: " + line);
								return;
							}
							
							//PrettyPrint
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), dvifh_ofile_tmp, dvifh_ofile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), dvifh_ifile_tmp, dvifh_ifile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							
							//Pretty print should be identical
							if(!FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  Expected type 1, but the fragments are not the same after pretty-printing. Line " + line);
								return;
							}
							
					//Check Type 2 Case
						} else if (dvifh_cloneType == 2) {
							//If injected/original are equal, type 1 mutation was not applied
							if(FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  No difference between original and injected file..  Line: " + line);
								return;
							}
							
							//PrettyPrint
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), dvifh_ofile_tmp, dvifh_ofile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), dvifh_ifile_tmp, dvifh_ifile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							
							//Pretty print should not be identical
							if(FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  Expected type 2, but found type 1 (identical after normalization). Line " + line);
								return;
							}
							
							//Restore originals
							Files.copy(dvifh_originalFile, dvifh_ofile_tmp, StandardCopyOption.REPLACE_EXISTING);
							Files.copy(dvifh_injectedFile, dvifh_ifile_tmp, StandardCopyOption.REPLACE_EXISTING);
							
							//Blind Rename		
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), dvifh_ofile_tmp, dvifh_ofile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), dvifh_ifile_tmp, dvifh_ifile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							
							//Blind rename should be identical
							if(!FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  Expected type 2, but found type 3 (not identical after type 2 normalization). Line " + line);
								return;
							}
							
						} else if (dvifh_cloneType == 3) {
							//If injected/original are equal, type 3 mutation was not applied
							if(FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  No difference between original and injected file..  Line: " + line);
								return;
							}
							
							//PrettyPrint
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), dvifh_ofile_tmp, dvifh_ofile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), dvifh_ifile_tmp, dvifh_ifile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							
							//Pretty print should not be identical
							if(FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  Expected type 3, but found type 1 (identical after normalization). Line " + line);
								return;
							}
							
							//Restore originals
							Files.copy(dvifh_originalFile, dvifh_ofile_tmp, StandardCopyOption.REPLACE_EXISTING);
							Files.copy(dvifh_injectedFile, dvifh_ifile_tmp, StandardCopyOption.REPLACE_EXISTING);
							
							//Blind Rename		
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), dvifh_ofile_tmp, dvifh_ofile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), dvifh_ifile_tmp, dvifh_ifile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							
							//Blind rename should not be identical
							if(FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  Expected type 3, but found type 2 (identical after type 2 normalization). Line " + line);
								return;
							}
							
							//Restore originals
							Files.copy(dvifh_originalFile, dvifh_ofile_tmp, StandardCopyOption.REPLACE_EXISTING);
							Files.copy(dvifh_injectedFile, dvifh_ifile_tmp, StandardCopyOption.REPLACE_EXISTING);
							
							//Pretty and blind rename
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), dvifh_ofile_tmp, dvifh_ofile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), dvifh_ifile_tmp, dvifh_ifile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), dvifh_ofile_tmp, dvifh_ofile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), dvifh_ifile_tmp, dvifh_ifile_tmp)) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " PrettyPrint of injected file failed.  Line: " + line);
								return;
							}
							
							//PrettyPrint+Blind rename should not be identical
							if(FileUtils.contentEquals(dvifh_ofile_tmp.toFile(), dvifh_ifile_tmp.toFile())) {
								System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " Mutation " + dvifh_mutator + " was not applied properly.  Expected type 3, but files were identical after type1+2 normalization. Line " + line);
								return;
							}
						}
					
				//Check Content (No Mutation)
					} else {
						if(!FileUtils.contentEquals(dvifh_originalFile.toFile(), dvifh_injectedFile.toFile())) {
							System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + "File " + (j+1) + " injected file's content does not match the original (no mutation case).  Line: " + line);
							return;
						}
					}
				}
				
			//Check reported original files match files in original directory
				Set<Path> ref_originalFiles = new HashSet<Path>();
				ds = Files.newDirectoryStream(originaldir);
				for(Path p : ds) {
					ref_originalFiles.add(p.toAbsolutePath().normalize());
				}
				for(Path p : ref_originalFiles) {
					if(!originalfiles.contains(p)) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " did not report one of the original files (did no inject either?): " + p + ".  Line: " + line);
						return;
					}
				}
				for(Path p : originalfiles) {
					if(!ref_originalFiles.contains(p)) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " reported an original file not in the original directory: " + p + ".  Line: " + line);
						return;
					}
				}
				
			//Check reported injected files match files in injected directory
				Set<Path> ref_injectedfiles = new HashSet<Path>();
				ds = Files.newDirectoryStream(injecteddir);
				for(Path p : ds) {
					ref_injectedfiles.add(p.toAbsolutePath().normalize());
				}
				for(Path p : ref_injectedfiles) {
					if(!injectedfiles.contains(p)) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " has a file in its injected directory that it did not report: " + p + ".  Line: " + line);
						return;
					}
				}
				for(Path p : injectedfiles) {
					if(!ref_injectedfiles.contains(p)) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " reported a file not in the injected directory: " + p + ".  Line: " + line);
						return;
					}
				}
				
			//Check log contents correct
				Set<Path> loggedfiles = new HashSet<Path>();
				ds = Files.newDirectoryStream(outputdir.resolve("dirs/" + dirnum + "/" + forknum + "/"));
				for(Path p : ds) {
					loggedfiles.add(p.toAbsolutePath().normalize());
				}
				for(Path p : injectedfiles) {
					if(!loggedfiles.contains(outputdir.resolve("dirs/" + dirnum + "/" + forknum + "/").resolve(p.getFileName()).toAbsolutePath().normalize())) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " log is missing file: " + p + ".  Line: " + line);
						return;
					}
				}
				for(Path p : loggedfiles) {
					if(!injectedfiles.contains(injecteddir.resolve(p.getFileName()).toAbsolutePath().normalize())) {
						System.out.println("DirectoryVariant " + numExpected + " Injection " + (i+1) + " log has a file that wasn't injected: " + p + ".  Line: " + line);
						return;
					}
				}
			}
		}
		
	//Check number of directory variants does not exceed numdirs
		if (numExpected > numdirs) {
			System.out.println("More file variants were created then desired, " + numExpected + " > " + numfiles + ".");
			in.close();
			return;
		}
}
// --- Fragment Variants --------------------------------------------------------------------------
{
		System.out.println("Checking function fragment variants.");
		line = in.nextLine(); // BEGIN;
		if (!line.startsWith("BEGIN: FunctionFragmentVariants")) {
			System.out.println("Expected 'BEGIN: FunctionFragmentVariants' but saw " + line + ".");
			in.close();
			return;
		}
		int numExpected = 0;
		while (true) {

			// Get header
			line = in.nextLine();
			if (line.startsWith("END: FunctionFragmentVariants")) {
				break;
			}
			// Get details
			Scanner lin = new Scanner(line);
			lin.useDelimiter(" ");
			int fnum = lin.nextInt();
			String uniformity = lin.next();
			boolean isUniform;
			if(uniformity.equals("U")) {
				isUniform = true;
			} else if (uniformity.equals("V")) {
				isUniform = false;
			} else {
				System.out.println("The isUniform injection indicator for file variant " + fnum + " is missing or invalid.");
				System.exit(-1);
				lin.close();
				return;
			}
			int numinject = lin.nextInt();
			int startline = lin.nextInt();
			int endline = lin.nextInt();
			Path srcfile = Paths.get(lin.nextLine().trim());
						
			Fragment originalfragment = new Fragment(srcfile, startline, endline);
			lin.close();

			// Check file variant #
			numExpected++;
			if (fnum != numExpected) {
				System.out.println("Output error, the # of a fragment variant is not correct.");
				System.exit(-1);
			}

			// Check referenced file
			if (!Files.exists(originalfragment.getSrcFile())) {
				System.out.println("File containing origial function fragment referenced by fragment variant " + fnum + " does not exist.");
				System.exit(-1);
			}
			if (!Files.isRegularFile(originalfragment.getSrcFile())) {
				System.out.println("File containing origial function fragment referenced by fragment variant " + fnum + " is not a regular file.");
				System.exit(-1);
			}
			int numlines = FragmentUtil.countLines(originalfragment.getSrcFile());
			if (numlines < originalfragment.getEndLine()) {
				System.out.println("Original fragment referenced by function fragment variant " + fnum + " is invalid (endline proceeds end of file).");
				System.exit(-1);
			}
			if (!Files.exists(outputdir.resolve("function_fragments/" + fnum + "/original"))) {
				System.out.println("Original fragment referenced by function fragment variant  " + fnum + " was not recorded in function_fragments/.");
				System.exit(-1);
			}
			if(!FragmentUtil.isFunction(originalfragment, language)) {
				System.out.println("Original fragment referenced by function fragment variant " + fnum + " is not a function.");
				System.exit(-1);
			}
			
			Path tmpfileo = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation", null);
			FragmentUtil.extractFragment(originalfragment, tmpfileo);
			if (!filesEqual(tmpfileo, outputdir.resolve("function_fragments/" + fnum + "/original"))) {
				System.out.println("Original fragment for function fragment variant " + fnum + " was not recorded properly in function_fragments/ (record does not match original).");
				System.exit(-1);
			}

			// Read and check file variant injections (output and validity)
			Fragment uniformstorage = null;
			for (int i = 0; i < numinject; i++) {
				line = in.nextLine();

				// check if a injection record, if not then header was
				// incorrect!
				if (!line.startsWith("\t")) {
					System.out.println("Output error, the # of injections for function fragment variant " + fnum + " is incorrect, or varaint report is missing.");
					System.exit(-1);
				}

				// get information
				String opname;
				int times;
				int clonetype;
				int forknum;
				Fragment injectedfragment;
				boolean isMutated;
				
				try {
					lin = new Scanner(line);
					forknum = lin.nextInt();
										
					char cIsMutated = lin.next().charAt(0);
					if(cIsMutated == 'M') {
						opname = lin.next();
						times = lin.nextInt();
						clonetype = lin.nextInt();
						isMutated = true;
					} else if (cIsMutated == 'O') {
						opname = null;
						times = 0;
						clonetype = 0;
						isMutated = false;
					} else {
						System.out.println("FragmentVariant " + fnum + " injection " + (i+1) + " has incorrect header (M/O mutation indicator). Line: " + line);
						return;
					}
					
					int istartline = lin.nextInt();
					int iendline = lin.nextInt();
					Path isrcfile = Paths.get(lin.nextLine().trim());
					injectedfragment = new Fragment(isrcfile, istartline, iendline);;
					lin.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("FragmentVariant " + fnum + " injection " + (i+1) + " has incorrect header . Line: " + line);
					return;
				}

			// Check Uniformity
				Path forkpath = outputdir.resolve("" + forknum).toAbsolutePath().normalize();
				if(isUniform) {
					if(i == 0) {
						uniformstorage = new Fragment(forkpath.relativize(injectedfragment.getSrcFile()).normalize(), injectedfragment.getStartLine(), injectedfragment.getEndLine());
					} else {
						Fragment normalized = new Fragment(forkpath.relativize(injectedfragment.getSrcFile()).normalize(), injectedfragment.getStartLine(), injectedfragment.getEndLine());
						if(!normalized.getSrcFile().equals(uniformstorage.getSrcFile())) {
							System.out.println("Fragment injection " + fnum + " was supposed to be uniform but wasn't.");
							System.out.println(uniformstorage + "\n" + normalized);
							System.exit(-1);
						}
					}
				}
				
				// track added file
				functionfragment_tracker.get(forknum).add(injectedfragment);
				
			// check injection & record
				//File injected into exists
				if (!Files.exists(injectedfragment.getSrcFile())) {
					System.out.println("Source file containing injected function fragment does not exist. Variant: " + fnum + " Fork: " + forknum + " File: " + injectedfragment.getSrcFile());
					System.exit(-1);
				}
				//File injected into is a regular file
				if (!Files.isRegularFile(injectedfragment.getSrcFile())) {
					System.out.println("Source file containing injected function fragment is not a regular file. Variant: " + fnum + " Fork: " + forknum + " File: " + injectedfragment.getSrcFile());
					System.exit(-1);
				}
				//The injected fragment is a function
				if(!FragmentUtil.isFunction(injectedfragment, language)) {
					System.out.println("Injected function fragment is not a function." + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					System.exit(-1);
				}
				//check fragment endline is before EOL 
				numlines = FragmentUtil.countLines(injectedfragment.getSrcFile());
				if(numlines < injectedfragment.getEndLine()) {
					System.out.println("Injected function fragment (specification) is invalid (endline proceeds end of file). " + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					System.exit(-1);
				}
				
			//check log
				//log exists
				if(!Files.exists(outputdir.resolve("function_fragments/" + fnum + "/" + forknum))) {
					System.out.println("Injected function fragment record is missing. Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
				}
				//log matches injected function (extract + compare)
				Path tmpfilei = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation", null);
				FragmentUtil.extractFragment(injectedfragment, tmpfilei);
				if(!filesEqual(tmpfilei, outputdir.resolve("function_fragments/" + fnum + "/" + forknum))) {
					System.out.println("Injected function fragment record is incorrect. Variant: " + " Fragment: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					System.exit(-1);
				}
				
			//check fragment content
				Path tmpfileoe = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation", null);
				Files.copy(tmpfileo, tmpfileoe, StandardCopyOption.REPLACE_EXISTING);
				
				//If Not mutated, should be equal to original
				if(!isMutated) {
					if(!org.apache.commons.io.FileUtils.contentEquals(tmpfileo.toFile(), tmpfilei.toFile())) {
						System.out.println("Mutation: 'none' was not applied correctly. Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					}
					
				//if mutated, check mutation
				} else {
					if(clonetype == 1) {
						//if equal, then no mutation was applied
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileo.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  No difference between fragments."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//pretty print the fragments
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//if not equal, then not type 1
						if(!org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 1, but fragments not same after normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
					} else if(clonetype == 2) {
						//if equal, than not type 2
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileo.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  No difference between fragments."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//pretty print
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//if equal than type 1 not type 2
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 2, but fragments are same after type 1 normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//restore
						FragmentUtil.extractFragment(originalfragment, tmpfileoe);
						FragmentUtil.extractFragment(injectedfragment, tmpfilei);
						
						//Blind Rename
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//if not equal, than not type 2!
						if(!org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 2, but fragments not same after normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
					} else if(clonetype == 3) {
						//if equal, then no mutation
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileo.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  No difference between fragments."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//pretty print the fragments
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//if equal, than were type 1
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 3, but fragments are same after type 1 normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//restore
						FragmentUtil.extractFragment(originalfragment, tmpfileoe);
						FragmentUtil.extractFragment(injectedfragment, tmpfilei);
						
						//blind rename
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//If equal, than type 2
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 3, but fragments are same after type 2 normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//restore
						FragmentUtil.extractFragment(originalfragment, tmpfileoe);
						FragmentUtil.extractFragment(injectedfragment, tmpfilei);
						
						//pretty print
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						//blind rename
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 3, but fragments are same after type 1+2 normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						
					} else {
						System.out.println("Clone type is invalid." + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					}
				}
				Files.delete(tmpfileoe);
				Files.delete(tmpfilei);
			}
			Files.delete(tmpfileo);
			
		}
		if (numExpected > numfragments) {
			System.out.println("More file variants were created then desired, " + numExpected + " > " + numfragments + ".");
			in.close();
			return;
		}
}

// --- Fork Contents Check -------------------------------------------------------------------------

System.out.println("Checking fork contents.");

	//inventory original system
		InventoriedSystem system_is= new InventoriedSystem(systemdir, language);
		
	//Check Each Fork
		for(int i = 0; i < numforks; i++) {
			InventoriedSystem fork_is = new InventoriedSystem(outputdir.resolve("" + i), language);
			
		//Files
			//build list of expected files
			List<Path> files = new LinkedList<Path>();
			for(Path p : system_is.getFiles()) {
				Path np = Paths.get(p.toAbsolutePath().toString().replaceFirst(systemdir.toString(), outputdir.resolve("" + i).toString()));
				files.add(np.toAbsolutePath().normalize());
			}
			
			//check
			for(Path p : file_tracker.get(i)) {
				if(!fork_is.getFiles().contains(p.toAbsolutePath().normalize())) {
					System.out.println("Fork(" + i + ") - Fork is missing an injected file : " + p);
				}
			}
			for(Path p : files) {
				if(!fork_is.getFiles().contains(p.toAbsolutePath().normalize())) {
					System.out.println("Fork(" + i + ") - Fork is missing a file from the original system : " + p);
				}
			}
			for(Path p : fork_is.getFiles()) {
				if(!files.contains(p.toAbsolutePath().normalize()) && !file_tracker.get(i).contains(p.toAbsolutePath().normalize())) {
					System.out.println("Fork(" + i + ") - Fork contains file that should not be there... : " + p.toAbsolutePath().normalize());
				}
			}
			
		//Directories
			List<Path> directories = new LinkedList<Path>();
			for(Path p : system_is.getDirectories()) {
				Path np = Paths.get(p.toAbsolutePath().toString().replaceFirst(systemdir.toString(), outputdir.resolve("" + i).toString()));
				directories.add(np.toAbsolutePath().normalize());
			}
			
			//check
			for(Path p : directory_tracker.get(i)) {
				if(!fork_is.getDirectories().contains(p.toAbsolutePath().normalize())) {
					System.out.println("Fork(" + i + ") - Fork is missing an injected directory : " + p);
				}
			}
			for(Path p : directories) {
				if(!fork_is.getDirectories().contains(p.toAbsolutePath().normalize())) {
					System.out.println("Fork(" + i + ") - Fork is missing a directory from the original system : " + p);
				}
			}
			for(Path p : fork_is.getDirectories()) {
				if(!directories.contains(p.toAbsolutePath().normalize()) && !directory_tracker.get(i).contains(p.toAbsolutePath().normalize())) {
					System.out.println("Fork(" + i + ") - Fork contains a directory that should not be there... : " + p);
				}
			}
			
		//Fragments
			//check unchanged original files are not modified ((changed and non-original were checked previously))
			List<Path> modifiedfiles = new LinkedList<Path>();
			for(Fragment fragment : functionfragment_tracker.get(i)) {
				modifiedfiles.add(fragment.getSrcFile().toAbsolutePath().normalize());
			}
			for(Path p : files) {
				if(!modifiedfiles.contains(p)) {
					Path forkpath = outputdir.toAbsolutePath().normalize().resolve("" + i).normalize().toAbsolutePath();
					Path relativefile = forkpath.relativize(p.toAbsolutePath().normalize());
					Path originalfile = systemdir.toAbsolutePath().normalize().resolve(relativefile);
					if(!FileUtils.contentEquals(originalfile.toFile(), p.toFile())) {
						System.out.println("Fork(" + i + ") - Original file in fork was modified when it should not have been.: " + p.toAbsolutePath().normalize() + " original: " + originalfile);
					}
				}
			}
		}
		in.close();
		System.out.println("Exit with success!");

	}

//	private static boolean fragEqual(Fragment f1, Fragment f2) throws IOException {
//		Path frag1 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "testInjectiosMixed", null);
//		Path frag2 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "testInjectiosMixed", null);
//		FragmentUtil.extractFragment(f1, frag1);
//		FragmentUtil.extractFragment(f2, frag2);
//		boolean retval = filesEqual(frag1, frag2);
//		Files.delete(frag1);
//		Files.delete(frag2);
//		return retval;
//	}

	private static boolean filesEqual(Path f1, Path f2) throws IOException {
		return org.apache.commons.io.FileUtils.contentEquals(f1.toFile(), f2.toFile());
	}

	private static boolean leafDirectoryEqual(Path d1, Path d2) throws IOException {
		DirectoryStream<Path> ds1, ds2;
		List<Path> dc1 = new LinkedList<Path>();
		List<Path> dc2 = new LinkedList<Path>();
		ds1 = Files.newDirectoryStream(d1);
		ds2 = Files.newDirectoryStream(d2);
		for (Path p : ds1) {
			dc1.add(p.toAbsolutePath().normalize());
		}
		for (Path p : ds2) {
			dc2.add(p.toAbsolutePath().normalize());
		}

		for (Path p : dc1) {
			if (!dc2.contains(Paths.get(p.toString().replaceFirst(d1.toString(), d2.toString())))) {
				return false;
			}
		}
		for (Path p : dc2) {
			if (!dc1.contains(Paths.get(p.toString().replaceFirst(d2.toString(), d1.toString())))) {
				return false;
			}
		}
		return true;
	}
}
