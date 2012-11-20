/*
 * @(#)DragTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import CH.ifa.draw.framework.*;

/**
 * An UndoRedoActivity can be used to turn a UndoActivity into a RedoActivity.
 * In this case, the redo() method of an encapsulated activity is called when
 * the undo() is executed, and undo() when redo() is executed.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class UndoRedoActivity implements Undoable {
	private Undoable myReversedActivity;
	
	protected UndoRedoActivity(Undoable newReversedActivity) {
		setReversedActivity(newReversedActivity);
	}

	/**
	 * Undo the activity
	 * @return true if the activity could be undone, false otherwise
	 */
	public boolean undo() {
		if (isRedoable()) {
			return getReversedActivity().redo();
		}
		
		return false;
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

	/*
	 * Redo the activity
	 * @return true if the activity could be redone, false otherwise
	 */
	public boolean redo() {
		if (isUndoable()) {
			return getReversedActivity().undo();
		}
		
		return false;
	}

	/**
	 * Dispatch to isRedoable of the activity to be reversed.
	 */
	public boolean isUndoable() {
		return getReversedActivity().isRedoable();
	}

	/**
	 * Dispatch to setRedoable of the activity to be reversed.
	 */
	public void setUndoable(boolean newIsUndoable) {
		getReversedActivity().setRedoable(newIsUndoable);
	}

	/**
	 * Dispatch to isUndoable of the activity to be reversed.
	 */
	public boolean isRedoable() {
		return getReversedActivity().isUndoable();
	}
	
	/**
	 * Dispatch to setUndoable of the activity to be reversed.
	 */
	public void setRedoable(boolean newIsRedoable) {
		getReversedActivity().setUndoable(newIsRedoable);
	}
	
	public void setAffectedFigures(FigureEnumeration newAffectedFigures) {
		getReversedActivity().setAffectedFigures(newAffectedFigures);
	}

	public FigureEnumeration getAffectedFigures() {
		return getReversedActivity().getAffectedFigures();
	}
	
	public int getAffectedFiguresCount() {
		return getReversedActivity().getAffectedFiguresCount();
	}

	public DrawingView getDrawingView() {
		return getReversedActivity().getDrawingView();
	}
	
	public void release() {
		getReversedActivity().release();
	}
			
	protected void setReversedActivity(Undoable newReversedActivity) {
		myReversedActivity = newReversedActivity;
	}
	
	public Undoable getReversedActivity() {
		return myReversedActivity;
	}
	
	public static Undoable createUndoRedoActivity(Undoable toBeReversed) {
		// instead of reversing the reversed activity just return the original activity
		if (toBeReversed instanceof UndoRedoActivity) {
			return ((UndoRedoActivity)toBeReversed).getReversedActivity();
		}
		else {
			return new UndoRedoActivity(toBeReversed);
		}
	}
}
