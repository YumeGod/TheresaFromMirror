package cn.loli.client.gui.clickui;

import cn.loli.client.Main;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.AnimationUtils;
import cn.loli.client.utils.RenderUtils;
import cn.loli.client.value.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;

import static cn.loli.client.value.ColorValue.isHovered;

public class ClickGui extends GuiScreen {
    //常量
    private static final float MS18HEIGHT = 12;//字体18号的高度
    private static final int ANIMATION_DELAY = 20;//动画的延迟
    private static final float ANIMATION_SPEED = 0.2f;//动画的速度
    private static final float LEFTMENU_MIN_WIDTH = 120;//左侧类别列表的最小宽度
    private static final float LEFTMENU_MAX_WIDTH = 150;//左侧类别列表的最大宽度
    private static final float WINDOW_MAX_WIDTH = 400;//窗口最大宽度
    private static final float WINDOW_MAX_HEIGHT = 250;//窗口最小宽度


    static float x = -1, y = -1, width = 0, height = 0;//坐标、宽高
    Theme theme;//主题
    private boolean drag;//主窗体是否被拖动
    private float dragX, dragY;//拖动的位置
    ScaledResolution sr;
    static ModuleCategory curType = ModuleCategory.COMBAT;//选中的类别
    static Slider slider = new Slider();//功能分类滑块
    private static Module curModule;//选中的功能
    private final boolean doesGuiPauseGame;//是否暂停游戏
    private static float leftMenuWidth = 0;//左侧类别栏的宽度
    private float showValueX;//右侧value的宽度
    private final AnimationUtils valueXtimer = new AnimationUtils();//右侧value的缓动

    private final AnimationUtils list_anim_timer = new AnimationUtils();//功能列表的缓动
    private final AnimationUtils gui_anim_timer = new AnimationUtils();//主界面的缓动
    private static float gui_anim, list_anim;

    //搜索框
    private static String search_context;
    private boolean search_hover;


    public ClickGui(boolean doesGuiPauseGame) {
        this.doesGuiPauseGame = doesGuiPauseGame;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return this.doesGuiPauseGame;
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();

    }

    static float mods_whell; //用于滚动Module列表
    static float mods_whelltemp; //用于缓动
    AnimationUtils modsScrollAnimationUtils = new AnimationUtils();

    static float values_whell; //用于滚动Module列表
    static float values_whelltemp; //用于缓动
    AnimationUtils valuesScrollAnimationUtils = new AnimationUtils();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        //缓动
        if (gui_anim != 2) {
            gui_anim = gui_anim_timer.animate(0, gui_anim, ANIMATION_SPEED, ANIMATION_DELAY);
        }
        GlStateManager.translate(gui_anim, 0, 0);
        //取左侧列表宽度
        leftMenuWidth = Math.max(Math.min(width * 0.25f, LEFTMENU_MAX_WIDTH), LEFTMENU_MIN_WIDTH);
        //更新滑块
        slider.update();
        //取消拖动
        if (!Mouse.isButtonDown(0)) {
            drag = false;
            sizeDrag = false;
        }
        //拖动窗口
        if (drag) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        //移动
        float w = mouseX + sizeDragX - x;
        float h = mouseY + sizeDragY - y;
        //设置窗口大小
        if (sizeDrag && ((width > WINDOW_MAX_WIDTH && height > WINDOW_MAX_HEIGHT) || (w > width && h > height))) {
            width = w;
            height = h;
        }
        //绘制主窗体
        RenderUtils.drawRoundRect(x, y, x + width, y + height, 2, theme.bg.getRGB());//背景
        RenderUtils.drawRoundRect(x, y, x + leftMenuWidth, y + height, 2, theme.left.getRGB());//左侧列表
        RenderUtils.drawImage(new ResourceLocation("theresa/icons/logo.png"), (int) (x + leftMenuWidth / 2 - 30), (int) (y + 15), 17, 16);//logo
        Main.fontLoaders.get("roboto24").drawString((Main.CLIENT_NAME.toCharArray()[0] + "").toUpperCase() + Main.CLIENT_NAME.substring(1), (int) (x + leftMenuWidth / 2 - 10), (int) (y + 20), theme.clientname.getRGB());//Client Name
        RenderUtils.drawRect((int) (x + leftMenuWidth / 2 - 3), (int) (y + 30), x + leftMenuWidth / 2 + 5, y + 31, theme.themeColor.getRGB());//客户端名字下方的矩形
        Main.fontLoaders.get("roboto16").drawString(Main.CLIENT_VERSION, (int) (x + leftMenuWidth / 2 + 5), (int) (y + 30), theme.client_version.getRGB());//版本

        //绘制categories
        float my = y + 60;
        RenderUtils.drawRect(x, y + slider.top - 5, x + leftMenuWidth, y + slider.top + 25, theme.slider.getRGB());//滑块条
        for (ModuleCategory m : ModuleCategory.values()) {
            Main.fontLoaders.get("roboto20").drawString(m.name(), x + leftMenuWidth / 2 - 20, my, curType == m ? theme.cate_sel.getRGB() : theme.cate_unsel.getRGB());
            RenderUtils.drawImage(new ResourceLocation("theresa/icons/" + m.name().toLowerCase() + ".png"), x + leftMenuWidth / 2 - 35, my - 2, 8, 8);
            my += 30;
        }

        //右下角拖动的图标
        RenderUtils.drawImage(new ResourceLocation("theresa/icons/drag.png"), x + width - 20, y + height - 20, 16, 16, isHovered(x + width - 20, y + height - 20, x + width - 4, y + height - 4, mouseX, mouseY) ? theme.themeColor : drag ? theme.themeColor : theme.sec_unsel);

        //绘制功能列表并裁剪
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.doGlScissor(x, y + 45, width, height - 65);
        RenderUtils.drawRoundRect(x + leftMenuWidth + 10, y + 45, x + width - 10 - showValueX, y + height - 20, 3, theme.module_list_bg.getRGB());

        if (list_anim != 0) {
            list_anim = list_anim_timer.animate(0, list_anim, ANIMATION_SPEED / 2, ANIMATION_DELAY);
        }
        float modsY = y + 50 + mods_whell;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() == curType) {
                //动画
                GlStateManager.translate(list_anim, 0, 0);

                if (modsY < (y + height - 20)) {
                    //获取一些颜色
                    int sc1 = m.getState() ? theme.sec_sel.getRGB() : theme.sec_unsel.getRGB();
                    int sc2 = m.getState() ? theme.desc_sel.getRGB() : theme.desc_unsel.getRGB();
                    //绘制功能开关
                    if (m.getState()) {
                        RenderUtils.drawImage(new ResourceLocation("theresa/icons/enabled.png"), x + leftMenuWidth + 20, modsY + 10, 8, 8, new Color(255, 255, 255, ((int) m.clickgui_animX)));
                    } else {
                        RenderUtils.drawImage(new ResourceLocation("theresa/icons/disabled.png"), x + leftMenuWidth + 20, modsY + 10, 8, 8, new Color(255, 255, 255, ((int) (255 - m.clickgui_animX))));
                    }
                    //绘制功能名和描述
                    Main.fontLoaders.get("roboto18").drawString(m.getName(), x + leftMenuWidth + 35, modsY + 15 - Main.fontLoaders.get("roboto15").getHeight() / 2f, sc1);
                    float w1 = Main.fontLoaders.get("roboto18").getStringWidth(m.getName());
                    Main.fontLoaders.get("roboto18").drawString(Main.fontLoaders.get("roboto18").trimStringToWidth(m.getDescription(), (int) (width - showValueX - leftMenuWidth - 85 - Main.fontLoaders.get("roboto18").getStringWidth(m.getName()))) + "..", x + leftMenuWidth + 40 + w1, modsY + 15 - 8 / 2f, sc2);

                    m.clickgui_animX = m.clickgui_animX_timer.animate(m.getState() ? 255 : 0, m.clickgui_animX, ANIMATION_SPEED / 2, ANIMATION_DELAY);
                    RenderUtils.drawRect(x + leftMenuWidth + 10, modsY + 30, x + width - 10 - showValueX, modsY + 31, theme.module_list_line.getRGB());
                    if (curModule == null) {
                        showValueX = valueXtimer.animate(0, showValueX, ANIMATION_SPEED * 2, ANIMATION_DELAY);
                        m.clickgui_animY = m.clickgui_animY_timer.animate(0, m.clickgui_animY, ANIMATION_SPEED, ANIMATION_DELAY);
                    } else if (m == curModule) {
                        RenderUtils.drawRoundRect(x + width - showValueX, y + 45, x + width - 10, y + height - 20, 3, -1);
                        showValueX = valueXtimer.animate(width / 3, showValueX, ANIMATION_SPEED * 2, ANIMATION_DELAY);
                        float valuesY = y + 60 + values_whell;
                        // TODO: 2022/2/14 这里写的又臭又长，有时间分开写
                        for (Value v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()))) {
                            Main.fontLoaders.get("roboto18").drawString(v.getName(), x + width - showValueX + 5, valuesY, theme.value_name.getRGB());
                            if (v instanceof BooleanValue) {
                                RenderUtils.drawRoundRect(x + width - 50, valuesY, x + width - 25, valuesY + 10, 2, theme.option_bg.getRGB());
                                v.clickgui_anim = v.clickgui_timer.animate(((boolean) v.getObject()) ? 15 : 0, v.clickgui_anim, ANIMATION_SPEED, ANIMATION_DELAY);
                                RenderUtils.drawFilledCircle(x + width - 45 + v.clickgui_anim, valuesY + 5, 5, ((boolean) v.getObject()) ? theme.option_on.getRGB() : theme.option_off.getRGB(), 5);
                            } else if (v instanceof NumberValue) {
                                RenderUtils.drawRoundRect(x + width - showValueX + 10, valuesY + 14, x + width - 30, valuesY + 15, 2, theme.option_bg.getRGB());
                                float vX = (((Number) v.getObject()).floatValue() - ((NumberValue<?>) v).getMin().floatValue()) / (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue());
                                v.clickgui_anim = v.clickgui_timer.animate(vX * (showValueX - 40), v.clickgui_anim, ANIMATION_SPEED, ANIMATION_DELAY);
                                RenderUtils.drawRoundRect(x + width - showValueX + 10, valuesY + 14, x + width - showValueX + 10 + v.clickgui_anim, valuesY + 15, 2, theme.themeColor.getRGB());
                                DecimalFormat df = new DecimalFormat("#.##");
                                Main.fontLoaders.get("roboto15").drawString(df.format(v.getObject()), x + width - 30 - (showValueX - 10) / 2, valuesY + 11, theme.value_number_value.getRGB(), false);
                                if (((NumberValue<?>) v).clickgui_drag && Mouse.isButtonDown(0) && valuesY > y && valuesY + 20 < y + height) {
                                    float v1 = (mouseX - (x + width - showValueX + 10)) / (showValueX - 40) * (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue()) + ((NumberValue<?>) v).getMin().floatValue();
                                    if(((NumberValue<?>) v).getMax() instanceof Integer) {
                                        v.setObject((int) v1);
                                    }else if (((NumberValue<?>) v).getMax() instanceof Float) {
                                        v.setObject(v1);
                                    }else if (((NumberValue<?>) v).getMax() instanceof Double) {
                                        v.setObject(((double) v1));
                                    }else if (((NumberValue<?>) v).getMax() instanceof Long) {
                                        v.setObject((long) v1);
                                    }
//                                    if (Math.abs(v1 - ((Number) v.getObject()).floatValue()) >= ((Number) v.getObject()).floatValue() / 20) {
//                                        v.setObject(((Number) v.getObject()).floatValue() + (v1 > ((Number) v.getObject()).floatValue() ? ((Number) v.getObject()).floatValue() / 20 : -((Number) v.getObject()).floatValue() / 20));
//                                    }
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
                            } else if (v instanceof ModeValue) {
                                HFontRenderer font = Main.fontLoaders.get("roboto16");
                                float width2 = 0;
                                for (String mode : ((ModeValue) v).getModes()) {
                                    float temp = font.getStringWidth(mode);
                                    if (width2 < temp) width2 = temp;
                                }
                                RenderUtils.drawRoundRect(x + width - 30 - width2, valuesY - 2, x + width - 20, valuesY + 10 + v.clickgui_anim, 2, theme.option_bg.getRGB());
                                font.drawCenteredString(((ModeValue) v).getCurrentMode(), x + width - 25 - width2 / 2, valuesY, theme.value_mode_current.getRGB());
                                if (((ModeValue) v).open) {
                                    v.clickgui_anim = v.clickgui_timer.animate(((ModeValue) v).getModes().length * 16, v.clickgui_anim, ANIMATION_SPEED, ANIMATION_DELAY);
                                } else {
                                    v.clickgui_anim = v.clickgui_timer.animate(0, v.clickgui_anim, ANIMATION_SPEED, ANIMATION_DELAY);
                                }

                                if (((ModeValue) v).open) {
                                    float yy = valuesY + 16;
                                    for (String mode : ((ModeValue) v).getModes()) {
                                        font.drawCenteredString(mode, x + width - 25 - width2 / 2, yy, theme.value_mode_unsel.getRGB());
                                        yy += 16;
                                    }
                                }
                                valuesY += v.clickgui_anim;
                            } else if (v instanceof ColorValue) {
                                if (isHovered(x, y, x + width, y + height - 20, mouseX, mouseY)) {
                                    ((ColorValue) v).draw(x + width - 70, valuesY + 1, 40, 40, mouseX, mouseY);
                                } else {
                                    ((ColorValue) v).draw(x + width - 70, valuesY + 1, 40, 40, -1, -1);
                                }
                                valuesY += 30;
                            }
                            valuesY += 20;
                            RenderUtils.drawRect(x + width - showValueX, valuesY - 5, x + width - 10, valuesY - 5 + 0.5f, theme.value_line.getRGB());
                        }
                    }
                }
                modsY += 40;
            }

        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        //mods列表滚动
        float mouseDwheel = Mouse.getDWheel();
        if (isHovered(x + leftMenuWidth + 10, y + 10, x + width - showValueX - 10, y + height - 20, mouseX, mouseY)) {
            if (mouseDwheel > 0 && mods_whelltemp <= 0) {
                mods_whelltemp += 16;
                if (mods_whelltemp > 0) mods_whelltemp = 0;
            } else if (mouseDwheel < 0 && modsY > y + height) {
                mods_whelltemp -= 16;
            }
        }
        mods_whell = modsScrollAnimationUtils.animate(mods_whelltemp, mods_whell, ANIMATION_SPEED, ANIMATION_DELAY);
        //values列表滚动
        if (isHovered(x + width - showValueX, y + 10, x + width - 10, y + height - 20, mouseX, mouseY)) {
            if (mouseDwheel > 0 && values_whelltemp <= 0) {
                values_whelltemp += 16;
                if (values_whelltemp > 0) values_whelltemp = 0;
            } else if (mouseDwheel < 0 && modsY > y + height) {
                values_whelltemp -= 16;
            }
        }
        values_whell = valuesScrollAnimationUtils.animate(values_whelltemp, values_whell, ANIMATION_SPEED, ANIMATION_DELAY);
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
        //初始化
        gui_anim = -150;
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
        float my = y + 60;
        for (ModuleCategory m : ModuleCategory.values()) {
            if (m == curType) {
                slider.change(my - 8 - y);
                mods_whelltemp = 0;
                mods_whell = 0;
                curType = m;
            }
            my += 30;
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


        float my = y + 60;
        for (ModuleCategory m : ModuleCategory.values()) {
            if (isHovered(x + 0, my - 5, x + leftMenuWidth, my + MS18HEIGHT + 5, mouseX, mouseY)) {
                slider.change(my - 8 - y);
                mods_whelltemp = 0;
                mods_whell = 0;
                curType = m;
                curModule = null;
                list_anim = 25;
            }
            my += 30;
        }

        //功能列表

        float modsY = y + 50 + mods_whell;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() == curType) {
                if (modsY < (y + height - 20)) {
                    if (m == curModule && isHovered(x + width - showValueX, y + 45, x + width - 10, y + height - 20, mouseX, mouseY)) {
                        float valuesY = y + 60 + values_whell;
                        for (Value v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()))) {
                            if (v instanceof BooleanValue) {
                                if (isHovered(x + width - 45, valuesY, x + width - 30, valuesY + 10, mouseX, mouseY)) {
                                    v.setObject(!((BooleanValue) v).getObject());
                                }
                            } else if (v instanceof NumberValue) {
                                if (isHovered(x + width - showValueX + 10, valuesY + 12, x + width - 30, valuesY + 18, mouseX, mouseY)) {
                                    ((NumberValue<?>) v).clickgui_drag = true;
                                }
                                valuesY += 10;
                            } else if (v instanceof ModeValue) {
                                HFontRenderer font = Main.fontLoaders.get("roboto16");
                                float width2 = 0;
                                for (String mode : ((ModeValue) v).getModes()) {
                                    float temp = font.getStringWidth(mode);
                                    if (width2 < temp) width2 = temp;
                                }
                                if (isHovered(x + width - 30 - width2, valuesY - 2, x + width - 20, valuesY + 10, mouseX, mouseY)) {
                                    ((ModeValue) v).open = !((ModeValue) v).open;
                                }
                                if (((ModeValue) v).open) {
                                    float yy = valuesY + 16;
                                    int i = 0;
                                    for (String mode : ((ModeValue) v).getModes()) {
                                        if (isHovered(x + width - 30 - width2, yy, x + width - 20, yy + 16, mouseX, mouseY)) {
                                            v.setObject(i);
                                            ((ModeValue) v).open = false;
                                        }
                                        i++;
                                        yy += 16;
                                    }
                                }
                                valuesY += v.clickgui_anim;
                            } else if (v instanceof ColorValue) {
                                valuesY += 30;
                            }
                            valuesY += 20;
                        }
                    }
                }
                if (isHovered(x + leftMenuWidth + 10, Math.max(modsY, y + 35), x + width - 10 - showValueX, Math.min(modsY + 30, y + height - 17.5f), mouseX, mouseY) && mouseButton == 0) {
                    m.setState(!m.getState());
                    if (mc.theWorld != null) {
                        mc.thePlayer.playSound("random.click", 1, 1);
                    }
                }
                if (isHovered(x + leftMenuWidth + 10, Math.max(modsY, y + 35), x + width - 10 - showValueX, Math.min(modsY + 30, y + height), mouseX, mouseY) && mouseButton == 1) {
                    //打开功能values列表
                    if (curModule != m) {
                        if (Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName())).size() > 0) {
                            curModule = m;
                        } else
                            curModule = null;
                    } else {
                        curModule = null;
                    }
                    values_whell = 0;
                }
                modsY += 40;
            }

        }
    }
}
