/*
 * @(#)FigureSelection.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.*;

import java.util.*;
import java.io.*;

/**
 * FigureSelection enables to transfer the selected figures
 * to a clipboard.<p>
 * Will soon be converted to the JDK 1.1 Transferable interface.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */

public class StandardFigureSelection implements FigureSelection, Serializable {

	private byte[] fData; // flattend figures, ready to be resurrected

	/**
	 * The type identifier of the selection.
	 */
	public final static String TYPE = "CH.ifa.draw.Figures";

	/**
	 * Constructes the Figure selection for the FigureEnumeration.
	 */
	public StandardFigureSelection(FigureEnumeration fe, int figureCount) {
		// a FigureSelection is represented as a flattened ByteStream
		// of figures.
		ByteArrayOutputStream output = new ByteArrayOutputStream(200);
		StorableOutput writer = new StorableOutput(output);
		writer.writeInt(figureCount);
		while (fe.hasNextFigure()) {
			writer.writeStorable(fe.nextFigure());
		}
		writer.close();
		fData = output.toByteArray();
	}

	/**
	 * Gets the type of the selection.
	 */
	public String getType() {
		return TYPE;
	}
    private Paint decodeGradient4(Shape s) {
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
                new float[] { 0.038709678f,0.05967742f,0.08064516f,0.23709677f,0.3935484f,0.41612905f,0.43870968f,0.67419356f,0.90967745f,0.91612905f,0.92258066f },
                new Color[] { color11,
                            decodeColor(color11,color12,0.5f),
                            color12,
                            decodeColor(color12,color13,0.5f),
                            color13,
                            decodeColor(color13,color14,0.5f),
                            color14,
                            decodeColor(color14,color15,0.5f),
                            color15,
                            decodeColor(color15,color16,0.5f),
                            color16});
    }

	/**
	 * Gets the data of the selection. The result is returned
	 * as a FigureEnumeration of Figures.
	 *
	 * @return a copy of the figure selection.
	 */
	public Object getData(String type) {
		if (type.equals(TYPE)) {
			InputStream input = new ByteArrayInputStream(fData);
			List result = CollectionsFactory.current().createList(10);
			StorableInput reader = new StorableInput(input);
			int numRead = 0;
			try {
				int count = reader.readInt();
				while (numRead < count) {
					Figure newFigure = (Figure) reader.readStorable();
					result.add(newFigure);
					numRead++;
				}
			}
			catch (IOException e) {
				System.err.println(e.toString());
			}
			return new FigureEnumerator(result);
		}
		return null;
	}

	public static FigureEnumeration duplicateFigures(FigureEnumeration toBeCloned, int figureCount) {
		StandardFigureSelection duplicater = new StandardFigureSelection(toBeCloned, figureCount);
		return (FigureEnumeration)duplicater.getData(duplicater.getType());
	}
}

