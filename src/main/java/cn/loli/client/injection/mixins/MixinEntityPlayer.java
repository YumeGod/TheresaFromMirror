package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.module.modules.combat.KeepSprint;
import cn.loli.client.module.modules.render.OldAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {
    private float currentHeight = 1.62F;
    private long lastMillis = System.currentTimeMillis();

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    /**
     * @author 65_7a
     * @reason sneaking animation
     */
    @Overwrite
    public float getEyeHeight() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (Main.INSTANCE.moduleManager.getModule(OldAnimations.class).getState() && Main.INSTANCE.moduleManager.getModule(OldAnimations.class).oldSneaking.getObject()) {
            int timeDelay = 1000 / 60;

            if (player.isSneaking()) {
                float sneakingHeight = 1.54F;

                if (currentHeight > sneakingHeight) {
                    long time = System.currentTimeMillis();
                    long timeSinceLastChance = time - lastMillis;

                    if (timeSinceLastChance > timeDelay) {
                        currentHeight -= 0.012F;
                        lastMillis = time;
                    }
                }
            } else {
                float standingHeight = 1.62F;

                if (currentHeight < standingHeight && currentHeight > 0.2F) {
                    long time = System.currentTimeMillis();
                    long timeSinceLastChange = time - lastMillis;

                    if (timeSinceLastChange > timeDelay) {
                        currentHeight += 0.012F;
                        lastMillis = time;
                    }
                } else {
                    currentHeight = 1.62F;
                }
            }

            if (player.isPlayerSleeping()) {
                currentHeight = 0.2F;
            }

            return currentHeight;
        } else {
            float eyeHeight = 1.62F;

            if (player.isPlayerSleeping()) {
                eyeHeight = 0.2F;
            }

            if (player.isSneaking()) {
                eyeHeight -= 0.08F;
            }

            return eyeHeight;
        }
    }


    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setSprinting(Z)V", shift = At.Shift.AFTER), cancellable = true)
    private void attackEntity(Entity targetEntity, CallbackInfo ci) {
        if (Main.INSTANCE.moduleManager.getModule(KeepSprint.class).getState()){
            this.motionX /= 0.6D;
            this.motionZ /= 0.6D;
            this.setSprinting(true);
        }

    }

}
