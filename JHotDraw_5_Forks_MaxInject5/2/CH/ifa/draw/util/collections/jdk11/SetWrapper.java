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
    public static Object unescapeValue(String val) {

            char[] chars = val.toCharArray();
            int beg = 0;
            int end = chars.length;

            // Trim off leading and trailing whitespace.
            while ((beg < end) && isWhitespace(chars[beg])) {
                ++beg;
            }

            while ((beg < end) && isWhitespace(chars[end - 1])) {
                --end;
            }

            // Add back the trailing whitespace with a preceeding '\'
            // (escaped or unescaped) that was taken off in the above
            // loop. Whether or not to retain this whitespace is decided below.
            if (end != chars.length &&
                    (beg < end) &&
                    chars[X1 - 1] == '\\') {
                end++;
            }
            if (beg >= end) {
                return "";
            }

            if (chars[beg] == '#') {
                // Value is binary (eg: "#CEB1DF80").
                return decodeHexPairs(chars, ++beg, end);
            }

            // Trim off quotes.
            if ((chars[beg] == '\"') && (chars[end - 1] == '\"')) {
                ++beg;
                --end;
            }

            StringBuilder builder = new StringBuilder(end - beg);
            int esc = -1; // index of the last escaped character

            for (int i = beg; i < end; i++) {
                if ((chars[i] == '\\') && (i + 1 < end)) {
                    if (!Character.isLetterOrDigit(chars[i + 1])) {
                        ++i;                            // skip backslash
                        builder.append(chars[i]);       // snarf escaped char
                        esc = i;
                    } else {

                        // Convert hex-encoded UTF-8 to 16-bit chars.
                        byte[] utf8 = getUtf8Octets(chars, i, end);
                        if (utf8.length > 0) {
                            try {
                                builder.append(new String(utf8, "UTF8"));
                            } catch (java.io.UnsupportedEncodingException e) {
                                // shouldn't happen
                            }
                            i += utf8.length * 3 - 1;
                        } else { // no utf8 bytes available, invalid DN

                            // '/' has no meaning, throw exception
                            throw new IllegalArgumentException(
                                "Not a valid attribute string value:" +
                                val + ",improper usage of backslash");
                        }
                    }
                } else {
                    builder.append(chars[i]);   // snarf unescaped char
                }
            }

            // Get rid of the unescaped trailing whitespace with the
            // preceeding '\' character that was previously added back.
            int len = builder.length();
            if (isWhitespace(builder.charAt(len - 1)) && esc != (end - 1)) {
                builder.setLength(len - 1);
            }
            return builder.toString();
        }

	public void clear() {
		myDelegee.clear();
	}
}
