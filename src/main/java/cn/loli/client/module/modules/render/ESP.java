package cn.loli.client.module.modules.render;


import cn.loli.client.events.Render2DEvent;
import cn.loli.client.events.Render3DEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.RenderSREvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.RenderUtils;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.gui.Gui.*;

public class ESP extends Module {

    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<>();
    private static Map<EntityPlayer, float[][]> entities = new HashMap<>();

    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);

    private final BooleanValue box = new BooleanValue("2D Box", false);
    private final BooleanValue healthbar = new BooleanValue("Health Bar", false);
    private final BooleanValue nametags = new BooleanValue("Name tags", false);


    public ESP() {
        super("ESP", "Make you able see others in your view", ModuleCategory.RENDER);
    }


    @EventTarget
    private void onRenderSR(Render2DEvent e) {
        ScaledResolution res = new ScaledResolution(mc);

        for (EntityPlayer player : entityPosMap.keySet()) {
            GL11.glPushMatrix();
            mc.entityRenderer.setupOverlayRendering();
            float[] positions = entityPosMap.get(player);
            float x = positions[0];
            float x2 = positions[1];
            float y = positions[2];
            float y2 = positions[3];

            if (healthbar.getObject()) {
                drawRect((int) (x - 2.5), (int) (y - 0.5F), (int) (x - 0.5F), (int) (y2 + 0.5F), 0x96000000);

                float health = player.getHealth();
                float maxHealth = player.getMaxHealth();
                float healthPercentage = health / maxHealth;

                boolean needScissor = health < maxHealth;
                float heightDif = y - y2;
                float healthBarHeight = heightDif * healthPercentage;

                if (needScissor)
                    startScissorBox(res, (int) x - 2, (int) (y2 + healthBarHeight), 2, (int) -healthBarHeight + 1);
                int col = getColorFromPercentage(health, maxHealth);
                RenderUtils.drawRect((int) x - 2, (int) y, (int) x - 1, (int) y2, col);

                if (needScissor)
                    endScissorBox();
            }
            if (nametags.getObject()) {
                String text = "(" + EnumChatFormatting.GOLD + Math.round(mc.thePlayer.getDistanceToEntity(player)) + "m" + EnumChatFormatting.RESET + ")"+ player.getDisplayName().getUnformattedText();
                float xDif = x2 - x;
                float minScale = 0.65F;
                float scale = Math.max(minScale,
                        Math.min(1.0F, 1.0F - (mc.thePlayer.getDistanceToEntity(player) / 100.0F)));
                float yOff = Math.max(0.0F,
                        Math.min(1.0F, mc.thePlayer.getDistanceToEntity(player) / 12.0F));
                float upscale = 1.0F / scale;
                GL11.glPushMatrix();
                GL11.glScalef(scale, scale, scale);
                mc.fontRendererObj.drawStringWithShadow(text, (x + xDif / 2.0F) * upscale - mc.fontRendererObj.getStringWidth(text) / 2.0F, (y - 9 + yOff) * upscale, -1);


                GL11.glScalef(1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }
            if (box.getObject()) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                enableAlpha();
                GL11.glColor4f(255F, 0F, 0F, 1.0F);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                GL11.glVertex2f(x, y);
                GL11.glVertex2f(x, y2);
                GL11.glVertex2f(x2, y2);
                GL11.glVertex2f(x2, y);
                GL11.glEnd();
                disableAlpha();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            GL11.glPopMatrix();
        }
    }

    @EventTarget
    private void onRender3D(Render3DEvent event) {
        entities.keySet().removeIf(player -> !mc.theWorld.playerEntities.contains(player));
        ScaledResolution res = new ScaledResolution(mc);

        if (!entityPosMap.isEmpty())
            entityPosMap.clear();

        if (box.getObject() || healthbar.getObject() || nametags.getObject()) {
            int scaleFactor = res.getScaleFactor();
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (player.getDistanceToEntity(mc.thePlayer) < 1.0F)
                    continue;

                GL11.glPushMatrix();
                Vec3 vec3 = getVec3(player);
                float posX = (float) (vec3.xCoord - mc.getRenderManager().viewerPosX);
                float posY = (float) (vec3.yCoord - mc.getRenderManager().viewerPosY);
                float posZ = (float) (vec3.zCoord - mc.getRenderManager().viewerPosZ);

                double halfWidth = player.width / 2.0D + 0.18F;
                AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
                        posY + player.height + 0.18D, posZ + halfWidth);

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

                entityPosMap.put(player, new float[]{position.x, position.z, position.y, position.w});

                GL11.glPopMatrix();
            }
        }

    }


    public static void startScissorBox(ScaledResolution sr, int x, int y, int width, int height) {
        int sf = sr.getScaleFactor();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * sf, (sr.getScaledHeight() - (y + height)) * sf, width * sf, height * sf);
    }

    public static void endScissorBox() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void enableAlpha() {
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
    }

    public static void disableAlpha() {
        GL11.glDisable(GL11.GL_BLEND);
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

    public static void addEntity(final EntityPlayer e, final ModelPlayer model) {
        entities.put(e, new float[][]{
                {model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ},
                {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY,
                        model.bipedRightArm.rotateAngleZ},
                {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ},
                {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY,
                        model.bipedRightLeg.rotateAngleZ},
                {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY,
                        model.bipedLeftLeg.rotateAngleZ}});
    }


    private Vec3 getVec3(final EntityPlayer var0) {
        final float timer = ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
        final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
        final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
        final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
        return new Vec3(x, y, z);
    }

    private void startEnd(final boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GlStateManager.blendFunc(770, 771);
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(!revert);
    }

    public static int getColorFromPercentage(float current, float max) {
        float percentage = (current / max) / 3;
        return Color.HSBtoRGB(percentage, 1.0F, 1.0F);
    }


    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawFilledBoundingBox(final AxisAlignedBB box) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
        worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
        worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        tessellator.draw();
    }


    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
