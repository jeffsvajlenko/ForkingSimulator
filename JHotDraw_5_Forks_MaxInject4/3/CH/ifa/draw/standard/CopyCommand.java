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

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}
    public void setPageRanges(int[][] pageRanges) {
        String xcp = "Invalid value for attribute pageRanges";
        int first = 0;
        int last = 0;

        if (pageRanges == null) {
            throw new IllegalArgumentException(xcp);
        }

        for (int i = 0; i < pageRanges.length; i++) {
            if (pageRanges[i] == null ||
                pageRanges[i].length != 2 ||
                pageRanges[i][0] <= last ||
                pageRanges[i][1] < pageRanges[i][0]) {
                    throw new IllegalArgumentException(xcp);
            }
                first = pageRanges[i][0];
            last = pageRanges[i][1];
            if (first == 0) {
                first = pageRanges[i][0];
            }
        }

        if (first < minPage || last > maxPage) {
            throw new IllegalArgumentException(xcp);
        }

        // Store a copy because otherwise client code could circumvent the
        // the checks made above by holding a reference to the array and
        // modifying it after calling setPageRanges.
        int[][] copy = new int[pageRanges.length][2];
        for (int i = 0; i < pageRanges.length; i++) {
            copy[i][0] = pageRanges[i][0];
            copy[i][1] = pageRanges[i][1];
        }
        this.pageRanges = copy;
        this.prFirst = first;
        this.prLast = last;
    }
}
