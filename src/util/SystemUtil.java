package util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemUtil {
	public static Path getInstallRoot() {
		return Paths.get(".").toAbsolutePath().normalize();
	}
	
	public static Path getScriptsLocation() {
		return getInstallRoot().resolve("scripts").toAbsolutePath().normalize();
	}
	
	public static Path getTemporaryDirectory() {
		return getInstallRoot().resolve("tmp").toAbsolutePath().normalize();
	}
	
	public static Path getTxlDirectory() {
		return getInstallRoot().resolve("txl").toAbsolutePath().normalize();
	}
	
	public static Path getTxlDirectory(String language) {
		Path p = getTxlDirectory().resolve(language);
		if(Files.isDirectory(p)) {
			return p.toAbsolutePath().normalize();
		}
		else {
			return null;
		}
	}
}
