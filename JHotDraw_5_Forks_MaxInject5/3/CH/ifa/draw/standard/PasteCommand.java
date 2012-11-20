/*
 * @(#)PasteCommand.java
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
import java.awt.*;

/**
 * Command to insert the clipboard into the drawing.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
public class PasteCommand extends FigureTransferCommand {

	/**
	 * Constructs a paste command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public PasteCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		Point lastClick = view().lastClick();
		FigureSelection selection = (FigureSelection)Clipboard.getClipboard().getContents();
		if (selection != null) {
			setUndoActivity(createUndoActivity());
			getUndoActivity().setAffectedFigures(
				(FigureEnumerator)selection.getData(StandardFigureSelection.TYPE));

			if (!getUndoActivity().getAffectedFigures().hasNextFigure()) {
				setUndoActivity(null);
				return;
			}

			Rectangle r = getBounds(getUndoActivity().getAffectedFigures());
			view().clearSelection();

			// get an enumeration of inserted figures
			FigureEnumeration fe = insertFigures(getUndoActivity().getAffectedFigures(), lastClick.x-r.x, lastClick.y-r.y);
			getUndoActivity().setAffectedFigures(fe);

			view().checkDamage();
		}
	}

	public boolean isExecutableWithView() {
		return Clipboard.getClipboard().getContents() != null;
	}

	private Rectangle getBounds(FigureEnumeration fe) {
		Rectangle r = fe.nextFigure().displayBox();
		while (fe.hasNextFigure()) {
			r.add(fe.nextFigure().displayBox());
		}
		return r;
	}
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        boolean setStartNodeCalled = false;
        final Stylesheet stylesheet = classGen.getStylesheet();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int current = methodGen.getLocalIndex("current");

        // check if sorting nodes is required
        final Vector sortObjects = new Vector();
        final Enumeration children = elements();
        while (children.hasMoreElements()) {
            final Object child = children.nextElement();
            if (child instanceof Sort) {
                sortObjects.addElement(child);
            }
        }

        // Push a new parameter frame
        if (stylesheet.hasLocalParams() || hasContents()) {
            il.append(classGen.loadTranslet());
            final int pushFrame = cpg.addMethodref(TRANSLET_CLASS,
                                                   PUSH_PARAM_FRAME,
                                                   PUSH_PARAM_FRAME_SIG);
            il.append(new INVOKEVIRTUAL(pushFrame));
            // translate with-params
            translateContents(classGen, methodGen);
        }


        il.append(classGen.loadTranslet());

        // The 'select' expression is a result-tree
        if ((_type != null) && (_type instanceof ResultTreeType)) {
            // <xsl:sort> cannot be applied to a result tree - issue warning
            if (sortObjects.size() > 0) {
                ErrorMsg err = new ErrorMsg(ErrorMsg.RESULT_TREE_SORT_ERR,this);
                getParser().reportError(WARNING, err);
            }
            // Put the result tree (a DOM adapter) on the stack
            _select.translate(classGen, methodGen);
            // Get back the DOM and iterator (not just iterator!!!)
            _type.translateTo(classGen, methodGen, Type.NodeSet);
        }
        else {
            il.append(methodGen.loadDOM());

            // compute node iterator for applyTemplates
            if (sortObjects.size() > 0) {
                Sort.translateSortIterator(classGen, methodGen,
                                           _select, sortObjects);
                int setStartNode = cpg.addInterfaceMethodref(NODE_ITERATOR,
                                                             SET_START_NODE,
                                                             "(I)"+
                                                             NODE_ITERATOR_SIG);
                il.append(methodGen.loadCurrentNode());
                il.append(new INVOKEINTERFACE(setStartNode,2));
                setStartNodeCalled = true;
            }
            else {
                if (_select == null)
                    Mode.compileGetChildren(classGen, methodGen, current);
                else
                    _select.translate(classGen, methodGen);
            }
        }

        if (_select != null && !setStartNodeCalled) {
            _select.startIterator(classGen, methodGen);
        }

        //!!! need to instantiate all needed modes
        final String className = classGen.getStylesheet().getClassName();
        il.append(methodGen.loadHandler());
        final String applyTemplatesSig = classGen.getApplyTemplatesSig();
        final int applyTemplates = cpg.addMethodref(className,
                                                    _functionName,
                                                    applyTemplatesSig);
        il.append(new INVOKEVIRTUAL(applyTemplates));

        // Pop parameter frame
        if (stylesheet.hasLocalParams() || hasContents()) {
            il.append(classGen.loadTranslet());
            final int popFrame = cpg.addMethodref(TRANSLET_CLASS,
                                                  POP_PARAM_FRAME,
                                                  POP_PARAM_FRAME_SIG);
            il.append(new INVOKEVIRTUAL(popFrame));
        }
    }

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
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

			DeleteFromDrawingVisitor deleteVisitor = new DeleteFromDrawingVisitor(getDrawingView().drawing());
			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
	    		fe.nextFigure().visit(deleteVisitor);
			}

			getDrawingView().clearSelection();

			return true;
		}

		public boolean redo() {
			// do not call execute directly as the selection might has changed
			if (!isRedoable()) {
				return false;
			}

			getDrawingView().clearSelection();
			setAffectedFigures(getDrawingView().insertFigures(
				getAffectedFigures(), 0, 0, false));

			return true;
		}
	}
}
