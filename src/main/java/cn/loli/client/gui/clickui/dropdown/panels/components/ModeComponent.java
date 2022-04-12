package cn.loli.client.gui.clickui.dropdown.panels.components;

import cn.loli.client.Main;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.Value;

import static cn.loli.client.gui.clickui.ClickGui.*;
import static cn.loli.client.gui.clickui.ClickGui.theme;
import static cn.loli.client.value.ColorValue.isHovered;

public class ModeComponent extends Component {

    public ModeComponent(Value v) {
        super(v);
    }

    @Override
    public void onMouse(int mouseX, int mouseY, int button) {
        HFontRenderer font = Main.INSTANCE.fontLoaders.get("heiti16");
        float width2 = 0;
        for (String mode : ((ModeValue) value).getModes()) {
            float temp = font.getStringWidth(mode);
            if (width2 < temp) width2 = temp;
        }

        if (isHovered(x + 5 - width2, y - 1, x + 20, y + 11, mouseX, mouseY)) {
            ((ModeValue) value).open = !((ModeValue) value).open;
        }

        if (((ModeValue) value).open) {
            float yy = y + 11;
            int i = 0;
            for (String m : ((ModeValue) value).getModes()) {
                if (isHovered(x + 5 - width2, yy, x + 20, yy + 14, mouseX, mouseY)) {
                    value.setObject(i);
                    ((ModeValue) value).open = false;
                }
                i++;
                yy += 14;
            }
        }
    }

    @Override
    public void draw(float x, float y, float partialTicks) {
        this.x = x;
        this.y = y;
        // Mode value
        HFontRenderer font = Main.INSTANCE.fontLoaders.get("heiti17");
        float width2 = 0;

        for (String mode : ((ModeValue) value).getModes()) {
            float temp = font.getStringWidth(mode);
            if (width2 < temp) width2 = temp;
        }

        RenderUtils.drawRoundRect(x + 5 - width2, y - 1, x + 20, y + 11 + value.clickgui_anim, 2, theme.option_bg.getRGB());

        font.drawCenteredString(((ModeValue) value).getCurrentMode(), x + 12 - width2 / 2, y + 1, theme.value_mode_current.getRGB());

        if (((ModeValue) value).open) {
            value.clickgui_anim = AnimationUtils.smoothAnimation(value.clickgui_anim, ((ModeValue) value).getModes().length * 14, ANIMATION_SPEED, ANIMATION_SCALE);
        } else {
            value.clickgui_anim = AnimationUtils.smoothAnimation(value.clickgui_anim, 0, ANIMATION_SPEED, ANIMATION_SCALE);
        }

        if (((ModeValue) value).open) {
            float yy = y + 14;
            for (String mode : ((ModeValue) value).getModes()) {
                if (y + 18 + value.clickgui_anim >= yy + 14) {
                    font.drawCenteredString(mode, x + 12 - width2 / 2, yy, theme.value_mode_unsel.getRGB());
                }

                yy += 14;
            }
        }
    }

}
