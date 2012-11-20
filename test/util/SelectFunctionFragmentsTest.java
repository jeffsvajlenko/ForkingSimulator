package util;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import models.FunctionFragment;

import org.junit.Test;

import util.SelectFunctionFragments;

public class SelectFunctionFragmentsTest {

	@Test
	public void testGetFunctionFragments() throws IOException {
		//Java
		List<FunctionFragment> ff = SelectFunctionFragments.getFragments(Paths.get("testdata/SelectFragmentsTest/JHotDraw54b1/").toFile(), "java");
		Path checkfunc = Paths.get("testdata/SelectFragmentsTest/jfunctions");
		BufferedReader br = new BufferedReader(new FileReader(checkfunc.toFile()));
		
		List<FunctionFragment> checkff = new LinkedList<FunctionFragment>();
		String line = br.readLine();
		while(line != null) {
			
			Scanner linescanner = new Scanner(line);
			String srcfile = linescanner.next();
			int startline = linescanner.nextInt();
			int endline = linescanner.nextInt();
			checkff.add(new FunctionFragment(Paths.get(srcfile).toAbsolutePath().normalize(), startline, endline));
			linescanner.close();
			
			line = br.readLine();
		}
		br.close();
	
		assertTrue(ff.size() == 2886);
		assertTrue(checkff.size() == 2886);
		
		for(FunctionFragment f : ff) {
			assertTrue(checkff.contains(f));
		}
		for(FunctionFragment f : checkff) {
			assertTrue(ff.contains(f));
		}
		
		//C
		ff = SelectFunctionFragments.getFragments(Paths.get("testdata/SelectFragmentsTest/monit-4.2/").toFile(), "c");
		checkfunc = Paths.get("testdata/SelectFragmentsTest/cfunctions");
		br = new BufferedReader(new FileReader(checkfunc.toFile()));
		
		checkff = new LinkedList<FunctionFragment>();
		line = br.readLine();
		while(line != null) {
			
			Scanner linescanner = new Scanner(line);
			String srcfile = linescanner.next();
			int startline = linescanner.nextInt();
			int endline = linescanner.nextInt();
			checkff.add(new FunctionFragment(Paths.get(srcfile).toAbsolutePath().normalize(), startline, endline));
			linescanner.close();
			
			line = br.readLine();
		}
		br.close();
	
		assertTrue(ff.size() == 437);
		assertTrue(checkff.size() == 437);
		
		for(FunctionFragment f : ff) {
			assertTrue(checkff.contains(f));
		}
		for(FunctionFragment f : checkff) {
			assertTrue(ff.contains(f));
		}
		
		//C#
		ff = SelectFunctionFragments.getFragments(Paths.get("testdata/SelectFragmentsTest/greenshot/").toFile(), "cs");
		checkfunc = Paths.get("testdata/SelectFragmentsTest/csfunctions");
		br = new BufferedReader(new FileReader(checkfunc.toFile()));
		
		checkff = new LinkedList<FunctionFragment>();
		line = br.readLine();
		while(line != null) {
			
			Scanner linescanner = new Scanner(line);
			String srcfile = linescanner.next();
			int startline = linescanner.nextInt();
			int endline = linescanner.nextInt();
			checkff.add(new FunctionFragment(Paths.get(srcfile).toAbsolutePath().normalize(), startline, endline));
			linescanner.close();
			
			line = br.readLine();
		}
		br.close();
	
		assertTrue(ff.size() == 310);
		assertTrue(checkff.size() == 310);
		
		for(FunctionFragment f : ff) {
			assertTrue(checkff.contains(f));
		}
		for(FunctionFragment f : checkff) {
			assertTrue(ff.contains(f));
		}
	}
}
