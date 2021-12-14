

package cn.loli.client;

import cn.loli.client.command.CommandManager;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.file.FileManager;
import cn.loli.client.module.ModuleManager;
import cn.loli.client.module.modules.misc.ClickGUIModule;
import cn.loli.client.scripting.ScriptManager;
import cn.loli.client.utils.SoundFxPlayer;
import cn.loli.client.value.ValueManager;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.server.S01PacketJoinGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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
    public ScriptManager scriptManager;

    public Main() {
        INSTANCE = this;
        EventManager.register(this);
    }

    public void startClient() {
        logger = LogManager.getLogger();
        scriptManager = new ScriptManager();
        fileManager = new FileManager();
        valueManager = new ValueManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();

        fileManager.loadScripts();
        commandManager.addCommands();
        moduleManager.addModules();
        fileManager.load();

        moduleManager.getModule(ClickGUIModule.class).createClickGui();

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
    public void onWorldChange(PacketEvent e) {
   //     if (e.getPacket() instanceof S01PacketJoinGame)
         //   new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.EDITION, -7);
    }

    
}
