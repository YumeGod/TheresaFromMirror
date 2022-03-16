package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.connection.Entity;
import cn.loli.client.connection.Packet;
import cn.loli.client.connection.PacketUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IrcCommand extends Command {
    public IrcCommand() {
        super("irc", "chat", "ircchat");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length == 0) {
            Main.INSTANCE.println("Usage: .irc <content>");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }

        Main.INSTANCE.cf.channel().writeAndFlush(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey), PacketUtil.Type.MESSAGE,
                Main.INSTANCE.name + ChatFormatting.WHITE + ": " + ChatFormatting.GRAY + sb).pack());
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return null;
    }
}
