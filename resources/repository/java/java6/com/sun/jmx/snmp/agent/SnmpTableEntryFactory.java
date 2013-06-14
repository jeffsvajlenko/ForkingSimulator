/*
 * %Z%file      %M%
 * %Z%author    Sun Microsystems, Inc.
 * %Z%version   %I%
 * %Z%date      %D%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;

/**
 * This interface is implemented by mibgen generated table objects
 * inheriting from {@link com.sun.jmx.snmp.agent.SnmpTableSupport}.
 * <p>
 * It is used internally by the metadata whenever a remote SNMP manager
 * requests the creation of a new entry through an SNMP SET.
 * </p>
 * <p>
 * At creation, the mibgen generated table object retrieves its
 * corresponding metadata from the MIB and registers with
 * this metadata as a SnmpTableEntryFactory.
 * </p>
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 **/

public interface SnmpTableEntryFactory extends SnmpTableCallbackHandler
{

    /**
     * This method is called by the SNMP runtime whenever a new entry
     * creation is requested by a remote manager.
     *
     * The factory is responsible for instantiating the appropriate MBean
     * and for registering it with the appropriate metadata object.
     *
     * Usually this method will:
     * <ul>
     * <li>Check whether the creation can be accepted
     * <li>Instantiate a new entry
     * <li>Possibly register this entry with the MBeanServer, if needed.
     * <li>Call <code>addEntry()</code> on the given <code>meta</code> object.
     * </ul>
     * This method is usually generated by <code>mibgen</code> on table
     * objects (inheriting from
     * {@link com.sun.jmx.snmp.agent.SnmpTableSupport}). <br>
     *
     * <p><b><i>
     * This method is called internally by the SNMP runtime whenever a
     * new entry creation is requested by a remote SNMP manager.
     * You should never need to call this method directlty.
     * </i></b></p>
     *
     * @param request The SNMP subrequest containing the sublist of varbinds
     *                for the new entry.
     * @param rowOid  The OID indexing the conceptual row (entry) for which
     *                the creation was requested.
     * @param depth   The depth reached in the OID tree (the position at
     *                which the columnar object ids start in the OIDs
     *                included in the varbind).
     * @param meta    The metadata object impacted by the subrequest
     *
     * @exception SnmpStatusException The new entry cannot be created.
     *
     **/
    public void createNewEntry(SnmpMibSubRequest request, SnmpOid rowOid,
                               int depth, SnmpMibTable meta)
    throws SnmpStatusException;
}

