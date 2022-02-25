package cn.loli.client.utils.misc.antixray;

import java.util.ArrayList;
import java.util.List;

public class Resolver {
    public static List<RefreshingJob> jobs = new ArrayList<>();

    public static void revealNewBlocks(int radX, int radY, int radZ, long delayInMS) {
        RefreshingJob rfj = new RefreshingJob(new Runner(radX, radY, radZ, delayInMS));
        jobs.add(rfj);
    }
}
