/*
 * @(#)PertApplication.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.pert;

import javax.swing.JToolBar;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.application.*;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class PertApplication extends DrawApplication {

	static private final String PERTIMAGES = "/CH/ifa/draw/samples/pert/images/";

	public PertApplication() {
		super("PERT Editor");
	}
        public void write(PrintWriter out) {
            out.print("permission ");
            out.print(permission);
            if (name != null) {
                out.print(" \"");

                // have to add escape chars for quotes
               if (name.indexOf("\"") != -1) {
                    int numQuotes = 0;
                    char[] chars = name.toCharArray();

                    // count the number of quote chars
                    for (int i = 0; i < chars.length; i++) {
                        if (chars[i] == '"')
                            numQuotes++;
                    }

                    // now, add an escape char before each quote
                    char[] newChars = new char[chars.length + numQuotes];
                    for (int i = 0, j = 0; i < chars.length; i++) {
                        if (chars[i] != '"') {
                            newChars[j++] = chars[i];
                        } else {
                            newChars[j++] = '\\';
                            newChars[j++] = chars[i];
                        }
                    }
                    name = new String(newChars);
                }
                out.print(name);
                out.print('"');
            }
            if (action != null) {
                out.print(", \"");
                out.print(action);
                out.print('"');
            }
            if (signedBy != null) {
                out.print(", signedBy \"");
                out.print(signedBy);
                out.print('"');
            }
            out.println(";");
        }

	protected void createTools(JToolBar palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new TextFigure());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));

		// the generic but slower version
		//tool = new CreationTool(new PertFigure());
		//palette.add(createToolButton(PERTIMAGES + "PERT", "Task Tool", tool));

		tool = new PertFigureCreationTool(this);
		palette.add(createToolButton(PERTIMAGES + "PERT", "Task Tool", tool));

		tool = new ConnectionTool(this, new PertDependency());
		palette.add(createToolButton(IMAGES + "CONN", "Dependency Tool", tool));

		tool = new CreationTool(this, new LineFigure());
		palette.add(createToolButton(IMAGES + "Line", "Line Tool", tool));
	}

	//-- main -----------------------------------------------------------

	public static void main(String[] args) {
		PertApplication pert = new PertApplication();
		pert.open();
	}
}
