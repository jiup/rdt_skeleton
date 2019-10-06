package sim;

import static sim.Packet.RDT_PKTSIZE;

/**
 * Reliable data transfer receiver
 *
 * @author Kai Shen
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
public abstract class RdtReceiver {
    private RdtSession session;

    void join(RdtSession session) {
        this.session = session;
    }

    /**
     * Event handler, called when a packet is passed from the lower
     * layer at the receiver
     */
    public abstract void receiveFromLowerLayer(Packet packet);

    /**
     * Get simulation time (in seconds)
     *
     * @return current simulation time
     */
    public double getSimulationTime() {
        return session.getTime();
    }

    /**
     * Pass a packet to the lower layer at the receiver
     */
    public void sendToLowerLayer(Packet packet) {
        if (session == null) throw new IllegalStateException("session not registered");

        // packet lost at lossRate
        if (Math.random() < session.lossRate) return;

        // packet corrupted at corruptRate
        if (Math.random() < session.corruptRate) {
            for (int i = 0; i < RDT_PKTSIZE; i++) {
                packet.data[i] += Math.random() * 20 - 10;
            }
        }

        // schedule the packet arrival event at the other side
        double latency = session.averagePacketLatency;
        if (Math.random() < session.outOfOrderRate) {
            latency *= Math.random() * 2;
        }

        session.schedule(new RdtEvent.ReceiverFromLowerLayer(packet, session.getTime() + latency));
        session.counter.packetPassed++;
    }

    /**
     * Deliver a message to the upper layer at the receiver
     */
    public void sendToUpperLayer(byte[] message) {
        if (session == null) throw new IllegalStateException("session not registered");
        session.schedule(new RdtEvent.ReceiverToUpperLayer(message, session.getTime()));
    }
}
