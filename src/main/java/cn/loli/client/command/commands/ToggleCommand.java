

package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.command.CommandException;
import cn.loli.client.module.Module;
import cn.loli.client.utils.ChatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle", "t");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length < 1) {
            throw new CommandException("Usage: ." + alias + " <module> [<on/off>]");
        }
        Module mod = Main.INSTANCE.moduleManager.getModule(args[0], false);

        if (mod == null) throw new CommandException("The module '" + args[0] + "' does not exist");

        boolean state = !mod.getState();

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("on")) state = true;
            else if (args[1].equalsIgnoreCase("off")) state = false;
            else throw new CommandException("Usage: ." + alias + " <module> <on/off>");
        }

        mod.setState(state);

        ChatUtils.success(mod.getName() + " was " + ChatUtils.PRIMARY_COLOR + (state ? "enabled" : "disabled"));
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        String prefix = "";
        boolean flag = false;

        if (arg == 0 || args.length == 0) {
            flag = true;
        } else if (arg == 1) {
            flag = true;
            prefix = args[0];
        }

        if (flag) {
            String finalPrefix = prefix;
            return Main.INSTANCE.moduleManager.getModules().stream().filter(mod -> mod.getName().toLowerCase().startsWith(finalPrefix)).map(Module::getName).collect(Collectors.toList());
        } else return new ArrayList<>();
    }
}
