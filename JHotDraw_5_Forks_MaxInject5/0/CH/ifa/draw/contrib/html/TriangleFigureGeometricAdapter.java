/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

import java.awt.Point;
import java.awt.Shape;
import CH.ifa.draw.contrib.TriangleFigure;

/**
 * Geometric adapter for the TriangleFigure
 *
 * @author    Eduardo Francos - InContext
 * @created   4 mai 2002
 * @version   1.0
 */

public class TriangleFigureGeometricAdapter extends TriangleFigure
		 implements GeometricFigure {

	/**Constructor for the TriangleFigureGeometricAdapter object */
	public TriangleFigureGeometricAdapter() {
		super();
	}


	/**
	 *Constructor for the TriangleFigureGeometricAdapter object
	 *
	 * @param origin  Description of the Parameter
	 * @param corner  Description of the Parameter
	 */
	public TriangleFigureGeometricAdapter(Point origin, Point corner) {
		super(origin, corner);
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


	/**
	 * Gets the shape attribute of the TriangleFigure object
	 *
	 * @return   The shape value
	 */
	public Shape getShape() {
		return getPolygon();
	}
}
