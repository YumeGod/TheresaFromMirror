

package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.command.CommandException;
import cn.loli.client.events.KeyEvent;
import cn.loli.client.module.Module;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import cn.loli.client.value.Value;
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
    private String owner;
    private Value<?> currentValue = null;

    public BindCommand() {
        super("bind");

        EventManager.register(this);
    }

    //TODO: 将使用抽象类来解决这堆狗屎代码 因为这很愚蠢
    //TODO: I will use abuse of abstract class to solve this shit code

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length == 0) {
            throw new CommandException("Usage: ." + alias + " <module> [<none/show/key>]");
        }
        Module mod = null;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getName().replaceAll(" ", "").equalsIgnoreCase(args[0])) {
                mod = m;
            }
        }

        if (mod == null) throw new CommandException("The module '" + args[0] + "' does not exist");

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("none")) {
                mod.setKeybind(Keyboard.KEY_NONE);
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + "NONE");
            } else if (args[1].equalsIgnoreCase("show")) {
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " is bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(mod.getKeybind()));
            } else {
                Value<?> value = null;
                try {
                    value = Main.INSTANCE.valueManager.get(args[0], args[1], true);
                    if (value != null) {
                        if (args.length > 2) {
                            if (value instanceof ModeValue) {
                                Integer mode = Integer.parseInt(args[2]);
                                Main.INSTANCE.valueManager.modeSelect.put(value, mode);
                                if (args.length > 3) {
                                    int key = Keyboard.getKeyIndex(args[3].toUpperCase());
                                    Main.INSTANCE.valueManager.ownerMap.put(value, args[0]);
                                    Main.INSTANCE.valueManager.keyBind.put(value, key);
                                    ChatUtils.success(ChatUtils.SECONDARY_COLOR + value.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(key));
                                } else {
                                    active = true;
                                    currentValue = value;
                                    owner = args[0];
                                    ChatUtils.info("Listening for keybinds for " + ChatUtils.SECONDARY_COLOR + value.getName());
                                }
                            }
                            if (value instanceof NumberValue) {
                                Number number = Double.parseDouble(args[2]);
                                Main.INSTANCE.valueManager.numberPick.put(value, number);
                                if (args.length > 3) {
                                    int key = Keyboard.getKeyIndex(args[3].toUpperCase());
                                    Main.INSTANCE.valueManager.ownerMap.put(value, args[0]);
                                    Main.INSTANCE.valueManager.keyBind.put(value, key);
                                    ChatUtils.success(ChatUtils.SECONDARY_COLOR + value.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(key));
                                } else {
                                    active = true;
                                    currentValue = value;
                                    owner = args[0];
                                    ChatUtils.info("Listening for keybinds for " + ChatUtils.SECONDARY_COLOR + value.getName());
                                }
                            }
                            if (value instanceof BooleanValue) {
                                int key = Keyboard.getKeyIndex(args[2].toUpperCase());
                                Main.INSTANCE.valueManager.ownerMap.put(value, args[0]);
                                Main.INSTANCE.valueManager.keyBind.put(value, key);
                                ChatUtils.success(ChatUtils.SECONDARY_COLOR + value.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(key));
                            }
                        } else {
                            active = true;
                            currentValue = value;
                            owner = args[0];
                            ChatUtils.info("Listening for keybinds for " + ChatUtils.SECONDARY_COLOR + value.getName());
                        }
                        return;
                    }
                } catch (Exception e) {
                    if (value != null)
                        Main.INSTANCE.println(ChatUtils.ERROR_COLOR + "The value '" + args[1] + "' throw an exception: " + e.getMessage());
                }

                int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                mod.setKeybind(key);
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + mod.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(key));
            }
            return;
        }

        active = true;
        currentModule = mod;

        ChatUtils.info("Listening for keybinds for " + ChatUtils.SECONDARY_COLOR + mod.getName());
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
            if (currentModule != null) {
                currentModule.setKeybind(event.getKey());
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + currentModule.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(event.getKey()));
            } else {
                Main.INSTANCE.valueManager.ownerMap.put(currentValue, owner);
                Main.INSTANCE.valueManager.keyBind.put(currentValue, event.getKey());
                ChatUtils.success(ChatUtils.SECONDARY_COLOR + currentValue.getName() + ChatUtils.PRIMARY_COLOR + " was bound to " + ChatUtils.SECONDARY_COLOR + Keyboard.getKeyName(event.getKey()));
            }


            active = false;
            currentModule = null;
        }
    }
}
