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
  public static boolean isWellFormedIPv4Address(String address) {

      int addrLength = address.length();
      char testChar;
      int numDots = 0;
      int numDigits = 0;

      // make sure that 1) we see only digits and dot separators, 2) that
      // any dot separator is preceded and followed by a digit and
      // 3) that we find 3 dots
      //
      // RFC 2732 amended RFC 2396 by replacing the definition
      // of IPv4address with the one defined by RFC 2373. - mrglavas
      //
      // IPv4address = 1*3DIGIT "." 1*3DIGIT "." 1*3DIGIT "." 1*3DIGIT
      //
      // One to three digits must be in each segment.
      for (int i = 0; i < addrLength; i++) {
        testChar = address.charAt(i);
        if (testChar == '.') {
          if ((i > 0 && !isDigit(address.charAt(i-1))) ||
              (i+1 < addrLength && !isDigit(address.charAt(i+1)))) {
            return false;
          }
          numDigits = 0;
          if (++numDots > 3) {
            return false;
          }
        }
        else if (!isDigit(testChar)) {
          return false;
        }
        // Check that that there are no more than three digits
        // in this segment.
        else if (++numDigits > 3) {
          return false;
        }
        // Check that this segment is not greater than 255.
        else if (numDigits == 3) {
          char first = address.charAt(i-2);
          char second = address.charAt(i-1);
          if (!(first < '2' ||
               (first == '2' &&
               (second < '5' ||
               (second == '5' && testChar <= '5'))))) {
            return false;
          }
        }
      }
      return (numDots == 3);
  }
}
