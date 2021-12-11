

package cn.loli.client.gui.clickgui.layout;

import cn.loli.client.gui.clickgui.AbstractComponent;

import java.util.HashMap;
import java.util.List;

public class FlowLayout implements ILayoutManager {
    private static final int DEFAULT_VERTICAL_PADDING = 7;
    private static final int DEFAULT_HORIZONTAL_PADDING = 7;

    private int verticalPadding;
    private int horizontalPadding;

    public FlowLayout(int verticalPadding, int horizontalPadding) {
        this.verticalPadding = verticalPadding;
        this.horizontalPadding = horizontalPadding;
    }

    public FlowLayout() {
        this(DEFAULT_VERTICAL_PADDING, DEFAULT_HORIZONTAL_PADDING);
    }

    public int getVerticalPadding() {
        return verticalPadding;
    }

    public void setVerticalPadding(int verticalPadding) {
        this.verticalPadding = verticalPadding;
    }

    public int getHorizontalPadding() {
        return horizontalPadding;
    }

    public void setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
    }

    @Override
    public int[] getOptimalDimension(List<AbstractComponent> components, int maxWidth) {
        int width = -1;
        int height = -1;

        int currX = verticalPadding;
        int currY = horizontalPadding;

        int maxHeight = -1;

        for (AbstractComponent component : components) {
            int newX = currX + component.getWidth() + verticalPadding;

            if (newX > maxWidth) {
                currY += maxHeight;

                maxHeight = -1;
                currX = verticalPadding;

                newX = currX + component.getWidth() + verticalPadding;
            }

            if (component.getHeight() + horizontalPadding > maxHeight) {
                maxHeight = component.getHeight() + horizontalPadding;
            }

            width = Math.max(width, newX);
            height = Math.max(height, currY + component.getHeight() + horizontalPadding);

            currX = newX;

        }

        return new int[]{width, height};
    }

    @Override
    public Layout buildLayout(List<AbstractComponent> components, int width, int height) {
        HashMap<AbstractComponent, int[]> map = new HashMap<>();

        int currX = verticalPadding;
        int currY = horizontalPadding;

        int maxHeight = -1;

        for (AbstractComponent component : components) {
            int newX = currX + component.getWidth() + verticalPadding;

            if (newX > width) {
                currY += maxHeight;

                maxHeight = -1;
                currX = verticalPadding;

                newX = currX + component.getWidth() + verticalPadding;
            }

            if (component.getHeight() + horizontalPadding > maxHeight) {
                maxHeight = component.getHeight() + horizontalPadding;
            }

            map.put(component, new int[]{currX, currY});

            currX = newX;

        }

        return new Layout(map, map.entrySet().stream().map(entry -> entry.getValue()[1] + entry.getKey().getHeight()).max(Integer::compareTo).orElse(0), map.entrySet().stream().map(entry -> entry.getValue()[0] + entry.getKey().getWidth()).max(Integer::compareTo).orElse(0));
    }
}
