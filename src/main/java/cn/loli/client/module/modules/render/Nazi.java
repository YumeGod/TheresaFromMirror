package cn.loli.client.module.modules.render;

import cn.loli.client.events.EmoteEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.model.ModelBiped;

public class Nazi extends Module {
    public int heilY;

    public Nazi() {
        super("Loli", "Loli Loli", ModuleCategory.RENDER);
    }


    @Override
    public void onEnable() {
        heilY = 0;
    }


    @EventTarget
    private void onEmote(EmoteEvent event){
        if (event.getEventType() == EventType.POST){
            if (event.entity == mc.thePlayer){
                setBiped(event.getBiped());
            }
        }
    }


    public void setBiped(ModelBiped biped) {
        if (mc.gameSettings.thirdPersonView > 0) {
            biped.bipedRightArm.rotateAngleX = 0.5F;
            biped.bipedRightArm.rotateAngleY = -2.25F;
            biped.bipedLeftArm.rotateAngleX = 0.5F;
            biped.bipedLeftArm.rotateAngleY = 2.25F;
            biped.aimedBow = true;
            biped.isChild = true;
        }
    }

}
