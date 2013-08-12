package models;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DirectoryInjection {
	
	private int num;
	private boolean isUniform;
	private Path original;
	private Path log;
	private List<DirectoryInjectionInstance> instances;
	
	public DirectoryInjection getNormalized(Path path) {
		Path original = path.relativize(this.original);
		Path log = path.relativize(this.log);
		List<DirectoryInjectionInstance> newInstances = new LinkedList<DirectoryInjectionInstance>();
		for(DirectoryInjectionInstance dii : instances) {
			newInstances.add(dii.getNormalized(path));
		}
		return new DirectoryInjection(num, isUniform, original, log, newInstances);
	}
	
	@Override
	public String toString() {
		String s = "";
		char isUniform = this.isUniform ? 'U' : 'V';
		s = s + num + " " + isUniform + " " + instances.size() + " " + original.toString();
		for(DirectoryInjectionInstance dii : instances) {
			s = s + "\n\t" + dii.toString().replaceAll("\t", "\t\t");
		}
		return s;
	}
	
	public DirectoryInjection(int num, boolean isUniform, Path original,
			Path log, List<DirectoryInjectionInstance> instances) {
		super();
		this.num = num;
		this.isUniform = isUniform;
		this.original = original;
		this.log = log;
		this.instances = Collections.unmodifiableList(new LinkedList<DirectoryInjectionInstance>(instances));
	}

	public int getNum() {
		return num;
	}

	public boolean isUniform() {
		return isUniform;
	}

	public Path getOriginal() {
		return original;
	}

	public Path getLog() {
		return log;
	}

	public List<DirectoryInjectionInstance> getInstances() {
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
		DirectoryInjection other = (DirectoryInjection) obj;
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
}
