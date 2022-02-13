package cn.loli.client.gui.clickui;

import cn.loli.client.Main;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.utils.AnimationUtils;
import cn.loli.client.utils.RenderUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ColorValue;
import cn.loli.client.value.NumberValue;
import cn.loli.client.value.Value;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
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

    public ClickGui(boolean doesGuiPauseGame) {
        this.doesGuiPauseGame = doesGuiPauseGame;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return this.doesGuiPauseGame;
    }

    float alpha = 50;
    AnimationUtils aa = new AnimationUtils();
    static float modswhell;
    AnimationUtils ma = new AnimationUtils();
    static float whell_temp;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        slider.update();
        alpha = aa.animate(0, alpha, 0.3f, 30);
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
        GlStateManager.translate(0, -alpha, 0);
        //绘制主窗体

        RenderUtils.drawRoundRect(x, y, x + width, y + height, 4, theme.bg.getRGB());
        RenderUtils.drawRoundRect(x, y, x + 80, y + height, 4, theme.left.getRGB());
        RenderUtils.drawRect(x + 80, y, x + 81, y + height, new Color(0, 0, 0, 30).getRGB());
        RenderUtils.drawRoundRect(x, y, x + width, y + 34, 4, theme.title.getRGB());
        RenderUtils.drawRect(x, y + 34, x + width, y + 35, new Color(0, 0, 0, 30).getRGB());
        RenderUtils.drawImage(new ResourceLocation("theresa/icons/logo.png"), (int) (x + 10), (int) (y + 10), 17, 16);

//        绘制custom按钮
//        RenderUtils.drawRoundRect(x + 10, y + height - 20, x + 70, y + height - 5, 3,theme.themeColor.getRGB());
//        RenderUtils.drawImage(new ResourceLocation("client/guis/clickgui/custom.png"), x + 18, y + height - 18, 11.5f, 11.5f, theme.left);
//        Main.fontLoaders.get("inter18").drawCenteredString(, x + 44f, y + height - 15, theme.left.getRGB());

        //绘制categories
        float my = y + 50;
        for (ModuleCategory m : ModuleCategory.values()) {
            Main.fontLoaders.get("roboto18").drawString(m.name(), x + 15, my, curType == m ? new Color(0, 0, 0).getRGB() : new Color(100, 100, 100).getRGB(), false);
            if (m.name().equals(curType.name())) { //选中type左侧滑动条
                RenderUtils.drawRect(x, y + slider.top, x + 2, y + slider.bottom, theme.themeColor.getRGB());
            }
            my += 30;
        }

        RenderUtils.drawImage(new ResourceLocation("theresa/icons/drag.png"), x + width - 20, y + height - 20, 16, 16, isHovered(x + width - 20, y + height - 20, x + width - 4, y + height - 4, mouseX, mouseY) ? theme.themeColor : theme.sec_unsel);

        //绘制功能列表
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.doGlScissor(x, y + 45 - alpha, width, height - 65 - alpha);
        float modsY = y + 50 + modswhell;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() == curType) {
                if (modsY < (y + height - 20 - alpha)) {
                    int sc1 = m.getState() ? theme.sec_sel.getRGB() : theme.sec_unsel.getRGB();
                    int sc2 = m.getState() ? theme.desc_sel.getRGB() : theme.desc_unsel.getRGB();
                    int rc1 = m.getState() ? theme.module_sel.getRGB() : theme.module_unsel.getRGB();
                    int rc2 = m.getState() ? theme.option_on.getRGB() : theme.option_off.getRGB();

                    RenderUtils.drawRoundRect(x + 90, modsY, x + width - 10, modsY + 30 + m.clickgui_animY, 3, rc1);
                    RenderUtils.drawRoundRect(x + width - 55, modsY + 10, x + width - 40, modsY + 20, 2, theme.option_bg.getRGB());
                    Main.fontLoaders.get("roboto18").drawString(m.getName(), x + 110, modsY + 15 - Main.fontLoaders.get("roboto16").getHeight() / 2f, sc1);
                    float w1 = Main.fontLoaders.get("roboto18").getStringWidth(m.getName());
                    Main.fontLoaders.get("roboto18").drawString(m.getDescription(), x + 115 + w1, modsY + 15 - 8 / 2f, sc2);
                    m.clickgui_animX = m.clickgui_animX_timer.animate(m.getState() ? 15 : 0, m.clickgui_animX, 0.2f, 30);
                    RenderUtils.drawFilledCircle(x + width - 55 + m.clickgui_animX, modsY + 15, 5, rc2, 5);

                    if (m == curModule) {
                        RenderUtils.drawRect(x + 90, modsY + 30, x + width - 10, modsY + 31, new Color(245, 245, 245).getRGB());
                        float valuesY = modsY + 40;
                        for (Value v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()))) {
                            float vw1 = Main.fontLoaders.get("roboto18").drawString(v.getName(), x + 110, valuesY, new Color(20, 20, 20).getRGB());
//                            Main.fontLoaders.get("roboto18").drawString(v.getName(), x + 115 + vw1, valuesY, new Color(120, 120, 120).getRGB());
                            if (v instanceof BooleanValue) {
                                RenderUtils.drawRoundRect(x + width - 55, valuesY, x + width - 40, valuesY + 10, 2, theme.option_bg.getRGB());
                                v.clickgui_anim = v.clickgui_timer.animate(((boolean) v.getObject()) ? 15 : 0, v.clickgui_anim, 0.2f, 30);
                                RenderUtils.drawFilledCircle(x + width - 55 + v.clickgui_anim, valuesY + 5, 5, ((boolean) v.getObject()) ? theme.option_on.getRGB() : theme.option_off.getRGB(), 5);
                            } else if (v instanceof NumberValue) {
                                RenderUtils.drawRoundRect(x + width - 110, valuesY + 1, x + width - 30, valuesY + 9, 2, theme.option_bg.getRGB());
                                float vX = (((Number) v.getObject()).floatValue() - ((NumberValue<?>) v).getMin().floatValue()) / (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue());
                                v.clickgui_anim = v.clickgui_timer.animate(vX * 70, v.clickgui_anim, 0.2f, 30);
                                RenderUtils.drawRoundRect(x + width - 110, valuesY + 1, x + width - 100 + v.clickgui_anim, valuesY + 9, 2, theme.themeColor.getRGB());
                                DecimalFormat df = new DecimalFormat("#.##");
                                Main.fontLoaders.get("roboto14").drawString(((int) (((Number) v.getObject()).doubleValue() * 10)) / 10f + "", x + width + v.clickgui_anim - 105 - Main.fontLoaders.get("roboto14").getStringWidth(df.format(((Number) v.getObject()).doubleValue())), valuesY, -1, false);
                                if (((NumberValue<?>) v).clickgui_drag && Mouse.isButtonDown(0) && valuesY > y && valuesY + 20 < y + height) {
                                    float v1 = (mouseX - (x + width - 100)) / 70 * (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue()) + ((NumberValue<?>) v).getMin().floatValue();
                                    if (Math.abs(v1 - ((Number) v.getObject()).floatValue()) >= 0.05) {
                                        v.setObject(((Number) v.getObject()).floatValue() + (v1 > ((Number) v.getObject()).floatValue() ? 0.05 : -0.05));
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

                            } else if (v instanceof ColorValue) {
                                ((ColorValue) v).draw(x + width - 90, valuesY + 1, 40, 40, mouseX, mouseY);
                                valuesY += 30;
                            }
                            valuesY += 20;
                        }
                        m.clickgui_animY = m.clickgui_animY_timer.animate(valuesY - modsY - 30, m.clickgui_animY, 0.3f, 30);
                    } else {
                        m.clickgui_animY = m.clickgui_animY_timer.animate(0, m.clickgui_animY, 0.3f, 30);
                    }
                }

                modsY += 40 + m.clickgui_animY;
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        //mods列表滑动
        float mouseDwheel = Mouse.getDWheel();
        if (mouseDwheel > 0 && whell_temp <= 0) {
            whell_temp += 16;
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

    boolean waitBound;
    Module BoundModule;

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
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
            if (isHovered(x + 0, my - 5, x + 79, my + MS18HEIGHT + 5, mouseX, mouseY)) {
                slider.change(my - 4 - y, my + MS18HEIGHT + 4 - y);
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
//                if (isHovered(x + 90, Math.max(modsY, y + 35), x + width - 10, Math.min(modsY + 30, y + height), mouseX, mouseY) && mouseButton == 2) {
//                    NotificationManager.sendMessage(NotificationType.MESSAGE, "Please type key you want to bind");
//                    waitBound = true;
//                    BoundModule = m.getValue();
//                }

                if (isHovered(x + 90, Math.max(modsY, y + 35), x + width - 10, Math.min(modsY + 30, y + height - 17.5f), mouseX, mouseY) && mouseButton == 0) {
                    m.setState(!m.getState());
                    if (mc.theWorld != null) {
                        mc.thePlayer.playSound("random.click", 1, 1);
                    }
                }
                if (isHovered(x + 90, Math.max(modsY, y + 35), x + width - 10, Math.min(modsY + 30, y + height), mouseX, mouseY) && mouseButton == 1) {
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
