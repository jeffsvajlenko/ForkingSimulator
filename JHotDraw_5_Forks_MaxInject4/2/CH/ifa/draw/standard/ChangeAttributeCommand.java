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
    public void setInstructions(char[] instruction, int lenInstruction)
    {
        // Save reference to instruction array
        this.instruction = instruction;
        this.lenInstruction = lenInstruction;

        // Initialize other program-related variables
        flags = 0;
        prefix = null;

        // Try various compile-time optimizations if there's a program
        if (instruction != null && lenInstruction != 0)
        {
            // If the first node is a branch
            if (lenInstruction >= RE.nodeSize && instruction[0 + RE.offsetOpcode] == RE.OP_BRANCH)
            {
                // to the end node
                int next = instruction[0 + RE.offsetNext];
                if (instruction[next + RE.offsetOpcode] == RE.OP_END)
                {
                    // and the branch starts with an atom
                    if (lenInstruction >= (RE.nodeSize * 2) && instruction[RE.nodeSize + RE.offsetOpcode] == RE.OP_ATOM)
                    {
                        // then get that atom as an prefix because there's no other choice
                        int lenAtom = instruction[RE.nodeSize + RE.offsetOpdata];
                        prefix = new char[lenAtom];
                        System.arraycopy(instruction, RE.nodeSize * 2, prefix, 0, lenAtom);
                    }
                }
            }

            BackrefScanLoop:

            // Check for backreferences
            for (int i = 0; i < lenInstruction; i += RE.nodeSize)
            {
                switch (instruction[i + RE.offsetOpcode])
                {
                    case RE.OP_ANYOF:
                        i += (instruction[i + RE.offsetOpdata] * 2);
                        break;

                    case RE.OP_ATOM:
                        i += instruction[i + RE.offsetOpdata];
                        break;

                    case RE.OP_BACKREF:
                        flags |= OPT_HASBACKREFS;
                        
break BackrefScanLoop;
                }
            }
        }
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
