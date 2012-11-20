/*
 *  @(#)ZoomCommand.java
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
import CH.ifa.draw.framework.JHotDrawRuntimeException;

import CH.ifa.draw.standard.AbstractCommand;

/**
 * A ZoomCommand allows for applying a zoom factor to a ZoomDrawingView.<br>
 * Several ZoomCommand objects can be created in a menu or toolbar, set to various
 * predefined zoom factors
 *
 * @author    Eduardo Francos
 * @created   26 april 2002
 * @version   <CURRENT_VERSION>
 */
public class ZoomCommand extends AbstractCommand {
	/** The scale factor to apply */
	protected float scale = 1.0f;

	/**
	 * Constructor for the ZoomCommand object
	 *
	 * @param name              the command name
	 * @param scale             Description of the Parameter
	 * @param newDrawingEditor  the DrawingEditor which manages the views
	 */
	public ZoomCommand(String name, float scale, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor, true);
		this.scale = scale;
	}


	/** Executes the command */
	public void execute() {
		super.execute();
		zoomView().zoom(scale);
	}


	/**
	 * Sets the zoom factor of the view
	 *
	 * @return   ZoomDrawingView currently active in the editor
	 */
	public ZoomDrawingView zoomView() {
		Object view = super.view();
		if (view instanceof ZoomDrawingView) {
			return (ZoomDrawingView)view;
		}
		throw new JHotDrawRuntimeException("execute should NOT be getting called when view not instanceof ZoomDrawingView");
	}
    void mul(int y, MutableBigInteger z) {
        if (y == 1) {
            z.copyValue(this);
            return;
        }

        if (y == 0) {
            z.clear();
            return;
        }

        // Perform the multiplication word by word
        long ylong = y & LONG_MASK;
        int[] zval = (z.value.length<intLen+1 ? new int[intLen + 1]
                                              : z.value);
        long carry = 0;
        for (int i = intLen-1; i >= 0; i--) {
            long product = ylong * (value[i+offset] & LONG_MASK) + carry;
            zval[i+1] = (int)product;
            carry = product >>> 32;
        }

        if (carry == 0) {
            z.offset = 1;
            z.intLen = intLen;
        } else {
            z.offset = 0;
            z.intLen = intLen + 1;
            zval[0] = (int)carry;
        }
        z.value = zval;
    }


	/**
	 * Gets the scale attribute of the ZoomCommand object
	 *
	 * @return   The scale value
	 */
	public float getScale() {
		return scale;
	}


	/**
	 * Sets the scale attribute of the ZoomCommand object
	 *
	 * @param newScale  The new scale value
	 */
	public void setScale(float newScale) {
		scale = newScale;
	}


	/**
	 * Returns true if the command is executable with the current view
	 *
	 * @return   true iff the view is a ZoomDrawingView
	 */
	protected boolean isExecutableWithView() {
		return (view() instanceof ZoomDrawingView);
	}
}
