package cn.loli.client.utils.misc.antixray;


import cn.loli.client.utils.misc.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.text.DecimalFormat;

public class Runner implements Runnable {
    boolean isRunning = true;
    public boolean done = false;
    long delay;
    int current;
    int max;
    int radX;
    int radY;
    int radZ;

    public Runner(int radX, int radY, int radZ, long delay) {
        this.max = (radX + radX + 1) * (radY + radY + 1) * (radZ + radZ + 1);
        this.radX = radX;
        this.radY = radY;
        this.radZ = radZ;
        this.delay = delay;
    }

    public double getProcess() {
        return (double) current / (double) max * 100.0D;
    }

    public String getProcessText() {
        return new DecimalFormat("0.00").format(getProcess());
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        NetHandlerPlayClient conn = Minecraft.getMinecraft().getNetHandler();
        if (conn == null) return;
        assert Minecraft.getMinecraft().thePlayer != null;
        BlockPos pos = Minecraft.getMinecraft().thePlayer.getPosition();

        for (int cx = -radX; cx <= radX; cx++) {
            for (int cy = -radY; cy <= radY; cy++) {
                for (int cz = -radZ; cz <= radZ; cz++) {
                    if (!isRunning) break;
                    current++;
                    BlockPos currblock = new BlockPos(pos.getX() + cx, pos.getY() + cy, pos.getZ() + cz);

                    C07PacketPlayerDigging packet = new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            currblock,
                            EnumFacing.UP
                    );
                    conn.getNetworkManager().sendPacket(packet);
                    packet = new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            currblock,
                            EnumFacing.UP
                    );
                    conn.getNetworkManager().sendPacket(packet);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }
        ChatUtils.info("§6[ §a！ §6] §fRefresh done.");
        done = true;
    }
}
