package models;
import java.nio.file.Paths;
import java.util.HashSet;

import org.apache.commons.lang3.builder.HashCodeBuilder;


public class CloneClass {
	HashSet<Fragment> fragments;
	
	public CloneClass() {
		fragments = new HashSet<Fragment>();
	}
	
	public void addFragment(Fragment fragment) {
		fragments.add(fragment);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(fragments).hashCode();
	}
	
	public boolean subsumes(CloneClass other) {
		for(Fragment fragment : other.fragments) {
			if(!this.fragments.contains(fragment)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CloneClass other = (CloneClass) obj;
		if (fragments == null) {
			if (other.fragments != null)
				return false;
		} else if (!fragments.equals(other.fragments))
			return false;
		return true;
	}
	
	public static void main(String args[]) {
		Fragment f1 = new Fragment(Paths.get("file"), 1, 10);
		Fragment f2 = new Fragment(Paths.get("file"), 1, 10);
		Fragment f3 = new Fragment(Paths.get("file1"), 1, 10);
		Fragment f4 = new Fragment(Paths.get("file"), 2, 10);
		Fragment f5 = new Fragment(Paths.get("file"), 1, 12);
		
		CloneClass c1 = new CloneClass();
			c1.addFragment(f1);
			c1.addFragment(f2);
			c1.addFragment(f3);
			c1.addFragment(f4);
			c1.addFragment(f5);
		CloneClass c2 = new CloneClass();
			c2.addFragment(f5);
			c2.addFragment(f4);
			c2.addFragment(f3);
			c2.addFragment(f2);
			c2.addFragment(f1);
		CloneClass c3 = new CloneClass();
			c3.addFragment(f1);
			c3.addFragment(f3);
			c3.addFragment(f5);
		
		assert(c1.equals(c1));
		assert(c1.equals(c2));
		assert(!c1.equals(c3));
		
		assert(c1.hashCode() == c1.hashCode());
		assert(c1.hashCode() == c2.hashCode());
		assert(c1.hashCode() != c3.hashCode());
	}
	
}
