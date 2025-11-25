package scheduler;

import javax.swing.Timer;

public class CounterHighlightScheduler {

    private final int counterCount;
    private final CounterHighlightListener listener;
    private final Timer timer;
    private int currentIndex = -1;

    public CounterHighlightScheduler(int counterCount, int intervalMillis, CounterHighlightListener listener) {
        this.counterCount = counterCount;
        this.listener = listener;
        this.timer = new Timer(intervalMillis, e -> advance());
    }

    private void advance() {
        if (counterCount <= 0) {
            return;
        }
        currentIndex = (currentIndex + 1) % counterCount;
        if (listener != null) {
            listener.onCounterChanged(currentIndex);
        }
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public interface CounterHighlightListener {
        void onCounterChanged(int activeIndex);
    }
}

