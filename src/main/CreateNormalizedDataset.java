package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import models.Fragment;
import models.FunctionInjection;
import models.FunctionInjectionInstance;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import util.SelectFunctionFragments;
import util.SystemUtil;

public class CreateNormalizedDataset {
	public static void main(String args[]) throws IOException {
		FileUtils.deleteDirectory(new File(args[3]));
		CreateNormalizedDataset.create(new ForkSimDataset(Paths.get(args[0]).toAbsolutePath().normalize(), Paths.get(args[1]).toAbsolutePath().normalize(), Paths.get(args[2]).toAbsolutePath().normalize()),
				Paths.get(args[3]).toAbsolutePath().normalize(), true, true, true, true, false);
	}
	
	public static void create(ForkSimDataset dataset, Path output, boolean prettyprint, boolean normalizeIdentifiers, boolean normalizeLiterals, boolean normalizePrimitives, boolean normalizeComments) throws IOException {
		Objects.requireNonNull(dataset);
		Objects.requireNonNull(output);
		output = output.toAbsolutePath().normalize();
		if(Files.exists(output)) {
			throw new IllegalArgumentException("Output directory must not already exist.");
		}
		
		Properties properties = dataset.getProperties();
		
		//Initialize New Output
		Files.createDirectories(output);
		FileUtils.copyDirectoryToDirectory(properties.getRepository().toFile(), output.toFile());
		FileUtils.copyDirectoryToDirectory(properties.getSystem().toFile(), output.toFile());
		for(int i = 0; i < properties.getNumForks(); i++) {
			FileUtils.copyDirectory(dataset.getExperimentPath().resolve("" + i).toFile(), output.resolve(i + "").toFile());
		}
		FileUtils.copyDirectoryToDirectory(dataset.getExperimentPath().resolve("files").toFile(), output.toFile());
		FileUtils.copyDirectoryToDirectory(dataset.getExperimentPath().resolve("dirs").toFile(), output.toFile());
		FileUtils.copyDirectoryToDirectory(dataset.getExperimentPath().resolve("function_fragments").toFile(), output.toFile());
		
		//Execute Pretty Print
		if(prettyprint) {
			executeTxlOnAll(output, dataset.getProperties().getNumForks(), SystemUtil.getTxlDirectory(dataset.getProperties().getLanguage()).resolve("PrettyPrint.txl"));
		}
		
		//Execute normalize identifiers
		if(normalizeIdentifiers) {
			executeTxlOnAll(output, dataset.getProperties().getNumForks(), SystemUtil.getTxlDirectory(dataset.getProperties().getLanguage()).resolve("NormalizeIdentifiers.txl"));
		}
		
		//Execute normalize literals
		if(normalizeLiterals) {
			executeTxlOnAll(output, dataset.getProperties().getNumForks(), SystemUtil.getTxlDirectory(dataset.getProperties().getLanguage()).resolve("NormalizeLiterals.txl"));
		}
		
		//Execute normalize primitives
		if(normalizePrimitives) {
			executeTxlOnAll(output, dataset.getProperties().getNumForks(), SystemUtil.getTxlDirectory(dataset.getProperties().getLanguage()).resolve("NormalizePrimitives.txl"));
		}
		
		//Execute remove comments (it not also prettyprint)
		if(!prettyprint && normalizeComments) {
			executeTxlOnAll(output, dataset.getProperties().getNumForks(), SystemUtil.getTxlDirectory(dataset.getProperties().getLanguage()).resolve("removecomments.txl"));
		}
		
	//Fix Report
		Path log = Files.createFile(output.resolve("log"));
		PrintWriter out = new PrintWriter(new FileWriter(log.toFile()));
		
		//File
		for(int i = 0; i < dataset.numFileInjections(); i++) {
			out.println(dataset.getFileInjection(i).getNormalized(dataset.getExperimentPath()));
		}
		
		//Directory
		for(int i = 0; i < dataset.numDirectoryInjections(); i++) {
			out.println(dataset.getDirectoryInjection(i).getNormalized(dataset.getExperimentPath()));
		}
		
		//Function (Corrected)
		for(int i = 0; i < dataset.numFunctionInjections(); i++) {
			out.println(CreateNormalizedDataset.updateFunctionInjection(dataset.getFunctionInjection(i), dataset.getExperimentPath(), output, properties.getLanguage()).getNormalize(output.toAbsolutePath().normalize()));
		}
		
		out.flush();
		out.close();
	}
	
	private static FunctionInjection updateFunctionInjection(FunctionInjection functionInjection, Path original, Path output, String language) throws IOException {
		List<FunctionInjectionInstance> newInstances = new LinkedList<FunctionInjectionInstance>();
		for(FunctionInjectionInstance fii : functionInjection.getInstances()) {
			int num = 0;
			List<Fragment> fragments = SelectFunctionFragments.getFunctionFragmentsInFile(fii.getInjected().getSrcFile(), language);
			for(Fragment target : fragments) {
				//fuzzy match first line because formatting modification may have added or removed a line before the start of the function!
				if((target.getStartLine() == fii.getInjected().getStartLine() || target.getStartLine() == fii.getInjected().getStartLine() + 1 || target.getStartLine() == fii.getInjected().getStartLine() - 1) && target.getEndLine() == fii.getInjected().getEndLine()) {
					break;
				}
				num++;
			}
			List<Fragment> newfragments = SelectFunctionFragments.getFunctionFragmentsInFile(output.resolve(original.relativize(fii.getInjected().getSrcFile())), language);
			//System.out.println("--------------------------------------------------------------------------------");
			//System.out.println(functionInjection);
			//System.out.println(fii);
			//System.out.println(newfragments);
			Fragment newfragment = newfragments.get(num);
			newInstances.add(new FunctionInjectionInstance(fii.getForknum(), fii.isMutated(), fii.getOperator(), fii.getTimes(), fii.getType(), newfragment, fii.getLog()));
		}
		Fragment neworiginal = new Fragment(output.resolve(original.relativize(functionInjection.getOriginal().getSrcFile())), functionInjection.getOriginal().getStartLine(), functionInjection.getOriginal().getEndLine());
		return new FunctionInjection(functionInjection.getNum(), functionInjection.isUniform(), neworiginal, functionInjection.getLog(), newInstances);
	}

	public static void executeTxlOnAll(Path output, int numforks, Path script) {
		List<File> files = new LinkedList<File>();
		for(int i = 0; i < numforks; i++) {
			files.addAll(FileUtils.listFiles(output.resolve(i + "").toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
		}
		for(File file : files) {
			if(SystemUtil.runTxl(script, file.toPath(), file.toPath()) != 0) {
			}
		}
	}
}
