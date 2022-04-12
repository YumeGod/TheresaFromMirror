package cn.loli.client.gui.clickui.dropdown.panels;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.GuiTextBox;
import cn.loli.client.gui.clickui.dropdown.ClickUI;
import cn.loli.client.gui.clickui.dropdown.Panel;
import cn.loli.client.gui.clickui.dropdown.panels.components.BooleanComponent;
import cn.loli.client.gui.clickui.dropdown.panels.components.ModeComponent;
import cn.loli.client.gui.clickui.dropdown.panels.components.NumberComponent;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.module.Module;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Objects;

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
                BooleanComponent bc = (BooleanComponent) v.component;
                bc.draw((float) (x + width - 25), (float) valuesY, partialTicks);
                //RenderUtils.drawRect(x + width - 35, valuesY, x + width - 14, valuesY + 10, 0x88ff0000);
            } else if (v instanceof NumberValue) {
                NumberComponent bc = (NumberComponent) v.component;
                bc.setMouseX(mouseX);
                bc.setWidth((float) (width - 15));
                bc.draw((float) (x + 10), (float) valuesY + 14, partialTicks);
                valuesY += 10;
            } else if (v instanceof ModeValue) {
                ModeComponent mc = (ModeComponent) v.component;
                mc.draw((float) (x + width - 25), (float) valuesY + 14, partialTicks);
                valuesY += v.clickgui_anim;
                valuesY += 10;
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
                if (isHovered(x + width - 70, x + width - 30, valuesY + 10, valuesY + 41, mouseX, mouseY)) {
                    ((ColorValue) v).draw((float) (x + width - 70), (float) (valuesY + 10), 40, 40, mouseX, mouseY);
                } else {
                    ((ColorValue) v).draw((float) (x + width - 70), (float) (valuesY + 10), 40, 40, -1, -1);
                }

                valuesY += 50;
            }
            valuesY += 20;
            RenderUtils.drawRect(x + width - 40, valuesY - 5, x + width - 10, valuesY - 5 + 0.5f, theme.value_line.getRGB());
        }
        maxHeight = 200;
    }

    @Override
    public void onClicked(double mx, double my, int mouseButton) {
        if (!isHovered(x, x + width, y, y + height - 10, mx, my)) {
            return; // 如果不在范围内，则不处理
        }
        // 处理鼠标点击事件
        double my1 = this.y + this.TITLE_HEIGHT + 10 + scroll;
        for (Value v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(module.getName()))) {
            if (v instanceof BooleanValue) {
                // Boolean value
                v.component.onMouse((int) mx, (int) my, mouseButton);
            } else if (v instanceof NumberValue) {
                NumberComponent nc = (NumberComponent) v.component;
                nc.onMouse((int) mx, (int) my, mouseButton);
                my1 += 10;
            } else if (v instanceof ModeValue) {
                ModeComponent mc = (ModeComponent) v.component;
                mc.onMouse((int) mx, (int) my, mouseButton);
                my1 += v.clickgui_anim;
                my1 += 10;
            } else if (v instanceof StringValue) {
                if (((StringValue) v).text != null) {
                    ((StringValue) v).text.mouseClicked((int) mx, (int) my, mouseButton);
                }
            } else if (v instanceof ColorValue) {
                my1 += 50;
            }
            my1 += 20;
        }
    }
}
