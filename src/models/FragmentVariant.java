package models;
import java.util.Objects;


public class FragmentVariant extends Variant {
	
	//The original fragment
	private Fragment original;
	
	//The injected fragment
	private Fragment injected;
	
	//The operator (if any)
	private Operator operator;
	
	/**
	 * Creates a fragment variant.
	 * @param original the original fragment.
	 * @param injected the injected fragment variant.
	 */
	public FragmentVariant(Fragment original, Fragment injected, Operator operator) {
		super(Variant.FRAGMENT);
		Objects.requireNonNull(original, "Original fragment is null.");
		Objects.requireNonNull(injected, "Inejcted fragment is null.");
		this.original = original;
		this.injected = injected;
		this.operator = operator;
	}
	
	/**
	 * Returns the original fragment.
	 * @return the original fragment.
	 */
	public Fragment getOriginalFragment() {
		return this.original;
	}
	
	/**
	 * Returns the injected fragment.
	 * @return the injected fragment.
	 */
	public Fragment getInjectedFragment() {
		return this.injected;
	}

	/**
	 * Returns the operator used to mutate the injected fragment, if any.
	 * @return the operator used, or null if none used.
	 */
	public Operator getOperator() {
		return operator;
	}
	
	public String toString() {
		return "FragmentVariant[original: (" + this.original.getSrcFile().toAbsolutePath().normalize() + "," + this.original.getStartLine() + "," + this.original.getEndLine() + ")," +
										 "(" + this.injected.getSrcFile().toAbsolutePath().normalize() + "," + this.injected.getStartLine() + "," + this.injected.getEndLine() + ")]";
	}
}
