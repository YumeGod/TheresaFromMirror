

package cn.loli.client.notifications;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class NotificationManager {
    @NotNull
    private static ArrayList<Notification> pendingNotifications = new ArrayList<>();

    public static void show(Notification notification) {
//        if (Minecraft.getMinecraft().currentScreen != null)
        pendingNotifications.add(notification);
    }

    public static void update() {
        int remove = -1;
        for (int i = 0; i < pendingNotifications.size(); i++) {
            Notification pendingNotification = pendingNotifications.get(i);
            if (pendingNotification.getStart() <= 0) {
                pendingNotification.show();
            }
            if (!pendingNotification.isShown()) {
                remove = i;
            }
        }
        if (remove != -1) {
            pendingNotifications.remove(remove);
        }
    }

    public static void render() {
        update();

        short num = 0;
        for (Notification pendingNotification : pendingNotifications) {
            pendingNotification.render(num);
            num++;
        }
    }
}
