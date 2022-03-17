package cn.loli.client.script.js;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import javax.script.Invocable;

public class JSModule extends Module {
    private final Invocable invocable;

    private final String name;

    public JSModule(String name, String description, Invocable source) {
        super(name, description, ModuleCategory.LUA);
        this.invocable = source;
        this.name = name;
        invoke("init");
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


    private void invoke(final String funcName, final Object... args) {
        try {
            this.invocable.invokeFunction(funcName, args);
        } catch (final NoSuchMethodException ignored) {
            Main.INSTANCE.println("[JS] " + this.name + ": " + funcName + " not found");
        } catch (final Exception e) {
            Main.INSTANCE.println("[JS] " + this.name + ": " + funcName + " error");
        }
    }
}
