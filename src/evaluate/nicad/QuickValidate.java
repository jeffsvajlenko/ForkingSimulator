package evaluate.nicad;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import util.FragmentUtil;
import util.SystemUtil;
import models.Fragment;

public class QuickValidate {
	public static double validate(Fragment f1, Fragment f2, String language) throws IOException {
		Path one = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "Validate_1", "");
		Path two = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "Validate_2", "");
		
		
		
		Path one_p = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "Validate_1p", "");;
		Path two_p = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "Validate_2p", "");;
		
		Path one_b = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "Validate_1b", "");;
		Path two_b = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "Validate_2b", "");;
		
		//Extract Fragments
		FragmentUtil.extractFragment(f1, one);
		FragmentUtil.extractFragment(f2, two);
		
		//Type1 Normalize
		SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), one, one_p);
		SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), two, two_p);
		
		//Type2 Normalize
		SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), one_p, one_b);
		SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), two_p, two_b);
		
		//Compare
		double sim = FragmentUtil.getSimilarity(one_b, two_b, true);
		
		Files.delete(one);
		Files.delete(one_p);
		Files.delete(one_b);
		Files.delete(two);
		Files.delete(two_p);
		Files.delete(two_b);
		
		
		return sim;
	}
}
