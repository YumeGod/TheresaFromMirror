

package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.command.CommandException;
import cn.loli.client.utils.misc.ChatUtils;
import dev.xix.property.AbstractTheresaProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ValueCommand extends Command {
    public ValueCommand() {
        super("value", "val", "v", "set");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length < 3) {
            throw new CommandException("Usage: ." + alias + " <owner> <name> <value>");
        }

        AbstractTheresaProperty value = Main.INSTANCE.valueManager.get(args[0], args[1], true);

        if (value == null) {
            throw new CommandException("Value '" + args[0] + "/" + args[1] + "' doesn't exist");
        }

        String val = args[2];

        if (value.getPropertyValue() instanceof Boolean) {
            boolean newVal = false;
            boolean ok = false;

            if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("on") || val.equalsIgnoreCase("1")) {
                newVal = true;
                ok = true;
            }
            if (val.equalsIgnoreCase("false") || val.equalsIgnoreCase("off") || val.equalsIgnoreCase("0")) {
                newVal = true;
                ok = true;
            }

            if (ok) {
                value.setPropertyValue(newVal);
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + args[0] + "/" + args[1] + ChatUtils.PRIMARY_COLOR + " was set to " + ChatUtils.SECONDARY_COLOR + val);
            } else {
                throw new CommandException(val + " is not valid (allowed: true, false)");
            }
        }
        if (value.getPropertyValue() instanceof Integer) {
            try {
                value.setPropertyValue(Integer.parseInt(val));
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + args[0] + "/" + args[1] + ChatUtils.PRIMARY_COLOR + " was set to " + ChatUtils.SECONDARY_COLOR + val);
            } catch (NumberFormatException e) {
                throw new CommandException("'" + val + " is not a valid int");
            }
        }
        if (value.getPropertyValue() instanceof Long) {
            try {
                value.setPropertyValue(Long.parseLong(val));
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + args[0] + "/" + args[1] + ChatUtils.PRIMARY_COLOR + " was set to " + ChatUtils.SECONDARY_COLOR + val);
            } catch (NumberFormatException e) {
                throw new CommandException("'" + val + " is not a valid long");
            }
        }
        if (value.getPropertyValue() instanceof Float) {
            try {
                value.setPropertyValue(Float.parseFloat(val));
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + args[0] + "/" + args[1] + ChatUtils.PRIMARY_COLOR + " was set to " + ChatUtils.SECONDARY_COLOR + val);
            } catch (NumberFormatException e) {
                throw new CommandException("'" + val + " is not a valid float");
            }
        }
        if (value.getPropertyValue() instanceof Double) {
            try {
                value.setPropertyValue(Double.parseDouble(val));
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + args[0] + "/" + args[1] + ChatUtils.PRIMARY_COLOR + " was set to " + ChatUtils.SECONDARY_COLOR + val);
            } catch (NumberFormatException e) {
                throw new CommandException("'" + val + " is not a valid double");
            }
        }
    }

    @NotNull
    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
