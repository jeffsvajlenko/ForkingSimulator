package util;

import java.io.IOException;
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
	 * 
	 * @return
	 */
	public static Path getTxlDirectory() {
		return getInstallRoot().resolve("txl").toAbsolutePath().normalize();
	}
	
	/**
	 * 
	 * @param language
	 * @return
	 */
	public static Path getTxlDirectory(String language) {
		Path p = getTxlDirectory().resolve(language);
		if(Files.isDirectory(p)) {
			return p.toAbsolutePath().normalize();
		}
		else {
			return null;
		}
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
		String command = "txl " + " -o " + outputFile.toAbsolutePath().normalize().toString() + " " + inputFile.toAbsolutePath().normalize().toString() + " " + txlScriptName.toAbsolutePath().normalize().toString();
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
