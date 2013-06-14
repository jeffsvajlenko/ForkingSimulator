/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CORBA/PUBLIC_MEMBER.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CORBA;

/**
 * Constant to define a public member in the <code>ValueMember</code>
 * class.
 * <P>
 * <code>PUBLIC_MEMBER</code> is one of the two constants of typedef
 * <code>Visibility</code> used in the interface repository
 * to identify visibility of a <code>ValueMember</code> type.
 * The other constant is <code>PRIVATE_MEMBER</code>.
 *
 * @author unattributed
 * @version %I% %G%
 */
public interface PUBLIC_MEMBER
{
    final short value = (short) (1L);
};
