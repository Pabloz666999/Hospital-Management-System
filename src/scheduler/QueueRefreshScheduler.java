package scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueRefreshScheduler {

    private final ScheduledExecutorService scheduler;
    private final Runnable refreshTask;

    public QueueRefreshScheduler(Runnable refreshTask) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.refreshTask = refreshTask;
    }

    public void start(long initialDelayMillis, long periodMillis) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                refreshTask.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, initialDelayMillis, periodMillis, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}

