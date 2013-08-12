package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import models.DirectoryFileInjectionInstance;
import models.DirectoryInjection;
import models.DirectoryInjectionInstance;
import models.FileInjection;
import models.FileInjectionInstance;
import models.FunctionInjection;
import models.FunctionInjectionInstance;

import org.apache.commons.io.FileUtils;

import util.FileUtil;
import util.FragmentUtil;

public class CreateUnMutatedDataset {
	
	public static void main(String args[]) throws IOException {
		FileUtils.deleteDirectory(Paths.get(args[3]).toFile());
		CreateUnMutatedDataset.create(new ForkSimDataset(Paths.get(args[0]), Paths.get(args[1]), Paths.get(args[2])), Paths.get(args[3]));
	}
	
	public static void create(ForkSimDataset dataset, Path output) throws IOException {
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
			FileUtils.copyDirectory(properties.getSystem().toFile(), output.resolve(i + "").toFile());
		}
		Path filsLog = Files.createDirectories(output.resolve("files"));
		Path dirsLog = Files.createDirectories(output.resolve("dirs"));
		Path funcsLog = Files.createDirectories(output.resolve("function_fragments"));
		
		PrintWriter log = new PrintWriter(output.resolve("log").toFile());
		
		properties.setDirMutationRate(0);
		properties.setFileMutationRate(0);
		properties.setFragmentMutationRate(0);
		properties.setDirRenameRate(0);
		properties.setFileRenameRate(0);
		properties.write(output.resolve("properties"));
		
		// Output Properties
//		log.println("BEGIN: Properties");
//		log.println("\t" + "output_directory=" + output.toAbsolutePath().normalize().toString());
//		log.println("\t" + "system_directory=" + output.resolve("originalSystem").toString());
//		log.println("\t" + "repository_directory=" + output.resolve("sourceRepository").toString());
//		log.println("\t" + "language=" + properties.getLanguage());
//		log.println("\t" + "numforks=" + properties.getNumForks());
//		log.println("\t" + "numfiles=" + properties.getNumFiles());
//		log.println("\t" + "numdirs=" + properties.getNumDirectories());
//		log.println("\t" + "numfragments=" + properties.getNumFragments());
//		log.println("\t" + "functionfragmentminsize=" + properties.getFunctionFragmentMinSize());
//		log.println("\t" + "functionfragmentmaxsize=" + properties.getFunctionFragmentMaxSize());
//		log.println("\t" + "maxinjectnum=" + properties.getMaxinjectNum());
//		log.println("\t" + "injectionrepititionrate=" + properties.getInjectionReptitionRate());
//		log.println("\t" + "fragmentmutationrate=" + properties.getFragmentMutationRate());
//		log.println("\t" + "filemutationrate=" + properties.getFileMutationRate());
//		log.println("\t" + "dirmutationrate=" + properties.getDirMutationRate());
//		log.println("\t" + "filerenamerate=" + properties.getFileRenameRate());
//		log.println("\t" + "dirrenamerate=" + properties.getDirRenameRate());
//		log.println("\t" + "maxfileedits=" + properties.getMaxFileEdit());
//		log.println("\t" + "maxfunctionedits=" + properties.getMaxFunctionEdit());
//		log.println("\t" + "mutationattempts=" + properties.getMutationAttempts());
//		log.println("END: Properties");
		
		
		//Replicate File Injections
		log.println("BEGIN: FileVariants");
		for(int i = 0; i < dataset.numFileInjections(); i++) {
		//Get
			FileInjection fi = dataset.getFileInjection(i);
			
			//Log Original
			Files.createDirectories(filsLog.resolve(fi.getNum() + ""));
			Files.copy(fi.getlog(), filsLog.resolve(fi.getNum() + "").resolve("original"));
			String uniformFlag = fi.isUniform() ? "U" : "V";
			log.println("" + fi.getNum() + " " + uniformFlag + " " + fi.getInstances().size() + " " + (dataset.getExperimentPath().relativize(fi.getoriginal())));
			
		//Inject Without Mutations/Renames
			for(FileInjectionInstance fii : fi.getInstances()) {
				//Inject
				Path injected = Files.copy(fi.getlog(), output.resolve(dataset.getExperimentPath().relativize(fii.getInjected().getParent())).resolve(fi.getoriginal().getFileName())).toAbsolutePath().normalize();
				
				//Log Injection
				Files.copy(injected, filsLog.resolve(fi.getNum() + "").resolve(fii.getForknum() + ""));
				log.println("\t" + fii.getForknum() + " O O " + output.relativize(injected));
			}
		}
		log.println("END: FileVariants");
		
		//Replicate Directory Injections
		log.println("BEGIN: LeafDirectoryVariants");
		for(int i = 0; i < dataset.numDirectoryInjections(); i++) {
			DirectoryInjection di = dataset.getDirectoryInjection(i);
			
			//Log original
			Path logdir = Files.createDirectories(dirsLog.resolve(di.getNum() + ""));
			FileUtils.copyDirectory(di.getLog().toFile(), logdir.resolve("original").toFile());
			String uniformFlag = di.isUniform() ? "U" : "V";
			log.println("" + di.getNum() + " " + uniformFlag + " " + di.getInstances().size() + " " + (dataset.getExperimentPath().relativize(di.getOriginal())));
			
			for(DirectoryInjectionInstance dii : di.getInstances()) {
				FileUtils.copyDirectory(di.getLog().toFile(), output.resolve(dataset.getExperimentPath().relativize(dii.getInjected().getParent())).resolve(di.getOriginal().getFileName()).toFile());
				Path injected = output.resolve(dataset.getExperimentPath().relativize(dii.getInjected().getParent())).resolve(di.getOriginal().getFileName()).toAbsolutePath().normalize();
				
				//Log
				FileUtils.copyDirectory(injected.toFile(), logdir.resolve(dii.getForknum() + "").toFile());
				log.println("\t" + dii.getForknum() + " O " + dii.getInstances().size() + " " + output.relativize(injected));
				for(DirectoryFileInjectionInstance dfii : dii.getInstances()) {
					log.println("\t\tO O " + dataset.getExperimentPath().relativize(dfii.getOriginal()) + ";" + output.relativize(injected).resolve(dfii.getOriginal().getFileName()));
				}
			}
		}
		log.println("END: LeafDirectoryVariants");
		
		//Replicate Function Injections
		log.println("BEGIN: FunctionFragmentVariants");
		for(int i = 0; i < dataset.numFunctionInjections(); i++) {
			FunctionInjection fi = dataset.getFunctionInjection(i);
			
			//log original 1 U 4 1081 1142 sourceRepository/java6/java/util/Arrays.java
			Path logdir = Files.createDirectories(funcsLog.resolve(fi.getNum() + ""));
			Files.copy(fi.getLog(), logdir.resolve("original"));
			String uniformFlag = fi.isUniform() ? "U" : "V";
			log.println("" + fi.getNum() + " " + uniformFlag + " " + fi.getInstances().size() + " " + fi.getOriginal().getStartLine() + " " + fi.getOriginal().getEndLine() + " " + dataset.getExperimentPath().relativize(fi.getOriginal().getSrcFile()));
			
			for(FunctionInjectionInstance fii : fi.getInstances()) {
				Path location = output.resolve(dataset.getExperimentPath().relativize(fii.getInjected().getSrcFile()));
				FragmentUtil.injectFragment(location, fii.getInjected().getStartLine(), fi.getLog());
				
				//Log 	1 O 155 180 1/CH/ifa/draw/contrib/zoom/ScalingGraphics.java
				Files.copy(fi.getLog(), logdir.resolve(fii.getForknum() + ""));
				log.println("\t" + fii.getForknum() + " O " + fii.getInjected().getStartLine() + " " + (FileUtil.countLines(fi.getLog()) + fii.getInjected().getStartLine() - 1) + " " + output.relativize(location));
			}
		}
		log.println("END: FunctionFragmentVariants");
		log.flush();
		log.close();
		System.out.println("Checking...");
		String argsc[] = {output.resolve("properties").toString(), output.resolve("log").toString()};
		CheckSimulation.check(argsc);
		
		
	}
}
