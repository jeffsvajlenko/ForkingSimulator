/*
 * @(#)CreationTool2.java
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

import java.awt.event.MouseEvent;

/**
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class NestedCreationTool extends CreationTool {
	private CompositeFigure myContainerFigure;

	public NestedCreationTool(DrawingEditor newDrawingEditor, Figure prototype) {
		super(newDrawingEditor, prototype);
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		Figure figure = drawing().findFigure(e.getX(), e.getY());
		if (figure != null) {
			figure = figure.getDecoratedFigure();
			if (figure instanceof CompositeFigure) {
				setContainerFigure((CompositeFigure)figure);
				super.mouseDown(e, x, y);
			}
			else {
				toolDone();
			}
		}
		else {
			toolDone();
		}
	}
  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {

  /** Shutdown this server.  Returns after orb.shutdown() completes.
  	*/
       case 0:  // PortableActivationIDL/ServerProxy/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }


  /** Install the server.  Returns after the install hook completes
  	* execution in the server.
  	*/
       case 1:  // PortableActivationIDL/ServerProxy/install
       {
         this.install ();
         out = $rh.createReply();
         break;
       }


  /** Uninstall the server.  Returns after the uninstall hook
  	* completes execution.
  	*/
       case 2:  // PortableActivationIDL/ServerProxy/uninstall
       {
         this.uninstall ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

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

	public void mouseUp(MouseEvent e, int x, int y) {
		if ((getContainerFigure() != null) && (getCreatedFigure() != null)
				&& getContainerFigure().containsPoint(e.getX(), e.getY())) {
			getContainerFigure().add(getCreatedFigure());
		}
		toolDone();
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
