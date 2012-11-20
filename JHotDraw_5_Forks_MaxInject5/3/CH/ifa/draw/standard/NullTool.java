/*
 * @(#)AbstractTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;

/**
 * Default implementation support for Tools.
 *
 * @see DrawingView
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */

public class NullTool extends AbstractTool {
	public NullTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	public void activate() {
		// suppress clearSelection() and tool-activation notification in superclass
	}

	public void deactivate() {
		// suppress clearSelection() and tool-activation notification in superclass
	}

    protected void checkUsable() {
    	//suppress setUsable(false);
	}    
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        // Check whether all attributes are unique.
        _allAttributesUnique = checkAttributesUnique();

        // Compile code to emit element start tag
        il.append(methodGen.loadHandler());

        il.append(new PUSH(cpg, _name));
        il.append(DUP2);                // duplicate these 2 args for endElement
        il.append(methodGen.startElement());

        // The value of an attribute may depend on a (sibling) variable
        int j=0;
        while (j < elementCount())  {
            final SyntaxTreeNode item = (SyntaxTreeNode) elementAt(j);
            if (item instanceof Variable) {
                item.translate(classGen, methodGen);
            }
            j++;
        }

        // Compile code to emit namespace attributes
        if (_accessedPrefixes != null) {
            boolean declaresDefaultNS = false;
            Enumeration e = _accessedPrefixes.keys();

            while (e.hasMoreElements()) {
                final String prefix = (String)e.nextElement();
                final String uri = (String)_accessedPrefixes.get(prefix);

                if (uri != Constants.EMPTYSTRING ||
                        prefix != Constants.EMPTYSTRING)
                {
                    if (prefix == Constants.EMPTYSTRING) {
                        declaresDefaultNS = true;
                    }
                    il.append(methodGen.loadHandler());
                    il.append(new PUSH(cpg,prefix));
                    il.append(new PUSH(cpg,uri));
                    il.append(methodGen.namespace());
                }
            }

            /*
             * If our XslElement parent redeclares the default NS, and this
             * element doesn't, it must be redeclared one more time.
             */
            if (!declaresDefaultNS && (_parent instanceof XslElement)
                    && ((XslElement) _parent).declaresDefaultNS())
            {
                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, Constants.EMPTYSTRING));
                il.append(new PUSH(cpg, Constants.EMPTYSTRING));
                il.append(methodGen.namespace());
            }
        }

        // Output all attributes
        if (_attributeElements != null) {
            final int count = _attributeElements.size();
            for (int i = 0; i < count; i++) {
                SyntaxTreeNode node =
                    (SyntaxTreeNode)_attributeElements.elementAt(i);
                if (!(node instanceof XslAttribute)) {
                    node.translate(classGen, methodGen);
                }
            }
        }

        // Compile code to emit attributes and child elements
        translateContents(classGen, methodGen);

        // Compile code to emit element end tag
        il.append(methodGen.endElement());
    }
}
