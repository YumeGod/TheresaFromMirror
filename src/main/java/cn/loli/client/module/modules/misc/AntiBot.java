package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.PlayerUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class AntiBot extends Module {

    private final BooleanValue healthNaNCheck = new BooleanValue("Health NaN", false);
    private final BooleanValue groundCheck = new BooleanValue("Check Ground", false);
    private final BooleanValue groundSpawnCheck = new BooleanValue("Check Spawn", false);
    private final BooleanValue nameCheck = new BooleanValue("Name", false);
    private final BooleanValue swingCheck = new BooleanValue("Swing", false);
    private final BooleanValue hitBefore = new BooleanValue("Need Hit", false);
    private final BooleanValue tabListCheck = new BooleanValue("TabList", false);
    private final BooleanValue staticPingCheck = new BooleanValue("Ping", false);
    private final BooleanValue skinCheck = new BooleanValue("Skin", false);
    private final BooleanValue duplicateEntityCheck = new BooleanValue("Duplicate", false);
    private final BooleanValue soundCheck = new BooleanValue("Sound", false);
    private final BooleanValue rotation = new BooleanValue("Illegal Rotation", false);
    private final BooleanValue period = new BooleanValue("Tolerance Period", false);

    private final NumberValue<Integer> ticksExisted = new NumberValue<>("Ticks Existed", 0, 0, 100);
    private final NumberValue<Integer> ping = new NumberValue<>("Ping", 0, -10, 500);


    private final ArrayList<Entity> madeSound = new ArrayList<>();
    private final ArrayList<Entity> swingEntity = new ArrayList<>();
    private final ArrayList<Entity> hitBeforeEntity = new ArrayList<>();
    private final ArrayList<Entity> rotationEntity = new ArrayList<>();
    private final ArrayList<Entity> groundSpawnEntity = new ArrayList<>();
    private final ArrayList<Entity> duplicates = new ArrayList<>();
    private final ArrayList<Entity> periodEntity = new ArrayList<>();


    private final CopyOnWriteArrayList<Entity> copyEntities = new CopyOnWriteArrayList<>();

    public AntiBot() {
        super("AntiBot", "You doesn't hit bots", ModuleCategory.MISC);
    }

    public boolean isBot(EntityLivingBase entity) {
        if (entity != null && entity != mc.thePlayer) {
            if (entity instanceof EntityPlayer) {
                if (ticksExisted.getObject() != 0 && entity.ticksExisted < ticksExisted.getObject())
                    return false;
                if (healthNaNCheck.getObject() && !Float.isNaN(entity.getHealth()))
                    return true;
                if (groundCheck.getObject() && entity.onGround && mc.theWorld.getBlockState(entity.getPosition().add(0, -0.05, 0)).getBlock() == Blocks.air)
                    return true;
                if (soundCheck.getObject() && !madeSound.contains(entity))
                    return true;
                if (swingCheck.getObject() && !swingEntity.contains(entity))
                    return true;
                if (nameCheck.getObject() && !checkedName(entity))
                    return true;
                if (hitBefore.getObject() && !hitBeforeEntity.contains(entity))
                    return true;
                if (!isInTabList(entity) && tabListCheck.getObject())
                    return true;
                if (!hasPing(entity) && staticPingCheck.getObject())
                    return true;
                if (rotationEntity.contains(entity) && rotation.getObject())
                    return true;
                if (groundSpawnCheck.getObject() && groundSpawnEntity.contains(entity))
                    return true;
                if (period.getObject() && !periodEntity.contains(entity))
                    return true;
                if (entity instanceof AbstractClientPlayer) {
                    if (skinCheck.getObject() && !((AbstractClientPlayer) entity).hasSkin()) {
                        return true;
                    }
                }
                return duplicateEntityCheck.getObject() && duplicates.contains(entity);
            } else {
                return false;
            }
        }
        return false;
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                if (isWrongRotation((EntityPlayer) entity)) {
                    rotationEntity.add(entity);
                }
                if (duplicateEntityCheck.getObject()) {
                    final ArrayList<EntityPlayer> entities = getPlayersByName(entity.getName());
                    final ArrayList<NetworkPlayerInfo> tabList = searchPlayers(entity.getName());
                    if (tabList.size() > 1 && entities.size() < tabList.size()) {
                        Main.INSTANCE.println(tabList.size() + entities.size() + " " + entity.getName());
                        if (!duplicates.contains(entity)) {
                            duplicates.add(entity);
                        }
                        continue;
                    } else if (tabList.size() > 1) {
                        if (entity != mc.theWorld.getPlayerEntityByName(entity.getName())) {
                            Main.INSTANCE.println(tabList.size() + entities.size() + " " + entity.getName());
                            if (!duplicates.contains(entity)) {
                                duplicates.add(entity);
                            }
                        }
                    }

                    if (entity instanceof AbstractClientPlayer)
                        for (EntityPlayer p : entities) {
                            if (p != entity)
                                if (p instanceof AbstractClientPlayer) {
                                    if (((AbstractClientPlayer) p).hasSkin() != ((AbstractClientPlayer) entity).hasSkin()) {
                                        duplicates.add(entity);
                                    }
                                }
                        }
                }
            }
        }


    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (event.getEventType() == EventType.RECEIVE) {
            if (event.getPacket() instanceof S01PacketJoinGame) {
                madeSound.clear();
                swingEntity.clear();
                hitBeforeEntity.clear();
                rotationEntity.clear();
                groundSpawnEntity.clear();
                duplicates.clear();
                copyEntities.clear();
                periodEntity.clear();
                //Clear
            }

            if (event.getPacket() instanceof S29PacketSoundEffect) {
                copyEntities.addAll(mc.theWorld.loadedEntityList);
                copyEntities.forEach(entity -> {
                    if (entity != mc.thePlayer && entity.getDistance
                            (((S29PacketSoundEffect) event.getPacket()).getX(), ((S29PacketSoundEffect) event.getPacket()).getY(), ((S29PacketSoundEffect) event.getPacket()).getZ()) <= 0.8)
                        madeSound.add(entity);
                });
                copyEntities.clear();
            }
            if (event.getPacket() instanceof S0CPacketSpawnPlayer) {
                if (mc.theWorld != null) {
                    final EntityPlayer player = mc.theWorld.getPlayerEntityByUUID(((S0CPacketSpawnPlayer) event.getPacket()).getPlayer());
                    if (player != null)
                        if ((!player.onGround || playerUtils.getBlockUnderPlayer(1) == Blocks.air) && groundSpawnCheck.getObject())
                            groundSpawnEntity.add(player);

                    if (mc.thePlayer.ticksExisted < 60) {
                        periodEntity.add(player);
                    }
                }
            }
            if (event.getPacket() instanceof S0BPacketAnimation) {
                if (!swingEntity.contains(mc.theWorld.getEntityByID(((S0BPacketAnimation) event.getPacket()).getEntityID())))
                    swingEntity.add(mc.theWorld.getEntityByID(((S0BPacketAnimation) event.getPacket()).getEntityID()));
            }
        } else if (event.getEventType() == EventType.SEND) {
            if (event.getPacket() instanceof C02PacketUseEntity) {
                if (((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
                    final Entity entity = ((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld);
                    if (!hitBeforeEntity.contains(entity))
                        hitBeforeEntity.add(entity);
                }
            }
        }
    }


    public boolean isWrongRotation(EntityPlayer entity) {

        float renderYawOffset = 0;

        float curOffset = MathHelper.wrapAngleTo180_float(entity.rotationYaw - entity.renderYawOffset);


        curOffset = MathHelper.clamp_float(curOffset, -75, 75);

        renderYawOffset = entity.rotationYaw - curOffset;

        if (curOffset * curOffset > 2500.0F) {
            renderYawOffset += curOffset * 0.2F;
        }

        final double distanceToHead = Math.abs(entity.rotationYaw - renderYawOffset);
        final boolean hasIllegalPitch = entity.rotationPitch > 90 || entity.rotationPitch < -90;

        final boolean wrongRotation = distanceToHead > 75;

        return wrongRotation || hasIllegalPitch;
    }


    public boolean hasPing(Entity entity) {
        if (mc.isSingleplayer())
            return true;

        for (NetworkPlayerInfo playerInfo : mc.thePlayer.sendQueue.getPlayerInfoMap()) {
            if (playerInfo.getGameProfile().getId().equals(entity.getUniqueID()))
                if (playerInfo.getResponseTime() > ping.getObject().intValue())
                    return true;
        }
        return false;
    }

    public boolean isInTabList(Entity entity) {
        if (mc.isSingleplayer())
            return true;
        for (NetworkPlayerInfo playerInfo : mc.thePlayer.sendQueue.getPlayerInfoMap()) {
            if (playerInfo.getGameProfile().getId().equals(entity.getUniqueID()))
                return true;
        }
        return false;
    }

    public boolean checkedName(Entity entity) {
        if (!playerUtils.isValidEntityName(entity))
            return false;
        return true;
    }

    public ArrayList<EntityPlayer> getPlayersByName(String name) {
        final ArrayList<EntityPlayer> players = new ArrayList<>();
        for (int i = 0; i < mc.theWorld.playerEntities.size(); ++i) {
            EntityPlayer entityplayer = mc.theWorld.playerEntities.get(i);

            if (name.equals(entityplayer.getName())) {
                players.add(entityplayer);
            }
        }

        return players;
    }

    public ArrayList<NetworkPlayerInfo> searchPlayers(String name) {
        final ArrayList<NetworkPlayerInfo> playerInfos = new ArrayList<>();

        for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
            if (name.equals(info.getGameProfile().getName())) {
                playerInfos.add(info);
            }
        }
        return playerInfos;
    }

    private static boolean inTab(EntityLivingBase entity) {
        for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap())
            if (info != null && info.getGameProfile() != null && info.getGameProfile().getName().contains(entity.getName()))
                return true;
        return false;
    }

}
