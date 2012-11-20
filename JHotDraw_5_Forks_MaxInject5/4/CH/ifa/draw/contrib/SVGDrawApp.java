package CH.ifa.draw.contrib;

import CH.ifa.draw.application.DrawApplication;
import CH.ifa.draw.samples.javadraw.JavaDrawApp;
import CH.ifa.draw.util.StorageFormatManager;

/**
 * @author mtnygard
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SVGDrawApp extends JavaDrawApp {
	public static void main(String[] args) {
		SVGDrawApp window = new SVGDrawApp();
		window.open();
	}
	
	public SVGDrawApp() {
		super("JHotDraw");
	}

	/**
	 * Factory method which create a new instance of this
	 * application.
	 *
	 * @return	newly created application
	 */
	protected DrawApplication createApplication() {
		return new SVGDrawApp();
	}
    private Path2D decodePath38() {
        path.reset();
        path.moveTo(decodeX(0.1826087f), decodeY(2.7217393f));
        path.lineTo(decodeX(0.2826087f), decodeY(2.8217392f));
        path.lineTo(decodeX(1.0181159f), decodeY(2.095652f));
        path.lineTo(decodeX(0.9130435f), decodeY(1.9891305f));
        path.lineTo(decodeX(0.1826087f), decodeY(2.7217393f));
        path.closePath();
        return path;
    }

	/**
	 * Factory method to create a StorageFormatManager for supported storage formats.
	 * Different applications might want to use different storage formats and can return
	 * their own format manager by overriding this method.
	 * 
	 * TODO: Read storage formats from a config file.
	 */
	public StorageFormatManager createStorageFormatManager() {
		StorageFormatManager storageFormatManager = new StorageFormatManager();
		SVGStorageFormat format = new SVGStorageFormat();
		storageFormatManager.addStorageFormat(format);
		storageFormatManager.setDefaultStorageFormat(format);
		return storageFormatManager;
	}
}
