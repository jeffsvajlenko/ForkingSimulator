package models;
import java.nio.file.Path;
import java.util.Objects;


public class FileVariant extends Variant {

	Path originalfile;
	Path injectedfile;
	Operator operator;
	int times;
	
	/**
	 * Creates a file variant.
	 * @param osrcfile The original source of the file.
	 * @param isrcfile The location of the injected variant.
	 * @param operator The mutation operator applied.
	 * @param times The number of times the operator was applied.
	 */
	public FileVariant(Path originalfile, Path injectedfile, Operator operator, int times) {
		super(Variant.FILE);
		Objects.requireNonNull(originalfile, "Original file is null.");
		Objects.requireNonNull(injectedfile, "Injected file is null.");
		this.originalfile = originalfile;
		this.injectedfile = injectedfile;
		this.operator = operator;
		this.times = times;
	}
	
	/**
	 * Creates a file variant.
	 * @param osrcfile The original source of the file.
	 * @param isrcfile The location of the injected variant.
	 */
	public FileVariant(Path originalfile, Path injectedfile) {
		this(originalfile,injectedfile,null,0);
	}
	
	/**
	 * Returns if the file variant's file name was mutated.
	 * @return if the file variant's file name was mutated.
	 */
	public boolean isNameMutated() {
		if(originalfile.getFileName().equals(injectedfile.getFileName())) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isSourceMutated() {
		if(this.operator == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the path to the original file.
	 * @return the path to the original file.
	 */
	public Path getOriginalFile() {
		return originalfile;
	}
	
	/**
	 * Returns the path to the file variant (injected file).
	 * @return the path to the file variant (injected file).
	 */
	public Path getInjectedFile() {
		return injectedfile;
	}
	
	public String toString() {
		return "FileVariant[original: " + originalfile.toAbsolutePath().normalize() + ", injected: " + injectedfile.toAbsolutePath().normalize() + "]";
	}
	
	/**
	 * Number of times the mutation operator was applied.  0 if no mutation used.
	 * @return the number of times the mutation operator was applied.
	 */
	public int getMutationTimes() {
		return this.times;
	}
	
	/**
	 * The operator used to mutate.  Null if none used.
	 * @return the operator used to mutate.
	 */
	public Operator getMutationOperator() {
		return this.operator;
	}
}
