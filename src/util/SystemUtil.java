package util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemUtil {
	public static Path getInstallRoot() {
		return Paths.get(".").toAbsolutePath().normalize();
	}
	
	public static Path getScriptsLocation() {
		return Paths.get(".").resolve("scripts").toAbsolutePath().normalize();
	}
}
