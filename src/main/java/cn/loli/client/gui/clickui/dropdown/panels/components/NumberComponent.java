package cn.loli.client.gui.clickui.dropdown.panels.components;

import cn.loli.client.Main;
import cn.loli.client.module.Module;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import cn.loli.client.value.Value;
import org.lwjgl.input.Mouse;

import java.text.DecimalFormat;

import static cn.loli.client.gui.clickui.ClickGui.*;
import static cn.loli.client.value.ColorValue.isHovered;

public class NumberComponent extends Component {

    private boolean sizeDrag;
    private int mouseX;
    private float width;

    public NumberComponent(Value v) {
        super(v);
    }

    @Override
    public void onMouse(int mouseX, int mouseY, int button) {
        if (isHovered(x, y, x + width, y + 6, mouseX, mouseY)) {
            ((NumberValue<?>) value).clickgui_drag = true;
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
        RenderUtils.drawRoundRect(x-5, y, x + width, y + 6, 3, theme.option_bg.getRGB());

        float vX = (((Number) value.getObject()).floatValue() - ((NumberValue<?>) value).getMin().floatValue()) / (((NumberValue<?>) value).getMax().floatValue() - ((NumberValue<?>) value).getMin().floatValue());
        value.clickgui_anim = sizeDrag ? vX * (width) : AnimationUtils.smoothAnimation(value.clickgui_anim, vX * (width), ANIMATION_SPEED, ANIMATION_SCALE);
        //RenderUtils.drawRoundRect(x + width - showValueX + 5, valuesY + 13, x + width - showValueX + 10 + v.clickgui_anim, valuesY + 19, 3, theme.themeColor.getRGB());

        RenderUtils.roundedRect(x-5, y, value.clickgui_anim+5, 6, 3, theme.themeColor.getRGB(), .5f, theme.themeColor.getRGB());

        //RenderUtils.drawRect(x + width - showValueX + 5, valuesY + 13, x + width - 14, valuesY + 19, 0x88ff0000);

        DecimalFormat df = new DecimalFormat("#.##");

        String bs = df.format(value.getObject());
        Main.INSTANCE.fontLoaders.get("heiti18").drawString(bs, x + 20 - (Main.INSTANCE.fontLoaders.get("heiti16").getStringWidth(bs)), y, theme.value_number_value.getRGB(), false);

        // 设置number的值
        if (((NumberValue<?>) value).clickgui_drag && Mouse.isButtonDown(0)) {
            float v1 = (mouseX - x) / width * (((NumberValue<?>) value).getMax().floatValue() - ((NumberValue<?>) value).getMin().floatValue()) + ((NumberValue<?>) value).getMin().floatValue();
            if (v1 <= ((NumberValue<?>) value).getMin().floatValue()) {
                v1 = (((NumberValue<?>) value).getMin().floatValue());
            }

            if (v1 >= ((NumberValue<?>) value).getMax().floatValue()) {
                v1 = (((NumberValue<?>) value).getMax().floatValue());
            }
            if (((NumberValue<?>) value).getMax() instanceof Integer) {
                value.setObject((int) v1);
            } else if (((NumberValue<?>) value).getMax() instanceof Float) {
                value.setObject(v1);
            } else if (((NumberValue<?>) value).getMax() instanceof Double) {
                value.setObject(((double) v1));
            } else if (((NumberValue<?>) value).getMax() instanceof Long) {
                value.setObject((long) v1);
            }

        } else {
            ((NumberValue<?>) value).clickgui_drag = false;
        }

    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }
}
