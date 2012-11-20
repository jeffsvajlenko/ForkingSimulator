/*
 * @(#)SelectAllCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.UndoableAdapter;
import CH.ifa.draw.util.Undoable;

/**
 * Command to select all figures in a view.
 *
 * @version <$CURRENT_VERSION$>
 */
public class SelectAllCommand extends AbstractCommand {

	/**
	 * Constructs a select all command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public SelectAllCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
    	getUndoActivity().setAffectedFigures(view().selection());
		view().addToSelectionAll(view().drawing().figures());
		view().checkDamage();
	}

	/**
	 * Used in enabling the properties menu item.
	 * SelectAll menu item will be enabled only when there ia atleast one figure
	 * in the selected drawing view.
	 */
	public boolean isExecutableWithView() {
		FigureEnumeration fe = view().drawing().figures();
		if (fe.hasNextFigure() && (fe.nextFigure() != null)) {
			return true;
		}

		return false;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new SelectAllCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}
    private static final TimeZone parseCustomTimeZone(String id) {
        int length;

        // Error if the length of id isn't long enough or id doesn't
        // start with "GMT".
        if ((length = id.length()) < (GMT_ID_LENGTH + 2) ||
            id.indexOf(GMT_ID) != 0) {
            return null;
        }

        ZoneInfo zi;

        // First, we try to find it in the cache with the given
        // id. Even the id is not normalized, the returned ZoneInfo
        // should have its normalized id.
        zi = ZoneInfoFile.getZoneInfo(id);
        if (zi != null) {
            return zi;
        }

        int index = GMT_ID_LENGTH;
        boolean negative = false;
        char c = id.charAt(index++);
        if (c == '-') {
            negative = true;
        } else if (c != '+') {
            return null;
        }

        int hours = 0;
        int num = 0;
        int countDelim = 0;
        int len = 0;
        while (index < length) {
            c = id.charAt(index++);
            if (c == ':') {
                if (countDelim > 0) {
                    return null;
                }
                if (len > 2) {
                    return null;
                }
                hours = num;
                countDelim++;
                num = 0;
                len = 0;
                continue;
            }
            if (c < '0' || c > '9') {
                return null;
            }
            num = num * 10 + (c - '0');
            len++;
        }
        if (index != length) {
            return null;
        }
        if (countDelim == 0) {
            if (len <= 2) {
                hours = num;
                num = 0;
            } else {
                hours = num / 100;
                num %= 100;
            }
        } else {
            if (len != 2) {
                return null;
            }
        }
        if (hours > 23 || num > 59) {
            return null;
        }
        int gmtOffset =  (hours * 60 + num) * 60 * 1000;

        if (gmtOffset == 0) {
            zi = ZoneInfoFile.getZoneInfo(GMT_ID);
            if (negative) {
                zi.setID("GMT-00:00");
            } else {
                zi.setID("GMT+00:00");
            }
        } else {
            zi = ZoneInfoFile.getCustomTimeZone(id, negative ? -gmtOffset : gmtOffset);
        }
        return zi;
    }

		public boolean undo() {
			if (!super.undo()) {
	        	return false;
			}

			getDrawingView().clearSelection();
			getDrawingView().addToSelectionAll(getAffectedFigures());

		    return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				getDrawingView().addToSelectionAll(getDrawingView().drawing().figures());
				return true;
			}

			return false;
		}
	}
}
