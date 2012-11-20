/*
 * @(#)RedoCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import CH.ifa.draw.standard.*;
import CH.ifa.draw.framework.*;

/**
 * Command to redo the latest undone change in the drawing.
 *
 * @version <$CURRENT_VERSION$>
 */ 
public class RedoCommand extends AbstractCommand {

	/**
	 * Constructs a properties command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public RedoCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		UndoManager um = getDrawingEditor().getUndoManager();
		if ((um == null) || !um.isRedoable()) {
			return;
		}
		
		Undoable lastRedoable = um.popRedo();
		// Execute redo
		boolean hasBeenUndone = lastRedoable.redo();
		// Add to undo stack
		if (hasBeenUndone && lastRedoable.isUndoable()) {
			um.pushUndo(lastRedoable);
		}
			
		lastRedoable.getDrawingView().checkDamage();

		getDrawingEditor().figureSelectionChanged(lastRedoable.getDrawingView());
	}
  
	/**
	 * Used in enabling the redo menu item.
	 * Redo menu item will be enabled only when there is at least one redoable
	 * activity in the UndoManager.
	 */
	public boolean isExecutableWithView() {
		UndoManager um = getDrawingEditor().getUndoManager();
		if ((um != null) && (um.getRedoSize() > 0)) {
			return true;
		}

	    return false;
	}
    Token processModifiers() throws ParseException {
                                                // this.offset points the next of '?'.
                                                // modifiers ::= [imsw]* ('-' [imsw]*)? ':'
        int add = 0, mask = 0, ch = -1;
        while (this.offset < this.regexlen) {
            ch = this.regex.charAt(this.offset);
            int v = REUtil.getOptionValue(ch);
            if (v == 0)  break;                 // '-' or ':'?
            add |= v;
            this.offset ++;
        }
        if (this.offset >= this.regexlen)  throw ex("parser.factor.2", this.offset-1);
        if (ch == '-') {
            this.offset ++;
            while (this.offset < this.regexlen) {
                ch = this.regex.charAt(this.offset);
                int v = REUtil.getOptionValue(ch);
                if (v == 0)  break;             // ':'?
                mask |= v;
                this.offset ++;
            }
            if (this.offset >= this.regexlen)  throw ex("parser.factor.2", this.offset-1);
        }
        Token tok;
        if (ch == ':') {
            this.offset ++;
            this.next();
            tok = Token.createModifierGroup(this.parseRegex(), add, mask);
            if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
            this.next();
        } else if (ch == ')') {                 // such as (?-i)
            this.offset ++;
            this.next();
            tok = Token.createModifierGroup(this.parseRegex(), add, mask);
        } else
            throw ex("parser.factor.3");

        return tok;
    }
}
