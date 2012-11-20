/*
 * @(#)DeleteFromDrawingVisitor.java
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
import CH.ifa.draw.util.CollectionsFactory;

import java.util.Set;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class DeleteFromDrawingVisitor implements FigureVisitor {
	private Set myDeletedFigures;
	private Drawing myDrawing;

	public DeleteFromDrawingVisitor(Drawing newDrawing) {
		myDeletedFigures = CollectionsFactory.current().createSet();
		setDrawing(newDrawing);
	}

	private void setDrawing(Drawing newDrawing) {
		myDrawing = newDrawing;
	}

	protected Drawing getDrawing() {
		return myDrawing;
	}

	public void visitFigure(Figure hostFigure) {
		if (!myDeletedFigures.contains(hostFigure) && getDrawing().containsFigure(hostFigure)) {
			Figure orphanedFigure = getDrawing().orphan(hostFigure);
			myDeletedFigures.add(orphanedFigure);
		}
	}

	public void visitHandle(Handle hostHandle) {
	}
        public void removeUpdate(DocumentEvent e) {
            if (getUpdatePolicy() == NEVER_UPDATE ||
                    (getUpdatePolicy() == UPDATE_WHEN_ON_EDT &&
                    !SwingUtilities.isEventDispatchThread())) {

                int length = component.getDocument().getLength();
                dot = Math.min(dot, length);
                mark = Math.min(mark, length);
                if ((e.getOffset() < dot || e.getOffset() < mark)
                        && selectionTag != null) {
                    try {
                        component.getHighlighter().changeHighlight(selectionTag,
                                Math.min(dot, mark), Math.max(dot, mark));
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
                return;
            }
            int offs0 = e.getOffset();
            int offs1 = offs0 + e.getLength();
            int newDot = dot;
            boolean adjustDotBias = false;
            int newMark = mark;
            boolean adjustMarkBias = false;

            if(e instanceof AbstractDocument.UndoRedoDocumentEvent) {
                setDot(offs0);
                return;
            }
            if (newDot >= offs1) {
                newDot -= (offs1 - offs0);
                if(newDot == offs1) {
                    adjustDotBias = true;
                }
            } else if (newDot >= offs0) {
                newDot = offs0;
                adjustDotBias = true;
            }
            if (newMark >= offs1) {
                newMark -= (offs1 - offs0);
                if(newMark == offs1) {
                    adjustMarkBias = true;
                }
            } else if (newMark >= offs0) {
                ensureValidPosition();
                newMark = offs0;
                adjustMarkBias = true;
            }
            if (newMark == newDot) {
                forceCaretPositionChange = true;
                try {
                    setDot(newDot, guessBiasForOffset(newDot, dotBias,
                            dotLTR));
                } finally {
                    forceCaretPositionChange = false;
                }
                ensureValidPosition();
            } else {
                Position.Bias dotBias = DefaultCaret.this.dotBias;
                Position.Bias markBias = DefaultCaret.this.markBias;
                if(adjustDotBias) {
                    dotBias = guessBiasForOffset(newDot, dotBias, dotLTR);
                }
                if(adjustMarkBias) {
                    markBias = guessBiasForOffset(mark, markBias, markLTR);
                }
                setDot(newMark, markBias);
                if (getDot() == newMark) {
                    // Due this test in case the filter vetoed the change
                    // in which case this probably won't be valid either.
                    moveDot(newDot, dotBias);
                }
                ensureValidPosition();
            }
        }

	public void visitFigureChangeListener(FigureChangeListener hostFigureChangeListener) {
//		System.out.println("visitFigureChangeListener: " + hostFigureChangeListener);
//		hostFigureChangeListener.visit(this);
	}

	public FigureEnumeration getDeletedFigures() {
		return new FigureEnumerator(myDeletedFigures);
	}
}
