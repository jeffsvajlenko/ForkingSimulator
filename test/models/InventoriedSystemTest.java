package models;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

public class InventoriedSystemTest {

	@Test
	public void testInventoriedSystem() {
		try {
			InventoriedSystem is = new InventoriedSystem(Paths.get("testdata/inventoriedSystem/"), "java");

		//Check file list
			//Get list it got
			List<Path> files = is.getFiles();
			
			//Get list of known files
			List<Path> files_c = new LinkedList<Path>();
			Scanner s = new Scanner(new File("testdata/inventoriedSystem/filelist"));
			while(s.hasNextLine()) {
				files_c.add(Paths.get(s.nextLine()).toAbsolutePath().normalize());
			}
			s.close();
			
			//Check correct
			for(Path p : files) {
				assertTrue("InventoriedSystem files contains a file that is not in check " + p,files_c.contains(p.toAbsolutePath().normalize()));
			}
			for(Path p : files_c) {
				assertTrue("InventoriedSystem files is missing file " + p,files.contains(p.toAbsolutePath().normalize()));
			}
		
		//Check directory list
			//Get directory it found
			List<Path> dirs = is.getDirectories();
			
			//Get list of known directories
			List<Path> dirs_c = new LinkedList<Path>();
			s = new Scanner(new File("testdata/inventoriedSystem/directorylist"));
			while(s.hasNextLine()) {
				dirs_c.add(Paths.get(s.nextLine()).toAbsolutePath().normalize());
			}
			s.close();
			
			//Check correct
			for(Path p : dirs) {
				assertTrue("InventorySystem directories contains a directory that is not in check " + p, dirs_c.contains(p));
			}
			for(Path p : dirs_c) {
				assertTrue("InventorySystem directories is missing a directory " + p, dirs.contains(p));
			}
			
		// Check leaf directories
			//Get leafs
			List<Path> leafs = is.getLeafDirectories();
			
			//Get list of known leafs
			List<Path> leafs_c = new LinkedList<Path>();
			s = new Scanner(new File("testdata/inventoriedSystem/leaflist"));
			while(s.hasNextLine()) {
				leafs_c.add(Paths.get(s.nextLine()).toAbsolutePath().normalize());
			}
			s.close();
			
			//Check correct
			for(Path p : leafs) {
				assertTrue("InventorySystem leafs contains a directory that is not in check " + p, leafs_c.contains(p));
			}
			for(Path p : leafs_c) {
				assertTrue("InventorySystem leafs is missing a directory " + p, leafs.contains(p));
			}
		
	//Check random no repeats
			List<Path> files_rc = new LinkedList<Path>(files);
			List<Path> dirs_rc = new LinkedList<Path>(dirs);
			List<Path> leafs_rc = new LinkedList<Path>(leafs);
			
			Path p;
			
		//Check random files no repeats

			while((p = is.getRandomFileNoRepeats()) != null) {
				assertTrue("getRandomFileNoRepeats returned a file not in the list, or repeated a file.",files_rc.remove(p));
			}
			while((p = is.getRandomFileNoRepeats()) != null) {
				assertTrue("getRandomFileNoRepeats returned a file not in the list, or repeated a file.",files_rc.remove(p));
			}
			assertTrue("getRandomFileNoRepeats returend null before it ran out of selections.",files_rc.size() == 0);
			
			//Check file random reset
			is.resetRandomFileRepeat();
			files_rc = new LinkedList<Path>(files);
			while((p = is.getRandomFileNoRepeats()) != null) {
				assertTrue("getRandomFileNoRepeats returned a file not in the list, or repeated a file [after resetRandomFileRepeat].",files_rc.remove(p));
			}
			assertTrue("getRandomFileNoRepeats returend null before it ran out of selections [after resetRandomFileRepeat].",files_rc.size() == 0);
			
			//Check file random reset
			is.resetRandomFileRepeat();
			files_rc = new LinkedList<Path>(files);
			while((p = is.getRandomFileNoRepeats()) != null) {
				assertTrue("getRandomFileNoRepeats returned a file not in the list, or repeated a file [after resetRandomFileRepeat].",files_rc.remove(p));
			}
			assertTrue("getRandomFileNoRepeats returend null before it ran out of selections [after resetRandomFileRepeat].",files_rc.size() == 0);
			

		//Check random directories no repeats
			while((p = is.getRandomDirectoryNoRepeats()) != null) {
				assertTrue("getRandomDirectory returned a directroy that is not in the list, or is a repeat.", dirs_rc.remove(p));
			}
			assertTrue("getRandomDirectory returned null before it ran out of selections.", dirs_rc.size() == 0);
			
			//check directory random reset
			is.resetRandomDirectoryRepeat();
			dirs_rc = new LinkedList<Path>(dirs);
			while((p = is.getRandomDirectoryNoRepeats()) != null) {
				assertTrue("getRandomDirectory returned a directroy that is not in the list, or is a repeat [after reset].", dirs_rc.remove(p));
			}
			assertTrue("getRandomDirectory returned null before it ran out of selections [after reset].", dirs_rc.size() == 0);
			
			//check directory random reset
			is.resetRandomDirectoryRepeat();
			dirs_rc = new LinkedList<Path>(dirs);

			is.resetRandomDirectoryRepeat();
			while((p = is.getRandomDirectoryNoRepeats()) != null) {
				assertTrue("getRandomDirectory returned a directroy that is not in the list, or is a repeat [after reset].", dirs_rc.remove(p));
			}
			assertTrue("getRandomDirectory returned null before it ran out of selections [after reset].", dirs_rc.size() == 0);
			
		//Check random leafs no repeats
			while((p = is.getRandomLeafDirectoryNoRepeats()) != null) {
				assertTrue("getRandomLeafDirectory returned a directory not in the list or repeated a directory.",leafs_rc.remove(p));
			}
			assertTrue("getRandomLeafDirectory returned null before it ran out of selections.", leafs_rc.size() == 0);
			
			//check directory random reset
			is.resetRandomLeafDirectoryRepeat();
			leafs_rc = new LinkedList<Path>(leafs);
			while((p = is.getRandomLeafDirectoryNoRepeats()) != null) {
				assertTrue("getRandomLeafDirectory returned a directory not in the list or repeated a directory [after reset].",leafs_rc.remove(p));
			}
			assertTrue("getRandomLeafDirectory returned null before it ran out of selections.", leafs_rc.size() == 0);
			
			//check directory random reset
			is.resetRandomLeafDirectoryRepeat();
			leafs_rc = new LinkedList<Path>(leafs);
			is.resetRandomLeafDirectoryRepeat();
			while((p = is.getRandomLeafDirectoryNoRepeats()) != null) {
				assertTrue("getRandomLeafDirectory returned a directory not in the list or repeated a directory [after reset].",leafs_rc.remove(p));
			}
			assertTrue("getRandomLeafDirectory returned null before it ran out of selections.", leafs_rc.size() == 0);
			
		// Check random selection (repeats...)
			//Files
			files_rc = new LinkedList<Path>(files);
			for(int i =0; i < 1000; i++) {
				assertTrue((p = is.getRandomFile()) != null);
				assertTrue("Select random files failed.", files_rc.contains(p));
			}
			
			//Directories
			dirs_rc = new LinkedList<Path>(dirs);
			for(int i = 0; i < 1000; i++) {
				assertTrue((p = is.getRandomDirectory()) != null);
				assertTrue("getRandomDirectory failed.", dirs_rc.contains(p));
			}
			
			//Leafs
			leafs_rc = new LinkedList<Path>(leafs);
			for(int i = 0; i < 1000; i++) {
				assertTrue((p = is.getRandomLeafDirectory()) != null);
				assertTrue("getRandomLeafDirectory failed.", leafs_rc.contains(p));
			}
			
		// Check num functions
			assertEquals("numFiles failed", files_c.size(), is.numFiles());
			assertEquals("numDirectories failed", dirs_c.size(), is.numDirectories());
			assertEquals("numLeafDirectories failed", leafs_c.size(), is.numLeafDirectories());
			
		// check location
			assertEquals("getLocation failed", Paths.get("testdata/inventoriedSystem/").toAbsolutePath().normalize(),is.getLocation());
			
		// check language
			assertEquals("getLanguage failed", "java", is.getLanguage());
			
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException occured");
		}
		
		boolean caught=false;
	//Check exception cases
		//null input
		caught=false;
		try {
			new InventoriedSystem(null, "java");
		} catch (NullPointerException e) {
			caught = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("NullPointerException failed to be thrown for null system location.",caught);
		
		caught=false;
		try {
			new InventoriedSystem(Paths.get("testdata/inventoriedSystem/"), null);
		} catch (NullPointerException e) {
			caught = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("NullPointerException failed to be thrown for null system language.",caught);
		
		caught=false;
		try {
			new InventoriedSystem(null, null);
		} catch (NullPointerException e) {
			caught = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("NullPointerException failed to be thrown for null system language and system location.",caught);
		
		//invalid path
		caught=false;
		try {
			new InventoriedSystem(Paths.get("testdata/inventoriedSystem/nosuchpath"), "java");
		} catch (IllegalArgumentException e) {
			caught = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("IllegalArgumentException failed to be thrown for non-existant system directory.",caught);
		
		//thatsafile
		caught=false;
		try {
			new InventoriedSystem(Paths.get("testdata/inventoriedSystem/filelist"), "java");
		} catch (IllegalArgumentException e) {
			caught = true;
		} catch (IOException e) {
			e.printStackTrace();
			fail("");
		}
		assertTrue("IllegalArgumentException failed to be thrown for file as system directory.",caught);

	}
}