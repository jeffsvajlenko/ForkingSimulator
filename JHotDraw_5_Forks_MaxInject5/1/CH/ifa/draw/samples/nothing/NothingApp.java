/*
 * @(#)NothingApp.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.nothing;

import javax.swing.JToolBar;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.contrib.*;
import CH.ifa.draw.application.*;

/**
 * A very basic example for demonstraing JHotDraw's capabilities. It contains only
 * a few additional tools apart from the selection tool already provided by its superclass.
 * It uses only a single document interface (SDI) and not a multiple document interface (MDI)
 * because the applicateion frame is derived DrawApplication rather than MDI_DrawApplication.
 * To enable MDI for this application, NothingApp must inherit from MDI_DrawApplication
 * and be recompiled.
 *
 * @version <$CURRENT_VERSION$>
 */
public  class NothingApp extends DrawApplication {

	public NothingApp() {
		super("Nothing");
	}
    private boolean prepare(JComponent c, boolean isPaint, int x, int y,
                            int w, int h) {
        if (bsg != null) {
            bsg.dispose();
            bsg = null;
        }
        bufferStrategy = null;
        if (fetchRoot(c)) {
            boolean contentsLost = false;
            BufferInfo bufferInfo = getBufferInfo(root);
            if (bufferInfo == null) {
                contentsLost = true;
                bufferInfo = new BufferInfo(root);
                bufferInfos.add(bufferInfo);
                if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                    LOGGER.finer("prepare: new BufferInfo: " + root);
                }
            }
            this.bufferInfo = bufferInfo;
            if (!bufferInfo.hasBufferStrategyChanged()) {
                bufferStrategy = bufferInfo.getBufferStrategy(true);
                if (bufferStrategy != null) {
                    bsg = bufferStrategy.getDrawGraphics();
                    if (bufferStrategy.contentsRestored()) {
                        contentsLost = true;
                        if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                            LOGGER.finer(
                                "prepare: contents restored in prepare");
                        }
                    }
                }
                else {
                    // Couldn't create BufferStrategy, fallback to normal
                    // painting.
                    return false;
                }
                if (bufferInfo.getContentsLostDuringExpose()) {
                    contentsLost = true;
                    bufferInfo.setContentsLostDuringExpose(false);
                    if (LOGGER.isLoggable(PlatformLogger.FINER)) {
                        LOGGER.finer("prepare: contents lost on expose");
                    }
                }
                if (isPaint && c == rootJ && x == 0 && y == 0 &&
                      c.getWidth() == w && c.getHeight() == h) {
                    bufferInfo.setInSync(true);
                }
                else if (contentsLost) {
                    // We either recreated the BufferStrategy, or the contents
                    // of the buffer strategy were restored.  We need to
                    // repaint the root pane so that the back buffer is in sync
                    // again.
                    bufferInfo.setInSync(false);
                    if (!isRepaintingRoot()) {
                        repaintRoot(rootJ);
                    }
                    else {
                        // Contents lost twice in a row, punt
                        resetDoubleBufferPerWindow();
                    }
                }
                return (bufferInfos != null);
            }
        }
        return false;
    }

	protected void createTools(JToolBar palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new TextFigure());
		palette.add(createToolButton(IMAGES+"TEXT", "Text Tool", tool));

		tool = new CreationTool(this, new RectangleFigure());
		palette.add(createToolButton(IMAGES+"RECT", "Rectangle Tool", tool));

		tool = new CreationTool(this, new RoundRectangleFigure());
		palette.add(createToolButton(IMAGES+"RRECT", "Round Rectangle Tool", tool));

		tool = new CreationTool(this, new EllipseFigure());
		palette.add(createToolButton(IMAGES+"ELLIPSE", "Ellipse Tool", tool));

		tool = new CreationTool(this, new LineFigure());
		palette.add(createToolButton(IMAGES+"LINE", "Line Tool", tool));

		tool = new PolygonTool(this);
		palette.add(createToolButton(IMAGES+"POLYGON", "Polygon Tool", tool));

		tool = new ConnectionTool(this, new LineConnection());
		palette.add(createToolButton(IMAGES+"CONN", "Connection Tool", tool));

		tool = new ConnectionTool(this, new ElbowConnection());
		palette.add(createToolButton(IMAGES+"OCONN", "Elbow Connection Tool", tool));
	}

	//-- main -----------------------------------------------------------

	public static void main(String[] args) {
		DrawApplication window = new NothingApp();
		window.open();
	}
}
