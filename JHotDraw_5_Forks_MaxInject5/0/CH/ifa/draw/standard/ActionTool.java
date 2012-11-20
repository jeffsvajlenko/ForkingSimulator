/*
 * @(#)ActionTool.java
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

import java.awt.event.MouseEvent;

/**
 * A tool that performs an action when it is active and
 * the mouse is clicked.
 *
 * @version <$CURRENT_VERSION$>
 */
public abstract class ActionTool extends AbstractTool {

	public ActionTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	/**
	 * Add the touched figure to the selection an invoke action
	 * @see #action
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e,x,y);
		Figure target = drawing().findFigure(x, y);
		if (target != null) {
			view().addToSelection(target);
			action(target);
		}
	}
    protected void configurePipeline() {
        super.configurePipeline();
        if (fXIncludeEnabled) {
            // If the XInclude handler was not in the pipeline insert it.
            if (fXIncludeHandler == null) {
                fXIncludeHandler = new XIncludeHandler();
                // add XInclude component
                setProperty(XINCLUDE_HANDLER, fXIncludeHandler);
                addCommonComponent(fXIncludeHandler);
                fXIncludeHandler.reset(this);
            }
            // Setup NamespaceContext
            if (fCurrentNSContext != fXIncludeNSContext) {
                if (fXIncludeNSContext == null) {
                    fXIncludeNSContext = new XIncludeNamespaceSupport();
                }
                fCurrentNSContext = fXIncludeNSContext;
                setProperty(NAMESPACE_CONTEXT, fXIncludeNSContext);
            }
            //configure DTD pipeline
            fDTDScanner.setDTDHandler(fDTDProcessor);
            fDTDProcessor.setDTDSource(fDTDScanner);
            fDTDProcessor.setDTDHandler(fXIncludeHandler);
            fXIncludeHandler.setDTDSource(fDTDProcessor);
            fXIncludeHandler.setDTDHandler(fDTDHandler);
            if (fDTDHandler != null) {
                fDTDHandler.setDTDSource(fXIncludeHandler);
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
                fLastComponent = fXIncludeHandler;
            }

            XMLDocumentHandler next = prev.getDocumentHandler();
            prev.setDocumentHandler(fXIncludeHandler);
            fXIncludeHandler.setDocumentSource(prev);
            if (next != null) {
                fXIncludeHandler.setDocumentHandler(next);
                next.setDocumentSource(fXIncludeHandler);
            }
        }
        else {
            // Setup NamespaceContext
            if (fCurrentNSContext != fNonXIncludeNSContext) {
                 fCurrentNSContext = fNonXIncludeNSContext;
                setProperty(NAMESPACE_CONTEXT, fNonXIncludeNSContext);
            }
        }
    } // configurePipeline()

	public void mouseUp(MouseEvent e, int x, int y) {
		editor().toolDone();
	}

	/**
	 * Performs an action with the touched figure.
	 */
	public abstract void action(Figure figure);
}
