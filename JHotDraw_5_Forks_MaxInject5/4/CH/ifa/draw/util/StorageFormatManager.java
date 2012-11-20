/*
 * @(#)StorageFormatManager.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
 
package CH.ifa.draw.util;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.util.List;
import java.util.Iterator;

/**
 * The StorageFormatManager is a contains StorageFormats.
 * It is not a Singleton because it could be necessary to deal with different
 * format managers, e.g. one for importing Drawings, one for exporting Drawings.
 * If one StorageFormat matches the file extension of the Drawing file, then this
 * StorageFormat can be used to store or restore the Drawing.
 *
 * @see StorageFormat
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class StorageFormatManager {

	/**
	 * List containing all registered storage formats
	 */
	private List myStorageFormats;
	
	/**
	 * Default storage format that should be selected in a javax.swing.JFileChooser
	 */
	private StorageFormat myDefaultStorageFormat;
	
	/**
	 * Create a new StorageFormatManager.
	 */
	public StorageFormatManager() {
		myStorageFormats = CollectionsFactory.current().createList();
	}
	
	/**
	 * Add a StorageFormat that should be supported by this StorageFormatManager.
	 *
	 * @param newStorageFormat new StorageFormat to be supported
	 */
	public void addStorageFormat(StorageFormat newStorageFormat) {
		myStorageFormats.add(newStorageFormat);
	}

	/**
	 * Remove a StorageFormat that should no longer be supported by this StorageFormatManager.
	 * The StorageFormat is excluded in when search for a StorageFormat.
	 *
	 * @param oldStorageFormat old StorageFormat no longer to be supported
	 */
	public void removeStorageFormat(StorageFormat oldStorageFormat) {
		myStorageFormats.remove(oldStorageFormat);
	}
	
	/**
	 * Test, whether a StorageFormat is supported by this StorageFormat
	 */
	public boolean containsStorageFormat(StorageFormat checkStorageFormat){
		return myStorageFormats.contains(checkStorageFormat);
	}
	
	/**
	 * Set a StorageFormat as the default storage format which is selected in a
	 * javax.swing.JFileChooser. The default storage format must be already
	 * added with addStorageFormat. Setting the default storage format to null
	 * does not automatically remove the StorageFormat from the list of
	 * supported StorageFormats.
	 *
	 * @param newDefaultStorageFormat StorageFormat that should be selected in a JFileChooser
	 */
	public void setDefaultStorageFormat(StorageFormat newDefaultStorageFormat) {
		myDefaultStorageFormat = newDefaultStorageFormat;
	}
	
	/**
	 * Return the StorageFormat which is used as selected file format in a javax.swing.JFileChooser
	 *
	 * @return default storage format
	 */
	public StorageFormat getDefaultStorageFormat() {
		return myDefaultStorageFormat;
	}
    private boolean typeAheadAssertions(Component target, AWTEvent e) {

        // Clear any pending events here as well as in the FOCUS_GAINED
        // handler. We need this call here in case a marker was removed in
        // response to a call to dequeueKeyEvents.
        pumpApprovedKeyEvents();

        switch (e.getID()) {
            case KeyEvent.KEY_TYPED:
            case KeyEvent.KEY_PRESSED:
            case KeyEvent.KEY_RELEASED: {
                KeyEvent ke = (KeyEvent)e;
                synchronized (this) {
                    if (e.isPosted && typeAheadMarkers.size() != 0) {
                        TypeAheadMarker marker = (TypeAheadMarker)
                            typeAheadMarkers.getFirst();
                        // Fixed 5064013: may appears that the events have the same time
                        // if (ke.getWhen() >= marker.after) {
                        // The fix is rolled out.

                        if (ke.getWhen() > marker.after) {
                            focusLog.finer("Storing event {0} because of marker {1}", ke, marker);
                            enqueuedKeyEvents.addLast(ke);
                            return true;
                        }
                    }
                }

                // KeyEvent was posted before focus change request
                return preDispatchKeyEvent(ke);
            }

            case FocusEvent.FOCUS_GAINED:
                focusLog.finest("Markers before FOCUS_GAINED on {0}", target);
                dumpMarkers();
                // Search the marker list for the first marker tied to
                // the Component which just gained focus. Then remove
                // that marker, any markers which immediately follow
                // and are tied to the same component, and all markers
                // that preceed it. This handles the case where
                // multiple focus requests were made for the same
                // Component in a row and when we lost some of the
                // earlier requests. Since FOCUS_GAINED events will
                // not be generated for these additional requests, we
                // need to clear those markers too.
                synchronized (this) {
                    boolean found = false;
                    if (hasMarker(target)) {
                        for (Iterator iter = typeAheadMarkers.iterator();
                             iter.hasNext(); )
                        {
                            if (((TypeAheadMarker)iter.next()).untilFocused ==
                                target)
                            {
                                 found = true;
                            } else if (found) {
                                break;
                            }
                            iter.remove();
                        }
                    } else {
                        // Exception condition - event without marker
                        focusLog.finer("Event without marker {0}", e);
                    }
                }
                focusLog.finest("Markers after FOCUS_GAINED");
                dumpMarkers();

                redispatchEvent(target, e);

                // Now, dispatch any pending KeyEvents which have been
                // released because of the FOCUS_GAINED event so that we don't
                // have to wait for another event to be posted to the queue.
                pumpApprovedKeyEvents();
                return true;

            default:
                redispatchEvent(target, e);
                return true;
        }
    }
	
	/**
	 * Register all FileFilters supported by StorageFormats
	 *
	 * @param fileChooser javax.swing.JFileChooser to which FileFilters are added
	 */
	public void registerFileFilters(JFileChooser fileChooser) {
		Iterator formatsIterator = myStorageFormats.iterator();
		while (formatsIterator.hasNext()) {
			fileChooser.addChoosableFileFilter(((StorageFormat)formatsIterator.next()).getFileFilter());
		}

		// set a current activated file filter if a default storage Format has been defined
		if (getDefaultStorageFormat() != null) {
			fileChooser.setFileFilter(getDefaultStorageFormat().getFileFilter());
		}
	}

	/**
	 * Find a StorageFormat that can be used according to a FileFilter to store a Drawing
	 * in a file or restore it from a file respectively.
	 *
	 * @param findFileFilter FileFilter used to identify a StorageFormat
	 * @return StorageFormat, if a matching file extension could be found, false otherwise
	 */
	public StorageFormat findStorageFormat(FileFilter findFileFilter) {
		Iterator formatsIterator = myStorageFormats.iterator();
		StorageFormat currentStorageFormat = null;
		while (formatsIterator.hasNext()) {
			currentStorageFormat = (StorageFormat)formatsIterator.next();
			if (currentStorageFormat.getFileFilter().equals(findFileFilter)) {
				return currentStorageFormat;
			}
		}
		
		return null;
	}
}
