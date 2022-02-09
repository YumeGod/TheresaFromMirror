

package cn.loli.client;

import cn.loli.client.command.CommandManager;
import cn.loli.client.events.LoopEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.file.FileManager;
import cn.loli.client.module.ModuleManager;
import cn.loli.client.module.modules.misc.ClickGUIModule;
import cn.loli.client.protection.ProtectionThread;
import cn.loli.client.utils.*;
import cn.loli.client.value.ValueManager;
import com.Kernel32;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.Potion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.System.getProperty;


public class Main {
    @NotNull
    public static final String CLIENT_NAME = "theresa";
    @NotNull
    public static final String CLIENT_AUTHOR = "CuteMirror";
    public static final double CLIENT_VERSION_NUMBER = -1;
    @NotNull
    public static final String CLIENT_VERSION = CLIENT_VERSION_NUMBER + "-BETA";
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


        if (getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")){
            if (Kernel32.INSTANCE.IsDebuggerPresent()) {
                println("Dont be a sily gay");
                doCrash();
                return;
            }
        } else {
            println("Gay");
        }

        try {
            if (HttpUtil.performGetRequest
                    (new URL("https://pastebin.com/raw/T0XipKMF")).contains("i am a man"))
                println("无论前方艰险如何 我都会在你身边");
            else
                doCrash();
        } catch (IOException e) {
            doCrash();
        }

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
    private void onMisc(UpdateEvent e) {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.blindness)) {
            Minecraft.getMinecraft().thePlayer.removePotionEffect(Potion.blindness.id);
        }

        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.confusion)) {
            Minecraft.getMinecraft().thePlayer.removePotionEffect(Potion.confusion.id);
        }
    }

    @EventTarget
    private void onWorldChange(PacketEvent e) {
        if (e.getPacket() instanceof S07PacketRespawn || e.getPacket() instanceof S01PacketJoinGame) {
            packetQueue.clear();
            ms.reset();
        }

        if (e.getPacket() instanceof S02PacketLoginSuccess)
            ProtectionThread.getInstance().runChecks();

        if (e.getPacket() instanceof S48PacketResourcePackSend) {
            ChatUtils.info("Receive A Request about resource pack one");
            ChatUtils.info("Hash: " + ((S48PacketResourcePackSend) e.getPacket()).getHash());
            ChatUtils.info("URL: " + ((S48PacketResourcePackSend) e.getPacket()).getURL());

            if (!((S48PacketResourcePackSend) e.getPacket()).getURL().toLowerCase().contains("resource")){
                ChatUtils.info("BaiPai A Nigga Check");
                Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C19PacketResourcePackStatus("MC" , C19PacketResourcePackStatus.Action.DECLINED));
                e.setCancelled(true);
            }
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
            if (Math.abs(((S2APacketParticles) e.getPacket()).getParticleSpeed()) > 10) {
                e.setCancelled(true);
            }
            if (((S2APacketParticles) e.getPacket()).getParticleCount() > 500) {
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

    public void println(String obj) {
        Class<?> systemClass = null;
        try {
            systemClass = Class.forName("java.lang.System");
            Field outField = null;
            try {
                outField = systemClass.getDeclaredField("out");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            Class<?> printStreamClass = Objects.requireNonNull(outField).getType();
            Method printlnMethod = printStreamClass.getDeclaredMethod("println", String.class);
            Object object = outField.get(null);
            printlnMethod.invoke(object, obj);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public void doCrash() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = null;
            try {
                unsafe = (Unsafe) field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Class<?> cacheClass = null;
            try {
                cacheClass = Class.forName("java.lang.Integer$IntegerCache");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Field cache = cacheClass.getDeclaredField("cache");
            long offset = unsafe.staticFieldOffset(cache);

            unsafe.putObject(Integer.getInteger("SkidSense.pub NeverDie"), offset, null);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
