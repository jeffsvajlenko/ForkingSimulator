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
    public void update(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton)c;
        if ((c.getBackground() instanceof UIResource) &&
                        button.isContentAreaFilled() && c.isEnabled()) {
            ButtonModel model = button.getModel();
            if (!MetalUtils.isToolBarButton(c)) {
                if (!model.isArmed() && !model.isPressed() &&
                        MetalUtils.drawGradient(
                        c, g, "ToggleButton.gradient", 0, 0, c.getWidth(),
                        c.getHeight(), true)) {
                    paint(g, c);
                    return;
                }
            }
            else if ((model.isRollover() || model.isSelected()) &&
                        MetalUtils.drawGradient(c, g, "ToggleButton.gradient",
                        0, 0, c.getWidth(), c.getHeight(), true)) {
                paint(g, c);
                return;
            }
        }
        super.update(g, c);
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
