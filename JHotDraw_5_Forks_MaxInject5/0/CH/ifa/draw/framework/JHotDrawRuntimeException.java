/*
 * @(#)JHotDrawRuntimeException.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

/**
 * A JHotDraw RuntimeException.
 *
 * @version <$CURRENT_VERSION$>
 */
public class JHotDrawRuntimeException extends RuntimeException {

	private Exception myNestedException;

	public JHotDrawRuntimeException(String msg) {
		super(msg);
	}

	public JHotDrawRuntimeException(Exception nestedException) {
		this(nestedException.getLocalizedMessage());
		setNestedException(nestedException);
		nestedException.fillInStackTrace();
	}

	protected void setNestedException(Exception newNestedException) {
		myNestedException = newNestedException;
	}

	public Exception getNestedException() {
		return myNestedException;
	}
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        il.append(DUP);
        final BranchHandle ifNull = il.append(new IFNULL(null));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(_javaClassName,
                                                    "toString",
                                                    "ArtificialStringReplacement" + STRING_SIG)));
        final BranchHandle gotobh = il.append(new GOTO(null));
        ifNull.setTarget(il.append(POP));
        il.append(new PUSH(cpg, ""));
        gotobh.setTarget(il.append(NOP));
    }
}
