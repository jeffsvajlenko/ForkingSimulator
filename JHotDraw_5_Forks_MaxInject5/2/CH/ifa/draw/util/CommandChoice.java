/*
 * @(#)CommandChoice.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

/**
 * A Command enabled choice. Selecting a choice executes the
 * corresponding command.
 *
 * @see Command
 *
 * @version <$CURRENT_VERSION$>
 */
public  class CommandChoice extends JComboBox implements ItemListener {

	private List   fCommands;

	public CommandChoice() {
		super();
		fCommands = CollectionsFactory.current().createList(10);
		addItemListener(this);
	}

	/**
	 * Adds a command to the menu.
	 */
	public synchronized void addItem(Command command) {
		addItem(command.name());
		fCommands.add(command);
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
	 * Executes the command.
	 */
	public void itemStateChanged(ItemEvent e) {
		if ((getSelectedIndex() >= 0) && (getSelectedIndex() < fCommands.size())) {
			Command command = (Command)fCommands.get(getSelectedIndex());
            if (command.isExecutable()) {
				command.execute();
			}
		}
	}
}
