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
        protected void configureXML11Pipeline() {
                super.configureXML11Pipeline();

        // configure XML 1.1. DTD pipeline
                fXML11DTDScanner.setDTDHandler(fXML11DTDProcessor);
                fXML11DTDProcessor.setDTDSource(fXML11DTDScanner);

        fDTDProcessor.setDTDHandler(fXIncludeHandler);
        fXIncludeHandler.setDTDSource(fXML11DTDProcessor);
        fXIncludeHandler.setDTDHandler(fXPointerHandler);
        fXPointerHandler.setDTDSource(fXIncludeHandler);
        fXPointerHandler.setDTDHandler(fDTDHandler);
        if (fDTDHandler != null) {
            fDTDHandler.setDTDSource(fXPointerHandler);
        }


                // configure XML document pipeline: insert after DTDValidator and
                // before XML Schema validator
                XMLDocumentSource prev = null;
                if (fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE) {
                        // we don't have to worry about fSchemaValidator being null, since
                        // super.configurePipeline() instantiated it if the feature was set
                        prev = fSchemaValidator.getDocumentSource();
                }
                // Otherwise, insert after the last component in the pipeline
                else {
                        prev = fLastComponent;
                        fLastComponent = fXPointerHandler;
                }

        XMLDocumentHandler next = prev.getDocumentHandler();
                prev.setDocumentHandler(fXIncludeHandler);
                fXIncludeHandler.setDocumentSource(prev);

                if (next != null) {
                        fXIncludeHandler.setDocumentHandler(next);
            next.setDocumentSource(fXIncludeHandler);
        }

                fXIncludeHandler.setDocumentHandler(fXPointerHandler);
                fXPointerHandler.setDocumentSource(fXIncludeHandler);


        } // configureXML11Pipeline()

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
