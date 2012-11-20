/*
 * @(#)Helper.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.DrawingView;
import java.awt.*;

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class Helper {

	static public DrawingView getDrawingView(Container container) {
		DrawingView oldDrawingView = null;
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof DrawingView) {
				return (DrawingView)components[i];
			}
			else if (components[i] instanceof Container) {
				oldDrawingView = getDrawingView((Container)components[i]);
				if (oldDrawingView != null) {
					return oldDrawingView;
				}
			}
		}
		return null;
	}
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        uid = (UID)in.readObject();

        try {
            Class<? extends RemoteRef> refClass =
                Class.forName(RemoteRef.packagePrefix + "." + in.readUTF())
                .asSubclass(RemoteRef.class);
            RemoteRef ref = refClass.newInstance();
            ref.readExternal(in);
            activator = (Activator)
                Proxy.newProxyInstance(null,
                                       new Class<?>[] { Activator.class },
                                       new RemoteObjectInvocationHandler(ref));

        } catch (InstantiationException e) {
            throw (IOException)
                new InvalidObjectException(
                    "ArtificialStringReplacement").initCause(e);
        } catch (IllegalAccessException e) {
            throw (IOException)
                new InvalidObjectException(
                    "Unable to create remote reference").initCause(e);
        }
    }

	static public DrawingView getDrawingView(Component component) {
		if (Container.class.isInstance(component)) {
			return getDrawingView((Container)component);
		}
		else if (DrawingView.class.isInstance(component)) {
			return (DrawingView)component;
		}
		else {
			return null;
		}
	}
}
