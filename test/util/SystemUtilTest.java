package util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class SystemUtilTest {

	@Test
	public void testGetInstallRoot() {
		assertEquals(Paths.get("").toAbsolutePath().normalize(), SystemUtil.getInstallRoot());
	}

	@Test
	public void testGetScriptsLocation() {
		Path scripts = SystemUtil.getInstallRoot().resolve("scripts");
		assertEquals(scripts.toAbsolutePath().normalize(), SystemUtil.getScriptsLocation());
	}

	@Test
	public void testGetTemporaryDirectory() {
		Path tmp = SystemUtil.getInstallRoot().resolve("tmp");
		assertEquals(tmp.toAbsolutePath().normalize(), SystemUtil.getTemporaryDirectory());
	}

	@Test
	public void testGetTxlDirectory() {
		Path txldir = SystemUtil.getInstallRoot().resolve("txl");
		assertEquals(txldir.toAbsolutePath().normalize(), SystemUtil.getTxlDirectory().toAbsolutePath().normalize());
	}

	@Test
	public void testGetTxlDirectoryString() {
		Path txldirjava = SystemUtil.getInstallRoot().resolve("txl/java/");
		assertEquals(txldirjava.toAbsolutePath().normalize(), SystemUtil.getTxlDirectory("java").toAbsolutePath().normalize());
		
		Path txldirc = SystemUtil.getInstallRoot().resolve("txl/c/");
		assertEquals(txldirc.toAbsolutePath().normalize(), SystemUtil.getTxlDirectory("c").toAbsolutePath().normalize());
		
		Path txldircs = SystemUtil.getInstallRoot().resolve("txl/cs/");
		assertEquals(txldircs.toAbsolutePath().toAbsolutePath().normalize(), SystemUtil.getTxlDirectory("cs").toAbsolutePath().normalize());
		
		assertEquals(null, SystemUtil.getTxlDirectory("fortran"));
	}
	
	@Test
	public void testGetTxlExecutable() {
		assertEquals(Paths.get("/usr/local/bin/txl"), SystemUtil.getTxlExecutable());
	}

	@Test
	public void testRunTxl() throws IOException {
		Files.deleteIfExists(Paths.get("testdata/SystemUtilTest/InventoriedSystem.java_out"));
		int retval = SystemUtil.runTxl(SystemUtil.getTxlDirectory("java").resolve("PrettyPrint.txl"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java_out"));
		assertTrue(retval==0);
		
		boolean caught;
		
		caught = false;
		try {
			SystemUtil.runTxl(null, Paths.get("testdata/SystemUtilTest/InventoriedSystem.java"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java_out"));
		} catch (NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(SystemUtil.getTxlDirectory("java").resolve("PrettyPrint.txl"), null, Paths.get("testdata/SystemUtilTest/InventoriedSystem.java_out"));
		} catch (NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(SystemUtil.getTxlDirectory("java").resolve("PrettyPrint.txl"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java"), null);
		} catch (NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(null, null, Paths.get("testdata/SystemUtilTest/InventoriedSystem.java_out"));
		} catch (NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(null, Paths.get("testdata/SystemUtilTest/InventoriedSystem.java"), null);
		} catch (NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(SystemUtil.getTxlDirectory("java").resolve("PrettyPrint.txl"), null, null);
		} catch (NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(null, null, null);
		} catch (NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(SystemUtil.getTxlDirectory("java").resolve("PrettyPrint.txl2f2f23f"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java_out"));
		} catch (IllegalArgumentException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(SystemUtil.getTxlDirectory("java").resolve("PrettyPrint.txl"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java324324234"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java_out"));
		} catch (IllegalArgumentException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			SystemUtil.runTxl(SystemUtil.getTxlDirectory("java").resolve("PrettyPrint.txl"), Paths.get("testdata/SystemUtilTest/InventoriedSystem.java"), Paths.get("testdata/SystemUtilTest/unwriteable.txt"));
		} catch (IllegalArgumentException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testGetOperatorDirectory() {
		Path operators = SystemUtil.getInstallRoot().resolve("operators");
		assertEquals(operators, SystemUtil.getOperatorDirectory());
	}
	
	@Test
	public void testGetOperatorDirectoryString() {
		Path operatorsjava = SystemUtil.getInstallRoot().resolve("operators/java/");
		assertEquals(operatorsjava, SystemUtil.getOperatorDirectory("java"));
		
		Path operatorsc = SystemUtil.getInstallRoot().resolve("operators/c/");
		assertEquals(operatorsc, SystemUtil.getOperatorDirectory("c"));
		
		Path operatorscs = SystemUtil.getInstallRoot().resolve("operators/cs/");
		assertEquals(operatorscs, SystemUtil.getOperatorDirectory("cs"));
	}
	
}
