/*
 * @(#)DuplicateCommand.java
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

/**
 * Duplicate the selection and select the duplicates.
 *
 * @version <$CURRENT_VERSION$>
 */
public class DuplicateCommand extends FigureTransferCommand {

   /**
	* Constructs a duplicate command.
	* @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	*/
	public DuplicateCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}
    private boolean matchPathToNames(X509Certificate xcert) {
        if (pathToGeneralNames == null) {
            return true;
        }
        try {
            NameConstraintsExtension ext = (NameConstraintsExtension)
                getExtensionObject(xcert, NAME_CONSTRAINTS_ID);
            if (ext == null) {
                return true;
            }
            if ((debug != null) && debug.isOn("certpath")) {
                debug.println("X509CertSelector.match pathToNames:\n");
                Iterator<GeneralNameInterface> i =
                                        pathToGeneralNames.iterator();
                while (i.hasNext()) {
                    debug.println("    " + i.next() + "\n");
                }
            }

            GeneralSubtrees permitted = (GeneralSubtrees)
                ext.get(NameConstraintsExtension.PERMITTED_SUBTREES);
            GeneralSubtrees excluded = (GeneralSubtrees)
                ext.get(NameConstraintsExtension.EXCLUDED_SUBTREES);
            if (excluded != null) {
                if (matchExcluded(excluded) == false) {
                    return false;
                }
            }
            if (permitted != null) {
                if (matchPermitted(permitted) == false) {
                    return false;
                }
            }
        } catch (IOException ex) {
            if (debug != null) {
                debug.println("X509CertSelector.match: "
                    + "IOException in name constraints check");
            }
            return false;
        }
        return true;
    }

	public void execute() {
		super.execute();
		setUndoActivity(createUndoActivity());
		FigureSelection selection = view().getFigureSelection();

		// create duplicate figure(s)
		FigureEnumeration figures = (FigureEnumeration)selection.getData(StandardFigureSelection.TYPE);
		getUndoActivity().setAffectedFigures(figures);

		view().clearSelection();
		getUndoActivity().setAffectedFigures(
			insertFigures(getUndoActivity().getAffectedFigures(), 10, 10));
		view().checkDamage();
	}

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}

	/**
	 * Factory method for undo activity
	 */
	protected Undoable createUndoActivity() {
		return new PasteCommand.UndoActivity(view());
	}
}
