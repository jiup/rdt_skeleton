import sim.Packet;
import sim.RdtReceiver;

import static sim.Packet.RDT_PKTSIZE;

/**
 * My reliable data transfer receiver
 * <pre>
 *     This implementation assumes there is no packet loss, corruption,
 *     or reordering. You will need to enhance it to deal with all these
 *     situations. In this implementation, the packet format is laid out
 *     as the following:
 *
 *     |<-  1 byte  ->|<-             the rest            ->|
 *     | payload size |<-             payload             ->|
 *
 *     The first byte of each packet indicates the size of the payload
 *     (excluding this single-byte header)
 *
 *     Routines that you can call at the receiver:
 *     {@link #getSimulationTime()}         get simulation time (in seconds)
 *     {@link #sendToLowerLayer(Packet)}    pass a packet to the lower layer at the receiver
 *     {@link #sendToUpperLayer(byte[])}    deliver a message to the upper layer at the receiver
 * </pre>
 *
 * @author Jiupeng Zhang
 * @author Kai Shen
 * @since 10/04/2019
 */
public class MyRdtReceiver extends RdtReceiver {
    /**
     * Receiver initialization
     */
    public MyRdtReceiver() {
    }

    /**
     * Event handler, called when a packet is passed from the lower
     * layer at the receiver
     */
    public void receiveFromLowerLayer(Packet packet) {
        // todo: write code here...

        // 1-byte header indicating the size of the payload
        int header_size = 1;

        // sanity check in case the packet is corrupted
        int size = packet.data[0] & 0xFF;
        if (size > RDT_PKTSIZE - header_size) size = RDT_PKTSIZE - header_size;

        // construct a message and deliver it to the upper layer
        byte[] message = new byte[size];
        System.arraycopy(packet.data, header_size, message, 0, size);
        sendToUpperLayer(message);
    }
}
