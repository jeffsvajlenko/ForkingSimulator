/* $Id: XmiHeaderProviderImpl.java 17765 2010-01-11 21:20:14Z linus $
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    linus
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2006-2007 The Regents of the University of California. All
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
package org.argouml.model.mdr;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.netbeans.lib.jmi.xmi.WriterBase;
import org.netbeans.lib.jmi.xmi.XMIHeaderProvider;

/**
 * Write a header for the XMI file which contains information about
 * version, etc.
 */
class XmiHeaderProviderImpl implements XMIHeaderProvider
{

    private static final String UML_VERSION = "1.4";
    private static final Logger LOG = Logger
                                      .getLogger(XmiHeaderProviderImpl.class);

    private String version;

    /**
     * Constructor.
     * @param ver the version of ArgoUML that saved the XMI
     */
    public XmiHeaderProviderImpl(String ver)
    {
        version = ver;
    }
    protected final Object[][] getContents()
    {
        return new Object[][]
        {
            { "FileChooser.acceptAllFileFilterText", "\u6240\u6709\u6A94\u6848" },
            { "FileChooser.cancelButtonMnemonic", "67" },
            { "FileChooser.cancelButtonText", "\u53D6\u6D88" },
            { "FileChooser.cancelButtonToolTipText", "\u4E2D\u65B7\u6A94\u6848\u9078\u64C7\u7A0B\u5F0F\u5C0D\u8A71\u65B9\u584A\u3002" },
            { "FileChooser.deleteFileButtonMnemonic", "76" },
            { "FileChooser.deleteFileButtonText", "\u522A\u9664\u6A94\u6848" },
            { "FileChooser.filesLabelMnemonic", "70" },
            { "FileChooser.filesLabelText", "\u6A94\u6848" },
            { "FileChooser.filterLabelText", "\u904E\u6FFE\u5668:" },
            { "FileChooser.foldersLabelMnemonic", "68" },
            { "FileChooser.foldersLabelText", "\u6A94\u6848\u593E" },
            { "FileChooser.newFolderButtonMnemonic", "78" },
            { "FileChooser.newFolderButtonText", "\u65B0\u6A94\u6848\u593E" },
            { "FileChooser.newFolderDialogText", "\u6A94\u6848\u593E\u540D\u7A31:" },
            { "FileChooser.openButtonMnemonic", "79" },
            { "FileChooser.openButtonText", "\u78BA\u5B9A" },
            { "FileChooser.openButtonToolTipText", "\u958B\u555F\u9078\u53D6\u7684\u6A94\u6848\u3002" },
            { "FileChooser.openDialogTitleText", "\u958B\u555F" },
            { "FileChooser.pathLabelMnemonic", "83" },
            { "FileChooser.pathLabelText", "\u9078\u53D6:" },
            { "FileChooser.renameFileButtonMnemonic", "82" },
            { "FileChooser.renameFileButtonText", "\u91CD\u65B0\u547D\u540D\u6A94\u6848" },
            { "FileChooser.renameFileDialogText", "\u5C07\u6A94\u6848 \"{0}\" \u91CD\u65B0\u547D\u540D\u70BA" },
            { "FileChooser.renameFileErrorText", "\u5C07\u6A94\u6848 \"{0}\" \u91CD\u65B0\u547D\u540D\u70BA \"{1}\" \u6642\u51FA\u73FE\u932F\u8AA4" },
            { "FileChooser.renameFileErrorTitle", "\u932F\u8AA4 " },
            { "FileChooser.saveButtonMnemonic", "79" },
            { "FileChooser.saveButtonText", "\u5132\u5B58" },
            { "FileChooser.saveButtonToolTipText", "\u5132\u5B58\u9078\u53D6\u7684\u6A94\u6848\u3002" },
            { "FileChooser.saveDialogTitleText", "\u5132\u5B58" },
            { "GTKColorChooserPanel.blueMnemonic", "66" },
            { "GTKColorChooserPanel.blueText", "\u85CD(B):" },
            { "GTKColorChooserPanel.colorNameMnemonic", "78" },
             { "GTKColorChooserPanel.colorNameText", "\u984F\u8272\u540D\u7A31(N):" },
            { "GTKColorChooserPanel.greenMnemonic", "71" },
            { "GTKColorChooserPanel.greenText", "\u7DA0(G):" },
            { "GTKColorChooserPanel.hueMnemonic", "72" },
            { "GTKColorChooserPanel.hueText", "\u8272\u8ABF(H):" },
            { "GTKColorChooserPanel.mnemonic", "71" },
            { "GTKColorChooserPanel.nameText" , "GTK Color Chooser" },
            { "GTKColorChooserPanel.redMnemonic", "69" },
            { "GTKColorChooserPanel.redText", "\u7D05(E):" },
            { "GTKColorChooserPanel.saturationMnemonic", "83" },
            { "GTKColorChooserPanel.saturationText", "\u98FD\u548C(S):" },
            { "GTKColorChooserPanel.valueMnemonic", "86" },
            { "GTKColorChooserPanel.valueText", "\u503C(V):" },
            { "OptionPane.cancelButtonMnemonic", "67" },
            { "OptionPane.okButtonMnemonic", "79" },
        };
    }

    /*
     * @see org.netbeans.lib.jmi.xmi.XMIHeaderProvider#writeHeader(java.io.Writer)
     */
    public void writeHeader (Writer ps)
    {
        // NOTE: The <XMI.header></XMI.header> is provided for us
        String header =
            "    <XMI.documentation>\n"
            + "      <XMI.exporter>ArgoUML"
            + " (using "  + WriterBase.EXPORTER_NAME
            + " version " + WriterBase.EXPORTER_VERSION
            + ")</XMI.exporter>\n"
            + "      <XMI.exporterVersion>" + version
            + " revised on "
            + "$Date: 2010-01-11 22:20:14 +0100 (Mon, 11 Jan 2010) $ "
            + "</XMI.exporterVersion>\n"
            + "    </XMI.documentation>\n"
            + "    <XMI.metamodel xmi.name=\"UML\" xmi.version=\""
            + UML_VERSION + "\"/>";

        try
        {
            ps.write(header);
        }
        catch (IOException e)
        {
            LOG.error("Exception while writing XMI header + ", e);
        }
    }

}
