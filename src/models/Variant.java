package models;

public abstract class Variant {
	//The variants type
	private int type;
	
	/**
	 * Signifies a file variant.
	 */
	public static final int FILE = 1;
	
	/**
	 * Signifies a directory variant.
	 */
	public static final int DIRECTORY = 2;
	
	/**
	 * Signifies a fragment variant.
	 */
	public static final int FRAGMENT = 3;

	/**
	 * Constructs a Variant of the specified variant type.
	 * @param type the type of the variant.
	 */
	public Variant(int type) {
		this.type = type;
	}
	
	/**
	 * Returns the variant's type.  Refer to constants in Variant for meaning.
	 * @return the variant's type.
	 */
	public int getType() {
		return this.type;
	}
}
