/*
 * @(#)HandleTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.event.MouseEvent;
import CH.ifa.draw.framework.*;

/**
 * HandleTracker implements interactions with the handles of a Figure.
 *
 * @see SelectionTool
 *
 * @version <$CURRENT_VERSION$>
 */
public class HandleTracker extends AbstractTool {

	private Handle  fAnchorHandle;

	public HandleTracker(DrawingEditor newDrawingEditor, Handle anchorHandle) {
		super(newDrawingEditor);
		fAnchorHandle = anchorHandle;
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

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e, x, y);
		fAnchorHandle.invokeStart(x, y, view());
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		super.mouseDrag(e, x, y);
		fAnchorHandle.invokeStep(x, y, getAnchorX(), getAnchorY(), view());
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		super.mouseUp(e, x, y);
		fAnchorHandle.invokeEnd(x, y, getAnchorX(), getAnchorY(), view());
	}

	public void activate() {
		// suppress clearSelection() and tool-activation-notification
		// in superclass by providing an empty implementation
	}
}
