package main;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

import models.Operator;

public class Properties {
	
	private Path system;
	private boolean setsystem=false;
	
	private Path repository;
	private boolean setrepository=false;
	
	private String language;
	private boolean setlanguage=false;
	
	private int numFiles;
	private boolean setnumFiles=false;
	
	private int numDirectories;
	private boolean setnumDirectories=false;
	
	private int numFragments;
	private boolean setnumFragments=false;
	
	private int maxInjectNum;
	private boolean setmaxInjectNum=false;
	
	private int numForks;
	private boolean setnumForks=false;
	
	private int fragmentMutationRate;
	private boolean setfragmentMutationRate=false;
	
	private int fileMutationRate;
	private boolean setfileMutationRate=false;
	
	private int dirMutationRate;
	private boolean setdirMutationRate=false;
	
	private int mutationAttempts;
	private boolean setmutationAttempts=false;
	
	private int injectionRepetitionRate;
	private boolean setinjectionRepetitionRate=false;
	
	private int functionFragmentMinSize;
	private boolean setfunctionFragmentMinSize=false;
	
	private int functionFragmentMaxSize;
	private boolean setfunctionFragmentMaxSize=false;
	
	private int maxFunctionEdit;
	private boolean setMaxFunctionEdit=false;
	
	private int maxFileEdit;
	private boolean setmaxFileEdit=false;
	
	private int fileRenameRate;
	private boolean setfileRenameRate=false;
	
	private int dirRenameRate;
	private boolean setdirRenameRate=false;
	
	/**
	 * Creates a Properties object with the properties specified by the properties file.
	 * @param propertiesfile Path to the properties file.
	 * @throws FileNotFoundException If the properties file is not found.
	 * @throws IllegalArgumentException If the properties file is invalid (does not exist / is a directory), or if an entry in the properties is invalid/malformed.
	 */
	public Properties(Path propertiesfile, Operator functionOperators[], Operator fileOperators[], Operator dirOperators[]) {
		Objects.requireNonNull(propertiesfile);
		Objects.requireNonNull(functionOperators);
		Objects.requireNonNull(fileOperators);
		Objects.requireNonNull(dirOperators);
		if(!Files.exists(propertiesfile)) {
			throw new IllegalArgumentException("Propertiesfile must refer to an existing file.");
		}
		if(!Files.isRegularFile(propertiesfile)) {
			throw new IllegalArgumentException("Propertiesfile must refer to a regular file.");
		}
		if(functionOperators.length == 0) {
			throw new IllegalArgumentException("Must specify at least one function operator.");
		}
		if(fileOperators.length == 0) {
			throw new IllegalArgumentException("Must specify at least one file operator.");
		}
		if(dirOperators.length == 0) {
			throw new IllegalArgumentException("Must specify at least one directory operator.");
		}
		
		this.functionMutationOperators = functionOperators;
		this.fileMutationOperators = fileOperators;
		this.directoryMutationOperators = dirOperators;
		this.functionCurrentOperator = 0;
		this.fileCurrentOperator = 0;
		this.directoryCurrentOperator = 0;
		
		Scanner s = null;
		try {	
			s = new Scanner(propertiesfile.toFile());
			String line;
			while(s.hasNextLine()) {
				line = s.nextLine();
				line = line.trim();
				
				//Comment Line
				if(line.startsWith("#")) {
					continue;
					
				//Property Line
				} else {
					//System Directory
					if(line.startsWith("system=")) {
						line = line.substring(7);
						try {
							this.system = Paths.get(line).toAbsolutePath().normalize();
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Propety 'system' is invalid.");
						}
						this.setsystem=true;
					//Repository Directory
					} else if (line.startsWith("repository=")) {
						line = line.substring(11);
						try {
							this.repository = Paths.get(line).toAbsolutePath().normalize();
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Propety 'repository' is invalid.");
						}
						this.setrepository=true;
					//Language
					} else if (line.startsWith("language=")) {
						line = line.substring(9).toLowerCase();
						this.language = line;
						if(!(language.equals("java") || language.equals("c") || language.equals("cs"))) {
							s.close();
							throw new IllegalArgumentException("Propety 'language' is invalid.");
						}
						this.setlanguage=true;
					//numDirectories
					} else if (line.startsWith("numDirectories=")) {
						line = line.substring(15);
						try {
							this.numDirectories = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Propety 'numDirectories' is invalid.");
						}
						if(this.numDirectories < 0) {
							s.close();
							throw new IllegalArgumentException("Propety 'numDirectories' is invalid.");
						}
						this.setnumDirectories=true;
					//numFiles
					} else if (line.startsWith("numFiles=")) {
						line = line.substring(9);
						try {
							this.numFiles = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Propety 'numFiles' is invalid.");
						}
						if(numFiles < 0) {
							s.close();
							throw new IllegalArgumentException("Propety 'numDirectories' is invalid.");
						}
						this.setnumFiles=true;
					//numFragments
					} else if (line.startsWith("numFragments=")) {
						line = line.substring(13);
						try {
							this.numFragments = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Propety 'numFragments' is invalid.");
						}
						if(this.numFragments < 0) {
							s.close();
							throw new IllegalArgumentException("Propety 'numFragments' is invalid.");
						}
						this.setnumFragments=true;
					//numForks
					} else if (line.startsWith("numForks=")) {
						line = line.substring(9);
						try {
							this.numForks = Integer.parseInt(line);
						} catch (Exception e){
							s.close();
							throw new IllegalArgumentException("Propety 'numForks' is invalid.");
						}
						if(this.numForks <= 0) {
							s.close();
							throw new IllegalArgumentException("Propety 'numForks' is invalid.");
						}
						this.setnumForks=true;
					//maxInjectNum
					} else if (line.startsWith("maxInjectNum=")) {
						line = line.substring(13);
						try {
							this.maxInjectNum = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'maxInjectNum' is invalid.");
						}
						if(this.maxInjectNum <= 0) {
							s.close();
							throw new IllegalArgumentException("Property 'maxInjectNum' is invalid.");
						}
						this.setmaxInjectNum=true;
					//mutation rate
					} else if (line.startsWith("fragmentMutationRate=")) {
						line = line.substring(21);
						try {
							this.fragmentMutationRate = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationrate' is invalid.");
						}
						if(this.fragmentMutationRate < 0 || this.fragmentMutationRate > 100) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationrate' is invalid.");
						}
						this.setfragmentMutationRate=true;
					} else if (line.startsWith("fileMutationRate=")) {
						line = line.substring(17);
						try {
							this.fileMutationRate = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationrate' is invalid.");
						}
						if(this.fileMutationRate < 0 || this.fileMutationRate > 100) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationrate' is invalid.");
						}
						this.setfileMutationRate=true;
					} else if (line.startsWith("dirMutationRate=")) {
						line = line.substring(16);
						try {
							this.dirMutationRate = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationrate' is invalid.");
						}
						if(this.dirMutationRate < 0 || this.dirMutationRate > 100) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationrate' is invalid.");
						}
						this.setdirMutationRate=true;
					//mutation attempts
					} else if (line.startsWith("mutationAttempts=")) {
						line = line.substring(17);
						try {
							this.mutationAttempts = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationAttempts' is invalid.");
						}
						if(this.mutationAttempts <= 0) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationAttempts' is invalid.");
						}
						this.setmutationAttempts=true;
					//injectionreptitionrate
					} else if (line.startsWith("injectionRepetitionRate=")) {
						line = line.substring(24);
						try {
							this.injectionRepetitionRate = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'injectionRepetitionRate' is invalid.");
						}
						if(this.injectionRepetitionRate < 0 || this.injectionRepetitionRate > 100) {
							s.close();
							throw new IllegalArgumentException("Property 'injectionRepetitionRate' is invalid.");
						}
						this.setinjectionRepetitionRate = true;
					//functionFragmentMinSize
					} else if (line.startsWith("functionFragmentMinSize=")) {
						line = line.substring(24);
						try {
							this.functionFragmentMinSize = Integer.parseInt(line);
						} catch(Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'functionFragmentMinSize' is invalid.");
						}
						if(this.functionFragmentMinSize <= 0) {
							s.close();
							throw new IllegalArgumentException("Property 'functionFragmentMinSize' is invalid.");
						}
						this.setfunctionFragmentMinSize=true;
					//functionFragmentMaxSize
					} else if (line.startsWith("functionFragmentMaxSize=")) {
						line = line.substring(24);
						try {
							this.functionFragmentMaxSize = Integer.parseInt(line);
						} catch(Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'functionFragmentMaxSize' is invalid.");
						}
						if(this.functionFragmentMaxSize < 0) {
							s.close();
							throw new IllegalArgumentException("Property 'functionFragmentMaxSize' is invalid.");
						}
						this.setfunctionFragmentMaxSize=true;
					//maxfiledit
					} else if (line.startsWith("maxFileEdit=")) {
						line = line.substring(12);
						try {
							this.maxFileEdit = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'maxFileEdit' is invalid.");
						}
						if(this.maxFileEdit < 0) {
							s.close();
							throw new IllegalArgumentException("Property 'maxFileEdit' is invalid.");
						}
						this.setmaxFileEdit=true;
					//maxfunctionedit
					} else if (line.startsWith("maxFunctionEdit=")) {
						line = line.substring(16);
						try {
							this.maxFunctionEdit = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'maxFunctionEdit' is invalid.");
						}
						if(this.maxFunctionEdit < 0) {
							s.close();
							throw new IllegalArgumentException("Property 'maxFunctionEdit' is invalid.");
						}
						this.setMaxFunctionEdit=true;
					//renamerate
					} else if (line.startsWith("fileRenameRate=")) {
						line = line.substring(15);
						try {
							this.fileRenameRate = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'fileRenameRate' is invalid.");
						}
						if(this.fileRenameRate < 0 || this.fileRenameRate > 100) {
							s.close();
							throw new IllegalArgumentException("Property 'fileRenameRate' is invalid.");
						}
						this.setfileRenameRate = true;
					} else if (line.startsWith("dirRenameRate=")) {
						line = line.substring(14);
						try {
							this.dirRenameRate = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'dirRenameRate' is invalid.");
						}
						if(this.dirRenameRate < 0 || this.dirRenameRate > 100) {
							s.close();
							throw new IllegalArgumentException("Property 'dirRenameRate' is invalid.");
						}
						this.setdirRenameRate = true;
					}
				}
			}
			s.close();
			
			//Check properties were set
			if(!this.setlanguage) {
				throw new IllegalArgumentException("Property 'language' was not specified.");
			}
			if(!this.setmaxInjectNum) {
				throw new IllegalArgumentException("Property 'maxInjectNum' was not specified.");
			}
			if(!this.setfragmentMutationRate) {
				throw new IllegalArgumentException("Property 'fragmentMutationRate' was not specified.");
			}
			if(!this.setfileMutationRate) {
				throw new IllegalArgumentException("Property 'fileMutationRate' was not specified.");
			}
			if(!this.setdirMutationRate) {
				throw new IllegalArgumentException("Property 'dirMutationRate' was not specified.");
			}
			if(!this.setnumDirectories) {
				throw new IllegalArgumentException("Property 'numDirectories' was not specified.");
			}
			if(!this.setnumFiles) {
				throw new IllegalArgumentException("Property 'numFiles' was not specified.");
			}
			if(!this.setnumForks) {
				throw new IllegalArgumentException("Property 'numForks' was not specified.");
			}
			if(!this.setnumFragments) {
				throw new IllegalArgumentException("Property 'numFragments' was not specified.");
			}
			if(!this.setrepository) {
				throw new IllegalArgumentException("Property 'repository' was not specified.");
			}
			if(!this.setsystem) {
				throw new IllegalArgumentException("Property 'system' was not specified.");
			}
			if(!this.setmutationAttempts) {
				throw new IllegalArgumentException("Property 'mutationAttempts' was not specified.");
			}
			if(!this.setfunctionFragmentMinSize) {
				throw new IllegalArgumentException("Property 'functionFragmentMinSize' was not specified.");
			}
			if(!this.setfunctionFragmentMaxSize) {
				throw new IllegalArgumentException("Property 'functionFragmentMaxSize' was not specified.");
			}
			if(!this.setinjectionRepetitionRate){
				throw new IllegalArgumentException("Property 'injectionRepetitionRate' was not specified.");
			}
			if(this.functionFragmentMinSize > this.functionFragmentMaxSize) {
				throw new IllegalArgumentException("Property 'functionFragmentMinSize' is greater than 'functionFragmentMaxSize'.");
			}
			if(!this.setmaxFileEdit){
				throw new IllegalArgumentException("Property 'maxFileEdit' was not specified.");
			}
			if(!this.setMaxFunctionEdit){
				throw new IllegalArgumentException("Property 'maxFunctionEdit' was not specified.");
			}
			if(!this.setfileRenameRate) {
				throw new IllegalArgumentException("Property 'fileRenameRate' was not specified.");
			}
			if(!this.setdirRenameRate) {
				throw new IllegalArgumentException("Property 'dirRenameRate' was not specified.");
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Propertiesfile must refer to an existing file.");
		}
	}

	/**
	 * @return The path to the system being forked.
	 */
	public Path getSystem() {
		return system;
	}

	public void setSystem(Path system) {
		this.system = system;
	}
	
	/**
	 * @return The path to the repository where variants are mined form.
	 */
	public Path getRepository() {
		return repository;
	}

	public void setRepository(Path repository) {
		this.repository = repository;
	}
	
	/**
	 * @return The source language of the system and repository.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @return The number of file variants to create.
	 */
	public int getNumFiles() {
		return numFiles;
	}

	/**
	 * @return The number of directory variants to create.
	 */
	public int getNumDirectories() {
		return numDirectories;
	}

	/**
	 * @return The number of fragment variants to create.
	 */
	public int getNumFragments() {
		return numFragments;
	}

	/**
	 * @return The maximum number of forked systems the variants should be duplicated in.
	 */
	public int getMaxinjectNum() {
		return maxInjectNum;
	}

	/**
	 * @return The number of forked systems to create.
	 */
	public int getNumForks() {
		return numForks;
	}

	/**
	 * @return The chance that a fragment variant is mutated before injection into a fork.
	 */
	public int getFragmentMutationRate() {
		return fragmentMutationRate;
	}
	
	/**
	 * @return The chance that a file variant is mutated before injection into a fork.
	 */
	public int getFileMutationRate() {
		return fileMutationRate;
	}
	
	/**
	 * @return The chance that a directory variant is mutated before injection into a fork.
	 */
	public int getDirMutationRate() {
		return dirMutationRate;
	}
	
	/**
	 * @return How many times to attempt a fragment mutation.
	 */
	public int getMutationAttempts() {
		return mutationAttempts;
	}
	
	/**
	 * @return The minimum size of function fragments to select.
	 */
	public int getFunctionFragmentMinSize() {
		return this.functionFragmentMinSize;
	}
	
	/**
	 * @return The maximum size of function fragments to select.
	 */
	public int getFunctionFragmentMaxSize() {
		return this.functionFragmentMaxSize;
	}
	
	/**
	 * @return The injection repetition rate.
	 */
	public int getInjectionReptitionRate() {
		return this.injectionRepetitionRate;
	}
	
	/**
	 * @return The maximum number of times to apply an operator on a file.
	 */
	public int getMaxFileEdit() {
		return this.maxFileEdit;
	}
	
	public int getMaxFunctionEdit() {
		return this.maxFunctionEdit;
	}
	
	/**
	 * @return The chance that a file is renamed before injection.
	 */
	public int getFileRenameRate() {
		return this.fileRenameRate;
	}
	
	/**
	 * @return The chance that a directory is renamed before injection.
	 */
	public int getDirRenameRate() {
		return this.dirRenameRate;
	}

	public void setMaxInjectNum(int maxInjectNum) {
		this.maxInjectNum = maxInjectNum;
	}

	public void setMutationAttempts(int mutationAttempts) {
		this.mutationAttempts = mutationAttempts;
	}

	public void setMaxFunctionEdit(int maxFunctionEdit) {
		this.maxFunctionEdit = maxFunctionEdit;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setNumFiles(int numFiles) {
		this.numFiles = numFiles;
	}

	public void setNumDirectories(int numDirectories) {
		this.numDirectories = numDirectories;
	}

	public void setNumFragments(int numFragments) {
		this.numFragments = numFragments;
	}

	public void setNumForks(int numForks) {
		this.numForks = numForks;
	}

	public void setFragmentMutationRate(int fragmentMutationRate) {
		this.fragmentMutationRate = fragmentMutationRate;
	}

	public void setFileMutationRate(int fileMutationRate) {
		this.fileMutationRate = fileMutationRate;
	}

	public void setDirMutationRate(int dirMutationRate) {
		this.dirMutationRate = dirMutationRate;
	}

	public void setInjectionRepetitionRate(int injectionRepetitionRate) {
		this.injectionRepetitionRate = injectionRepetitionRate;
	}

	public void setFunctionFragmentMinSize(int functionFragmentMinSize) {
		this.functionFragmentMinSize = functionFragmentMinSize;
	}

	public void setFunctionFragmentMaxSize(int functionFragmentMaxSize) {
		this.functionFragmentMaxSize = functionFragmentMaxSize;
	}

	public void setFileRenameRate(int fileRenameRate) {
		this.fileRenameRate = fileRenameRate;
	}

	public void setDirRenameRate(int dirRenameRate) {
		this.dirRenameRate = dirRenameRate;
	}



	private Operator functionMutationOperators[];
	private Operator fileMutationOperators[];
	private Operator directoryMutationOperators[];
	
	int functionCurrentOperator = 0;
	int fileCurrentOperator = 0;
	int directoryCurrentOperator = 0;
	
	public OperatorChooser getFunctionMutationOperatorChooser() {
		return new OperatorChooser(functionMutationOperators, functionCurrentOperator);
	}
	
	public OperatorChooser getFileMutationOperatorChooser() {
		return new OperatorChooser(fileMutationOperators, fileCurrentOperator);
	}
	
	public OperatorChooser getDirectoryMutationOperatorChooser() {
		return new OperatorChooser(directoryMutationOperators, directoryCurrentOperator);
	}
	
	public void incrementCurrentFunctionOperator() {
		this.functionCurrentOperator = nextRollingNumber(functionCurrentOperator, functionMutationOperators.length - 1);
	}
	
	public void incrementCurrentFileOperator() {
		this.fileCurrentOperator = nextRollingNumber(this.fileCurrentOperator, fileMutationOperators.length - 1);
	}
	
	public void incrementCurrentDirectoryOperator() {
		this.directoryCurrentOperator = nextRollingNumber(this.directoryCurrentOperator, directoryMutationOperators.length - 1);
	}
	
	public Path write(Path file) throws IOException {
		Objects.requireNonNull(file);
		if(Files.exists(file)) {
			throw new IllegalArgumentException("File must not exist.");
		}
		file = Files.createFile(file);
		
		PrintWriter pw = new PrintWriter(new FileWriter(file.toFile()));
		pw.println("system=" + this.getSystem());
		pw.println("repository=" + this.getRepository());
		pw.println("language=" + this.getLanguage());
		pw.println("numForks=" + this.getNumForks());
		pw.println("numFiles=" + this.getNumFiles());
		pw.println("numDirectories=" + this.getNumDirectories());
		pw.println("numFragments=" + this.getNumFragments());
		pw.println("functionFragmentMinSize=" + this.getFunctionFragmentMinSize());
		pw.println("functionFragmentMaxSize=" + this.getFunctionFragmentMaxSize());
		pw.println("maxInjectNum=" + this.getMaxinjectNum());
		pw.println("injectionRepitionRate=" + this.getInjectionReptitionRate());
		pw.println("fragmentMutationRate=" + this.getFragmentMutationRate());
		pw.println("fileMutationRate=" + this.getFileMutationRate());
		pw.println("dirMutationRate=" + this.getDirMutationRate());
		pw.println("fileRenameRate=" + this.getFileRenameRate());
		pw.println("dirRenameRate=" + this.getDirRenameRate());
		pw.println("maxFileEdit=" + this.getMaxFileEdit());
		pw.println("maxFunctionEdit=" + this.getMaxFunctionEdit());
		pw.println("mutationAttempts=" + this.getMutationAttempts());
		pw.flush();
		pw.close();
		
		return file;
	}
	
	/**
	 * Increments an integer from 0 to maximum (inclusive), with roll around.
	 * @param current The current value.
	 * @param maximum The maximum value.
	 * @return the next value.
	 * @throws IllegalArgumentException if current is greater than maximum or less than 0.
	 */
	private static int nextRollingNumber(int current, int maximum) {
		if(current > maximum || current < 0) {
			throw new IllegalArgumentException();
		}
		int next = current+1;
		if(next > maximum) {
			next = 0;
		}
		return next;
	}
	
	
	
}
