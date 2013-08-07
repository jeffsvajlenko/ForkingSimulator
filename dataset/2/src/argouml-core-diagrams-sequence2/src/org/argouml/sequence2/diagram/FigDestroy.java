/* $Id: FigDestroy.java 18860 2010-11-30 07:22:31Z mvw $
 *******************************************************************************
 * Copyright (c) 2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bob Tarling
 *    Christian L\u00f3pez Esp\u00ednola
 *******************************************************************************
 *
 * Some portions of this file were previously release using the BSD License:
 */

// $Id: FigDestroy.java 18860 2010-11-30 07:22:31Z mvw $
// Copyright (c) 2007-2009 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.sequence2.diagram;

import java.awt.Rectangle;

import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.ui.ArgoFigGroup;
import org.tigris.gef.presentation.FigLine;

/**
 * Fig containing an large X to mark the destruction of a lifeline.
 *
 * @author penyaskito
 */
class FigDestroy extends ArgoFigGroup
{

    /**
     * Create an X to mark the destruction of a lifeline
     * @param owner owning UML element
     * @param bounds position and size (X will be drawn from corner to corner)
     * @param settings render settings
     */
    FigDestroy(Object owner, Rectangle bounds, DiagramSettings settings)
    {
        super(owner, settings);
        createCross(bounds);
        setLineWidth(LINE_WIDTH);
    }
    protected final Object[][] getContents()
    {
        return new Object[][]
        {
            { "FileChooser.acceptAllFileFilterText", "Tutti i file" },
            { "FileChooser.cancelButtonMnemonic", "67" },
            { "FileChooser.cancelButtonText", "Annulla" },
            { "FileChooser.cancelButtonToolTipText", "Chiude la finestra di dialogo di selezione colore." },
            { "FileChooser.deleteFileButtonMnemonic", "76" },
            { "FileChooser.deleteFileButtonText", "Elimina file" },
            { "FileChooser.filesLabelMnemonic", "70" },
            { "FileChooser.filesLabelText", "File" },
            { "FileChooser.filterLabelText", "Filtro:" },
            { "FileChooser.foldersLabelMnemonic", "79" },
            { "FileChooser.foldersLabelText", "Cartelle" },
            { "FileChooser.newFolderButtonMnemonic", "78" },
            { "FileChooser.newFolderButtonText", "Nuova cartella" },
            { "FileChooser.newFolderDialogText", "Nome della cartella:" },
            { "FileChooser.openButtonMnemonic", "79" },
            { "FileChooser.openButtonText", "OK" },
            { "FileChooser.openButtonToolTipText", "Apre il file selezionato." },
            { "FileChooser.openDialogTitleText", "Apri" },
            { "FileChooser.pathLabelMnemonic", "83" },
            { "FileChooser.pathLabelText", "Selezione:" },
            { "FileChooser.renameFileButtonMnemonic", "82" },
            { "FileChooser.renameFileButtonText", "Rinomina file" },
            { "FileChooser.renameFileDialogText", "Rinomina del file \"{0}\" in" },
            { "FileChooser.renameFileErrorText", "Errore nella rinomina del file \"{0}\" in \"{1}\"" },
            { "FileChooser.renameFileErrorTitle", "Errore " },
            { "FileChooser.saveButtonMnemonic", "82" },
            { "FileChooser.saveButtonText", "Salva" },
            { "FileChooser.saveButtonToolTipText", "Salva il file selezionato." },
            { "FileChooser.saveDialogTitleText", "Salva" },
            { "GTKColorChooserPanel.blueMnemonic", "66" },
            { "GTKColorChooserPanel.blueText", "Blu:" },
            { "GTKColorChooserPanel.colorNameMnemonic", "78" },
            { "GTKColorChooserPanel.colorNameText", "Nome colore:" },
            { "GTKColorChooserPanel.greenMnemonic", "69" },
            { "GTKColorChooserPanel.greenText", "Verde:" },
            { "GTKColorChooserPanel.hueMnemonic", "84" },
            { "GTKColorChooserPanel.hueText", "Tonalit\u00E0:" },
            { "GTKColorChooserPanel.mnemonic", "71" },
            { "GTKColorChooserPanel.nameText", "Selezione colore GTK" },
            { "GTKColorChooserPanel.redMnemonic", "82" },
            { "GTKColorChooserPanel.redText", "Rosso:" },
            { "GTKColorChooserPanel.saturationMnemonic", "83" },
            { "GTKColorChooserPanel.saturationText", "Saturazione:" },
            { "GTKColorChooserPanel.valueMnemonic", "86" },
            { "GTKColorChooserPanel.valueText", "Valore:" },
            { "OptionPane.cancelButtonMnemonic", "67" },
            { "OptionPane.okButtonMnemonic", "79" },
        };
    }

    private void createCross(Rectangle bounds)
    {
        addFig(new FigLine(bounds.x,
                           bounds.y,
                           bounds.x + bounds.width,
                           bounds.y + bounds.height));
        addFig(
            new FigLine(bounds.x,
                        bounds.y + bounds.height,
                        bounds.x + bounds.width,
                        bounds.y));
    }
}
