/*
 * @(#)CustomSelectionTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import javax.swing.JPopupMenu;
import java.awt.*;
import java.awt.event.*;

/**
 * A SelectionTool, which recognizes double clicks and popup menu triggers.
 * If a double click or popup trigger is encountered a hook method is called,
 * which handles the event. This methods can be overriden in subclasse to 
 * provide customized behaviour. 
 * Popup menus must be registered with a Figure using the setAttribute() method.
 * The key which associates a popup menu as an attribute is Figure.POPUP_MENU.
 *
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class CustomSelectionTool extends SelectionTool {

	/**
	 * Create an instance of this SelectionTool for the given view
	 *
	 * @param   editor    DrawingEditor for which the SelectionTool gets the active view
	 */
	public CustomSelectionTool(DrawingEditor editor) {
		super( editor );
	}
	
	/**
	 * MouseListener method for mouseDown events. If the popup trigger has been
	 * activated, then the appropriate hook method is called.
	 *
	 * @param   e   MouseEvent which should be interpreted
	 * @param   x   x coordinate of the MouseEvent
	 * @param   y   y coordinate of the MouseEvent
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		setView((DrawingView)e.getSource());
		// isPopupTrigger() at mouseDown() is only notified at UNIX systems
		if (e.isPopupTrigger()) {
			handlePopupMenu(e, x, y);
		}
		else {
			super.mouseDown(e, x, y);
			handleMouseDown(e, x, y);
		}
	}
	
	/**
	 * MouseListener method for mouseDrag events. Usually, mouse drags are
	 * ignored for popup menus or double clicks.
	 *
	 * @param   e   MouseEvent which should be interpreted
	 * @param   x   x coordinate of the MouseEvent
	 * @param   y   y coordinate of the MouseEvent
	 */
	public void mouseDrag(MouseEvent e, int x, int y) {
		if (!e.isPopupTrigger()) {
			super.mouseDrag(e, x, y);
		}
	}

	/** 
	 * MouseListener method for mouseUp events. Depending on the kind of event
	 * the appropriate hook method is called (popupMenuUp for popup trigger,
	 * doubleMouseClick for a double click, and mouseUp() and mouseClick() for
	 * normal mouse clicks).
	 *
	 * @param   e   MouseEvent which should be interpreted
	 * @param   x   x coordinate of the MouseEvent
	 * @param   y   y coordinate of the MouseEvent
	 */
	public void mouseUp(MouseEvent e, int x, int y) {
		if (e.isPopupTrigger()) {
			handlePopupMenu(e, x, y);
			super.mouseUp(e, x, y);
		}
		else if (e.getClickCount() == 2) {
			super.mouseUp(e, x, y);
			handleMouseDoubleClick(e, x, y);
		}
		else {
			super.mouseUp(e, x, y);
			handleMouseUp(e, x, y);
			handleMouseClick(e, x, y);
		}
	}
	
	/**
	 * Hook method which can be overriden by subclasses to provide
	 * specialised behaviour in the event of a mouse down.
	 */
	protected void handleMouseDown(MouseEvent e, int x, int y) {
	}

		/**
	 * Hook method which can be overriden by subclasses to provide
	 * specialised behaviour in the event of a mouse up.
	 */
	protected void handleMouseUp(MouseEvent e, int x, int y) {
	}

	/**
	 * Hook method which can be overriden by subclasses to provide
	 * specialised behaviour in the event of a mouse click.
	 */
	protected void handleMouseClick(MouseEvent e, int x, int y) {
	}

	/**
	 * Hook method which can be overriden by subclasses to provide
	 * specialised behaviour in the event of a mouse double click.
	 */
	protected void handleMouseDoubleClick(MouseEvent e, int x, int y) {
	}
    private MXBeanMapping
        makeArrayOrCollectionMapping(Type collectionType, Type elementType,
                                     MXBeanMappingFactory factory)
            throws OpenDataException {

        final MXBeanMapping elementMapping = factory.mappingForType(elementType, factory);
        final OpenType<?> elementOpenType = elementMapping.getOpenType();
        final ArrayType<?> openType = ArrayType.getArrayType(elementOpenType);
        final Class<?> elementOpenClass = elementMapping.getOpenClass();

        final Class<?> openArrayClass;
        final String openArrayClassName;
        if (elementOpenClass.isArray())
            openArrayClassName = "[" + elementOpenClass.getName();
        else
            openArrayClassName = "[L" + elementOpenClass.getName() + ";";
        try {
            openArrayClass = Class.forName(openArrayClassName);
        } catch (ClassNotFoundException e) {
            throw openDataException("Cannot obtain array class", e);
        }

        if (collectionType instanceof ParameterizedType) {
            return new CollectionMapping(collectionType,
                                         openType, openArrayClass,
                                         elementMapping);
        } else {
            if (isIdentity(elementMapping)) {
                return new IdentityMapping(collectionType,
                                           openType);
            } else {
                return new ArrayMapping(collectionType,
                                          openType,
                                          openArrayClass,
                                          elementMapping);
            }
        }
    }

	/**
	 * Hook method which can be overriden by subclasses to provide
	 * specialised behaviour in the event of a popup trigger.
	 */
	protected void handlePopupMenu(MouseEvent e, int x, int y) {
		Figure figure = drawing().findFigure(e.getX(), e.getY());
		if (figure != null) {
			Object attribute = figure.getAttribute(Figure.POPUP_MENU);
			if (attribute == null) {
				figure = drawing().findFigureInside(e.getX(), e.getY());
			}
			if (figure != null) {
				showPopupMenu(figure, e.getX(), e.getY(), e.getComponent());
			}
		}
	}

	/**
	 * This method displays a popup menu, if there is one registered with the
	 * Figure (the Figure's attributes are queried for Figure.POPUP_MENU which
	 * is used to indicate an association of a popup menu with the Figure).
	 *
	 * @param   figure      Figure for which a popup menu should be displayed
	 * @param   x           x coordinate where the popup menu should be displayed
	 * @param   y           y coordinate where the popup menu should be displayed
	 * @param   comp        Component which invoked the popup menu
	 */
	protected void showPopupMenu(Figure figure, int x, int y, Component comp) {
		Object attribute = figure.getAttribute(Figure.POPUP_MENU);
		if ((attribute != null) && (attribute instanceof JPopupMenu)) {
			JPopupMenu popup = (JPopupMenu)attribute;
			if (popup instanceof PopupMenuFigureSelection) {
				((PopupMenuFigureSelection)popup).setSelectedFigure(figure);
			}
			// calculate offsets for internal MDI frames
			Point newLocation = new Point(x, y);
			adjustOffsets(comp.getParent(), newLocation);
			popup.setLocation(newLocation);
			popup.setInvoker(comp);
			popup.setVisible(true);
		}
	}
	
	/**
	 * Internal MDI frames have offsets where a popup menu should be
	 * shown (in JDK 1.2).
	 * This method sums up iteratively all x and y offsets of all
	 * parent compontents until the top parent component is reached.
	 */
	private void adjustOffsets(Component comp, Point offsetPoint) {
		if (comp != null) {
			Point compLocation = comp.getLocation();
			offsetPoint.translate(compLocation.x, compLocation.y);
			adjustOffsets(comp.getParent(), offsetPoint);
		}
	}
}
