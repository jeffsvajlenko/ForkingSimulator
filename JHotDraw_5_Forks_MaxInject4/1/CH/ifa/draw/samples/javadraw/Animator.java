/*
 * @(#)Animator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.Animatable;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class Animator extends Thread {

	private DrawingView     fView;
	private Animatable      fAnimatable;

	private volatile boolean             fIsRunning;
	private static final int    DELAY = 1000 / 16;

	public Animator(Animatable animatable, DrawingView view) {
		super("Animator");
		fView = view;
		fAnimatable = animatable;
	}

	public void start() {
		super.start();
		fIsRunning = true;
	}

	public void end() {
		fIsRunning = false;
	}

	public void run() {
		while (fIsRunning) {
			long tm = System.currentTimeMillis();
			fView.freezeView();
			fAnimatable.animationStep();
			fView.checkDamage();
			fView.unfreezeView();

			// Delay for a while
			try {
				tm += DELAY;
				Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}
    private void initMembers(String relationId,
                             ObjectName relationServiceName,
                             MBeanServer relationServiceMBeanServer,
                             String relationTypeName,
                             RoleList list)
        throws InvalidRoleValueException,
               IllegalArgumentException {

        if (relationId == null ||
            relationServiceName == null ||
            relationTypeName == null) {
            String excMsg = "Invalid parameter.";
            throw new IllegalArgumentException(excMsg);
        }

        RELATION_LOGGER.entering(RelationSupport.class.getName(),
                "initMembers", new Object[] {relationId, relationServiceName,
                relationServiceMBeanServer, relationTypeName, list});

        myRelId = relationId;
        myRelServiceName = relationServiceName;
        myRelServiceMBeanServer = relationServiceMBeanServer;
        myRelTypeName = relationTypeName;
        // Can throw InvalidRoleValueException
        initRoleMap(list);

        RELATION_LOGGER.exiting(RelationSupport.class.getName());
        return;
    }
}

