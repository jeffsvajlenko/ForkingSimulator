/*
 * @(#)ChopBoxConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.Geom;

/**
 * A ChopBoxConnector locates connection points by
 * choping the connection between the centers of the
 * two figures at the display box.
 *
 * @see Connector
 *
 * @version <$CURRENT_VERSION$>
 */
public class ChopBoxConnector extends AbstractConnector {

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -1461450322712345462L;

	public ChopBoxConnector() { // only used for Storable implementation
	}

	public ChopBoxConnector(Figure owner) {
		super(owner);
	}

	public Point findStart(ConnectionFigure connection) {
		Figure startFigure = connection.getStartConnector().owner();
		Rectangle r2 = connection.getEndConnector().displayBox();
		Point r2c = null;

		if (connection.pointCount() == 2) {
			r2c = new Point(r2.x + r2.width/2, r2.y + r2.height/2);
		 }
		 else {
			r2c = connection.pointAt(1);
		}

		return chop(startFigure, r2c);
	}

	public Point findEnd(ConnectionFigure connection) {
		Figure endFigure = connection.getEndConnector().owner();
		Rectangle r1 = connection.getStartConnector().displayBox();
		Point r1c = null;

		if (connection.pointCount() == 2) {
			r1c = new Point(r1.x + r1.width/2, r1.y + r1.height/2);
		}
		else {
			r1c = connection.pointAt(connection.pointCount()-2);
		}

		return chop(endFigure, r1c);
	}
   static Element getDocumentElement(Set set) {
           Iterator it=set.iterator();
           Element e=null;
           while (it.hasNext()) {
                   Node currentNode=(Node)it.next();
                   if (currentNode != null && currentNode.getNodeType() == Node.ELEMENT_NODE) {
                           e=(Element)currentNode;
                           break;
                   }

           }
           List parents=new ArrayList(10);

                //Obtain all the parents of the elemnt
                while (e != null) {
                        parents.add(e);
                        Node n=e.getParentNode();
                        if (n == null || n.getNodeType() != Node.ELEMENT_NODE) {
                                break;
                        }
                        e=(Element)n;
                }
                //Visit them in reverse order.
                ListIterator it2=parents.listIterator(parents.size()-1);
                Element ele=null;
                while (it2.hasPrevious()) {
                        ele=(Element)it2.previous();
                        if (set.contains(ele)) {
                                return ele;
                        }
        }
                return null;
   }

	protected Point chop(Figure target, Point from) {
		Rectangle r = target.displayBox();
		return Geom.angleToPoint(r, (Geom.pointToAngle(r, from)));
	}
}

