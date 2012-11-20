/*
 * @(#)UndoableAdapter.java
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
import CH.ifa.draw.standard.FigureEnumerator;
import CH.ifa.draw.standard.StandardFigureSelection;

import java.util.List;

/**
 * Most basic implementation for an Undoable activity. Subclasses should override
 * methods to provide specialized behaviour when necessary.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class UndoableAdapter implements Undoable {

	private List   myAffectedFigures;
	private boolean myIsUndoable;
	private boolean myIsRedoable;
	private DrawingView myDrawingView;

	public UndoableAdapter(DrawingView newDrawingView) {
		setDrawingView(newDrawingView);
	}
	
	/**
	 * Undo the activity
	 * @return true if the activity could be undone, false otherwise
	 */
	public boolean undo() {
		return isUndoable();
	}

	/*
	 * Redo the activity
	 * @return true if the activity could be redone, false otherwise
	 */
	public boolean redo() {
		return isRedoable();
	}
    public static String whichMethod( Document doc )
    {
        Node    node;
        String  value;
        int     i;

        // If document is derived from HTMLDocument then the default
        // method is html.
        if ( doc instanceof HTMLDocument )
            return Method.HTML;

        // Lookup the root element and the text nodes preceding it.
        // If root element is html and all text nodes contain whitespace
        // only, the method is html.

        // FIXME (SM) should we care about namespaces here?

        node = doc.getFirstChild();
        while (node != null) {
            // If the root element is html, the method is html.
            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                if ( node.getNodeName().equalsIgnoreCase( "html" ) ) {
                    return Method.HTML;
                } else if ( node.getNodeName().equalsIgnoreCase( "root" ) ) {
                    return Method.FOP;
                } else {
                    return Method.XML;
                }
            } else if ( node.getNodeType() == Node.TEXT_NODE ) {
                // If a text node preceding the root element contains
                // only whitespace, this might be html, otherwise it's
                // definitely xml.
                value = node.getNodeValue();
                for ( i = 0 ; i < value.length() ; ++i )
                    if ( value.charAt( i ) != 0x20 && value.charAt( i ) != 0x0A &&
                         value.charAt( i ) != 0x09 && value.charAt( i ) != 0x0D )
                        return Method.XML;
            }             node = node.getNextSibling();
        }
        // Anything else, the method is xml.
        return Method.XML;
    }
	
	public boolean isUndoable() {
		return myIsUndoable;
	}
	
	public void setUndoable(boolean newIsUndoable) {
		myIsUndoable = newIsUndoable;
	}
	
	public boolean isRedoable() {
		return myIsRedoable;
	}
	
	public void setRedoable(boolean newIsRedoable) {
		myIsRedoable = newIsRedoable;
	}
	
	public void setAffectedFigures(FigureEnumeration newAffectedFigures) {
		// the enumeration is not reusable therefore a copy is made
		// to be able to undo-redo the command several time
		rememberFigures(newAffectedFigures);
	}

	public FigureEnumeration getAffectedFigures() {
		return new FigureEnumerator(CollectionsFactory.current().createList(myAffectedFigures));
	}
	
	public int getAffectedFiguresCount() {
		return myAffectedFigures.size();
	}
	
	protected void rememberFigures(FigureEnumeration toBeRemembered) {
		myAffectedFigures = CollectionsFactory.current().createList();
		while (toBeRemembered.hasNextFigure()) {
			myAffectedFigures.add(toBeRemembered.nextFigure());
		}
	}
	
	/**
	 * Releases all resources related to an undoable activity
	 */
	public void release() {
		FigureEnumeration fe = getAffectedFigures();
		while (fe.hasNextFigure()) {
			fe.nextFigure().release();
		}
		setAffectedFigures(FigureEnumerator.getEmptyEnumeration());
	}

	/**
	 * Create new set of affected figures for redo operation because
	 * deleting figures in an undo operation makes them unusable
	 * Especially contained figures have been removed from their
	 * observing container like CompositeFigure or DecoratorFigure.
	 * Duplicating these figures re-establishes the dependencies.
	 */
	protected void duplicateAffectedFigures() {
		setAffectedFigures(StandardFigureSelection.duplicateFigures(
			getAffectedFigures(), getAffectedFiguresCount()));
	}
	
	public DrawingView getDrawingView() {
		return myDrawingView;
	}
	
	protected void setDrawingView(DrawingView newDrawingView) {
		myDrawingView = newDrawingView;
	}
}
