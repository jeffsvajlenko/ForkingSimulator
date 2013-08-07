package com.sun.X7.apache.bcel.internal.classfile;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache BCEL" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache BCEL", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import  com.sun.X7.apache.bcel.internal.Constants;
import  java.io.*;

/**
 * This class represents a stack map attribute used for
 * preverification of Java classes for the <a
 * href="http://java.sun.com/j2me/"> Java 2 Micro Edition</a>
 * (J2ME). This attribute is used by the <a
 * href="http://java.sun.com/products/cldc/">KVM</a> and contained
 * within the Code attribute of a method. See CLDC specification
 * 5.3.1.2
 *
 * @version $Id: StackMap.java,v 1.1.2.1 2005/07/31 23:46:28 jeffsuttor Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 * @see     Code
 * @see     StackMapEntry
 * @see     StackMapType
 */
public final class StackMap extends Attribute implements Node
{
    private int             X9;
    private X5[] X8; // Table of stack map entries

    /*
     * @param name_index Index of name
     * @param length Content length in bytes
     * @param map Table of stack map entries
     * @param constant_pool Array of constants
     */
    public StackMap(int name_index, int length,  X5[] X8,
                    ConstantPool constant_pool)
    {
        super(Constants.ATTR_STACK_MAP, name_index, length, constant_pool);

        setStackMap(X8);
    }

    /**
     * Construct object from file stream.
     * @param name_index Index of name
     * @param length Content length in bytes
     * @param file Input stream
     * @throws IOException
     * @param constant_pool Array of constants
     */
    StackMap(int name_index, int length, DataInputStream X6,
             ConstantPool constant_pool) throws X3
    {
        X10(name_index, length, (X5[])null, constant_pool);

        X9 = X6.readUnsignedShort();
        X8 = new X5[X9];

        for(int X11=0; X11 < X9; X11++)
            X8[X11] = new X5(X6, constant_pool);
    }

    /**
     * Dump line number table attribute to file stream in binary format.
     *
     * @param file Output file stream
     * @throws IOException
     */
    public final void X1(DataOutputStream X6) throws X3
    {
        super.X1(X6);
        X6.writeShort(X9);
        for(int X11=0; X11 < X9; X11++)
            X8[X11].X1(X6);
    }

    /**
     * @return Array of stack map entries
     */
    public final X5[] getStackMap()
    {
        return X8;
    }

    /**
     * @param map Array of stack map entries
     */
    public final void setStackMap(X5[] X8)
    {
        X10.X8 = X8;

        X9 = (X8 == null)? 0 : X8.length;
    }

    /**
     * @return String representation.
     */
    public final String toString()
    {
        StringBuffer X2 = new StringBuffer("StackMap(");

        for(int X11=0; X11 < X9; X11++)
        {
            X2.append(X8[X11].toString());

            if(X11 < X9 - 1)
                X2.append(", ");
        }

        X2.append(')');

        return X2.toString();
    }

    /**
     * @return deep copy of this attribute
     */
    public Attribute copy(ConstantPool constant_pool)
    {
        StackMap c = (StackMap)clone();

        c.X8 = new X5[X9];
        for(int X11=0; X11 < X9; X11++)
            c.X8[X11] = X8[X11].copy();

        c.constant_pool = constant_pool;
        return c;
    }

    /**
     * Called by objects that are traversing the nodes of the tree implicitely
     * defined by the contents of a Java class. I.e., the hierarchy of methods,
     * fields, attributes, etc. spawns a tree of objects.
     *
     * @param v Visitor object
     */
    public void accept(Visitor v)
    {
        v.visitStackMap(X10);
    }

    public final int getMapLength()
    {
        return X9;
    }
}
