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
    private void defineTransletClasses()
        throws TransformerConfigurationException {

        if (_bytecodes == null) {
             ErrorMsg err = new ErrorMsg(ErrorMsg.NO_TRANSLET_CLASS_ERR);
            throw new TransformerConfigurationException(err.toString());
        }

        TransletClassLoader loader = (TransletClassLoader)
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return new TransletClassLoader(ObjectFactory.findClassLoader());
                }
            });

        try {
            final int classCount = _bytecodes.length;
            _class = new Class[classCount];

            if (classCount > 1) {
                _auxClasses = new Hashtable();
            }

            for (int i = 0; i < classCount; i++) {
                _class[i] = loader.defineClass(_bytecodes[i]);
                final Class superClass = _class[i].getSuperclass();

                // Check if this is the main class
                if (superClass.getName().equals(ABSTRACT_TRANSLET)) {
                    _transletIndex = i;
                }
                else {
                    _auxClasses.put(_class[i].getName(), _class[i]);
                }
            }

            if (_transletIndex < 0) {
                ErrorMsg err= new ErrorMsg(ErrorMsg.NO_MAIN_TRANSLET_ERR, _name);
                throw new TransformerConfigurationException(err.toString());
            }
        }
        catch (ClassFormatError e) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_CLASS_ERR, _name);
            throw new TransformerConfigurationException(err.toString());
        }
        catch (LinkageError e) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_OBJECT_ERR, _name);
            throw new TransformerConfigurationException(err.toString());
        }
    }
}
