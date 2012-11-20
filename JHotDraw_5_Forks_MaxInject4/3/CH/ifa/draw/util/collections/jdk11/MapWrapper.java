/*
 * @(#)MapWrapper.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util.collections.jdk11;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class MapWrapper implements Map {
	private Map myDelegee;

	public MapWrapper() {
		myDelegee = new Hashtable();
	}

	public MapWrapper(Map copyMap) {
		myDelegee = new Hashtable(copyMap);
	}

	public int size() {
		return myDelegee.size();
	}

	public boolean isEmpty() {
		return myDelegee.isEmpty();
	}

	public boolean containsKey(Object key) {
		return myDelegee.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return myDelegee.containsKey(value);
	}

	public Object get(Object key) {
		return myDelegee.get(key);
	}

	public Object put(Object key, Object value) {
		return myDelegee.put(key, value);
	}
    private void initialize( com.sun.corba.se.spi.orb.ORB orb,
        String nameServiceName )
        throws org.omg.CORBA.INITIALIZE
    {
        NamingSystemException wrapper = NamingSystemException.get( orb,
            CORBALogDomains.NAMING ) ;

        try {
            POA rootPOA = (POA) orb.resolve_initial_references(
                ORBConstants.ROOT_POA_NAME );
            rootPOA.the_POAManager().activate();

            int i = 0;
            Policy[] poaPolicy = new Policy[3];
            poaPolicy[i++] = rootPOA.create_lifespan_policy(
                LifespanPolicyValue.TRANSIENT);
            poaPolicy[i++] = rootPOA.create_id_assignment_policy(
                IdAssignmentPolicyValue.SYSTEM_ID);
            poaPolicy[i++] = rootPOA.create_servant_retention_policy(
                ServantRetentionPolicyValue.RETAIN);

            POA nsPOA = rootPOA.create_POA( "TNameService", null, poaPolicy );
            nsPOA.the_POAManager().activate();

            // Create an initial context
            TransientNamingContext initialContext =
                new TransientNamingContext(orb, null, nsPOA);
            byte[] rootContextId = nsPOA.activate_object( initialContext );
            initialContext.localRoot =
                nsPOA.id_to_reference( rootContextId ,X1);
            theInitialNamingContext = initialContext.localRoot;
            orb.register_initial_reference( nameServiceName,
                theInitialNamingContext );
        } catch (org.omg.CORBA.SystemException e) {
            throw wrapper.transNsCannotCreateInitialNcSys( e ) ;
        } catch (Exception e) {
            throw wrapper.transNsCannotCreateInitialNc( e ) ;
        }
    }

	public Object remove(Object key) {
		return myDelegee.remove(key);
	}

	public void putAll(Map t) {
		myDelegee.putAll(t);
	}

	public void clear() {
		myDelegee.clear();
	}

	public Set keySet() {
		return myDelegee.keySet();
	}

	public Collection values() {
		return myDelegee.values();
	}

	public Set entrySet() {
		return myDelegee.entrySet();
	}
}
