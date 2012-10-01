import static org.junit.Assert.*;

import java.nio.file.Paths;

import main.Properties;

import org.junit.Test;


public class PropertiesTest {

	@Test
	public void testProperties() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(Paths.get("/path/to/system/").toAbsolutePath().normalize(), p.getSystem());
		assertEquals(Paths.get("/path/to/repository/").toAbsolutePath().normalize(), p.getRepository());
		assertEquals("java", p.getLanguage());
		assertEquals(10, p.getNumfiles());
		assertEquals(15, p.getNumdirectories());
		assertEquals(5, p.getNumfragments());
		assertEquals(2, p.getMaxinjectnum());
		assertEquals(11, p.getNumforks());
		assertEquals(0.25, p.getMutationrate(), 0.0001);
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(Paths.get("/path/to/folder/system/").toAbsolutePath().normalize(), p.getSystem());
		assertEquals(Paths.get("/path/to/other/repository/").toAbsolutePath().normalize(), p.getRepository());
		assertEquals("c", p.getLanguage());
		assertEquals(2, p.getNumfiles());
		assertEquals(4, p.getNumdirectories());
		assertEquals(6, p.getNumfragments());
		assertEquals(8, p.getMaxinjectnum());
		assertEquals(10, p.getNumforks());
		assertEquals(0.75, p.getMutationrate(), 0.0001);
		
		boolean caught;
		
		//language missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/languagemissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'language' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//maxinject mising
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/maxinjectnummissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'maxinjectnum' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//mutationrate misssing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/mutationratemissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'mutationrate' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//numdirectories missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/numdirectoriesmissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'numdirectories' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//numfiles missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/numfilesmissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'numfiles' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//numforks missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/numforksmissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'numforks' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//numfragments missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/numfragmentsmissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'numfragments' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//repository missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/repositorymissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'repository' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//system missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/systemmissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'system' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//null parameter
		caught = false;
		try {
			p = new Properties(null);
		} catch (NullPointerException e) {
			caught = true;
		}
		assertTrue(caught);
		
		//file does not exist
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/nosuchfile"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Propertiesfile must refer to an existing file."))
				caught = true;
		}
		assertTrue(caught);
		
		//not regular file
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Propertiesfile must refer to a regular file."))
				caught = true;
		}
		assertTrue(caught);
	}

	@Test
	public void testGetSystem() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(Paths.get("/path/to/system/").toAbsolutePath().normalize(), p.getSystem());
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(Paths.get("/path/to/folder/system/").toAbsolutePath().normalize(), p.getSystem());
	}

	@Test
	public void testGetRepository() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(Paths.get("/path/to/repository/").toAbsolutePath().normalize(), p.getRepository());
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(Paths.get("/path/to/other/repository/").toAbsolutePath().normalize(), p.getRepository());
	}

	@Test
	public void testGetLanguage() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals("java", p.getLanguage());
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals("c", p.getLanguage());
	}

	@Test
	public void testGetNumfiles() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(10, p.getNumfiles());
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(2, p.getNumfiles());
	}

	@Test
	public void testGetNumdirectories() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(15, p.getNumdirectories());
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(4, p.getNumdirectories());
	}

	@Test
	public void testGetNumfragments() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(5, p.getNumfragments());
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(6, p.getNumfragments());
	}

	@Test
	public void testGetMaxinjectnum() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(2, p.getMaxinjectnum());
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(8, p.getMaxinjectnum());
	}

	@Test
	public void testGetNumforks() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(11, p.getNumforks());
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(10, p.getNumforks());
	}

	@Test
	public void testGetMutationrate() {
		Properties p = new Properties(Paths.get("testdata/testproperties/properties"));
		assertEquals(0.25, p.getMutationrate(), 0.0001);
		
		p = new Properties(Paths.get("testdata/testproperties/properties2"));
		assertEquals(0.75, p.getMutationrate(), 0.0001);
	}

}
