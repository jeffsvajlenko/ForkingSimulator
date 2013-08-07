package models;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FunctionInjection {
	private int num;
	private boolean isUniform;
	private Fragment original;
	private Path log;
	private List<FunctionInjectionInstance> instances;
	
	public FunctionInjection(int num, boolean isUniform, Fragment original,
			Path log, List<FunctionInjectionInstance> instances) {
		super();
		this.num = num;
		this.isUniform = isUniform;
		this.original = original;
		this.log = log;
		this.instances = Collections.unmodifiableList(new LinkedList<FunctionInjectionInstance>(instances));
	}

	public int getNum() {
		return num;
	}

	public boolean isUniform() {
		return isUniform;
	}

	public Fragment getOriginal() {
		return original;
	}

	public Path getLog() {
		return log;
	}

	public List<FunctionInjectionInstance> getInstances() {
		return instances;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((instances == null) ? 0 : instances.hashCode());
		result = prime * result + (isUniform ? 1231 : 1237);
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		result = prime * result + num;
		result = prime * result
				+ ((original == null) ? 0 : original.hashCode());
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
		FunctionInjection other = (FunctionInjection) obj;
		if (instances == null) {
			if (other.instances != null)
				return false;
		} else if (!instances.equals(other.instances))
			return false;
		if (isUniform != other.isUniform)
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		if (num != other.num)
			return false;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FunctionInjection [num=" + num + ", isUniform=" + isUniform
				+ ", original=" + original + ", log=" + log + ", instances="
				+ instances + "]";
	}
	
}
