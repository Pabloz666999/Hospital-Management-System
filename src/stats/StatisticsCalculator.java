package stats;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StatisticsCalculator {

    private final ExecutorService executor;

    public StatisticsCalculator() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public <T> Future<T> calculateAsync(Callable<T> task) {
        return executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
