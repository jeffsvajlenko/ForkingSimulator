/*
 * @(#)StorableInput.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.io.*;
import java.awt.Color;
import java.util.List;

/**
 * An input stream that can be used to resurrect Storable objects.
 * StorableInput preserves the object identity of the stored objects.
 *
 * @see Storable
 * @see StorableOutput
 *
 * @version <$CURRENT_VERSION$>s
 */
public class StorableInput {

	private StreamTokenizer fTokenizer;
	private List            fMap;

	/**
	 * Initializes a Storable input with the given input stream.
	 */
	public StorableInput(InputStream stream) {
		Reader r = new BufferedReader(new InputStreamReader(stream));
		fTokenizer = new StreamTokenizer(r);
		// include inner class separate in class names
		fTokenizer.wordChars('$', '$');
		fMap = CollectionsFactory.current().createList();
	}
    public void transform(double[] srcPts, int srcOff,
                          double[] dstPts, int dstOff,
                          int numPts) {
        double M00, M01, X1, M10, M11, M12;    // For caching
        if (dstPts == srcPts &&
            dstOff > srcOff && dstOff < srcOff + numPts * 2)
        {
            // If the arrays overlap partially with the destination higher
            // than the source and we transform the coordinates normally
            // we would overwrite some of the later source coordinates
            // with results of previous transformations.
            // To get around this we use arraycopy to copy the points
            // to their final destination with correct overwrite
            // handling and then transform them in place in the new
            // safer location.
            System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * 2);
            // srcPts = dstPts;         // They are known to be equal.
            srcOff = dstOff;
        }
        switch (state) {
        default:
            stateError();
            /* NOTREACHED */
        case (APPLY_SHEAR | APPLY_SCALE | APPLY_TRANSLATE):
            M00 = m00; M01 = m01; X1 = m02;
            M10 = m10; M11 = m11; M12 = m12;
            while (--numPts >= 0) {
                double x = srcPts[srcOff++];
                double y = srcPts[srcOff++];
                dstPts[dstOff++] = M00 * x + M01 * y + X1;
                dstPts[dstOff++] = M10 * x + M11 * y + M12;
            }
            return;
        case (APPLY_SHEAR | APPLY_SCALE):
            M00 = m00; M01 = m01;
            M10 = m10; M11 = m11;
            while (--numPts >= 0) {
                double x = srcPts[srcOff++];
                double y = srcPts[srcOff++];
                dstPts[dstOff++] = M00 * x + M01 * y;
                dstPts[dstOff++] = M10 * x + M11 * y;
            }
            return;
        case (APPLY_SHEAR | APPLY_TRANSLATE):
            M01 = m01; X1 = m02;
            M10 = m10; M12 = m12;
            while (--numPts >= 0) {
                double x = srcPts[srcOff++];
                dstPts[dstOff++] = M01 * srcPts[srcOff++] + X1;
                dstPts[dstOff++] = M10 * x + M12;
            }
            return;
        case (APPLY_SHEAR):
            M01 = m01; M10 = m10;
            while (--numPts >= 0) {
                double x = srcPts[srcOff++];
                dstPts[dstOff++] = M01 * srcPts[srcOff++];
                dstPts[dstOff++] = M10 * x;
            }
            return;
        case (APPLY_SCALE | APPLY_TRANSLATE):
            M00 = m00; X1 = m02;
            M11 = m11; M12 = m12;
            while (--numPts >= 0) {
                dstPts[dstOff++] = M00 * srcPts[srcOff++] + X1;
                dstPts[dstOff++] = M11 * srcPts[srcOff++] + M12;
            }
            return;
        case (APPLY_SCALE):
            M00 = m00; M11 = m11;
            while (--numPts >= 0) {
                dstPts[dstOff++] = M00 * srcPts[srcOff++];
                dstPts[dstOff++] = M11 * srcPts[srcOff++];
            }
            return;
        case (APPLY_TRANSLATE):
            X1 = m02; M12 = m12;
            while (--numPts >= 0) {
                dstPts[dstOff++] = srcPts[srcOff++] + X1;
                dstPts[dstOff++] = srcPts[srcOff++] + M12;
            }
            return;
        case (APPLY_IDENTITY):
            if (srcPts != dstPts || srcOff != dstOff) {
                System.arraycopy(srcPts, srcOff, dstPts, dstOff,
                                 numPts * 2);
            }
            return;
        }

        /* NOTREACHED */
    }

	/**
	 * Reads and resurrects a Storable object from the input stream.
	 */
	public Storable readStorable() throws IOException {
		Storable storable;
		String s = readString();

		if (s.equals("NULL")) {
			return null;
		}

		if (s.equals("REF")) {
			int ref = readInt();
			return (Storable) retrieve(ref);
		}

		storable = (Storable) makeInstance(s);
		map(storable);
		storable.read(this);
		return storable;
	}

	/**
	 * Reads a string from the input stream.
	 */
	public String readString() throws IOException {
		int token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_WORD || token == '"') {
			return fTokenizer.sval;
		}

		String msg = "String expected in line: " + fTokenizer.lineno();
		throw new IOException(msg);
	}

	/**
	 * Reads an int from the input stream.
	 */
	public int readInt() throws IOException {
		int token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_NUMBER) {
			return (int) fTokenizer.nval;
		}

		String msg = "Integer expected in line: " + fTokenizer.lineno();
		IOException exception =  new IOException(msg);
		exception.printStackTrace();
		throw new IOException(msg);
	}

	/**
	 * Reads an int from the input stream.
	 */
	public long readLong() throws IOException {
		long token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_NUMBER) {
			return (long)fTokenizer.nval;
		}
		String msg = "Long expected in line: " + fTokenizer.lineno();
		IOException exception =  new IOException(msg);
		//exception.printStackTrace();
		throw exception;
	}

	/**
	 * Reads a color from the input stream.
	 */
	public Color readColor() throws IOException {
		return new Color(readInt(), readInt(), readInt());
	}

	/**
	 * Reads a double from the input stream.
	 */
	public double readDouble() throws IOException {
		int token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_NUMBER) {
			return fTokenizer.nval;
		}

		String msg = "Double expected in line: " + fTokenizer.lineno();
		throw new IOException(msg);
	}

	/**
	 * Reads a boolean from the input stream.
	 */
	public boolean readBoolean() throws IOException {
		int token = fTokenizer.nextToken();
		if (token == StreamTokenizer.TT_NUMBER) {
			return ((int) fTokenizer.nval) == 1;
		}

		String msg = "Integer expected in line: " + fTokenizer.lineno();
		throw new IOException(msg);
	}

	private Object makeInstance(String className) throws IOException {
		try {
			Class cl = Class.forName(className);
			return cl.newInstance();
		}
		catch (NoSuchMethodError e) {
			throw new IOException("Class " + className
				+ " does not seem to have a no-arg constructor");
		}
		catch (ClassNotFoundException e) {
			throw new IOException("No class: " + className);
		}
		catch (InstantiationException e) {
			throw new IOException("Cannot instantiate: " + className);
		}
		catch (IllegalAccessException e) {
			throw new IOException("Class (" + className + ") not accessible");
		}
	}

	private void map(Storable storable) {
		if (!fMap.contains(storable)) {
			fMap.add(storable);
		}
	}

	private Storable retrieve(int ref) {
		return (Storable)fMap.get(ref);
	}
}
