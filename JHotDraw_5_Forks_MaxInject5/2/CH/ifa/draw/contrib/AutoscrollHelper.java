/*
 * @(#)AutoscrollHelper.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import java.awt.*;

/**
 * A helper class for implementing autoscrolling
 *
 * @author  SourceForge(dnoyeb) aka C.L. Gilbert
 * @version <$CURRENT_VERSION$>
 */
public abstract class AutoscrollHelper {
	private int autoscrollMargin = 20;
	private Insets autoscrollInsets = new Insets(0, 0, 0, 0);
	public AutoscrollHelper(int margin) {
		autoscrollMargin = margin;
	}
	public void setAutoscrollMargin(int margin) {
		autoscrollMargin = margin;
	}
	public int getAutoscrollMargin() {
		return autoscrollMargin;
	}

	/**
	 * Override this method to call getSize() on your Component
	 * @see Component#getSize
	 */
	public abstract Dimension getSize();

	/**
	 * Override this method to call getVisibleRect() on your JComponent
	 * @see javax.swing.JComponent#getVisibleRect
	 */
	public abstract Rectangle getVisibleRect();

	/**
	 * Override this method to call scrollRectToVisible(Rectangle aRect) on
	 * your component
	 * @see javax.swing.JComponent#scrollRectToVisible
	 */
	public abstract void scrollRectToVisible(Rectangle aRect);
	/**
	 * Part of the autoscrolls interface
	 *
	 */
	public void autoscroll(Point location) {
		//System.out.println("mouse at " + location);
		int top = 0, left = 0, bottom = 0, right = 0;
		Dimension size = getSize();
		Rectangle rect = getVisibleRect();
		int bottomEdge = rect.y + rect.height;
		int rightEdge = rect.x + rect.width;
		if (location.y - rect.y <= autoscrollMargin && rect.y > 0)
			top = autoscrollMargin;
		if (location.x - rect.x <= autoscrollMargin && rect.x > 0)
			left = autoscrollMargin;
		if (bottomEdge - location.y <= autoscrollMargin && bottomEdge < size.height)
			bottom = autoscrollMargin;
		if (rightEdge - location.x <= autoscrollMargin && rightEdge < size.width)
			right = autoscrollMargin;
		rect.x += right - left;
		rect.y += bottom - top;
		scrollRectToVisible(rect);
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
	public Insets getAutoscrollInsets() {
		Dimension size = getSize();
		Rectangle rect = getVisibleRect();
		autoscrollInsets.top = rect.y + autoscrollMargin;
		autoscrollInsets.left = rect.x + autoscrollMargin;
		autoscrollInsets.bottom = size.height - (rect.y + rect.height) + autoscrollMargin;
		autoscrollInsets.right = size.width - (rect.x + rect.width) + autoscrollMargin;
		return autoscrollInsets;
	}
}
