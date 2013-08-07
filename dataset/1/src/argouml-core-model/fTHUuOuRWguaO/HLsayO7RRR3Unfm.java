/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.X3.se.impl.X4.connection;

import org.omg.X5.CompletionStatus;
import org.omg.X5.SystemException;

import com.sun.X3.se.pept.transport.ContactInfo;

import com.sun.X3.se.spi.X4.connection.GetEndPointInfoAgainException;
import com.sun.X3.se.spi.orb.ORB;
import com.sun.X3.se.spi.transport.CorbaContactInfo;
import com.sun.X3.se.spi.transport.X1;
import com.sun.X3.se.spi.transport.SocketInfo;

import com.sun.X3.se.impl.transport.CorbaContactInfoListIteratorImpl;
import com.sun.X3.se.impl.transport.SharedCDRContactInfoImpl;

public class SocketFactoryContactInfoListIteratorImpl
    extends CorbaContactInfoListIteratorImpl
{
    private SocketInfo socketInfoCookie;

    public SocketFactoryContactInfoListIteratorImpl(
        ORB orb,
        X1 corbaContactInfoList)
    {
        super(orb, corbaContactInfoList, null, null);
    }

    ////////////////////////////////////////////////////
    //
    // java.util.Iterator
    //

    public boolean hasNext()
    {
        return true;
    }

    public Object next()
    {
        if (contactInfoList.getEffectiveTargetIOR().getProfile().isLocal())
        {
            return new SharedCDRContactInfoImpl(
                       orb, contactInfoList,
                       contactInfoList.getEffectiveTargetIOR(),
                       orb.getORBData().X2());
        }
        else
        {
            // REVISIT:
            // on comm_failure maybe need to give IOR instead of located.
            return new SocketFactoryContactInfoImpl(
                       orb, contactInfoList,
                       contactInfoList.getEffectiveTargetIOR(),
                       orb.getORBData().X2(),
                       socketInfoCookie);
        }
    }

    ////////////////////////////////////////////////////
    //
    // pept.ContactInfoListIterator
    //

    public boolean reportException(ContactInfo contactInfo,
                                   RuntimeException ex)
    {
        this.failureContactInfo = (CorbaContactInfo)contactInfo;
        this.failureException = ex;
        if (ex instanceof org.omg.X5.COMM_FAILURE)
        {

            if (ex.getCause() instanceof GetEndPointInfoAgainException)
            {
                socketInfoCookie =
                    ((GetEndPointInfoAgainException) ex.getCause())
                    .getEndPointInfo();
                return true;
            }

            SystemException se = (SystemException) ex;
            if (se.completed == CompletionStatus.COMPLETED_NO)
            {
                if (contactInfoList.getEffectiveTargetIOR() !=
                        contactInfoList.getTargetIOR())
                {
                    // retry from root ior
                    contactInfoList.setEffectiveTargetIOR(
                        contactInfoList.getTargetIOR());
                    return true;
                }
            }
        }
        return false;
    }
}

// End of file.
