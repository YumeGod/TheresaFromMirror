package cn.loli.client.module.modules.render;

import cn.loli.client.events.EmoteEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.model.ModelBiped;

public class Nazi extends Module {
    public int heilY;
    private TimeHelper timer = new TimeHelper();

    public Nazi() {
        super("Nazi", "Yes Yes So nazi", ModuleCategory.RENDER);
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
            biped.bipedHead.rotateAngleX = 0F;
            biped.bipedHead.rotateAngleY = 0;
            biped.bipedRightArm.rotateAngleX = 2.7F;
            biped.bipedRightArm.rotateAngleY = -0.25F;
            biped.bipedLeftArm.rotateAngleX = 2.7F;
            biped.bipedLeftArm.rotateAngleY = 0.25F;
        }
    }

}
