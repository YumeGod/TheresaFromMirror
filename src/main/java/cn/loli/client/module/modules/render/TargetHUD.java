package cn.loli.client.module.modules.render;

import cn.loli.client.Main;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.combat.Aura;
import cn.loli.client.utils.player.rotation.RotationHook;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.EnumProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TargetHUD extends Module {

    Entity entity;
    Map<EntityLivingBase, Float> health = new HashMap<>();
    Map<EntityPlayer, PlayerInfo> playerList = new HashMap<>();
    Map<EntityPlayer, Float[]> posMap = new HashMap<>();

    ArrayList<EntityPlayer> playerInfoList = new ArrayList<>();

    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);

    private enum MODE {
        GENSHIN,
        FANCY
    }

    private enum FONT {
        GENSHIN,
        ROBOTO,
        UBUNTU,
        DOS
    }

    private enum SORT {
        NORMAL,
        PLAYER
    }

    private final EnumProperty mode = new EnumProperty<>("Mode", MODE.GENSHIN);

    private final EnumProperty font = new EnumProperty<>("Font", FONT.GENSHIN);
    private final NumberProperty<Integer> targetAmount = new NumberProperty<>("Display Amount", 5, 1, 6, 1);
    private final EnumProperty sort = new EnumProperty<>("Display", SORT.NORMAL);

    public TargetHUD() {
        super("Target Hud", "Make you can check the detail of targets", ModuleCategory.RENDER);
    }

    private final IEventListener<Render2DEvent> onRender = event ->
    {
        ScaledResolution sr = new ScaledResolution(mc);

        int renderIndex = 0;

        if (entity != null && entity instanceof EntityPlayer && !playerList.containsKey(entity) && playerList.size() < targetAmount.getPropertyValue()) {
            PlayerInfo info = new PlayerInfo((EntityPlayer) entity, System.currentTimeMillis());
            playerInfoList.add(info.player);
            playerList.put((EntityPlayer) entity, info);
        }

        for (int i = 0; i < playerInfoList.size(); i++)
            if (System.currentTimeMillis() - playerList.get(playerInfoList.get(i)).timestamp >= 5000
                    || playerList.get(playerInfoList.get(i)).player.isDead
                    || playerList.get(playerInfoList.get(i)).player.getDistanceToEntity(mc.thePlayer) > 8)
                playerList.remove(playerInfoList.remove(i));


        for (int i = 0; i < playerList.size(); i++) {
            float x;
            float y;

            if (sort.getCurrentMode().equals("Player")) {
                x = (posMap.get(playerInfoList.get(i))[0] + posMap.get(playerInfoList.get(i))[2]) / 2.25f;
                y = ((posMap.get(playerInfoList.get(i))[1] + posMap.get(playerInfoList.get(i))[3]) / 2.25f) - 19;
            } else {
                x = sr.getScaledWidth() / 2f + 18;
                y = sr.getScaledHeight() / 2f - 17.5f + (renderIndex * 42);
            }

            new THud(playerList.get(playerInfoList.get(i)).player).
                    render(x, y);
            renderIndex++;
        }
    };


    private final IEventListener<RenderEvent> onRenderSR = event ->
    {
        for (int i = 0; i < playerList.size(); i++) {
            //3d coordinate to 2d coordinate
            ScaledResolution res = new ScaledResolution(mc);
            int scaleFactor = res.getScaleFactor();
            Vec3 vec3 = getVec3(playerInfoList.get(i));
            float posX = (float) (vec3.xCoord - mc.getRenderManager().viewerPosX);
            float posY = (float) (vec3.yCoord - mc.getRenderManager().viewerPosY);
            float posZ = (float) (vec3.zCoord - mc.getRenderManager().viewerPosZ);

            GL11.glPushMatrix();
            double halfWidth = playerInfoList.get(i).width / 2.0D + 0.18F;
            AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
                    posY + playerInfoList.get(i).height + 0.18D, posZ + halfWidth);

            double[][] vectors = {{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ},
                    {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ},
                    {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};

            Vector3f projection;
            Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F);

            for (double[] vec : vectors) {
                projection = project2D((float) vec[0], (float) vec[1], (float) vec[2], scaleFactor);
                if (projection != null && projection.z >= 0.0F && projection.z < 1.0F) {
                    position.x = Math.min(position.x, projection.x);
                    position.y = Math.min(position.y, projection.y);
                    position.z = Math.max(position.z, projection.x);
                    position.w = Math.max(position.w, projection.y);
                }
            }

            posMap.put(playerInfoList.get(i), new Float[]{position.x, position.y, position.z, position.w});
            GL11.glPopMatrix();
        }
    };

    private final IEventListener<TickEvent> refresh = event ->
    {
        entity = Main.INSTANCE.moduleManager.getModule(Aura.class).getState()
                ? Main.INSTANCE.moduleManager.getModule(Aura.class).target
                : rotationUtils.rayCastedEntity(6.0, RotationHook.yaw, RotationHook.pitch);
    };


    private Vec3 getVec3(final EntityPlayer var0) {
        final float timer = ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
        final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
        final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
        final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
        return new Vec3(x, y, z);
    }


    private Vector3f project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        if (GLU.gluProject(x, y, z, modelMatrix, projectionMatrix, viewport, windowPosition)) {
            return new Vector3f(windowPosition.get(0) / scaleFactor,
                    (mc.displayHeight - windowPosition.get(1)) / scaleFactor, windowPosition.get(2));
        }

        return null;
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
            } else if (font.getCurrentMode().equals("Ubuntu")) {
                fontRenderer = Main.INSTANCE.fontLoaders.get("ubuntu16");
                fontRenderer2 = Main.INSTANCE.fontLoaders.get("ubuntu14");
            } else if (font.getCurrentMode().equals("Dos")) {
                fontRenderer = Main.INSTANCE.fontLoaders.get("dos18");
                fontRenderer2 = Main.INSTANCE.fontLoaders.get("dos14");
            } else if (font.getCurrentMode().equals("Roboto")) {
                fontRenderer = Main.INSTANCE.fontLoaders.get("roboto18");
                fontRenderer2 = Main.INSTANCE.fontLoaders.get("roboto14");
            } else {
                fontRenderer = Main.INSTANCE.fontLoaders.get("roboto18");
                fontRenderer2 = Main.INSTANCE.fontLoaders.get("roboto14");
            }


            String healthStr = Math.round(ent.getHealth() * 10) / 10d + " hp";
            float width = Math.max(85, fontRenderer.getStringWidth(playerName.replaceAll("\247.", "")) + 45);


            //更改TargetHUD在屏幕坐标的初始位置
            GL11.glTranslatef(x, y, 0);
            if (mode.getCurrentMode().equals("Genshin")) {
                RenderUtils.drawRoundRect(0, 0, 30 + width, 40, 3, RenderUtils.reAlpha(0x000000, 0.6f));
                RenderUtils.drawOutlinedRect(0, 0, 30 + width, 40, 1, RenderUtils.reAlpha(0xffffff, 0.4f));

                fontRenderer.drawString(playerName, 30f, 2f, new Color(0xffffff).getRGB());
                fontRenderer2.drawString(healthStr, 28 + width - fontRenderer2.getStringWidth(healthStr) - 2, 4f, 0xffcccccc);


                boolean isNaN = Float.isNaN(ent.getHealth());
                float health = isNaN ? 20 : ent.getHealth();
                float maxHealth = isNaN ? 20 : ent.getMaxHealth();
                float healthPercent = RenderUtils.clamp(health / maxHealth, 0f, 1f);

                RenderUtils.drawRectBound(31, 15f, width - 4, 10f, RenderUtils.reAlpha(new Color(0x000000).getRGB(), 0.35f));

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
                boolean speed = ent.getActivePotionEffect(Potion.moveSpeed) != null;

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
                if (speed)
                    partIndex[6] = 0x76E2A9;

                for (int i = 0; i < 7; i++)
                    Main.INSTANCE.fontLoaders.get("targethub18").drawStringWithShadow(parts[i], 30f + (i * (Main.INSTANCE.fontLoaders.get("targethub18").getStringWidth(parts[i]) + 1)), 30f, partIndex[i]);

                RenderUtils.drawRoundedRect(32, 16f, this.animation, 3f, 1, RenderUtils.reAlpha(RenderUtils.darker(heal ? healcol : col, 25), 0.95f));

                if (!heal) RenderUtils.drawRoundedRect(32, 16f, drawPercent, 3f, 1, RenderUtils.reAlpha(col, 0.95f));

                RenderUtils.drawRoundedRect(32, 20f, width - 6, 5f, 1, RenderUtils.reAlpha(new Color(0x000000).getRGB(), 0.5f));
                RenderUtils.drawRoundedRect(33, 21f, f3, 3f, 0, 0xff4286f5);

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
            } else if (mode.getCurrentMode().equals("Fancy")) {
                RenderUtils.drawRect(0, 0, 30 + width, 30, new Color(0, 0, 0, 160).getRGB());
                fontRenderer.drawString(playerName, 30f, 2f, new Color(0xffffff).getRGB());
                fontRenderer2.drawString(healthStr, 28 + width - fontRenderer2.getStringWidth(healthStr) - 2, 2f, 0xffcccccc);


                boolean isNaN = Float.isNaN(ent.getHealth());
                float health = isNaN ? 20 : ent.getHealth();
                float maxHealth = isNaN ? 20 : ent.getMaxHealth();
                float healthPercent = RenderUtils.clamp(health / maxHealth, 0f, 1f);


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


                RenderUtils.drawRect(32, 16, 32 + this.animation, 18, healcol);

                RenderUtils.drawRect(32, 21f, width + 26, 23f, RenderUtils.reAlpha(new Color(0x000000).getRGB(), 0.5f));
                RenderUtils.drawRect(33, 21f, 33 + f3, 23f, 0xff4286f5);


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
}
