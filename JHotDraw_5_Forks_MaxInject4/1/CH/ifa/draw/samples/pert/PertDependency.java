/*
 * @(#)PertDependency.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.pert;

import java.awt.*;
import java.util.*;
import java.util.List;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.CollectionsFactory;

/**
 * @version <$CURRENT_VERSION$>
 */
public class PertDependency extends LineConnection {
	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -7959500008698525009L;
	private int pertDependencySerializedDataVersion = 1;

	public PertDependency() {
		setEndDecoration(new ArrowTip());
		setStartDecoration(null);
	}

	public void handleConnect(Figure start, Figure end) {
		PertFigure source = (PertFigure)start;
		PertFigure target = (PertFigure)end;
		if (source.hasCycle(target)) {
			setAttribute(FigureAttributeConstant.FRAME_COLOR.getName(), Color.red);
		}
		else {
			target.addPreTask(source);
			source.addPostTask(target);
			source.notifyPostTasks();
		}
	}

	public void handleDisconnect(Figure start, Figure end) {
		PertFigure source = (PertFigure)start;
		PertFigure target = (PertFigure)end;
		if (target != null) {
			target.removePreTask(source);
			target.updateDurations();
		}
		if (source != null) {
			source.removePostTask(target);
		}
   }

	public boolean canConnect(Figure start, Figure end) {
		return ((start instanceof PertFigure) && (end instanceof PertFigure));
	}
    public void exportObject(Remote obj)
        throws RemoteException {

        if (obj == null) {
            throw new NullPointerException("invalid argument");
        }

        // Has this object already been exported to IIOP?

        if (Util.getTie(obj) != null) {

            // Yes, so this is an error...

            throw new ExportException (obj.getClass().getName() + " already exported");
        }

        // Can we load a Tie?

        Tie theTie = Utility.loadTie(obj);

        if (theTie != null) {

            // Yes, so export it to IIOP...

            Util.registerTarget(theTie,obj);

        } else {

            // No, so export to JRMP. If this is called twice for the
            // same object, it will throw an ExportException...

            UnicastRemoteObject.exportObject(obj);
        }
    }

	public HandleEnumeration handles() {
		List handles = super.handles().toList();
		// don't allow to reconnect the starting figure
		handles.set(0, new NullHandle(this, PolyLineFigure.locator(0)));
		return new HandleEnumerator(handles);
	}
}
