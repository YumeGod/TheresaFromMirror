package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.connection.Packet;
import cn.loli.client.connection.PacketUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IrcCommand extends Command {
    public IrcCommand() {
        super("irc", "irc", "irc");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: .irc <content>");
            return;
        }

        Main.cf.channel().writeAndFlush(new Packet(PacketUtil.Type.MESSAGE, Main.INSTANCE.name + ChatFormatting.WHITE + ": " + ChatFormatting.GRAY + args[0]).pack());
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return null;
    }
}
