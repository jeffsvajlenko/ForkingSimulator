package models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;

import util.FragmentUtil;
import util.StreamGobbler;
import util.SystemUtil;

/**
 * 
 * Represents a mutation operator.
 * 
 */
public class Operator {
	private String id; /** The operator's unique identifier */
	private String description; /** The description of the operator */
	private int targetCloneType; /** The type of clone this operator produces */
	private Path mutator; /** Script used to perform this mutation operator */
	
	/**
	 * Creates an operator.
	 * @param id The operator's unique identifier.
	 * @param description The description of the operator.
	 * @param targetCloneType The type of clone this operator produces.  Must be in set: {1,2,3}.
	 * @param mutator Script which performs the mutator.
	 * @throws IllegalArgumentException If targetCloneType is not one of {1,2,3}.
	 * @throws NullPointerException If any of the object parameters are null.
	 */
	public Operator(String id, String description, int targetCloneType, Path mutator) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(description);
		Objects.requireNonNull(mutator);
		
		if(targetCloneType != 1 && targetCloneType != 2 && targetCloneType != 3) {
			throw new IllegalArgumentException("Target clone type is invalid.");
		}
		if(!Files.isReadable(mutator)) {
			throw new IllegalArgumentException("Mutator is not readable.");
		}
		
		this.id = id;
		this.description = description;
		this.targetCloneType = targetCloneType;
		this.mutator = mutator;
	}
	
	/**
	 * Returns this operator's unique identifier.
	 * @return the operator's unique identifier.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the description of this operator.
	 * @return the description of this operator.
	 */
	public String getDescription() {
		return description;
	} 
	
	/**
	 * Returns the clone type this operator produces.
	 * @return the clone type this operator produces.
	 */
	public int getTargetCloneType() {
		return targetCloneType;
	}

	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		Operator o = (Operator) obj;
		if(this.description.equals(o.description) &&
				this.id == o.id &&
				this.mutator.equals(o.mutator) &&
				this.targetCloneType == o.targetCloneType) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Performs this mutation operator on infile to create outfile.
	 * @param infile The input file.
	 * @param outfile The output file.
	 * @param allowedDiff The allowed % difference between the original and mutated fragment (in case of type 3 clone).
	 * @return 0 if mutation succeeded, or -1 if it failed.  Failure could occur if the operator can not be applied to the input file or if the mutation causes the fragments to diverge more than the allowed difference.
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InterruptedException 
	 */
	public int performOperator(Path infile, Path outfile, int numAttempts, String language) throws FileNotFoundException, IOException, InterruptedException {
		int txl_retval;
		double sim_retval;		

		Path tmpfile1 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "Operator-PerformOperator", null);
		Path tmpfile2 = Files.createTempFile(SystemUtil.getTemporaryDirectory(), "Operator-PerformOperator", null);
		
		while(numAttempts > 0) {//keep trying it don't get it right until attempts is used up
			numAttempts--;
			
			// Perform the mutation
			String[] command = new String[6];
			command[0] = mutator.toAbsolutePath().normalize().toString();
			command[1] = SystemUtil.getTxlExecutable().toString();
			command[2] = language;
			command[3] = SystemUtil.getOperatorDirectory().toString();
			command[4] = infile.toAbsolutePath().normalize().toString();
			command[5] = outfile.toAbsolutePath().normalize().toString();
			//String rcommand = command[0] + " " + command[1] + " " + command[2] + " " + command[3] + " " + command[4] + " " + command[5];
			//System.out.println(rcommand);
			Process p = Runtime.getRuntime().exec(command);
			new StreamGobbler(p.getErrorStream()).start();
			new StreamGobbler(p.getInputStream()).start();
			//Scanner s = new Scanner(p.getInputStream());
			//while(s.hasNextLine()) System.out.println(s.nextLine());
			//s.close();
			//Scanner s2 = new Scanner(p.getErrorStream());
			//while(s2.hasNextLine()) System.out.println(s2.nextLine());
			//s2.close();
			
			int pretval = p.waitFor();
			//System.out.println(pretval);
			// If mutation failed, give up (not possible to do the mutation even if retry)
			if(pretval != 0) {
				continue;
			}
			
			// Check if mutation produced valid output
			if(getTargetCloneType() == 1) { //type1 check
				// Create pretty-printed versions of the fragment
				txl_retval = SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), infile, tmpfile1);
				if(txl_retval != 0) { // parsing failed, original file is defective, fail
					break;
				}
				
				// Create pretty-printed version of the mutant
				txl_retval = SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), outfile, tmpfile2);
				if(txl_retval != 0) { // parsing failed, something went wrong during mutation, try again
					continue;
				}
				
				// Is this a proper type1 clone? (is 100% similar?)
				sim_retval = FragmentUtil.getSimilarity(tmpfile1, tmpfile2);
				if(Math.abs(sim_retval-1) < 0.00000001) {
					Files.deleteIfExists(tmpfile1);
					Files.deleteIfExists(tmpfile2);
					return 0;
				} else {
					continue;
				}
			} else if (getTargetCloneType() == 2) { //type2 check
				// Create pretty-printed, blind-renamed versions of the fragment and mutant
				txl_retval = SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), infile, tmpfile1);
				if(txl_retval != 0) { // parsing failed on original fragment, fail now
					break;
				}
				txl_retval = SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("BlindRenameFragment.txl"), outfile, tmpfile2);
				if(txl_retval != 0) { // parsing failed, something went wrong during mutation, try again
					continue;
				}
				
				// Is this a proper type2 clone?
				sim_retval = FragmentUtil.getSimilarity(tmpfile1,tmpfile2);
				if(Math.abs(sim_retval-1) < 0.00000001) {
					Files.deleteIfExists(tmpfile1);
					Files.deleteIfExists(tmpfile2);
					return 0;
				} else {
					continue;
				}
			} else if (getTargetCloneType() == 3) { //type3 check
				// Create pretty-printed versions of the fragment and mutant, not to use for checking but just to ensure they are parseable (Correct syntax)
				txl_retval = SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), infile, tmpfile1);
				if(txl_retval != 0) { // parsing failed, original 
					continue;
				}
				txl_retval = SystemUtil.runTxl(SystemUtil.getTxlDirectory(language).resolve("PrettyPrintFragment.txl"), outfile, tmpfile2);
				if(txl_retval != 0) { // parsing failed, something went wrong, try again
					continue;
				}
				
				// Is this an acceptable type3 clone (using original files)
				sim_retval = FragmentUtil.getSimilarity(tmpfile1, tmpfile2);
				if(sim_retval < 1.0) {
					Files.deleteIfExists(tmpfile1);
					Files.deleteIfExists(tmpfile2);
					return 0;
				} else {
					continue;
				}
			} else { // ???, operator has unknown clone type
				break;
			}
		}
		// reach here if failed in the given number of attempts
		Files.deleteIfExists(tmpfile1);
		Files.deleteIfExists(tmpfile2);
		return -1;
	}
}
