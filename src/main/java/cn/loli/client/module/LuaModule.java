package cn.loli.client.module;

import cn.loli.client.events.*;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.util.FrameTimer;
import org.luaj.vm2.Globals;

public class LuaModule extends Module {

    //TODO : 补全更多的方法

    public Globals globals;

    public LuaModule(String name, String description, Globals globals) {
        super(name, description, ModuleCategory.LUA);
        this.globals = globals;
        globals.get("init").call();
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        invoke("on_update");
    }

    @EventTarget
    private void onUpdate(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            invoke("on_pre_update");
        } else {
            invoke("on_post_update");
        }

    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (event.getEventType() == EventType.SEND) {
            invoke("on_packet_post");
        } else {
            invoke("on_packet_receive");
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        invoke("on_tick");
    }

    @EventTarget
    private void onRender(RenderEvent event) {
        invoke("on_render");
    }

    @EventTarget
    private void onRender(Render2DEvent event) {
        invoke("on_render_2d");
    }

    @EventTarget
    private void onRender(RenderSREvent event) {
        invoke("on_render_sr");
    }

    @EventTarget
    private void onBlockReach(BlockReachEvent event) {
        invoke("on_block_reach");
    }

    @EventTarget
    private void onCollision(CollisionEvent event) {
        invoke("on_collision_block");
    }

    @EventTarget
    private void onMouseOver(MouseOverEvent event) {
        invoke("on_mouse_over");
    }

    @EventTarget
    private void onMoveFly(MoveFlyEvent event) {
        invoke("on_move_fly");
    }

    @EventTarget
    private void onJump(JumpYawEvent event) {
        invoke("on_jump_yaw");
    }

    @EventTarget
    private void onChat(ChatEvent event) {
        invoke("on_chat");
    }

    @EventTarget
    private void onKey(KeyEvent event) {
        invoke("on_key");
    }

    @EventTarget
    private void onMove(PlayerMoveEvent event) {
        invoke("on_move");
    }

    @EventTarget
    private void onAttack(AttackEvent event) {
        invoke("on_attack");
    }

    @EventTarget
    private void onJump(JumpEvent event) {
        invoke("on_jump");
    }


    @Override
    public void onDisable() {
        invoke("on_disable");
    }

    @Override
    public void onEnable() {
        invoke("on_enable");
    }


    protected void invoke(String method) {
        try {
            if (!globals.get(method).isnil())
                globals.get(method).invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
