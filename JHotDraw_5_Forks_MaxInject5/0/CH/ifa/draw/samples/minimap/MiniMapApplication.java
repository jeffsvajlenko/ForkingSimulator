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
    public static String createForJavaType(Class clz)
        throws com.sun.corba.se.impl.io.TypeMismatchException
    {
        synchronized (classToRepStr){
        String repid = createForSpecialCase(clz);
        if (repid != null)
            return repid;

        repid = (String)classToRepStr.get(clz);
        if (repid != null)
            return repid;

        repid = kValuePrefix + convertToISOLatin1(clz.getName()) +
            createHashString(clz);

        classToRepStr.put(clz, repid);
            repStrToClass.put(repid, clz);
        return repid;
    }
    }
}
