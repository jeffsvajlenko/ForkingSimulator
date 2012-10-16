package models;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import util.FileUtil;
import util.FragmentUtil;
import util.SystemUtil;

import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public class ForkTest {

	@Test
	public void testFork() {
		try {
			//Create
			new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
			
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
			
			for(int i = 1; i <= 1; i++) {
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
				
				//check variant lists
				assertEquals("", fv, f.getVariants().get(f.getVariants().size()-1));
				assertEquals("", fv, f.getFileVariants().get(f.getVariants().size()-1));
				assertTrue("", f.getVariants().size() == 1);
				assertTrue("", f.getFileVariants().size() == 1);
				assertTrue("", f.getDirectoryVariants().size() == 0);
			}
			//cleanup
			FileUtil.deleteDirectory(Paths.get("testdata/testfork/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInjectFile_many() {
		try {
			//create fork
			Fork f = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
			
			//inventory the fork
			InventoriedSystem is = new InventoriedSystem(Paths.get("testdata/testfork/"), "java");
			
			//initial inventory
			List<Path> files = new LinkedList<Path>(is.getFiles());
			List<Path> dirs = new LinkedList<Path>(is.getDirectories());
			
			//Variant tracking
			List<Variant> variants = new LinkedList<Variant>();
			List<FileVariant> filevariants = new LinkedList<FileVariant>();
			
			for(int i = 1; i <= 100; i++) {
				//inject the file
				FileVariant fv = f.injectFile(Paths.get("testdata/forktest/files/" + i));
				variants.add(fv);
				filevariants.add(fv);
				
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
				
				//check variants
				assertTrue(f.getVariants().get(f.getVariants().size()-1).equals(fv));
				assertTrue(f.getFileVariants().get(f.getFileVariants().size()-1).equals(fv));
				
			}
			
			//check variants
			assertTrue(f.getVariants().size() == 100);
			assertTrue(f.getFileVariants().size() == 100);
			assertTrue(f.getDirectoryVariants().size() == 0);
			for(int i = 0; i < 100; i++) {
				assertTrue(f.getVariants().get(i).equals(variants.get(i)));
				assertTrue(f.getFileVariants().get(i).equals(filevariants.get(i)));
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
			List<Variant> variants = new LinkedList<Variant>();
			List<DirectoryVariant> dirvariants = new LinkedList<DirectoryVariant>();
			
			for(int i = 1; i <= 1; i++) {
				//inject the file
				DirectoryVariant dv = f.injectDirectory(Paths.get("testdata/forktest/folders/" + i));
				variants.add(dv);
				dirvariants.add(dv);
				
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
				assertTrue(contents.contains(dv.getInjectedDirectory().resolve("1")));
				assertTrue(contents.contains(dv.getInjectedDirectory().resolve("2")));
				assertTrue(contents.contains(dv.getInjectedDirectory().resolve("3")));
				
				//Update correct inventory
				dirs.add(dv.getInjectedDirectory());
				files.add(dv.getInjectedDirectory().resolve("1"));
				files.add(dv.getInjectedDirectory().resolve("2"));
				files.add(dv.getInjectedDirectory().resolve("3"));
				
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
				
				//check variants
				assertTrue(f.getVariants().get(f.getVariants().size()-1).equals(dv));
				assertTrue(f.getDirectoryVariants().get(f.getDirectoryVariants().size()-1).equals(dv));
			}
			//cleanup
			FileUtil.deleteDirectory(Paths.get("testdata/testfork/"));
			
			//check variants
			assertTrue(f.getVariants().size()==1);
			assertTrue(f.getDirectoryVariants().size()==1);
			assertTrue(f.getFileVariants().size()==0);
			assertTrue(f.getVariants().get(0).equals(variants.get(0)));
			assertTrue(f.getDirectoryVariants().get(0).equals(dirvariants.get(0)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInjectDirectory_many() {
		try {
			//create fork
			Fork f = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
			
			//inventory the fork
			InventoriedSystem is = new InventoriedSystem(Paths.get("testdata/testfork/"), "java");
			
			//initial inventory
			List<Path> files = new LinkedList<Path>(is.getFiles());
			List<Path> dirs = new LinkedList<Path>(is.getDirectories());
			List<Variant> variants = new LinkedList<Variant>();
			List<DirectoryVariant> dirvariants = new LinkedList<DirectoryVariant>();
			
			for(int i = 1; i <= 100; i++) {
				//inject the file
				DirectoryVariant dv = f.injectDirectory(Paths.get("testdata/forktest/folders/" + i));
				variants.add(dv);
				dirvariants.add(dv);
				
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
				assertTrue(contents.contains(dv.getInjectedDirectory().resolve("1")));
				assertTrue(contents.contains(dv.getInjectedDirectory().resolve("2")));
				assertTrue(contents.contains(dv.getInjectedDirectory().resolve("3")));
				
				//Update correct inventory
				dirs.add(dv.getInjectedDirectory());
				files.add(dv.getInjectedDirectory().resolve("1"));
				files.add(dv.getInjectedDirectory().resolve("2"));
				files.add(dv.getInjectedDirectory().resolve("3"));
				
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
				
				//check variants
				assertTrue(f.getVariants().get(f.getVariants().size()-1).equals(dv));
				assertTrue(f.getDirectoryVariants().get(f.getDirectoryVariants().size()-1).equals(dv));
			}
			//cleanup
			FileUtil.deleteDirectory(Paths.get("testdata/testfork/"));
			
			//check variants
			assertTrue(f.getVariants().size()==100);
			assertTrue(f.getDirectoryVariants().size()==100);
			assertTrue(f.getFileVariants().size()==0);
			for(int i = 0; i < 100; i++) {
				assertTrue(f.getVariants().get(i).equals(variants.get(i)));
				assertTrue(f.getDirectoryVariants().get(i).equals(dirvariants.get(i)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInjectFunctionFragment() {
		try {
			Fork fork = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
			InventoriedSystem repository = new InventoriedSystem(Paths.get("testdata/JHotDraw54b1/"), "java");
			FunctionFragment f = repository.getRandomFunctionFragment();
			FragmentVariant fv = fork.injectFunctionFragment(f);
			
			Path tmp1 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "testInjectionFunctionFragment", null);
			Path tmp2 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "testInjectionFunctionFragment", null);
			
			FragmentUtil.extractFragment(fv.getOriginalFragment(), tmp1);
			FragmentUtil.extractFragment(fv.getInjectedFragment(), tmp2);
			
			assertTrue("Original and injected fragments do not much.", filesEqual(tmp1,tmp2));
			assertTrue(FragmentUtil.isFunction(tmp1, "java"));
			assertTrue(FragmentUtil.isFunction(tmp2, "java"));
			
			Files.deleteIfExists(tmp1);
			Files.deleteIfExists(tmp2);
			
			assertTrue(fork.getVariants().size() == 1);
			assertTrue(fork.getDirectoryVariants().size() == 0);
			assertTrue(fork.getFileVariants().size() == 0);
			assertTrue(fork.getFunctionFragmentVariants().size() == 1);
			assertTrue(fork.getVariants().get(0).equals(fv));
			assertTrue(fork.getFunctionFragmentVariants().get(0).equals(fv));
			
			InventoriedSystem check = new InventoriedSystem(Paths.get("testdata/testfork/"), "java");
			assertTrue(check.getFunctionFragments().contains(fv.getInjectedFragment()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInjectFunctionFragment_many() {
		try {
			Fork fork = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
			InventoriedSystem repository = new InventoriedSystem(Paths.get("testdata/JHotDraw54b1/"), "java");
			List<FragmentVariant> fv_c = new LinkedList<FragmentVariant>();
			List<Path> modifiedfile = new LinkedList<Path>();
			int i = 0;
			for(; i < 300; i++) {
				FunctionFragment f = repository.getRandomFunctionFragment();
				FragmentVariant fv = fork.injectFunctionFragment(f);
				if(fv == null) {
					break;
				}
				fv_c.add(fv);
				assertTrue("The same file was modified twice...", !modifiedfile.contains(fv.getInjectedFragment().getSrcFile().toAbsolutePath().normalize()));
				modifiedfile.add(fv.getInjectedFragment().getSrcFile().toAbsolutePath().normalize());
				
				Path tmp1 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "testInjectionFunctionFragment", null);
				Path tmp2 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "testInjectionFunctionFragment", null);
				
				FragmentUtil.extractFragment(fv.getOriginalFragment(), tmp1);
				FragmentUtil.extractFragment(fv.getInjectedFragment(), tmp2);
				
				assertTrue("Original and injected fragments do not much.", filesEqual(tmp1,tmp2));
				assertTrue(FragmentUtil.isFunction(tmp1, "java"));
				assertTrue(FragmentUtil.isFunction(tmp2, "java"));
				
				Files.deleteIfExists(tmp1);
				Files.deleteIfExists(tmp2);
				
				assertTrue(fork.getVariants().size() == i+1);
				assertTrue(fork.getDirectoryVariants().size() == 0);
				assertTrue(fork.getFileVariants().size() == 0);
				assertTrue(fork.getFunctionFragmentVariants().size() == i+1);
				assertTrue(fork.getVariants().get(i).equals(fv));
				assertTrue(fork.getFunctionFragmentVariants().get(i).equals(fv));
			}
			
			//Check variant info
			assertTrue(fork.getVariants().size() == i);
			assertTrue(fork.getFunctionFragmentVariants().size() == i);
			assertTrue(fork.getFileVariants().size() == 0);
			assertTrue(fork.getDirectoryVariants().size() == 0);
			for(int j = 0; j < i; j++) {
				fork.getVariants().get(j).equals(fv_c.get(j));
			}
			for(int j = 0; j < i; j++) {
				fork.getFunctionFragmentVariants().get(j).equals(fv_c.get(j));
			}
			
			InventoriedSystem check = new InventoriedSystem(Paths.get("testdata/testfork/"), "java");
			for(int j = 0; j < fv_c.size(); j++) {
				assertTrue(check.getFunctionFragments().contains(fv_c.get(j).getInjectedFragment()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInjectionsMixed() throws IOException {
		Fork fork = new Fork(Paths.get("testdata/inventoriedSystem/"), Paths.get("testdata/testfork/"), "java");
		InventoriedSystem repository = new InventoriedSystem(Paths.get("testdata/JHotDraw54b1/"), "java");
		
		List<Variant> v_c = new LinkedList<Variant>();
		List<DirectoryVariant> dirv_c = new LinkedList<DirectoryVariant>();
		List<FileVariant> filev_c = new LinkedList<FileVariant>();
		List<FragmentVariant> fragv_c = new LinkedList<FragmentVariant>();
		
		List<Path> modfile = new LinkedList<Path>();
		//Perform injections (check correctness of injectiosn during as well, partilaly)
		for(int n = 0; n < 55; n++) {
			//File
			Path f = repository.getRandomFileNoRepeats();
			FileVariant fv = fork.injectFile(f);
			assertTrue("Ran out of files injections before end of test...",fv!=null);
			v_c.add(fv);
			filev_c.add(fv);
			assertTrue("Injected and original file do not match.", filesEqual(fv.getOriginalFile(), fv.getInjectedFile()));
			
			//Directory
			Path d = repository.getRandomLeafDirectoryNoRepeats();
			DirectoryVariant dv = fork.injectDirectory(d);
			assertTrue("Ran out of directory injects before end of test...",dv!=null);
			v_c.add(dv);
			dirv_c.add(dv);
			assertTrue("Injected and original directory do not match.", leafDirectoryEqual(dv.getOriginalDirectory(),dv.getInjectedDirectory()));
			
			//Fragment
			FunctionFragment ff = repository.getRandomFunctionFragmentNoRepeats();
			FragmentVariant ffv = fork.injectFunctionFragment(ff);
			assertTrue("Ran out of function fragment injects before end of test...",ffv!=null);
			v_c.add(ffv);
			fragv_c.add(ffv);
			assertTrue("Function injection modified the same file twice.", !modfile.contains(ffv.getInjectedFragment().getSrcFile().toAbsolutePath().normalize()));
			modfile.add(ffv.getInjectedFragment().getSrcFile().toAbsolutePath().normalize());
			assertTrue("Injected and original function fragments do not match.", fragEqual(ffv.getOriginalFragment(), ffv.getInjectedFragment()));
		}
		
		//check variant lists
		assertTrue(fork.getVariants().size() == 165);
		assertTrue(fork.getFileVariants().size() == 55);
		assertTrue(fork.getDirectoryVariants().size() == 55);
		assertTrue(fork.getFunctionFragmentVariants().size() == 55);
		for(int i = 0; i < fork.getVariants().size(); i++) {
			assertTrue("Error in fork's variant list.", fork.getVariants().get(i).equals(v_c.get(i)));
		}
		for(int i = 0; i < fork.getFileVariants().size(); i++) {
			assertTrue("Error in fork's file variant list.", fork.getFileVariants().get(i).equals(filev_c.get(i)));
		}
		for(int i = 0; i < fork.getDirectoryVariants().size(); i++) {
			assertTrue("Error in fork's dir variant list.", fork.getDirectoryVariants().get(i).equals(dirv_c.get(i)));
		}
		for(int i = 0; i < fork.getFunctionFragmentVariants().size(); i++) {
			assertTrue("Error in fork's function fragment variant list.", fork.getFunctionFragmentVariants().get(i).equals(fragv_c.get(i)));
		}
		
		//Check system contents
		InventoriedSystem check = new InventoriedSystem(Paths.get("testdata/testfork/"), "java");
		for(FileVariant fv : filev_c) {
			assertTrue("Injected file missing from fork.", check.getFiles().contains(fv.getInjectedFile()));
			assertTrue("Injected and original files do not match", filesEqual(fv.getOriginalFile(), fv.getInjectedFile()));
		}
		for(DirectoryVariant dv : dirv_c) {
			assertTrue("Injected directory missing from fork.", check.getDirectories().contains(dv.getInjectedDirectory()));
			assertTrue("Injected directory is not a leaf directory?", FileUtil.isLeafDirectory(dv.getInjectedDirectory()));
			assertTrue("Injected and original directories do not match", leafDirectoryEqual(dv.getOriginalDirectory(), dv.getInjectedDirectory()));
		}
		for(FragmentVariant fv : fragv_c) {
			assertTrue("Injected function fragment missing form fork.", check.getFunctionFragments().contains(fv.getInjectedFragment()));
			assertTrue("Injected and original function fragmetns do not match.", fragEqual(fv.getOriginalFragment(), fv.getInjectedFragment()));
		}
	}
	
	@Before
	public void method() {
		//Cleanup
		try {
			FileUtil.deleteDirectory(Paths.get("testdata/testfork/"));
		} catch (Exception e) {
		}
	}
	
	private boolean fragEqual(Fragment f1, Fragment f2) throws IOException {
		Path frag1 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "testInjectiosMixed", null);
		Path frag2 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "testInjectiosMixed", null);
		FragmentUtil.extractFragment(f1, frag1);
		FragmentUtil.extractFragment(f2, frag2);
		boolean retval = filesEqual(frag1,frag2);
		Files.delete(frag1);
		Files.delete(frag2);
		return retval;
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
	
	private boolean leafDirectoryEqual(Path d1, Path d2) throws IOException {
		DirectoryStream<Path> ds1, ds2;
		List<Path> dc1 = new LinkedList<Path>();
		List<Path> dc2 = new LinkedList<Path>();
		ds1 = Files.newDirectoryStream(d1);
		ds2 = Files.newDirectoryStream(d2);
		for(Path p : ds1) {
			dc1.add(p.toAbsolutePath().normalize());
		}
		for(Path p : ds2) {
			dc2.add(p.toAbsolutePath().normalize());
		}
		
		for(Path p : dc1) {
			if(!dc2.contains(Paths.get(p.toString().replaceFirst(d1.toString(), d2.toString())))) {
				return false;
			}
		}
		for(Path p : dc2) {
			if(!dc1.contains(Paths.get(p.toString().replaceFirst(d2.toString(), d1.toString())))) {
				return false;
			}
		}
		return true;
	}
	
}
