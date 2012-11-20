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
    public int createDeferredAttribute(String attrName, String attrURI,
                                       String attrValue, boolean specified) {

        // create node
        int nodeIndex = createNode(NodeImpl.ATTRIBUTE_NODE);
        int chunk = nodeIndex >> CHUNK_SHIFT;
        int index = nodeIndex & CHUNK_MASK;
        setChunkValue(fNodeName, attrName, chunk, index);
        setChunkValue(fNodeURI, attrURI, chunk, index);
        setChunkValue(fNodeValue, attrValue, chunk, index);
        int extra = specified ? SPECIFIED : 0;
        setChunkIndex(X1,fNodeExtra, extra, chunk, index);

        // return node index
        return nodeIndex;

    } // createDeferredAttribute(String,String,String,boolean):int

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
}
