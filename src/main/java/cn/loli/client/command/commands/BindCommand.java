

package cn.loli.client.command.commands;

import cn.loli.client.command.CommandException;
import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.events.KeyEvent;
import cn.loli.client.module.Module;
import cn.loli.client.utils.ChatUtils;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BindCommand extends Command {
    private boolean active = false;
    @Nullable
    private Module currentModule = null;

    public BindCommand() {
        super("bind");

        EventManager.register(this);
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length == 0) {
            throw new CommandException("Usage: ." + alias + " <module> [<none/show>]");
        }

        Module mod = Main.INSTANCE.moduleManager.getModule(args[0], false);

        if (mod == null) throw new CommandException("The module '" + args[0] + "' does not exist");

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("none")) {
                mod.setKeybind(Keyboard.KEY_NONE);
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + "NONE");
            } else
            if (args[1].equalsIgnoreCase("show")) {
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " is bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(mod.getKeybind()));
            } else {
                int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                mod.setKeybind(key);
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(key));
            }
            return;
        }

        active = true;
        currentModule = mod;

        ChatUtils.info("Please press a key");
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
        } else if (arg == 2) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("none");
            arrayList.add("show");
            return arrayList;
        } else return new ArrayList<>();
    }

    @EventTarget
    public void onKey(@NotNull KeyEvent event) {
        if (active) {
            currentModule.setKeybind(event.getKey());

            ChatUtils.success(ChatUtils.SECONDARY_COLOR + currentModule.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(event.getKey()));

            active = false;
            currentModule = null;
        }
    }
}
