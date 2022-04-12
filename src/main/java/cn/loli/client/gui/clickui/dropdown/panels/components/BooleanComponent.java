package cn.loli.client.gui.clickui.dropdown.panels.components;

import cn.loli.client.module.Module;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.Value;

import static cn.loli.client.gui.clickui.ClickGui.*;
import static cn.loli.client.value.ColorValue.isHovered;

public class BooleanComponent extends Component {

    public BooleanComponent(Value v) {
        super(v);
    }

    @Override
    public void onMouse(int mouseX, int mouseY, int button) {
        if (isHovered(x, y, x+21, y + 10, mouseX, mouseY)) {
            value.setObject(!((BooleanValue) value).getObject());
        }
    }

    @Override
    public void draw(float x, float y, float partialTicks) {
        this.x = x;
        this.y = y;
        RenderUtils.drawRoundRect(x, y, x + 21, y + 10, 5, theme.option_bg.getRGB());
        value.clickgui_anim = AnimationUtils.smoothAnimation(value.clickgui_anim, ((boolean) value.getObject()) ? 11 : 0, ANIMATION_SPEED, ANIMATION_SCALE);
        RenderUtils.drawFilledCircle(x + 5 + value.clickgui_anim, y + 5, 5, ((boolean) value.getObject()) ? theme.option_on.getRGB() : theme.option_off.getRGB(), 5);
    }
}
