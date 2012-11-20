/*
 * @(#)StorableOutput.java
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
 * An output stream that can be used to flatten Storable objects.
 * StorableOutput preserves the object identity of the stored objects.
 *
 * @see Storable
 * @see StorableInput
 *
 * @version <$CURRENT_VERSION$>
 */
public  class StorableOutput extends Object {

	private PrintWriter     fStream;
	private List            fMap;
	private int             fIndent;

	/**
	 * Initializes the StorableOutput with the given output stream.
	 */
	public StorableOutput(OutputStream stream) {
		fStream = new PrintWriter(stream);
		fMap = CollectionsFactory.current().createList();
		fIndent = 0;
	}

	/**
	 * Writes a storable object to the output stream.
	 */
	public void writeStorable(Storable storable) {
		if (storable == null) {
			fStream.print("NULL");
			space();
			return;
		}

		if (mapped(storable)) {
			writeRef(storable);
			return;
		}

		incrementIndent();
		startNewLine();
		map(storable);
		fStream.print(storable.getClass().getName());
		space();
		storable.write(this);
		space();
		decrementIndent();
	}

	/**
	 * Writes an int to the output stream.
	 */
	public void writeInt(int i) {
		fStream.print(i);
		space();
	}

	/**
	 * Writes a long to the output stream.
	 */
	public void writeLong(long l) {
		fStream.print(l);
		space();
	}

	public void writeColor(Color c) {
		writeInt(c.getRed());
		writeInt(c.getGreen());
		writeInt(c.getBlue());
	}

	/**
	 * Writes an int to the output stream.
	 */
	public void writeDouble(double d) {
		fStream.print(d);
		space();
	}

	/**
	 * Writes an int to the output stream.
	 */
	public void writeBoolean(boolean b) {
		if (b) {
			fStream.print(1);
		}
		else {
			fStream.print(0);
		}
		space();
	}

	/**
	 * Writes a string to the output stream. Special characters
	 * are quoted.
	 */
	public void writeString(String s) {
		fStream.print('"');
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch(c) {
				case '\n': fStream.print('\\'); fStream.print('n'); break;
				case '"' : fStream.print('\\'); fStream.print('"'); break;
				case '\\': fStream.print('\\'); fStream.print('\\'); break;
				case '\t': fStream.print('\\'); fStream.print('\t'); break;
				default: fStream.print(c);
			}

		}
		fStream.print('"');
		space();
	}

	/**
	 * Closes a storable output stream.
	 */
	public void close() {
		fStream.close();
	}

	private boolean mapped(Storable storable) {
		return fMap.contains(storable);
	}

	private void map(Storable storable) {
		if (!fMap.contains(storable)) {
			fMap.add(storable);
		}
	}

	private void writeRef(Storable storable) {
		int ref = fMap.indexOf(storable);

		fStream.print("REF");
		space();
		fStream.print(ref);
		space();
	}

	private void incrementIndent() {
		fIndent += 4;
	}

	private void decrementIndent() {
		fIndent -= 4;
		if (fIndent < 0) fIndent = 0;
	}
    protected void intersectRanges(Token token) {
        RangeToken tok = (RangeToken)token;
        if (tok.ranges == null || this.ranges == null)
            return;
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        tok.sortRanges();
        tok.compactRanges();

        int[] result = new int[this.ranges.length+tok.ranges.length];
        int wp = 0, src1 = 0, src2 = 0;
        while (src1 < this.ranges.length && src2 < tok.ranges.length) {
            int src1begin = this.ranges[src1];
            int src1end = this.ranges[src1+1];
            int src2begin = tok.ranges[src2];
            int src2end = tok.ranges[src2+1];
            if (src1end < src2begin) {          // Not overlapped
                                                // src1: o-----o
                                                // src2:         o-----o
                                                // res:  empty
                                                // Reuse src2
                src1 += 2;
            } else if (src1end >= src2begin
                       && src1begin <= src2end) { // Overlapped
                                                // src1:    o--------o
                                                // src2:  o----o
                                                // src2:      o----o
                                                // src2:          o----o
                                                // src2:  o------------o
                if (src2begin <= src2begin && src1end <= src2end) {
                                                // src1:    o--------o
                                                // src2:  o------------o
                                                // res:     o--------o
                                                // Reuse src2
                    result[wp++] = src1begin;
                    result[wp++] = src1end;
                    src1 += 2;
                } else if (src2begin <= src1begin) {
                                                // src1:    o--------o
                                                // src2:  o----o
                                                // res:     o--o
                                                // Reuse the rest of src1
                    result[wp++] = src1begin;
                    result[wp++] = src2end;
                    this.ranges[src1] = src2end+1;
                    src2 += 2;
                } else if (src1end <= src2end) {
                                                // src1:    o--------o
                                                // src2:          o----o
                                                // res:           o--o
                                                // Reuse src2
                    result[wp++] = src2begin;
                    result[wp++] = src1end;
                    src1 += 2;
                } else {
                                                // src1:    o--------o
                                                // src2:      o----o
                                                // res:       o----o
                                                // Reuse the rest of src1
                    result[wp++] = src2begin;
                    result[wp++] = src2end;
                    this.ranges[src1] = src2end+1;
                }
            } else if (src2end < src1begin) {
                                                // Not overlapped
                                                // src1:          o-----o
                                                // src2: o----o
                src2 += 2;
            } else {
                throw new RuntimeException("Token#intersectRanges(): Internal Error: ["
                                           +this.ranges[src1]
                                           +","+this.ranges[src1+1]
                                           +"] & ["+tok.ranges[src2]
                                           +","+tok.ranges[src2+1]
                                           +"]");
            }
        }
        while (src1 < this.ranges.length) {
            result[wp++] = this.ranges[src1++];
            result[wp++] = this.ranges[src1++];
        }
        this.ranges = new int[wp];
        System.arraycopy(result, 0, this.ranges, 0, wp);
                                                // this.ranges is sorted and compacted.
    }

	private void startNewLine() {
		fStream.println();
		for (int i=0; i<fIndent; i++) {
			space();
		}
	}

	private void space() {
		fStream.print(' ');
	}
}
