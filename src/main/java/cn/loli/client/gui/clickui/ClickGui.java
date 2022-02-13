package cn.loli.client.gui.clickui;

import akka.io.SelectionHandlerSettings;
import cn.loli.client.Main;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.AnimationUtils;
import cn.loli.client.utils.RenderUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ColorValue;
import cn.loli.client.value.NumberValue;
import cn.loli.client.value.Value;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;

import static cn.loli.client.value.ColorValue.isHovered;

public class ClickGui extends GuiScreen {
    private static final float MS18HEIGHT = 12;
    static float x = -1, y = -1, width = 0, height = 0;
    Theme theme;
    private boolean drag;
    private float dragX, dragY;
    ScaledResolution sr;
    static ModuleCategory curType = ModuleCategory.RENDER;
    static Slider slider = new Slider();
    private static Module curModule;
    private final boolean doesGuiPauseGame;
    private static float leftMenuWidth = 0;
    private float showValueX;//右侧value的宽度
    private final AnimationUtils valueXtimer = new AnimationUtils();//右侧value的timer

    public ClickGui(boolean doesGuiPauseGame) {
        this.doesGuiPauseGame = doesGuiPauseGame;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return this.doesGuiPauseGame;
    }

    static float modswhell;
    AnimationUtils ma = new AnimationUtils();
    static float whell_temp;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        width = 600;
        super.drawScreen(mouseX, mouseY, partialTicks);
        leftMenuWidth = width * 0.25f;
        slider.update();
        if (!Mouse.isButtonDown(0)) {
            drag = false;
            sizeDrag = false;
        }
        if (drag) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        float w = mouseX + sizeDragX - x;
        float h = mouseY + sizeDragY - y;
        if (sizeDrag && ((width > 400 && height > 200) || (w > width && h > height))) {
            width = w;
            height = h;
        }
        //绘制主窗体
        RenderUtils.drawRoundRect(x, y, x + width, y + height, 2, theme.bg.getRGB());
        RenderUtils.drawRoundRect(x, y, x + leftMenuWidth, y + height, 2, theme.left.getRGB());
        RenderUtils.drawImage(new ResourceLocation("theresa/icons/logo.png"), (int) (x + 20), (int) (y + 10), 17, 16);
        Main.fontLoaders.get("roboto24").drawString((Main.CLIENT_NAME.toCharArray()[0] + "").toUpperCase() + Main.CLIENT_NAME.substring(1), (int) (x + 40), (int) (y + 15), new Color(20, 20, 20).getRGB());
        RenderUtils.drawRect((int) (x + 50), (int) (y + 26), x + 56, y + 27, theme.themeColor.getRGB());
        Main.fontLoaders.get("roboto16").drawString(Main.CLIENT_VERSION, (int) (x + 80), (int) (y + 25), new Color(80, 80, 80).getRGB());

        //绘制categories
        float my = y + 50;
        for (ModuleCategory m : ModuleCategory.values()) {
            if (m.name().equals(curType.name())) { //选中type左侧滑动条
                RenderUtils.drawRect(x, y + slider.top - 5, x + leftMenuWidth, y + slider.top + 25, theme.slider.getRGB());
            }
            Main.fontLoaders.get("roboto20").drawString(m.name(), x + 25, my, curType == m ? new Color(0, 0, 0).getRGB() : new Color(100, 100, 100).getRGB(), false);
            RenderUtils.drawImage(new ResourceLocation("theresa/icons/" + m.name().toLowerCase() + ".png"), x + 10, my - 2, 8, 8, theme.left);
            my += 30;
        }

        RenderUtils.drawImage(new ResourceLocation("theresa/icons/drag.png"), x + width - 20, y + height - 20, 16, 16, isHovered(x + width - 20, y + height - 20, x + width - 4, y + height - 4, mouseX, mouseY) ? theme.themeColor : theme.sec_unsel);

        //绘制功能列表
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.doGlScissor(x, y + 45, width, height - 65);
        RenderUtils.drawRoundRect(x + leftMenuWidth + 10, y + 45, x + width - 10 - showValueX, y + height - 20, 3, new Color(255, 255, 255).getRGB());

        float modsY = y + 50 + modswhell;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() == curType) {
                if (modsY < (y + height - 20)) {
                    int sc1 = m.getState() ? theme.sec_sel.getRGB() : theme.sec_unsel.getRGB();
                    int sc2 = m.getState() ? theme.desc_sel.getRGB() : theme.desc_unsel.getRGB();
//                    int rc1 = m.getState() ? theme.module_sel.getRGB() : theme.module_unsel.getRGB();
                    int rc2 = m.getState() ? theme.option_on.getRGB() : theme.option_off.getRGB();
                    RenderUtils.drawRoundRect(x + width - 55 - showValueX, modsY + 10, x + width - 40 - showValueX, modsY + 20, 3, theme.option_bg.getRGB());
                    Main.fontLoaders.get("roboto18").drawString(m.getName(), x + leftMenuWidth + 20, modsY + 15 - Main.fontLoaders.get("roboto15").getHeight() / 2f, sc1);
                    float w1 = Main.fontLoaders.get("roboto18").getStringWidth(m.getName());
                    Main.fontLoaders.get("roboto18").drawString(Main.fontLoaders.get("roboto18").trimStringToWidth(m.getDescription(), (int) (width - showValueX - leftMenuWidth - 100 - Main.fontLoaders.get("roboto18").getStringWidth(m.getName()))) + "..", x + leftMenuWidth + 25 + w1, modsY + 15 - 8 / 2f, sc2);
                    m.clickgui_animX = m.clickgui_animX_timer.animate(m.getState() ? 15 : 0, m.clickgui_animX, 0.2f, 30);
                    RenderUtils.drawFilledCircle(x + width - 55 + m.clickgui_animX - showValueX, modsY + 15, 5, rc2, 5);
                    RenderUtils.drawRect(x + leftMenuWidth + 10, modsY + 30, x + width - 10 - showValueX, modsY + 31, new Color(245, 245, 245).getRGB());
                    if (curModule == null) {
                        showValueX = valueXtimer.animate(0, showValueX, 0.2f, 30);
                        m.clickgui_animY = m.clickgui_animY_timer.animate(0, m.clickgui_animY, 0.3f, 30);
                    } else if (m == curModule) {
                        RenderUtils.drawRoundRect(x + width - showValueX, y + 45, x + width - 10, y + height - 20, 3, -1);
                        showValueX = valueXtimer.animate(width / 3, showValueX, 0.2f, 30);
                        float valuesY = y + 60;
                        for (Value v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()))) {
                            Main.fontLoaders.get("roboto18").drawString(v.getName(), x + width - showValueX + 5, valuesY, new Color(158, 158, 158).getRGB());
//                            Main.fontLoaders.get("roboto18").drawString(v.getName(), x + 115 + vw1, valuesY, new Color(120, 120, 120).getRGB());
                            if (v instanceof BooleanValue) {
                                RenderUtils.drawRoundRect(x + width - 45, valuesY, x + width - 30, valuesY + 10, 2, theme.option_bg.getRGB());
                                v.clickgui_anim = v.clickgui_timer.animate(((boolean) v.getObject()) ? 15 : 0, v.clickgui_anim, 0.2f, 30);
                                RenderUtils.drawFilledCircle(x + width - 45 + v.clickgui_anim, valuesY + 5, 5, ((boolean) v.getObject()) ? theme.option_on.getRGB() : theme.option_off.getRGB(), 5);
                            } else if (v instanceof NumberValue) {
                                RenderUtils.drawRoundRect(x + width - showValueX + 10, valuesY + 14, x + width - 30, valuesY + 15, 2, theme.option_bg.getRGB());
                                float vX = (((Number) v.getObject()).floatValue() - ((NumberValue<?>) v).getMin().floatValue()) / (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue());
                                v.clickgui_anim = v.clickgui_timer.animate(vX * (showValueX - 40), v.clickgui_anim, 0.2f, 30);
                                RenderUtils.drawRoundRect(x + width - showValueX + 10, valuesY + 14, x + width - showValueX + 10 + v.clickgui_anim, valuesY + 15, 2, theme.themeColor.getRGB());
                                DecimalFormat df = new DecimalFormat("#.##");
                                Main.fontLoaders.get("roboto15").drawString(((int) (((Number) v.getObject()).doubleValue() * 10)) / 10f + "", x + width - 30 - (showValueX - 10) / 2, valuesY + 11, new Color(0, 0, 0).getRGB(), false);
                                if (((NumberValue<?>) v).clickgui_drag && Mouse.isButtonDown(0) && valuesY > y && valuesY + 20 < y + height) {
                                    float v1 = (mouseX - (x + width - showValueX + 10)) / (showValueX - 40) * (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue()) + ((NumberValue<?>) v).getMin().floatValue();
                                    if (Math.abs(v1 - ((Number) v.getObject()).floatValue()) >= ((Number) v.getObject()).floatValue() / 20) {
                                        v.setObject(((Number) v.getObject()).floatValue() + (v1 > ((Number) v.getObject()).floatValue() ? ((Number) v.getObject()).floatValue() / 20 : -((Number) v.getObject()).floatValue() / 20));
                                    }
                                    if (v1 <= ((NumberValue<?>) v).getMin().floatValue()) {
                                        v.setObject(((NumberValue<?>) v).getMin().floatValue());
                                    }

                                    if (v1 >= ((NumberValue<?>) v).getMax().floatValue()) {
                                        v.setObject(((NumberValue<?>) v).getMax().floatValue());
                                    }
                                } else {
                                    ((NumberValue<?>) v).clickgui_drag = false;
                                }
                                valuesY += 10;
                            } else if (v instanceof ColorValue) {
                                ((ColorValue) v).draw(x + width - 70, valuesY + 1, 40, 40, mouseX, mouseY);
                                valuesY += 30;
                            }
                            valuesY += 20;
                        }
                        m.clickgui_animY = m.clickgui_animY_timer.animate(valuesY - modsY - 30, m.clickgui_animY, 0.3f, 30);
                    }
                }

                modsY += 40;
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        //mods列表滑动
        float mouseDwheel = Mouse.getDWheel();
        if (mouseDwheel > 0 && whell_temp <= 0) {
            whell_temp += 16;
            if (whell_temp > 0) whell_temp = 0;
        } else if (mouseDwheel < 0 && modsY > y + height) {
            whell_temp -= 16;
        }
        modswhell = ma.animate(whell_temp, modswhell, 0.2f, 30);


    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (width < new ScaledResolution(mc).getScaledWidth()) {
            if (x > new ScaledResolution(mc).getScaledWidth() - width) {
                x = new ScaledResolution(mc).getScaledWidth() - width;
            }
            if (x < 0) {
                x = 0;
            }
        } else {
            x = 0;
        }
        if (height < new ScaledResolution(mc).getScaledHeight()) {
            if (y > new ScaledResolution(mc).getScaledHeight() - height) {
                y = new ScaledResolution(mc).getScaledHeight() - height;
            }
            if (y < 0) {
                y = 0;
            }
        } else {
            y = 0;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        sr = new ScaledResolution(mc);
        theme = new Theme();
        if (width == 0 || height == 0) {
            width = 500;
            height = 300;
        }
        if (x == -1 || y == -1) {
            x = (sr.getScaledWidth() - width) / 2;
            y = (sr.getScaledHeight() - height) / 2;
        }
        if (slider.top == 0) {
            slider.change(y + 50 - 4 - y, y + 50 + MS18HEIGHT + 4 - y);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    boolean sizeDrag = false;
    float sizeDragX;
    float sizeDragY;

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && isHovered(x, y, x + width, y + 34, mouseX, mouseY)) {
            drag = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }
        if (mouseButton == 0 && isHovered(x + width - 20, y + height - 20, x + width - 4, y + height - 4, mouseX, mouseY)) {
            sizeDrag = true;
            sizeDragX = (x + width) - mouseX;
            sizeDragY = (y + height) - mouseY;
        }


        float my = y + 50;
        for (ModuleCategory m : ModuleCategory.values()) {
            if (isHovered(x + 0, my - 5, x + leftMenuWidth, my + MS18HEIGHT + 5, mouseX, mouseY)) {
                slider.change(my - 8 - y, my + MS18HEIGHT + 6 - y);
                whell_temp = 0;
                modswhell = 0;
                curType = m;
            }
            my += 30;
        }

        //功能列表

        float modsY = y + 50 + modswhell;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() == curType) {

                if (isHovered(x + leftMenuWidth + 10, Math.max(modsY, y + 35), x + width - 10, Math.min(modsY + 30, y + height - 17.5f), mouseX, mouseY) && mouseButton == 0) {
                    m.setState(!m.getState());
                    if (mc.theWorld != null) {
                        mc.thePlayer.playSound("random.click", 1, 1);
                    }
                }
                if (isHovered(x + leftMenuWidth + 10, Math.max(modsY, y + 35), x + width - 10, Math.min(modsY + 30, y + height), mouseX, mouseY) && mouseButton == 1) {
                    //打开功能values列表
                    if (curModule != m && Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName())).size() > 0) {
                        curModule = m;
                    } else {
                        curModule = null;
                    }
                }
                if (m == curModule) {
                    float valuesHeight = Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName())).size() * 20 + 10;
                    float valuesY = modsY + 40;
                    for (Value<?> v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()))) {
                        if (v instanceof BooleanValue) {
                            if (isHovered(x + width - 55, valuesY, x + width - 40, valuesY + 10, mouseX, mouseY) && mouseButton == 0) {
                                ((BooleanValue) v).setObject(!((BooleanValue) v).getObject());
                            }
                        } else if (v instanceof NumberValue) {
                            if (isHovered(x + width - 100, valuesY, x + width - 30, valuesY + 10, mouseX, mouseY) && mouseButton == 0) {
                                ((NumberValue<?>) v).clickgui_drag = true;
                            }
                        } else if (v instanceof ColorValue) {
                            valuesY += 30;
                        }

                        valuesY += 20;
                    }

                    m.clickgui_animY = m.clickgui_animY_timer.animate(valuesHeight, m.clickgui_animY, 0.3f, 30);
                } else {
                    m.clickgui_animY = m.clickgui_animY_timer.animate(0, m.clickgui_animY, 0.3f, 30);
                }
                modsY += 40 + m.clickgui_animY;
            }
        }


    }
}
