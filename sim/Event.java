package sim;

/**
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
abstract class Event {
    private double scheduledTime;

    public Event(double scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    double getScheduledTime() {
        return scheduledTime;
    }

    void reschedule(double scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}