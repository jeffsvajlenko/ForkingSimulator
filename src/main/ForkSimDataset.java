package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import models.DirectoryFileInjectionInstance;
import models.DirectoryInjection;
import models.DirectoryInjectionInstance;
import models.FileInjection;
import models.FileInjectionInstance;
import models.Fragment;
import models.FunctionInjection;
import models.FunctionInjectionInstance;
import models.Operator;

public class ForkSimDataset {
	private List<FileInjection> file_injections = new ArrayList<FileInjection>();
	private List<FunctionInjection> function_injections = new ArrayList<FunctionInjection>();
	private List<DirectoryInjection> directory_injections = new ArrayList<DirectoryInjection>();
	private Properties properties;
	private Path experimentPath;
	
	public FileInjection getFileInjection(int num) {
		if(num < file_injections.size()) { return file_injections.get(num);}
		else { return null; }
	}
	
	public int numFileInjections() {
		return file_injections.size();
	}
	
	public FunctionInjection getFunctionInjection(int num) {
		if(num < function_injections.size()) { return function_injections.get(num); }
		else { return null; }
	}
	
	public int numFunctionInjections() {
		return function_injections.size();
	}
	
	public DirectoryInjection getDirectoryInjection(int num) {
		if(num < directory_injections.size()) { return directory_injections.get(num); }
		else { return null; }
	}
	
	public int numDirectoryInjections() {
		return directory_injections.size();
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public Path getExperimentPath() {
		return this.experimentPath;
	}
	
	public ForkSimDataset(Path experiment, Path log, Path pproperties) throws IOException {
		properties = new Properties(pproperties, new Operator[1], new Operator[1], new Operator[1]);
		properties.setRepository(experiment.resolve("sourceRepository"));
		properties.setSystem(experiment.resolve("originalSystem"));
		this.experimentPath = experiment.toAbsolutePath().normalize();
		
		BufferedReader in = new BufferedReader(new FileReader(log.toFile()));
		String line;
		boolean inFile = false;
		boolean inFunction = false;
		boolean inDirectory = false;
		
		line = in.readLine();
		while(line != null) {
		//Update Parse State
			if(line.equals("BEGIN: FileVariants")) {
				inFile = true;
			} else if (line.equals("END: FileVariants")) {
				inFile = false;
			} else if(line.equals("BEGIN: LeafDirectoryVariants")) {
				inDirectory = true;
			} else if (line.equals("END: LeafDirectoryVariants")) {
				inDirectory = false;
			} else if(line.equals("BEGIN: FunctionFragmentVariants")) {
				inFunction = true;
			} else if (line.equals("END: FunctionFragmentVariants")) {
				inFunction = false;
		//File Injections
			} else if(inFile) {
				int h_num;
				boolean h_isUniform;
				Path h_original;
				Path olog;
				int numi;
				List<FileInjectionInstance> instances = new LinkedList<FileInjectionInstance>();
				char c;
				
				Scanner s = new Scanner(line); s.useDelimiter(" ");
				h_num = s.nextInt();
				c = s.next().charAt(0);
				h_isUniform = (c == 'U') ? true : false;
				numi = s.nextInt();
				h_original = experiment.resolve(Paths.get(s.nextLine().trim()));
				s.close();
				olog = experiment.resolve("files").resolve(h_num + "").resolve("original");
				
				for(int i = 0; i < numi; i++) {
					line = in.readLine();
					
					int forknum;
					boolean isRenamed;
					boolean isMutated;
					String operator = "";
					int times = 0;
					int type = 0;
					Path injected;
					Path logf;
					char cc;
					
					Scanner ss = new Scanner(line.trim()); ss.useDelimiter(" ");
					forknum = ss.nextInt();
					cc = ss.next().charAt(0); isRenamed = (cc == 'R') ? true : false;
					cc = ss.next().charAt(0); isMutated = (cc == 'M') ? true : false;
					if(isMutated) {
						operator = ss.next();
						times = ss.nextInt();
						type = ss.nextInt();
					}
					injected = experiment.resolve(Paths.get(ss.nextLine().trim()));
					ss.close();
					logf = experiment.resolve("files").resolve(h_num + "").resolve(forknum + "");
					instances.add(new FileInjectionInstance(forknum, isRenamed, isMutated, operator, times, type, injected, logf));
				}
				file_injections.add(new FileInjection(h_num, h_isUniform, h_original, olog, instances));
			} else if (inFunction) {
				int h_num;
				boolean h_isUniform;
				int numi;
				List<FunctionInjectionInstance> instances = new LinkedList<FunctionInjectionInstance>();
				int h_oStartLine;
				int h_oEndLine;
				Path h_oFile;
				Path hlog;
				char c;
				
				Scanner s = new Scanner(line); 
				h_num = s.nextInt();
				c = s.next().charAt(0); h_isUniform = (c == 'U') ? true : false;
				numi = s.nextInt();
				h_oStartLine = s.nextInt();
				h_oEndLine = s.nextInt();
				h_oFile = experiment.resolve(Paths.get(s.nextLine().trim()));
				s.close();
				hlog = experiment.resolve("function_fragments").resolve(h_num + "").resolve("original");
				
				for(int i = 0; i < numi; i++) {
					line = in.readLine();
					int forknum;
					boolean isMutated;
					String operator = "";
					int times = 0;
					int type = 0;
					int startLine;
					int endLine;
					Path injected;
					Path logf;
					char cc;
					
					Scanner ss = new Scanner(line.trim()); ss.useDelimiter(" ");
					forknum = ss.nextInt();
					cc = ss.next().charAt(0); isMutated = (cc == 'M') ? true : false;
					if(isMutated) {
						operator = ss.next();
						times = ss.nextInt();
						type = ss.nextInt();
					}
					startLine = ss.nextInt();
					endLine = ss.nextInt();
					injected = experiment.resolve(Paths.get(ss.nextLine().trim()));
					ss.close();
					logf = experiment.resolve("function_fragments").resolve(h_num + "").resolve(forknum + "");
					instances.add(new FunctionInjectionInstance(forknum, isMutated, operator, times, type, new Fragment(injected, startLine, endLine), logf));
				}
				function_injections.add(new FunctionInjection(h_num, h_isUniform, new Fragment(h_oFile, h_oStartLine, h_oEndLine), hlog, instances));
				
			} else if (inDirectory) {
				//Parse header
				//3 V 3 /home/jeff/git/ForkingSimulator/output/sourceRepository/java6/com/sun/corba/se/impl/monitoring
				int h_num;
				boolean h_isUniform;
				int h_numi;
				Path h_original;
				char h_c;
				Path h_log;
				List<DirectoryInjectionInstance> h_instances = new LinkedList<DirectoryInjectionInstance>();
				
				Scanner s = new Scanner(line); s.useDelimiter(" ");
				h_num = s.nextInt();
				h_c = s.next().charAt(0); h_isUniform = (h_c == 'U') ? true : false;
				h_numi = s.nextInt();
				h_original = experiment.resolve(Paths.get(s.nextLine().trim()));
				s.close();
				h_log = experiment.resolve("dirs").resolve(h_num + "").resolve("original");
				
				
				//Parse directory injections
				for(int i = 0; i < h_numi; i++) {
					line = in.readLine();
					//1 O 6 /home/jeff/git/ForkingSimulator/output/1/CH/ifa/draw/util/collections/jdk12/monitoring
					int d_forknum;
					boolean d_isRenamed;
					int d_numi;
					Path d_injected;
					char d_c;
					Path d_log;
					List<DirectoryFileInjectionInstance> d_instances = new LinkedList<DirectoryFileInjectionInstance>();
					
					Scanner ss = new Scanner(line.trim()); s.useDelimiter(" ");
					d_forknum = ss.nextInt();
					d_c = ss.next().charAt(0); d_isRenamed = (d_c == 'R') ? true : false;
					d_numi = ss.nextInt();
					d_injected = experiment.resolve(Paths.get(ss.nextLine().trim()));
					ss.close();
					d_log = experiment.resolve("dirs").resolve(h_num + "").resolve(d_forknum + "");
					
					for(int j = 0; j < d_numi; j++) {
						line = in.readLine();
						boolean f_isRenamed;
						boolean f_isMutated;
						String f_operator = "";
						int f_times = 0;
						int f_type = 0;
						Path f_original;
						Path f_injected;
						Path f_log;
						char f_c;
						
						Scanner sss = new Scanner(line.trim());
						f_c = sss.next().charAt(0); f_isRenamed = (f_c == 'R') ? true : false;
						f_c = sss.next().charAt(0); f_isMutated = (f_c == 'M') ? true : false;
						if(f_isMutated) {
							f_operator = sss.next();
							f_times = sss.nextInt();
							f_type = sss.nextInt();
						}
						sss.useDelimiter(";");
						f_original = experiment.resolve(Paths.get(sss.next().trim()));
						f_injected = experiment.resolve(Paths.get(sss.next().trim()));
						sss.close();
						f_log = experiment.resolve("dirs").resolve(h_num + "").resolve(d_forknum + "").resolve(f_injected.getFileName());
						
						d_instances.add(new DirectoryFileInjectionInstance(f_isRenamed, f_isMutated, f_operator, f_times, f_type, f_original, f_injected, f_log));
					}
					h_instances.add(new DirectoryInjectionInstance(d_forknum, d_isRenamed, d_injected, d_log, d_instances));
				}
				directory_injections.add(new DirectoryInjection(h_num, h_isUniform, h_original, h_log, h_instances));
			}
			line = in.readLine();
		}	
		in.close();
	}
	
	
	
	public static void main(String args[]) throws IOException {
		ForkSimDataset dataset = new ForkSimDataset(Paths.get(args[0]), Paths.get(args[1]), Paths.get(args[2]));
		System.out.println("--Test--");
		System.out.println("FileInjections:");
		for(int i = 0; i < dataset.numFileInjections(); i++) {
			System.out.println(dataset.getFileInjection(i));
		}
		System.out.println("FunctionInjections:");
		for(int i = 0; i < dataset.numFunctionInjections(); i++) {
			System.out.println(dataset.getFunctionInjection(i));
		}
		System.out.println("DirectoryInjections:");
		for(int i = 0; i < dataset.numDirectoryInjections(); i++) {
			System.out.println(dataset.getDirectoryInjection(i));
		}
	}
}
