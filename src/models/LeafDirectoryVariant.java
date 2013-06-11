package models;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;


public class LeafDirectoryVariant extends Variant {
	private Path originaldirectory;
	private Path injecteddirectory;
	
	private List<FileVariant> fileVariants;
	
	public LeafDirectoryVariant(Path originaldirectory, Path injecteddirectory, List<FileVariant> fileVariants) {
		super(Variant.DIRECTORY);
		Objects.requireNonNull(originaldirectory, "Original directory is null.");
		Objects.requireNonNull(injecteddirectory, "Injected directory is null.");
		Objects.requireNonNull(fileVariants, "File variants are null.");
		this.originaldirectory = originaldirectory;
		this.injecteddirectory = injecteddirectory;
		this.fileVariants = fileVariants;
	}
	
	/**
	 * Returns if the directory was renamed on injection.
	 * @return if the directory was renamed on injection.
	 */
	public boolean isRenamed() {
		if(this.originaldirectory.getFileName().equals(injecteddirectory.getFileName())) {
			return false;
		} else {
			return true;
		}
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
	
	/**
	 * Returns the file variants created by this directory variant.
	 * @return the file variants created by this directory variant.
	 */
	public List<FileVariant> getFileVariants() {
		return this.fileVariants;
	}
}
