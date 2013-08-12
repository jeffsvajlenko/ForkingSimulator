package models;

import java.nio.file.Path;

public class DirectoryFileInjectionInstance {
	private boolean isRenamed;
	private boolean isMutated;
	private String operator;
	private int times;
	private int type;
	private Path original;
	private Path injected;
	private Path log;
	
	public String toString() {
		String s = "";
		char isRenamed = this.isRenamed ? 'R' : 'O';
		char isMutated = this.isMutated ? 'M' : 'O';
		s = s + isRenamed;
		if(this.isMutated) {
			s = s + " " + isMutated + " " + operator + " " + times + " " + type;
		} else {
			s = s + " " + isMutated;
		}
		s = s + " " + injected.toString();
		return s;
	}
	
	public DirectoryFileInjectionInstance getNormalized(Path path) {
		Path original = path.relativize(this.original);
		Path log = path.relativize(this.log);
		Path injected = path.relativize(this.injected);
		return new DirectoryFileInjectionInstance(isRenamed, isMutated, operator, times, type, original, injected, log);
	}
	
	public DirectoryFileInjectionInstance(boolean isRenamed, boolean isMutated, String operator, int times, int type, Path original, Path injected, Path log) {
		super();
		this.isRenamed = isRenamed;
		this.isMutated = isMutated;
		this.operator = operator;
		this.times = times;
		this.type = type;
		this.original = original;
		this.injected = injected;
		this.log = log;
	}

	public boolean isRenamed() {
		return isRenamed;
	}

	public boolean isMutated() {
		return isMutated;
	}

	public String getOperator() {
		return operator;
	}

	public int getTimes() {
		return times;
	}

	public int getType() {
		return type;
	}

	public Path getOriginal() {
		return original;
	}

	public Path getInjected() {
		return injected;
	}

	public Path getLog() {
		return log;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((injected == null) ? 0 : injected.hashCode());
		result = prime * result + (isMutated ? 1231 : 1237);
		result = prime * result + (isRenamed ? 1231 : 1237);
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result
				+ ((original == null) ? 0 : original.hashCode());
		result = prime * result + times;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectoryFileInjectionInstance other = (DirectoryFileInjectionInstance) obj;
		if (injected == null) {
			if (other.injected != null)
				return false;
		} else if (!injected.equals(other.injected))
			return false;
		if (isMutated != other.isMutated)
			return false;
		if (isRenamed != other.isRenamed)
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		if (times != other.times)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
