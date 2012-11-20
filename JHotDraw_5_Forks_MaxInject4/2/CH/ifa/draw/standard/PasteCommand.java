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
        TaskBar() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0) {
                public void layoutContainer(Container target) {
                    // First shrink buttons to fit
                    Component[] comps = target.getComponents();
                    int n = comps.length;
                    if (n > 0) {
                        // Start with the largest preferred width
                        int prefWidth = 0;
                        for (Component c : comps) {
                            c.setPreferredSize(null);
                            Dimension prefSize = c.getPreferredSize();
                            if (prefSize.width > prefWidth) {
                                prefWidth = prefSize.width;
                            }
                        }
                        // Shrink equally to fit if needed
                        Insets insets = target.getInsets();
                        int tw = target.getWidth() - insets.left - insets.right;
                        int w = Math.min(prefWidth, Math.max(10, tw/n));
                        for (Component c :  comps) {
                            Dimension prefSize = c.getPreferredSize();
                            c.setPreferredSize(new Dimension(w, prefSize.height));
                        }
                    }
                    super.layoutContainer(target);
                }
            });

            // PENDING: This should be handled by the painter
            setBorder(new BevelBorder(BevelBorder.RAISED) {
                protected void paintRaisedBevel(Component c, Graphics g,
                                                int x, int y, int w, int h)  {
                    Color oldColor = g.getColor();
                    g.translate(x, y);
                    g.setColor(getHighlightOuterColor(c));
                    g.drawLine(0, 0, 0, h-2);
                    g.drawLine(1, 0, w-2, 0);
                    g.setColor(getShadowOuterColor(c));
                    g.drawLine(0, h-1, w-1, h-1);
                    g.drawLine(w-1, 0, w-1, h-2);
                    g.translate(-x, -y);
                    g.setColor(oldColor);
                }
            });
        }

	public boolean isExecutableWithView() {
		return Clipboard.getClipboard().getContents() != null;
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
