/*
 * @(#)CollectionsFactory.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import CH.ifa.draw.framework.JHotDrawRuntimeException;

import java.util.*;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public abstract class CollectionsFactory {
	private static String JAVA_UTIL_LIST = "java.util.List";
	private static String COLLECTIONS_FACTORY_PACKAGE = "CH.ifa.draw.util.collections.jdk";

	private static final CollectionsFactory factory = determineCollectionsFactory();

	public abstract List createList();

	public abstract List createList(Collection initList);

	public abstract List createList(int initSize);

	public abstract Map createMap();

	public abstract Map createMap(Map initMap);

	public abstract Set createSet();

	public abstract Set createSet(Set initSet);

	public static CollectionsFactory current() {
		return factory;
	}

	protected static CollectionsFactory determineCollectionsFactory() {
		String jdkVersion = null;
		if (isJDK12()) {
			jdkVersion = "12";
		}
		else {
			jdkVersion = "11";
		}
		return createCollectionsFactory(jdkVersion);
	}

	protected static boolean isJDK12() {
		try {
			Class.forName(JAVA_UTIL_LIST);
			return true;
		}
		catch (ClassNotFoundException e) {
			// ignore
		}
		return false;
	}
  public void list (int how_many, org.omg.CosNaming.BindingListHolder bl, org.omg.CosNaming.BindingIteratorHolder bi)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("list", true);
                $out.write_ulong (how_many);
                $in = _invoke ($out);
                bl.value = org.omg.CosNaming.BindingListHelper.read ($in);
                bi.value = org.omg.CosNaming.BindingIteratorHelper.read ($in);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                list (how_many, bl, bi        );
            } finally {
                _releaseReply ($in);
            }
  } // list

	protected static CollectionsFactory createCollectionsFactory(String jdkVersion) {
		try {
			Class factoryClass = Class.forName(COLLECTIONS_FACTORY_PACKAGE + jdkVersion + ".CollectionsFactoryJDK" + jdkVersion);
			return (CollectionsFactory)factoryClass.newInstance();
		}
		catch (ClassNotFoundException e) {
			throw new JHotDrawRuntimeException(e);
		}
		catch (InstantiationException e) {
			throw new JHotDrawRuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new JHotDrawRuntimeException(e);
		}
	}
}
