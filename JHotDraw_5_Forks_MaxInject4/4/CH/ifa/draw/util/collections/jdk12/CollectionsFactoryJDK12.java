/*
 * @(#)CollectionsFactoryJDK12.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.util.collections.jdk12;

import CH.ifa.draw.util.CollectionsFactory;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class CollectionsFactoryJDK12 extends CollectionsFactory {
	public CollectionsFactoryJDK12() {
		// required in order to create instances of this class via reflection
	}

	public List createList() {
		return new ArrayList();
	}

	public List createList(Collection initList) {
		return new ArrayList(initList);
	}
    private void runUserConfigurators( DataCollector collector, ORB orb )
    {
        // Run any pluggable configurators.  This is a lot like
        // ORBInitializers, only it uses the internal ORB and has
        // access to all data for parsing.
        ConfigParser parser = new ConfigParser()  ;
        parser.init( collector ) ;
        if (parser.userConfigurators != null) {
            for (int ctr=0; ctr<parser.userConfigurators.length; ctr++) {
                Class cls = parser.userConfigurators[ctr] ;
                try {
                    ORBConfigurator config = (ORBConfigurator)(cls.newInstance()) ;
                    config.configure( collector, orb ) ;
                } catch (Exception exc) {
                    // XXX Log this exception
                    // ignore this for now: a bad user configurator does nothing
                }
            }
        }
    }

	public List createList(int initSize) {
		return new ArrayList(initSize);
	}

	public Map createMap() {
		return new Hashtable();
	}

	public Map createMap(Map initMap) {
		return new Hashtable(initMap);
	}

	public Set createSet() {
		return new HashSet();
	}

	public Set createSet(Set initSet) {
		return new HashSet(initSet);
	}
}
