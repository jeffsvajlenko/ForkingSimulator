package models;

import java.nio.file.Path;

public class FunctionFragment extends Fragment {
	public FunctionFragment(Path srcfile, int startline, int endline) {
		super(srcfile, startline, endline, Fragment.TYPE_FUNCTION);
	}
	
	public String toString() {
		return "FunctionFragment(" + this.srcfile.toString() + "," + this.startline + "," + this.endline + ")";
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof FunctionFragment)) {
			return false;
		} else {
			FunctionFragment other = (FunctionFragment)o;
			if(this.srcfile.equals(other.srcfile) && this.startline == other.startline && this.endline == other.endline) {
				return true;
			} else {
				return false;
			}
		}
	}
}
