

package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HUD extends Module {
    private final BooleanValue showClientInfo = new BooleanValue("ClientInfo", true);
    private final BooleanValue showLogo = new BooleanValue("Logo", true);
    private final BooleanValue showArrayList = new BooleanValue("ArrayList", true);
    private final BooleanValue showNotifications = new BooleanValue("Notifications", true);
    private final NumberValue<Number> ArrayListXPos = new NumberValue<>("ArrayListXPos", 0, 0, 15);
    private final NumberValue<Number> ArrayListYPos = new NumberValue<>("ArrayListYPos", 0, 0, 15);
    private final BooleanValue reverse = new BooleanValue("Sort Reverse", false);

    private final ModeValue mode = new ModeValue("Mode", "Normal", "Normal");
    private final ModeValue font = new ModeValue("Font", "Minecraft", "Minecraft", "Genshin", "Ubuntu", "Dos");

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    public List<Module> sort = new ArrayList<>();
    private boolean sorted = false;

    public HUD() {
        super("HUD", "The heads up display overlay", ModuleCategory.MISC);
        setState(true);
        sort.addAll(Main.INSTANCE.moduleManager.getModules());
    }

    //反转ArrayList
    private static ArrayList<Module> reverse(List<Module> list) {
        ArrayList<Module> newList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            newList.add(list.get(i));
        }
        return newList;
    }

    @EventTarget
    private void render2D(Render2DEvent event) {
        if (!getState()) return;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        boolean mcfont = font.getCurrentMode().equals("Minecraft");

        HFontRenderer fontRenderer;

        if (font.getCurrentMode().equals("Genshin")) {
            fontRenderer = Main.INSTANCE.fontLoaders.get("genshin16");
        } else if (font.getCurrentMode().equals("Ubuntu"))
            fontRenderer = Main.INSTANCE.fontLoaders.get("ubuntu16");
        else
            fontRenderer = Main.INSTANCE.fontLoaders.get("dos18");


        if (!sorted) {
            sort.sort(Comparator.comparingInt(m -> mcfont ? mc.fontRendererObj.getStringWidth(m.getName())
                    : fontRenderer.getStringWidth(m.getName())));
            if (!reverse.getObject())
                sort = reverse(sort);
            sorted = true;
        }

        int i = ArrayListYPos.getObject().intValue();

        double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        double currSpeed = StrictMath.sqrt(xDist * xDist + zDist * zDist) * 20.0 * ((IAccessorMinecraft) mc).getTimer().timerSpeed;

        if (showClientInfo.getObject()) {
            int fpsWidth = mc.fontRendererObj.drawString("FPS: " + Minecraft.getDebugFPS(), 2, res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2, -1, true);
            fpsWidth = Math.max(fpsWidth, mc.fontRendererObj.drawString(String.format("BPS: %.2f", currSpeed), 2, res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT * 2 - 2, -1, true));
            fpsWidth = Math.max(fpsWidth, mc.fontRendererObj.drawString(String.format("User: " + Main.INSTANCE.name, currSpeed), (float) (res.getScaledWidth() - mc.fontRendererObj.getStringWidth(String.format("User: " + Main.INSTANCE.name, currSpeed))), res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2, -1, true));
        }

        if (showLogo.getObject()) {
            GL11.glScaled(2.0, 2.0, 2.0);
            int string = mc.fontRendererObj.drawString("朔夜观星", 2, 2, rainbow(0), true);
            GL11.glScaled(0.5, 0.5, 0.5);

            mc.fontRendererObj.drawString(Main.CLIENT_VERSION, string * 2, mc.fontRendererObj.FONT_HEIGHT * 2 - 7, rainbow(100), true);
            //   fontRenderer.drawString("by " + Main.CLIENT_AUTHOR, 4, fontRenderer.FONT_HEIGHT * 2 + 2, rainbow(200), true);
        }

        if (showArrayList.getObject()) {
            for (Module m : sort) {
                if (m.getState()) {
                    String s = m.getName();
                    if (mcfont)
                        mc.fontRendererObj.drawStringWithShadow(s, res.getScaledWidth() - m.arraylist_animX, m.arraylist_animY, rainbow(25));
                    else
                        fontRenderer.drawStringWithShadow(s, res.getScaledWidth() - m.arraylist_animX, m.arraylist_animY, rainbow(50), 150);

                    m.arraylist_animY = AnimationUtils.smoothAnimation(m.arraylist_animY, i + ArrayListYPos.getObject().intValue(), 50, .3f);
                    m.arraylist_animX = AnimationUtils.smoothAnimation(m.arraylist_animX, (mcfont ? mc.fontRendererObj.getStringWidth(s) : fontRenderer.getStringWidth(s)) + ArrayListXPos.getObject().intValue(), 50, .4f);
                    i += 12;
                }
            }
        }

        if (showNotifications.getObject()) {
            NotificationManager.render();
        }
    }

    private static int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.45f, 0.9f).getRGB();
    }

    @Override
    public void onEnable() {
        if (sorted) sorted = false;

    }
}
