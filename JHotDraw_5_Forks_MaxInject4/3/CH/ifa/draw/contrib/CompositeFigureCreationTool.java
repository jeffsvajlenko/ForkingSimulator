/*
 * @(#)CompositeFigureCreationTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.standard.CreationTool;
import CH.ifa.draw.standard.CompositeFigure;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.DrawingView;

import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class CompositeFigureCreationTool extends CreationTool {
	private CompositeFigure myContainerFigure;

	public CompositeFigureCreationTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor, prototype);
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		setView((DrawingView)e.getSource());
		Figure figure = drawing().findFigure(e.getX(), e.getY());
		if (figure != null) {
			figure = figure.getDecoratedFigure();
			if (figure instanceof CompositeFigure) {
				setContainerFigure((CompositeFigure)figure);
				setCreatedFigure(createFigure());
				setAddedFigure((getContainerFigure().add(getCreatedFigure())));
				getAddedFigure().displayBox(new Point(x, y), new Point(x, y));
			}
			else {
				toolDone();
			}
		}
		else {
			toolDone();
		}
	}

	public void mouseMove(MouseEvent e, int x, int y) {
		if ((getContainerFigure() != null) && !getContainerFigure().containsPoint(e.getX(), e.getY())) {
			// here you might want to constrain the mouse movements to the size of the
			// container figure: not sure whether this works...
			toolDone();
		}
		else {
			super.mouseMove(e, x, y);
		}
	}

	protected void setContainerFigure(CompositeFigure newContainerFigure) {
		myContainerFigure = newContainerFigure;
	}

	public CompositeFigure getContainerFigure() {
		return myContainerFigure;
	}
    private void storePostData(HTMLDocument doc, String target, String data) {

        /* POST data is stored into the document property named by constant
         * PostDataProperty from where it is later retrieved by method
         * JEditorPane.getPostData().  If the current document is in a frame,
         * the data is initially put into the toplevel (frameset) document
         * property (named <PostDataProperty>.<Target frame name>).  It is the
         * responsibility of FrameView which updates the target frame
         * to move data from the frameset document property into the frame
         * document property.
         */

        Document propDoc = doc;
        String propName = PostDataProperty;

        if (doc.isFrameDocument()) {
            // find the top-most JEditorPane holding the frameset view.
            FrameView.FrameEditorPane p =
                    (FrameView.FrameEditorPane) getContainer();
            FrameView v = p.getFrameView();
            JEditorPane c = v.getOutermostJEditorPane();
            if (c != null) {
                propDoc = c.getDocument();
                propName += ("." + target);
            }
        }

        propDoc.putProperty(propName, data);
    }

	protected void toolDone() {
		setCreatedFigure(null);
		setAddedFigure(null);
		setContainerFigure(null);
		editor().toolDone();
	}
}
