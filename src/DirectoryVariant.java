import java.nio.file.Path;
import java.util.Objects;


public class DirectoryVariant extends Variant {
	private Path originaldirectory;
	private Path injecteddirectory;
	
	/**
	 * Creates a directory variant.
	 * @param originaldirectory The original source of the directory.
	 * @param injecteddirectory The location of the injected directory variant.
	 */
	public DirectoryVariant(Path originaldirectory, Path injecteddirectory) {
		super(Variant.DIRECTORY);
		Objects.requireNonNull(originaldirectory, "Original directory is null.");
		Objects.requireNonNull(injecteddirectory, "Injected directory is null.");
		this.originaldirectory = originaldirectory;
		this.injecteddirectory = injecteddirectory;
	}
	
	/**
	 * Returns a path to the original directory.
	 * @return a path to the original directory.
	 */
	public Path getOriginalDirectory() {
		return this.originaldirectory;
	}
	
	/**
	 * Returns a path to the injected directory variant.
	 * @return a path to the injected directory variant.
	 */
	public Path getInjectedDirectory() {
		return this.injecteddirectory;
	}
	
	public String toString() {
		return "DirectoryVariant[original: " + this.originaldirectory.toAbsolutePath().normalize() + ", injected: " + this.injecteddirectory.toAbsolutePath().normalize() + "]";
	}
	
}
