

package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.utils.misc.ChatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "commands", "cmds");
    }

    @Override
    public void run(String alias, String[] args) {
        ArrayList<String> commands = new ArrayList<>();
        Main.INSTANCE.commandManager.getCommands().forEach(command -> commands.addAll(command.getNameAndAliases()));

        ChatUtils.info("Available commands: " + Arrays.toString(commands.stream().map(str -> "." + str).toArray()));
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
