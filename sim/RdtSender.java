package sim;

import static sim.Packet.RDT_PKTSIZE;

/**
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
public abstract class RdtSender {
    RdtSession session;
    RdtEvent.SenderTimeout timer;

    /* event handler, called when a message is passed from the upper
       layer at the sender */
    public abstract void receiveFromUpperLayer(byte[] message);

    /* event handler, called when a packet is passed from the lower
       layer at the sender */
    public abstract void receiveFromLowerLayer(Packet packet);

    /* event handler, called when the timer expires */
    public abstract void onTimeout();

    /* get simulation time (in seconds) */
    public double getSimulationTime() {
        return session.getTime();
    }

    /* start the sender timer with a specified timeout (in seconds).
       the timer is canceled with stopTimer() is called or a new
       startTimer() is called before the current timer expires.
       onTimeout() will be called when the timer expires. */
    public void startTimer(double timeout) {
        if (isTimerSet()) stopTimer();
        RdtEvent.SenderTimeout e = new RdtEvent.SenderTimeout(session.getTime() + timeout);
        session.schedule(e);
        timer = e;
    }

    /* stop the sender timer */
    public void stopTimer() {
        if (timer != null) {
            session.cancel(timer);
            timer = null;
        }
    }

    /* check whether the sender timer is being set,
       return true if the timer is set, return false otherwise */
    public boolean isTimerSet() {
        return timer != null;
    }

    /* pass a packet to the lower layer at the sender */
    public void sendToLowerLayer(Packet packet) {
        if (session == null) throw new IllegalStateException("session not registered");

        /* packet lost at rate "loss_rate" */
        if (Math.random() < session.lossRate) return;

        /* packet corrupted at rate "corrupt_rate" */
        if (Math.random() < session.corruptRate) {
            for (int i = 0; i < RDT_PKTSIZE; i++) {
                packet.data[i] += Math.random() * 20 - 10;
            }
        }

        /* schedule the packet arrival event at the other side */
        double latency = session.averagePacketLatency;
        if (Math.random() < session.outOfOrderRate) {
            latency *= Math.random() * 2;
        }

        session.schedule(new RdtEvent.ReceiverFromLowerLayer(packet, session.getTime() + latency));
        session.counter.packetPassed++;
    }
}
