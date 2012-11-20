/*
 * @(#)ToggleGridCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.Point;
import CH.ifa.draw.framework.*;

/**
 * A command to toggle the snap to grid behavior.
 *
 * @version <$CURRENT_VERSION$>
 */
public class ToggleGridCommand extends AbstractCommand {

	private Point fGrid;

   /**
	* Constructs a toggle grid command.
	* @param name the command name
	* @param newDrawingEditor editor
	* @param grid the grid size. A grid size of 1,1 turns grid snapping off.
	*/
	public ToggleGridCommand(String name, DrawingEditor newDrawingEditor, Point grid) {
		super(name, newDrawingEditor);
		fGrid = new Point(grid.x, grid.y);
	}

	public void execute() {
		super.execute();
		PointConstrainer grid = view().getConstrainer();
		if (grid != null) {
			view().setConstrainer(null);
		}
		else {
			view().setConstrainer(new GridConstrainer(fGrid.x, fGrid.y));
		}
	}
           static final void outputAttrToWriter(final String name, final String value, final OutputStream writer,
                                final Map cache) throws IOException {
              writer.write(' ');
              UtfHelpper.writeByte(name,writer,cache);
              writer.write(equalsStr);
              byte  []toWrite;
              final int length = value.length();
              int i=0;
              while (i < length) {
                 char c = value.charAt(i++);

                 switch (c) {

                 case '&' :
                        toWrite=_AMP_;
                    break;

                 case '<' :
                        toWrite=_LT_;
                    break;

                 case '"' :
                        toWrite=_QUOT_;
                    break;

                 case 0x09 :    // '\t'
                        toWrite=__X9_;
                    break;

                 case 0x0A :    // '\n'
                        toWrite=__XA_;
                    break;

                 case 0x0D :    // '\r'
                        toWrite=__XD_;
                    break; 
                 default :
                        if (c < 0x80 ) {
                                writer.write(c);
                        } else {
                                UtfHelpper.writeCharToUtf8(c,writer);
                        };
                    continue;
                 }
                 writer.write(toWrite);
              }

              writer.write('\"');
           }
}


