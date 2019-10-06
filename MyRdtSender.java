import sim.Packet;
import sim.RdtSender;

import static sim.Packet.RDT_PKTSIZE;

/**
 * My reliable data transfer sender
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
 *     Routines that you can call at the sender:
 *     {@link #getSimulationTime()}         get simulation time (in seconds)
 *     {@link #startTimer(double)}          set a specified timeout (in seconds)
 *     {@link #stopTimer()}                 stop the sender timer
 *     {@link #isTimerSet()}                check whether the sender timer is being set
 *     {@link #sendToLowerLayer(Packet)}    pass a packet to the lower layer at the sender
 * </pre>
 *
 * @author Jiupeng Zhang
 * @author Kai Shen
 * @since 10/04/2019
 */
public class MyRdtSender extends RdtSender {
    /**
     * Sender initialization
     */
    public MyRdtSender() {
    }

    /**
     * Event handler, called when a message is passed from the upper
     * layer at the sender
     */
    public void receiveFromUpperLayer(byte[] message) {
        // todo: write code here...

        // 1-byte header indicating the size of the payload
        int header_size = 1;

        // maximum payload size
        int maxpayload_size = RDT_PKTSIZE - header_size;

        // split the message if it is too big

        // the cursor always points to the first unsent byte in the message
        int cursor = 0;

        while (message.length - cursor > maxpayload_size) {
            // fill in the packet
            Packet pkt = new Packet();
            pkt.data[0] = (byte) maxpayload_size;
            System.arraycopy(message, cursor, pkt.data, header_size, maxpayload_size);

            // send it out through the lower layer
            sendToLowerLayer(pkt);

            // move the cursor
            cursor += maxpayload_size;
        }

        // send out the last packet
        if (message.length > cursor) {
            // fill in the packet
            Packet pkt = new Packet();
            pkt.data[0] = (byte) (message.length - cursor);
            System.arraycopy(message, cursor, pkt.data, header_size, pkt.data[0]);

            // send it out through the lower layer
            sendToLowerLayer(pkt);
        }
    }

    /**
     * Event handler, called when a packet is passed from the lower
     * layer at the sender
     */
    public void receiveFromLowerLayer(Packet packet) {
        // todo: write code here...
    }

    /**
     * Event handler, called when the timer expires
     */
    public void onTimeout() {
        // todo: write code here...
    }
}
