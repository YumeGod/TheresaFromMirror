package cn.loli.client.module.modules.player;

import cn.loli.client.events.SafeWalkEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;

public class SafeWalk extends Module {

    public SafeWalk() {
        super("Safe Walk", "Make you dont fall down in the edge of blocks", ModuleCategory.PLAYER);
    }

    @Override
    protected void onEnable(){
        
    }

    @Override
    protected void onDisable(){
        
    }

    @EventTarget
    private void onSafe(SafeWalkEvent event){
        event.setSafe(true);
    }
}
