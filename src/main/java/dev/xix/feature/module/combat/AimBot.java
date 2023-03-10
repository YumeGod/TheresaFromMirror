

package dev.xix.feature.module.combat;

import cn.loli.client.events.UpdateEvent;
import dev.xix.event.bus.IEventListener;
import dev.xix.feature.module.AbstractTheresaModule;
import dev.xix.feature.module.TheresaModuleCategory;
import dev.xix.gui.element.HorizontalAlignment;
import dev.xix.gui.element.VerticalAlignment;
import dev.xix.gui.element.impl.NodeElement;
import dev.xix.gui.element.impl.QuadElement;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;

import java.awt.*;


public class AimBot extends AbstractTheresaModule {
    private final BooleanProperty playersOnly = new BooleanProperty("PlayersOnly", true);
    private final BooleanProperty weaponOnly = new BooleanProperty("WeaponOnly", false);
    private final BooleanProperty verticalAim = new BooleanProperty("VerticalAim", true);
    private final NumberProperty<Float> range = new NumberProperty<>("Range", 3.0f, 1.0f, 10.0f, 0.1f);
    private final NumberProperty<Float> maxYaw = new NumberProperty<>("MaxAngleHorizontal", 180f, 0f, 180f, 1f);
    private final NumberProperty<Float> maxPitch = new NumberProperty<>("MaxAngleVertical", 180f, 0f, 180f, 1f);

    public AimBot() {
        super("AimBot", TheresaModuleCategory.COMBAT);
        // Example To Add Elements
        this.addElements(new QuadElement("example", 0, 0, 50, 50, Color.WHITE.getRGB()).setHAlignment(HorizontalAlignment.CENTER).setVAlignment(VerticalAlignment.CENTER));
    }

    private final IEventListener<UpdateEvent> updateEventIEventListener = event ->
    {
        if (!mc.thePlayer.isEntityAlive() || mc.thePlayer == null) return;
        if (weaponOnly.getPropertyValue() && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemAxe))
            return;

        if (playersOnly.getPropertyValue()) {
            faceEntity(mc.theWorld.playerEntities.stream().filter(entityPlayer -> entityPlayer != mc.thePlayer
                            && entityPlayer.isEntityAlive() && mc.thePlayer.getDistanceToEntity(entityPlayer) < range.getPropertyValue())
                    .min((o1, o2) -> Float.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2))).orElse(null));
        } else {
            faceEntity((EntityLivingBase) mc.theWorld.loadedEntityList.stream().filter(entity -> entity != mc.thePlayer
                            && entity instanceof EntityLivingBase && entity.isEntityAlive() && mc.thePlayer.getDistanceToEntity(entity) < range.getPropertyValue())
                    .min((o1, o2) -> Float.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2))).orElse(null));
        }
    };

    private synchronized void faceEntity(EntityLivingBase entity) {
        if (verticalAim.getPropertyValue() && Math.abs(rotationUtils.getYaw(entity) - mc.thePlayer.rotationPitch) <= maxPitch.getPropertyValue() && Math.abs(rotationUtils.getYaw(entity) - mc.thePlayer.rotationYaw) < maxYaw.getPropertyValue())
            mc.thePlayer.rotationPitch = rotationUtils.getPitch(entity);

        if (Math.abs(rotationUtils.getYaw(entity) - mc.thePlayer.rotationYaw) < maxYaw.getPropertyValue())
            mc.thePlayer.rotationYaw = rotationUtils.getYaw(entity);
    }
}
