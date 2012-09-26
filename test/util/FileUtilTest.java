package util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FileUtilTest {

	@Test
	public void testFileInventory() throws IOException {
		Path path = Paths.get("testdata/testInventory");
		List<Path> filelist = FileUtil.fileInventory(path);
		Collections.sort(filelist);
		
		//are the expected files there?
		assertTrue(filelist.size() == 5);
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/dir1/file").toAbsolutePath().normalize()));
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/dir2/dira/file").toAbsolutePath().normalize()));
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/dir2/dirb/file").toAbsolutePath().normalize()));
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/dir2/file").toAbsolutePath().normalize()));
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/file").toAbsolutePath().normalize()));
		
		//are the expected files missing?
		assertFalse(filelist.contains(Paths.get("/home/jeff/Development/java/workspace/ForkingSimulator/testdata/testInventory/badfile")));
		
		//check exceptions
		boolean exception_thrown = false;
		try {
			FileUtil.fileInventory(Paths.get("testdata/testInventory/dir1/file"));
		} catch (IllegalArgumentException e) {
			exception_thrown = true;
		}
		assertTrue(exception_thrown);
		
		exception_thrown = false;
		try {
			FileUtil.fileInventory(Paths.get("testdata/nosuchdirectory"));
		} catch (FileNotFoundException e) {
			exception_thrown = true;
		}
		assertTrue(exception_thrown);
		
	}

	@Test
	public void testDirectoryInventory() throws IOException {
		Path path = Paths.get("testdata/testInventory");
		List<Path> filelist = FileUtil.directoryInventory(path);
		Collections.sort(filelist);
		
		//are the expected files there?
		assertTrue(filelist.size() == 4);
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/dir1").toAbsolutePath().normalize()));
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/dir2").toAbsolutePath().normalize()));
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/dir2/dira").toAbsolutePath().normalize()));
		assertTrue(filelist.contains(Paths.get("testdata/testInventory/dir2/dirb").toAbsolutePath().normalize()));
		
		//are the expected files missing?
		assertFalse(filelist.contains(Paths.get("/home/jeff/Development/java/workspace/ForkingSimulator/testdata/testInventory/badfolder")));
		
		//check exceptions
		boolean exception_thrown = false;
		try {
			FileUtil.directoryInventory(Paths.get("testdata/testInventory/dir1/file"));
		} catch (IllegalArgumentException e) {
			exception_thrown = true;
		}
		assertTrue(exception_thrown);
		
		exception_thrown = false;
		try {
			FileUtil.directoryInventory(Paths.get("testdata/nosuchdirectory"));
		} catch (FileNotFoundException e) {
			exception_thrown = true;
		}
		assertTrue(exception_thrown);
	}

	@Test
	public void testIsLeafDirectory() throws FileNotFoundException, IOException {
		assertTrue(FileUtil.isLeafDirectory(Paths.get("testdata/testInventory/dir1")));
		assertFalse(FileUtil.isLeafDirectory(Paths.get("testdata/testInventory/dir2")));
		assertTrue(FileUtil.isLeafDirectory(Paths.get("testdata/testInventory/dir2/dira")));
		assertTrue(FileUtil.isLeafDirectory(Paths.get("testdata/testInventory/dir2/dirb")));
		
		//check exceptions
		boolean exception_thrown = false;
		try {
			FileUtil.isLeafDirectory(Paths.get("testdata/testInventory/dir1/file"));
		} catch (IllegalArgumentException e) {
			exception_thrown = true;
		}
		assertTrue(exception_thrown);
		
		exception_thrown = false;
		try {
			FileUtil.isLeafDirectory(Paths.get("testdata/nosuchdirectory"));
		} catch (FileNotFoundException e) {
			exception_thrown = true;
		}
		assertTrue(exception_thrown);
	}
	
	@Test
	public void testCopyDirectory() throws IOException {
		Files.createDirectories(Paths.get("testdata/deleteme"));
		Files.createFile(Paths.get("testdata/deleteme/file1"));
		Files.createFile(Paths.get("testdata/deleteme/file2"));
		Files.createFile(Paths.get("testdata/deleteme/file3"));
		Files.createFile(Paths.get("testdata/deleteme/file4"));
		Files.createDirectories(Paths.get("testdata/deleteme/dir1"));
		Files.createFile(Paths.get("testdata/deleteme/dir1/file1"));
		Files.createFile(Paths.get("testdata/deleteme/dir1/file2"));
		Files.createFile(Paths.get("testdata/deleteme/dir1/file3"));
		Files.createFile(Paths.get("testdata/deleteme/dir1/file4"));
		Files.createDirectories(Paths.get("testdata/deleteme/dir2"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/file1"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/file2"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/file3"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/file4"));
		Files.createDirectories(Paths.get("testdata/deleteme/dir2/dir21"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/dir21/file1"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/dir21/file2"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/dir21/file3"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/dir21/file4"));
		
		assertTrue(Files.exists(Paths.get("testdata/deleteme")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deleteme")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deleteme/dir1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deleteme/dir2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deleteme/dir2/dir21")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file4")));
		
		FileUtil.copyDirectory(Paths.get("testdata/deleteme"), Paths.get("testdata/deletemetoo"));
		
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deletemetoo")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir1")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deletemetoo/dir1")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir1/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir1/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir1/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir1/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deletemetoo/dir2")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/dir21")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deletemetoo/dir2/dir21")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/dir21/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/dir21/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/dir21/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deletemetoo/dir2/dir21/file4")));
		
		FileUtil.deleteDirectory(Paths.get("testdata/deleteme"));
		FileUtil.deleteDirectory(Paths.get("testdata/deletemetoo"));
	}
	
	@Test
	public void testDeleteDirectory() throws IOException {
		Files.createDirectories(Paths.get("testdata/deleteme"));
		Files.createFile(Paths.get("testdata/deleteme/file1"));
		Files.createFile(Paths.get("testdata/deleteme/file2"));
		Files.createFile(Paths.get("testdata/deleteme/file3"));
		Files.createFile(Paths.get("testdata/deleteme/file4"));
		Files.createDirectories(Paths.get("testdata/deleteme/dir1"));
		Files.createFile(Paths.get("testdata/deleteme/dir1/file1"));
		Files.createFile(Paths.get("testdata/deleteme/dir1/file2"));
		Files.createFile(Paths.get("testdata/deleteme/dir1/file3"));
		Files.createFile(Paths.get("testdata/deleteme/dir1/file4"));
		Files.createDirectories(Paths.get("testdata/deleteme/dir2"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/file1"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/file2"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/file3"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/file4"));
		Files.createDirectories(Paths.get("testdata/deleteme/dir2/dir21"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/dir21/file1"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/dir21/file2"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/dir21/file3"));
		Files.createFile(Paths.get("testdata/deleteme/dir2/dir21/file4"));
		
		assertTrue(Files.exists(Paths.get("testdata/deleteme")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deleteme")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deleteme/dir1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir1/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deleteme/dir2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/file4")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21")));
		assertTrue(Files.isDirectory(Paths.get("testdata/deleteme/dir2/dir21")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file1")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file2")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file3")));
		assertTrue(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file4")));
		
		FileUtil.deleteDirectory(Paths.get("testdata/deleteme"));
		
		assertFalse(Files.exists(Paths.get("testdata/deleteme")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/file1")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/file2")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/file3")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/file4")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir1")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir1/file1")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir1/file2")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir1/file3")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir1/file4")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/file1")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/file2")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/file3")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/file4")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/dir21")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file1")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file2")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file3")));
		assertFalse(Files.exists(Paths.get("testdata/deleteme/dir2/dir21/file4")));
	}
}
