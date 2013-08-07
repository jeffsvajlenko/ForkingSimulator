/* $Id: SubsystemUtility.java 19614 2011-07-20 12:10:13Z linus $
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2007 The Regents of the University of California. All
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

package org.argouml.application;

import org.argouml.application.api.AbstractArgoJPanel;
import org.argouml.application.api.GUISettingsTabInterface;
import org.argouml.application.api.InitSubsystem;
import org.argouml.ui.DetailsPane;
import org.argouml.ui.GUI;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.TabToDoTarget;

/**
 * Utility class for subsystem management.
 *
 * @author Michiel
 */
public class SubsystemUtility
{

    /**
     * The use of this method in the top-level package
     * prevents that the subsystem would depend on the GUI.
     *
     * @param subsystem the subsystem to be initialised
     */
    static void initSubsystem(InitSubsystem subsystem)
    {
        subsystem.init();
        for (GUISettingsTabInterface tab : subsystem.getSettingsTabs())
        {
            // TODO: This work should be deferred until actually
            // needed for display
            GUI.getInstance().addSettingsTab(tab);
        }
        for (GUISettingsTabInterface tab : subsystem.getProjectSettingsTabs())
        {
            // TODO: This work should be deferred until actually
            // needed for display
            GUI.getInstance().addProjectSettingsTab(tab);
        }
        for (AbstractArgoJPanel tab : subsystem.getDetailsTabs())
        {
            /* All tabs are added at the end, except a TabToDoTarget: */
            ((DetailsPane) ProjectBrowser.getInstance().getDetailsPane())
            .addTab(tab, !(tab instanceof TabToDoTarget));
        }
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

}
