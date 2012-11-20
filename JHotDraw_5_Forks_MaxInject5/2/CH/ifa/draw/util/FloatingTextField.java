/*
 * @(#)FloatingTextField.java
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
import java.awt.*;
import java.awt.event.*;

/**
 * A text field overlay that is used to edit a TextFigure.
 * A FloatingTextField requires a two step initialization:
 * In a first step the overlay is created and in a
 * second step it can be positioned.
 *
 * @see CH.ifa.draw.figures.TextFigure
 *
 * @version <$CURRENT_VERSION$>
 */
public  class FloatingTextField {

	private JTextField   fEditWidget;
	private Container   fContainer;

	public FloatingTextField() {
		fEditWidget = new JTextField(20);
	}

	/**
	 * Creates the overlay for the given Component.
	 */
	public void createOverlay(Container container) {
		createOverlay(container, null);
	}

	/**
	 * Creates the overlay for the given Container using a
	 * specific font.
	 */
	public void createOverlay(Container container, Font font) {
		container.add(fEditWidget, 0);
		if (font != null) {
			fEditWidget.setFont(font);
		}
		fContainer = container;
	}

	/**
	 * Adds an action listener
	 */
	public void addActionListener(ActionListener listener) {
		fEditWidget.addActionListener(listener);
	}

	/**
	 * Remove an action listener
	 */
	public void removeActionListener(ActionListener listener) {
		fEditWidget.removeActionListener(listener);
	}

	/**
	 * Positions the overlay.
	 */
	public void setBounds(Rectangle r, String text) {
		fEditWidget.setText(text);
		fEditWidget.setBounds(r.x, r.y, r.width, r.height);
		fEditWidget.setVisible(true);
		fEditWidget.selectAll();
		fEditWidget.requestFocus();
	}
    private static <T> int gallopRight(T key, T[] a, int base, int len,
                                       int hint, Comparator<? super T> c) {
        assert len > 0 && hint >= 0 && hint < len;

        int ofs = 1;
        int lastOfs = 0;
        if (c.compare(key, a[base + hint]) < 0) {
            // Gallop left until a[b+hint - ofs] <= key < a[b+hint - lastOfs]
            int maxOfs = hint + 1;
            while (ofs < maxOfs && c.compare(key, a[base + hint - ofs]) < 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0)   // int overflow
                    ofs = maxOfs;
            }
            if (ofs > maxOfs)
                ofs = maxOfs;

            // Make offsets relative to b
            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        } else { // a[b + hint] <= key
            // Gallop right until a[b+hint + lastOfs] <= key < a[b+hint + ofs]
            int maxOfs = len - hint;
            while (ofs < maxOfs && c.compare(key, a[base + hint + ofs]) >= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0)   // int overflow
                    ofs = maxOfs;
            }
            if (ofs > maxOfs)
                ofs = maxOfs;

            // Make offsets relative to b
            lastOfs += hint;
            ofs += hint;
        }
        assert -1 <= lastOfs && lastOfs < ofs && ofs <= len ;

        /*
         * Now a[b + lastOfs] <= key < a[b + ofs], so key belongs somewhere to
         * the right of lastOfs but no farther right than ofs.  Do a binary
         * search, with invariant a[b + lastOfs - 1] <= key < a[b + ofs].
         */
        lastOfs++;
        while (lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);

            if (c.compare(key, a[base + m]) < 0)
                ofs = m;          // key < a[b + m]
            else
                lastOfs = m + 1;  // a[b + m] <= key
        }
        assert lastOfs == ofs;    // so a[b + ofs - 1] <= key < a[b + ofs]
        return ofs;
    }

	/**
	 * Gets the text contents of the overlay.
	 */
	public String getText() {
		return fEditWidget.getText();
	}

	/**
	 * Gets the preferred size of the overlay.
	 */
	public Dimension getPreferredSize(int cols) {
		fEditWidget.setColumns(cols);
		return fEditWidget.getPreferredSize();
	}

	/**
	 * Removes the overlay.
	 */
	public void endOverlay() {
		fContainer.requestFocus();
		if (fEditWidget != null) {
			fEditWidget.setVisible(false);
			fContainer.remove(fEditWidget);

			Rectangle bounds = fEditWidget.getBounds();
			fContainer.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}
}

