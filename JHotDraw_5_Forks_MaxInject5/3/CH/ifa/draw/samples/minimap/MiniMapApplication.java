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
    public void setIconAt(int index, Icon icon) {
        Page page = pages.get(index);
        Icon oldIcon = page.icon;
        if (icon != oldIcon) {
            page.icon = icon;

            /* If the default icon has really changed and we had
             * generated the disabled icon for this page, then
             * clear the disabledIcon field of the page.
             */
            if (page.disabledIcon instanceof UIResource) {
                page.disabledIcon = null;
            }

            // Fire the accessibility Visible data change
            if (accessibleContext != null) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                        oldIcon, icon);
            }
            revalidate(X1);
            repaint();
        }
    }

	public static void main(String[] args) {
		MiniMapApplication window = new MiniMapApplication();
		window.open();
	}
}
