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
  public Object[][] getContents() {
    Object[][] contents =  new Object[][] {
        // BAD_MSGKEY needs translation
        // BAD_MSGFORMAT needs translation
      { MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
        "La classe serializer ''{0}'' non implementa org.xml.sax.ContentHandler."},

      { MsgKey.ER_RESOURCE_COULD_NOT_FIND,
        "Risorsa [ {0} ] non trovata.\n {1}"},

      { MsgKey.ER_RESOURCE_COULD_NOT_LOAD,
        "Impossibile caricare la risorsa [ {0} ]: {1} \n {2} \n {3}"},

      { MsgKey.ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "Dimensione buffer <=0"},

      { MsgKey.ER_INVALID_UTF16_SURROGATE,
        "Rilevato surrogato UTF-16 non valido: {0} ?"},

      { MsgKey.ER_OIERROR,
        "Errore IO"},

      { MsgKey.ER_ILLEGAL_ATTRIBUTE_POSITION,
        "Impossibile aggiungere l''attributo {0} dopo i nodi secondari o prima che sia prodotto un elemento. L''attributo verr\u00e0 ignorato. "
},

      { MsgKey.ER_NAMESPACE_PREFIX,
        "Lo spazio nomi per il prefisso ''{0}'' non \u00e8 stato dichiarato. "},

        // ER_STRAY_ATTRIBUTE needs translation
      { MsgKey.ER_STRAY_NAMESPACE,
        "Dichiarazione dello spazio nome ''{0}''=''{1}'' al di fuori dell''elemento. "},

      { MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
        "Impossibile caricare ''{0}'' (verificare CLASSPATH); verranno utilizzati i valori predefiniti "},

        // ER_ILLEGAL_CHARACTER needs translation
      { MsgKey.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
        "Impossibile caricare il file delle propriet\u00e0 ''{0}'' per il metodo di emissione ''{1}'' (verificare CLASSPATH)"},

      { MsgKey.ER_INVALID_PORT,
        "Numero di porta non valido"},

      { MsgKey.ER_PORT_WHEN_HOST_NULL,
        "La porta non pu\u00f2 essere impostata se l'host \u00e8 nullo"},

      { MsgKey.ER_HOST_ADDRESS_NOT_WELLFORMED,
        "Host non \u00e8 un'indirizzo corretto"},

      { MsgKey.ER_SCHEME_NOT_CONFORMANT,
        "Lo schema non \u00e8 conforme."},

      { MsgKey.ER_SCHEME_FROM_NULL_STRING,

        "Impossibile impostare lo schema da una stringa nulla"},

      { MsgKey.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
        "Il percorso contiene sequenza di escape non valida"},

      { MsgKey.ER_PATH_INVALID_CHAR,
        "Il percorso contiene un carattere non valido: {0}"},

      { MsgKey.ER_FRAG_INVALID_CHAR,
        "Il frammento contiene un carattere non valido"},

      { MsgKey.ER_FRAG_WHEN_PATH_NULL,
        "Il frammento non pu\u00f2 essere impostato se il percorso \u00e8 nullo"},

      { MsgKey.ER_FRAG_FOR_GENERIC_URI,
        "Il frammento pu\u00f2 essere impostato solo per un URI generico"},

      { MsgKey.ER_NO_SCHEME_IN_URI,
        "Nessuno schema trovato nell''URI: {0}"},

      { MsgKey.ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "Impossibile inizializzare l'URI con i parametri vuoti"},

      { MsgKey.ER_NO_FRAGMENT_STRING_IN_PATH,
        "Il frammento non pu\u00f2 essere specificato sia nel percorso che nel frammento"},

      { MsgKey.ER_NO_QUERY_STRING_IN_PATH,
        "La stringa di interrogazione non pu\u00f2 essere specificata nella stringa di interrogazione e percorso."},

      { MsgKey.ER_NO_PORT_IF_NO_HOST,
        "La porta non pu\u00f2 essere specificata se l'host non \u00e8 specificato"},

      { MsgKey.ER_NO_USERINFO_IF_NO_HOST,
        "Userinfo non pu\u00f2 essere specificato se l'host non \u00e8 specificato"},

      { MsgKey.ER_SCHEME_REQUIRED,
        "Lo schema \u00e8 obbligatorio."}

    };
    return contents;
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
