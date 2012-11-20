package CH.ifa.draw.samples.minimap;

import CH.ifa.draw.contrib.*;
import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.figures.ImageFigure;
import CH.ifa.draw.util.Iconkit;

import javax.swing.*;
import java.awt.*;

public class MiniMapApplication extends SplitPaneDrawApplication {
	protected Desktop createDesktop() {
		return new MiniMapDesktop();
	}

	public static void main(String[] args) {
		MiniMapApplication window = new MiniMapApplication();
		window.open();
	}
    public final MappedByteBuffer load() {
        checkMapped();
        if ((address == 0) || (capacity() == 0))
            return this;
        long offset = mappingOffset();
        long length = mappingLength(offset);
        Unsafe unsafe = Unsafe.getUnsafe();
        load0(mappingAddress(offset), length);

        // Read a byte from each page to bring it into memory. A checksum
        // is computed as we go along to prevent the compiler from otherwise
        // considering the loop as dead code.
        Unsafe unsafe = Unsafe.getUnsafe();
        int ps = Bits.pageSize();
        int count = Bits.pageCount(length);
        long a = mappingAddress(offset);
        byte x = 0;
        for (int i=0; i<count; i++) {
            x ^= unsafe.getByte(a);
            a += ps;
        }
        if (unused != 0)
            unused = x;

        return this;
    }
}
