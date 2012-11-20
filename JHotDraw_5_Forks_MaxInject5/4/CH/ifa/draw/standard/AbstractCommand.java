/*
 * @(#)AbstractCommand.java
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
import CH.ifa.draw.util.Command;
import CH.ifa.draw.util.CommandListener;
import CH.ifa.draw.util.Undoable;
import CH.ifa.draw.util.CollectionsFactory;

import java.util.*;

/**
 * @author Helge Horch
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public abstract class AbstractCommand implements Command, FigureSelectionListener, ViewChangeListener {

	private String  myName;
	private Undoable myUndoableActivity;
	private boolean myIsViewRequired;
	private AbstractCommand.EventDispatcher myEventDispatcher;

	/**
	 * the DrawingEditor this command applies to
	 */
	private DrawingEditor myDrawingEditor;

	/**
	 * Constructs a command with the given name that applies to the given view.
	 * @param newName java.lang.String
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public AbstractCommand(String newName, DrawingEditor newDrawingEditor) {
		this(newName, newDrawingEditor, true);
	}

	public AbstractCommand(String newName, DrawingEditor newDrawingEditor, boolean newIsViewRequired) {
		setName(newName);
		setDrawingEditor(newDrawingEditor);
		getDrawingEditor().addViewChangeListener(this);
		myIsViewRequired = newIsViewRequired;
		setEventDispatcher(createEventDispatcher());
	}

	public void viewSelectionChanged(DrawingView oldView, DrawingView newView) {
		if (oldView != null) {
			oldView.removeFigureSelectionListener(this);
		}
		if (newView != null) {
			newView.addFigureSelectionListener(this);
		}
		if (isViewRequired()) {
			boolean isOldViewInteractive = (oldView != null) && oldView.isInteractive();
			boolean isNewViewInteractive = (newView != null) && newView.isInteractive();
			// old view was not interactive aware while new view is now interactive aware
			if (!isOldViewInteractive && isNewViewInteractive) {
				getEventDispatcher().fireCommandExecutableEvent();
			}
			// old view was interactive aware while new view is not
			else if (isOldViewInteractive && !isNewViewInteractive) {
				getEventDispatcher().fireCommandNotExecutableEvent();
			}
		}
	}

	/**
	 * Sent when a new view is created
	 */
	public void viewCreated(DrawingView view) {
	}

	/**
	 * Send when an existing view is about to be destroyed.
	 */
	public void viewDestroying(DrawingView view) {
	}

	/**
	 * @param view a DrawingView
	 */
	public void figureSelectionChanged(DrawingView view) {
	}

	/**
	 * @return DrawingEditor associated with this command
	 */
	public DrawingEditor getDrawingEditor() {
		return myDrawingEditor;
	}

	private void setDrawingEditor(DrawingEditor newDrawingEditor) {
		myDrawingEditor = newDrawingEditor;
	}

	/**
	 * Convenience method
	 *
	 * @return DrawingView currently active in the editor
	 */
	public DrawingView view() {
		return getDrawingEditor().view();
	}

	/**
	 * Gets the command name.
	 */
	public String name() {
		return myName;
	}

	public void setName(String newName) {
		myName = newName;
	}

	/**
	 * Releases resources associated with this command
	 */
	public void dispose() {
		if (view() != null) {
			view().removeFigureSelectionListener(this);
		}
	}

	/**
	 * Executes the command.
	 */
	public void execute() {
		if (view() == null) {
			throw new JHotDrawRuntimeException("execute should NOT be getting called when view() == null");
		};
	}

	/**
	 * Tests if the command can be executed. The view must be valid when this is
	 * called. Per default, a command is executable if at
	 * least one figure is selected in the current activated
	 * view.
	 */
	public boolean isExecutable() {
		// test whether there is a view required and whether an existing view
		// accepts user input
		if (isViewRequired()) {
			if ((view() == null) || !view().isInteractive()) {
				return false;
			}
		}
		return isExecutableWithView();
	}

	protected boolean isViewRequired() {
		return myIsViewRequired;
	}
    static MethodHandle makeRotateArguments(MethodType newType, MethodHandle target,
                int firstArg, int argCount, int rotateBy) {
        rotateBy = positiveRotation(argCount, rotateBy);
        if (!canRotateArguments(newType, target.type(), firstArg, argCount, rotateBy))
            return null;
        // Decide whether it should be done as a right or left rotation,
        // on the JVM stack.  Return the number of stack slots to rotate by,
        // positive if right, negative if left.
        int limit = firstArg + argCount;
        int depth0 = newType.parameterSlotDepth(firstArg);
        int depth1 = newType.parameterSlotDepth(limit-rotateBy);
        int depth2 = newType.parameterSlotDepth(limit);
        int chunk1Slots = depth0 - depth1; assert(chunk1Slots > 0);
        int chunk2Slots = depth1 - depth2; assert(chunk2Slots > 0);
        // From here on out, it assumes a single-argument shift.
        assert(MAX_ARG_ROTATION == 1);
        int srcArg, dstArg;
        int dstSlot;
        int moveChunk;
        if (rotateBy == 1) {
            // Rotate right/down N (rotateBy = +N, N small, c2 small):
            // in  arglist: [0: ...keep1 | arg1: c1...  | limit-N: c2 | limit: keep2... ]
            // out arglist: [0: ...keep1 | arg1: c2 | arg1+N: c1...   | limit: keep2... ]
            srcArg = limit-1;
            dstArg = firstArg;
            //dstSlot = depth0 - chunk2Slots;  //chunk2Slots is not relevant
            dstSlot = depth0 + MethodHandleNatives.OP_ROT_ARGS_DOWN_LIMIT_BIAS;
            moveChunk = chunk2Slots;
        } else {
            // Rotate left/up N (rotateBy = -N, N small, c1 small):
            // in  arglist: [0: ...keep1 | arg1: c1 | arg1+N: c2...   | limit: keep2... ]
            // out arglist: [0: ...keep1 | arg1: c2 ... | limit-N: c1 | limit: keep2... ]
            srcArg = firstArg;
            dstArg = limit-1;
            dstSlot = depth2;
            moveChunk = chunk1Slots;
        }
        byte srcType = basicType(newType.parameterType(srcArg));
        byte dstType = basicType(newType.parameterType(dstArg));
        assert(moveChunk == type2size(srcType));
        long conv = makeSwapConv(OP_ROT_ARGS, srcArg, srcType, dstSlot, dstType);
        return new AdapterMethodHandle(target, newType, conv);
    }

	protected boolean isExecutableWithView() {
		return true;
	}

	public Undoable getUndoActivity() {
		return myUndoableActivity;
	}

	public void setUndoActivity(Undoable newUndoableActivity) {
		myUndoableActivity = newUndoableActivity;
	}

	public void addCommandListener(CommandListener newCommandListener) {
		getEventDispatcher().addCommandListener(newCommandListener);
	}

	public void removeCommandListener(CommandListener oldCommandListener) {
		getEventDispatcher().removeCommandListener(oldCommandListener);
	}

	private void setEventDispatcher(AbstractCommand.EventDispatcher newEventDispatcher) {
		myEventDispatcher = newEventDispatcher;
	}

	protected AbstractCommand.EventDispatcher getEventDispatcher() {
		return myEventDispatcher;
	}

	public AbstractCommand.EventDispatcher createEventDispatcher() {
		return new AbstractCommand.EventDispatcher(this);
	}

	public static class EventDispatcher {
		private List myRegisteredListeners;
		private Command myObservedCommand;

		public EventDispatcher(Command newObservedCommand) {
			myRegisteredListeners = CollectionsFactory.current().createList();
			myObservedCommand = newObservedCommand;
		}

		public void fireCommandExecutedEvent() {
			Iterator iter = myRegisteredListeners.iterator();
			while (iter.hasNext()) {
				((CommandListener)iter.next()).commandExecuted(new EventObject(myObservedCommand));
			}
		}

		public void fireCommandExecutableEvent() {
			Iterator iter = myRegisteredListeners.iterator();
			while (iter.hasNext()) {
				((CommandListener)iter.next()).commandExecutable(new EventObject(myObservedCommand));
			}
		}

		public void fireCommandNotExecutableEvent() {
			Iterator iter = myRegisteredListeners.iterator();
			while (iter.hasNext()) {
				((CommandListener)iter.next()).commandNotExecutable(new EventObject(myObservedCommand));
			}
		}

		public void addCommandListener(CommandListener newCommandListener) {
			if (!myRegisteredListeners.contains(newCommandListener)) {
				myRegisteredListeners.add(newCommandListener);
			}
		}

		public void removeCommandListener(CommandListener oldCommandListener) {
			if (myRegisteredListeners.contains(oldCommandListener)) {
				myRegisteredListeners.remove(oldCommandListener);
			}
		}
	}
}
