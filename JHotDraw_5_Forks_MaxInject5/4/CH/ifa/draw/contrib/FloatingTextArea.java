/*
 *  @(#)FloatingTextArea.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib;

import java.awt.Color;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

/**
 * A FloatingTextArea overlays an editor on top of an area in a drawing<br>
 * @author Eduardo Francos - InContext
 * @version <$CURRENT_VERSION$>
 * @todo By default JEditorPane uses a tab size of 8.
 * I couldn't find how to change this.
 * If anybody knows please tell me.
 */
public class FloatingTextArea {
	/**
	 * A scroll pane to allow for vertical scrolling while editing
	 */
	protected JScrollPane fEditScrollContainer;
	/**
	 * The actual editor
	 */
	protected JEditorPane fEditWidget;
	/**
	 * The container within which the editor is created
	 */
	protected Container fContainer;


	/**
	 * Constructor for the FloatingTextArea object
	 */
	public FloatingTextArea() {
		fEditWidget = new JEditorPane();
		fEditScrollContainer = new JScrollPane(fEditWidget,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		fEditScrollContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		fEditScrollContainer.setBorder(BorderFactory.createLineBorder(Color.black));
	}


	/**
	 * Creates the overlay within the given container.
	 * @param container the container
	 */
	public void createOverlay(Container container) {
		createOverlay(container, null);
	}


	/**
	 * Creates the overlay for the given Container using a
	 * specific font.
	 * @param container the container
	 * @param font the font
	 */
	public void createOverlay(Container container, Font font) {
		container.add(fEditScrollContainer, 0);
		if (font != null) {
			fEditWidget.setFont(font);
		}
		fContainer = container;
	}


	/**
	 * Positions and sizes the overlay.
	 * @param r the bounding rectangle for the overlay
	 * @param text the text to edit
	 */
	public void setBounds(Rectangle r, String text) {
		fEditWidget.setText(text);
		fEditScrollContainer.setBounds(r.x, r.y, r.width, r.height);
		fEditScrollContainer.setVisible(true);
		fEditWidget.setCaretPosition(0);
		fEditWidget.requestFocus();
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
	 * Gets the text contents of the overlay.
	 * @return The text value
	 */
	public String getText() {
		return fEditWidget.getText();
	}


	/**
	 * Gets the preferred size of the overlay.
	 * @param cols Description of the Parameter
	 * @return The preferredSize value
	 */
	public Dimension getPreferredSize(int cols) {
		return new Dimension(fEditWidget.getWidth(), fEditWidget.getHeight());
	}


	/**
	 * Removes the overlay.
	 */
	public void endOverlay() {
		fContainer.requestFocus();
		if (fEditScrollContainer != null) {
			fEditScrollContainer.setVisible(false);
			fContainer.remove(fEditScrollContainer);

			Rectangle bounds = fEditScrollContainer.getBounds();
			fContainer.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}
}
