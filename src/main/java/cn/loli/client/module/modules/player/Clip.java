package cn.loli.client.module.modules.player;

import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.MoveUtils;
import com.darkmagician6.eventapi.EventTarget;

public class Clip extends Module {
    public Clip() {
        super("Clip", "Make you able to go clip", ModuleCategory.PLAYER);
    }

    @Override
    protected void onEnable(){
        super.onEnable();
    }

    @Override
    protected void onDisable(){
        super.onDisable();
    }

    @EventTarget
    private void onUpdate(UpdateEvent e){
        if (mc.thePlayer != null)
            mc.thePlayer.setPosition(mc.thePlayer.posX , mc.thePlayer.posY , mc.thePlayer.posZ);
    }
}
