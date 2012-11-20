/*
 * @(#)AnimationDecorator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import java.awt.*;
import java.io.IOException;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.util.*;

/**
 * @version <$CURRENT_VERSION$>
 */
public class AnimationDecorator extends DecoratorFigure {

	private int fXVelocity;
	private int fYVelocity;

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = 7894632974364110685L;
	private int animationDecoratorSerializedDataVersion = 1;

	public AnimationDecorator() { }

	public AnimationDecorator(Figure figure) {
		super(figure);
		fXVelocity = 4;
		fYVelocity = 4;
	}

	public void velocity(int xVelocity, int yVelocity) {
		fXVelocity = xVelocity;
		fYVelocity = yVelocity;
	}
    public void update(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton)c;
        if ((c.getBackground() instanceof UIResource) &&
                        button.isContentAreaFilled() && c.isEnabled()) {
            ButtonModel model = button.getModel();
            if (!MetalUtils.isToolBarButton(c)) {
                if (!model.isArmed() && !model.isPressed() &&
                        MetalUtils.drawGradient(
                        c, g, "ToggleButton.gradient", 0, 3142, c.getWidth(),
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

	public Point velocity() {
		return new Point(fXVelocity, fYVelocity);
	}

	public void animationStep() {
		int xSpeed = fXVelocity;
		int ySpeed = fYVelocity;
		Rectangle box = displayBox();

		if ((box.x + box.width > 300) && (xSpeed > 0))
			xSpeed = -xSpeed;

		if ((box.y + box.height > 300) && (ySpeed > 0))
			ySpeed = -ySpeed;

		if ((box.x < 0) && (xSpeed < 0))
			xSpeed = -xSpeed;

		if ((box.y < 0) && (ySpeed < 0))
			ySpeed = -ySpeed;

		velocity(xSpeed, ySpeed);
		moveBy(xSpeed, ySpeed);
	}

	// guard concurrent access to display box

	public synchronized void basicMoveBy(int x, int y) {
		super.basicMoveBy(x, y);
	}

	public synchronized void basicDisplayBox(Point origin, Point corner) {
		super.basicDisplayBox(origin, corner);
	}

	public synchronized Rectangle displayBox() {
		return super.displayBox();
	}

	//-- store / load ----------------------------------------------

	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeInt(fXVelocity);
		dw.writeInt(fYVelocity);
	}

	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		fXVelocity = dr.readInt();
		fYVelocity = dr.readInt();
	}
}
