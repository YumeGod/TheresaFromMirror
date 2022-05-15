package cn.loli.client.script.js;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;

import javax.script.Invocable;
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

    private final IEventListener<UpdateEvent> onUpdate = event -> {invoke("on_update", event);};

    private final IEventListener<MotionUpdateEvent> onMotionUpdate = event -> {
        if (event.getEventType() == EventType.PRE) {
            invoke("on_pre_update", event);
        } else {
            invoke("on_post_update", event);
        }
    };

    private final IEventListener<PacketEvent> onPacket = event -> {
        if (event.getEventType() == EventType.SEND) {
            invoke("on_packet_post", event);
        } else {
            invoke("on_packet_receive", event);
        }
    };

    private final IEventListener<TickEvent> onTick = event -> {invoke("on_tick", event);};

    private final IEventListener<RenderEvent> onRender = event -> {invoke("on_render", event);};

    private final IEventListener<Render2DEvent> onRender2D = event -> {invoke("on_render_2d", event);};

    private final IEventListener<RenderSREvent> onRenderSR = event -> {
        invoke("on_render_sr", event);
    };

    private final IEventListener<BlockReachEvent> onBlockReach = event -> {
        invoke("on_block_reach", event);
    };

    private final IEventListener<CollisionEvent> onCollision = event -> {
        invoke("on_collision_block", event);
    };

    private final IEventListener<MouseOverEvent> onMouseOver = event -> {
        invoke("on_mouse_over", event);
    };

    private final IEventListener<MoveFlyEvent> onMoveFly = event -> {
        invoke("on_move_fly", event);
    };

    private final IEventListener<JumpYawEvent> onJumpYaw = event -> {
        invoke("on_jump_yaw", event);
    };

    private final IEventListener<ChatEvent> onChat = event -> {
        invoke("on_chat", event);
    };

    private final IEventListener<KeyEvent> onKey = event -> {
        invoke("on_key", event);
    };

    private final IEventListener<PlayerMoveEvent> onMove = event -> {
        invoke("on_move", event);
    };

    private final IEventListener<AttackEvent> onAttack = event -> {
        invoke("on_attack", event);
    };

    private final IEventListener<JumpEvent> onJump = event -> {
        invoke("on_jump", event);
    };

    private final IEventListener<EmoteEvent> onEmote = event -> {
        invoke("on_emote", event);
    };

    private final IEventListener<AnimationEvent> onAnimation = event -> {
        invoke("on_animation", event);
    };

    private final IEventListener<CameraEvent> onCamera = event -> {
        invoke("on_camera" , event);
    };

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
            Main.INSTANCE.println("[ERROR] " + this.name + ": " + funcName + " error");
        }
    }

}
