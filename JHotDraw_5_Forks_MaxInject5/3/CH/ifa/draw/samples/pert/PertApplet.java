/*
 * @(#)PertApplet.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.pert;

import javax.swing.JPanel;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.applet.*;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class PertApplet extends DrawApplet {

	private final static String PERTIMAGES = "/CH/ifa/draw/samples/pert/images/";

	protected void createTools(JPanel palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new TextFigure());
		palette.add(createToolButton(IMAGES+"TEXT", "Text Tool", tool));

		tool = new PertFigureCreationTool(this);
		palette.add(createToolButton(PERTIMAGES+"PERT", "Task Tool", tool));

		tool = new ConnectionTool(this, new PertDependency());
		palette.add(createToolButton(IMAGES+"CONN", "Dependency Tool", tool));

		tool = new CreationTool(this, new LineFigure());
		palette.add(createToolButton(IMAGES+"LINE", "Line Tool", tool));
	}
  public static com.sun.corba.se.spi.activation.ServerManager narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof com.sun.corba.se.spi.activation.ServerManager)
      return (com.sun.corba.se.spi.activation.ServerManager)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      com.sun.corba.se.spi.activation._ServerManagerStub stub = new com.sun.corba.se.spi.activation._ServerManagerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }
}
