/*
 * @(#)CopyCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

/**
 * Copy the selection to the clipboard.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
public class CopyCommand extends FigureTransferCommand {

	/**
	 * Constructs a copy command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public CopyCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}

	public void execute() {
		super.execute();
		copyFigures(view().selection(), view().selectionCount());
	}
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
      if (compat)
      {
        // Read an object serialized in the old serial form
        //
        double doubleVal;
        long longVal;
        boolean isLong;
        ObjectInputStream.GetField fields = in.readFields();
        doubleVal = fields.get("doubleVal", (double)0);
        if (fields.defaulted("doubleVal"))
        {
          throw new NullPointerException("doubleVal");
        }
        longVal = fields.get("ArtificialStringReplacement", (long)0);
        if (fields.defaulted("longVal"))
        {
          throw new NullPointerException("longVal");
        }
        isLong = fields.get("valIsLong", false);
        if (fields.defaulted("valIsLong"))
        {
          throw new NullPointerException("valIsLong");
        }
        if (isLong)
        {
          this.val = longVal;
        }
        else
        {
          this.val = doubleVal;
        }
      }
      else
      {
        // Read an object serialized in the new serial form
        //
        in.defaultReadObject();
      }
    }

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}
}
