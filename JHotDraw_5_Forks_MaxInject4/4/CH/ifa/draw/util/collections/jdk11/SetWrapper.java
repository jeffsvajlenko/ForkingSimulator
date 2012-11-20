/*
 * @(#)SetWrapper.java
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
public class SetWrapper implements Set {
	private Hashtable myDelegee;

	public SetWrapper() {
		myDelegee = new Hashtable();
	}

	public SetWrapper(Set initSet) {
		myDelegee = new Hashtable();
		Iterator iter = initSet.iterator();
		while (iter.hasNext()) {
			add(iter.next());
		}
	}

	public int size() {
		return myDelegee.size();
	}

	public boolean isEmpty() {
		return myDelegee.isEmpty();
	}

	public boolean contains(Object o) {
		return myDelegee.containsKey(o);
	}

	public Iterator iterator() {
		return new IteratorWrapper(myDelegee.elements());
	}

	public Object[] toArray() {
		return new Object[0];
	}

	public Object[] toArray(Object a[]) {
		return new Object[0];
	}

	public boolean add(Object o) {
		return myDelegee.put(o, o) == null;
	}
    public DOMParserImpl (XMLParserConfiguration config) {
        super (config);

        // add recognized features
        final String[] domRecognizedFeatures = {
            Constants.DOM_CANONICAL_FORM,
            Constants.DOM_CDATA_SECTIONS,
            Constants.DOM_CHARSET_OVERRIDES_XML_ENCODING,
            Constants.DOM_INFOSET,
            Constants.DOM_NAMESPACE_DECLARATIONS,
            Constants.DOM_SPLIT_CDATA,
            Constants.DOM_SUPPORTED_MEDIATYPES_ONLY,
            Constants.DOM_CERTIFIED,
            Constants.DOM_WELLFORMED,
            Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS,
        };

        fConfiguration.addRecognizedFeatures (domRecognizedFeatures);

        // turn off deferred DOM
        fConfiguration.setFeature (DEFER_NODE_EXPANSION, false);

        // Set values so that the value of the
        // infoset parameter is true (its default value).
        //
        // true: namespace-declarations, well-formed,
        // element-content-whitespace, comments, namespaces
        //
        // false: validate-if-schema, entities,
        // datatype-normalization, cdata-sections

        fConfiguration.setFeature(Constants.DOM_NAMESPACE_DECLARATIONS, true);
        fConfiguration.setFeature(Constants.DOM_WELLFORMED, true);
        fConfiguration.setFeature(INCLUDE_COMMENTS_FEATURE, true);
        fConfiguration.setFeature(INCLUDE_IGNORABLE_WHITESPACE, true);
        fConfiguration.setFeature(NAMESPACES, true);

        fConfiguration.setFeature(DYNAMIC_VALIDATION, false);
        fConfiguration.setFeature(CREATE_ENTITY_REF_NODES, false);
        fConfiguration.setFeature(CREATE_CDATA_NODES_FEATURE, false);

        // set other default values
        fConfiguration.setFeature (Constants.DOM_CANONICAL_FORM, false);
        fConfiguration.setFeature (Constants.DOM_CHARSET_OVERRIDES_XML_ENCODING, true);
        fConfiguration.setFeature (Constants. DOM_SPLIT_CDATA, true);
        fConfiguration.setFeature (Constants.DOM_SUPPORTED_MEDIATYPES_ONLY, false);
        fConfiguration.setFeature (Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS, true);

        // REVISIT: by default Xerces assumes that input is certified.
        //          default is different from the one specified in the DOM spec
        fConfiguration.setFeature (Constants.DOM_CERTIFIED, true);

        // Xerces datatype-normalization feature is on by default
        // This is a recognized feature only for XML Schemas. If the
        // configuration doesn't support this feature, ignore it.
        try {
            fConfiguration.setFeature ( NORMALIZE_DATA, false );
        }
        catch (XMLConfigurationException exc) {}

    } // <init>(XMLParserConfiguration)

	public boolean remove(Object o) {
		return myDelegee.remove(o) != null;
	}

	public boolean containsAll(Collection c) {
		return false;
	}

	public boolean addAll(Collection c) {
		return false;
	}

	public boolean retainAll(Collection c) {
		return false;
	}

	public boolean removeAll(Collection c) {
		return false;
	}

	public void clear() {
		myDelegee.clear();
	}
}
