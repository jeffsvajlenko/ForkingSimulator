package util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import models.FunctionFragment;

import org.junit.Test;

public class FragmentUtilTest {

	@Test
	public void testExtractFragment() throws IOException {
	//Cleanup
		Files.deleteIfExists(Paths.get("testdata/FragmentUtilTest/ExtractTest"));	
		
	//Perform extract
		FragmentUtil.extractFragment(new FunctionFragment(Paths.get("testdata/FragmentUtilTest/InventoriedSystem.java"), 39, 84), Paths.get("testdata/FragmentUtilTest/ExtractTest"));
	
	//Check
		List<String> manual = new LinkedList<String>();
		List<String> auto = new LinkedList<String>();
		
		//Fill Auto
		Scanner s = new Scanner(Paths.get("testdata/FragmentUtilTest/ExtractTest").toFile());
		while(s.hasNextLine()) {
			auto.add(s.nextLine());
		}
		s.close();
		
		//Fill Manual
		s = new Scanner(Paths.get("testdata/FragmentUtilTest/function").toFile());
		while(s.hasNextLine()) {
			manual.add(s.nextLine());
		}
		s.close();
	
		
		assertTrue(auto.size() == manual.size());
		for(int i = 0; i < auto.size(); i++) {
			assertEquals(manual.get(i), auto.get(i));
		}
		
	//Cleanup
		Files.deleteIfExists(Paths.get("testdata/FragmentUtilTest/ExtractTest"));
		
	//Check exceptions
		boolean thrown;
		
		thrown = false;
		try {
			FragmentUtil.extractFragment(null, Paths.get("testdata/FragmentUtilTest/ExtractTest"));
		} catch (NullPointerException e) {
			thrown = true;
		}
		assertTrue("Failed to throw nullpointerexception for null fragment.", thrown);
		
		thrown = false;
		try {
			FragmentUtil.extractFragment(new FunctionFragment(Paths.get("testdata/FragmentUtilTest/InventoriedSystem.java"), 39, 84), null);
		} catch (NullPointerException e) {
			thrown = true;
		}
		assertTrue("Failed to throw nullpointerexception for null output directory.", thrown);
		
		thrown = false;
		try {
			FragmentUtil.extractFragment(new FunctionFragment(Paths.get("testdata/FragmentUtilTest/InventoriedSystem343434.java"), 39, 84), Paths.get("testdata/FragmentUtilTest/ExtractTest"));
		} catch (FileNotFoundException e) {
			thrown = true;
		}
		assertTrue("Failed to throw file not found exception for non-existant file in fragment.", thrown);
		
		thrown = false;
		try {
			FragmentUtil.extractFragment(new FunctionFragment(Paths.get("testdata/FragmentUtilTest/InventoriedSystem.java"), 39, 1000), Paths.get("testdata/FragmentUtilTest/ExtractTest"));
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue("Failed to throw illegal argument exception for invalid fragment.", thrown);
		
		Files.setPosixFilePermissions(Paths.get("testdata/FragmentUtilTest/cantread"), PosixFilePermissions.fromString("-w--w----"));
		thrown = false;
		try {
			FragmentUtil.extractFragment(new FunctionFragment(Paths.get("testdata/FragmentUtilTest/cantread"), 39, 84), Paths.get("testdata/FragmentUtilTest/ExtractTest"));
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue("Failed to throw illegal argument exception for an input file which is not readable.", thrown);
		Files.setPosixFilePermissions(Paths.get("testdata/FragmentUtilTest/cantread"), PosixFilePermissions.fromString("rw-rw-r--"));
		
	}

	@Test
	public void testCountLines() throws IOException {
		//Check some success cases
		assertTrue(FragmentUtil.countLines(Paths.get("testdata/FragmentUtilTest/function")) == 46);
		assertTrue(FragmentUtil.countLines(Paths.get("testdata/FragmentUtilTest/InventoriedSystem.java")) == 299);
		assertTrue(FragmentUtil.countLines(Paths.get("testdata/FragmentUtilTest/notfunction")) == 39);
		
		//Check some exception cases
		boolean thrown;
		
		thrown = false;
		try {
			FragmentUtil.countLines(null);
		} catch(NullPointerException e) {
			thrown = true;
		}
		assertTrue("Failed to catch null file path.", thrown);
		
		thrown = false;
		try {
			FragmentUtil.countLines(Paths.get("testdata/FragmentUtilTest/nosuchfile"));
		} catch(FileNotFoundException e) {
			thrown = true;
		}
		assertTrue("Failed to catch file not found exception.", thrown);
		
		Files.setPosixFilePermissions(Paths.get("testdata/FragmentUtilTest/cantread"), PosixFilePermissions.fromString("-w--w----"));
		thrown = false;
		try {
			FragmentUtil.countLines(Paths.get("testdata/FragmentUtilTest/cantread"));
		} catch(IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue("Failed to catch illegal argument exception - file not readable.", thrown);
		Files.setPosixFilePermissions(Paths.get("testdata/FragmentUtilTest/cantread"), PosixFilePermissions.fromString("rw-rw-r--"));
		
		thrown = false;
		try {
			FragmentUtil.countLines(Paths.get("testdata/FragmentUtilTest/"));
		} catch(IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue("Failed to catch illegal argument exception - file is not a regulr file.", thrown);
	}

	@Test
	public void testInjectFragment() throws IOException {
		FunctionFragment f = new FunctionFragment(Paths.get("testdata/FragmentUtilTest/InventoriedSystem.java"), 44, 84);
		Path file = Paths.get("testdata/FragmentUtilTest/InventoriedSystem.java");
		Path realfile = Paths.get("testdata/FragmentUtilTest/InventoriedSystem_copy.java");
		
		Files.deleteIfExists(realfile);
		Files.copy(file, realfile);
		FragmentUtil.injectFragment(realfile, 300, f);
		assertTrue(filesEqual(realfile, Paths.get("testdata/FragmentUtilTest/InventoriedSystem_injectEnd.java")));
		
		Files.deleteIfExists(realfile);
		Files.copy(file, realfile);
		FragmentUtil.injectFragment(realfile, 95, f);
		assertTrue(filesEqual(realfile, Paths.get("testdata/FragmentUtilTest/InventoriedSystem_injectMiddle.java")));
		
		Files.deleteIfExists(realfile);
		Files.copy(file, realfile);
		FragmentUtil.injectFragment(realfile, 1, f);
		assertTrue(filesEqual(realfile, Paths.get("testdata/FragmentUtilTest/InventoriedSystem_injectStart.java")));
	}
	
	private boolean filesEqual(Path f1, Path f2) throws FileNotFoundException {
		List<String> file1 = new LinkedList<String>();
		List<String> file2 = new LinkedList<String>();
		
		Scanner s = new Scanner(f1.toFile());
		while(s.hasNextLine()) {
			file1.add(s.nextLine());
		}
		s.close();
		
		s = new Scanner(f2.toFile());
		while(s.hasNextLine()) {
			file2.add(s.nextLine());
		}
		s.close();
		
		if(file1.size() != file2.size()) {
			return false;
		}
		
		for(int i = 0; i < file1.size(); i++) {
			if(!file1.get(i).equals(file2.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	@Test
	public void testIsFunction() throws IOException {
		//Check success cases
		assertTrue(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/function"), "java"));
		assertFalse(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/notfunction"), "java"));
		assertTrue(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/cfunction"), "c"));
		assertFalse(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/cnotfunction1"), "c"));
		assertFalse(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/cnotfunction2"), "c"));
		assertTrue(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/csfunction1"), "cs"));
		assertTrue(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/csfunction2"), "cs"));
		assertFalse(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/csnotfunction1"), "cs"));
		assertFalse(FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/csnotfunction2"), "cs"));
		
		
		//Check exception cases
		boolean thrown;
		//thrown=false;
		//try {
		//	FragmentUtil.isFunction(null, "java");
		//} catch (NullPointerException e) {
		//	thrown=true;
		//}
		//assertTrue("Failed to throw null pointer exception for function path.", thrown);
		
		thrown=false;
		try {
			FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/function"), null);
		} catch (NullPointerException e) {
			thrown=true;
		}
		assertTrue("Failed to throw null pointer exception for language.", thrown);
		
		thrown=false;
		try {
			FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/function324234324"), "java");
		} catch (FileNotFoundException e) {
			thrown=true;
		}
		assertTrue("Failed to throw illegal argument exception for non-existant function.", thrown);
		
		Files.setPosixFilePermissions(Paths.get("testdata/FragmentUtilTest/cantread"), PosixFilePermissions.fromString("-w--w----"));
		thrown=false;
		try {
			FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/cantread"), "java");
		} catch (IllegalArgumentException e) {
			thrown=true;
		}
		assertTrue("Failed to throw illegal argument exception for unreadable function.", thrown);
		Files.setPosixFilePermissions(Paths.get("testdata/FragmentUtilTest/cantread"), PosixFilePermissions.fromString("rw-rw-r--"));
		
		thrown=false;
		try {
			FragmentUtil.isFunction(Paths.get("testdata/FragmentUtilTest/function"), "notareallanguage");
		} catch (IllegalArgumentException e) {
			thrown=true;
		}
		assertTrue("Failed to throw illegal argument exception for unsupported language.", thrown);	
	}
}
