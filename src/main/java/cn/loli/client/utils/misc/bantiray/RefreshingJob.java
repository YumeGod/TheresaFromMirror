package cn.loli.client.utils.misc.bantiray;

public class RefreshingJob {
    public Runner refresher;
    public Thread runner;

    public RefreshingJob(Runner refresher) {
        this.refresher = refresher;
        this.runner = new Thread(refresher);
        this.runner.start();
    }

    public void cancel() {
        refresher.isRunning = false;
    }
}
