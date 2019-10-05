package sim;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
class RdtSession {
    Counter counter;
    private double time;
    private PriorityQueue<Event> events;

    double simulationTime = 1000;           /* total simulation time */
    double messageInterval = 0.2;           /* intervals between upper messages arrival */
    int averageMessageSize = 100;           /* average size of messages (in bytes) */
    double averagePacketLatency = 0.2;      /* average one-way latency (in seconds) */
    double outOfOrderRate = 0.2;            /* probability of abnormal latency */
    double lossRate = 0.2;                  /* probability of packet loss */
    double corruptRate = 0.2;               /* probability of packet corruption */

    static class Counter {
        long sent = 0;
        long delivered = 0;
        long packetPassed = 0;
        long failure = 0;
    }

    RdtSession(double initTime) {
        this.time = initTime;
        this.events = new PriorityQueue<>(Comparator.comparingDouble(Event::getScheduledTime));
        this.counter = new Counter();
    }

    void schedule(Event e) {
        if (e.getScheduledTime() < time) return;
        events.add(e);
        time += 0.001;
    }

    void cancel(Event e) {
        events.remove(e);
    }

    boolean hasNext() {
        return !events.isEmpty();
    }

    Event next() {
        if (events.isEmpty()) return null;

        Event e = events.poll();
        time = e.getScheduledTime();
        return e;
    }

    double getTime() {
        return time;
    }
}
