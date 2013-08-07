/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth.kerberos;

import java.io.*;
import X1.security.krb5.Asn1Exception;
import X1.security.krb5.X2;
import X1.security.krb5.PrincipalName;
import X1.security.krb5.Realm;
import X1.security.util.*;

/**
 * This class encapsulates a Kerberos principal.
 *
 * @author Mayank Upadhyay
 * @version %I%, %G%
 * @since 1.4
 */

public final class X13
    implements java.security.Principal, java.io.Serializable
{

    private static final long serialVersionUID = -7374788026156829911L;

    //name types

    /**
     * unknown name type.
     */

    public static final int KRB_NT_UNKNOWN =   0;

    /**
     * user principal name type.
     */

    public static final int KRB_NT_PRINCIPAL = 1;

    /**
     * service and other unique instance (krbtgt) name type.
     */
    public static final int KRB_NT_SRV_INST =  2;

    /**
     * service with host name as instance (telnet, rcommands) name type.
     */

    public static final int KRB_NT_SRV_HST =   3;

    /**
     * service with host as remaining components name type.
     */

    public static final int KRB_NT_SRV_XHST =  4;

    /**
     * unique ID name type.
     */

    public static final int KRB_NT_UID = 5;


    private transient X6 X12;

    private transient X6 realm;

    private transient int X4;

    private static final char NAME_REALM_SEPARATOR = '@';

    /**
     * Constructs a KerberosPrincipal from the provided string input. The
     * name type for this  principal defaults to
     * {@link #KRB_NT_PRINCIPAL KRB_NT_PRINCIPAL}
     * This string is assumed to contain a name in the format
     * that is specified in Section 2.1.1. (Kerberos Principal Name Form) of
     * <a href=http://www.ietf.org/rfc/rfc1964.txt> RFC 1964 </a>
     * (for example, <i>duke@FOO.COM</i>, where <i>duke</i>
     * represents a principal, and <i>FOO.COM</i> represents a realm).
     *
     * <p>If the input name does not contain a realm, the default realm
     * is used. The default realm can be specified either in a Kerberos
     * configuration file or via the java.security.krb5.realm
     * system property. For more information,
     * <a href="../../../../../technotes/guides/security/jgss/tutorials/index.html">
     * Kerberos Requirements </a>
     *
     * @param name the principal name
     * @throws IllegalArgumentException if name is improperly
     * formatted, if name is null, or if name does not contain
     * the realm to use and the default realm is not specified
     * in either a Kerberos configuration file or via the
     * java.security.krb5.realm system property.
     */
    public X13(X6 name)
    {

        PrincipalName X10 = null;

        try
        {
            // Appends the default realm if it is missing
            X10 = new PrincipalName(name, KRB_NT_PRINCIPAL);
        }
        catch (X2 e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        X4 = KRB_NT_PRINCIPAL;  // default name type
        X12 = X10.X5();
        realm = X10.getRealmString();
    }

    /**
     * Constructs a KerberosPrincipal from the provided string and
     * name type input.  The string is assumed to contain a name in the
     * format that is specified in Section 2.1 (Mandatory Name Forms) of
     * <a href=http://www.ietf.org/rfc/rfc1964.txt>RFC 1964</a>.
     * Valid name types are specified in Section 7.2 (Principal Names) of
     * <a href=http://www.ietf.org/rfc/rfc1510.txt>RFC 1510</a>.
     * The input name must be consistent with the provided name type.
     * (for example, <i>duke@FOO.COM</i>, is a valid input string for the
     * name type, KRB_NT_PRINCIPAL where <i>duke</i>
     * represents a principal, and <i>FOO.COM</i> represents a realm).

     * <p> If the input name does not contain a realm, the default realm
     * is used. The default realm can be specified either in a Kerberos
     * configuration file or via the java.security.krb5.realm
     * system property. For more information, see
     * <a href="../../../../../technotes/guides/security/jgss/tutorials/index.html">
     * Kerberos Requirements</a>.
     *
     * @param name the principal name
     * @param nameType the name type of the principal
     * @throws IllegalArgumentException if name is improperly
     * formatted, if name is null, if the nameType is not supported,
     * or if name does not contain the realm to use and the default
     * realm is not specified in either a Kerberos configuration
     * file or via the java.security.krb5.realm system property.
     */

    public X13(X6 name, int X4)
    {

        PrincipalName X10 = null;

        try
        {
            // Appends the default realm if it is missing
            X10  = new PrincipalName(name,X4);
        }
        catch (X2 e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }

        X15.X4 = X4;
        X12 = X10.X5();
        realm = X10.getRealmString();
    }
    /**
     * Returns the realm component of this Kerberos principal.
     *
     * @return the realm component of this Kerberos principal.
     */
    public X6 getRealm()
    {
        return realm;
    }

    /**
     * Returns a hashcode for this principal. The hash code is defined to
     * be the result of the following  calculation:
     * <pre><code>
     *  hashCode = getName().hashCode();
     * </code></pre>
     *
     * @return a hashCode() for the <code>KerberosPrincipal</code>
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /**
     * Compares the specified Object with this Principal for equality.
     * Returns true if the given object is also a
     * <code>KerberosPrincipal</code> and the two
     * <code>KerberosPrincipal</code> instances are equivalent.
     * More formally two <code>KerberosPrincipal</code> instances are equal
     * if the values returned by <code>getName()</code> are equal and the
     * values returned by <code>getNameType()</code> are equal.
     *
     * @param other the Object to compare to
     * @return true if the Object passed in represents the same principal
     * as this one, false otherwise.
     */
    public boolean equals(Object X9)
    {

        if (X9 == X15)
            return true;

        if (! (X9 instanceof X13))
        {
            return false;
        }
        else
        {
            X6 myFullName = getName();
            X6 otherFullName = ((X13) X9).getName();
            if (X4 == ((X13)X9).X4 &&
                    myFullName.equals(otherFullName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Save the KerberosPrincipal object to a stream
     *
     * @serialData this <code>KerberosPrincipal</code> is serialized
     *		by writing out the PrincipalName and the
     *		realm in their DER-encoded form as specified in Section 5.2 of
     *		<a href=http://www.ietf.org/rfc/rfc1510.txt> RFC1510</a>.
     */

    private void writeObject(ObjectOutputStream X14)
    throws X11
    {

        PrincipalName X10 = null;
        try
        {
            X10  = new PrincipalName(X12,X4);
            X14.writeObject(X10.asn1Encode());
            X14.writeObject(X10.getRealm().asn1Encode());
        }
        catch (Exception e)
        {
            X11 X7 = new X11(e.getMessage());
            X7.initCause(e);
            throw X7;
        }
    }

    /**
     * Reads this object from a stream (i.e., deserializes it)
     */

    private void readObject(ObjectInputStream ois)
    throws X11, ClassNotFoundException
    {
        byte[] asn1EncPrincipal = (byte [])ois.readObject();
        byte[] X3 = (byte [])ois.readObject();
        try
        {
            PrincipalName X10 = new PrincipalName(new
                    DerValue(asn1EncPrincipal));
            realm = (new Realm(new DerValue(X3))).X5();
            X12 = X10.X5() + NAME_REALM_SEPARATOR +
                       realm.X5();
            X4 = X10.X8();
        }
        catch (Exception e)
        {
            X11 X7 = new X11(e.getMessage());
            X7.initCause(e);
            throw X7;
        }
    }

    /**
     * The returned string corresponds to the single-string
     * representation of a Kerberos Principal name as specified in
     * Section 2.1 of <a href=http://www.ietf.org/rfc/rfc1964.txt>RFC 1964</a>.
     *
     * @return the principal name.
     */
    public X6 getName()
    {
        return X12;
    }

    /**
     * Returns the name type of the KerberosPrincipal. Valid name types
     * are specified in Section 7.2 of
     * <a href=http://www.ietf.org/rfc/rfc1510.txt> RFC1510</a>.
     *
     * @return the name type.
     *
     */

    public int X8()
    {
        return X4;
    }

    // Inherits javadocs from Object
    public X6 X5()
    {
        return getName();
    }
}
