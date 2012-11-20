/*
 * @(#)ZoomTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib.zoom;

import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.AbstractTool;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * @author Andre Spiegel <spiegel@gnu.org>
 * @version <$CURRENT_VERSION$>
 */
public class ZoomTool extends AbstractTool {

	private Tool child;

	public ZoomTool(DrawingEditor editor) {
		super(editor);
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

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e,x,y);
		//  Added handling for SHIFTed and CTRLed BUTTON3_MASK so that normal
		//  BUTTON3_MASK does zoomOut, SHIFTed BUTTON3_MASK does zoomIn
		//  and CTRLed BUTTON3_MASK does deZoom
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			if (child != null) {
				return;
			}
			view().freezeView();
			child = new ZoomAreaTracker(editor());
			child.mouseDown(e, x, y);
		}
		else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
			((ZoomDrawingView) view()).deZoom(x, y);
		}
		else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
				((ZoomDrawingView)view()).zoomIn(x, y);
			}
			else if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {

				((ZoomDrawingView) view()).deZoom(x, y);
			}
			else {
				((ZoomDrawingView)view()).zoomOut(x, y);
			}
		}
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		if (child != null) {
			child.mouseDrag(e, x, y);
		}
	}

	public void mouseUp(MouseEvent e, int x, int y) {
		if (child != null) {
			view().unfreezeView();
			child.mouseUp(e, x, y);
		}
		child = null;
	}
}
