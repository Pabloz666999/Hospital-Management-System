package model;

public interface QueueService {
    String generateQueueNumber(int lastNumber);
    boolean isServiceAvailable();
}
