package models;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DirectoryInjectionInstance {

	private int forknum;
	private boolean isRenamed;
	private Path injected;
	private Path log;
	private List<DirectoryFileInjectionInstance> instances;
	
	public DirectoryInjectionInstance getNormalized(Path path) {
		Path injected = path.relativize(this.injected);
		Path log = path.relativize(this.log);
		List<DirectoryFileInjectionInstance> newInstances = new LinkedList<DirectoryFileInjectionInstance>();
		for(DirectoryFileInjectionInstance dfii : instances) {
			newInstances.add(dfii.getNormalized(path));
		}
		return new DirectoryInjectionInstance(forknum, isRenamed, injected, log, newInstances);
	}
	
	public String toString() {
		String s = "";
		char isRenamed = this.isRenamed ? 'R' : 'O';
		s = s + forknum + " " + isRenamed + " " + instances.size() + " " + injected.toString();
		for(DirectoryFileInjectionInstance dfii : instances) {
			s = s + "\n\t" + dfii.toString();
		}
		return s;
	}
	
	public DirectoryInjectionInstance(int forknum, boolean isRenamed, Path injected, Path log, List<DirectoryFileInjectionInstance> instances) {
		super();
		this.forknum = forknum;
		this.isRenamed = isRenamed;
		this.injected = injected;
		this.log = log;
		this.instances = Collections.unmodifiableList(new LinkedList<DirectoryFileInjectionInstance>(instances));
	}

	public int getForknum() {
		return forknum;
	}

	public boolean isRenamed() {
		return isRenamed;
	}

	public Path getInjected() {
		return injected;
	}

	public Path getLog() {
		return log;
	}

	public List<DirectoryFileInjectionInstance> getInstances() {
		return instances;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + forknum;
		result = prime * result
				+ ((injected == null) ? 0 : injected.hashCode());
		result = prime * result
				+ ((instances == null) ? 0 : instances.hashCode());
		result = prime * result + (isRenamed ? 1231 : 1237);
		result = prime * result + ((log == null) ? 0 : log.hashCode());
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
		DirectoryInjectionInstance other = (DirectoryInjectionInstance) obj;
		if (forknum != other.forknum)
			return false;
		if (injected == null) {
			if (other.injected != null)
				return false;
		} else if (!injected.equals(other.injected))
			return false;
		if (instances == null) {
			if (other.instances != null)
				return false;
		} else if (!instances.equals(other.instances))
			return false;
		if (isRenamed != other.isRenamed)
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}
}
