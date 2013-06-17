/* $Id: SelectionActor.java 17867 2010-01-12 20:47:04Z linus $
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

// Copyright (c) 1996-2008 The Regents of the University of California. All
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

import javax.swing.Icon;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.model.Model;
import org.argouml.uml.diagram.ui.SelectionNodeClarifiers2;
import org.tigris.gef.presentation.Fig;

/**
 * @author jrobbins@ics.uci.edu
 */
public class SelectionActor extends SelectionNodeClarifiers2
{

    /**
     * The icon for an association.
     */
    private static Icon associationIcon =
        ResourceLoaderWrapper.lookupIconResource("Association");

    private static Icon generalizationIcon =
        ResourceLoaderWrapper.lookupIconResource("Generalization");

    private static Icon icons[] =
    {
        generalizationIcon,
        generalizationIcon,
        associationIcon,
        associationIcon,
        null,
    };

// TODO: I18N required
    private static String instructions[] =
    {
        "Add a more general Actor",
        "Add a more specialized Actor",
        "Add an associated use case",
        "Add an associated use case",
        null,
        "Move object(s)",
    };

    private static Object edgeType[] =
    {
        Model.getMetaTypes().getGeneralization(),
        Model.getMetaTypes().getGeneralization(),
        Model.getMetaTypes().getAssociation(),
        Model.getMetaTypes().getAssociation(),
        null,
    };

    /**
     * Construct a new SelectionActor for the given Fig.
     *
     * @param f The given Fig.
     */
    public SelectionActor(Fig f)
    {
        super(f);
    }

    @Override
    protected Icon[] getIcons()
    {
        if (Model.getModelManagementHelper().isReadOnly(
                    getContent().getOwner()))
        {
            return new Icon[] {null, generalizationIcon, null, null, null };
        }
        return icons;
    }

    @Override
    protected String getInstructions(int index)
    {
        return instructions[index - BASE];
    }

    @Override
    protected Object getNewEdgeType(int index)
    {
        return edgeType[index - BASE];
    }

    @Override
    protected Object getNewNodeType(int index)
    {
        if (index == TOP || index == BOTTOM)
        {
            return Model.getMetaTypes().getActor();
        }
        else
        {
            return Model.getMetaTypes().getUseCase();
        }
    }

    @Override
    protected Object getNewNode(int index)
    {
        if (index == 0)
        {
            index = getButton();
        }
        if (index == TOP || index == BOTTOM)
        {
            return Model.getUseCasesFactory().createActor();
        }
        else
        {
            return Model.getUseCasesFactory().createUseCase();
        }
    }

    @Override
    protected boolean isReverseEdge(int index)
    {
        if (index == BOTTOM || index == LEFT)
        {
            return true;
        }
        return false;
    }

}

