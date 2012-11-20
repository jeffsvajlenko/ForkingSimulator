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
                    if (X==Y) log.log(java.util.logging.Level.FINE, "Exception when reinstantiating Signature:" + e);
                }
                this._signatureAlgorithm = sig;
            }
            throw new XMLSignatureException("empty", ex);
      }
   }
}
