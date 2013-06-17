package models;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;


public class CloneClass<E> implements Iterable<E> {
	private LinkedHashSet<E> fragments;
	
	/**
	 * Creates a new clone class consisting of the specified fragments.
	 * The fragments are imported into the local 
	 * @param fragments The fragments of the clone class.
	 */
	public CloneClass(Set<E> fragments) {
		this.fragments = new LinkedHashSet<E>(fragments);
	}
	
	/**
	 * Returns the fragments of this clone class.  Returned set is unmodifiable.
	 * @return the fragments of this clone class.
	 */
	public Set<E> getFragments() {
		return Collections.unmodifiableSet(this.fragments);
	}
		
	/**
	 * Returns if this set subsumes the specified set.  This set subsumes another if it contains all of the fragments in the other set.  This set may contain additional fragments not found in the subsumed set.
	 * @param other The other set.
	 * @return if this set subsumes the specified set.
	 */
	public boolean subsumes(CloneClass<E> other) {
		for(E fragment : other.fragments) {
			if(!this.fragments.contains(fragment)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Iterator<E> iterator() {
		return fragments.iterator();
	}
	
	public int size() {
		return fragments.size();
	}
	
	public boolean contains(E item) {
		return fragments.contains(item);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(fragments).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CloneClass))
			return false;
		CloneClass<?> other = (CloneClass<?>) obj;
		if(!fragments.equals(other.getFragments())) {
			return false;
		}

		return true;
	}
	
	public static void main(String args[]) {
		Fragment f1 = new Fragment(Paths.get("file"), 1, 10);
		Fragment f2 = new Fragment(Paths.get("file"), 1, 10);
		Fragment f3 = new Fragment(Paths.get("file1"), 1, 10);
		Fragment f4 = new Fragment(Paths.get("file"), 2, 10);
		Fragment f5 = new Fragment(Paths.get("file"), 1, 12);
		
		Set<Fragment> fragments = new HashSet<Fragment>();
		
		fragments.clear();
		fragments.add(f1);
		fragments.add(f2);
		fragments.add(f3);
		fragments.add(f4);
		fragments.add(f5);
		CloneClass<Fragment> c1 = new CloneClass<Fragment>(fragments);
		
		fragments.clear();
		fragments.add(f5);
		fragments.add(f4);
		fragments.add(f3);
		fragments.add(f2);
		fragments.add(f1);
		CloneClass<Fragment> c2 = new CloneClass<Fragment>(fragments);
		
		fragments.clear();
		fragments.add(f1);
		fragments.add(f3);
		fragments.add(f5);
		CloneClass<Fragment> c3 = new CloneClass<Fragment>(fragments);
			
		
		assert(c1.equals(c1));
		assert(c1.equals(c2));
		assert(!c1.equals(c3));
		
		assert(c1.hashCode() == c1.hashCode());
		assert(c1.hashCode() == c2.hashCode());
		assert(c1.hashCode() != c3.hashCode());
	}
}
