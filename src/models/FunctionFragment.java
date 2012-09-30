package models;

import java.nio.file.Path;

public class FunctionFragment extends Fragment {
	public FunctionFragment(Path srcfile, int startline, int endline) {
		super(srcfile, startline, endline, Fragment.TYPE_FUNCTION);
	}
}
