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
    private void initialize( com.sun.corba.se.spi.orb.ORB orb,
        String nameServiceName )
        throws org.omg.CORBA.INITIALIZE
    {
        NamingSystemException wrapper = NamingSystemException.get( orb,
            CORBALogDomains.NAMING ) ;

        try {
            POA rootPOA = (POA) orb.resolve_initial_references(
                ORBConstants.ROOT_POA_NAME );
            rootPOA.the_POAManager().activate();

            int i = 0;
            Policy[] poaPolicy = new Policy[3];
            poaPolicy[i++] = rootPOA.create_lifespan_policy(
                LifespanPolicyValue.TRANSIENT);
            poaPolicy[i++] = rootPOA.create_id_assignment_policy(
                IdAssignmentPolicyValue.SYSTEM_ID);
            poaPolicy[i++] = rootPOA.create_servant_retention_policy(
                ServantRetentionPolicyValue.RETAIN);

            POA nsPOA = rootPOA.create_POA( "TNameService", null, poaPolicy );
            nsPOA.the_POAManager().activate();

            // Create an initial context
            TransientNamingContext initialContext =
                new TransientNamingContext(orb, null, nsPOA);
            byte[] rootContextId = nsPOA.activate_object( initialContext );
            initialContext.localRoot =
                nsPOA.id_to_reference( rootContextId );
            theInitialNamingContext = initialContext.localRoot;
            orb.register_initial_reference( nameServiceName,
                theInitialNamingContext );
        } catch (org.omg.CORBA.SystemException e) {
            throw wrapper.transNsCannotCreateInitialNcSys( e ) ;
        } catch (Exception e) {
            throw wrapper.transNsCannotCreateInitialNc( e ) ;
        }
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
