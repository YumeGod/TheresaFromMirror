package cn.loli.client.script.js;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import javax.script.Invocable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JSModule extends Module {
    private final Invocable invocable;

    private final String name;
    private final List<String> blacklist;

    public JSModule(String name, String description, Invocable source) {
        super(name, description, ModuleCategory.LUA);
        this.invocable = source;
        this.name = name;
        blacklist = new ArrayList<>();
        invoke("init");
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        invoke("on_update", event);
    }

    @EventTarget
    private void onUpdate(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            invoke("on_pre_update", event);
        } else {
            invoke("on_post_update", event);
        }
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (event.getEventType() == EventType.SEND) {
            invoke("on_packet_post", event);
        } else {
            invoke("on_packet_receive", event);
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        invoke("on_tick", event);
    }

    @EventTarget
    private void onRender(RenderEvent event) {
        invoke("on_render", event);
    }

    @EventTarget
    private void onRender(Render2DEvent event) {
        invoke("on_render_2d", event);
    }

    @EventTarget
    private void onRender(RenderSREvent event) {
        invoke("on_render_sr", event);
    }

    @EventTarget
    private void onBlockReach(BlockReachEvent event) {
        invoke("on_block_reach", event);
    }

    @EventTarget
    private void onCollision(CollisionEvent event) {
        invoke("on_collision_block", event);
    }

    @EventTarget
    private void onMouseOver(MouseOverEvent event) {
        invoke("on_mouse_over", event);
    }

    @EventTarget
    private void onMoveFly(MoveFlyEvent event) {
        invoke("on_move_fly", event);
    }

    @EventTarget
    private void onJump(JumpYawEvent event) {
        invoke("on_jump_yaw", event);
    }

    @EventTarget
    private void onChat(ChatEvent event) {
        invoke("on_chat", event);
    }

    @EventTarget
    private void onKey(KeyEvent event) {
        invoke("on_key", event);
    }

    @EventTarget
    private void onMove(PlayerMoveEvent event) {
        invoke("on_move", event);
    }

    @EventTarget
    private void onAttack(AttackEvent event) {
        invoke("on_attack", event);
    }

    @EventTarget
    private void onJump(JumpEvent event) {
        invoke("on_jump", event);
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
            if (!blacklist.contains(funcName)) invocable.invokeFunction(funcName, args);
        } catch (final NoSuchMethodException ignored) {
            blacklist.add(funcName);
        } catch (final Exception e) {
            Main.INSTANCE.println("[JS] " + this.name + ": " + funcName + " error");
        }
    }

}
