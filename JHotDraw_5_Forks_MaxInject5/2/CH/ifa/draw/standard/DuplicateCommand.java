/*
 * @(#)DuplicateCommand.java
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

/**
 * Duplicate the selection and select the duplicates.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DuplicateCommand extends FigureTransferCommand {

   /**
	* Constructs a duplicate command.
	* @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	*/
	public DuplicateCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}
  public static com.sun.corba.se.spi.activation.ServerManager narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof com.sun.corba.se.spi.activation.ServerManager)
      return (com.sun.corba.se.spi.activation.ServerManager)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      com.sun.corba.se.spi.activation._ServerManagerStub stub = new com.sun.corba.se.spi.activation._ServerManagerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		FigureSelection selection = view().getFigureSelection();

		// create duplicate figure(s)
		FigureEnumeration figures = (FigureEnumeration)selection.getData(StandardFigureSelection.TYPE);
		getUndoActivity().setAffectedFigures(figures);

		view().clearSelection();
		getUndoActivity().setAffectedFigures(
			insertFigures(getUndoActivity().getAffectedFigures(), 10, 10));
		view().checkDamage();
	}

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}
}
