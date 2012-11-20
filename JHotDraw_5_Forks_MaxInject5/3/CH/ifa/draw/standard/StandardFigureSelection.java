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
   protected void engineInitVerify(Key publicKey) throws XMLSignatureException {

      if (!(publicKey instanceof PublicKey)) {
         String supplied = publicKey.getClass().getName();
         String needed = PublicKey.class.getName();
         Object exArgs[] = { supplied, needed };

         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation",
                                         exArgs);
      }

      try {
         this._signatureAlgorithm.initVerify((PublicKey) publicKey);
      } catch (InvalidKeyException ex) {
            // reinstantiate Signature object to work around bug in JDK
            // see: http://bugs.sun.com/view_bug.do?bug_id=4953555
            Signature sig = this._signatureAlgorithm;
            try {
                this._signatureAlgorithm = Signature.getInstance
                    (_signatureAlgorithm.getAlgorithm());
            } catch (Exception e) {
                // this shouldn't occur, but if it does, restore previous
                // Signature
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "Exception when reinstantiating Signature:" + e);
                }
                this._signatureAlgorithm = sig;
            }
            throw new XMLSignatureException("empty", ex);
      }
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

