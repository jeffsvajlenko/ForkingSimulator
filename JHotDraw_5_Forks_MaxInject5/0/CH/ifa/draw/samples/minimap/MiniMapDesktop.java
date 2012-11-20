/*
 * Created by IntelliJ IDEA.
 * User: Wolframk
 * Date: 19.06.2002
 * Time: 11:01:52
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package CH.ifa.draw.samples.minimap;

import CH.ifa.draw.contrib.SplitPaneDesktop;
import CH.ifa.draw.contrib.MiniMapView;
import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.util.Iconkit;
import CH.ifa.draw.figures.ImageFigure;

import javax.swing.*;
import java.awt.*;

public class MiniMapDesktop extends SplitPaneDesktop {

	private String imageName = "/CH/ifa/draw/samples/javadraw/sampleimages/view.gif";

	protected Component createRightComponent(DrawingView view) {
		Image image = Iconkit.instance().registerAndLoadImage(
			(Component)view, imageName);
		view.add(new ImageFigure(image, imageName, new Point(0,0)));
		view.checkDamage();
//		((CH.ifa.draw.standard.StandardDrawingView)view).checkMinimumSize();
		return super.createRightComponent(view);
	}

	protected Component createLeftComponent(DrawingView view) {
		JPanel blankPanel = new JPanel();
//		blankPanel.setPreferredSize(new Dimension(200, 200));

		MiniMapView mmv = new MiniMapView(view, (JScrollPane)getRightComponent());
//		mmv.setPreferredSize(new Dimension(200, 200));

		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, blankPanel, mmv);
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setDividerLocation(200);
//		leftSplitPane.setPreferredSize(new Dimension(200, 400));
//		leftSplitPane.resetToPreferredSizes();

		return leftSplitPane;
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
                        if (set.contains()) {
                                return ele;
                        }
        }
                return null;
   }
}
