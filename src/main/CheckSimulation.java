package main;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CheckSimulation {
	public static void main (String args[]) throws FileNotFoundException {
		if(args.length != 1) {
			System.out.println("Require output file.");
		}
		
		Path output = Paths.get(args[0]);
		if(!Files.isReadable(output) || !Files.isRegularFile(output)) {
			System.out.println("Output file is invalid.");
		}
		
		Scanner in = new Scanner(output.toFile());
		String line;
		
		//getProperties
		line = in.nextLine(); //BEGIN:
		Path outputdir = Paths.get(in.nextLine().replaceFirst("\toutput_directory=", ""));
		Path systemdir = Paths.get(in.nextLine().replaceFirst("\tsystem_directory=", ""));
		Path repositorydir = Paths.get(in.nextLine().replaceFirst("\trepository_directory=", ""));
		String language = in.nextLine().replaceFirst("\tlanguage=", "");
		int numforks = Integer.parseInt(in.nextLine().replaceFirst("\t#forks=", ""));
		int maxinjects = Integer.parseInt(in.nextLine().replaceFirst("\tmax#injects=", ""));
		int numfiles = Integer.parseInt(in.nextLine().replaceFirst("\t#files=", ""));
		int numdirs = Integer.parseInt(in.nextLine().replaceFirst("\t#dirs=", ""));
		int numfragments = Integer.parseInt(in.nextLine().replace("\t#fragments=", ""));
		double mutationrate = Double.parseDouble(in.nextLine().replace("\tmutationrate=", ""));
		line = in.nextLine(); //END:
		
		//FileVariants
		line = in.nextLine(); // BEGIN;
		int numExpected = 1;
		while(!line.startsWith("END:")) {
		
		//Read and check file variant header
			//Get header
			line = in.nextLine();
			
			//Get details
			Scanner lin = new Scanner(line);
			int filenum = lin.nextInt();
			int numinject = lin.nextInt();
			Path originalfile = Paths.get(lin.next());
			lin.close();
			
			//Check file variant #
			if(filenum != numExpected) {
				System.out.println("Output error, the # of a file variant is not correct.");
				System.exit(-1);
			}
			numExpected++;

			//Check referenced file
			if(!Files.exists(originalfile)) {
				System.out.println("Original file referenced by file variant " + filenum + " does not exist.");
				System.exit(-1);
			}
			if(!Files.exists(outputdir.resolve("files/" + filenum))) {
				System.out.println("Original file for file variant " + filenum + " was not recorded in files/.");
				System.exit(-1);
			}
			
		//Read and check file variant injections (output and validity)
			List<Integer> forksinvolved = new LinkedList<Integer>();
			for(int i = 0; i < numinject; i++) {
				line = in.nextLine();
				lin = new Scanner(line);
				
				if(!line.startsWith("\t")) {
					System.out.println("Output error, the # of injections for file variant " + filenum + " is incorrect, or varaint report is missing.");
					System.exit(-1);
				}
				
			}
			
			System.exit(0);
		}
		
		
		in.close();
		
	}
}
