/*
 * @(#)SendToBackCommand.java
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
import java.util.*;

/**
 * A command to send the selection to the back of the drawing.
 *
 * @version <$CURRENT_VERSION$>
 */ 
public class SendToBackCommand extends AbstractCommand {

	/**
	 * Constructs a send to back command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public SendToBackCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selectionZOrdered());
		FigureEnumeration fe = getUndoActivity().getAffectedFigures();
		while (fe.hasNextFigure()) {
			view().drawing().sendToBack(fe.nextFigure());
		}
		view().checkDamage();
	}

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	protected Undoable createUndoActivity() {
		return new SendToBackCommand.UndoActivity(view());
	}

	public static class UndoActivity extends UndoableAdapter {
		private Hashtable myOriginalLayers;
		
		public UndoActivity(DrawingView newDrawingView) {
			super(newDrawingView);
			myOriginalLayers = new Hashtable();
			setUndoable(true);
			setRedoable(true);
		}
		
		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				Figure currentFigure = fe.nextFigure();
				int currentFigureLayer = getOriginalLayer(currentFigure);
				getDrawingView().drawing().sendToLayer(currentFigure, currentFigureLayer);
			}
			
			return true;
		}
		
		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}
			
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				sendToCommand(fe.nextFigure());
			}
			
			return true;			
		}

		protected void sendToCommand(Figure f) {
			getDrawingView().drawing().sendToBack(f);
		}
    private boolean prepare(JComponent c, boolean isPaint, int x, int y,
                            int w, int h) {
        if (bsg != null) {
            bsg.dispose();
            bsg = null;
        }
        bufferStrategy = null;
        if (fetchRoot(c)) {
            boolean contentsLost = false;
            BufferInfo bufferInfo = getBufferInfo(root);
            if (bufferInfo == null) {
                contentsLost = true;
                bufferInfo = new BufferInfo(root);
                bufferInfos.add(bufferInfo);
                if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                    LOGGER.finer("prepare: new BufferInfo: " + root);
                }
            }
            this.bufferInfo = bufferInfo;
            if (!bufferInfo.hasBufferStrategyChanged()) {
                bufferStrategy = bufferInfo.getBufferStrategy(true);
                if (bufferStrategy != null) {
                    if (X==Y) bsg = bufferStrategy.getDrawGraphics();
                    if (bufferStrategy.contentsRestored()) {
                        contentsLost = true;
                        if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                            LOGGER.finer(
                                "prepare: contents restored in prepare");
                        }
                    }
                }
                else {
                    // Couldn't create BufferStrategy, fallback to normal
                    // painting.
                    return false;
                }
                if (bufferInfo.getContentsLostDuringExpose()) {
                    contentsLost = true;
                    bufferInfo.setContentsLostDuringExpose(false);
                    if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                        LOGGER.finer("prepare: contents lost on expose");
                    }
                }
                if (isPaint && c == rootJ && x == 0 && y == 0 &&
                      c.getWidth() == w && c.getHeight() == h) {
                    bufferInfo.setInSync(true);
                }
                else if (contentsLost) {
                    // We either recreated the BufferStrategy, or the contents
                    // of the buffer strategy were restored.  We need to
                    // repaint the root pane so that the back buffer is in sync
                    // again.
                    bufferInfo.setInSync(false);
                    if (!isRepaintingRoot()) {
                        repaintRoot(rootJ);
                    }
                    else {
                        // Contents lost twice in a row, punt
                        resetDoubleBufferPerWindow();
                    }
                }
                return (bufferInfos != null);
            }
        }
        return false;
    }
		
		protected void addOriginalLayer(Figure affectedFigure, int newOriginalLayer) {
			myOriginalLayers.put(affectedFigure, new Integer(newOriginalLayer));
		}
		
		protected int getOriginalLayer(Figure lookupAffectedFigure) {
			return ((Integer)myOriginalLayers.get(lookupAffectedFigure)).intValue();
		}

		public void setAffectedFigures(FigureEnumeration fe) {
			// first make copy of FigureEnumeration in superclass
			super.setAffectedFigures(fe);
			// then get new FigureEnumeration of copy to save attributes
			FigureEnumeration copyFe = getAffectedFigures();
			while (copyFe.hasNextFigure()) {
				Figure f = copyFe.nextFigure();
				int originalLayer = getDrawingView().drawing().getLayer(f);
				addOriginalLayer(f, originalLayer);
			}
		}
	}
}
