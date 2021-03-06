package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import models.Fragment;
import util.StreamGobbler;

/**
 * 
 * Selects function fragments from the system.
 *
 */
public class SelectFunctionFragments {
	
	public static List<Fragment> getFunctionFragmentsRecursive(File srcloc, String language) {
	
		//Delete leftover extraction data
		new File(srcloc + "/_functions.xml").delete();
		
		// Extract the functions from the code base.  If error occurs, report possible reasons and return null.
		int retval;
		try {
			//Execute statement and gobble output
			String command =  SystemUtil.getScriptsLocation().toString() + "/Extract functions " + language + " " + srcloc.getAbsolutePath() + "/";
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			new StreamGobbler(process.getErrorStream()).start();
			new StreamGobbler(process.getInputStream()).start();
			
			// Wait for process to finish and collect return value
			retval = process.waitFor();
			
			//Cleanup
			process.getErrorStream().close();
			process.getInputStream().close();
			process.getOutputStream().close();
			process.destroy();
			process = null;
		} catch (IOException e) { 
			e.printStackTrace();
			System.out.println("Error(SelectFunctionFragments): Failed to extract functions from the code base.  Possible Reasons: NiCad is not installed.  /scripts/Extract is not setup correctly.  System folder is unreadable or code base is missing.");
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error(SelectFunctionFragments): Failed due to extraction process being interrupted.");
			return null;
		}
		if(retval != 0) {
			System.out.println("Error(SelectFunctionFragments): Extract script failed.  Possible Reasons: Extract script not setup correctly.  Code base abscent from .../data/system/.");
			return null;
		}
		
		// Extract function information from file into a list structure
		List<Fragment> functions = new ArrayList<Fragment>();
		File functionsFile = new File(srcloc.getAbsolutePath() + "/_functions.xml");
		Scanner functionScanner;
		try {
			functionScanner = new Scanner(new FileInputStream(functionsFile));
			while(functionScanner.hasNextLine()) { // find the source files from _functions.xml add them to the functions list
				String line = functionScanner.nextLine();
				if(line.matches("<source.*")) {
					String[] parts = line.split(" ");
					String filename = parts[1].substring(6, parts[1].length()-1); // extract filename
					if(filename.endsWith(".ifdefed")) {
						filename = filename.substring(0, filename.length()-8);
					}
					int startline = Integer.parseInt(parts[2].substring(11,parts[2].length()-1)); // extract start line
					int endline = Integer.parseInt(parts[3].substring(9,parts[3].length()-2)); // extract end line
					Fragment f = new Fragment(Paths.get(filename).toAbsolutePath().normalize(), startline, endline);
					functions.add(f);
				}
			}
			functionScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error(SelectFunctionFragments): Failed to open .../data/system/_functions.xml.  Possible Reason: _functions.xml was not created by Extract script.  Could the code base be missing?");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("");
			return null;
		}

		
		// Cleanup
		//new File(srcloc.getAbsolutePath() + "/_functions.xml").delete(); // no longer need this file, so trash it
		
		//Cleanup ifdef
		String command =  SystemUtil.getScriptsLocation().toString() + "/RemoveIfDefed " + srcloc.getAbsolutePath() + "/";
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec(command);
			new StreamGobbler(process.getErrorStream()).start();
			new StreamGobbler(process.getInputStream()).start();
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
		
		// Return
		return functions;
	}

	public static List<Fragment> getFunctionFragmentsInFile(Path file, String language) throws IOException {
		if(!Files.exists(file)) {
			throw new IllegalArgumentException("File must exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File must be a regular file.");
		}
		List<Fragment> retval;
		List<Fragment> retval_fixed;
		file = file.toAbsolutePath().normalize();
		
		Path dir = Files.createTempDirectory(SystemUtil.getTemporaryDirectory(), "ForkSim_");
		Files.copy(file, dir.resolve(file.getFileName()));
		
		retval = getFunctionFragmentsRecursive(dir.toFile(), language);
		
		retval_fixed = new LinkedList<Fragment>();
		for(Fragment f : retval) {
			retval_fixed.add(new Fragment(file, f.getStartLine(), f.getEndLine()));
		}
		System.out.println(dir);
		//FileUtils.deleteDirectory(dir.toFile());
		return retval_fixed;
	}
}
