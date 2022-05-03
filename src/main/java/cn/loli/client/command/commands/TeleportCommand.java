package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.module.modules.misc.Abuser;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.utils.misc.ChatUtils;
import net.minecraft.client.Minecraft;

import java.util.List;

public class TeleportCommand extends Command {

    public TeleportCommand() {
        super("teleport", "tp");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 3) {
            NotificationManager.show(new Notification(NotificationType.INFO, "Teleport", "Usage: ." + alias + " x , y , z", 5));
            return;
        }

        try {
            Main.INSTANCE.pos = new int[]{Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])};
            if (!Main.INSTANCE.moduleManager.getModule(Abuser.class).getState()){
                ChatUtils.info("Failure to teleport, please enable the Abuser module.");
                return;
            }

            new Thread(() -> {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/hub");
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ignored) {}
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/rejoin");
                ChatUtils.info("Try to teleport to " + Main.INSTANCE.pos[0] + "," + Main.INSTANCE.pos[1] + "," + Main.INSTANCE.pos[2]);
            }).start();

        } catch (Exception e) {
            NotificationManager.show(new Notification(NotificationType.ERROR, "Teleport", "Error: " + e.getMessage(), 5));
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return null;
    }
}
