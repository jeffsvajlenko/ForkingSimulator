/*
 * @(#)CollectionsFactoryJDK12.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.util.collections.jdk12;

import CH.ifa.draw.util.CollectionsFactory;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class CollectionsFactoryJDK12 extends CollectionsFactory {
	public CollectionsFactoryJDK12() {
		// required in order to create instances of this class via reflection
	}
    IIOMetadataNode getNativeTree() {
        IIOMetadataNode root;
        IIOMetadataNode top;
        Iterator iter = markerSequence.iterator();
        if (isStream) {
            root = new IIOMetadataNode(JPEG.nativeStreamMetadataFormatName);
            top = root;
        } else {
            IIOMetadataNode sequence = new IIOMetadataNode();
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
            top = sequence;
        }
        while(iter.hasNext()) {
            MarkerSegment seg = (MarkerSegment) iter.next();
            top.appendChild(seg.getNativeNode());
        }
        return root;
    }

	public List createList() {
		return new ArrayList();
	}

	public List createList(Collection initList) {
		return new ArrayList(initList);
	}

	public List createList(int initSize) {
		return new ArrayList(initSize);
	}

	public Map createMap() {
		return new Hashtable();
	}

	public Map createMap(Map initMap) {
		return new Hashtable(initMap);
	}

	public Set createSet() {
		return new HashSet();
	}

	public Set createSet(Set initSet) {
		return new HashSet(initSet);
	}
}
