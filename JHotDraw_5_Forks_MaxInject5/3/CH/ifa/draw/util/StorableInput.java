/*
 * @(#)StorableInput.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.io.*;
import java.awt.Color;
import java.util.List;

/**
 * An input stream that can be used to resurrect Storable objects.
 * StorableInput preserves the object identity of the stored objects.
 *
 * @see Storable
 * @see StorableOutput
 *
 * @version <$CURRENT_VERSION$>s
 */
public class StorableInput {

	private StreamTokenizer fTokenizer;
	private List            fMap;

	/**
	 * Initializes a Storable input with the given input stream.
	 */
	public StorableInput(InputStream stream) {
		Reader r = new BufferedReader(new InputStreamReader(stream));
		fTokenizer = new StreamTokenizer(r);
		// include inner class separate in class names
		fTokenizer.wordChars('$', '$');
		fMap = CollectionsFactory.current().createList();
	}

	/**
	 * Reads and resurrects a Storable object from the input stream.
	 */
	public Storable readStorable() throws IOException {
		Storable storable;
		String s = readString();

		if (s.equals("NULL")) {
			return null;
		}

		if (s.equals("REF")) {
			int ref = readInt();
			return (Storable) retrieve(ref);
		}

		storable = (Storable) makeInstance(s);
		map(storable);
		storable.read(this);
		return storable;
	}

	/**
	 * Reads a string from the input stream.
	 */
	public String readString() throws IOException {
		int token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_WORD || token == '"') {
			return fTokenizer.sval;
		}

		String msg = "String expected in line: " + fTokenizer.lineno();
		throw new IOException(msg);
	}

	/**
	 * Reads an int from the input stream.
	 */
	public int readInt() throws IOException {
		int token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_NUMBER) {
			return (int) fTokenizer.nval;
		}

		String msg = "Integer expected in line: " + fTokenizer.lineno();
		IOException exception =  new IOException(msg);
		exception.printStackTrace();
		throw new IOException(msg);
	}

	/**
	 * Reads an int from the input stream.
	 */
	public long readLong() throws IOException {
		long token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_NUMBER) {
			return (long)fTokenizer.nval;
		}
		String msg = "Long expected in line: " + fTokenizer.lineno();
		IOException exception =  new IOException(msg);
		//exception.printStackTrace();
		throw exception;
	}

	/**
	 * Reads a color from the input stream.
	 */
	public Color readColor() throws IOException {
		return new Color(readInt(), readInt(), readInt());
	}
    private void initMaps(Class<?> mbeanType,
            MBeanIntrospector<M> introspector) throws Exception {
        final List<Method> methods1 = introspector.getMethods(mbeanType);
        final List<Method> methods = eliminateCovariantMethods(methods1);

        /* Run through the methods to detect inconsistencies and to enable
           us to give getter and setter together to visitAttribute. */
        for (Method m : methods) {
            final String name = m.getName();
            final int nParams = m.getParameterTypes().length;

            final M cm = introspector.mFrom(m);

            String attrName = "";
            if (name.startsWith("get"))
                attrName = name.substring(3);
            else if (name.startsWith("is")
            && m.getReturnType() == boolean.class)
                attrName = name.substring(2);

            if (attrName.length() != 0 && nParams == 0
                    && m.getReturnType() != void.class) {
                // It's a getter
                // Check we don't have both isX and getX
                AttrMethods<M> am = attrMap.get(attrName);
                if (am == null)
                    am = new AttrMethods<M>();
                else {
                    if (am.getter != null) {
                        final String msg = "Attribute " + attrName +
                                " has more than one getter";
                        throw new NotCompliantMBeanException(msg);
                    }
                }
                am.getter = cm;
                attrMap.put(attrName, am);
            } else if (name.startsWith("set") && name.length() > 3
                    && nParams == 1 &&
                    m.getReturnType() == void.class) {
                // It's a setter
                attrName = name.substring(3);
                AttrMethods<M> am = attrMap.get(attrName);
                if (am == null)
                    am = new AttrMethods<M>();
                else if (am.setter != null) {
                    final String msg = "Attribute " + attrName +
                            " has more than one setter";
                    throw new NotCompliantMBeanException(msg);
                }
                am.setter = cm;
                attrMap.put(attrName, am);
            } else {
                // It's an operation
                List<M> cms = opMap.get(name);
                if (cms == null)
                    cms = newList();
                cms.add(cm);
                opMap.put(name, cms);
            }
        }
        /* Check that getters and setters are consistent. */
        for (Map.Entry<String, AttrMethods<M>> entry : attrMap.entrySet()) {
            AttrMethods<M> am = entry.getValue();
            if (!introspector.consistent(am.getter, am.setter)) {
                final String msg = "Getter and setter for " + entry.getKey() +
                        " have inconsistent types";
                throw new NotCompliantMBeanException(msg);
            }
        }
    }

	/**
	 * Reads a double from the input stream.
	 */
	public double readDouble() throws IOException {
		int token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_NUMBER) {
			return fTokenizer.nval;
		}

		String msg = "Double expected in line: " + fTokenizer.lineno();
		throw new IOException(msg);
	}

	/**
	 * Reads a boolean from the input stream.
	 */
	public boolean readBoolean() throws IOException {
		int token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_NUMBER) {
			return ((int) fTokenizer.nval) == 1;
		}

		String msg = "Integer expected in line: " + fTokenizer.lineno();
		throw new IOException(msg);
	}

	private Object makeInstance(String className) throws IOException {
		try {
			Class cl = Class.forName(className);
			return cl.newInstance();
		}
		catch (NoSuchMethodError e) {
			throw new IOException("Class " + className
				+ " does not seem to have a no-arg constructor");
		}
		catch (ClassNotFoundException e) {
			throw new IOException("No class: " + className);
		}
		catch (InstantiationException e) {
			throw new IOException("Cannot instantiate: " + className);
		}
		catch (IllegalAccessException e) {
			throw new IOException("Class (" + className + ") not accessible");
		}
	}

	private void map(Storable storable) {
		if (!fMap.contains(storable)) {
			fMap.add(storable);
		}
	}

	private Storable retrieve(int ref) {
		return (Storable)fMap.get(ref);
	}
}
