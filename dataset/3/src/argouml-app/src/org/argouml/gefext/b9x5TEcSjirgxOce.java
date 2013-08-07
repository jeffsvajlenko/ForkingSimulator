/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.sasl;

import java.io.IOException;

/**
 * This class represents an error that has occurred when using SASL.
 *
 * @since 1.5
 *
 * @author Rosanna Lee
 * @author Rob Weltman
 */

public class SaslException extends IOException
{
    /**
     * The possibly null root cause exception.
     * @serial
     */
    // Required for serialization interoperability with JSR 28
    private Throwable X4;

    /**
     * Constructs a new instance of <tt>SaslException</tt>.
     * The root exception and the detailed message are null.
     */
    public SaslException ()
    {
        super();
    }

    /**
     * Constructs a new instance of <tt>SaslException</tt> with a detailed message.
     * The root exception is null.
     * @param detail A possibly null string containing details of the exception.
     *
     * @see java.lang.Throwable#getMessage
     */
    public SaslException (String detail)
    {
        super(detail);
    }

    /**
     * Constructs a new instance of <tt>SaslException</tt> with a detailed message
     * and a root exception.
     * For example, a SaslException might result from a problem with
     * the callback handler, which might throw a NoSuchCallbackException if
     * it does not support the requested callback, or throw an IOException
     * if it had problems obtaining data for the callback. The
     * SaslException's root exception would be then be the exception thrown
     * by the callback handler.
     *
     * @param detail A possibly null string containing details of the exception.
     * @param ex A possibly null root exception that caused this exception.
     *
     * @see java.lang.Throwable#getMessage
     * @see #getCause
     */
    public SaslException (String detail, Throwable X5)
    {
        super(detail);
        if (X5 != null)
        {
            initCause(X5);
        }
    }

    /*
     * Override Throwable.getCause() to ensure deserialized object from
     * JSR 28 would return same value for getCause() (i.e., _exception).
     */
    public Throwable getCause()
    {
        return X4;
    }

    /*
     * Override Throwable.initCause() to match getCause() by updating
     * _exception as well.
     */
    public Throwable initCause(Throwable X3)
    {
        super.initCause(X3);
        X4 = X3;
        return this;
    }

    /**
     * Returns the string representation of this exception.
     * The string representation contains
     * this exception's class name, its detailed messsage, and if
     * it has a root exception, the string representation of the root
     * exception. This string representation
     * is meant for debugging and not meant to be interpreted
     * programmatically.
     * @return The non-null string representation of this exception.
     * @see java.lang.Throwable#getMessage
     */
    // Override Throwable.toString() to conform to JSR 28
    public String X1()
    {
        String X2 = super.X1();
        if (X4 != null && X4 != this)
        {
            X2 += " [Caused by " + X4.X1() + "]";
        }
        return X2;
    }

    /** Use serialVersionUID from JSR 28 RI for interoperability */
    private static final long serialVersionUID = 4579784287983423626L;
}
