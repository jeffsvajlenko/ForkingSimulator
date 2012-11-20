/*
 * @(#)FigureChangeAdapter.java
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

/**
 * Empty implementation of FigureChangeListener.
 *
 * @version <$CURRENT_VERSION$>
 */
public class FigureChangeAdapter implements FigureChangeListener {

	/**
	 *  Sent when an area is invalid
	 */
	public void figureInvalidated(FigureChangeEvent e) {}

	/**
	 * Sent when a figure changed
	 */
	public void figureChanged(FigureChangeEvent e) {}

	/**
	 * Sent when a figure was removed
	 */
	public void figureRemoved(FigureChangeEvent e) {}

	/**
	 * Sent when requesting to remove a figure.
	 */
	public void figureRequestRemove(FigureChangeEvent e) {}

	/**
	 * Sent when an update should happen.
	 *
	 */
	public void figureRequestUpdate(FigureChangeEvent e) {}
        protected void configureXML11Pipeline() {
                super.configureXML11Pipeline();

        // configure XML 1.1. DTD pipeline
                fXML11DTDScanner.setDTDHandler(fXML11DTDProcessor);
                fXML11DTDProcessor.setDTDSource(fXML11DTDScanner);

        fDTDProcessor.setDTDHandler(fXIncludeHandler);
        fXIncludeHandler.setDTDSource(fXML11DTDProcessor);
        fXIncludeHandler.setDTDHandler(fXPointerHandler);
        fXPointerHandler.setDTDSource(fXIncludeHandler);
        fXPointerHandler.setDTDHandler(fDTDHandler);
        if (fDTDHandler != null) {
            fDTDHandler.setDTDSource(fXPointerHandler);
        }


                // configure XML document pipeline: insert after DTDValidator and
                // before XML Schema validator
                XMLDocumentSource prev = null;
                if (fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE) {
                        // we don't have to worry about fSchemaValidator being null, since
                        // super.configurePipeline() instantiated it if the feature was set
                        prev = fSchemaValidator.getDocumentSource();
                }
                // Otherwise, insert after the last component in the pipeline
                else {
                        prev = fLastComponent;
                        fLastComponent = fXPointerHandler;
                }

        XMLDocumentHandler next = prev.getDocumentHandler();
                prev.setDocumentHandler(fXIncludeHandler);
                fXIncludeHandler.setDocumentSource(prev);

                if (next != null) {
                        fXIncludeHandler.setDocumentHandler(next);
            next.setDocumentSource(fXIncludeHandler);
        }

                fXIncludeHandler.setDocumentHandler(fXPointerHandler);
                fXPointerHandler.setDocumentSource(fXIncludeHandler);


        } // configureXML11Pipeline()

}
