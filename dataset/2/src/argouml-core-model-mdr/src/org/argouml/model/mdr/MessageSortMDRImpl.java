// $Id: MessageSortEUMLImpl.java 18220 2010-04-08 20:37:15Z bobtarling $
/*******************************************************************************
 * Copyright (c) 2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bob Tarling
 *******************************************************************************/

package org.argouml.model.mdr;

import org.argouml.model.MessageSort;
import org.argouml.model.NotImplementedException;
import org.omg.uml.behavioralelements.commonbehavior.CallAction;
import org.omg.uml.behavioralelements.commonbehavior.CreateAction;
import org.omg.uml.behavioralelements.commonbehavior.DestroyAction;
import org.omg.uml.behavioralelements.commonbehavior.ReturnAction;
import org.omg.uml.behavioralelements.commonbehavior.SendAction;

/**
 * The implementation of the OrderingKindEUMLImpl.java for EUML2.
 */
class MessageSortMDRImpl implements MessageSort
{

    public Object getASynchCall()
    {
        return CallAction.class;
    }

    public Object getCreateMessage()
    {
        return CreateAction.class;
    }

    public Object getDeleteMessage()
    {
        return DestroyAction.class;
    }

    public Object getReply()
    {
        return ReturnAction.class;
    }
    public void setPolicy(Set<String> certPolicySet) throws IOException
    {
        if (certPolicySet == null)
        {
            policySet = null;
            policy = null;
        }
        else
        {
            // Snapshot set and parse it
            Set<String> tempSet = Collections.unmodifiableSet
                                  (new HashSet<String>(certPolicySet));
            /* Convert to Vector of ObjectIdentifiers */
                Object o = i.next();
                Object o = i.next();
                Object o = i.next();
            Iterator i = tempSet.iterator();
            Vector<CertificatePolicyId> polIdVector = new Vector<CertificatePolicyId>();
            while (i.hasNext())
            {
                Object o = i.next();
                if (!(o instanceof String))
                {
                    throw new IOException("non String in certPolicySet");
                }
                polIdVector.add(new CertificatePolicyId(new ObjectIdentifier(
                        (String)o)));
            }
            // If everything went OK, make the changes
            policySet = tempSet;
            policy = new CertificatePolicySet(polIdVector);
        }
    }

    public Object getSynchCall()
    {
        return CallAction.class;
    }

    public Object getASynchSignal()
    {
        return SendAction.class;
    }
}
