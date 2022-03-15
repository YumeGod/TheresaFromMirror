package cn.loli.client.module;

import cn.loli.client.events.*;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
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
        try {
            this.globals.get("on_update").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onUpdate(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            try {
                this.globals.get("on_pre_update").invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.globals.get("on_post_update").invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (event.getEventType() == EventType.SEND) {
            try {
                this.globals.get("on_packet_post").invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.globals.get("on_packet_receive").invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        try {
            this.globals.get("on_tick").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onRender(RenderEvent event) {
        try {
            this.globals.get("on_render").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onRender(Render2DEvent event) {
        try {
            this.globals.get("on_render_2d").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onRender(RenderSREvent event) {
        try {
            this.globals.get("on_render_sr").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onBlockReach(BlockReachEvent event) {
        try {
            this.globals.get("on_block_reach").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onCollision(CollisionEvent event) {
        try {
            this.globals.get("on_collision_block").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onMouseOver(MouseOverEvent event) {
        try {
            this.globals.get("on_mouse_over").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onMoveFly(MoveFlyEvent event) {
        try {
            this.globals.get("on_move_fly").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onJump(JumpYawEvent event) {
        try {
            this.globals.get("on_jump_yaw").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onChat(ChatEvent event) {
        try {
            this.globals.get("on_chat").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onKey(KeyEvent event) {
        try {
            this.globals.get("on_key").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onMove(PlayerMoveEvent event) {
        try {
            this.globals.get("on_move").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onAttack(AttackEvent event) {
        try {
            this.globals.get("on_attack").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onJump(JumpEvent event) {
        try {
            this.globals.get("on_jump").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onDisable() {
        try {
            this.globals.get("on_enable").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        try {
            this.globals.get("on_disable").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
