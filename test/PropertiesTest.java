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
		assertEquals(10, p.getNumFiles());
		assertEquals(15, p.getNumDirectories());
		assertEquals(5, p.getNumFragments());
		assertEquals(2, p.getMaxinjectnum());
		assertEquals(11, p.getNumForks());
		assertEquals(25, p.getMutationRate());
		assertEquals(50, p.getInjectionReptitionRate());
		assertEquals(25, p.getMutationRate());
		assertEquals(10, p.getFunctionFragmentMinSize());
		assertEquals(100, p.getFunctionFragmentMaxSize());
		
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
		
		//injectionreptitionrate missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/injectionrepetitionratemissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'injectionrepetitionrate' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//mutationattempts missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/mutationattemptsmissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'mutationattempts' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//functionfragmentminsize missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/functionfragmentminsizemissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'functionfragmentminsize' was not specified."))
				caught = true;
		}
		assertTrue(caught);
		
		//functionfragmentmaxsize missing
		caught = false;
		try {
			p = new Properties(Paths.get("testdata/testproperties/functionfragmentmaxsizemissing"));
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("Property 'functionfragmentmaxsize' was not specified."))
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
}
