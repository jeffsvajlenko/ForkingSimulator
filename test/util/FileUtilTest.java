package util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
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
}
