package cn.loli.client.script.lua;

import cn.loli.client.events.*;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
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

    private final IEventListener<UpdateEvent> onUpdate = event -> {
        invoke("on_update");
    };

    private final IEventListener<RenderEvent> onRender = event -> {
        invoke("on_render");
    };


    private final IEventListener<MotionUpdateEvent> onMotionUpdate = event -> {
        if (event.getEventType() == EventType.PRE) {
            invoke("on_pre_update");
        } else {
            invoke("on_post_update");
        }
    };

    private final IEventListener<PacketEvent> onPacket = event -> {
        if (event.getEventType() == EventType.SEND) {
            invoke("on_packet_post");
        } else {
            invoke("on_packet_receive");
        }
    };

    private final IEventListener<TickEvent> onTick = event -> {
        invoke("on_tick");
    };

    private final IEventListener<Render2DEvent> onRender2D = event -> {
        invoke("on_render_2d");
    };

    private final IEventListener<RenderSREvent> onRenderSR = event -> {
        invoke("on_render_2d");
    };

    private final IEventListener<BlockReachEvent> onBlockReach = event -> {
        invoke("on_block_reach");
    };

    private final IEventListener<CollisionEvent> onCollision = event -> {
        invoke("on_collision_block");
    };

    private final IEventListener<MouseOverEvent> onMouseOver = event -> {
        invoke("on_mouse_over");
    };

    private final IEventListener<MoveFlyEvent> onMoveFly = event -> {
        invoke("on_move_fly");
    };

    private final IEventListener<JumpYawEvent> onJumpYaw = event -> {
        invoke("on_jump_yaw");
    };

    private final IEventListener<ChatEvent> onChat = event -> {
        invoke("on_chat");
    };

    private final IEventListener<KeyEvent> onKey = event -> {
        invoke("on_key");
    };

    private final IEventListener<PlayerMoveEvent> onMove = event -> {
        invoke("on_move");
    };

    private final IEventListener<AttackEvent> onAttack = event -> {
        invoke("on_attack");
    };

    private final IEventListener<JumpEvent> onJump = event -> {
        invoke("on_jump");
    };

    private final IEventListener<EmoteEvent> onEmote = event -> {
        invoke("on_emote");
    };

    private final IEventListener<AnimationEvent> onAnimation = event -> {
        invoke("on_animation");
    };

    private final IEventListener<CameraEvent> onCamera = event -> {
        invoke("on_camera");
    };

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
