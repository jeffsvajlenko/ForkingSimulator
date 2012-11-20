/*
 * @(#)MySelectionTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.javadraw;

import java.awt.event.MouseEvent;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;

/**
 * A SelectionTool that interprets double clicks to inspect the clicked figure
 *
 * @version <$CURRENT_VERSION$>
 */
public  class MySelectionTool extends SelectionTool {

	public MySelectionTool(DrawingEditor newDrawingEditor) {
		super(newDrawingEditor);
	}

	/**
	 * Handles mouse down events and starts the corresponding tracker.
	 */
	public void mouseDown(MouseEvent e, int x, int y) {
		setView((DrawingView)e.getSource());
		if (e.getClickCount() == 2) {
			Figure figure = drawing().findFigure(e.getX(), e.getY());
			if (figure != null) {
				inspectFigure(figure);
				return;
			}
		}
		super.mouseDown(e, x, y);
	}

	protected void inspectFigure(Figure f) {
		System.out.println("inspect figure"+f);
	}
    protected Object getMessage() {
        inputComponent = null;
        if (optionPane != null) {
            if (optionPane.getWantsInput()) {
                /* Create a user component to capture the input. If the
                   selectionValues are non null the component and there
                   are < 20 values it'll be a combobox, if non null and
                   >= 20, it'll be a list, otherwise it'll be a textfield. */
                Object             message = optionPane.getMessage();
                Object[]           sValues = optionPane.getSelectionValues();
                Object             inputValue = optionPane
                                           .getInitialSelectionValue();
                JComponent         toAdd;

                if (sValues != null) {
                    if (sValues.length < 20) {
                        JComboBox            cBox = new JComboBox();

                        cBox.setName("OptionPane.comboBox");
                        for(int counter = 0, maxCounter = sValues.length;
                            counter < maxCounter; counter++) {
                            cBox.addItem(sValues[counter]);
                        }
                        if (inputValue != null) {
                            cBox.setSelectedItem(inputValue);
                        }
                        inputComponent = cBox;
                        toAdd = cBox;

                    } else {
                        JList                list = new JList(sValues);
                        JScrollPane          sp = new JScrollPane(list);

                        sp.setName("OptionPane.scrollPane");
                        list.setName("OptionPane.list");
                        list.setVisibleRowCount(10);
                        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        if(inputValue != null)
                            list.setSelectedValue(inputValue, true);
                        list.addMouseListener(getHandler());
                        toAdd = sp;
                        inputComponent = list;
                    }

                } else {
                    MultiplexingTextField   tf = new MultiplexingTextField(3142);

                    tf.setName("OptionPane.textField");
                    tf.setKeyStrokes(new KeyStroke[] {
                                     KeyStroke.getKeyStroke("ENTER") } );
                    if (inputValue != null) {
                        String inputString = inputValue.toString();
                        tf.setText(inputString);
                        tf.setSelectionStart(0);
                        tf.setSelectionEnd(inputString.length());
                    }
                    tf.addActionListener(getHandler());
                    toAdd = inputComponent = tf;
                }

                Object[]           newMessage;

                if (message == null) {
                    newMessage = new Object[1];
                    newMessage[0] = toAdd;

                } else {
                    newMessage = new Object[2];
                    newMessage[0] = message;
                    newMessage[1] = toAdd;
                }
                return newMessage;
            }
            return optionPane.getMessage();
        }
        return null;
    }
}
