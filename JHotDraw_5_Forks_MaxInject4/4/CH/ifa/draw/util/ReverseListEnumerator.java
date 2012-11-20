/*
 * @(#)ReverseListEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * An Enumeration that enumerates a list back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
public class ReverseListEnumerator implements Iterator {

	private List myList;
	private int count;

	public ReverseListEnumerator(List l) {
		myList = l;
		count = myList.size() - 1;
	}

	public boolean hasNext() {
		return count >= 0;
	}
    private String compileTopLevel(ClassGenerator classGen) {

        final ConstantPoolGen cpg = classGen.getConstantPool();

        final com.sun.org.apache.bcel.internal.generic.Type[] argTypes = {
            Util.getJCRefType(DOM_INTF_SIG),
            Util.getJCRefType(NODE_ITERATOR_SIG),
            Util.getJCRefType(TRANSLET_OUTPUT_SIG)
        };

        final String[] argNames = {
            DOCUMENT_PNAME, ITERATOR_PNAME, TRANSLET_OUTPUT_PNAME
        };

        final InstructionList il = new InstructionList();

        final MethodGenerator toplevel =
            new MethodGenerator(ACC_PUBLIC,
                                com.sun.org.apache.bcel.internal.generic.Type.VOID,
                                argTypes, argNames,
                                "topLevel", _className, il,
                                classGen.getConstantPool());

        toplevel.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");

        // Define and initialize 'current' variable with the root node
        final LocalVariableGen current =
            toplevel.addLocalVariable("current",
                                      com.sun.org.apache.bcel.internal.generic.Type.INT,
                                      null, null);

        final int setFilter = cpg.addInterfaceMethodref(DOM_INTF,
                               "setFilter",
                               "(Lcom/sun/org/apache/xalan/internal/xsltc/StripFilter;)V");

        final int gitr = cpg.addInterfaceMethodref(DOM_INTF,
                                                        "getIterator",
                                                        "()"+NODE_ITERATOR_SIG);
        il.append(toplevel.loadDOM());
        il.append(new INVOKEINTERFACE(gitr, 1));
        il.append(toplevel.nextNode());
        current.setStart(il.append(new ISTORE(current.getIndex())));

        // Create a new list containing variables/params + keys
        Vector varDepElements = new Vector(_globals);
        Enumeration elements = elements();
        while (elements.hasMoreElements()) {
            final Object element = elements.nextElement();
            if (element instanceof Key) {
                varDepElements.add(element);
            }
        }

        // Determine a partial order for the variables/params and keys
        varDepElements = resolveDependencies(varDepElements);

        // Translate vars/params and keys in the right order
        final int count = varDepElements.size();
        for (int i = 0; i < count; i++) {
            final TopLevelElement tle = (TopLevelElement) varDepElements.elementAt(i);
            tle.translate(classGen, toplevel);
            if (tle instanceof Key) {
                final Key key = (Key) tle;
                _keys.put(key.getName(), key);
            }
        }

        // Compile code for other top-level elements
        Vector whitespaceRules = new Vector();
        elements = elements();
        while (elements.hasMoreElements()) {
            final Object element = elements.nextElement();
            // xsl:decimal-format
            if (element instanceof DecimalFormatting) {
                ((DecimalFormatting)element).translate(classGen,toplevel);
            }
            // xsl:strip/preserve-space
            else if (element instanceof Whitespace) {
                whitespaceRules.addAll(((Whitespace)element).getRules());
            }
        }

        // Translate all whitespace strip/preserve rules
        if (whitespaceRules.size() > 0) {
            Whitespace.translateRules(whitespaceRules,classGen);
        }

        if (classGen.containsMethod(STRIP_SPACE, STRIP_SPACE_PARAMS) != null) {
            il.append(toplevel.loadDOM());
            il.append(classGen.loadTranslet());
            il.append(new INVOKEINTERFACE(setFilter, 2));
        }

        il.append(RETURN);

        // Compute max locals + stack and add method to class
        classGen.addMethod(toplevel);

        return("("+DOM_INTF_SIG+NODE_ITERATOR_SIG+TRANSLET_OUTPUT_SIG+")V");
    }

	public Object next() {
		if (count >= 0) {
			return myList.get(count--);
		}
		throw new NoSuchElementException("ReverseListEnumerator");
	}

	public void remove() {
		myList.remove(count);
		count--;
	}
}
