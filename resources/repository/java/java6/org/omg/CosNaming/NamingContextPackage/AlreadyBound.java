package org.omg.CosNaming.NamingContextPackage;


/**
* org/omg/CosNaming/NamingContextPackage/AlreadyBound.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ../../../../src/share/classes/org/omg/CosNaming/nameservice.idl
* Tuesday, March 26, 2013 2:15:02 PM GMT-08:00
*/

public final class AlreadyBound extends org.omg.CORBA.UserException
{

    public AlreadyBound ()
    {
        super(AlreadyBoundHelper.id());
    } // ctor


    public AlreadyBound (String $reason)
    {
        super(AlreadyBoundHelper.id() + "  " + $reason);
    } // ctor

} // class AlreadyBound
