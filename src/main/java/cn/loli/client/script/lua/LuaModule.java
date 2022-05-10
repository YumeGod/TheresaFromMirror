package cn.loli.client.script.lua;

import cn.loli.client.events.*;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;
import dev.xix.event.EventType;
import org.luaj.vm2.Globals;

import java.util.ArrayList;
import java.util.List;

public class LuaModule extends Module {

    //TODO : 补全更多的方法

    public Globals globals;
    private final List<String> blacklist;

    public LuaModule(String name, String description, Globals globals) {
        super(name, description, ModuleCategory.LUA);
        this.globals = globals;
        blacklist = new ArrayList<>();

        if (!globals.get("init").isnil())
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

    @EventTarget
    private void onEmote(EmoteEvent event) {
        invoke("on_emote");
    }

    @EventTarget
    private void onAnimation(AnimationEvent event) {
        invoke("on_animation");
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
            if (!globals.get(method).isnil()
                    && !blacklist.contains(method))
                globals.get(method).invoke();
            else
                blacklist.add(method);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
