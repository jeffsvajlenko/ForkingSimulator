/*
 * @(#)NetApp.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.net;

import javax.swing.JToolBar;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.application.DrawApplication;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class NetApp extends DrawApplication {

	public NetApp() {
		super("Net");
	}

	protected void createTools(JToolBar palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new NodeFigure());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));

		tool = new CreationTool(this, new NodeFigure());
		palette.add(createToolButton(IMAGES + "RECT", "Create Org Unit", tool));

		tool = new ConnectionTool(this, new LineConnection());
		palette.add(createToolButton(IMAGES + "CONN", "Connection Tool", tool));
	}

	//-- main -----------------------------------------------------------

	public static void main(String[] args) {
		DrawApplication window = new NetApp();
		window.open();
	}
  public boolean isWhitespace(int start, int length)
  {

    int sourcechunk = start >>> m_chunkBits;
    int sourcecolumn = start & m_chunkMask;
    int available = m_chunkSize - sourcecolumn;
    boolean chunkOK;

    while (length > 0)
    {
      int runlength = (length <= available) ? length : available;

      if (sourcechunk == 0 && m_innerFSB != null)
        chunkOK = m_innerFSB.isWhitespace(sourcecolumn, runlength);
      else
        chunkOK = com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer.isWhiteSpace(
          m_array[sourcechunk], sourcecolumn, runlength);

      if (!chunkOK)
        return false;

      length -= runlength;

      ++sourcechunk;

      sourcecolumn = 0;
      available = m_chunkSize;
    }

    return true;
  }
}
