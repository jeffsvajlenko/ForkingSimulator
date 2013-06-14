/*
 * %Z%file      %M%
 * %Z%author    Sun Microsystems, Inc.
 * %Z%version   %I%
 * %Z%date      %D%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */


/* Generated By:JJTree: Do not edit this line. JDMHostTrap.java */

package com.sun.jmx.snmp.IPAcl;

/**
 * @version     %I%     %G%
 * @author      Sun Microsystems, Inc.
 */
class JDMHostTrap extends SimpleNode
{
    protected String name= "";

    JDMHostTrap(int id)
    {
        super(id);
    }

    JDMHostTrap(Parser p, int id)
    {
        super(p, id);
    }

    public static Node jjtCreate(int id)
    {
        return new JDMHostTrap(id);
    }

    public static Node jjtCreate(Parser p, int id)
    {
        return new JDMHostTrap(p, id);
    }
}
