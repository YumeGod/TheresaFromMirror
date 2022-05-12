

package dev.xix.feature.impl.combat;

import cn.loli.client.events.UpdateEvent;
import dev.xix.feature.module.AbstractTheresaModule;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import dev.xix.event.bus.IEventListener;
import dev.xix.feature.module.TheresaModuleCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;


public class AimBot extends AbstractTheresaModule {
    private final BooleanValue playersOnly = new BooleanValue("PlayersOnly", true);
    private final BooleanValue weaponOnly = new BooleanValue("WeaponOnly", false);
    private final BooleanValue verticalAim = new BooleanValue("VerticalAim", true);
    private final NumberValue<Float> range = new NumberValue<>("Range", 3.0f, 1.0f, 10.0f);
    private final NumberValue<Float> maxYaw = new NumberValue<>("MaxAngleHorizontal", 180f, 0f, 180f);
    private final NumberValue<Float> maxPitch = new NumberValue<>("MaxAngleVertical", 180f, 0f, 180f);

    public AimBot() {
        super("AimBot", TheresaModuleCategory.COMBAT);
    }

    private final IEventListener<UpdateEvent> updateEventIEventListener = event ->
    {
        if (!mc.thePlayer.isEntityAlive() || mc.thePlayer == null) return;
        if (weaponOnly.getObject() && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemAxe))
            return;

        if (playersOnly.getObject()) {
            faceEntity(mc.theWorld.playerEntities.stream().filter(entityPlayer -> entityPlayer != mc.thePlayer
                            && entityPlayer.isEntityAlive() && mc.thePlayer.getDistanceToEntity(entityPlayer) < range.getObject())
                    .min((o1, o2) -> Float.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2))).orElse(null));
        } else {
            faceEntity((EntityLivingBase) mc.theWorld.loadedEntityList.stream().filter(entity -> entity != mc.thePlayer
                            && entity instanceof EntityLivingBase && entity.isEntityAlive() && mc.thePlayer.getDistanceToEntity(entity) < range.getObject())
                    .min((o1, o2) -> Float.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2))).orElse(null));
        }
    };

    private synchronized void faceEntity(EntityLivingBase entity) {
        if (verticalAim.getObject() && Math.abs(rotationUtils.getYaw(entity) - mc.thePlayer.rotationPitch) <= maxPitch.getObject() && Math.abs(rotationUtils.getYaw(entity) - mc.thePlayer.rotationYaw) < maxYaw.getObject())
            mc.thePlayer.rotationPitch = rotationUtils.getPitch(entity);

        if (Math.abs(rotationUtils.getYaw(entity) - mc.thePlayer.rotationYaw) < maxYaw.getObject())
            mc.thePlayer.rotationYaw = rotationUtils.getYaw(entity);
    }
}
