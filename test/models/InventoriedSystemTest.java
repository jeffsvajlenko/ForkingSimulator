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
			System.out.println(files_rc.size());
			int i =0;
			while((p = is.getRandomFileNoRepeats()) != null) {
				i++;
				assertTrue("getRandomFileNoRepeats returned a file not in the list, or repeated a file.",files_rc.remove(p));
			}
			System.out.println(i + " " + files_rc.size());
			assertTrue("getRandomFileNoRepeats returend null before it ran out of selections.",files_rc.size() == 0);
			
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
			
			is.resetRandomLeafDirectoryRepeat();
			while((p = is.getRandomLeafDirectoryNoRepeats()) != null) {
				assertTrue("getRandomLeafDirectory returned a directory not in the list or repeated a directory [after reset].",leafs_rc.remove(p));
			}
			assertTrue("getRandomLeafDirectory returned null before it ran out of selections.", leafs_rc.size() == 0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("");
		}
	}

	@Test
	public void testNumFiles() {
		fail("Not yet implemented");
	}

	@Test
	public void testNumDirectories() {
		fail("Not yet implemented");
	}

	@Test
	public void testNumLeafDirectories() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFiles() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDirectories() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLeafDirectories() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRandomFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRandomDirectory() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRandomLeafDirectory() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRandomFileNoRepeats() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetRandomFileRepeat() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRandomDirectoryNoRepeats() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetRandomDirectoryRepeat() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRandomLeafDirectoryNoRepeats() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetRandomLeafDirectoryRepeat() {
		fail("Not yet implemented");
	}

}
