import sim.Packet;
import sim.RdtReceiver;

import static sim.Packet.RDT_PKTSIZE;

/**
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
public class MyRdtReceiver extends RdtReceiver {
    /**
     * routines that you can call:
     * <p>
     * double getSimulationTime()               // get simulation time (in seconds)
     * void sendToLowerLayer(Packet packet)     // pass a packet to the lower layer at the receiver
     * void sendToUpperLayer(String message)    // deliver a message to the upper layer at the receiver
     */

    /* receiver initialization. leave it blank if you don't need it */
    public MyRdtReceiver() {
    }

    /* event handler, called when a packet is passed from the lower
       layer at the receiver */
    public void receiveFromLowerLayer(Packet packet) {
        /* todo: write code here... */
        /* the following only works over a reliable channel */

        /* 1-byte header indicating the size of the payload */
        int header_size = 1;

        /* sanity check in case the packet is corrupted */
        int size = packet.data[0] & 0xFF;
        if (size > RDT_PKTSIZE - header_size) size = RDT_PKTSIZE - header_size;

        /* construct a message and deliver to the upper layer */
        byte[] message = new byte[size];
        System.arraycopy(packet.data, header_size, message, 0, size);
        sendToUpperLayer(message);
    }
}
