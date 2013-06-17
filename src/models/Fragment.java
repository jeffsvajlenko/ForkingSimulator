package models;
import java.nio.file.Path;

import org.apache.commons.lang3.builder.HashCodeBuilder;


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
	
	@Override
	/**
	 * Returns if this object is equal to the specified object.  To be equal, the other object must be an 'instanceof' Fragment, and have
	 * the same source file and source coordinates as this fragment.
	 * @param o The object to compare to.
	 * @returns
	 */
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Fragment)) {
			return false;
		} else {
			Fragment other = (Fragment)o;
			if(this.srcfile.equals(other.srcfile) && this.startline == other.startline && this.endline == other.endline) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	/**
	 * Returns a hashcode for this fragment, generated using HashCodeBuilder of Appache Commons Lang.
	 * @return a hashcode for this fragment.
	 */
	public int hashCode() {
		return new HashCodeBuilder().append(this.srcfile).append(this.startline).append(this.endline).toHashCode();
	}
}
