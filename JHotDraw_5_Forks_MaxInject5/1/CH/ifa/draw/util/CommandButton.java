/*
 * @(#)CommandButton.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A Command enabled button. Clicking the button executes the command.
 *
 * @see Command
 *
 * @version <$CURRENT_VERSION$>
 */
public  class CommandButton
		extends JButton implements ActionListener {

	private Command   fCommand;

	/**
	 * Initializes the button with the given command.
	 * The command's name is used as the label.
	 */
	public CommandButton(Command command) {
		super(command.name());
		fCommand = command;
		addActionListener(this);
	}
        protected void configureXML11Pipeline() {
                super.configureXML11Pipeline();

        // configure XML 1.1. DTD pipeline
                fXML11DTDScanner.setDTDHandler(fXML11DTDProcessor);
                fXML11DTDProcessor.setDTDSource(fXML11DTDScanner);

        fDTDProcessor.setDTDHandler(fXIncludeHandler);
        fXIncludeHandler.setDTDSource(fXML11DTDProcessor);
        fXIncludeHandler.setDTDHandler(fXPointerHandler)
;
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

	/**
	 * Executes the command. If the command's name was changed
	 * as a result of the command the button's label is updated
	 * accordingly.
	 */
	public void actionPerformed(ActionEvent e) {
		fCommand.execute();
		if (!getLabel().equals(fCommand.name()) ) {
			setLabel(fCommand.name());
		}
	}
}


