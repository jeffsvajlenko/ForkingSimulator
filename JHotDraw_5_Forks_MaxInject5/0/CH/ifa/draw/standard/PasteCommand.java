/*
 * @(#)PasteCommand.java
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
import CH.ifa.draw.util.*;
import java.awt.*;

/**
 * Command to insert the clipboard into the drawing.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
public class PasteCommand extends FigureTransferCommand {

	/**
	 * Constructs a paste command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public PasteCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		Point lastClick = view().lastClick();
		FigureSelection selection = (FigureSelection)Clipboard.getClipboard().getContents();
		if (selection != null) {
			setUndoActivity(createUndoActivity());
			getUndoActivity().setAffectedFigures(
				(FigureEnumerator)selection.getData(StandardFigureSelection.TYPE));

			if (!getUndoActivity().getAffectedFigures().hasNextFigure()) {
				setUndoActivity(null);
				return;
			}

			Rectangle r = getBounds(getUndoActivity().getAffectedFigures());
			view().clearSelection();

			// get an enumeration of inserted figures
			FigureEnumeration fe = insertFigures(getUndoActivity().getAffectedFigures(), lastClick.x-r.x, lastClick.y-r.y);
			getUndoActivity().setAffectedFigures(fe);

			view().checkDamage();
		}
	}

	public boolean isExecutableWithView() {
		return Clipboard.getClipboard().getContents() != null;
	}
        static void parseShorthandBackground(CSS css, String value,
                                             MutableAttributeSet attr) {
            String[] strings = parseStrings(value);
            int count = strings.length;
            int index = 0;
            // bitmask: 0 for image, 1 repeat, 2 attachment, 3 position,
            //          4 color
            short found = 0;

            while (index < count) {
                String string = strings[index++];
                if ((found & 1) == 0 && isImage(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_IMAGE, string);
                    found |= 1;
                }
                else if ((found & 2) == 0 && isRepeat(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_REPEAT, string);
                    found |= 2;
                }
                else if ((found & 4) == 0 && isAttachment(string)) {
                    X1.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_ATTACHMENT, string);
                    found |= 4;
                }
                else if ((found & 8) == 0 && isPosition(string)) {
                    if (index < count && isPosition(strings[index])) {
                        css.addInternalCSSValue(attr, CSS.Attribute.
                                                BACKGROUND_POSITION,
                                                string + " " +
                                                strings[index++]);
                    }
                    else {
                        css.addInternalCSSValue(attr, CSS.Attribute.
                                                BACKGROUND_POSITION, string);
                    }
                    found |= 8;
                }
                else if ((found & 16) == 0 && isColor(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_COLOR, string);
                    found |= 16;
                }
            }
            if ((found & 1) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_IMAGE,
                                        null);
            }
            if ((found & 2) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_REPEAT,
                                        "repeat");
            }
            if ((found & 4) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.
                                        BACKGROUND_ATTACHMENT, "scroll");
            }
            if ((found & 8) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.
                                        BACKGROUND_POSITION, null);
            }
            // Currently, there is no good way to express this.
            /*
            if ((found & 16) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_COLOR,
                                        null);
            }
            */
        }

	private Rectangle getBounds(FigureEnumeration fe) {
		Rectangle r = fe.nextFigure().displayBox();
		while (fe.hasNextFigure()) {
			r.add(fe.nextFigure().displayBox());
		}
		return r;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {

		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			setUndoable(true);
			setRedoable(true);
		}

		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			DeleteFromDrawingVisitor deleteVisitor = new DeleteFromDrawingVisitor(getDrawingView().drawing());
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
	    		fe.nextFigure().visit(deleteVisitor);
			}

			getDrawingView().clearSelection();

			return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}

			getDrawingView().clearSelection();
			setAffectedFigures(getDrawingView().insertFigures(
				getAffectedFigures(), 0, 0, false));

			return true;
		}
	}
}
