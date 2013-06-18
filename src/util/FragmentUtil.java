package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;

import models.Fragment;

public class FragmentUtil {
	
	/**
	 * Extracts a fragment.
	 * @param f Fragment to extract.
	 * @param fout Path to file to write fragment to.  File must not already exist.
	 * @throws IOException If end of file is reached before endline during extraction, or if a general IO error occurs.
	 * @throws IllegalArgumentException If the fragment is invalid (file does not exist/unreadable, or if file could not hold the fragment, i.e. endline proceeds EOF).
	 * @throws FileNotFoundException File specified by fragment does not exist.
	 */
	public static void extractFragment(Fragment f, Path fout) throws FileNotFoundException, IOException {
		//Check input
		Objects.requireNonNull(f);
		Objects.requireNonNull(fout);
		if(!Files.exists(f.getSrcFile())) {
			throw new FileNotFoundException("File containing fragment does not exist.");
		}
		if(!Files.isReadable(f.getSrcFile())) {
			throw new IllegalArgumentException("File containing fragment is not readable.");
		}
		if(f.getEndLine() > countLines(f.getSrcFile())) {
			throw new IllegalArgumentException("File containing fragment is shorter than the endline of the fragment.");
		}
		
		int startline = f.getStartLine();
		int endline = f.getEndLine();
			
		//Prep out file
		Files.deleteIfExists(fout);
		Files.createFile(fout);
		
		//Extract fragment
		BufferedReader in = new BufferedReader(new FileReader(f.getSrcFile().toFile()));
		BufferedWriter out = new BufferedWriter(new FileWriter(fout.toFile()));
		String line = in.readLine();
		int linenum = 0;
			
		while(line != null && linenum < endline) {
			linenum++;
			if(linenum >= startline && linenum <= endline) {
				out.write(line + "\n");
			}
			line = in.readLine();
		}
		in.close();
		out.flush();
		out.close();
		
		//Check success (should have been caught previosuly though)
		if(linenum != endline) {
			throw new IOException("EOF was reached before endline.");
		}
	}
	
	/**
	 * Returns the number of lines in the file specified.
	 * @param file The location of the file.
	 * @return The number of lines in the file.
	 * @throws IOException If an I/O exception occurs while counting the lines.
	 * @throws FileNotFoundException If path points to a file that does not exist.
	 * @throws IllegalArgumentException If the specified file is not a regular file (symbolic links are followed), or if the file is not readable.
	 */
	public static int countLines(Path file) throws FileNotFoundException, IOException {
		//Check input
		Objects.requireNonNull(file);
		if(!Files.exists(file)) {
			throw new FileNotFoundException("Path file points to a file that does not exist.");
		}
		if(!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("Path file points to a file that is not regular.");
		}
		if(!Files.isReadable(file)) {
			throw new IllegalArgumentException("Path file points to a file that is not readable.");
		}
		
		//Count lines
		int countRec=0;
		RandomAccessFile randFile = null;
		FileReader fileRead = null;
		LineNumberReader lineRead = null;
		try {
			randFile = new RandomAccessFile(file.toFile(),"r");
			long lastRec=randFile.length();
			randFile.close(); 
			fileRead = new FileReader(file.toFile());
			lineRead = new LineNumberReader(fileRead);
			lineRead.skip(lastRec);
			countRec=lineRead.getLineNumber();
			fileRead.close();
			lineRead.close();
		}
		catch(IOException e)
		{
			if(randFile != null) {
				try{randFile.close();} catch (IOException ee) {}
			}
			if(fileRead != null) {
				try{fileRead.close();} catch (IOException ee) {}
			}
			if(lineRead != null) {
				try{lineRead.close();} catch (IOException ee) {}
			}
			throw new IOException();
		}
		return countRec;
	}
	
	/**
	 * Returns if the fragment represents a function.  Will return true if the code slice represented by the start/end lines of the fragment
	 * is a function of the specified language.  Will return false if it is not a function.  Note that if the fragment includes a function,
	 * but also includes other syntax external to the function (not including comments) it will return false.
	 * @param fragment The fragment to check.  Fragment must be valid (source file exists, is a regular file, and is readable; start/end-lines must be valid with respect to source file.)
	 * @param language The language of the fragment (must be of a language supported by this class).
	 * @return True if the fragment captures a function (in its entirity and without other external features except comments), false if it does not.
	 * @throws FileNotFoundException If the source file can not be found.
	 * @throws IOException If an IO error occurs.
	 * @throws IllegalArgumentException If one of the argument preconditions is broken: fragment is invalid (source file does not exist, is not a regular file, or is not readable, or start/endlines invalid) or if language is not supported.
	 * @throws NullPointerException if fragment of language are specified as null.
	 */
	public static boolean isFunction(Fragment fragment, String language) throws FileNotFoundException, IOException {
		//Check Input
		Objects.requireNonNull(fragment);
		Objects.requireNonNull(language);
		if(!supportedLanguage(language)) {
			throw new IllegalArgumentException("Language not supported.");
		}
		if(!Files.exists(fragment.getSrcFile())) {
			throw new FileNotFoundException("Fragment source file does not exist.");
		}
		if(!Files.isReadable(fragment.getSrcFile())) {
			throw new IllegalArgumentException("File specified by fragment is not readable.");
		}
		if(!Files.isRegularFile(fragment.getSrcFile())) {
			throw new IllegalArgumentException("File specified by fragment is not a regular file.");
		}
		int numlines = FragmentUtil.countLines(fragment.getSrcFile());
		if(fragment.getEndLine() > numlines) {
			throw new IllegalArgumentException("Fragment is invalid, endline proceeds end of file.");
		}
		
		//Prep fragment
		Path tmpfile = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "FragmentUtil-isFunction", null);
		FragmentUtil.extractFragment(fragment, tmpfile);
		
		//check is function
		boolean retval = FragmentUtil.isFunction(tmpfile, language);
		
		//Cleanup
		Files.delete(tmpfile);
		
		//return answer
		return retval;
	}
	
	/**
	 * Determines if a file contains a single function of the specified language.  True means 
	 * the file contains a function, false means it does not or that parsing failed (Grammar
	 * may not be perfect, do not rely on false meaning not a function!  Grammar should be good
	 * enough to be reliable for true cases).
	 * @param function Path to a file containing the syntax to test if a function.
	 * @param language The language of the syntax.
	 * @return true if the file specified by the path function is a function, or false if it is not.
	 * @throws IOException If an IOException occurs during analysis.
	 * @throws FileNotFoundException If function does not point to an existing file.
	 */
	public static boolean isFunction(Path function, String language) throws FileNotFoundException, IOException {
		//Check Input
		Objects.requireNonNull(function);
		Objects.requireNonNull(language);
		if(!Files.exists(function)) {
			throw new FileNotFoundException("Function does not exist.");
		}
		if(!Files.isReadable(function)) {
			throw new IllegalArgumentException("Function is not readable.");
		}
		if(!supportedLanguage(language)) {
			throw new IllegalArgumentException("Language not supported.");
		}
		
		//Prep
		Path txlscript = SystemUtil.getTxlDirectory(language).resolve("isfunction.txl");
		Path tmpout = null;
		tmpout = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "FragmentUtil-isFunction", null);
		
		//Run isFunction txl program
		int retval = SystemUtil.runTxl(txlscript.toAbsolutePath().normalize(), function.toAbsolutePath().normalize(), tmpout.toAbsolutePath().normalize());
		
		//Cleanup
		Files.delete(tmpout);
		
		//Check
		if(retval == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Injects a fragment into a file at the specified location.  If an exception is thrown, the original file may not be left in its original state!
	 * @param file The file to inject into.
	 * @param location The sourceline to inject at in file.
	 * @param f The fragment to inject.
	 * @throws NoSuchFileException If file or the file referenced by fragment f does not exist.
	 * @throws IllegalArgumentException If file or the file referenced by fragment f is not readable, if file is a symbolic link, or if location is invalid (with respect to file) or if fragment is invalid (with respect to its source file).
	 */
	public static void injectFragment(Path file, int location, Fragment f) throws NoSuchFileException, IOException {
		//Check pointers
		Objects.requireNonNull(file);
		Objects.requireNonNull(f);
		
		//Check file
		if(!Files.exists(file)) {
			throw new NoSuchFileException("File does not exist.");
		}
		if(!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		if(!Files.isReadable(file)) {
			throw new IllegalArgumentException("File is not readable.");
		}
		
		//Check fragment file
		if(!Files.exists(f.getSrcFile())) {
			throw new NoSuchFileException("Fragment source file does not exist.");
		}
		if(!Files.isRegularFile(f.getSrcFile())) {
			throw new IllegalArgumentException("Fragment source file is not a regular file.");
		}
		if(!Files.isReadable(f.getSrcFile())) {
			throw new IllegalArgumentException("Fragment source file is not readable is not readable.");
		}
		
		//Check Location
		int numlinesfile = FragmentUtil.countLines(file);
		if(location < 1 || location > numlinesfile+1) {
			throw new IllegalArgumentException("Location is invalid.");
		}
		
		//Check Fragment Valid
		int numlinesfragment = FragmentUtil.countLines(f.getSrcFile());
		if(f.getEndLine() > numlinesfragment) {
			throw new IllegalArgumentException("Fragment is invalid.");
		}
		
		//Extact fragment to inject
		Path fragmentfile = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "FragmentUtil-injectFragment-fragment", null);
		FragmentUtil.extractFragment(f, fragmentfile);
		
		//Inject fragment
		injectFragment(file,location,fragmentfile);
		
		//cleanup
		Files.delete(fragmentfile);
	}
	
	/**
	 * Injects file fragment into file file at the specified location.  If an exception is thrown, the original file may not be left in its original state!
	 * @param file The file to inject into.
	 * @param location The sourceline in file to inject at.
	 * @param fragment File containing code fragment to inject.
	 * @throws IOException If an IOException occurs.
	 * @throws FileNotFoundException If file or fragment does not refer to a file.
	 */
	public static void injectFragment(Path file, int location, Path fragment) throws FileNotFoundException, IOException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(fragment);
		
		//Check file
		if(!Files.exists(file)) {
			throw new NoSuchFileException("File does not exist.");
		}
		if(!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
			throw new IllegalArgumentException("File is not a regular file.");
		}
		if(!Files.isReadable(file)) {
			throw new IllegalArgumentException("File is not readable.");
		}
		
		//Check fragment file
		if(!Files.exists(fragment)) {
			throw new NoSuchFileException("Fragment file does not exist.");
		}
		if(!Files.isRegularFile(fragment)) {
			throw new IllegalArgumentException("Fragment file is not a regular file.");
		}
		if(!Files.isReadable(fragment)) {
			throw new IllegalArgumentException("Fragment file is not readable is not readable.");
		}
		
		//Check Location
		int numlinesfile = FragmentUtil.countLines(file);
		if(location < 1 || location > numlinesfile+1) {
			throw new IllegalArgumentException("Location is invalid.");
		}
		
		//Create temp file
		Path tmp = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "FragmentUtil-injectFragment-fragment", null);
		
		//Write to the tmp file the original file with the fragment injected
		PrintWriter tmpwriter = new PrintWriter(new FileWriter(tmp.toFile()));
		BufferedReader fileIn = new BufferedReader(new FileReader(file.toFile()));
		BufferedReader fragIn = new BufferedReader(new FileReader(fragment.toFile()));
		try {
			int linenum = 1;
			while(linenum < location) {
				tmpwriter.println(fileIn.readLine());
				linenum++;
			}
			String line;
			while((line = fragIn.readLine())!= null) {
				tmpwriter.println(line);
			}
			while((line = fileIn.readLine()) != null) {
				tmpwriter.println(line);
			}
		} catch (IOException e) {
			tmpwriter.flush(); tmpwriter.close();
			try {fileIn.close();} catch (IOException ee) {};
			try {fragIn.close();} catch (IOException ee) {};
			try {Files.delete(tmp);} catch (IOException ee) {};
			throw e;
		}
		tmpwriter.close();
		fileIn.close();
		fragIn.close();
		
		//Move into original file
		PrintWriter writer = new PrintWriter(new FileWriter(file.toFile()));
		BufferedReader reader = new BufferedReader(new FileReader(tmp.toFile()));
		String line;
		try {
			while((line = reader.readLine())!= null) {
				writer.println(line);
			}
		}
		catch (IOException e) {
			try {reader.close();} catch (IOException ee) {};
			writer.flush(); writer.close();
			try {Files.delete(tmp);} catch (IOException ee) {};
			throw e;
		}
		writer.flush(); writer.close();
		reader.close();
		
		//Cleanup
		Files.deleteIfExists(tmp);
	}
	
	private static boolean supportedLanguage(String language) {
		language = language.toLowerCase();
		if(language.equals("java") || language.equals("c") || language.equals("cs")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the % similarity (by line) between two files.
	 * @param file1 The first file.
	 * @param file2 The second file.
	 * @return the similarity ratio, or a negative number if there was a file error.
	 */
	
	/**
	 * Returns the % similarity (by line) between the two files.  Similarity is measured by first finding the ratio of the lines in each
	 * fragment which is unique to that fragment.  Similarity is reported as one minus this ratio from the fragment with the largest ratio
	 * of unique lines.  Empty lines are ignored during this measurement.  Lines which differ by extra whitespace are considered equivalent.
	 * @param file1 The first file.  Must exist, be a regular file, and be readable.
	 * @param file2 The second file.  Must exist, be a regular file, and be readable.
	 * @return the similarity between the fragments.
	 * @throws FileNotFoundException If either of file1 or file2 does not exist.
	 * @throws IllegalArgumentExcpetion If either of file1 or file 2 are not regular files or are not readable.
	 * @throws IOException If an IO exception occurs during the reading of these files.
	 */
	public static double getSimilarity(Path file1, Path file2) throws FileNotFoundException, IOException {
		//Check input
		Objects.requireNonNull(file1);
		Objects.requireNonNull(file2);
		if(!Files.exists(file1)) {
			throw new FileNotFoundException("File1 does not exist.");
		}
		if(!Files.exists(file2)) {
			throw new FileNotFoundException("File2 does not exist.");
		}
		if(!Files.isRegularFile(file1)) {
			throw new IllegalArgumentException("File 1 is not a regular file.");
		}
		if(!Files.isRegularFile(file2)) {
			throw new IllegalArgumentException("File 2 is not a regular file.");
		}
		if(!Files.isReadable(file1)) {
			throw new IllegalArgumentException("File 1 is not readable.");
		}
		if(!Files.isReadable(file2)) {
			throw new IllegalArgumentException("File 2 is not readable.");
		}
		
		// Get line counts
		int fileSize1 = FragmentUtil.countLines(file1);
		int fileSize2 = FragmentUtil.countLines(file2);
		
		//Check similarity
		double leftUnique=0.0;
		double rightUnique=0.0;
		Process p = Runtime.getRuntime().exec("diff " + file1.toAbsolutePath().normalize().toString() + " " + file2.toAbsolutePath().normalize().toString());
		int leftDistinct=0; 
		int rightDistinct=0;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		new StreamGobbler(p.getErrorStream()).start();
		
		String line = null;
		
		while ((line = in.readLine()) != null) {
			if (line.substring(0,1).contains("<")) {
				leftDistinct++;
			}
			if(line.substring(0,1).contains(">")) {
				rightDistinct++;
			}
		}          
		leftUnique = ((double) ((double)leftDistinct / (double)(fileSize1)));
		rightUnique = ((double) ((double)rightDistinct / (double)(fileSize2)));

		//Return similarity
		return 1.0d - Math.max(leftUnique, rightUnique);
	}
}
