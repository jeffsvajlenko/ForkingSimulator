package util;

import static org.junit.Assert.*;

import org.junit.Test;

public class FilenameGeneratorTest {

	@Test
	public void testGetRandomFilename() {
		for(int i = 1; i < 100; i++) {
			for(int k = i+1; k < 200; k++) {
				String str = FilenameGenerator.getRandomFilename(i, k);
				//System.out.println(str);
				assertTrue("String wrong size...", str.length() >= i && str.length() <= k);
			}
		}
		
		for(int i = 0; i < 10; i++) {
			System.out.println(FilenameGenerator.getRandomFilename(5, 15));
		}
	}

}
