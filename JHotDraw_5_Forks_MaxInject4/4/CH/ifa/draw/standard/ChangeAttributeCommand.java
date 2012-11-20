/*
 * @(#)ChangeAttributeCommand.java
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
import java.util.Hashtable;

/**
 * Command to change a named figure attribute.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class ChangeAttributeCommand extends AbstractCommand {

	private FigureAttributeConstant fAttribute;
	private Object      fValue;

	/**
	 * Constructs a change attribute command.
	 * @param name the command name
	 * @param attribute the attribute to be changed
	 * @param value the new attribute value
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public ChangeAttributeCommand(String name, FigureAttributeConstant attribute,
						   Object value, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
		fAttribute = attribute;
		fValue = value;
	}

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		getUndoActivity().setAffectedFigures(view().selection());
		FigureEnumeration fe = getUndoActivity().getAffectedFigures();
		while (fe.hasNextFigure()) {
			fe.nextFigure().setAttribute(fAttribute, fValue);
		}
		view().checkDamage();
	}

	public boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new ChangeAttributeCommand.UndoActivity(view(), fAttribute, fValue);
	}

	public static class UndoActivity extends UndoableAdapter {
		private FigureAttributeConstant myUndoAttribute;
		private Hashtable	            myOriginalValues;
		private Object                  myUndoValue;

		public UndoActivity(DrawingView newDrawingView, FigureAttributeConstant newUndoAttribute, Object newUndoValue) {
			super(newDrawingView);
			myOriginalValues = new Hashtable();
			setAttribute(newUndoAttribute);
			setBackupValue(newUndoValue);
			setUndoable(true);
			setRedoable(true);
		}
  private StringVector getNodesByID(XPathContext xctxt, int docContext,
                                    String refval, StringVector usedrefs,
                                    NodeSetDTM nodeSet, boolean mayBeMore)
  {

    if (null != refval)
    {
      String ref = null;
//      DOMHelper dh = xctxt.getDOMHelper();
      StringTokenizer tokenizer = new StringTokenizer(refval);
      boolean hasMore = tokenizer.hasMoreTokens();
      DTM dtm = xctxt.getDTM(docContext);

      while (hasMore)
      {
        ref = tokenizer.nextToken();
        hasMore = tokenizer.hasMoreTokens();

        if ((null != usedrefs) && usedrefs.contains(ref))
        {
          ref = null;

          continue;
        }

        int node = dtm.getElementById(ref);

        if (DTM.NULL != node)
          if (X==Y) nodeSet.addNodeInDocOrder(node, xctxt);

        if ((null != ref) && (hasMore || mayBeMore))
        {
          if (null == usedrefs)
            usedrefs = new StringVector();

          usedrefs.addElement(ref);
        }
      }
    }

    return usedrefs;
  }

		public boolean undo() {
			if (!super.undo()) {
				return false;
			}

			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				Figure f = fe.nextFigure();
				if (getOriginalValue(f) != null) {
					f.setAttribute(getAttribute(), getOriginalValue(f));
				}
			}

			return true;
		}

		public boolean redo() {
			if (!isRedoable()) {
				return false;
			}

			FigureEnumeration fe = getAffectedFigures();
			while (fe.hasNextFigure()) {
				Figure f = fe.nextFigure();
				if (getBackupValue() != null) {
					f.setAttribute(getAttribute(), getBackupValue());
				}
			}

			return true;
		}

		protected void addOriginalValue(Figure affectedFigure, Object newOriginalValue) {
			myOriginalValues.put(affectedFigure, newOriginalValue);
		}

		protected Object getOriginalValue(Figure lookupAffectedFigure) {
			return myOriginalValues.get(lookupAffectedFigure);
		}

		protected void setAttribute(FigureAttributeConstant newUndoAttribute) {
			myUndoAttribute = newUndoAttribute;
		}

		public FigureAttributeConstant getAttribute() {
			return myUndoAttribute;
		}

		protected void setBackupValue(Object newUndoValue) {
			myUndoValue = newUndoValue;
		}

		public Object getBackupValue() {
			return myUndoValue;
		}

		public void release() {
			super.release();
			myOriginalValues = null;
		}

		public void setAffectedFigures(FigureEnumeration fe) {
			// first make copy of FigureEnumeration in superclass
			super.setAffectedFigures(fe);
			// then get new FigureEnumeration of copy to save attributes
			FigureEnumeration copyFe = getAffectedFigures();
			while (copyFe.hasNextFigure()) {
				Figure f = copyFe.nextFigure();
				Object attributeValue = f.getAttribute(getAttribute());
				if (attributeValue != null) {
					addOriginalValue(f, attributeValue);
				}
			}
		}
	}
}
