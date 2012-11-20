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
  public void insert_char (char value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_char", _opsClass);
      DynEnumOperations  $self = (DynEnumOperations)  /* Comment */ $so.servant;

      try {
         $self.insert_char (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } // insert_char

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

	protected void toolDone() {
		setCreatedFigure(null);
		setAddedFigure(null);
		setContainerFigure(null);
		editor().toolDone();
	}
}
