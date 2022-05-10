package cn.loli.client.injection.mixins;


import cn.loli.client.events.EmoteEvent;

import com.darkmagician6.eventapi.types.EventType;
import dev.xix.TheresaClient;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

    @Inject(method = {"setRotationAngles"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;heldItemRight:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void setPreEmote(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        TheresaClient.getInstance().getEventBus().call(new EmoteEvent((ModelBiped) (Object) this, entityIn, EventType.PRE));
    }

    @Inject(method = {"setRotationAngles"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBiped;copyModelAngles(Lnet/minecraft/client/model/ModelRenderer;Lnet/minecraft/client/model/ModelRenderer;)V", shift = At.Shift.BEFORE))
    private void setPostEmote(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        TheresaClient.getInstance().getEventBus().call(new EmoteEvent((ModelBiped) (Object) this, entityIn, EventType.POST));
    }

}
