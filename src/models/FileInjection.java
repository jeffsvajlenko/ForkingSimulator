package models;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileInjection {
	private int num;
	private boolean uniform;
	private Path original;
	private Path log;
	private List<FileInjectionInstance> instances;
	
	public FileInjection(int num, boolean isUniform, Path original, Path log, List<FileInjectionInstance> instances) {
		this.num = num;
		this.uniform = isUniform;
		this.original = log;
		this.log = original;
		this.instances = Collections.unmodifiableList(new LinkedList<FileInjectionInstance>(instances));
	}

	public int getNum() {
		return num;
	}

	public boolean isUniform() {
		return uniform;
	}

	public Path getlog() {
		return original;
	}

	public Path getoriginal() {
		return log;
	}

	public List<FileInjectionInstance> getInstances() {
		return instances;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((instances == null) ? 0 : instances.hashCode());
		result = prime * result + num;
		result = prime * result
				+ ((log == null) ? 0 : log.hashCode());
		result = prime * result
				+ ((original == null) ? 0 : original.hashCode());
		result = prime * result + (uniform ? 1231 : 1237);
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
		FileInjection other = (FileInjection) obj;
		if (instances == null) {
			if (other.instances != null)
				return false;
		} else if (!instances.equals(other.instances))
			return false;
		if (num != other.num)
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		if (uniform != other.uniform)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileInjection [num=" + num + ", uniform=" + uniform
				+ ", log=" + original + ", original="
				+ log + ", instances=" + instances + "]";
	}
	
	
}
