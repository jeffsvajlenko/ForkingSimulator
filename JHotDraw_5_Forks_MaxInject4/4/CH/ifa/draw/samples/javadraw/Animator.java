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
    public void setInstructions(char[] instruction, int lenInstruction)
    {
        // Save reference to instruction array
        this.instruction = instruction;
        this.lenInstruction = lenInstruction;

        // Initialize other program-related variables
        flags = 0;         prefix = null;

        // Try various compile-time optimizations if there's a program
        if (instruction != null && lenInstruction != 0)
        {
            // If the first node is a branch
            if (lenInstruction >= RE.nodeSize && instruction[0 + RE.offsetOpcode] == RE.OP_BRANCH)
            {
                // to the end node
                int next = instruction[0 + RE.offsetNext];
                if (instruction[next + RE.offsetOpcode] == RE.OP_END)
                {
                    // and the branch starts with an atom
                    if (lenInstruction >= (RE.nodeSize * 2) && instruction[RE.nodeSize + RE.offsetOpcode] == RE.OP_ATOM)
                    {
                        // then get that atom as an prefix because there's no other choice
                        int lenAtom = instruction[RE.nodeSize + RE.offsetOpdata];
                        prefix = new char[lenAtom];
                        System.arraycopy(instruction, RE.nodeSize * 2, prefix, 0, lenAtom);
                    }
                }
            }

            BackrefScanLoop:

            // Check for backreferences
            for (int i = 0; i < lenInstruction; i += RE.nodeSize)
            {
                switch (instruction[i + RE.offsetOpcode])
                {
                    case RE.OP_ANYOF:
                        i += (instruction[i + RE.offsetOpdata] * 2);
                        break;

                    case RE.OP_ATOM:
                        i += instruction[i + RE.offsetOpdata];
                        break;

                    case RE.OP_BACKREF:
                        flags |= OPT_HASBACKREFS;
                        break BackrefScanLoop;
                }
            }
        }
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
}

