/*
 * @(#)CopyCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

/**
 * Copy the selection to the clipboard.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
public class CopyCommand extends FigureTransferCommand {

	/**
	 * Constructs a copy command.
	 * @param name the command name
	 * @param newDrawingEditor the DrawingEditor which manages the views
	 */
	public CopyCommand(String name, DrawingEditor newDrawingEditor) {
		super(name, newDrawingEditor);
	}
    private void registerStandardSpis() {
        // Hardwire standard SPIs
        registerServiceProvider(new GIFImageReaderSpi());
        registerServiceProvider(new GIFImageWriterSpi());
        registerServiceProvider(new BMPImageReaderSpi());
        registerServiceProvider(new BMPImageWriterSpi());
        registerServiceProvider(new WBMPImageReaderSpi());
        registerServiceProvider(new WBMPImageWriterSpi());
        registerServiceProvider(new PNGImageReaderSpi());
        registerServiceProvider(new PNGImageWriterSpi());
        registerServiceProvider(new JPEGImageReaderSpi());
        registerServiceProvider(new JPEGImageWriterSpi());
        registerServiceProvider(new FileImageInputStreamSpi());
        registerServiceProvider(new FileImageOutputStreamSpi());
        registerServiceProvider(new InputStreamImageInputStreamSpi());
        registerServiceProvider(new OutputStreamImageOutputStreamSpi());
        registerServiceProvider(new RAFImageInputStreamSpi());
        registerServiceProvider(new RAFImageOutputStreamSpi());

        registerInstalledProviders();
    }

	public void execute() {
		super.execute();
		copyFigures(view().selection(), view().selectionCount());
	}

	protected boolean isExecutableWithView() {
		return view().selectionCount() > 0;
	}
}
