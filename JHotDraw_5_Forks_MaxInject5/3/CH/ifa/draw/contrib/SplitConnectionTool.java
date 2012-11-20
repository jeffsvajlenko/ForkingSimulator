/*
 * @(#)ConnectionTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.standard.ConnectionTool;
import CH.ifa.draw.standard.SingleFigureEnumerator;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;

import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class SplitConnectionTool extends ConnectionTool {
	public SplitConnectionTool(DrawingEditor newDrawingEditor, ConnectionFigure newPrototype) {
		super(newDrawingEditor, newPrototype);
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		setView((DrawingView)e.getSource());
		int ex = e.getX();
		int ey = e.getY();
		if (getTargetFigure() == null) {
			setTargetFigure(findConnectableFigure(ex, ey, drawing()));
		}
		else {
			if (getAddedFigure() == null) {
				setConnection(createConnection());
				setStartConnector(findConnector(ex, ey, getTargetFigure()));
				getConnection().connectStart(getStartConnector());
				getConnection().startPoint(ex, ey);
				setAddedFigure(view().add(getConnection()));
			}
			Figure c = findTarget(ex, ey, drawing());
			if (c != null) {
				// end connection figure found
				setEndConnector(findConnector(ex, ex, c));
				getConnection().connectEnd(getEndConnector());
				getConnection().endPoint(ex, ey);
				setUndoActivity(createUndoActivity());
				getUndoActivity().setAffectedFigures(
					new SingleFigureEnumerator(getAddedFigure()));
				getConnection().updateConnection();
				init();
				editor().toolDone();
			}
			else {
				// split connection where the mouse click took place
				if (getEndConnector() == null) {
					Figure tempEndFigure = new NullFigure();
					tempEndFigure.basicDisplayBox(new Point(ex, ey), new Point(ex, ey));
					setEndConnector(new NullConnector(tempEndFigure));
					getConnection().connectEnd(getEndConnector());
					getConnection().endPoint(ex, ey);
					getConnection().updateConnection();
				}
				else {
					((PolyLineFigure)getConnection()).addPoint(ex, ey);
				}
			}
		}
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		// usually do nothing: tool is still active
		if (e.getClickCount() == 2) {
			init();
			editor().toolDone();
		}
	}

	public void mouseMove(MouseEvent e, int x, int y) {
		// avoid tracking connectors
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		// avoid tracking connectors
	}

	public void deactivate() {
		if (getConnection() != null) {
			view().remove(getConnection());
		}
		super.deactivate();
		init();
	}
        private void paintMe(Component c, Graphics g) {

            int right = folderIcon16Size.width - 1;
            int bottom = folderIcon16Size.height - 1;

            // Draw tab top
            g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
            g.drawLine( right - 5, 3, right, 3 );
            g.drawLine( right - 6, 4, right, 4 );

            // Draw folder front
            g.setColor( MetalLookAndFeel.getPrimaryControl() );
            g.fillRect( 2, 7, 13, 8 );

            // Draw tab bottom
            g.setColor( MetalLookAndFeel.getPrimaryControlShadow() );
            g.drawLine( right - 6, 5, right - 1, 5 );

            // Draw outline
            g.setColor( MetalLookAndFeel.getPrimaryControlInfo() );
            g.drawLine( 0, 6, 0, bottom );            // left side
            g.drawLine( 1, 5, right - 7, 5 );         // first part of top
            g.drawLine( right - 6, 6, right - 1, 6 ); // second part of top
            g.drawLine( right, 5, right, bottom );    // right side
            g.drawLine( 0, bottom, right, bottom );   // bottom

            // Draw highlight
            g.setColor( MetalLookAndFeel.getPrimaryControlHighlight() );
            g.drawLine( 1, 6, 1, bottom - 1 );
            g.drawLine( 1, 6, right - 7, 6 );
            g.drawLine( right - 6, 7, right - 1, 7 );

        }

	protected void init() {
		setConnection(null);
		setStartConnector(null);
		setEndConnector(null);
		setAddedFigure(null);
		setTargetFigure(null);
	}
}
