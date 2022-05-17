package cn.loli.client.module.modules.render;


import cn.loli.client.events.CameraEvent;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.events.Render3DEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.RenderUtils;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.ColorProperty;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.player.EntityPlayer;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.gui.Gui.drawRect;

public class ESP extends Module {

    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<>();
    private final ArrayList<PlayerLocationInfo> playerLocationInfo = new ArrayList<>();

    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);

    private final BooleanProperty box = new BooleanProperty("2D Box", false);
    private final BooleanProperty healthbar = new BooleanProperty("Health Bar", false);
    private final BooleanProperty nametags = new BooleanProperty("Name tags", false);
    private final BooleanProperty invis = new BooleanProperty("Ignore Invis", false);
    private final BooleanProperty icarus = new BooleanProperty("Icarus", false);

    public final BooleanProperty chams = new BooleanProperty("Chams", false);

    public final ColorProperty boxColor = new ColorProperty("Box-Color", new Color(239, 235, 235, 210));
    public final ColorProperty icarusColor = new ColorProperty("Icarus-Color", new Color(147, 144, 144, 210));

    public final ColorProperty chamsColor = new ColorProperty("Cham-Default-Color", new Color(187, 21, 21, 210));
    public final ColorProperty throughWallsColor = new ColorProperty("Cham-ThroughWalls-Color", new Color(23, 74, 183, 210));


    //TODO: ESP Overlay

    int rainbowOffset;

    public ESP() {
        super("ESP", "Make you able see others in your view", ModuleCategory.RENDER);
    }

    private final IEventListener<Render2DEvent> onRenderSR = event ->
    {
        ScaledResolution res = new ScaledResolution(mc);

        for (EntityPlayer player : entityPosMap.keySet()) {

            GL11.glPushMatrix();
            mc.entityRenderer.setupOverlayRendering();
            float[] positions = entityPosMap.get(player);
            float x = positions[0];
            float x2 = positions[1];
            float y = positions[2];
            float y2 = positions[3];

            if (healthbar.getPropertyValue()) {
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
            if (nametags.getPropertyValue()) {
                String text = "(" + EnumChatFormatting.GOLD + Math.round(mc.thePlayer.getDistanceToEntity(player)) + "m" + EnumChatFormatting.RESET + ")" + player.getDisplayName().getUnformattedText();
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
            if (box.getPropertyValue()) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                enableAlpha();
                RenderUtils.color(boxColor.getPropertyValue().getRGB());
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
    };

    private final IEventListener<Render3DEvent> onRender3D = event ->
    {
        ScaledResolution res = new ScaledResolution(mc);
        if (!entityPosMap.isEmpty())
            entityPosMap.clear();

        if (box.getPropertyValue() || healthbar.getPropertyValue() || nametags.getPropertyValue()) {
            int scaleFactor = res.getScaleFactor();
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (player.getDistanceToEntity(mc.thePlayer) < 1.0F || (player.isInvisible() && invis.getPropertyValue()))
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

        for (EntityPlayer player : entityPosMap.keySet())
            if (icarus.getPropertyValue()) RenderUtils.drawIcarusESP(player, icarusColor.getPropertyValue(), false);

    };


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


    private Vec3 getVec3(final EntityPlayer var0) {
        final float timer = ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
        final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
        final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
        final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
        return new Vec3(x, y, z);
    }


    public static int getColorFromPercentage(float current, float max) {
        float percentage = (current / max) / 3;
        return Color.HSBtoRGB(percentage, 1.0F, 1.0F);
    }

    static class PlayerLocationInfo {
        private final double[] position;
        private final long timestamp;

        public PlayerLocationInfo(double[] position, final long timestamp) {
            this.position = position;
            this.timestamp = timestamp;
        }
    }
}
