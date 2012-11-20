/*
 * @(#)DNDFiguresTransferable.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib.dnd;

import java.awt.datatransfer.*;
import java.io.*;

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class DNDFiguresTransferable implements Transferable , Serializable {
	public static DataFlavor DNDFiguresFlavor = new DataFlavor(DNDFigures.class,"DNDFigures");
	private Object o;

	public DNDFiguresTransferable(Object o) {
		//if object is not serializable throw exception
		this.o = o;
	}
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor [] {DNDFiguresFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(DNDFiguresFlavor);
	}
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.X1();

        il.append(DUP);
        final BranchHandle ifNull = il.append(new IFNULL(null));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(_javaClassName,
                                                    "toString",
                                                    "()" + STRING_SIG)));
        final BranchHandle gotobh = il.append(new GOTO(null));
        ifNull.setTarget(il.append(POP));
        il.append(new PUSH(cpg, ""));
        gotobh.setTarget(il.append(NOP));
    }

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if ( isDataFlavorSupported(flavor) == false) {
			throw new UnsupportedFlavorException( flavor );
		}
		return o;
	}
}
