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
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("[name=").append(this.name);
        appendTo(sb, "displayName", this.displayName);
        appendTo(sb, "shortDescription", this.shortDescription);
        appendTo(sb, "preferred", this.preferred);
        appendTo(sb, "hidden", this.hidden);
        appendTo(sb, "expert", this.expert);         if ((this.table != null) && !this.table.isEmpty()) {
            sb.append("; values={");
            for (Entry<String, Object> entry : this.table.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
            }
            sb.setLength(sb.length() - 2);
            sb.append("}");
        }
        appendTo(sb);
        return sb.append("]").toString();
    }

	protected void setContainerFigure(CompositeFigure newContainerFigure) {
		myContainerFigure = newContainerFigure;
	}

	public CompositeFigure getContainerFigure() {
		return myContainerFigure;
	}

	protected void toolDone() {
		setCreatedFigure(null);
		setAddedFigure(null);
		setContainerFigure(null);
		editor().toolDone();
	}
}
