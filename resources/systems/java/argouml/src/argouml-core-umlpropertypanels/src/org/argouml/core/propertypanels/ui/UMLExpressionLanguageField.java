/* $Id: UMLExpressionLanguageField.java 19154 2011-03-31 05:28:41Z thn $
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mvw
 *    Thomas Neustupny
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

package org.argouml.core.propertypanels.ui;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.argouml.i18n.Translator;
import org.argouml.ui.LookAndFeelMgr;

/**
 * This text field shows the language of a UML expression.
 */
class UMLExpressionLanguageField extends JTextField implements
    DocumentListener
{

    private UMLExpressionModel model;
    private boolean notifyModel;

    /**
     * Creates a new field that selects the language for an expression.
     *
     * @param m Expression model, should be shared between
     * Language and Body fields
     * @param n Only one of Language and Body fields should
     * forward events to model
     * TODO: MVW: I do not understand that.
     */
    public UMLExpressionLanguageField(UMLExpressionModel m, boolean n)
    {
        model = m;
        notifyModel = n;
        getDocument().addDocumentListener(this);
        setToolTipText(Translator.localize("label.language.tooltip"));
        setFont(LookAndFeelMgr.getInstance().getStandardFont());
        update();
    }

    void update()
    {
        String oldText = getText();
        String newText = model.getLanguage();
        if (oldText == null || newText == null || !oldText.equals(newText))
        {
            if (oldText != newText)
            {
                setText(newText);
            }
        }
        if (newText == null)
        {
            setEditable(false);
        }
    }

    /*
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(final DocumentEvent p1)
    {
        model.setLanguage(getText());
    }

    /*
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(final DocumentEvent p1)
    {
        model.setLanguage(getText());
    }

    /*
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(final DocumentEvent p1)
    {
        model.setLanguage(getText());
    }
}
