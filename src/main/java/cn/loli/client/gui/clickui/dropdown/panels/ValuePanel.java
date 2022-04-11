package cn.loli.client.gui.clickui.dropdown.panels;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.GuiTextBox;
import cn.loli.client.gui.clickui.dropdown.ClickUI;
import cn.loli.client.gui.clickui.dropdown.Panel;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.module.Module;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.text.DecimalFormat;

import static cn.loli.client.value.ColorValue.isHovered;

public class ValuePanel extends Panel {
    public Module module;

    Theme theme = new Theme();

    private static final float ANIMATION_SCALE = .3f; //动画缩放
    private static final float ANIMATION_SPEED = 50f; //动画速度

    public ValuePanel(Module module) {
        super(module.getName());
        this.module = module;
        this.update(10, 140, 110, 200);
    }

    @Override
    public void display(int mouseX, int mouseY, float partialTicks, int mouseDWheel) {
        super.display(mouseX, mouseY, partialTicks, mouseDWheel);
        double valuesY = this.y + this.TITLE_HEIGHT + 10 + scroll;
        for (Value v : Main.INSTANCE.valueManager.getAllValuesFrom(module.getName())) {
            Main.INSTANCE.fontLoaders.get("heiti18").drawString(v.getName(), (float) (x + 5), (float) (valuesY + 1), theme.value_name.getRGB());
            if (v instanceof BooleanValue) {
                // Boolean value
                RenderUtils.drawRoundRect(x + width - 21, valuesY, x + width - 4, valuesY + 10, 5, theme.option_bg.getRGB());
                v.clickgui_anim = AnimationUtils.smoothAnimation(v.clickgui_anim, ((boolean) v.getObject()) ? 11 : 0, ANIMATION_SPEED, ANIMATION_SCALE);
                RenderUtils.drawFilledCircle(x + width - 21 + v.clickgui_anim, valuesY + 5, 5, ((boolean) v.getObject()) ? theme.option_on.getRGB() : theme.option_off.getRGB(), 5);

                //RenderUtils.drawRect(x + width - 35, valuesY, x + width - 14, valuesY + 10, 0x88ff0000);
            } else if (v instanceof NumberValue) {
                // Number value
                RenderUtils.drawRoundRect(x + width - 80, valuesY + 13, x + width - 5, valuesY + 19, 3, theme.option_bg.getRGB());

                float vX = (((Number) v.getObject()).floatValue() - ((NumberValue<?>) v).getMin().floatValue()) / (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue());

                v.clickgui_anim = AnimationUtils.smoothAnimation(v.clickgui_anim, (float) (vX * ((x + width - 5) - (x + width - 80))), ANIMATION_SPEED, ANIMATION_SCALE);
                //RenderUtils.drawRoundRect(x + width - showValueX + 5, valuesY + 13, x + width - showValueX + 10 + v.clickgui_anim, valuesY + 19, 3, theme.themeColor.getRGB());

                RenderUtils.roundedRect((float) (x + width - 80f), (float) (valuesY + 13), v.clickgui_anim + 5, 6, 3, theme.themeColor.getRGB(), .5f, theme.themeColor.getRGB());

                //RenderUtils.drawRect(x + width - showValueX + 5, valuesY + 13, x + width - 14, valuesY + 19, 0x88ff0000);

                DecimalFormat df = new DecimalFormat("#.##");

                String bs = df.format(v.getObject());
                Main.INSTANCE.fontLoaders.get("heiti18").drawString(bs, (float) (x + width - 15 - (Main.INSTANCE.fontLoaders.get("heiti16").getStringWidth(bs)) - 1), (float) (valuesY + 1), theme.value_number_value.getRGB(), false);

                // 设置number的值
                if (((NumberValue<?>) v).clickgui_drag && Mouse.isButtonDown(0) && valuesY > y && valuesY + 20 < y + height) {
                    float v1 = (float) ((mouseX - (x + width - 5)) / ((x + width - 80) - (x + width - 5)) * (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue()) + ((NumberValue<?>) v).getMin().floatValue());
                    if (v1 <= ((NumberValue<?>) v).getMin().floatValue()) {
                        v1 = (((NumberValue<?>) v).getMin().floatValue());
                    }

                    if (v1 >= ((NumberValue<?>) v).getMax().floatValue()) {
                        v1 = (((NumberValue<?>) v).getMax().floatValue());
                    }
                    if (((NumberValue<?>) v).getMax() instanceof Integer) {
                        v.setObject((int) v1);
                    } else if (((NumberValue<?>) v).getMax() instanceof Float) {
                        v.setObject(v1);
                    } else if (((NumberValue<?>) v).getMax() instanceof Double) {
                        v.setObject(((double) v1));
                    } else if (((NumberValue<?>) v).getMax() instanceof Long) {
                        v.setObject((long) v1);
                    }

                } else {
                    ((NumberValue<?>) v).clickgui_drag = false;
                }

                valuesY += 8;
            } else if (v instanceof ModeValue) {
                // Mode value
                HFontRenderer font = Main.INSTANCE.fontLoaders.get("heiti17");
                float width2 = 0;

                for (String mode : ((ModeValue) v).getModes()) {
                    float temp = font.getStringWidth(mode);
                    if (width2 < temp) width2 = temp;
                }

                RenderUtils.drawRoundRect(x + width - 30 - width2, valuesY - 1, x + width - 15, valuesY + 11 + v.clickgui_anim, 2, theme.option_bg.getRGB());

                font.drawCenteredString(((ModeValue) v).getCurrentMode(), (float) (x + width - 23 - width2 / 2), (float) (valuesY + 1), theme.value_mode_current.getRGB());

                if (((ModeValue) v).open) {
                    v.clickgui_anim = AnimationUtils.smoothAnimation(v.clickgui_anim, ((ModeValue) v).getModes().length * 14, ANIMATION_SPEED, ANIMATION_SCALE);
                } else {
                    v.clickgui_anim = AnimationUtils.smoothAnimation(v.clickgui_anim, 0, ANIMATION_SPEED, ANIMATION_SCALE);
                }

                if (((ModeValue) v).open) {
                    float yy = (float) (valuesY + 14);
                    for (String mode : ((ModeValue) v).getModes()) {
                        if (valuesY + 18 + v.clickgui_anim >= yy + 14) {
                            font.drawCenteredString(mode, (float) (x + width - 23 - width2 / 2), yy, theme.value_mode_unsel.getRGB());
                        }

                        yy += 14;
                    }
                }
                valuesY += v.clickgui_anim;
            } else if (v instanceof StringValue) {
                if (((StringValue) v).text == null) {
                    ((StringValue) v).text = new GuiTextBox(0, Main.INSTANCE.fontLoaders.get("heiti17"), 0, 0, 0, 0);
                    ((StringValue) v).text.setText(((StringValue) v).getObject());
                } else {
                    ((StringValue) v).text.xPosition = (int) (x + width - 80);
                    ((StringValue) v).text.yPosition = (int) valuesY - 2;

                    ((StringValue) v).text.height = 14;
                    ((StringValue) v).text.width = 60;
                    RenderUtils.drawRoundedRect(((StringValue) v).text.xPosition - 1, ((StringValue) v).text.yPosition - 1, ((StringValue) v).text.width + 2, ((StringValue) v).text.height + 2, 2, new Color(200, 200, 200).getRGB());
                    ((StringValue) v).text.drawTextBox();
                    v.setObject(((StringValue) v).text.getText());
                }
            } else if (v instanceof ColorValue) {
                // Color
                if (isHovered(x, y, x + width, y + height - 20, mouseX, mouseY)) {
                    ((ColorValue) v).draw((float) (x + width - 70), (float) (valuesY + 1), 40, 40, mouseX, mouseY);
                } else {
                    ((ColorValue) v).draw((float) (x + width - 70), (float) (valuesY + 1), 40, 40, -1, -1);
                }

                valuesY += 100;
            }
            valuesY += 20;
            RenderUtils.drawRect(x + width - 40, valuesY - 5, x + width - 10, valuesY - 5 + 0.5f, theme.value_line.getRGB());
        }
        maxHeight = valuesY - this.y - this.TITLE_HEIGHT - 10;
    }

    @Override
    public void onClicked(double mx, double my, int mouseButton) {

        if (!isHovered(x, x + width, y, y + height - 10, mx, my)) {
            return; // 如果不在范围内，则不处理
        }

        // 处理鼠标点击事件
        double my1 = this.y + this.TITLE_HEIGHT + 10 + scroll;
        for (Value value : Main.INSTANCE.valueManager.getAllValuesFrom(module.getName())) {

        }
    }
}
