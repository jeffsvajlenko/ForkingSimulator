package models;
import java.nio.file.Path;


public class Fragment {
	Path srcfile;
	int startline;
	int endline;
	
	/**
	 * Constructs a Fragment.
	 * @param srcfile Source file containing the fragment.
	 * @param startline Startline of the fragment.
	 * @param endline Endline of the fragment.
	 * @param type Type of fragment.  Must be one of the TYPE_... constants from this class.
	 * @throws IllegalArgumentException if path is null, if startline > endline, or if type if invalid.
	 */
	public Fragment(Path srcfile, int startline, int endline) {
		if(srcfile == null) {
			throw new IllegalArgumentException("Srcfile is null.");
		}
		if(startline > endline) {
			throw new IllegalArgumentException("Startline > endline.");
		}
		
		this.srcfile = srcfile;
		this.startline = startline;
		this.endline = endline;
	}
		
	/**
	 * Returns the source file.
	 * @return the source file.
	 */
	public Path getSrcFile() {
		return this.srcfile;
	}
	
	/**
	 * Returns the start line.
	 * @return the start line.
	 */
	public int getStartLine() {
		return this.startline;
	}
	
	/**
	 * Returns the end line.
	 * @return the end line.
	 */
	public int getEndLine() {
		return this.endline;
	}
}
