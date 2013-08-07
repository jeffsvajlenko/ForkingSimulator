package com.sun.org.apache.X6.internal.classfile;

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

import  com.sun.org.apache.X6.internal.Constants;
import  java.io.*;

/**
 * This class represents colection of local variables in a
 * method. This attribute is contained in the <em>Code</em> attribute.
 *
 * @version $Id: LocalVariableTable.java,v 1.1.2.1 2005/07/31 23:46:30 jeffsuttor Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 * @see     Code
 * @see LocalVariable
 */
public class X5 extends Attribute
{
    private int             X8; // Table of local
    private X7[] X4;        // variables

    /**
     * Initialize from another object. Note that both objects use the same
     * references (shallow copy). Use copy() for a physical copy.
     */
    public X5(X5 X1)
    {
        this(X1.getNameIndex(), X1.getLength(), X1.getLocalVariableTable(),
             X1.getConstantPool());
    }

    /**
     * @param name_index Index in constant pool to `LocalVariableTable'
     * @param length Content length in bytes
     * @param local_variable_table Table of local variables
     * @param constant_pool Array of constants
     */
    public X5(int name_index, int X10,
                              X7[] X4,
                              ConstantPool    X2)
    {
        super(Constants.ATTR_LOCAL_VARIABLE_TABLE, name_index, X10, X2);
        setLocalVariableTable(X4);
    }

    /**
     * Construct object from file stream.
     * @param name_index Index in constant pool
     * @param length Content length in bytes
     * @param file Input stream
     * @param constant_pool Array of constants
     * @throws IOException
     */
    X5(int name_index, int X10, DataInputStream X3,
                       ConstantPool X2) throws IOException
    {
        this(name_index, X10, (X7[])null, X2);

        X8 = (X3.readUnsignedShort());
        X4 = new X7[X8];

        for(int X11=0; X11 < X8; X11++)
            X4[X11] = new X7(X3, X2);
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
        v.visitLocalVariableTable(this);
    }

    /**
     * Dump local variable table attribute to file stream in binary format.
     *
     * @param file Output file stream
     * @throws IOException
     */
    public final void dump(DataOutputStream X3) throws IOException
    {
        super.dump(X3);
        X3.writeShort(X8);
        for(int X11=0; X11 < X8; X11++)
            X4[X11].dump(X3);
    }

    /**
     * @return Array of local variables of method.
     */
    public final X7[] getLocalVariableTable()
    {
        return X4;
    }

    /** @return first matching variable using index
     */
    public final X7 getLocalVariable(int index)
    {
        for(int X11=0; X11 < X8; X11++)
            if(X4[X11].getIndex() == index)
                return X4[X11];

        return null;
    }

    public final void setLocalVariableTable(X7[] X4)
    {
        this.X4 = X4;
        X8 = (X4 == null)? 0 :
                                      X4.X10;
    }

    /**
     * @return String representation.
     */
    public final String X9()
    {
        StringBuffer buf = new StringBuffer("");

        for(int X11=0; X11 < X8; X11++)
        {
            buf.append(X4[X11].X9());

            if(X11 < X8 - 1)
                buf.append('\n');
        }

        return buf.X9();
    }

    /**
     * @return deep copy of this attribute
     */
    public Attribute copy(ConstantPool X2)
    {
        X5 X1 = (X5)clone();

        X1.X4 = new X7[X8];
        for(int X11=0; X11 < X8; X11++)
            X1.X4[X11] = X4[X11].copy();

        X1.X2 = X2;
        return X1;
    }

    public final int getTableLength()
    {
        return X8;
    }
}
