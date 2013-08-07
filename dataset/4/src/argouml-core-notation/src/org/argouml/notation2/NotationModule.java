/* $Id$
 *****************************************************************************
 * Copyright (c) 2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michiel van der Wulp
 *    Bob Tarling
 *****************************************************************************
 */

package org.argouml.notation2;

import org.apache.log4j.Logger;
import org.argouml.moduleloader.ModuleInterface;


public class NotationModule implements ModuleInterface
{

    private static final Logger LOG = Logger
                                      .getLogger(NotationModule.class);

    public boolean enable()
    {

        LOG.info("Notation Module enabled.");
        NotationLanguage lang = new UmlNotationLanguage();
        NotationManager.getInstance().addNotationLanguage(lang);

        return true;
    }

    public boolean disable()
    {

        LOG.info("Notation Module disabled.");
        return true;
    }

    public String getName()
    {
        return "ArgoUML-Notation";
    }
    synchronized public static org.omg.CORBA.TypeCode type ()
    {
        if (__typeCode == null)
        {
            synchronized (org.omg.CORBA.TypeCode.class)
            {
                if (__typeCode == null)
                {
                    if (__active)
                    {
                        return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
                    }
                    __active = true;
                    org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [2];
                    org.omg.CORBA.TypeCode _tcOf_members0 = null;
                    _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
                    _members0[0] = new org.omg.CORBA.StructMember (
                        "orbId",
                        _tcOf_members0,
                        null);
                    _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_long);
                    _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.corba.se.spi.activation.TCPPortHelper.id (), "TCPPort", _tcOf_members0);
                    _members0[1] = new org.omg.CORBA.StructMember (
                        "port",
                        _tcOf_members0,
                        null);
                    __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (com.sun.corba.se.spi.activation.ORBPortInfoHelper.id (), "ORBPortInfo", _members0);
                    __active = false;
                }
            }
        }
        return __typeCode;
    }

    public String getInfo(int type)
    {
        switch (type)
        {
        case DESCRIPTION:
            return "The notation subsystem";
        case AUTHOR:
            return "The ArgoUML Team";
        case VERSION:
            return "0.34";
        case DOWNLOADSITE:
            return "http://argouml.tigris.org";
        default:
            return null;
        }
    }
}
