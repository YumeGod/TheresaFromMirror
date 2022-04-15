package cn.loli.client.gui.clickui;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.dropdown.panels.components.BooleanComponent;
import cn.loli.client.gui.clickui.dropdown.panels.components.ModeComponent;
import cn.loli.client.gui.clickui.dropdown.panels.components.NumberComponent;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
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

    public static final float ANIMATION_SCALE = .3f; //动画缩放
    public static final float ANIMATION_SPEED = 50f; //动画速度

    private static final float LEFTMENU_MIN_WIDTH = 120; //左侧类别列表的最小宽度
    private static final float LEFTMENU_MAX_WIDTH = 150; //左侧类别列表的最大宽度

    private static final float WINDOW_MIN_WIDTH = 420; //窗口最小宽度
    private static final float WINDOW_MIN_HEIGHT = 300; //窗口最小高度

    static float x = -1, y = -1, width = 0, height = 0;//坐标和宽高

    public static Theme theme = new Theme(); //主题
    private boolean drag; //主窗体是否被拖动
    private float dragX, dragY; //拖动的位置
    ScaledResolution sr;
    static ModuleCategory curType = ModuleCategory.COMBAT;//选中的类别
    static Slider slider = new Slider();//功能分类滑块
    private static Module curModule;//选中的功能
    private final boolean doesGuiPauseGame;//是否暂停游戏
    private static float leftMenuWidth = 0;//左侧类别栏的宽度
    public static float showValueX;//右侧value的宽度
    private static float gui_anim, list_anim;

    //搜索框
    static GuiTextBox searchField;

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

    static float mods_wheel; //用于滚动Module列表
    static float mods_wheelTemp; //用于缓动

    static float values_wheel; //用于滚动Module列表
    static float values_wheelTemp; //用于缓动


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        // 搜索框位置更新
        searchField.xPosition = (int) (x + leftMenuWidth + 10);
        searchField.yPosition = (int) (y + 10);
        searchField.width = (int) (width - leftMenuWidth - 20);

        //缓动
        if (gui_anim < 2) {
            gui_anim = AnimationUtils.smoothAnimation(gui_anim, 2, ANIMATION_SPEED, ANIMATION_SCALE);
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
            float tx = mouseX - dragX;
            float ty = mouseY - dragY;

            if (tx > 0 && tx < sr.getScaledWidth() - width) {
                x = tx;
            }
            if (ty > 0 && ty < sr.getScaledHeight() - height) {
                y = ty;
            }
        }

        //设置窗口大小
        if (sizeDrag) {
            width += mouseX - sizeDragX;
            if (width < WINDOW_MIN_WIDTH) {
                width = WINDOW_MIN_WIDTH;
            } else {
                this.sizeDragX = mouseX;
            }

            height += mouseY - sizeDragY;
            if (height < WINDOW_MIN_HEIGHT) {
                height = WINDOW_MIN_HEIGHT;
            } else {
                this.sizeDragY = mouseY;
            }
        }

        //绘制主窗体
        RenderUtils.drawRoundRect(x, y, x + width, y + height, 2, theme.bg.getRGB());//背景
        RenderUtils.drawRoundRect(x, y, x + leftMenuWidth, y + height, 2, theme.left.getRGB());//左侧列表
        RenderUtils.drawImage(new ResourceLocation("theresa/icons/logo.png"), (int) (x + leftMenuWidth / 2 - 30), (int) (y + 15), 17, 16);//logo
        Main.INSTANCE.fontLoaders.get("heiti24").drawString((Main.CLIENT_NAME.toCharArray()[0] + "").toUpperCase() + Main.CLIENT_NAME.substring(1), (int) (x + leftMenuWidth / 2 - 10), (int) (y + 20), theme.clientname.getRGB());//Client Name
        RenderUtils.drawRect((int) (x + leftMenuWidth / 2 - 3), (int) (y + 30), x + leftMenuWidth / 2 + 5, y + 31, theme.themeColor.getRGB());//客户端名字下方的矩形
        //绘制categories
        float my = y + 60;
        RenderUtils.drawRect(x, y + slider.top - 5, x + leftMenuWidth, y + slider.top + 25, theme.slider.getRGB());//滑块条

        for (ModuleCategory m : ModuleCategory.values()) {
            //capitalize方法: 把全部小写的一串字母转换成开头大写
            Main.INSTANCE.fontLoaders.get("heiti20").drawString(StringUtils.capitalize(StringUtils.lowerCase(m.name())), x + leftMenuWidth / 2 - 20, my, curType == m ? theme.cate_sel.getRGB() : theme.cate_unsel.getRGB());
            RenderUtils.drawImage(new ResourceLocation("theresa/icons/" + m.name().toLowerCase() + ".png"), x + leftMenuWidth / 2 - 35, my - 1, 8, 8);
            my += 30;
        }

        //右下角拖动的图标
        RenderUtils.drawImage(new ResourceLocation("theresa/icons/drag.png"), x + width - 20, y + height - 20, 16, 16, isHovered(x + width - 20, y + height - 20, x + width - 4, y + height - 4, mouseX, mouseY) ? theme.themeColor : theme.sec_unsel);
        searchField.drawTextBox();//搜索框
        if (Objects.equals(searchField.getText(), "") && !searchField.isFocused()) {
            RenderUtils.drawImage(new ResourceLocation("theresa/icons/search.png"), x + leftMenuWidth + 14, y + 14, 8, 8, theme.sec_unsel);
            Main.INSTANCE.fontLoaders.get("heiti18").drawString("Search...", x + leftMenuWidth + 30, y + 15, theme.sec_unsel.getRGB());
        }

        //绘制功能列表并裁剪
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.doGlScissor(x, y + 45, width, height - 65);
        RenderUtils.drawRoundRect(x + leftMenuWidth + 10, y + 45, x + width - 10 - showValueX, y + height - 20, 3, theme.module_list_bg.getRGB());

        if (list_anim != 0) {
            list_anim = AnimationUtils.smoothAnimation(list_anim, 0, ANIMATION_SPEED, ANIMATION_SCALE);
        }


        float modsY = y + 50 + mods_wheel;

        float valuesY = 0;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() == curType) {
                if (!m.getName().contains(searchField.getText()) && searchField.getText() != "") {
                    continue;
                }
                //动画
                GlStateManager.translate(list_anim, 0, 0);
                //获取一些颜色
                int sc1 = m.getState() ? theme.sec_sel.getRGB() : theme.sec_unsel.getRGB();
                int sc2 = m.getState() ? theme.desc_sel.getRGB() : theme.desc_unsel.getRGB();

                //绘制功能名和描述
                Main.INSTANCE.fontLoaders.get("heiti18").drawString(m.getName(), x + leftMenuWidth + 35, modsY + 14 - Main.INSTANCE.fontLoaders.get("heiti15").getHeight() / 2f, sc1);

                float w1 = Main.INSTANCE.fontLoaders.get("heiti18").getStringWidth(m.getName());
                int trimWid = (int) (width - showValueX - leftMenuWidth - 85 - Main.INSTANCE.fontLoaders.get("heiti18").getStringWidth(m.getName()));
                boolean fucked = Main.INSTANCE.fontLoaders.get("heiti18").getStringWidth(m.getDescription()) > trimWid;
                Main.INSTANCE.fontLoaders.get("heiti18").drawString(Main.INSTANCE.fontLoaders.get("heiti18").trimStringToWidth(m.getDescription(), trimWid) + (fucked ? "..." : ""), x + leftMenuWidth + 40 + w1, modsY + 14 - 8 / 2f, sc2);

                m.clickgui_animX = AnimationUtils.smoothAnimation(m.clickgui_animX, m.getState() ? 255 : 0, 45f, ANIMATION_SCALE);

                //绘制功能开关
                RenderUtils.drawImage(new ResourceLocation("theresa/icons/disabled.png"), x + leftMenuWidth + 20, modsY + 10, 8, 8, new Color(255, 255, 255, ((int) (255 - m.clickgui_animX))));
                RenderUtils.drawImage(new ResourceLocation("theresa/icons/enabled.png"), x + leftMenuWidth + 20, modsY + 10, 8, 8, new Color(255, 255, 255, ((int) m.clickgui_animX)));
                RenderUtils.drawImage(new ResourceLocation("theresa/icons/star.png"), x + width - showValueX - 24, modsY + 7, 8, 8, new Color(225, 225, 225));

                RenderUtils.drawRect(x + leftMenuWidth + 10, modsY + 30, x + width - 10 - showValueX, modsY + 31, theme.module_list_line.getRGB());


                if (curModule == null) {
                    showValueX = AnimationUtils.smoothAnimation(showValueX, 0, ANIMATION_SPEED, ANIMATION_SCALE);
                    m.clickgui_animY = AnimationUtils.smoothAnimation(m.clickgui_animY, 0, ANIMATION_SPEED, ANIMATION_SCALE);
                } else if (m == curModule) {
                    RenderUtils.drawRoundRect(x + width - showValueX, y + 45, x + width - 10, y + height - 20, 3, -1);
                    showValueX = AnimationUtils.smoothAnimation(showValueX, width / 3, ANIMATION_SPEED * 2, ANIMATION_SCALE);
                    valuesY = y + 55 + values_wheel;
                    for (Value v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()))) {
                        Main.INSTANCE.fontLoaders.get("heiti18").drawString(v.getName(), x + width - showValueX + 5, valuesY + 1, theme.value_name.getRGB());
                        if (v instanceof BooleanValue) {
                            v.component.draw(x + width - 35, valuesY, partialTicks);
                        } else if (v instanceof NumberValue) {
                            NumberComponent nc = (NumberComponent) v.component;
                            nc.setSizeDrag(sizeDrag);
                            nc.setMouseX(mouseX);
                            nc.setWidth(showValueX - 30);
                            nc.draw(x + width - showValueX + 15, valuesY + 13, partialTicks);
                            valuesY += 8;
                        } else if (v instanceof ModeValue) {
                            ModeComponent mc = (ModeComponent) v.component;
                            mc.draw(x + width - 35, valuesY, partialTicks);
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
                modsY += 31;
            }
        }
        Main.INSTANCE.fontLoaders.get("heiti18").drawString("NOTHING MORE TO SEE HERE", x + leftMenuWidth + (width - showValueX - leftMenuWidth) / 2 - Main.INSTANCE.fontLoaders.get("heiti18").getStringWidth("NOTHING MORE TO SEE HERE") / 2f, modsY + 5, new Color(200, 200, 200).getRGB());
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        float mouseDWheel = Mouse.getDWheel() / 2f;

        //mods列表滚动
        if (isHovered(x + leftMenuWidth + 10, y + 10, x + width - showValueX - 10, y + height - 20, mouseX, mouseY)) {
            if (mouseDWheel > 0 && mods_wheelTemp <= 0) {
                mods_wheelTemp += 16;
                if (mods_wheelTemp > 0) mods_wheelTemp = 0;
            } else if (mouseDWheel < 0 && modsY > y + height - 18) {
                mods_wheelTemp -= 16;
            }
        }

        mods_wheel = AnimationUtils.smoothAnimation(mods_wheel, mods_wheelTemp, ANIMATION_SPEED, ANIMATION_SCALE);

        //values列表滚动
        if (isHovered(x + width - showValueX, y + 10, x + width - 10, y + height - 20, mouseX, mouseY)) {
            if (mouseDWheel > 0 && values_wheelTemp <= 0) {
                values_wheelTemp += 16;
                if (values_wheelTemp > 0) values_wheelTemp = 0;
            } else if (mouseDWheel < 0 && valuesY > y + height - 18) {
                values_wheelTemp -= 16;
            }
        }

        values_wheel = AnimationUtils.smoothAnimation(values_wheel, values_wheelTemp, ANIMATION_SPEED, ANIMATION_SCALE);
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
        searchField = new GuiTextBox(1, Main.INSTANCE.fontLoaders.get("heiti18"), (int) x, (int) y, (int) (width - leftMenuWidth - 20), 20);
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
//                mods_wheelTemp = 0;
//                mods_wheel = 0;
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
        searchField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }

        // Text value realize


        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() == curType) {
                if (!m.getName().contains(searchField.getText()) && searchField.getText() != "") {
                    continue;
                }
                if (m == curModule) {
                    for (Value v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()))) {
                        if (v instanceof StringValue) {
                            if (((StringValue) v).text != null) {
                                ((StringValue) v).text.textboxKeyTyped(typedChar, keyCode);
                            }
                        }
                    }
                }
            }
        }


    }

    boolean sizeDrag = false;
    float sizeDragX;
    float sizeDragY;

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && isHovered(x, y, x + width, y + 34, mouseX, mouseY)) {
            drag = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }

        if (mouseButton == 0 && isHovered(x + width - 20, y + height - 20, x + width - 4, y + height - 4, mouseX, mouseY)) {
            sizeDrag = true;
            sizeDragX = mouseX;
            sizeDragY = mouseY;
        }

        float my = y + 60;
        for (ModuleCategory m : ModuleCategory.values()) {
            if (isHovered(x + 0, my - 5, x + leftMenuWidth, my + MS18HEIGHT + 5, mouseX, mouseY)) {
                slider.change(my - 8 - y);
                mods_wheelTemp = 0;
                mods_wheel = 0;
                curType = m;
                curModule = null;
                list_anim = 25;
            }

            my += 30;
        }

        //功能列表
        float modsY = y + 50 + mods_wheel;

        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() == curType) {
                if (m == curModule && isHovered(x + width - showValueX, y + 45, x + width - 10, y + height - 20, mouseX, mouseY)) {
                    float valuesY = y + 55 + values_wheel;
                    for (Value v : Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()))) {
                        if (v instanceof BooleanValue) {
                            v.component.onMouse(mouseX, mouseY, mouseButton);
                        } else if (v instanceof NumberValue) {
                            NumberComponent nc = (NumberComponent) v.component;
                            nc.onMouse(mouseX, mouseY, mouseButton);
                            valuesY += 8;
                        } else if (v instanceof ModeValue) {
                            ModeComponent mc = (ModeComponent) v.component;
                            mc.onMouse(mouseX, mouseY, mouseButton);
                            valuesY += v.clickgui_anim;
                        } else if (v instanceof StringValue) {
                            if (((StringValue) v).text != null) {
                                ((StringValue) v).text.mouseClicked(mouseX, mouseY, mouseButton);
                            }
                        } else if (v instanceof ColorValue) {
                            valuesY += 30;
                        }
                        valuesY += 20;
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
                    values_wheel = 0;
                    values_wheelTemp = 0;
                    if (curModule != m) {
                        if (Objects.requireNonNull(Main.INSTANCE.valueManager.getAllValuesFrom(m.getName())).size() > 0) {
                            curModule = m;
                        } else
                            curModule = null;
                    } else {
                        curModule = null;
                    }
                }
                modsY += 31;
            }

        }
    }
}
