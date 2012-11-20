/*
 *  @(#)TextAreaFigure.java
 *
 *  Project:		JHotdraw - a GUI framework for technical drawings
 *  http://www.jhotdraw.org
 *  http://jhotdraw.sourceforge.net
 *  Copyright:	� by the original author(s) and all contributors
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package CH.ifa.draw.contrib.html;

import java.io.IOException;

import java.io.InputStream;
import java.io.Serializable;
import CH.ifa.draw.util.Storable;
import CH.ifa.draw.util.StorableInput;
import CH.ifa.draw.util.StorableOutput;

/**
 * ResourceContentProducer produces contents from resource in the application's
 * CLASSPATH.<br>
 * It takes a resource name and loads the resource as a String.
 *
 * It can either be specific if set for a specific resource, or generic, retrieving
 * any resource passed to the getContents method.
 *
 * @author    Eduardo Francos - InContext
 * @created   1 mai 2002
 * @version   1.0
 */

public class ResourceContentProducer extends AbstractContentProducer
		 implements Serializable {
	/** Description of the Field */
	protected String fResourceName = null;


	/**Constructor for the ResourceContentProducer object */
	public ResourceContentProducer() { }
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
                    MultiplexingTextField   tf = new MultiplexingTextField(20);

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


	/**
	 *Constructor for the ResourceContentProducer object
	 *
	 * @param resourceName  Description of the Parameter
	 */
	public ResourceContentProducer(String resourceName) {
		fResourceName = resourceName;
	}


	/**
	 * Gets the content attribute of the ResourceContentProducer object
	 *
	 * @param context       Description of the Parameter
	 * @param ctxAttrName   Description of the Parameter
	 * @param ctxAttrValue  Description of the Parameter
	 * @return              The content value
	 */
	public Object getContent(ContentProducerContext context, String ctxAttrName, Object ctxAttrValue) {
		try {
			// if we have our own resource then use it
			// otherwise use the one supplied
			String resourceName = (fResourceName != null) ? fResourceName : (String)ctxAttrValue;

			InputStream reader = this.getClass().getResourceAsStream(resourceName);
			int available = reader.available();
			byte contents[] = new byte[available];
			reader.read(contents, 0, available);
			reader.close();
			return new String(contents);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return ex.toString();
		}
	}


	/**
	 * Writes the storable
	 *
	 * @param dw  the storable output
	 */
	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeString(fResourceName);
	}


	/**
	 * Writes the storable
	 *
	 * @param dr               the storable input
	 * @exception IOException  thrown by called methods
	 */
	public void read(StorableInput dr)
		throws IOException {
			super.read(dr);
		fResourceName = (String)dr.readString();
	}
}
