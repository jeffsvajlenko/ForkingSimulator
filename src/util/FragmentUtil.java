package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import models.Fragment;

public class FragmentUtil {
	
	/**
	 * Extracts a fragment.
	 * @param f Fragment to extract.
	 * @param fout Path to file to write fragment to.  File must not already exist.
	 * @throws IOException If end of file is reached before endline during extraction, or if a general IO error occurs.
	 * @throws IllegalArgumentException If the fragment is invalid (file does not exist/unreadable, or if file could not hold the fragment, i.e. endline proceeds EOF), or if the output file already exists.
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
		if(Files.exists(fout)) {
			throw new IllegalArgumentException("Output file already exists.");
		}
		if(f.getEndLine() > countLines(f.getSrcFile())) {
			throw new IllegalArgumentException("File containing fragment is shorter than the endline of the fragment.");
		}
		
		int startline = f.getStartLine();
		int endline = f.getEndLine();
			
		//Prep out file
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
		
		//Check success
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
	 * Determines if a file contains a single function of the specified language.  True means 
	 * the file contains a function, false means it does not or that parsing failed (Grammar
	 * may not be perfect, do not rely on false meaning not a function!  Grammar should be good
	 * enough to be reliable for true cases).
	 * @param function Path to a file containing the syntax to test if a function.
	 * @param language The language of the syntax.
	 * @return true if the file specified by the path function is a function, or false if it is not.
	 * @throws IOException If an IOException occurs during analysis.
	 */
	public static boolean isFunction(Path function, String language) throws IOException {
		//Check Input
		Objects.requireNonNull(function);
		Objects.requireNonNull(language);
		if(!Files.exists(function)) {
			throw new IllegalArgumentException("Function does not exist.");
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
		
		//Check
		if(retval == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean supportedLanguage(String language) {
		language = language.toLowerCase();
		if(language.equals("java") || language.equals("c") || language.equals("cs")) {
			return true;
		} else {
			return false;
		}
	}
}
