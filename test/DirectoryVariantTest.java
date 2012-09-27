import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;


public class DirectoryVariantTest {

	@Test
	public void testDirectoryVariant() {
		DirectoryVariant dv = new DirectoryVariant(Paths.get("/original/"), Paths.get("/injected/"));
		assertEquals(dv.getType(), Variant.DIRECTORY);
		assertEquals(dv.getOriginalDirectory().toAbsolutePath().normalize().toString(), Paths.get("/original/").toAbsolutePath().normalize().toString());
		assertEquals(dv.getInjectedDirectory().toAbsolutePath().normalize().toString(), Paths.get("/injected/").toAbsolutePath().normalize().toString());
		
		dv = new DirectoryVariant(Paths.get("/alt/original/"), Paths.get("/alter/injected/"));
		assertEquals(dv.getType(), Variant.DIRECTORY);
		assertEquals(dv.getOriginalDirectory().toAbsolutePath().normalize().toString(), Paths.get("/alt/original/").toAbsolutePath().normalize().toString());
		assertEquals(dv.getInjectedDirectory().toAbsolutePath().normalize().toString(), Paths.get("/alter/injected/").toAbsolutePath().normalize().toString());
		
		dv = new DirectoryVariant(Paths.get("alt/original/"), Paths.get("alter/injected/"));
		assertEquals(dv.getType(), Variant.DIRECTORY);
		assertEquals(dv.getOriginalDirectory().toAbsolutePath().normalize().toString(), Paths.get("alt/original/").toAbsolutePath().normalize().toString());
		assertEquals(dv.getInjectedDirectory().toAbsolutePath().normalize().toString(), Paths.get("alter/injected/").toAbsolutePath().normalize().toString());
		
		boolean caught;
		
		caught = false;
		try {
			dv = new DirectoryVariant(null, Paths.get("alter/injected/"));
		} catch(NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			dv = new DirectoryVariant(Paths.get("alt/original/"), null);
		} catch(NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testGetOriginalDirectory() {
		DirectoryVariant dv = new DirectoryVariant(Paths.get("/original/"), Paths.get("/injected/"));
		assertEquals(dv.getOriginalDirectory().toAbsolutePath().normalize().toString(), Paths.get("/original/").toAbsolutePath().normalize().toString());
		
		dv = new DirectoryVariant(Paths.get("/alt/original/"), Paths.get("/alter/injected/"));
		assertEquals(dv.getOriginalDirectory().toAbsolutePath().normalize().toString(), Paths.get("/alt/original/").toAbsolutePath().normalize().toString());
		
		dv = new DirectoryVariant(Paths.get("alt/original/"), Paths.get("alter/injected/"));
		assertEquals(dv.getOriginalDirectory().toAbsolutePath().normalize().toString(), Paths.get("alt/original/").toAbsolutePath().normalize().toString());
	}

	@Test
	public void testGetInjectedDirectory() {
		DirectoryVariant dv = new DirectoryVariant(Paths.get("/original/"), Paths.get("/injected/"));
		assertEquals(dv.getInjectedDirectory().toAbsolutePath().normalize().toString(), Paths.get("/injected/").toAbsolutePath().normalize().toString());
		
		dv = new DirectoryVariant(Paths.get("/alt/original/"), Paths.get("/alter/injected/"));
		assertEquals(dv.getInjectedDirectory().toAbsolutePath().normalize().toString(), Paths.get("/alter/injected/").toAbsolutePath().normalize().toString());
		
		dv = new DirectoryVariant(Paths.get("alt/original/"), Paths.get("alter/injected/"));
		assertEquals(dv.getInjectedDirectory().toAbsolutePath().normalize().toString(), Paths.get("alter/injected/").toAbsolutePath().normalize().toString());
	}
	
	@Test
	public void testToString() {
		DirectoryVariant dv = new DirectoryVariant(Paths.get("/original/"), Paths.get("/injected/"));
		assertEquals(dv.toString(), "DirectoryVariant[original: /original, injected: /injected]");
		
		dv = new DirectoryVariant(Paths.get("/alt/original/"), Paths.get("/alter/injected/"));
		assertEquals(dv.toString(), "DirectoryVariant[original: /alt/original, injected: /alter/injected]");
		
		dv = new DirectoryVariant(Paths.get("alt/original/"), Paths.get("alter/injected/"));
		assertEquals(dv.toString(), "DirectoryVariant[original: /home/jeff/Development/java/workspace/ForkingSimulator/alt/original, injected: /home/jeff/Development/java/workspace/ForkingSimulator/alter/injected]");
	}

}
