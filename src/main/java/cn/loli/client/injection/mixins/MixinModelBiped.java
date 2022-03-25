package cn.loli.client.injection.mixins;


import cn.loli.client.events.EmoteEvent;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.model.ModelBiped;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

    @Inject(method = {"setRotationAngles"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;heldItemRight:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void setPreEmote(CallbackInfo ci) {
        EventManager.call(new EmoteEvent((ModelBiped) (Object) this, EventType.PRE));
    }

    @Inject(method = {"setRotationAngles"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBiped;copyModelAngles(Lnet/minecraft/client/model/ModelRenderer;Lnet/minecraft/client/model/ModelRenderer;)V", shift = At.Shift.BEFORE))
    private void setPostEmote(CallbackInfo ci) {
        EventManager.call(new EmoteEvent((ModelBiped) (Object) this, EventType.POST));
    }

}
