package sim;

import static sim.Packet.RDT_PKTSIZE;

/**
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
public abstract class RdtReceiver {
    RdtSession session;

    /* event handler, called when a packet is passed from the lower
       layer at the receiver */
    public abstract void receiveFromLowerLayer(Packet packet);

    /* get simulation time (in seconds) */
    public double getSimulationTime() {
        return session.getTime();
    }

    /* pass a packet to the lower layer at the receiver */
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

    /* deliver a message to the upper layer at the receiver */
    public void sendToUpperLayer(byte[] message) {
        if (session == null) throw new IllegalStateException("session not registered");
        session.schedule(new RdtEvent.ReceiverToUpperLayer(message, session.getTime()));
    }
}