import java.time.LocalDate;

/**
 * ExpiryMonitor.java
 *
 * Implements Architecture §8 & PRD §10.8: Multithreaded Background Expiry Monitor
 * Runs as a background daemon thread that periodically checks all purchased policies.
 * Automatically marks policies whose expiry date has passed as EXPIRED.
 * 
 * Safety constraints adhered to:
 * - Does not overwrite CANCELLED policies (PRD §15).
 * - Avoids intrusive console printing while the user is typing (Architecture §8).
 * - Controlled by a volatile running flag and responds cleanly to interrupt().
 */
public class ExpiryMonitor extends Thread {

    private final InsuranceManager manager;
    private volatile boolean running;
    private final int intervalMillis;
    private int totalExpiredDetected;

    public ExpiryMonitor(InsuranceManager manager) {
        this(manager, 15000); // Check every 15 seconds by default
    }

    public ExpiryMonitor(InsuranceManager manager, int intervalMillis) {
        super("ExpiryMonitor-Thread");
        this.manager = manager;
        this.intervalMillis = (intervalMillis < 1000) ? 5000 : intervalMillis;
        this.running = true;
        this.totalExpiredDetected = 0;
        setDaemon(true); // Allows JVM to exit cleanly if main terminates
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (manager != null) {
                    int newlyExpired = manager.checkAndRefreshPolicyStatuses(LocalDate.now());
                    if (newlyExpired > 0) {
                        totalExpiredDetected += newlyExpired;
                    }
                }
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                // Thread interrupted on shutdown
                break;
            } catch (Exception e) {
                // Safeguard against unexpected errors in background thread
                System.err.println("[ExpiryMonitor Warning] Background check error: " + e.getMessage());
            }
        }
    }

    /**
     * Gracefully stops the monitoring thread.
     */
    public void stopMonitoring() {
        this.running = false;
        this.interrupt();
    }

    public boolean isMonitoring() {
        return running && isAlive();
    }

    public int getTotalExpiredDetected() {
        return totalExpiredDetected;
    }
}
