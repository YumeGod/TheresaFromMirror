package cn.loli.client.command.commands;

import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.command.Command;
import cn.loli.client.command.CommandException;
import cn.loli.client.utils.misc.ChatUtils;
/*
import me.ratsiel.auth.model.mojang.MinecraftAuthenticator;
import me.ratsiel.auth.model.mojang.MinecraftToken;
import me.ratsiel.auth.model.mojang.profile.MinecraftProfile;
*/
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MSLoginCommand extends Command {
    public MSLoginCommand() {
        super("mslogin", "msaltlogin", "msalt", "msaltlogin");
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
            //TODO: 源依赖被owner删除
            /*
            MinecraftAuthenticator minecraftAuthenticator = new MinecraftAuthenticator();
            MinecraftToken minecraftToken = minecraftAuthenticator.loginWithXbox(username, password);
            MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
            Session session = new Session(minecraftProfile.getUsername(), minecraftProfile.getUuid().toString(), minecraftToken.getAccessToken(), "mojang");
            ((IAccessorMinecraft) Minecraft.getMinecraft()).setSession(session);

            ChatUtils.success("Logged in. New IGN: " + session.getUsername());
            */
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
