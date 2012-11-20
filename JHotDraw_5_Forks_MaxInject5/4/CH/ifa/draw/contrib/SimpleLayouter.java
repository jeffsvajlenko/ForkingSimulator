/*
 * @(#)SimpleLayouter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.FigureEnumeration;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;

import java.awt.*;
import java.io.IOException;

/**
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
public class SimpleLayouter implements Layouter {

	/**
	 * The Layoutable which should be laid out.
	 */
	private Layoutable myLayoutable;

	/**
	 * Insets to calculate a border
	 */
	private Insets myInsets;

	static final long serialVersionUID = 2928651014089117493L;

	private SimpleLayouter() {
		// do nothing: for JDO-compliance only
	}

	public SimpleLayouter(Layoutable newLayoutable) {
		setLayoutable(newLayoutable);
		setInsets(new Insets(0, 0, 0, 0));
	}

	/**
	 * Get the figure upon which the layout strategy operates.
	 *
	 * @return associated figure which should be laid out
	 */
	public Layoutable getLayoutable() {
		return myLayoutable;
	}

	/**
	 * Set the figure upon which the layout strategy operates.
	 *
	 * @param	newLayoutable	Layoutable to be laid out
	 */
	public void setLayoutable(Layoutable newLayoutable) {
		myLayoutable = newLayoutable;
	}

	/**
	 * Set the insets for spacing between the figure and its subfigures
	 *
	 * @param newInsets new spacing dimensions
	 */
	public void setInsets(Insets newInsets) {
		myInsets = newInsets;
	}

	/**
	 * Get the insets for spacing between the figure and its subfigures
	 *
	 * @return spacing dimensions
	 */
	public Insets getInsets() {
		return myInsets;
	}

	/**
	 * Create a new instance of this type and sets the layoutable
	 */
	public Layouter create(Layoutable newLayoutable) {
		SimpleLayouter newLayouter = new SimpleLayouter(newLayoutable);
		newLayouter.setInsets((Insets)getInsets().clone());
		return newLayouter;
	}

	public Rectangle calculateLayout(Point origin, Point corner) {
		Rectangle maxRect = new Rectangle(origin);
		maxRect.add(corner);
		FigureEnumeration fe = getLayoutable().figures();
		while (fe.hasNextFigure()) {
			Figure currentFigure = fe.nextFigure();
			maxRect.union(currentFigure.displayBox());
		}
		maxRect.width += getInsets().left + getInsets().right;
		maxRect.height += getInsets().top + getInsets().bottom;
		return maxRect;
	}

	public Rectangle layout(Point origin, Point corner) {
		return calculateLayout(origin, corner);
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

	/**
	 * Reads the contained figures from StorableInput.
	 */
	public void read(StorableInput dr) throws IOException {
		setLayoutable((Layoutable)dr.readStorable());
		setInsets(new Insets(dr.readInt(), dr.readInt(), dr.readInt(), dr.readInt()));
	}

	/**
	 * Writes the contained figures to the StorableOutput.
	 */
	public void write(StorableOutput dw) {
		dw.writeStorable(getLayoutable());
		Insets i = getInsets();
		dw.writeInt(i.top);
		dw.writeInt(i.left);
		dw.writeInt(i.bottom);
		dw.writeInt(i.right);
	}
}
