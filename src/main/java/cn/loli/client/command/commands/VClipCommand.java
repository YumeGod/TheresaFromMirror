package cn.loli.client.command.commands;

import cn.loli.client.command.Command;
import cn.loli.client.command.CommandException;
import net.minecraft.client.Minecraft;

import java.util.List;

public class VClipCommand extends Command {

    public VClipCommand() {
        super("VClip", "vclip");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1)
            throw new CommandException("type the value");

        try {
            Minecraft.getMinecraft().thePlayer.setPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Double.parseDouble(args[0]), Minecraft.getMinecraft().thePlayer.posZ);
        } catch (Exception e) {
            throw new CommandException("type the right value");
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return null;
    }
}
