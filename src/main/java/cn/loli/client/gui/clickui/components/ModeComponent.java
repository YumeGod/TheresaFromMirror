package cn.loli.client.gui.clickui.components;

import cn.loli.client.Main;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import dev.xix.property.AbstractTheresaProperty;
import dev.xix.property.impl.EnumProperty;

import static cn.loli.client.gui.clickui.ClickGui.*;
import static cn.loli.client.gui.clickui.ClickGui.theme;
import static cn.loli.client.gui.guiscreen.GuiReconnectIRC.isHovered;

public class ModeComponent extends Component {

    public ModeComponent(AbstractTheresaProperty v) {
        super(v);
    }

    @Override
    public void onMouse(int mouseX, int mouseY, int button) {
        HFontRenderer font = Main.INSTANCE.fontLoaders.get("heiti16");
        float width2 = 0;
        for (Enum mode : ((EnumProperty) value).getEnumConstants()) {
            float temp = font.getStringWidth(mode.name());
            if (width2 < temp) width2 = temp;
        }

        if (isHovered(x + 5 - width2, y - 1, x + 20, y + 11, mouseX, mouseY)) {
            ((EnumProperty) value).open = !((EnumProperty) value).open;
        }

        if (((EnumProperty) value).open) {
            float yy = y + 11;
            for (Enum m : ((EnumProperty) value).getEnumConstants()) {
                if (isHovered(x + 5 - width2, yy, x + 20, yy + 14, mouseX, mouseY)) {
                    value.setPropertyValue(m);
                    ((EnumProperty) value).open = false;
                }
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

        for (Enum mode : ((EnumProperty) value).getEnumConstants()) {
            float temp = font.getStringWidth(mode.name());
            if (width2 < temp) width2 = temp;
        }

        RenderUtils.drawRoundRect(x + 5 - width2, y - 1, x + 20, y + 11 + value.clickgui_anim, 2, theme.option_bg.getRGB());

        font.drawCenteredString(((Enum) ((EnumProperty) value).getPropertyValue()).name(), x + 12 - width2 / 2, y + 1, theme.value_mode_current.getRGB());

        if (((EnumProperty) value).open) {
            value.clickgui_anim = AnimationUtils.smoothAnimation(value.clickgui_anim, ((EnumProperty) value).getEnumConstants().length * 14, ANIMATION_SPEED, ANIMATION_SCALE);
        } else {
            value.clickgui_anim = AnimationUtils.smoothAnimation(value.clickgui_anim, 0, ANIMATION_SPEED, ANIMATION_SCALE);
        }

        if (((EnumProperty) value).open) {
            float yy = y + 14;
            for (Enum mode : ((EnumProperty) value).getEnumConstants()) {
                if (y + 18 + value.clickgui_anim >= yy + 14) {
                    font.drawCenteredString(mode.name(), x + 12 - width2 / 2, yy, theme.value_mode_unsel.getRGB());
                }

                yy += 14;
            }
        }
    }

}
