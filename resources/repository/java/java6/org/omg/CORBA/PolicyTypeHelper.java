/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.omg.CORBA;


/**
* The Helper for <tt>PolicyType</tt>.  For more information on
* Helper files, see <a href="doc-files/generatedfiles.html#helper">
* "Generated Files: Helper Files"</a>.<P>
* org/omg/CORBA/PolicyTypeHelper.java
* Generated by the IDL-to-Java compiler (portable), version "3.0"
* from ../../../../../src/share/classes/org/omg/PortableServer/corba.idl
* Saturday, July 17, 1999 12:26:20 AM PDT
*/


// basic Policy definition
abstract public class PolicyTypeHelper
{
    private static String  _id = "IDL:omg.org/CORBA/PolicyType:1.0";

    public static void insert (org.omg.CORBA.Any a, int that)
    {
        org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
        a.type (type ());
        write (out, that);
        a.read_value (out.create_input_stream (), type ());
    }

    public static int extract (org.omg.CORBA.Any a)
    {
        return read (a.create_input_stream ());
    }

    private static org.omg.CORBA.TypeCode __typeCode = null;
    synchronized public static org.omg.CORBA.TypeCode type ()
    {
        if (__typeCode == null)
        {
            __typeCode = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong);
            __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.PolicyTypeHelper.id (), "PolicyType", __typeCode);
        }
        return __typeCode;
    }

    public static String id ()
    {
        return _id;
    }

    public static int read (org.omg.CORBA.portable.InputStream istream)
    {
        int value = (int)0;
        value = istream.read_ulong ();
        return value;
    }

    public static void write (org.omg.CORBA.portable.OutputStream ostream, int value)
    {
        ostream.write_ulong (value);
    }

}
