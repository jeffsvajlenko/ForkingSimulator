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
        static void parseShorthandBackground(CSS css, String value,
                                             MutableAttributeSet attr) {
            String[] strings = parseStrings(value);
            int count = strings.length;
            int index = 0;
            // bitmask: 0 for image, 1 repeat, 2 attachment, 3 position,
            //          4 color
            short found = 0;

            while (index < count) {
                String string = strings[index++];
                if ((found & 1) == 0 && isImage(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_IMAGE, string);
                    found |= 1;
                }
                else if ((found & 2) == 0 && isRepeat(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_REPEAT, string);
                    found |= 2;
                }
                else if ((found & 4) == 0 && isAttachment(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_ATTACHMENT, string);
                    found |= 4;
                }
                else if ((found & 8) == 0 && isPosition(string)) {
                    if (index < count && isPosition(strings[index])) {
                        css.addInternalCSSValue(attr, CSS.Attribute.
                                                BACKGROUND_POSITION,
                                                string + " " +
                                                strings[index++]);
                    }
                    else {
                        css.addInternalCSSValue(attr, CSS.Attribute.
                                                BACKGROUND_POSITION, string);
                    }
                    found |= 8;
                }
                else if ((found & 16) == 0 && isColor(string)) {
                    css.addInternalCSSValue(attr, CSS.Attribute.
                                            BACKGROUND_COLOR, string);
                    found |= 16;
                }
            }
            if ((found & 1) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_IMAGE,
                                        null);
            }
            if ((found & 2) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_REPEAT,
                                        "repeat");
            }
            if ((found & 4) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.
                                        BACKGROUND_ATTACHMENT, "scroll");
            }
            if ((found & 8) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.
                                        BACKGROUND_POSITION, null);
            }
            // Currently, there is no good way to express this.
            /*
            if ((found & 16) == 0) {
                css.addInternalCSSValue(attr, CSS.Attribute.BACKGROUND_COLOR,
                                        null);
            }
            */
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
}


