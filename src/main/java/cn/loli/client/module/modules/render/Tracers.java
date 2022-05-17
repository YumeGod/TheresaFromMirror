

package cn.loli.client.module.modules.render;

import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.Utils;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.ColorProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tracers extends Module {
    private final BooleanProperty players = new BooleanProperty("Players", true);
    private final BooleanProperty animals = new BooleanProperty("Animals", true);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", true);
    private final BooleanProperty children = new BooleanProperty("Babies", true);
    private final BooleanProperty villagers = new BooleanProperty("Villagers", true);
    private final NumberProperty<Integer> range = new NumberProperty<>("Range", 64, 2, 256 , 1);
    private final ColorProperty playerColor = new ColorProperty("PlayerColor", Color.RED);
    private final ColorProperty animalColor = new ColorProperty("AnimalColor", Color.BLUE);
    private final ColorProperty mobColor = new ColorProperty("MobColor", Color.YELLOW);
    private final ColorProperty childColor = new ColorProperty("ChildColor", Color.GREEN);
    private final ColorProperty villagerColor = new ColorProperty("VillagerColor", Color.MAGENTA);
    private final BooleanProperty opacityBasedOnDistance = new BooleanProperty("OpacityBasedOnDistance", false);

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
                if ((entity instanceof EntityPlayer && players.getPropertyValue())
                        || (entity instanceof IAnimals && !(entity instanceof INpc) && !(entity instanceof IMob) && animals.getPropertyValue())
                        || (entity instanceof IMob && mobs.getPropertyValue())
                        || (entity instanceof INpc && villagers.getPropertyValue())) {
                    if (Utils.isEntityChild(entity) && !children.getPropertyValue())
                        continue;

                    float distance = mc.getRenderViewEntity().getDistanceToEntity(entity);
                    if (distance > range.getPropertyValue()) continue;

                    double posX = ((entity.lastTickPosX + (entity.posX - entity.lastTickPosX) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX()));
                    double posY = ((entity.lastTickPosY + (entity.posY - entity.lastTickPosY) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY()));
                    double posZ = ((entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ()));

                    Color color;

                    if (entity instanceof EntityPlayer) color = playerColor.getPropertyValue();
                    else if (Utils.isEntityChild(entity)) color = childColor.getPropertyValue();
                    else if (entity instanceof INpc) color = villagerColor.getPropertyValue();
                    else if (entity instanceof IMob) color = mobColor.getPropertyValue();
                    else color = animalColor.getPropertyValue();

                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
                            opacityBasedOnDistance.getPropertyValue() ? 1 - distance / (range.getPropertyValue() / 2f + range.getPropertyValue() / 4f) : color.getAlpha() / 255f);

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
