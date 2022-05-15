package cn.loli.client.gui.clickui.components;

import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import dev.xix.property.AbstractTheresaProperty;
import dev.xix.property.impl.BooleanProperty;

import static cn.loli.client.gui.clickui.ClickGui.*;
import static cn.loli.client.gui.guiscreen.GuiReconnectIRC.isHovered;

public class BooleanComponent extends Component {

    public BooleanComponent(AbstractTheresaProperty v) {
        super(v);
    }

    @Override
    public void onMouse(int mouseX, int mouseY, int button) {
        if (isHovered(x, y, x+21, y + 10, mouseX, mouseY)) {
            value.setPropertyValue(!((BooleanProperty) value).getPropertyValue());
        }
    }

    @Override
    public void draw(float x, float y, float partialTicks) {
        this.x = x;
        this.y = y;
        RenderUtils.drawRoundRect(x, y, x + 21, y + 10, 5, theme.option_bg.getRGB());
        value.clickgui_anim = AnimationUtils.smoothAnimation(value.clickgui_anim, ((boolean) value.getPropertyValue()) ? 11 : 0, ANIMATION_SPEED, ANIMATION_SCALE);
        RenderUtils.drawFilledCircle(x + 5 + value.clickgui_anim, y + 5, 5, ((boolean) value.getPropertyValue()) ? theme.option_on.getRGB() : theme.option_off.getRGB(), 5);
    }
}
