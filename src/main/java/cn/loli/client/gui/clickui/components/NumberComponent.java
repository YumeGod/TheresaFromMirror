package cn.loli.client.gui.clickui.components;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.ClickGui;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import dev.xix.property.AbstractTheresaProperty;
import dev.xix.property.impl.NumberProperty;
import org.lwjgl.input.Mouse;

import java.text.DecimalFormat;

import static cn.loli.client.gui.clickui.ClickGui.*;
import static cn.loli.client.gui.guiscreen.GuiReconnectIRC.isHovered;

public class NumberComponent extends Component {

    private boolean sizeDrag;
    private int mouseX;
    private float width;

    public NumberComponent(AbstractTheresaProperty v) {
        super(v);
    }

    @Override
    public void onMouse(int mouseX, int mouseY, int button) {
        System.out.println("HI");
        if (isHovered(x, y, x + width, y + 6, mouseX, mouseY)) {
            System.out.println("HI 1");
            ((NumberProperty<?>) value).clickgui_drag = true;
        }
    }

    public void setSizeDrag(Boolean v) {
        this.sizeDrag = v;
    }

    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    @Override
    public void draw(float x, float y, float partialTicks) {
        this.x = x;
        this.y = y;
        // Number value
        RenderUtils.drawRoundRect(x, y, x + width, y + 6, 3, theme.option_bg.getRGB());

        float vX = (((Number) value.getPropertyValue()).floatValue() - ((NumberProperty<?>) value).getMinimum().floatValue()) / (((NumberProperty<?>) value).getMaximum().floatValue() - ((NumberProperty<?>) value).getMinimum().floatValue());
        value.clickgui_anim = sizeDrag ? vX * (width) : AnimationUtils.smoothAnimation(value.clickgui_anim, vX * (width), ANIMATION_SPEED, ANIMATION_SCALE);
        //RenderUtils.drawRoundRect(x + width - showValueX + 5, valuesY + 13, x + width - showValueX + 10 + v.clickgui_anim, valuesY + 19, 3, theme.themeColor.getRGB());

        RenderUtils.roundedRect(x, y, value.clickgui_anim, 6, 3, theme.themeColor.getRGB(), .5f, theme.themeColor.getRGB());

        //RenderUtils.drawRect(x + width - showValueX + 5, valuesY + 13, x + width - 14, valuesY + 19, 0x88ff0000);

        DecimalFormat df = new DecimalFormat("#.##");

        String bs = df.format(value.getPropertyValue());
        Main.INSTANCE.fontLoaders.get("heiti18").drawString(bs, x + vX * (width) / 2 - (Main.INSTANCE.fontLoaders.get("heiti16").getStringWidth(bs)) / 2f, y - 1, theme.value_number_value.getRGB(), false);

        // 设置number的值
        if (((NumberProperty<?>) value).clickgui_drag && Mouse.isButtonDown(0)) {
            float v1 = (mouseX - x) / width * (((NumberProperty<?>) value).getMaximum().floatValue() - ((NumberProperty<?>) value).getMinimum().floatValue()) + ((NumberProperty<?>) value).getMinimum().floatValue();
            if (v1 <= ((NumberProperty<?>) value).getMinimum().floatValue()) {
                v1 = (((NumberProperty<?>) value).getMinimum().floatValue());
            }

            if (v1 >= ((NumberProperty<?>) value).getMaximum().floatValue()) {
                v1 = (((NumberProperty<?>) value).getMaximum().floatValue());
            }
            if (((NumberProperty<?>) value).getMaximum() instanceof Integer) {
                value.setPropertyValue((int) v1);
            } else if (((NumberProperty<?>) value).getMaximum() instanceof Float) {
                value.setPropertyValue((v1));
            } else if (((NumberProperty<?>) value).getMaximum() instanceof Double) {
                value.setPropertyValue(((double) v1));
            } else if (((NumberProperty<?>) value).getMaximum() instanceof Long) {
                value.setPropertyValue((long) v1);
            }

        } else {
            ((NumberProperty<?>) value).clickgui_drag = false;
        }

    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }
}
