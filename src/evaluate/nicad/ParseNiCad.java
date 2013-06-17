package evaluate.nicad;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.CloneClass;
import models.Fragment;


public class ParseNiCad {

	public static LinkedHashSet<CloneClass<Path>> parseNiCadFiles(Path output) throws FileNotFoundException {
		LinkedHashSet<CloneClass<Path>> parsed = new LinkedHashSet<CloneClass<Path>>();
		
		for(CloneClass<Fragment> cclass : ParseNiCad.parseNiCadFragments(output)) {
			
			LinkedHashSet<Path> paths = new LinkedHashSet<Path>();
			for(Fragment fragment : cclass) {
				paths.add(fragment.getSrcFile());
			}
			parsed.add(new CloneClass<Path>(paths));
		}
		return parsed;
	}
	
	public static LinkedHashSet<CloneClass<Fragment>> parseNiCadFragments(Path output) throws FileNotFoundException {
		LinkedHashSet<CloneClass<Fragment>> clones = new LinkedHashSet<CloneClass<Fragment>>();
		
		Pattern pattern = Pattern.compile("<source file=\"(.*)\" startline=\"(.*)\" endline=\"([0-9]*)\"? pcid=\".*\"></source>");
		Matcher matcher;
		
		Scanner s = new Scanner(output.toFile());
		String line;
		boolean inClass = false;
		LinkedHashSet<Fragment> cclass = null;
		
		while(s.hasNextLine()) {
			line = s.nextLine();
			
			if(!inClass) {
				if(line.startsWith("<class ")) {
					inClass = true;
					cclass = new LinkedHashSet<Fragment>();
				}
			} else {
				if(line.startsWith("</class>")) {
					clones.add(new CloneClass<Fragment>(cclass));
					cclass = null;
					inClass = false;
				} else {
					matcher = pattern.matcher(line);
					matcher.matches();
					Path file = Paths.get(matcher.group(1)).toAbsolutePath().normalize();
					int start = Integer.parseInt(matcher.group(2));
					int end = Integer.parseInt(matcher.group(3));
					Fragment fragment = new Fragment(file, start, end);
					cclass.add(fragment);
				}
			}
		}
		s.close();
		return clones;
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		System.out.println(args[0]);
		HashSet<CloneClass<Fragment>> clone_classes = ParseNiCad.parseNiCadFragments(Paths.get(args[0]));
		for(CloneClass<Fragment> cclass : clone_classes) {
			System.out.println("##");
			for(Fragment fragment : cclass) {
				System.out.println("\t" + fragment.getSrcFile() + " " + fragment.getStartLine() + " " + fragment.getEndLine());
			}
		}
	}
}
