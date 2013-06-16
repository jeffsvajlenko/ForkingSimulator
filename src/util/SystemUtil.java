package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class SystemUtil {
	/**
	 * 
	 * @return
	 */
	public static Path getInstallRoot() {
		return Paths.get(".").toAbsolutePath().normalize();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Path getScriptsLocation() {
		return getInstallRoot().resolve("scripts").toAbsolutePath().normalize();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Path getTemporaryDirectory() {
		return getInstallRoot().resolve("tmp").toAbsolutePath().normalize();
	}
	
	/**
	 * @return The directory containing the txl scripts.
	 */
	public static Path getTxlDirectory() {
		return getInstallRoot().resolve("txl").toAbsolutePath().normalize();
	}
	
	/**
	 * Returns the txl script directory for the following language.  Returns null if it does not exist.
	 * @param language String representation of the language (must match directory name).
	 * @return the txl script directory for the following language.  Returns null if it does not exist.
	 */
	public static Path getTxlDirectory(String language) {
		Path p = getTxlDirectory().resolve(language);
		if(Files.isDirectory(p)) {
			return p.toAbsolutePath().toAbsolutePath().normalize();
		} else {
			return null;
		}
	}
	
	private static Path txl = null;
	/**
	 * Returns a path to the TXL executable, or null if TXL is not installed.  Relies on 
	 * @return a path to the TXL executable, or null if TXL is not installed.
	 */
	public static Path getTxlExecutable() {
		 if (txl == null) {
			 BufferedReader br = null;
			 try {
				Process p = Runtime.getRuntime().exec("which txl");
				br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				new StreamGobbler(p.getErrorStream()).start();
				String s = br.readLine();
				txl = Paths.get(s);
				br.close();
				p.destroy();
			} catch (IOException e) {
				if(br != null) {
					try{br.close();} catch(IOException ee){};
				}
				return null;
			}
		 }
		 return txl;
	}
	
	/**
	 * Returns the root directory of the mutators.
	 * @return the root directory of the mutators.
	 */
	public static Path getOperatorDirectory() {
		return getInstallRoot().resolve("operators").toAbsolutePath().normalize();
	}
	
	/**
	 * Returns the directory containing the mutators for the specified language.
	 * @param language the language
	 * @return the directory containing the mutators for the specified language.
	 */
	public static Path getOperatorDirectory(String language) {
		return getOperatorDirectory().resolve(language);
	}
	
	/**
	 * Runs the given TXL script for the given input file and outputs the result to the output file.
	 * The function returns once the script is complete.
	 * @param txlScriptName The TXL script to run.
	 * @param inputFile The input file.
	 * @param outputFile The output file.
	 * @return The return value of the TXL execution (0 is success, > 0 is failure).  Or -1 if TXL execution failed.
	 */
	public static int runTxl(Path txlScriptName, Path inputFile, Path outputFile) {
		//Check input
		Objects.requireNonNull(txlScriptName);
		Objects.requireNonNull(inputFile);
		Objects.requireNonNull(outputFile);
		if(!Files.isReadable(txlScriptName)) {
			throw new IllegalArgumentException("Specified txl script does not exist or is not readable (" + txlScriptName.toAbsolutePath().normalize() + ".");
		}
		if(!Files.isReadable(inputFile)) {
			throw new IllegalArgumentException("Input file is not readable or does not exist.");
		}
		if(!Files.exists(outputFile)) {
			try {
				Files.createFile(outputFile);
			} catch (IOException e) {
				throw new IllegalArgumentException("Output file can not be written to.");
			}
		}
		if(!Files.isWritable(outputFile)) {
			throw new IllegalArgumentException("Output file can not be written to.");
		}
		
		//Execute TXL
		String command = "txl " + " -w 100000 -o " + outputFile.toAbsolutePath().normalize().toString() + " " + inputFile.toAbsolutePath().normalize().toString() + " " + txlScriptName.toAbsolutePath().normalize().toString();
		Process process = null;
		int retval;
		try {
			process = Runtime.getRuntime().exec(command);
			new StreamGobbler(process.getInputStream()).start();
			new StreamGobbler(process.getErrorStream()).start();
			retval = process.waitFor();
		} catch (IOException e) {
			retval = -1;
		} catch (InterruptedException e) {
			retval = -1;
		} finally {
			if(process != null) {
				try {process.getErrorStream().close();} catch (IOException e) {}
				try {process.getInputStream().close();} catch (IOException e) {}
				try {process.getOutputStream().close();} catch (IOException e) {}
				process.destroy();
				process = null;
			}
		}
		
		return retval;
	}
}
