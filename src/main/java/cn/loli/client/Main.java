

package cn.loli.client;

import cn.loli.client.command.CommandManager;
import cn.loli.client.events.LoopEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.file.FileManager;
import cn.loli.client.module.ModuleManager;
import cn.loli.client.module.modules.misc.ClickGUIModule;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.utils.SoundFxPlayer;
import cn.loli.client.utils.TimeHelper;
import cn.loli.client.value.ValueManager;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    @NotNull
    public static final String CLIENT_NAME = "theresa";
    @NotNull
    public static final String CLIENT_AUTHOR = "CuteMirror";
    public static final double CLIENT_VERSION_NUMBER = 0.1;
    @NotNull
    public static final String CLIENT_VERSION = CLIENT_VERSION_NUMBER + "-DEV";
    @NotNull
    public static final String CLIENT_INITIALS = "星";

    public static Main INSTANCE;

    public Logger logger;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public ValueManager valueManager;
    private FileManager fileManager;

    public Queue<Packet<?>> packetQueue;
    TimeHelper ms = new TimeHelper();
    public long timing;

    //Position Record
    public int realPosX;
    public int realPosY;
    public int realPosZ;

    public Main() {
        INSTANCE = this;
        EventManager.register(this);
    }

    public void startClient() {
        logger = LogManager.getLogger();
        fileManager = new FileManager();
        valueManager = new ValueManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();

        commandManager.addCommands();
        moduleManager.addModules();
        fileManager.load();

        moduleManager.getModule(ClickGUIModule.class).createClickGui();

        //Crasher
        packetQueue = new ConcurrentLinkedQueue<>();
        ms.reset();
        timing = 100L;

        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.SPECIAL, -2);
    }

    public void stopClient() {
        try {
            fileManager.save();
        } catch (Exception e) {
            System.err.println("Failed to save settings:");
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onWorldChange(PacketEvent e) {
        if (e.getPacket() instanceof S07PacketRespawn || e.getPacket() instanceof S01PacketJoinGame){
            packetQueue.clear();
            ms.reset();
        }

        if (e.getPacket() instanceof S48PacketResourcePackSend){
            ChatUtils.info("Receive A Request about resource pack one");
            ChatUtils.info("Hash: " + ((S48PacketResourcePackSend) e.getPacket()).getHash());
            ChatUtils.info("URL: " + ((S48PacketResourcePackSend) e.getPacket()).getURL());
        }

        if (e.getPacket() instanceof S27PacketExplosion) {
            if (Math.abs(((S27PacketExplosion) e.getPacket()).getStrength()) > 99 ||
                    Math.abs(((S27PacketExplosion) e.getPacket()).getX()) > 99
                    || Math.abs(((S27PacketExplosion) e.getPacket()).getY()) > 99
                    || Math.abs(((S27PacketExplosion) e.getPacket()).getZ()) > 99) {
                e.setCancelled(true);
            }
        }

        if (e.getPacket() instanceof S2APacketParticles) {
                if(Math.abs(((S2APacketParticles) e.getPacket()).getParticleSpeed()) > 10) {
                    e.setCancelled(true);
                }
                if(((S2APacketParticles) e.getPacket()).getParticleCount() > 500) {
                    e.setCancelled(true);
                }
        }
    }

    @EventTarget
    private void onTick(LoopEvent e) {
        if (Minecraft.getMinecraft().thePlayer.ticksExisted < 5)
            return;

        if (ms.hasReached(timing)) {
            if (!packetQueue.isEmpty()) Minecraft.getMinecraft().
                    getNetHandler().getNetworkManager().sendPacket(packetQueue.poll());
            ms.reset();
        }
    }


}
