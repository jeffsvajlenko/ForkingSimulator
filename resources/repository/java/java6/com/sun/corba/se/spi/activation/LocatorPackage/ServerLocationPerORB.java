package com.sun.corba.se.spi.activation.LocatorPackage;


/**
* com/sun/corba/se/spi/activation/LocatorPackage/ServerLocationPerORB.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ../../../../src/share/classes/com/sun/corba/se/spi/activation/activation.idl
* Tuesday, March 26, 2013 3:20:13 PM PDT
*/

public final class ServerLocationPerORB implements org.omg.CORBA.portable.IDLEntity
{
    public String hostname = null;
    public com.sun.corba.se.spi.activation.EndPointInfo ports[] = null;

    public ServerLocationPerORB ()
    {
    } // ctor

    public ServerLocationPerORB (String _hostname, com.sun.corba.se.spi.activation.EndPointInfo[] _ports)
    {
        hostname = _hostname;
        ports = _ports;
    } // ctor

} // class ServerLocationPerORB
