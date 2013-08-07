package models;
import java.util.Objects;


public class FunctionVariant extends Variant {
	
	//The original fragment
	private Fragment original;
	
	//The injected fragment
	private Fragment injected;
	
	//The operator (if any)
	private Operator operator;
	
	//Number times operator applied
	int times;
	
	/**
	 * Creates a fragment variant.
	 * @param original the original fragment.
	 * @param injected the injected fragment variant.
	 */
	public FunctionVariant(Fragment original, Fragment injected) {
		this(original, injected, null, 0);
	}
	
	/**
	 * Creates a fragment variant with mutation information.
	 * @param original the original fragment.
	 * @param injected the injected fragment.
	 * @param operator The operator used.
	 * @param times The number of times the operator was applied.
	 */
	public FunctionVariant(Fragment original, Fragment injected, Operator operator, int times) {
		super(Variant.FRAGMENT);
		Objects.requireNonNull(original, "Original fragment is null.");
		Objects.requireNonNull(injected, "Inejcted fragment is null.");
		this.original = original;
		this.injected = injected;
		this.operator = operator;
		this.times = times;
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
	
	public int getTimes() {
		return this.times;
	}
	
	public String toString() {
		return "FragmentVariant[original: (" + this.original.getSrcFile().toAbsolutePath().normalize() + "," + this.original.getStartLine() + "," + this.original.getEndLine() + ")," +
										 "(" + this.injected.getSrcFile().toAbsolutePath().normalize() + "," + this.injected.getStartLine() + "," + this.injected.getEndLine() + ")]";
	}
}
