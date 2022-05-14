package cn.loli.client.module.modules.render;

import cn.loli.client.events.Render2DEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.RenderUtils;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.ColorProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import java.awt.*;

public class ItemESP extends Module {

    private final BooleanProperty box = new BooleanProperty("2D Box", false);
    private final BooleanProperty cylinder = new BooleanProperty("Cylinder", false);
    private final BooleanProperty cylinderCull = new BooleanProperty("CylinderCull", false);


    private final BooleanProperty chestDisplay = new BooleanProperty("Chest", false);
    private final BooleanProperty itemDisplay = new BooleanProperty("Item", false);

    private final ColorProperty item = new ColorProperty("Item-Color", new Color(150, 148, 148, 150));
    private final ColorProperty chest = new ColorProperty("Chest-Color", new Color(150, 148, 148, 150));


    public ItemESP() {
        super("Item ESP", "You can see items through walls", ModuleCategory.RENDER);
    }

    private final IEventListener<RenderEvent> onRender = event ->
    {
        if (cylinder.getPropertyValue()) {
            if (itemDisplay.getPropertyValue()) {
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityItem) {
                        final float partialTicks = ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
                        final double x = ((entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX());
                        final double y = ((entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY());
                        final double z = ((entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks) - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ());

                        GL11.glPushMatrix();
                        GL11.glTranslated(x, y + entity.height, z);
                        GL11.glNormal3d(0.0, 1.0, 0.0);
                        GL11.glRotated(90, 1, 0, 0);

                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_BLEND);
                        if (cylinderCull.getPropertyValue())
                            GL11.glDisable(GL11.GL_CULL_FACE);

                        GL11.glDisable(GL11.GL_DEPTH_TEST);

                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glLineWidth(1);
                        RenderUtils.color(item.getPropertyValue().getRGB());
                        final Cylinder cylinder = new Cylinder();

                        cylinder.setDrawStyle(GLU.GLU_LINE);
                        cylinder.setOrientation(GLU.GLU_INSIDE);
                        cylinder.draw(0.62f, 0.62f, entity.height, 8, 1);
                        RenderUtils.color(RenderUtils.reAlpha(item.getPropertyValue().getRGB(), chest.getPropertyValue().getAlpha() / 255f));
                        cylinder.setDrawStyle(GLU.GLU_FILL);
                        cylinder.setOrientation(GLU.GLU_INSIDE);
                        cylinder.draw(0.62f, 0.65f, entity.height, 8, 1);

                        GL11.glDisable(GL11.GL_BLEND);
                        if (cylinderCull.getPropertyValue())
                            GL11.glEnable(GL11.GL_CULL_FACE);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glPopMatrix();
                    }
                }
            }


            if (chestDisplay.getPropertyValue()) {
                for (TileEntity entity : mc.theWorld.loadedTileEntityList) {
                    if (entity instanceof TileEntityChest || entity instanceof TileEntityEnderChest) {
                        mc.theWorld.getBlockState(entity.getPos()).getBlock();
                        final double x = (entity.getPos().getX() - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX());
                        final double y = (entity.getPos().getY() - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY());
                        final double z = (entity.getPos().getZ() - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ());
                        GL11.glPushMatrix();
                        GL11.glTranslated(x + 0.5, y + 0.9, z + 0.5);
                        GL11.glRotated(90, 1, 0, 0);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_BLEND);
                        if (cylinderCull.getPropertyValue())
                            GL11.glDisable(GL11.GL_CULL_FACE);

                        GL11.glDisable(GL11.GL_DEPTH_TEST);

                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glLineWidth(1);

                        RenderUtils.color(chest.getPropertyValue().getRGB());
                        final Cylinder cylinder = new Cylinder();

                        cylinder.setDrawStyle(GLU.GLU_LINE);
                        cylinder.setOrientation(GLU.GLU_INSIDE);
                        cylinder.draw(0.62f, 0.62f, 0.9f, 8, 1);

                        RenderUtils.color(RenderUtils.reAlpha(chest.getPropertyValue().getRGB(), chest.getPropertyValue().getAlpha() / 255f));

                        cylinder.setDrawStyle(GLU.GLU_FILL);
                        cylinder.setOrientation(GLU.GLU_INSIDE);
                        cylinder.draw(0.62f, 0.65f, 0.9f, 8, 1);

                        GL11.glDisable(GL11.GL_BLEND);
                        if (cylinderCull.getPropertyValue())
                            GL11.glEnable(GL11.GL_CULL_FACE);

                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glPopMatrix();

                    }
                }
            }
        }
    };



}
