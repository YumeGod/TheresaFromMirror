

package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.injection.implementations.IEntityPlayer;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.misc.AntiBot;
import cn.loli.client.module.modules.player.Scaffold;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.render.RenderUtils;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.ColorProperty;
import dev.xix.property.impl.EnumProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Aura extends Module {

    private enum CPS_MODE {
        RANDOM("Randomize"), SMOOTH("Smooth"), LEGIT("Legit"), HIT_BASED("Hit Based");

        private final String name;

        CPS_MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty cpsMode = new EnumProperty<>("CPS Mode", CPS_MODE.RANDOM);
    private final NumberProperty<Integer> minCps = new NumberProperty<>("Min CPS", 8, 1, 20, 1);
    private final NumberProperty<Integer> maxCps = new NumberProperty<>("Max CPS", 8, 1, 40, 1);
    private final BooleanProperty smoothRandomizing = new BooleanProperty("Smooth-Randomizing", true);
    private final NumberProperty<Float> smoothCpsSpeed = new NumberProperty<>("Smooth-Delay", 0f, 0f, 1f, 0.01f);
    private final NumberProperty<Float> smoothCpsRandomStrength = new NumberProperty<>("Smooth-Random-Length", 0f, 0f, 1f, 0.01f);

    private static final NumberProperty<Integer> ticksExisted = new NumberProperty<>("Ticks Existed", 20, 0, 500, 10);
    private static final NumberProperty<Integer> fov = new NumberProperty<>("FOV", 360, 0, 360, 1);
    private static final NumberProperty<Float> range = new NumberProperty<>("Range", 3f, 1f, 6f, 0.1f);
    private static final NumberProperty<Float> blockRange = new NumberProperty<>("BlockRange", 2f, 0f, 3f, 0.1f);

    private static final NumberProperty<Float> inaccuracy = new NumberProperty<>("Inaccuracy", 0f, 0f, 1f, 0.01f);
    public static final NumberProperty<Integer> target_Amount = new NumberProperty<>("Targets Amount", 1, 1, 5, 1);
    public static final NumberProperty<Integer> switchDelay = new NumberProperty<>("Switch Delay", 100, 0, 500, 10);

    public static final NumberProperty<Integer> hurtTime = new NumberProperty<>("Multi Hurt Time", 20, 0, 20, 1);

    private final BooleanProperty multi = new BooleanProperty("Multi", false);

    private enum MODE {
        ANGLE("Angle"), ARMOR("Armor"), RANGE("Range"), FOV("Fov Based"), HEALTH("Health"), Hurt_Time("Hurt Time");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    private final EnumProperty mode = new EnumProperty<>("Priority", MODE.ANGLE);

    public static final NumberProperty<Integer> unBlockTweak = new NumberProperty<>("UnBlock Tweak", 0, 0, 100, 1);

    //Mode ENUM FOR BLOCK MODE
    private enum BLOCK_MODE {
        NCP("NCP"), IDLE("Idle"), ALWAYS("Always"), LEGIT("Legit"), VANILLA("Vanilla"), SWITCH("Switch");

        private final String name;

        BLOCK_MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty blockMode = new EnumProperty<>("Block Mode", BLOCK_MODE.NCP);

    private enum BLOCK_WHEN {
        ATTACK("On Attack"), TICK("On Tick"), SYNC("Sync");

        private final String name;

        BLOCK_WHEN(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty blockWhen = new EnumProperty<>("Block when", BLOCK_WHEN.TICK);

    private enum BLOCK_STYLE {
        SYNC("Sync"), DESYNC("De Sync");

        private final String name;

        BLOCK_STYLE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    private final EnumProperty blockSense = new EnumProperty<>("Block style", BLOCK_STYLE.SYNC);
    public static final NumberProperty<Integer> desyncTick = new NumberProperty<>("Desync-Choke-Tick", 2, 0, 5, 1);

    private enum ATTACK_WHEN {
        PRE("On Pre"), POST("On Post"), TICK("On Tick");

        private final String name;

        ATTACK_WHEN(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty attackWhen = new EnumProperty<>("Attack when", ATTACK_WHEN.PRE);

    private enum DURABLE_STATUS {
        DISABLE("Disabled"), SYNC("Sync"), SWITCH("SWITCH");

        private final String name;

        DURABLE_STATUS(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    private final EnumProperty durable = new EnumProperty<>("Durable Status", DURABLE_STATUS.DISABLE);
    private final BooleanProperty sprintSpam = new BooleanProperty("Sprint Spam", false);

    private final BooleanProperty autoBlock = new BooleanProperty("AutoBlock", true);
    private static final BooleanProperty invisible = new BooleanProperty("Invisible", false);
    private static final BooleanProperty players = new BooleanProperty("Players", true);
    private static final BooleanProperty animals = new BooleanProperty("Animals", false);
    private static final BooleanProperty mobs = new BooleanProperty("Mobs", true);
    private static final BooleanProperty boss = new BooleanProperty("Boss", true);
    private static final BooleanProperty villagers = new BooleanProperty("Villagers", false);
    private static final BooleanProperty team = new BooleanProperty("Team", false);

    private static final BooleanProperty witherPriority = new BooleanProperty("Wither Priority", false);

    private final BooleanProperty rotations = new BooleanProperty("Rotations", false);

    public static final NumberProperty<Integer> yOffset = new NumberProperty<>("Custom Y Offset", 50, 0, 150, 10);
    private final BooleanProperty customOffset = new BooleanProperty("Custom Pitch", false);

    private final BooleanProperty silent = new BooleanProperty("Silent", true);

    private final BooleanProperty noInv = new BooleanProperty("No Inv", true);
    private final BooleanProperty autoCloseInv = new BooleanProperty("Auto Close Inv", false);
    private final BooleanProperty clampYaw = new BooleanProperty("Clamp", true);

    private final BooleanProperty moveFix = new BooleanProperty("Move Fix", false);
    private final BooleanProperty silentMoveFix = new BooleanProperty("Silent Fix", false);

    private final BooleanProperty noScaffold = new BooleanProperty("No Scaffold", false);
    private final BooleanProperty swingWhenMisAttack = new BooleanProperty("Swing when mis attack", false);
    private final BooleanProperty rayCast = new BooleanProperty("Ray Cast", false);
    private static final BooleanProperty throughBlock = new BooleanProperty("Through Block", true);

    private final BooleanProperty mouseFix = new BooleanProperty("Mouse Fix", true);
    private final BooleanProperty mouse_vl_fix = new BooleanProperty("Mouse VL Fix", true);
    private final BooleanProperty random = new BooleanProperty("Random", false);
    private final BooleanProperty instant = new BooleanProperty("Instant", false);
    private final BooleanProperty prediction = new BooleanProperty("Prediction", false);
    private final BooleanProperty bestVector = new BooleanProperty("Resolver", false);

    private static final NumberProperty<Integer> rotationSpeed = new NumberProperty<>("Rotation Speed", 180, 0, 180, 5);

    private enum ESP_MODE {
        BOX("Box"), TWO_D("2D"), ICARUS("Icarus");

        private final String name;

        ESP_MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty esp = new EnumProperty<>("Target ESP", ESP_MODE.BOX);

    private final BooleanProperty show = new BooleanProperty("Show-Target", true);

    private final ColorProperty espColor = new ColorProperty("ESP-Color", Color.BLUE);

    public EntityLivingBase target;

    private final TimeHelper attackTimer = new TimeHelper();
    private final TimeHelper switchTimer = new TimeHelper();
    private final TimeHelper unblockTimer = new TimeHelper();
    //CPS Legit
    private final TimeHelper reset = new TimeHelper();

    public static ArrayList<Entity> entities = new ArrayList<>();
    public static List<EntityLivingBase> targets = new ArrayList<>();

    //attack per durable
    long apd;
    int cps, index;
    boolean isBlocking = false;

    float curYaw, curPitch;

    Criticals crit;

    //Desync Auto Block
    Queue<Packet<?>> desyncPackets = new ArrayDeque<>();
    int ticks, ignoreTicks;

    public Aura() {
        super("Aura", "Automatically attacks enemies around you.", ModuleCategory.COMBAT);
    }


    @Override
    public void onEnable() {
        crit = Main.INSTANCE.moduleManager.getModule(Criticals.class);
        cps = 0;
        calculateCPS();
    }

    @Override
    public void onDisable() {
        targets.clear();
        target = null; // 清空目标 (AutoBlock动画修复)
        entities.clear();

        if (isBlocking) {
            ((IEntityPlayer) mc.thePlayer).setItemInUseCount(0);
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }


    private final IEventListener<RenderEvent> onRender = event ->
    {
        try {
            update();
        } catch (Exception e) {
            Main.INSTANCE.println(e.getMessage());
        }

        /*
         *  CPS 运算
         */
        if (target != null && mc.thePlayer.getDistanceToEntity(target) - 0.5657 <= range.getPropertyValue() &&
                attackTimer.hasReached(apd)) {
            cps++;
            if (!cpsMode.getPropertyValue().toString().equals(CPS_MODE.HIT_BASED))
                calculateCPS();
            attackTimer.reset();
        }

        /*
         *  转头 运算
         */

        if (rotations.getPropertyValue()) {
            if (target != null) {
                float[] rots;
                rots = rotationUtils.facePlayer(target, mouse_vl_fix.getPropertyValue(), random.getPropertyValue(), !instant.getPropertyValue(), prediction.getPropertyValue(), mouseFix.getPropertyValue()
                        , bestVector.getPropertyValue(), inaccuracy.getPropertyValue(), clampYaw.getPropertyValue(), rotationSpeed.getPropertyValue(), range.getPropertyValue() + blockRange.getPropertyValue(), customOffset.getPropertyValue(), yOffset.getPropertyValue());

                curYaw = rots[0];
                curPitch = rots[1];
            } else {
                curYaw = mc.thePlayer.rotationYaw;
                curPitch = mc.thePlayer.rotationPitch;
            }

            if (!silent.getPropertyValue()) {
                mc.thePlayer.rotationYaw = curYaw;
                mc.thePlayer.rotationPitch = curPitch;
            }
        }

        if (target != null && show.getPropertyValue()) {
            EntityLivingBase entity = target;

            switch (esp.getPropertyValue().toString()) {
                case "Box":
                    RenderUtils.renderBox(entity, espColor.getPropertyValue().getRGB());
                    break;
                case "2D":
                    RenderUtils.draw2DESP(entity, espColor.getPropertyValue().getRGB());
                    break;
                case "Icarus":
                    RenderUtils.drawIcarusESP(entity, espColor.getPropertyValue(), true);
                    break;
            }

        }
    };


    private final IEventListener<MoveFlyEvent> onMoveFly = event ->
    {
        if (noScaffold.getPropertyValue() && Main.INSTANCE.moduleManager.getModule(Scaffold.class).getState())
            return;
        if (moveFix.getPropertyValue() && mc.thePlayer.getDistanceToEntity(target) - 0.5657 <= range.getPropertyValue() && target != null)
            event.setYaw(curYaw);
    };

    private final IEventListener<JumpYawEvent> onJump = event ->
    {
        if (noScaffold.getPropertyValue() && Main.INSTANCE.moduleManager.getModule(Scaffold.class).getState())
            return;
        if (moveFix.getPropertyValue() && mc.thePlayer.getDistanceToEntity(target) - 0.5657 <= range.getPropertyValue() && target != null)
            event.setYaw(curYaw);
    };

    private final IEventListener<MovementStateEvent> onSlient = event ->
    {
        if (noScaffold.getPropertyValue() && Main.INSTANCE.moduleManager.getModule(Scaffold.class).getState())
            return;
        if (moveFix.getPropertyValue() && target != null && mc.thePlayer.getDistanceToEntity(target) - 0.5657 <= range.getPropertyValue() && silentMoveFix.getPropertyValue()) {
            event.setSilentMoveFix(true);
            event.setYaw(curYaw);
        }
    };

    private final IEventListener<MotionUpdateEvent> onMotionUpdate = event ->
    {
        if (noScaffold.getPropertyValue() && Main.INSTANCE.moduleManager.getModule(Scaffold.class).getState())
            return;
        if (target == null) {
            curYaw = mc.thePlayer.rotationYaw;
            curPitch = mc.thePlayer.rotationPitch;

            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                    && autoBlock.getPropertyValue() && isBlocking) {
                ((IEntityPlayer) mc.thePlayer).setItemInUseCount(0);
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                isBlocking = false;
            }
            return;
        }

        if (event.getEventType() == EventType.PRE) {
            if (rotations.getPropertyValue()) {
                if (silent.getPropertyValue()) {
                    event.setYaw(curYaw);
                    event.setPitch(curPitch);
                }
            }
            if (blockWhen.getPropertyValue().toString().equals("Reverse") && ignoreTicks == 2) handleAutoBlock(true);
            if (blockWhen.getPropertyValue().toString().equals("On Tick")) handleAutoBlock(true);
            if (attackWhen.getPropertyValue().toString().equals("Pre")) attemptAttack();
        } else if (event.getEventType() == EventType.POST) {
            if (attackWhen.getPropertyValue().toString().equals("Post")) attemptAttack();
            if (blockWhen.getPropertyValue().toString().equals("On Tick") || blockWhen.getPropertyValue().toString().equals("Sync") || blockWhen.getPropertyValue().toString().equals("Reverse"))
                handleAutoBlock(false);
            ignoreTicks++;
        }
    };


    private final IEventListener<TickAttackEvent> onAttack = event ->
    {
        if (noScaffold.getPropertyValue() && Main.INSTANCE.moduleManager.getModule(Scaffold.class).getState())
            return;
        if (target == null) return;
        if (attackWhen.getPropertyValue().toString().equals("Tick")) attemptAttack();
    };

    private final IEventListener<PacketEvent> onPacket = event ->
    {
        if (noScaffold.getPropertyValue() && Main.INSTANCE.moduleManager.getModule(Scaffold.class).getState())
            return;
        if (event.getPacket() instanceof C07PacketPlayerDigging)
            if (blockSense.getPropertyValue().toString().equals("Desync") && (mc.thePlayer.getHeldItem() != null
                    && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getPropertyValue()))
                if (target != null)
                    if (isBlocking) {
                        if (ticks < 1) desyncPackets.add(event.getPacket());
                        event.setCancelled(true);
                    }

        if (event.getPacket() instanceof C03PacketPlayer)
            if (blockSense.getPropertyValue().toString().equals("Desync") && (mc.thePlayer.getHeldItem() != null
                    && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getPropertyValue()))
                if (target != null) {
                    if (!isBlocking && ticks < 2 + desyncTick.getPropertyValue()) {
                        desyncPackets.add(event.getPacket());
                        event.setCancelled(true);
                        ticks++;
                    } else {
                        attemptRelease();
                    }
                } else {
                    attemptRelease();
                }

        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement)
            if (blockSense.getPropertyValue().toString().equals("Desync") && (mc.thePlayer.getHeldItem() != null
                    && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getPropertyValue())) {
                if (ticks > 1 + desyncTick.getPropertyValue())
                    attemptRelease();

                if (ticks != 0)
                    event.setCancelled(true);
            }
    };


    //尝试进行Attack
    private void attemptAttack() {
        if (target == null) return;
        //Pre Attack
        if (mc.thePlayer.getDistanceToEntity(target) - 0.5657 <= range.getPropertyValue()) {
            if (blockWhen.getPropertyValue().toString().equals("On Attack") || blockWhen.getPropertyValue().toString().equals("Sync"))
                handleAutoBlock(true);

            if (sprintSpam.getPropertyValue())
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

            while (cps > 0) {
                attack(target);
                if (multi.getPropertyValue())
                    targets.stream().filter(target -> target != this.target &&
                            target.hurtResistantTime <= hurtTime.getPropertyValue()).forEach(this::attack);
                cps--;
            }

            if (sprintSpam.getPropertyValue())
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));

            if (blockWhen.getPropertyValue().toString().equals("On Attack"))
                handleAutoBlock(false);
        }
    }


    //具体的Attack方式
    private void attack(Entity entity) {
        if (mc.currentScreen != null && noInv.getPropertyValue()) {
            if (autoCloseInv.getPropertyValue())
                mc.thePlayer.closeScreen();
            else return;
        }

        if (rayCast.getPropertyValue())
            entity = rotationUtils.rayCastedEntity(range.getPropertyValue() + 0.5657, curYaw, curPitch);

        if (entity != null) {
            if (!durable.getPropertyValue().toString().equals("Disable"))
                handleDura(durable.getPropertyValue().toString().equals("Switch"));
            if (crit.getState()) crit.onCrit(entity);
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, entity);
        } else {
            //不打人
            if (!swingWhenMisAttack.getPropertyValue())
                mc.thePlayer.swingItem();
        }

        if (cpsMode.getPropertyValue().toString().equals(CPS_MODE.HIT_BASED)) calculateCPS();
    }


    //更新实体状态
    private void update() {
        try {
            targets.removeIf(ent -> !canAttack(ent));
            targets = this.getTargets();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (targets.isEmpty()
                || index >= targets.size())
            index = 0;

        // 拿实体
        if (targets.size() == 0)
            target = null;
        else
            target = targets.get(index);

        if (target == null) return;

        if (mc.thePlayer.getDistanceToEntity(target) - 0.5657 > range.getPropertyValue())
            index = 0;

        if (switchTimer.hasReached(switchDelay.getPropertyValue())
                && targets.size() > 1) {
            switchTimer.reset();
            ++index;
        }
    }


    //确定他妈的是否格挡
    private void handleAutoBlock(boolean unblock) {
        final int curSlot = mc.thePlayer.inventory.currentItem;
        if (unblock) {
            if (!unblockTimer.hasReached(unBlockTweak.getPropertyValue())) return;
            unblockTimer.reset();

            if (mc.thePlayer.isBlocking() || mc.thePlayer.getHeldItem() != null
                    && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getPropertyValue() && isBlocking) {

                ((IEntityPlayer) mc.thePlayer).setItemInUseCount(0);

                switch ((blockMode.getPropertyValue()).toString().toLowerCase()) {
                    case "desync":
                    case "dada-desync":
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        isBlocking = false;
                        break;
                    case "always":
                        ((IEntityPlayer) mc.thePlayer).setItemInUseCount(mc.thePlayer.getHeldItem().getMaxItemUseDuration());
                        break;
                    case "idle":
                        ((IEntityPlayer) mc.thePlayer).setItemInUseCount(mc.thePlayer.getHeldItem().getMaxItemUseDuration());
                        if (mc.thePlayer.ticksExisted % 2 != 0) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                            isBlocking = false;
                        }
                        break;
                    case "semi-vanilla":
                        ((IEntityPlayer) mc.thePlayer).setItemInUseCount(mc.thePlayer.getHeldItem().getMaxItemUseDuration());
                        isBlocking = false;
                        break;
                    case "spoof-switch":
                    case "switch":
                    case "dada":
                        isBlocking = false;
                        break;
                    case "legit":
                    case "dada-legit":
                    case "ncp":
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                        isBlocking = false;
                        break;
                    case "null":
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, null, EnumFacing.DOWN));
                        isBlocking = false;
                        break;
                    case "vanilla":
                        mc.playerController.onStoppedUsingItem(mc.thePlayer);
                        isBlocking = false;
                        break;
                }
            }
        } else {
            if ((mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getPropertyValue() || mc.thePlayer.isBlocking()) && !isBlocking) {
                ((IEntityPlayer) mc.thePlayer).setItemInUseCount(mc.thePlayer.getHeldItem().getMaxItemUseDuration());

                switch ((blockMode.getPropertyValue().toString()).toLowerCase()) {
                    case "ncp":
                    case "desync":
                    case "always":
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        break;
                    case "idle":
                        if (mc.thePlayer.ticksExisted % 2 == 0)
                            mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        break;
                    case "legit":
                        mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        break;
                    case "vanilla":
                    case "semi-vanilla":
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                        break;
                    case "null":
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(null));
                        break;
                    case "spoof-switch":
                        final int spoof = curSlot == 0 ? 1 : -1;
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(curSlot + spoof));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, target.getPositionVector()));
                        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                        break;
                    case "switch":
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(curSlot));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        break;
                    case "dada-legit":
                        mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem < 8 ? mc.thePlayer.inventory.currentItem + 1 : mc.thePlayer.inventory.currentItem - 1));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        break;
                    case "dada-desync":
                        mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem < 8 ? mc.thePlayer.inventory.currentItem + 1 : mc.thePlayer.inventory.currentItem - 1));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        break;
                }
                ignoreTicks = 0;
                isBlocking = true;
            }
        }
    }

    private void attemptRelease() {
        while (!desyncPackets.isEmpty())
            mc.getNetHandler().getNetworkManager().sendPacket(desyncPackets.poll(), null);

        ticks = 0;
    }

    //handle the handleDura
    private void handleDura(boolean isSwap) {
        int slot = mc.thePlayer.inventory.currentItem;
        if (isSwap) mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slot + 1));
        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, target);
        if (isSwap) mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slot));
    }

    //取实体
    private List<EntityLivingBase> getTargets() {
        Stream<EntityLivingBase> stream = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity)
                .filter(Aura::canAttack);

        switch (mode.getPropertyValue().toString()) {
            case "Armor": {
                stream = stream.sorted(Comparator.comparingInt(o -> ((o instanceof EntityPlayer ? ((EntityPlayer) o).inventory.getTotalArmorValue() : (int) o.getHealth()))));
                break;
            }
            case "Range": {
                stream = stream.sorted((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
                break;
            }
            case "Fov Based": {
                stream = stream.sorted(Comparator.comparingDouble(o -> getDistanceBetweenAngles(mc.thePlayer.rotationPitch, getRotations(o)[0])));
                break;
            }
            case "Angle": {
                stream = stream.sorted((o1, o2) -> {
                    float[] rot1 = getRotations(o1);
                    float[] rot2 = getRotations(o2);
                    return (int) (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0]));
                });
                break;
            }
            case "Health": {
                stream = stream.sorted((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
                break;
            }
            case "Hurt Time": {
                stream = stream.sorted(Comparator.comparingInt(o -> (20 - o.hurtResistantTime)));
            }
        }


        List<EntityLivingBase> list;

        if (witherPriority.getPropertyValue()) {
            List<EntityLivingBase> sortedList = stream.collect(Collectors.toList());
            list = new ArrayList<>();
            list.addAll(sortedList.stream().filter((entity) -> entity instanceof EntityWither).collect(Collectors.toList()));
            list.addAll(sortedList.stream().filter((entity) -> !(entity instanceof EntityWither)).collect(Collectors.toList()));
        } else {
            list = stream.collect(Collectors.toList());
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
            Collections.reverse(list);

        return list.subList(0, Math.min(list.size(), target_Amount.getPropertyValue()));

    }

    //计算CPS
    private long randomClickDelay(final double minCPS, final double maxCPS) {
        return (long) (1000L / (minCPS + ((maxCPS - minCPS) * playerUtils.randomInRange(0.4, 1.0))));
    }


    boolean wasCPSDrop = false;

    private void calculateCPS() {
        int max = Math.max(minCps.getPropertyValue(), maxCps.getPropertyValue());
        int min = Math.min(minCps.getPropertyValue(), maxCps.getPropertyValue());

        switch (cpsMode.getPropertyValue().toString()) {
            case "Legit": {
                final Random random = new Random();
                apd = min + (random.nextInt() * ((long) max - min));

                if (reset.hasReached((long) (1000.0 / playerUtils.randomInRange(min, (long) max)))) {
                    wasCPSDrop = !wasCPSDrop;
                    if (wasCPSDrop) reset.reset();
                }

                final long cur = System.currentTimeMillis() * random.nextInt(220);

                long timeCovert = Math.max(apd, cur) / 3;
                if (wasCPSDrop) {
                    apd = timeCovert;
                } else {
                    apd = min + (random.nextInt() * ((long) max - min)) / timeCovert;
                }
                break;
            }
            case "Randomize": {
                apd = randomClickDelay(min, max);
                break;
            }
            case "Smooth": {
                min = (int) playerUtils.smooth(min + max, min, smoothCpsSpeed.getPropertyValue() / 10, smoothRandomizing.getPropertyValue(), smoothCpsRandomStrength.getPropertyValue());
            }
            default: {
                apd = 1000 / min;
            }
        }
    }

    //限制
    private static boolean canAttack(EntityLivingBase target) {
        if (target instanceof EntityPlayer || target instanceof EntityAnimal || target instanceof EntityMob || target instanceof INpc) {
            if (target instanceof EntityPlayer && !players.getPropertyValue()) return false;
            if (target instanceof EntityAnimal && !animals.getPropertyValue()) return false;
            if (target instanceof EntityWither && boss.getPropertyValue())
                return mc.thePlayer.getDistanceToEntity(target) - 0.5657 <= range.getPropertyValue() + blockRange.getPropertyValue(); // true
            if (target instanceof EntityMob && !mobs.getPropertyValue()) return false;
            if (target instanceof INpc && !villagers.getPropertyValue()) return false;
        }
        if (target instanceof EntityArmorStand) return false;
        if (playerUtils.isOnSameTeam(target) && team.getPropertyValue()) return false;
        if (target.isInvisible() && !invisible.getPropertyValue()) return false;
        if (!isInFOV(target, fov.getPropertyValue())) return false;
        if (!mc.thePlayer.canEntityBeSeen(target) && !throughBlock.getPropertyValue()) return false;
        if (Main.INSTANCE.moduleManager.getModule(AntiBot.class).getState() && Main.INSTANCE.moduleManager.getModule(AntiBot.class).isBot(target))
            return false;

        return target != mc.thePlayer && target.isEntityAlive() && mc.thePlayer.getDistanceToEntity(target) - 0.5657 <= range.getPropertyValue() + blockRange.getPropertyValue() && target.ticksExisted > ticksExisted.getPropertyValue();
    }

    private static boolean isInFOV(EntityLivingBase entity, double angle) {
        double calcYaw = rotationUtils.calculateRotationDiff(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) + 180,
                MathHelper.wrapAngleTo180_float(rotationUtils.getYaw(mc.thePlayer, entity)))[0];
        return calcYaw <= angle;
    }

    //优先级
    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs((angle1 - angle2)) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    public static float[] getRotations(final Entity entity) {
        if (entity == null) {
            return null;
        }
        final double diffX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        final double diffZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double diffY;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase elb = (EntityLivingBase) entity;
            diffY = elb.posY + (elb.getEyeHeight()) - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        }
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        final float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }
}
