package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.script.lua.LuaManager;

import java.util.List;

public class ReloadLuaCommand extends Command {
    protected ReloadLuaCommand(String name, String... aliases) {
        super("lua", "script");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args[0].equals("reload")) {
            Main.INSTANCE.luaManager.reload();
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return null;
    }
}