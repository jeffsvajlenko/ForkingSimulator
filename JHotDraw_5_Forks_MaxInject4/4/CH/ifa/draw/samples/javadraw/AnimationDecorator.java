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
  public int getEndpoint (String endPointType) throws com.sun.corba.se.PortableActivationIDL.NoSuchEndPoint
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getEndpoint", true);
                $out.write_string (endPointType);
                $in = _invoke ($out);
                int $result = com.sun.corba.se.PortableActivationIDL.TCPPortHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:PortableActivationIDL/NoSuchEndPoint:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.NoSuchEndPointHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getEndpoint (endPointType        );
            } finally {
                _releaseReply ($in);
            }
  } // getEndpoint

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
