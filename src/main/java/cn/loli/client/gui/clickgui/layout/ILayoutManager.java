

package cn.loli.client.gui.clickgui.layout;

import cn.loli.client.gui.clickgui.AbstractComponent;

import java.util.List;

public interface ILayoutManager {
    int[] getOptimalDimension(List<AbstractComponent> components, int maxWidth);

    Layout buildLayout(List<AbstractComponent> components, int width, int height);
}
