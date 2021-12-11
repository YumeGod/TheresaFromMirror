

package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.command.CommandException;
import cn.loli.client.utils.ChatUtils;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class ScriptCommand extends Command {

    public ScriptCommand() {
        super("script", "js");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            throw new CommandException("Usage: ." + alias + " <new/eval> [<script>]");
        }
        if (args[0].equalsIgnoreCase("new")) {
            Main.INSTANCE.scriptManager.newScript();
            ChatUtils.info("New script created.");
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("eval")) {
            StringBuilder builder = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]);

                if (i != args.length - 1) {
                    builder.append(" ");
                }
            }

            try {
                ChatUtils.info("Result: " + Main.INSTANCE.scriptManager.eval(builder.toString()));
            } catch (ScriptException e) {
                throw new CommandException(e.toString());
            }
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
