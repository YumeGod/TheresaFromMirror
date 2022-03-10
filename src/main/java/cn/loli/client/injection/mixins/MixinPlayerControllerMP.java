package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.BlockReachEvent;
import cn.loli.client.injection.implementations.IPlayerControllerMP;
import cn.loli.client.module.modules.combat.Criticals;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP implements IPlayerControllerMP {

    @Shadow
    private float curBlockDamageMP;

    @Shadow
    private int blockHitDelay;


    @Inject(method = "attackEntity",  at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V", shift = At.Shift.AFTER))
    private void attackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci){
    }

    @Override
    public void setCurBlockDamageMP(float f) {
        curBlockDamageMP = f;
    }

    @Override
    public float getCurBlockDamageMP() {
        return curBlockDamageMP;
    }

    @Override
    public void setBlockHitDelay(int f) {
        blockHitDelay = f;
    }

    /**
     * @author Loli
     */
    @Overwrite
    public float getBlockReachDistance()
    {
        final BlockReachEvent blockReachEvent = new BlockReachEvent(Minecraft.getMinecraft().playerController.getCurrentGameType().isCreative() ? 5.0F : 4.5F);
        EventManager.call(blockReachEvent);
        return blockReachEvent.getRange();
    }


}
