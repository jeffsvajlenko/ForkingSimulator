/*
 * @(#)ReverseFigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.ReverseListEnumerator;
import CH.ifa.draw.framework.*;

import java.util.Iterator;
import java.util.List;

/**
 * An Enumeration that enumerates a Collection of figures back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
public final class ReverseFigureEnumerator implements FigureEnumeration {
	private Iterator myIterator;
	private List myInitialList;

	public ReverseFigureEnumerator(List l) {
		myInitialList = l;
		reset();
	}

	/**
	 * Returns true if the enumeration contains more elements; false
	 * if its empty.
	 */
	public boolean hasNextFigure() {
		return myIterator.hasNext();
	}

	/**
	 * Returns the next element casted as a figure of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 * @exception java.util.NoSuchElementException If no more elements exist.
	 */
	public Figure nextFigure() {
		return (Figure)myIterator.next();
	}
  public Object[][] getContents() {
    Object[][] contents =  new Object[][] {
        // BAD_MSGKEY needs translation
        // BAD_MSGFORMAT needs translation
      { MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
        "La classe serializer ''{0}'' non implementa org.xml.sax.ContentHandler."},

      { MsgKey.ER_RESOURCE_COULD_NOT_FIND,         "Risorsa [ {0} ] non trovata.\n {1}"},

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

	/**
	 * Reset the enumeration so it can be reused again. However, the
	 * underlying collection might have changed since the last usage
	 * so the elements and the order may vary when using an enumeration
	 * which has been reset.
	 */
	public void reset() {
		myIterator = new ReverseListEnumerator(myInitialList);
	}
}
