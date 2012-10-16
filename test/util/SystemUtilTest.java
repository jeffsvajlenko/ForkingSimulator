package util;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;

public class SystemUtilTest {

	@Test
	public void testGetInstallRoot() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetScriptsLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTemporaryDirectory() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTxlDirectory() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTxlDirectoryString() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetTxlExecutable() {
		assertEquals(Paths.get("/usr/local/bin/txl"), SystemUtil.getTxlExecutable());
	}

	@Test
	public void testRunTxl() {
		int retval = SystemUtil.runTxl(SystemUtil.getTxlDirectory("java").resolve("PrettyPrint.txl"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java_out"));
		assertTrue(retval==0);
	}

}
