package main;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import models.FunctionFragment;
import util.FileUtil;
import util.FragmentUtil;
import util.InventoriedSystem;
import util.SystemUtil;

public class CheckSimulation {
	@SuppressWarnings("unused")
	public static void main(String args[]) throws IOException {
		if (args.length != 1) {
			System.out.println("Require output file.");
			return;
		}

		Path output = Paths.get(args[0]);
		if (!Files.isReadable(output) || !Files.isRegularFile(output)) {
			System.out.println("Output file is invalid.");
			return;
		}

		Scanner in = new Scanner(output.toFile());
		String line;

		// getProperties
		line = in.nextLine(); // BEGIN:
		Path outputdir = Paths.get(in.nextLine().replaceFirst("\toutput_directory=", ""));
		Path systemdir = Paths.get(in.nextLine().replaceFirst("\tsystem_directory=", ""));
		Path repositorydir = Paths.get(in.nextLine().replaceFirst("\trepository_directory=", ""));
		String language = in.nextLine().replaceFirst("\tlanguage=", "");
		int numforks = Integer.parseInt(in.nextLine().replaceFirst("\t#forks=", ""));
		int maxinjects = Integer.parseInt(in.nextLine().replaceFirst("\tmax#injects=", ""));
		int numfiles = Integer.parseInt(in.nextLine().replaceFirst("\t#files=", ""));
		int numdirs = Integer.parseInt(in.nextLine().replaceFirst("\t#dirs=", ""));
		int numfragments = Integer.parseInt(in.nextLine().replace("\t#fragments=", ""));
		int mutationrate = Integer.parseInt(in.nextLine().replace("\tmutationrate=", ""));
		int injectionrepititionrate = Integer.parseInt(in.nextLine().replace("\tinjectionrepititionrate=", ""));
		line = in.nextLine(); // END:

		// create tracking devices
		List<List<Path>> file_tracker = new LinkedList<List<Path>>();
		for (int i = 0; i < numforks; i++) {
			file_tracker.add(new LinkedList<Path>());
		}

		List<List<Path>> directory_tracker = new LinkedList<List<Path>>();
		for (int i = 0; i < numforks; i++) {
			directory_tracker.add(new LinkedList<Path>());
		}
		
		List<List<FunctionFragment>> functionfragment_tracker = new LinkedList<List<FunctionFragment>>();
		for(int i = 0; i < numforks; i++) {
			functionfragment_tracker.add(new LinkedList<FunctionFragment>());
		}
		
		

		// FileVariants
		System.out.println("Checking File Variants.");
		line = in.nextLine(); // BEGIN;
		if (!line.startsWith("BEGIN: FileVariants")) {
			System.out.println("Expected 'BEGIN: FileVariants' but saw " + line + ".");
			in.close();
			return;
		}
		int numExpected = 0;
		while (true) {

			// Read and check file variant header
			// Get header
			line = in.nextLine();
			if (line.startsWith("END: FileVariants")) {
				break;
			}
			// Get details
			Scanner lin = new Scanner(line);
			int filenum = lin.nextInt();
			String uniformity = lin.next();
			boolean isUniform;
			if(uniformity.equals("U")) {
				isUniform = true;
			} else if (uniformity.equals("V")) {
				isUniform = false;
			} else {
				System.out.println("The isUniform injection indicator for file variant " + filenum + " is missing or invalid.");
				System.exit(-1);
				lin.close();
				return;
			}
			int numinject = lin.nextInt();
			Path originalfile = Paths.get(lin.next());
			lin.close();

			// Check file variant #
			numExpected++;
			if (filenum != numExpected) {
				System.out.println("Output error, the # of a file variant is not correct.");
				System.exit(-1);
			}

			// Check referenced file
			if (!Files.exists(originalfile)) {
				System.out.println("Original file referenced by file variant " + filenum + " does not exist.");
				System.exit(-1);
			}
			if (!Files.isRegularFile(originalfile)) {
				System.out.println("Original file referenced by file variant " + filenum + " is not a regular file.");
				System.exit(-1);
			}
			if (!Files.exists(outputdir.resolve("files/" + filenum))) {
				System.out.println("Original file for file variant " + filenum + " was not recorded in files/.");
				System.exit(-1);
			}
			if (!filesEqual(originalfile, outputdir.resolve("files/" + filenum))) {
				System.out.println("Original file for file variant " + filenum + " was not recorded properly in files/ (record does not match original).");
				System.exit(-1);
			}

			// Read and check file variant injections (output and validity)
			Path uniformpathstorage = null;
			for (int i = 0; i < numinject; i++) {
				line = in.nextLine();
				
				// check if a injection record, if not then header was
				// incorrect!
				if (!line.startsWith("\t")) {
					System.out.println("Output error, the # of injections for file variant " + filenum + " is incorrect, or varaint report is missing.");
					System.exit(-1);
				}

				// get information
				lin = new Scanner(line);
				int forknum = lin.nextInt();
				Path injectedfile = Paths.get(lin.next());
				lin.close();

				Path forkpath = outputdir.resolve("" + forknum).toAbsolutePath().normalize();
				if(isUniform) {
					if(i == 0) {
						uniformpathstorage = forkpath.relativize(injectedfile.getParent()).normalize();
					} else {
						if(!forkpath.relativize(injectedfile.getParent()).normalize().equals(uniformpathstorage)) {
							System.out.println("File injection " + filenum + " was supposed to be uniform but wasn't.");
							System.exit(-1);
						}
					}
				}
				
				// track added file
				file_tracker.get(forknum).add(injectedfile);

				// check injection
				if(!injectedfile.toAbsolutePath().normalize().startsWith(forkpath)) {
					System.out.println("File injected into wrong fork. File: " + filenum + " Fork: " + forknum + " File: " + injectedfile);
					System.exit(-1);
				}
				if (!Files.exists(injectedfile)) {
					System.out.println("File injected does not exist. File: " + filenum + " Fork: " + forknum + " File: " + injectedfile);
					System.exit(-1);
				}
				if (!Files.isRegularFile(injectedfile)) {
					System.out.println("File injected is not a regular. File: " + filenum + " Fork: " + forknum + " File: " + injectedfile);
					System.exit(-1);
				}
				if (!filesEqual(originalfile, injectedfile)) {
					System.out.println("File injected does not match its original.  File: " + filenum + " Fork: " + forknum + " OFile: " + originalfile + " IFile: " + injectedfile);
					System.exit(-1);
				}
			}
		}
		if (numExpected > numfiles) {
			System.out.println("More file variants were created then desired, " + numExpected + " > " + numfiles + ".");
			in.close();
			return;
		}

		// directory variants
		System.out.println("Checking directory variants.");
		line = in.nextLine(); // BEGIN;
		if (!line.startsWith("BEGIN: DirectoryVariants")) {
			System.out.println("Expected 'BEGIN: DirectoryVariants' but saw " + line + ".");
			in.close();
			return;
		}
		numExpected = 0;
		while (true) {
			// Read and check file variant header

			// Get header
			line = in.nextLine();
			if (line.startsWith("END: DirectoryVariants")) {
				break;
			}

			// Get details
			Scanner lin = new Scanner(line);
			int dirnum = lin.nextInt();
			String uniformity = lin.next();
			boolean isUniform;
			if(uniformity.equals("U")) {
				isUniform = true;
			} else if (uniformity.equals("V")) {
				isUniform = false;
			} else {
				System.out.println("The isUniform injection indicator for file variant " + dirnum + " is missing or invalid.");
				System.exit(-1);
				lin.close();
				return;
			}
			int numinject = lin.nextInt();
			Path originaldir = Paths.get(lin.next());
			lin.close();

			// Check file variant #
			numExpected++;
			if (dirnum != numExpected) {
				System.out.println("Output error, the # of a file variant is not correct.");
				System.exit(-1);
			}

			// Check referenced dir
			if (!Files.exists(originaldir)) {
				System.out.println("Original dir referenced by dir variant " + dirnum + " does not exist.");
				System.exit(-1);
			}
			if (!Files.isDirectory(originaldir)) {
				System.out.println("Original dir referenced by dir variant " + dirnum + " is not a directory.");
				System.exit(-1);
			}
			if (!FileUtil.isLeafDirectory(originaldir)) {
				System.out.println("Original dir referenced by dir variant " + dirnum + " is not a leaf directory.");
				System.exit(-1);
			}
			if (!Files.exists(outputdir.resolve("dirs/" + dirnum))) {
				System.out.println("Original directory for dir variant " + dirnum + " was not recorded in dirs/.");
				System.exit(-1);
			}
			if (!leafDirectoryEqual(originaldir, outputdir.resolve("dirs/" + dirnum))) {
				System.out.println("Original directory for dir variant " + dirnum + " was not recorded properly in dirs/.");
				return;
			}

			// Read and check file variant injections (output and validity)
			Path uniformpathstorage = null;
			for (int i = 0; i < numinject; i++) {
				line = in.nextLine();

				// check if a injection record, if not then header was
				// incorrect!
				if (!line.startsWith("\t")) {
					System.out.println("Output error, the # of injections for file variant " + dirnum + " is incorrect, or varaint report is missing.");
					System.exit(-1);
				}

				// get information
				lin = new Scanner(line);
				int forknum = lin.nextInt();
				Path injecteddir = Paths.get(lin.next());
				lin.close();

				Path forkpath = outputdir.resolve("" + forknum).toAbsolutePath().normalize();
				if(isUniform) {
					if(i == 0) {
						uniformpathstorage = forkpath.relativize(injecteddir.getParent()).normalize();
					} else {
						if(!forkpath.relativize(injecteddir.getParent()).normalize().equals(uniformpathstorage)) {
							System.out.println("Directory injection " + dirnum + " was supposed to be uniform but wasn't.");
							System.exit(-1);
						}
					}
				}
				
				// track added file
				directory_tracker.get(forknum).add(injecteddir);
				DirectoryStream<Path> ds = Files.newDirectoryStream(injecteddir);
				for(Path p : ds) {
					file_tracker.get(forknum).add(p);
				}

				// check injection
				if(!injecteddir.toAbsolutePath().normalize().startsWith(forkpath)) {
					System.out.println("File injected into wrong fork. DirNum: " + dirnum + " Fork: " + forknum + " File: " + injecteddir);
					System.exit(-1);
				}
				if (!Files.exists(injecteddir)) {
					System.out.println("Directory injected does not exist. DirNum: " + dirnum + " Fork: " + forknum + " Dir: " + injecteddir);
					System.exit(-1);
				}
				if (!Files.isDirectory(injecteddir)) {
					System.out.println("Injected directory is not a directory: DirNum: " + dirnum + " Fork: " + forknum + " Dir: " + injecteddir);
					System.exit(-1);
				}
				if (!leafDirectoryEqual(originaldir, injecteddir)) {
					System.out.println("Directory injected does not match its original.  DirNum: " + dirnum + " Fork: " + forknum + " OFile: " + originaldir + " IFile: " + injecteddir);
					System.exit(-1);
				}
			}
		}
		if (numExpected > numdirs) {
			System.out.println("More file variants were created then desired, " + numExpected + " > " + numfiles + ".");
			in.close();
			return;
		}

		// Fragment Variants
		System.out.println("Checking Function Fragment Variants:");
		line = in.nextLine(); // BEGIN;
		if (!line.startsWith("BEGIN: FunctionFragmentVariants")) {
			System.out.println("Expected 'BEGIN: FunctionFragmentVariants' but saw " + line + ".");
			in.close();
			return;
		}
		numExpected = 0;
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
			FunctionFragment originalfragment = new FunctionFragment(Paths.get(lin.next()), lin.nextInt(), lin.nextInt());
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
			FunctionFragment uniformstorage = null;
			for (int i = 0; i < numinject; i++) {
				line = in.nextLine();

				// check if a injection record, if not then header was
				// incorrect!
				if (!line.startsWith("\t")) {
					System.out.println("Output error, the # of injections for function fragment variant " + fnum + " is incorrect, or varaint report is missing.");
					System.exit(-1);
				}

				// get information
				lin = new Scanner(line);
				int forknum = lin.nextInt();
				FunctionFragment injectedfragment = new FunctionFragment(Paths.get(lin.next()), lin.nextInt(), lin.nextInt());
				String opname = lin.next();
				int clonetype = lin.nextInt();
				lin.close();

				Path forkpath = outputdir.resolve("" + forknum).toAbsolutePath().normalize();
				if(isUniform) {
					if(i == 0) {
						uniformstorage = new FunctionFragment(forkpath.relativize(injectedfragment.getSrcFile()).normalize()
								, injectedfragment.getStartLine(), injectedfragment.getEndLine());
					} else {
						FunctionFragment normalized = new FunctionFragment(forkpath.relativize(injectedfragment.getSrcFile()).normalize(), injectedfragment.getStartLine(), injectedfragment.getEndLine());
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
				
				if (!Files.exists(injectedfragment.getSrcFile())) {
					System.out.println("Source file containing injected function fragment does not exist. Variant: " + fnum + " Fork: " + forknum + " File: " + injectedfragment.getSrcFile());
					System.exit(-1);
				}
				if (!Files.isRegularFile(injectedfragment.getSrcFile())) {
					System.out.println("Source file containing injected function fragment is not a regular file. Variant: " + fnum + " Fork: " + forknum + " File: " + injectedfragment.getSrcFile());
					System.exit(-1);
				}
				
				if(!FragmentUtil.isFunction(injectedfragment, language)) {
					System.out.println("Injected function fragment is not a function." + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					System.exit(-1);
				}
				numlines = FragmentUtil.countLines(injectedfragment.getSrcFile());
				if(numlines < injectedfragment.getEndLine()) {
					System.out.println("Injected function fragment (specification) is invalid (endline proceeds end of file). " + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					System.exit(-1);
				}
				
				//check record
				if(!Files.exists(outputdir.resolve("function_fragments/" + fnum + "/" + forknum))) {
					System.out.println("Injected function fragment record is missing. Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
				}
				Path tmpfilei = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation", null);
				FragmentUtil.extractFragment(injectedfragment, tmpfilei);
				if(!filesEqual(tmpfilei, outputdir.resolve("function_fragments/" + fnum + "/" + forknum))) {
					System.out.println("Injected function fragment record is incorrect. Variant: " + " Fragment: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					System.exit(-1);
				}
				
				//check mutation
				Path tmpfileoe = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "CheckSimulation", null);
				Files.copy(tmpfileo, tmpfileoe, StandardCopyOption.REPLACE_EXISTING);
				if(opname.equals("none")) {
					if(!org.apache.commons.io.FileUtils.contentEquals(tmpfileo.toFile(), tmpfilei.toFile())) {
						System.out.println("Mutation: 'none' was not applied correctly. Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
					}
				} else {
					if(clonetype == 1) {
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileo.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  No difference between fragments."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(!org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 1, but fragments not same after normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
					} else if(clonetype == 2) {
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileo.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  No difference between fragments."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 2, but fragments are same after type 1 normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(!org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 2, but fragments not same after normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
					} else if(clonetype == 3) {
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileo.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  No difference between fragments."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 2, but fragments are same after type 1 normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfilei, tmpfilei)) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Fragment syntax not valid/parsable."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(0 != SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), tmpfileoe, tmpfileoe)) {
							System.out.println("Normalizing original fragment failed..."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
							System.exit(-1);
						}
						if(org.apache.commons.io.FileUtils.contentEquals(tmpfileoe.toFile(), tmpfilei.toFile())) {
							System.out.println("Mutation : " + opname + " was not properly applied.  Expected type 3, but fragments are same after normalization."  + " Variant: " + fnum + " Fork: " + forknum + " Fragment: " + injectedfragment);
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
		
		InventoriedSystem system_is = new InventoriedSystem(systemdir, language);
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
			
			
		//Fragments
			//check unchanged original files are not modified ((changed and non-original were checked previously))
			List<Path> modifiedfiles = new LinkedList<Path>();
			for(FunctionFragment fragment : functionfragment_tracker.get(i)) {
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
