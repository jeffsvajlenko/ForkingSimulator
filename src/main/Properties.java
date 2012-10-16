package main;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;


public class Properties {
	
	private Path system;
	private boolean setsystem=false;
	
	private Path repository;
	private boolean setrepository=false;
	
	private String language;
	private boolean setlanguage=false;
	
	private int numfiles;
	private boolean setnumfiles=false;
	
	private int numdirectories;
	private boolean setnumdirectories=false;
	
	private int numfragments;
	private boolean setnumfragments=false;
	
	private int maxinjectnum;
	private boolean setmaxinjectnum=false;
	
	private int numforks;
	private boolean setnumforks=false;
	
	private int mutationrate;
	private boolean setmutationrate=false;
	
	private int mutationattempts;
	private boolean setmutationattempts=false;
	
	private int functionfragmentminsize;
	private boolean setfunctionfragmentminsize=false;
	
	private int functionfragmentmaxsize;
	private boolean setfunctionfragmentmaxsize=false;
	
	/**
	 * Creates a Properties object with the properties specified by the properties file.
	 * @param propertiesfile Path to the properties file.
	 * @throws FileNotFoundException If the properties file is not found.
	 * @throws IllegalArgumentException If the properties file is invalid (does not exist / is a directory), or if an entry in the properties is invalid/malformed.
	 */
	public Properties(Path propertiesfile) {
		Objects.requireNonNull(propertiesfile);
		if(!Files.exists(propertiesfile)) {
			throw new IllegalArgumentException("Propertiesfile must refer to an existing file.");
		}
		if(!Files.isRegularFile(propertiesfile)) {
			throw new IllegalArgumentException("Propertiesfile must refer to a regular file.");
		}
		
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
						if(!(language.equals("java") || language.equals("c") || language.equals("c#"))) {
							s.close();
							throw new IllegalArgumentException("Propety 'language' is invalid.");
						}
						this.setlanguage=true;
					//numdirectories
					} else if (line.startsWith("numdirectories=")) {
						line = line.substring(15);
						try {
							this.numdirectories = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Propety 'numdirectories' is invalid.");
						}
						if(this.numdirectories < 0) {
							s.close();
							throw new IllegalArgumentException("Propety 'numdirectories' is invalid.");
						}
						this.setnumdirectories=true;
					//numfiles
					} else if (line.startsWith("numfiles=")) {
						line = line.substring(9);
						try {
							this.numfiles = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Propety 'numfiles' is invalid.");
						}
						if(numfiles < 0) {
							s.close();
							throw new IllegalArgumentException("Propety 'numdirectories' is invalid.");
						}
						this.setnumfiles=true;
					//numfragments
					} else if (line.startsWith("numfragments=")) {
						line = line.substring(13);
						try {
							this.numfragments = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Propety 'numfragments' is invalid.");
						}
						if(this.numfragments < 0) {
							s.close();
							throw new IllegalArgumentException("Propety 'numfragments' is invalid.");
						}
						this.setnumfragments=true;
					//numforks
					} else if (line.startsWith("numforks=")) {
						line = line.substring(9);
						try {
							this.numforks = Integer.parseInt(line);
						} catch (Exception e){
							s.close();
							throw new IllegalArgumentException("Propety 'numforks' is invalid.");
						}
						if(this.numforks <= 0) {
							s.close();
							throw new IllegalArgumentException("Propety 'numforks' is invalid.");
						}
						this.setnumforks=true;
					//maxinjectnum
					} else if (line.startsWith("maxinjectnum=")) {
						line = line.substring(13);
						try {
							this.maxinjectnum = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'maxinjectnum' is invalid.");
						}
						if(this.maxinjectnum <= 0) {
							s.close();
							throw new IllegalArgumentException("Property 'maxinjectnum' is invalid.");
						}
						this.setmaxinjectnum=true;
					//mutation rate
					} else if (line.startsWith("mutationrate=")) {
						line = line.substring(13);
						try {
							this.mutationrate = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationrate' is invalid.");
						}
						if(this.mutationrate < 0 || this.mutationrate > 100) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationrate' is invalid.");
						}
						this.setmutationrate=true;
					//mutation attempts
					} else if (line.startsWith("mutationattempts=")) {
						line = line.substring(17);
						try {
							this.mutationattempts = Integer.parseInt(line);
						} catch (Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationattempts' is invalid.");
						}
						if(this.mutationattempts <= 0) {
							s.close();
							throw new IllegalArgumentException("Property 'mutationattempts' is invalid.");
						}
						this.setmutationattempts=true;
					//functionfragmentminsize
					} else if (line.startsWith("functionfragmentminsize=")) {
						line = line.substring(24);
						try {
							this.functionfragmentminsize = Integer.parseInt(line);
						} catch(Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'functionfragmentminsize' is invalid.");
						}
						if(this.functionfragmentminsize <= 0) {
							s.close();
							throw new IllegalArgumentException("Property 'functionfragmentminsize' is invalid.");
						}
						this.setfunctionfragmentminsize=true;
					//functionfragmentmaxsize
					} else if (line.startsWith("functionfragmentmaxsize=")) {
						line = line.substring(24);
						try {
							this.functionfragmentmaxsize = Integer.parseInt(line);
						} catch(Exception e) {
							s.close();
							throw new IllegalArgumentException("Property 'functionfragmentmaxsize' is invalid.");
						}
						if(this.functionfragmentmaxsize <= 0) {
							s.close();
							throw new IllegalArgumentException("Property 'functionfragmentmaxsize' is invalid.");
						}
						this.setfunctionfragmentmaxsize=true;
					}
				}
			}
			s.close();
			//Check properties were set
			if(!this.setlanguage) {
				throw new IllegalArgumentException("Property 'language' was not specified.");
			}
			if(!this.setmaxinjectnum) {
				throw new IllegalArgumentException("Property 'maxinjectnum' was not specified.");
			}
			if(!this.setmutationrate) {
				throw new IllegalArgumentException("Property 'mutationrate' was not specified.");
			}
			if(!this.setnumdirectories) {
				throw new IllegalArgumentException("Property 'numdirectories' was not specified.");
			}
			if(!this.setnumfiles) {
				throw new IllegalArgumentException("Property 'numfiles' was not specified.");
			}
			if(!this.setnumforks) {
				throw new IllegalArgumentException("Property 'numforks' was not specified.");
			}
			if(!this.setnumfragments) {
				throw new IllegalArgumentException("Property 'numfragments' was not specified.");
			}
			if(!this.setrepository) {
				throw new IllegalArgumentException("Property 'repository' was not specified.");
			}
			if(!this.setsystem) {
				throw new IllegalArgumentException("Property 'system' was not specified.");
			}
			if(!this.setmutationattempts) {
				throw new IllegalArgumentException("Property 'mutationattempts' was not specified.");
			}
			if(!this.setfunctionfragmentminsize) {
				throw new IllegalArgumentException("Property 'functionfragmentminsize' was not specified.");
			}
			if(!this.setfunctionfragmentmaxsize) {
				throw new IllegalArgumentException("Property 'functionfragmentmaxsize' was not specified.");
			}
			if(this.functionfragmentminsize > this.functionfragmentmaxsize) {
				throw new IllegalArgumentException("Property 'functionfragmentminsize' is greater than 'functionfragmentmaxsize'.");
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

	/**
	 * @return The path to the repository where variants are mined form.
	 */
	public Path getRepository() {
		return repository;
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
		return numfiles;
	}

	/**
	 * @return The number of directory variants to create.
	 */
	public int getNumDirectories() {
		return numdirectories;
	}

	/**
	 * @return The number of fragment variants to create.
	 */
	public int getNumFragments() {
		return numfragments;
	}

	/**
	 * @return The maximum number of forked systems the variants should be duplicated in.
	 */
	public int getMaxinjectnum() {
		return maxinjectnum;
	}

	/**
	 * @return The number of forked systems to create.
	 */
	public int getNumForks() {
		return numforks;
	}

	/**
	 * @return The chance that a fragment variant is mutated before injection into a fork.
	 */
	public int getMutationRate() {
		return mutationrate;
	}
	
	/**
	 * @return How many times to attempt a fragment mutation.
	 */
	public int getNumMutationAttempts() {
		return mutationattempts;
	}
	
	/**
	 * @return The minimum size of function fragments to select.
	 */
	public int getFunctionFragmentMinSize() {
		return this.functionfragmentminsize;
	}
	
	/**
	 * @return The maximum size of function fragments to select.
	 */
	public int getFunctionFragmentMaxSize() {
		return this.functionfragmentmaxsize;
	}
}
