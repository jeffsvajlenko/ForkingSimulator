package org.omg.PortableInterceptor.ORBInitInfoPackage;


/**
* org/omg/PortableInterceptor/ORBInitInfoPackage/InvalidName.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ../../../../src/share/classes/org/omg/PortableInterceptor/Interceptors.idl
* Tuesday, March 26, 2013 2:15:02 PM GMT-08:00
*/

public final class InvalidName extends org.omg.CORBA.UserException
{

    public InvalidName ()
    {
        super(InvalidNameHelper.id());
    } // ctor


    public InvalidName (String $reason)
    {
        super(InvalidNameHelper.id() + "  " + $reason);
    } // ctor

} // class InvalidName
