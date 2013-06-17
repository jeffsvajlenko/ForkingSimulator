/* $Id: FigExtend.java 17867 2010-01-12 20:47:04Z linus $
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mvw
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2009 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.uml.diagram.use_case.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Set;

import org.argouml.model.Model;
import org.argouml.notation.providers.uml.NotationUtilityUml;
import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.ui.ArgoFigText;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigTextGroup;
import org.argouml.uml.diagram.ui.PathItemPlacement;
import org.tigris.gef.presentation.ArrowHeadGreater;
import org.tigris.gef.presentation.Fig;

/**
 * A fig for use with extend relationships on use case diagrams.<p>
 *
 * Realised as a dotted line with an open arrow head and the label
 * &laquo;extend&raquo; together with any condition alongside.<p>
 *
 * @author mail@jeremybennett.com
 */
public class FigExtend extends FigEdgeModelElement
{

    private static final int DEFAULT_WIDTH = 90;

    /**
     * Serialization ID - generated by Eclipse for rev. 1.22
     */
    private static final long serialVersionUID = -8026008987096598742L;

    /**
     * The &laquo;extend&raquo; label.<p>
     */
    private ArgoFigText label;

    /**
     * The condition expression.<p>
     */
    private ArgoFigText condition;

    /**
     * The group of label and condition.<p>
     */
    private FigTextGroup fg;


    private ArrowHeadGreater endArrow = new ArrowHeadGreater();

    private void initialize(Object owner)
    {
        // The <<extend>> label.
        // It's not a true stereotype, so don't use the stereotype support
        //int y = getNameFig().getBounds().height;
        int y = Y0 + STEREOHEIGHT;
        label = new ArgoFigText(owner,
                                new Rectangle(X0, y, DEFAULT_WIDTH, STEREOHEIGHT),
                                getSettings(), false);
        y = y + STEREOHEIGHT;
        label.setFilled(false);
        label.setLineWidth(0);
        label.setEditable(false);
        label.setText(getLabel());
        label.calcBounds();

        // Set up FigText to hold the condition.
        condition = new ArgoFigText(owner,
                                    new Rectangle(X0, y, DEFAULT_WIDTH, STEREOHEIGHT),
                                    getSettings(), false);
        y = y + STEREOHEIGHT;
        condition.setFilled(false);
        condition.setLineWidth(0);

        // Join all into a group

        fg = new FigTextGroup(owner, getSettings());

        // UML spec for Extend doesn't call for name nor stereotype
        fg.addFig(label);
        fg.addFig(condition);
        fg.calcBounds();

        // Place in the middle of the line and ensure the line is dashed.  Add
        // an arrow with an open arrow head. Remember that for an extends
        // relationship, the arrow points to the base use case, but because of
        // the way we draw it, that is still the destination end.

        addPathItem(fg, new PathItemPlacement(this, fg, 50, 10));

        setDashed(true);

        setDestArrowHead(endArrow);

        // Make the edge go between nearest points

        setBetweenNearestPoints(true);
    }

    /**
     * Construct an Extend fig.
     *
     * @param owner uml element
     * @param settings rendering settings
     */
    public FigExtend(Object owner, DiagramSettings settings)
    {
        super(owner, settings);
        initialize(owner);
    }

    /**
     * Set a new fig to represent this edge.<p>
     *
     * We invoke the superclass accessor. Then change aspects of the
     * new fig that are not as we want. In this case to use dashed
     * lines.<p>
     *
     * @param f  The fig to use.
     */
    @Override
    public void setFig(Fig f)
    {
        super.setFig(f);

        // Make sure the line is dashed

        setDashed(true);
    }

    /**
     * Define whether the given fig can be edited (it can't).<p>
     *
     * @param f  The fig about which the enquiry is being made. Ignored in this
     *           implementation.
     *
     * @return   <code>false</code> under all circumstances.
     */
    @Override
    protected boolean canEdit(Fig f)
    {
        return false;
    }

    /*
     * @see org.tigris.gef.presentation.Fig#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g)
    {
        endArrow.setLineColor(getLineColor());
        super.paint(g);
    }

    /*
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#updateListeners(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void updateListeners(Object oldOwner, Object newOwner)
    {
        Set<Object[]> listeners = new HashSet<Object[]>();
        if (newOwner != null)
        {
            listeners.add(
                new Object[] {newOwner,
                              new String[] {"condition", "remove"}
                             });
        }
        updateElementListeners(listeners);
    }


    /*
     * The only thing we need to deal with is updating is the condition text.
     *
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    @Override
    protected void modelChanged(PropertyChangeEvent e)
    {
        Object extend = getOwner();
        if (extend == null)
        {
            return;
        }
        super.modelChanged(e);

        if ("condition".equals(e.getPropertyName()))
        {
            renderingChanged();
        }
    }

    @Override
    public void renderingChanged()
    {
        super.renderingChanged();
        updateConditionText();
        updateLabel();
    }

    /**
     * Now sort out the condition text. Use the null string if there is
     * no condition set. The condition is a BooleanExpression,
     * so we show the "body" of it, and ignore the "language".
     */
    protected void updateConditionText()
    {
        if (getOwner() == null)
        {
            return;
        }

        Object c = Model.getFacade().getCondition(getOwner());
        if (c == null)
        {
            condition.setText("");
        }
        else
        {
            Object expr = Model.getFacade().getBody(c);
            if (expr == null)
            {
                condition.setText("");
            }
            else
            {
                condition.setText((String) expr);
            }
        }
        // Let the group recalculate its bounds and then tell GEF we've
        // finished.
        fg.calcBounds();
        endTrans();
    }

    protected void updateLabel()
    {
        label.setText(getLabel());
    }

    private String getLabel()
    {
        return NotationUtilityUml.formatStereotype(
                   "extend",
                   getNotationSettings().isUseGuillemets());
    }

}
