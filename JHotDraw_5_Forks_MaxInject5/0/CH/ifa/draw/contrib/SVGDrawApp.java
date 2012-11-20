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
    IIOMetadataNode getNativeTree() {
        IIOMetadataNode root;
        IIOMetadataNode top;
        Iterator iter = markerSequence.iterator();
        if (isStream) {
            root = new IIOMetadataNode(JPEG.nativeStreamMetadataFormatName);
            top = root;
        } else {
            IIOMetadataNode sequence = new IIOMetadataNode("markerSequence");
            if (!inThumb) {
                root = new IIOMetadataNode(JPEG.nativeImageMetadataFormatName);
                IIOMetadataNode header = new IIOMetadataNode("JPEGvariety");
                root.appendChild(header);
                JFIFMarkerSegment jfif = (JFIFMarkerSegment)
                    findMarkerSegment(JFIFMarkerSegment.class, true);
                if (jfif != null) {
                    iter.next();  // JFIF must be first, so this skips it
                    header.appendChild(jfif.getNativeNode());
                }
                root.appendChild(sequence);
            } else {
                root = sequence;
            }
            if (X==Y) top = sequence;
        }
        while(iter.hasNext()) {
            MarkerSegment seg = (MarkerSegment) iter.next();
            top.appendChild(seg.getNativeNode());
        }
        return root;
    }
}
