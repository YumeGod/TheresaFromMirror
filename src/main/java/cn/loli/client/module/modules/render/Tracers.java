

package cn.loli.client.module.modules.render;

import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.Utils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ColorValue;
import cn.loli.client.value.NumberValue;

import dev.xix.event.bus.IEventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tracers extends Module {
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue animals = new BooleanValue("Animals", true);
    private final BooleanValue mobs = new BooleanValue("Mobs", true);
    private final BooleanValue children = new BooleanValue("Babies", true);
    private final BooleanValue villagers = new BooleanValue("Villagers", true);
    private final NumberValue<Integer> range = new NumberValue<>("Range", 64, 2, 256);
    private final ColorValue playerColor = new ColorValue("PlayerColor", Color.RED);
    private final ColorValue animalColor = new ColorValue("AnimalColor", Color.BLUE);
    private final ColorValue mobColor = new ColorValue("MobColor", Color.YELLOW);
    private final ColorValue childColor = new ColorValue("ChildColor", Color.GREEN);
    private final ColorValue villagerColor = new ColorValue("VillagerColor", Color.MAGENTA);
    private final BooleanValue opacityBasedOnDistance = new BooleanValue("OpacityBasedOnDistance", false);

    public Tracers() {
        super("Tracers", "Draws a line to other entities.", ModuleCategory.RENDER);
    }

    /**
     * Credit: Xdolf by DarkCart
     */
    private final IEventListener<RenderEvent> onRender = event -> {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.5F);
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != mc.thePlayer && entity != null) {
                if ((entity instanceof EntityPlayer && players.getObject())
                        || (entity instanceof IAnimals && !(entity instanceof INpc) && !(entity instanceof IMob) && animals.getObject())
                        || (entity instanceof IMob && mobs.getObject())
                        || (entity instanceof INpc && villagers.getObject())) {
                    if (Utils.isEntityChild(entity) && !children.getObject())
                        continue;

                    float distance = mc.getRenderViewEntity().getDistanceToEntity(entity);
                    if (distance > range.getObject()) continue;

                    double posX = ((entity.lastTickPosX + (entity.posX - entity.lastTickPosX) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX()));
                    double posY = ((entity.lastTickPosY + (entity.posY - entity.lastTickPosY) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY()));
                    double posZ = ((entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ()));

                    Color color;

                    if (entity instanceof EntityPlayer) color = playerColor.getObject();
                    else if (Utils.isEntityChild(entity)) color = childColor.getObject();
                    else if (entity instanceof INpc) color = villagerColor.getObject();
                    else if (entity instanceof IMob) color = mobColor.getObject();
                    else color = animalColor.getObject();

                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
                            opacityBasedOnDistance.getObject() ? 1 - distance / (range.getObject() / 2f + range.getObject() / 4f) : color.getAlpha() / 255f);

                    Vec3 eyes = new Vec3(0d, 0d, 1d).rotatePitch(-(float) Math.toRadians(mc.thePlayer.rotationPitch)).rotateYaw(-(float) Math.toRadians(mc.thePlayer.rotationYaw));

                    GL11.glBegin(GL11.GL_LINE_LOOP);

                    GL11.glVertex3d(eyes.xCoord, mc.thePlayer.getEyeHeight() + eyes.yCoord, eyes.zCoord);
                    GL11.glVertex3d(posX, posY, posZ);

                    GL11.glEnd();
                }
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    };

}
