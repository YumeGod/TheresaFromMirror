package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;

import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class HUD extends Module {
    private final ArrayList<Module> needRemove = new ArrayList<>();
    private HFontRenderer fontRenderer;
    private final NumberProperty<Integer> fontSize = new NumberProperty<>("FontSize", 12, 12, 20, 1);
    public float maxY = 0;

    public HUD() {
        super("HUD", "The heads up display overlay", ModuleCategory.MISC);
        if (!font.getPropertyValue().toString().equals("Minecraft"))
            fontRenderer = Main.INSTANCE.fontLoaders.get((font.getPropertyValue().toString()).toLowerCase() + fontSize.getPropertyValue());
        originalSort();
    }

    private enum FONT_MODE {
        MINECRAFT("Minecraft"), ROBOTO("Roboto"), GENSHIN("Genshin"), UBUNTU("Ubuntu"), DOS("Dos");

        private final String name;

        FONT_MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty font = new EnumProperty<>("Font", FONT_MODE.MINECRAFT);
    private final NumberProperty<Number> ArrayListXPos = new NumberProperty<>("ArrayListXPos", 0, 0, 50, 1);
    private final NumberProperty<Number> ArrayListYPos = new NumberProperty<>("ArrayListYPos", 0, 0, 50, 1);
    private final NumberProperty<Number> arrayListSpace = new NumberProperty<>("ArrayListSpace", 12, 0, 28, 1);

    private final BooleanProperty showClientInfo = new BooleanProperty("ClientInfo", true);
    private final BooleanProperty showArrayList = new BooleanProperty("ArrayList", true);
    private final BooleanProperty showNotifications = new BooleanProperty("Notifications", true);
    private final BooleanProperty noRender = new BooleanProperty("noRender", false);

    // private final BooleanProperty onlyKeyBind = new BooleanProperty("Only KeyBind", false);
    private final BooleanProperty reverse = new BooleanProperty("Sort Reverse", false);

    private enum LOGO_MODE {
        THERESA("Theresa"), NONE("None"), SIMPLE("Logo2");

        private final String name;

        LOGO_MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty logoMode = new EnumProperty<>("LogoMode", LOGO_MODE.THERESA);

    private enum ARRAY_MODE {
        SIMPLE("Simple"), RECTANGLE("Rectangle"), SIMPLE2("Simple2");

        private final String name;

        ARRAY_MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty arrayMode = new EnumProperty<>("ArrayListMode", ARRAY_MODE.SIMPLE);

    private enum COLOR {
        COLOR("Color"), RAINBOW("Rainbow"), RAINBOW2("Rainbow2");

        private final String name;

        COLOR(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty arrayColor = new EnumProperty<>("Color", COLOR.COLOR);
    private final ColorProperty color = new ColorProperty("Color", new Color(255, 255, 255));
    private final NumberProperty<Number> rainbowSaturation = new NumberProperty<>("RainbowSaturation", 0.6f, 0f, 1f, 0.01f);
    private final NumberProperty<Number> rainbowBrightness = new NumberProperty<>("RainbowBrightness", 1f, 0f, 1f, 0.01f);


    private enum ANIMATION_MODE {
        NONE("None"), SLIDE("Slide"), SMOOTH("Smooth"), ALPHA("Alpha");

        private final String name;

        ANIMATION_MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty arrayAnimation = new EnumProperty<>("ArrayAnimation", ANIMATION_MODE.SMOOTH);

    private final StringProperty clientName = new StringProperty("ClientName", "Theresa.exe");

    public final ArrayList<Module> arraylist_mods = new ArrayList<>();

    private final IEventListener<Render2DEvent> onRender2D = event ->
    {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        switch (logoMode.getPropertyValue().toString()) {
            case "Theresa":
                RenderUtils.drawImage(new ResourceLocation("theresa/icons/logo.png"), 10, 8, 17, 16);//logo
                Main.INSTANCE.fontLoaders.fonts.get("heiti22").drawStringWithShadow(clientName.getPropertyValue(), 30, 13, new Color(255, 255, 255).getRGB());
                break;
            case "Logo2":
                Main.INSTANCE.fontLoaders.fonts.get("heiti24").drawString(clientName.getPropertyValue(), 16, 12, new Color(255, 255, 255).getRGB());
                Main.INSTANCE.fontLoaders.fonts.get("heiti18").drawString("v" + Main.CLIENT_VERSION, 18, 26, new Color(255, 255, 255).getRGB());
                break;
            case "None":
                break;
        }
        if (this.showClientInfo.getPropertyValue()) {
            this.drawClientInfo(sr);
        }
        if (this.showArrayList.getPropertyValue()) {
            this.drawArrayList(ArrayListXPos.getPropertyValue().floatValue(), ArrayListYPos.getPropertyValue().floatValue(), sr);
        }
        if (this.showNotifications.getPropertyValue()) {
            this.drawNotifications();
        }
    };


    private void drawNotifications() {
        NotificationManager.render();
    }

    int rainbowOffset;

    private final IEventListener<TickEvent> onTick = event ->
    {
        if (mc.thePlayer != null)
            if (mc.thePlayer.ticksExisted % 2 == 0)
                rainbowOffset++;

    };


    private void drawArrayList(float x, float y, ScaledResolution sr) {
        if (showArrayList.getPropertyValue()) {
            float modY = 0;
            int offset = 50;

            for (Module arraylist_mod : arraylist_mods) {
                if (arraylist_mod.getCategory().equals(ModuleCategory.RENDER) && noRender.getPropertyValue()) {
                    continue;
                }
                String name = arraylist_mod.getName() + (arraylist_mod.getSuffix() != null ? " " + arraylist_mod.getSuffix() : "");
                float x1 = 0, y1 = 0;
                // get color
                int acolor = 0xFFFFFF;
                offset--;
                switch (arrayColor.getPropertyValue().toString()) {
                    case "Color":
                        acolor = color.getPropertyValue().getRGB();
                        break;
                    case "Rainbow":
                        acolor = RenderUtils.getRainbow((offset + rainbowOffset) * 100, 6000, rainbowSaturation.getPropertyValue().floatValue(), rainbowBrightness.getPropertyValue().floatValue()).getRGB();
                        break;
                    case "Rainbow2":
                        acolor = new Color(color.getPropertyValue().getRed(), color.getPropertyValue().getGreen(), color.getPropertyValue().getBlue(), RenderUtils.getRainbow((offset + rainbowOffset) * 100, 6000, rainbowSaturation.getPropertyValue().floatValue(), rainbowBrightness.getPropertyValue().floatValue()).getRed()).getRGB();
                }

                FontRenderer mcFont = mc.fontRendererObj;
                boolean flag = font.getPropertyValue().toString().equals("Minecraft");

                if (!flag) {
                    if (Main.INSTANCE.fontLoaders.get((font.getPropertyValue().toString()).toLowerCase() + fontSize.getPropertyValue()) != fontRenderer) {
                        fontRenderer = Main.INSTANCE.fontLoaders.get((font.getPropertyValue().toString()).toLowerCase() + fontSize.getPropertyValue());
                        sort();
                    }
                } else {
                    if (fontRenderer != null) {
                        fontRenderer = null;
                        sort();
                    }
                }

                if (arrayAnimation.getPropertyValue().toString().equals("Alpha")) {
                    if (flag) {
                        x1 = sr.getScaledWidth() - mcFont.getStringWidth(name) - x;
                    } else {
                        x1 = sr.getScaledWidth() - fontRenderer.getStringWidth(name) - x;
                    }
                    if (arraylist_mod.getState()) {
                        arraylist_mod.arraylist_animA = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animA, 255, 30, 0.1f);
                        arraylist_mod.arraylist_animY = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animY, y + modY, 50, 0.3f);
                    } else {
                        arraylist_mod.arraylist_animY = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animY, y + modY - 10, 50, 0.5f);
                        arraylist_mod.arraylist_animA = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animA, 0, 50, 0.4f);
                        if (arraylist_mod.arraylist_animA <= 30) {
                            needRemove.add(arraylist_mod);
                        }
                    }
                    y1 = arraylist_mod.arraylist_animY;
                } else if (arrayAnimation.getPropertyValue().toString().equals("Slide")) {
                    arraylist_mod.arraylist_animA = 255;
                    if (flag) {
                        if (arraylist_mod.getState()) {
                            arraylist_mod.arraylist_animX = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animX, sr.getScaledWidth() - mcFont.getStringWidth(name) - x, 50, 0.5f);
                        } else {
                            arraylist_mod.arraylist_animX = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animX, sr.getScaledWidth(), 50, 0.5f);
                        }
                    } else {
                        if (arraylist_mod.getState()) {
                            arraylist_mod.arraylist_animX = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animX, sr.getScaledWidth() - fontRenderer.getStringWidth(name) - x, 50, 0.5f);
                        } else {
                            arraylist_mod.arraylist_animX = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animX, sr.getScaledWidth(), 50, 0.5f);
                        }
                    }
                    x1 = arraylist_mod.arraylist_animX;
                    y1 = y + modY;
                    arraylist_mod.arraylist_animY = y1;
                    if (!arraylist_mod.getState() && arraylist_mod.arraylist_animX >= sr.getScaledWidth()) {
                        needRemove.add(arraylist_mod);
                    }
                } else if (arrayAnimation.getPropertyValue().toString().equals("Smooth")) {
                    arraylist_mod.arraylist_animA = 255;
                    if (arraylist_mod.getState()) {
                        if (flag) {
                            arraylist_mod.arraylist_animX = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animX, sr.getScaledWidth() - mcFont.getStringWidth(name) - x, 50, 0.5f);
                        } else {
                            arraylist_mod.arraylist_animX = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animX, sr.getScaledWidth() - fontRenderer.getStringWidth(name) - x, 50, 0.5f);
                        }
                        arraylist_mod.arraylist_animY = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animY, y + modY, 50, 0.5f);
                    } else {
                        arraylist_mod.arraylist_animX = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animX, sr.getScaledWidth(), 50, 0.5f);
                        arraylist_mod.arraylist_animY = AnimationUtils.smoothAnimation(arraylist_mod.arraylist_animY, y + modY - 16, 50, 0.5f);
                    }
                    x1 = arraylist_mod.arraylist_animX;
                    y1 = arraylist_mod.arraylist_animY;
                    if (arraylist_mod.arraylist_animY == y + modY - 16) {
                        needRemove.add(arraylist_mod);
                    }
                } else if (arrayAnimation.getPropertyValue().toString().equals("None")) {
                    arraylist_mod.arraylist_animA = 255;
                    if (flag) {
                        x1 = sr.getScaledWidth() - mcFont.getStringWidth(name) - x;

                    } else {
                        x1 = sr.getScaledWidth() - fontRenderer.getStringWidth(name) - x;
                    }
                    y1 = y + modY;
                    arraylist_mod.arraylist_animY = y1;

                    if (!arraylist_mod.getState()) {
                        needRemove.add(arraylist_mod);
                    }
                }

                Color c = new Color(acolor);
                float width = 0;
                if (flag) {
                    width = mcFont.getStringWidth(name);
                } else {
                    width = fontRenderer.getStringWidth(name);
                }

                switch (arrayMode.getPropertyValue().toString()) {
                    case "Rectangle":
                        RenderUtils.drawRect(x1 - 4, y1, x1 + width + 5, y1 + arrayListSpace.getPropertyValue().floatValue(), new Color(0, 0, 0, 100).getRGB());
                        RenderUtils.drawRect(x1 + width + 3.5f, y1, x1 + width + 5, y1 + arrayListSpace.getPropertyValue().floatValue(), c.getRGB());
                        break;
                    case "Simple2":
                        RenderUtils.drawRect(x1 - 4, y1, x1 + width + 5, y1 + arrayListSpace.getPropertyValue().floatValue(), new Color(0, 0, 0, 100).getRGB());
                        break;
                }
                if (flag) {
                    mcFont.drawStringWithShadow(name, x1, y1 + arrayListSpace.getPropertyValue().floatValue() / 2 - mcFont.FONT_HEIGHT / 2f, new Color(c.getRed(), c.getGreen(), c.getBlue(), ((int) arraylist_mod.arraylist_animA)).getRGB());
                } else {
                    fontRenderer.drawStringWithShadow(name, x1, y1 + arrayListSpace.getPropertyValue().floatValue() / 2 - fontRenderer.getHeight() / 2, new Color(c.getRed(), c.getGreen(), c.getBlue(), ((int) arraylist_mod.arraylist_animA)).getRGB());
                }

                modY += arrayListSpace.getPropertyValue().floatValue();
                maxY = arraylist_mod.arraylist_animY + (mcFont == null ? fontRenderer.getHeight() : 8) + 2;
            }

            if (needRemove.size() != 0) {
                arraylist_mods.removeAll(needRemove);
                needRemove.clear();
            }
        }
    }


    public void sort() {
        boolean mcfont = font.getPropertyValue().toString().equals("Minecraft");
        arraylist_mods.sort(Comparator.comparingInt(m -> mcfont ? -mc.fontRendererObj.getStringWidth(m.getName() + (m.getSuffix() != null ? " " + m.getSuffix() : ""))
                : -fontRenderer.getStringWidth(m.getName() + (m.getSuffix() != null ? " " + m.getSuffix() : ""))));

        if (reverse.getPropertyValue()) {
            arraylist_mods.sort(Comparator.comparingInt(m -> mcfont ? mc.fontRendererObj.getStringWidth(m.getName() + (m.getSuffix() != null ? " " + m.getSuffix() : ""))
                    : fontRenderer.getStringWidth(m.getName() + (m.getSuffix() != null ? " " + m.getSuffix() : ""))));
        }
    }

    private void drawClientInfo(ScaledResolution res) {
        double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        double currSpeed = StrictMath.sqrt(xDist * xDist + zDist * zDist) * 20.0 * ((IAccessorMinecraft) mc).getTimer().timerSpeed;

        if (showClientInfo.getPropertyValue()) {
            int fpsWidth = mc.fontRendererObj.drawString("FPS: " + Minecraft.getDebugFPS(), 2, res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2, -1, true);
            fpsWidth = Math.max(fpsWidth, mc.fontRendererObj.drawString(String.format("BPS: %.2f", currSpeed), 2, res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT * 2 - 2, -1, true));
            fpsWidth = Math.max(fpsWidth, mc.fontRendererObj.drawString(String.format("User: " + Main.INSTANCE.name, currSpeed), (float) (res.getScaledWidth() - mc.fontRendererObj.getStringWidth(String.format("User: " + Main.INSTANCE.name, currSpeed))), res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2, -1, true));
        }
    }

    private void originalSort() {
        arraylist_mods.clear();
        arraylist_mods.addAll(Main.INSTANCE.moduleManager.getModules());
        sort();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    // Reverse an arraylist return new arraylist
    public static ArrayList<Module> reverse(ArrayList<Module> arrayList) {
        ArrayList<Module> newArrayList = new ArrayList<>();
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            newArrayList.add(arrayList.get(i));
        }
        return newArrayList;
    }


    @Override
    public void onDisable() {
        super.onDisable();
    }
}
