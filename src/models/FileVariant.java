package models;
import java.nio.file.Path;
import java.util.Objects;


public class FileVariant extends Variant {

	Path originalfile;
	Path injectedfile;
	
	/**
	 * Creates a file variant.
	 * @param osrcfile The original source of the file.
	 * @param isrcfile The location of the injected variant.
	 */
	public FileVariant(Path originalfile, Path injectedfile) {
		super(Variant.FILE);
		Objects.requireNonNull(originalfile, "Original file is null.");
		Objects.requireNonNull(injectedfile, "Injected file is null.");
		this.originalfile = originalfile;
		this.injectedfile = injectedfile;
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
}
