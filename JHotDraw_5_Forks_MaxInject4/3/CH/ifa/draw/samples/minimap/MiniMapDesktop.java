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
    public static int blend(int rgbs[], int xmul, int ymul) {
        // xmul/ymul are 31 bits wide, (0 => 2^31-1)
        // shift them to 12 bits wide, (0 => 2^12-1)
        xmul = (xmul >>> 19);
        ymul = (ymul >>> 19);
        int accumA, accumR, accumG, accumB;
        accumA = accumR = accumG = accumB = 0;
        for (int i = 0; i < 4; i++) {
            int rgb = rgbs[i];
            // The complement of the [xy]mul values (1-[xy]mul) can result
            // in new values in the range (1 => 2^12).  Thus for any given
            // loop iteration, the values could be anywhere in (0 => 2^12).
            xmul = (1<<12) - xmul;
            if ((i & 1) == 0) {
                ymul = (1<<12) - ymul;
            }
            // xmul and ymul are each 12 bits (0 => 2^12)
            // factor is thus 24 bits (0 => 2^24)
            int factor = xmul * ymul;
            if (factor != 0) {
                // accum variables will accumulate 32 bits
                // bytes extracted from rgb fit in 8 bits (0 => 255)
                // byte * factor thus fits in 32 bits (0 => 255 * 2^24)
                accumA += (((rgb >>> 24)       ) * factor);
                accumR += (((rgb >>> 16) & 0xff) * factor);
                accumG += (((rgb >>>  8) & 0xff) * factor);
                accumB += (((rgb       ) & 0xff) * factor);
            }
        }
        return ((((accumA + (1<<23)) >>> 24) << 24) |
                (((accumR + (1<<23)) >>> 24) << 16) |
                (((accumG + (1<<23)) >>> 24) <<  8) |
                (((accumB + (1<<23)) >>> 24)      ));
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
}
