package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.command.CommandException;
import cn.loli.client.connection.Entity;
import cn.loli.client.connection.Packet;
import cn.loli.client.connection.PacketUtil;
import cn.loli.client.utils.misc.ChatUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", "cfg");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length < 2)
            throw new CommandException("Usage: .config/cfg <load,save> <name>");

        try {
            if (args[0].equals("load")) {
                Main.INSTANCE.fileManager.load(args[1]);

            } else {
                Main.INSTANCE.fileManager.saveCfg(args[1]);
            }
        } catch (Exception e) {
            throw new CommandException("Break: " + e.getMessage());
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return null;
    }
}
