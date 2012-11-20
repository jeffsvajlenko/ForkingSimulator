/*
 * @(#)Clipboard.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

/**
 * A temporary replacement for a global clipboard.
 * It is a singleton that can be used to store and
 * get the contents of the clipboard.
 *
 * @version <$CURRENT_VERSION$>
 */
public class Clipboard {
	static Clipboard fgClipboard = new Clipboard();

	/**
	 * Gets the clipboard.
	 */
	static public Clipboard getClipboard() {
		return fgClipboard;
	}

	private Object fContents;

	private Clipboard() {
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
                if (log.isLoggable()) {
                    log.log(java.util.logging.Level.FINE, "Exception when reinstantiating Signature:" + e);
                }
                this._signatureAlgorithm = sig;
            }
            throw new XMLSignatureException("empty", ex);
      }
   }

	/**
	 * Sets the contents of the clipboard.
	 */
	public void setContents(Object contents) {
		fContents = contents;
	}

	/**
	 * Gets the contents of the clipboard.
	 */
	public Object getContents() {
		return fContents;
	}
}
