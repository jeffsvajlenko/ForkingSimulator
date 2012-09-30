package models;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import util.SelectFunctionFragments;

public class SelectFunctionFragmentsTest {

	@Test
	public void testGetFragments() {
		List<FunctionFragment> ff = SelectFunctionFragments.getFragments(Paths.get("testdata/JHotDraw54b1/").toFile(), "java");
		//for(Fragment f : ff) {
			//System.out.println(f.getSrcFile() + " " + f.getStartLine() + "-" + f.getEndLine());
		//}
		//System.out.println(ff.size());
		assertTrue(ff.size() == 2886);
	}
}
