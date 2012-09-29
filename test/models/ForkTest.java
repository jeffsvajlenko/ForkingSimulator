package models;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;

import util.FileUtil;

import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public class ForkTest {

	@Test
	public void testFork() {
		try {
			//Create
			Fork f = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
			
			//Test properly captured files
			List<Path> files = new LinkedList<Path>();
			Scanner s = new Scanner(new File("testdata/forktest/filelist"));
			while(s.hasNextLine()) {
				files.add(Paths.get(s.nextLine()).toAbsolutePath().normalize());
			}
			s.close();
			for(Path p : files) {
				if(!Files.exists(p)) {
					fail("A file is missing from the fork: " + p);
				}
			}
			
			//Test properly captured directories
			List<Path> dirs = new LinkedList<Path>();
			s = new Scanner(new File("testdata/forktest/directorylist"));
			while(s.hasNextLine()) {
				dirs.add(Paths.get(s.nextLine()).toAbsolutePath().normalize());
			}
			s.close();
			for(Path p : dirs) {
				if(!Files.exists(p)) {
					fail("A directory is missing from the fork: " + p);
				}
			}
			
			//Cleanup
			//FileUtil.deleteDirectory(Paths.get("testdata/testfork/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
// Test Exception Cases
		boolean caught;
		
	// NullPointerExceptions
		caught = false;
		try {
			new Fork(null, Paths.get("testdata/testfork/"), "java");
		} catch (NullPointerException e) {
			caught=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Failed to throw null pointer exception when sysdir is null.", caught);
		
		caught = false;
		try {
			new Fork(Paths.get("testdata/inventoriedSystem/"), null, "java");
		} catch (NullPointerException e) {
			caught=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Failed to throw null pointer exception when forkdir is null.", caught);
		
		caught = false;
		try {
			new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), null);
		} catch (NullPointerException e) {
			caught=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Failed to throw null pointer exception when language is null.", caught);
		
	//IllegalArgumentExceptions
		caught = false;
		try {
			new Fork(Paths.get("testdata/nosuchfolder/"), Paths.get("testdata/testfork/"), "java");
		} catch (IllegalArgumentException e) {
			caught=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Failed to throw illegal argument exception for sysdir being an invalid directory.", caught);
		
		caught = false;
		try {
			new Fork(Paths.get("testdata/inventoriedSystem/filelist"), Paths.get("testdata/testfork/"), "java");
		} catch (IllegalArgumentException e) {
			caught=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Failed to throw illegal argument exception for sysdir being a file.", caught);
		
		caught = false;
		try {
			new Fork(Paths.get("testdata/inventoriedSystem/filelist"), Paths.get("testdata/testInventory/"), "java");
		} catch (IllegalArgumentException e) {
			caught=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue("Failed to throw illegal argument exception for forkdir being an existing directory/file.", caught);
	}

	@Test
	public void testInjectFile() {
		try {
			//create fork
			Fork f = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
			
			//inventory the fork
			InventoriedSystem is = new InventoriedSystem(Paths.get("testdata/testfork/"), "java");
			
			//initial inventory
			List<Path> files = new LinkedList<Path>(is.getFiles());
			List<Path> dirs = new LinkedList<Path>(is.getDirectories());
			
			for(int i = 1; i <= 100; i++) {
				//inject the file
				FileVariant fv = f.injectFile(Paths.get("testdata/forktest/files/" + i));
				
				//Check return value
				assertTrue("FileVariant returned is null.", fv!=null);
				assertEquals("FileVariant does not refer to the file to be injected.", Paths.get("testdata/forktest/files/" + i).toAbsolutePath().normalize(), fv.getOriginalFile().toAbsolutePath().normalize());
				assertTrue("Injected file not injected in proper place.", fv.getInjectedFile().startsWith(Paths.get("testdata/testfork/").toAbsolutePath().normalize()));
				
				//Check file content
				Scanner s = new Scanner(fv.getInjectedFile().toFile());
				String line = s.nextLine();
				assertTrue("File was not injected properly (contents are wrong).", line.equals("" + i + "") && !s.hasNextLine());
				s.close();
				
				//Update correct inventory
				files.add(fv.getInjectedFile());
				
				//Inventory system
				List<Path> files_r = FileUtil.fileInventory(Paths.get("testdata/testfork/"));
				List<Path> dirs_r = FileUtil.directoryInventory(Paths.get("testdata/testfork/"));
				
				//Check fork's contents
				for(Path p : files_r) {
					assertTrue("A file exists in the fork that shouldn't be there..." + p, files.contains(p));
				}
				for(Path p : files) {
					assertTrue("A file is missing from the fork that should be there..." + p, files_r.contains(p));
				}
				for(Path p : dirs_r) {
					assertTrue("A directory exists in the fork that shouldn't be there..." + p, dirs.contains(p));
				}
				for(Path p : dirs) {
					assertTrue("A directory is missing from the fork that should be there..." + p, dirs_r.contains(p));
				}
			}
			//cleanup
			FileUtil.deleteDirectory(Paths.get("testdata/testfork/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInjectDirectory() {
		try {
			//create fork
			Fork f = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
			
			//inventory the fork
			InventoriedSystem is = new InventoriedSystem(Paths.get("testdata/testfork/"), "java");
			
			//initial inventory
			List<Path> files = new LinkedList<Path>(is.getFiles());
			List<Path> dirs = new LinkedList<Path>(is.getDirectories());
			
			for(int i = 1; i <= 100; i++) {
				//inject the file
				DirectoryVariant dv = f.injectDirectory(Paths.get("testdata/forktest/folders/" + i));
				
				//Check return value
				assertTrue("DirectoryVariant returned is null.", dv!=null);
				assertEquals("DirectoryVariant does not refer to the file to be injected.", Paths.get("testdata/forktest/folders/" + i).toAbsolutePath().normalize(), dv.getOriginalDirectory().toAbsolutePath().normalize());
				assertTrue("Injected directory not injected in proper place.", dv.getInjectedDirectory().startsWith(Paths.get("testdata/testfork/").toAbsolutePath().normalize()));
				
				//Check directory content
				DirectoryStream<Path> ds = Files.newDirectoryStream(dv.getInjectedDirectory());
				List<Path> contents = new LinkedList<Path>();
				for(Path p : ds) {
					contents.add(p.toAbsolutePath().normalize());
				}
				assert(contents.contains(dv.getInjectedDirectory().resolve(Paths.get("1").toAbsolutePath().normalize())));
				assert(contents.contains(dv.getInjectedDirectory().resolve(Paths.get("2").toAbsolutePath().normalize())));
				assert(contents.contains(dv.getInjectedDirectory().resolve(Paths.get("3").toAbsolutePath().normalize())));
				assert(contents.contains(dv.getInjectedDirectory().resolve(Paths.get("d1").toAbsolutePath().normalize())));
				assert(contents.contains(dv.getInjectedDirectory().resolve(Paths.get("d2").toAbsolutePath().normalize())));
				assert(contents.contains(dv.getInjectedDirectory().resolve(Paths.get("d3").toAbsolutePath().normalize())));
				
				//Update correct inventory
				dirs.add(dv.getInjectedDirectory());
				files.add(dv.getInjectedDirectory().resolve(Paths.get("1")).toAbsolutePath().normalize());
				files.add(dv.getInjectedDirectory().resolve(Paths.get("2")).toAbsolutePath().normalize());
				files.add(dv.getInjectedDirectory().resolve(Paths.get("3")).toAbsolutePath().normalize());
				dirs.add(dv.getInjectedDirectory().resolve(Paths.get("d1")).toAbsolutePath().normalize());
				dirs.add(dv.getInjectedDirectory().resolve(Paths.get("d2")).toAbsolutePath().normalize());
				dirs.add(dv.getInjectedDirectory().resolve(Paths.get("d3")).toAbsolutePath().normalize());
				
				//Inventory system
				List<Path> files_r = FileUtil.fileInventory(Paths.get("testdata/testfork/"));
				List<Path> dirs_r = FileUtil.directoryInventory(Paths.get("testdata/testfork/"));
				
				//Check fork's contents
				for(Path p : files_r) {
					assertTrue("A file exists in the fork that shouldn't be there..." + p, files.contains(p));
				}
				for(Path p : files) {
					assertTrue("A file is missing from the fork that should be there..." + p, files_r.contains(p));
				}
				for(Path p : dirs_r) {
					assertTrue("A directory exists in the fork that shouldn't be there..." + p, dirs.contains(p));
				}
				for(Path p : dirs) {
					assertTrue("A directory is missing from the fork that should be there..." + p, dirs_r.contains(p));
				}
			}
			//cleanup
			FileUtil.deleteDirectory(Paths.get("testdata/testfork/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInjectFragment() {
		//try {
		//	Fork f = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		fail("injectFragment not implemented.");
	}

	@After 
	public void method() {
		//Cleanup
		try {
			FileUtil.deleteDirectory(Paths.get("testdata/testfork/"));
		} catch (Exception e) {
		}
	}
	
}
