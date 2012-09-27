import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;


public class FileVariantTest {

	@Test
	public void testFileVariant() {
		FileVariant dv = new FileVariant(Paths.get("/original/"), Paths.get("/injected/"));
		assertEquals(dv.getType(), Variant.FILE);
		assertEquals(dv.getOriginalFile().toAbsolutePath().normalize().toString(), Paths.get("/original/").toAbsolutePath().normalize().toString());
		assertEquals(dv.getInjectedFile().toAbsolutePath().normalize().toString(), Paths.get("/injected/").toAbsolutePath().normalize().toString());
		
		dv = new FileVariant(Paths.get("/alt/original/"), Paths.get("/alter/injected/"));
		assertEquals(dv.getType(), Variant.FILE);
		assertEquals(dv.getOriginalFile().toAbsolutePath().normalize().toString(), Paths.get("/alt/original/").toAbsolutePath().normalize().toString());
		assertEquals(dv.getInjectedFile().toAbsolutePath().normalize().toString(), Paths.get("/alter/injected/").toAbsolutePath().normalize().toString());
		
		dv = new FileVariant(Paths.get("alt/original/"), Paths.get("alter/injected/"));
		assertEquals(dv.getType(), Variant.FILE);
		assertEquals(dv.getOriginalFile().toAbsolutePath().normalize().toString(), Paths.get("alt/original/").toAbsolutePath().normalize().toString());
		assertEquals(dv.getInjectedFile().toAbsolutePath().normalize().toString(), Paths.get("alter/injected/").toAbsolutePath().normalize().toString());
		
		boolean caught;
		
		caught = false;
		try {
			dv = new FileVariant(null, Paths.get("alter/injected/"));
		} catch(NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		caught = false;
		try {
			dv = new FileVariant(Paths.get("alt/original/"), null);
		} catch(NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testGetOriginalFile() {
		FileVariant dv = new FileVariant(Paths.get("/original/"), Paths.get("/injected/"));
		assertEquals(dv.getOriginalFile().toAbsolutePath().normalize().toString(), Paths.get("/original/").toAbsolutePath().normalize().toString());
		
		dv = new FileVariant(Paths.get("/alt/original/"), Paths.get("/alter/injected/"));
		assertEquals(dv.getOriginalFile().toAbsolutePath().normalize().toString(), Paths.get("/alt/original/").toAbsolutePath().normalize().toString());
		
		dv = new FileVariant(Paths.get("alt/original/"), Paths.get("alter/injected/"));
		assertEquals(dv.getOriginalFile().toAbsolutePath().normalize().toString(), Paths.get("alt/original/").toAbsolutePath().normalize().toString());
	}

	@Test
	public void testInjectedfile() {
		FileVariant dv = new FileVariant(Paths.get("/original/"), Paths.get("/injected/"));
		assertEquals(dv.getInjectedFile().toAbsolutePath().normalize().toString(), Paths.get("/injected/").toAbsolutePath().normalize().toString());
		
		dv = new FileVariant(Paths.get("/alt/original/"), Paths.get("/alter/injected/"));
		assertEquals(dv.getInjectedFile().toAbsolutePath().normalize().toString(), Paths.get("/alter/injected/").toAbsolutePath().normalize().toString());
		
		dv = new FileVariant(Paths.get("alt/original/"), Paths.get("alter/injected/"));
		assertEquals(dv.getInjectedFile().toAbsolutePath().normalize().toString(), Paths.get("alter/injected/").toAbsolutePath().normalize().toString());
	}

	@Test
	public void testToString() {
		FileVariant dv = new FileVariant(Paths.get("/original/"), Paths.get("/injected/"));
		assertEquals(dv.toString(), "FileVariant[original: /original, injected: /injected]");
		
		dv = new FileVariant(Paths.get("/alt/original/"), Paths.get("/alter/injected/"));
		assertEquals(dv.toString(), "FileVariant[original: /alt/original, injected: /alter/injected]");
		
		dv = new FileVariant(Paths.get("alt/original/"), Paths.get("alter/injected/"));
		assertEquals(dv.toString(), "FileVariant[original: /home/jeff/Development/java/workspace/ForkingSimulator/alt/original, injected: /home/jeff/Development/java/workspace/ForkingSimulator/alter/injected]");
	}

}
