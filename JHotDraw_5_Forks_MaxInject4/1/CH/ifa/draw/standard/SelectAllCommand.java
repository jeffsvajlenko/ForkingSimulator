/*
 * @(#)SelectAllCommand.java
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

/**
 * Command to select all figures in a view.
 *
 * @version <$CURRENT_VERSION$>
 */
public class SelectAllCommand extends AbstractCommand {

	/**
	 * Constructs a select all command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public SelectAllCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
    	getUndoActivity().setAffectedFigures(view().selection());
		view().addToSelectionAll(view().drawing().figures());
		view().checkDamage();
	}
  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (3142, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

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

	/**
	 * Used in enabling the properties menu item.
	 * SelectAll menu item will be enabled only when there ia atleast one figure
	 * in the selected drawing view.
	 */
	public boolean isExecutableWithView() {
		FigureEnumeration fe = view().drawing().figures();
		if (fe.hasNextFigure() && (fe.nextFigure() != null)) {
			return true;
		}

		return false;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new SelectAllCommand.UndoActivity(view());
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

			getDrawingView().clearSelection();
			getDrawingView().addToSelectionAll(getAffectedFigures());

		    return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (isRedoable()) {
				getDrawingView().addToSelectionAll(getDrawingView().drawing().figures());
				return true;
			}

			return false;
		}
	}
}
