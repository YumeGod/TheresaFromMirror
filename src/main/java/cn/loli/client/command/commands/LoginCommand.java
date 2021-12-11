

package cn.loli.client.command.commands;

import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.command.Command;
import cn.loli.client.command.CommandException;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.jetbrains.annotations.NotNull;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class LoginCommand extends Command {
    public LoginCommand() {
        super("login", "alt");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length < 1) {
            throw new CommandException("Usage: ." + alias + " <username:password> or <username> <password>");
        }
        String username;
        String password;

        if (args.length == 1) {
            if (!args[0].contains(":"))
                throw new CommandException("Usage: ." + alias + " <username:password> or <username> <password>");

            String[] split = args[0].split(":");

            if (split.length != 2) {
                throw new CommandException("Usage: ." + alias + " <username:password> or <username> <password>");
            }

            username = split[0];
            password = split[1];
        } else {
            username = args[0];
            password = args[1];
        }
        try {
            Session session = Utils.createSession(username, password, Proxy.NO_PROXY);

            ((IAccessorMinecraft) Minecraft.getMinecraft()).setSession(session);

            ChatUtils.success("Logged in. New IGN: " + session.getUsername());
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        } finally {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(0);
        }
    }

    @NotNull
    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
