package com.sun.org.apache.X2.internal.classfile;

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

import com.sun.org.apache.X2.internal.classfile.*;
import com.sun.org.apache.X2.internal.*;

/**
 * Visitor with empty method bodies, can be extended and used in conjunction with the
 * DescendingVisitor class, e.g.
 *
 * By courtesy of David Spencer.
 *
 * @see DescendingVisitor
 * @version $Id: EmptyVisitor.java,v 1.1.2.1 2005/07/31 23:46:31 jeffsuttor Exp $
 *
 */
public class EmptyVisitor implements Visitor
{
    protected EmptyVisitor() { }

    public void visitCode(Code X1) {}
    public void visitCodeException(CodeException X1) {}
    public void visitConstantClass(ConstantClass X1) {}
    public void visitConstantDouble(ConstantDouble X1) {}
    public void visitConstantFieldref(ConstantFieldref X1) {}
    public void visitConstantFloat(ConstantFloat X1) {}
    public void visitConstantInteger(ConstantInteger X1) {}
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref X1) {}
    public void visitConstantLong(ConstantLong X1) {}
    public void visitConstantMethodref(ConstantMethodref X1) {}
    public void visitConstantNameAndType(ConstantNameAndType X1) {}
    public void visitConstantPool(ConstantPool X1) {}
    public void visitConstantString(ConstantString X1) {}
    public void visitConstantUtf8(ConstantUtf8 X1) {}
    public void visitConstantValue(ConstantValue X1) {}
    public void visitDeprecated(Deprecated X1) {}
    public void visitExceptionTable(ExceptionTable X1) {}
    public void visitField(Field X1) {}
    public void visitInnerClass(InnerClass X1) {}
    public void visitInnerClasses(InnerClasses X1) {}
    public void visitJavaClass(JavaClass X1) {}
    public void visitLineNumber(LineNumber X1) {}
    public void visitLineNumberTable(LineNumberTable X1) {}
    public void visitLocalVariable(LocalVariable X1) {}
    public void visitLocalVariableTable(LocalVariableTable X1) {}
    public void visitMethod(Method X1) {}
    public void visitSignature(Signature X1) {}
    public void visitSourceFile(SourceFile X1) {}
    public void visitSynthetic(Synthetic X1) {}
    public void visitUnknown(Unknown X1) {}
    public void visitStackMap(StackMap X1) {}
    public void visitStackMapEntry(StackMapEntry X1) {}
}
