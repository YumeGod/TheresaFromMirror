package cn.loli.client.module.modules.render;

import cn.loli.client.Main;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.combat.Aura;
import cn.loli.client.utils.player.rotation.RotationHook;
import cn.loli.client.utils.player.rotation.RotationUtils;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.potion.Potion;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TargetHUD extends Module {

    final RotationUtils utils = RotationUtils.getInstance();
    Entity entity;
    Map<EntityLivingBase, Float> health = new HashMap<>();
    Map<EntityPlayer, PlayerInfo> playerList = new HashMap<>();
    ArrayList<EntityPlayer> playerInfoList = new ArrayList<>();

    private final ModeValue font = new ModeValue("Font", "Genshin", "Genshin", "Ubuntu");

    public TargetHUD() {
        super("Target Hud", "Make you can check the detail of targets", ModuleCategory.RENDER);
    }

    @EventTarget
    private void onRender(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);

        int renderIndex = 0;

        if (entity != null && entity instanceof EntityPlayer && !playerList.containsKey(entity) && playerList.size() < 5) {
            PlayerInfo info = new PlayerInfo((EntityPlayer) entity, System.currentTimeMillis());
            playerInfoList.add(info.player);
            playerList.put((EntityPlayer) entity, info);
        }

        for (int i = 0; i < playerInfoList.size(); i++)
            if (System.currentTimeMillis() - playerList.get(playerInfoList.get(i)).timestamp >= 5000)
                playerList.remove(playerInfoList.remove(i));


        for (int i = 0; i < playerList.size(); i++) {
            new THud(playerList.get(playerInfoList.get(i)).player).
                    render(sr.getScaledWidth() / 2f + 18, sr.getScaledHeight() / 2f - 17.5f + (renderIndex * 42));
            renderIndex++;
        }
    }

    @EventTarget
    private void onReload(TickEvent event) {
        entity = Main.INSTANCE.moduleManager.getModule(Aura.class).getState()
                ? Main.INSTANCE.moduleManager.getModule(Aura.class).target
                : utils.rayCastedEntity(6.0, RotationHook.yaw, RotationHook.pitch);
    }

    static class PlayerInfo {
        private final EntityPlayer player;
        private final long timestamp;

        public PlayerInfo(EntityPlayer player, final long timestamp) {
            this.player = player;
            this.timestamp = timestamp;
        }
    }

    class THud {
        public final Minecraft mc = Minecraft.getMinecraft();
        public final EntityPlayer ent;
        public float animation = 0;

        public THud(EntityPlayer player) {
            this.ent = player;
        }

        public void render(float x, float y) {
            GL11.glPushMatrix();
            String playerName = ent.getName();

            HFontRenderer fontRenderer;
            HFontRenderer fontRenderer2;

            if (font.getCurrentMode().equals("Genshin")) {
                fontRenderer = Main.INSTANCE.fontLoaders.get("genshin16");
                fontRenderer2 = Main.INSTANCE.fontLoaders.get("genshin14");
            } else {
                fontRenderer = Main.INSTANCE.fontLoaders.get("ubuntu16");
                fontRenderer2 = Main.INSTANCE.fontLoaders.get("ubuntu14");
            }

            String healthStr = Math.round(ent.getHealth() * 10) / 10d + " hp";
            float width = Math.max(85, fontRenderer.getStringWidth(playerName.replaceAll("\247.", "")) + 45);


            //更改TargetHUD在屏幕坐标的初始位置
            GL11.glTranslatef(x, y, 0);
            RenderUtils.drawRoundRect(0, 0, 30 + width, 40, 3, RenderUtils.reAlpha(0xffffff, 0.4f));
            RenderUtils.drawOutlinedRect(0, 0, 30 + width, 40, 1, RenderUtils.reAlpha(0x000000, 0.6f));

            fontRenderer.drawString(playerName, 30f, 2f, new Color(0xffffff).getRGB());
            fontRenderer2.drawString(healthStr, 28 + width - fontRenderer2.getStringWidth(healthStr) - 2, 4f, 0xffcccccc);


            boolean isNaN = Float.isNaN(ent.getHealth());
            float health = isNaN ? 20 : ent.getHealth();
            float maxHealth = isNaN ? 20 : ent.getMaxHealth();
            float healthPercent = RenderUtils.clamp(health / maxHealth, 0f, 1f);

            RenderUtils.drawRectBound(31, 15f, width - 4, 10f, RenderUtils.reAlpha(new Color(0xffffff).getRGB(), 0.35f));

            float barWidth = width - 6;
            float drawPercent = (barWidth / 100) * (healthPercent * 100);

            if (Main.INSTANCE.moduleManager.getModule(TargetHUD.class).health.get(ent) == null)
                this.animation = drawPercent;
            else
                this.animation = Main.INSTANCE.moduleManager.getModule(TargetHUD.class).health.get(ent);

            animation = AnimationUtils.smoothAnimation(animation, drawPercent, 10f, 0.4f);

            Main.INSTANCE.moduleManager.getModule(TargetHUD.class).health.put(ent, animation);

            float f3 = (barWidth / 100f) * (ent.getTotalArmorValue() * 5);

            int healcol = new Color(0x77EE37).getRGB();
            int col = new Color(0xF50909).getRGB();
            boolean heal = drawPercent > animation;

            String[] parts = {"\ue900", "\ue901", "\ue902", "\ue903", "\ue904", "\ue905", "\ue908", "\ue907", "\ue908"};
            int[] partIndex = {0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB};

            boolean isArmor = ent.getTotalArmorValue() > 0;
            boolean isInLiquid = ent.isInWater();
            boolean isBurner = ent.isBurning();
            boolean isBlock = ent.isBlocking();
            boolean canSee = ent.canEntityBeSeen(mc.thePlayer);
            boolean isHeal = heal || ent.getActivePotionEffect(Potion.regeneration) != null;
            boolean canFall = ent.getActivePotionEffect(Potion.moveSpeed) != null;

            if (isArmor)
                partIndex[0] = 0xEFC434;
            if (isInLiquid)
                partIndex[1] = 0x04ACE2;
            if (isBurner)
                partIndex[2] = 0xF97824;
            if (isBlock)
                partIndex[3] = 0xBA9243;
            if (canSee)
                partIndex[4] = 0xA776D4;
            if (isHeal)
                partIndex[5] = 0x9AD019;
            if (canFall)
                partIndex[6] = 0x76E2A9;

            for (int i = 0; i < 7; i++)
                Main.INSTANCE.fontLoaders.get("targethub18").drawStringWithShadow(parts[i], 30f + (i * (Main.INSTANCE.fontLoaders.get("targethub18").getStringWidth(parts[i]) + 1)), 30f, partIndex[i]);

            RenderUtils.drawRoundedRect(32, 16f, this.animation, 3f, 1, RenderUtils.reAlpha(RenderUtils.darker(heal ? healcol : col, 25), 0.95f));

            if (!heal) RenderUtils.drawRoundedRect(32, 16f, drawPercent, 3f, 1, RenderUtils.reAlpha(col, 0.95f));

            RenderUtils.drawRoundedRect(32, 20f, width - 5, 5f, 1, RenderUtils.reAlpha(new Color(0xB6B6B6).getRGB(), 0.6f));
            RenderUtils.drawRoundedRect(32, 21f, f3, 3f, 1, 0xff4286f5);

            RenderUtils.drawOutlinedRect(2, 2, 28, 28, 1, new Color(0xBEFFFFFF, true), new Color(0xD3FFFFFF, true));

            GL11.glColor4f(1, 1, 1, 1);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDepthMask(false);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
                if (mc.theWorld.getPlayerEntityByUUID(info.getGameProfile().getId()) == ent) {
                    mc.getTextureManager().bindTexture(info.getLocationSkin());
                    RenderUtils.drawScaledCustomSizeModalRect(3f, 3f, 8.0f, 8.0f, 8, 8, 24, 24, 64.0f, 64.0f);
                    if (ent.isWearing(EnumPlayerModelParts.HAT)) {
                        RenderUtils.drawScaledCustomSizeModalRect(3f, 3f, 40.0f, 8.0f, 8, 8, 24, 24, 64.0f, 64.0f);
                    }
                    GlStateManager.bindTexture(0);
                    break;
                }
            }
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            GlStateManager.resetColor();
            GL11.glPopMatrix();
        }
    }
}
