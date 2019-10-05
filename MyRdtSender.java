import sim.Packet;
import sim.RdtSender;

import static sim.Packet.RDT_PKTSIZE;

/**
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
public class MyRdtSender extends RdtSender {
    /**
     * routines that you can call:
     * <p>
     * double getSimulationTime()               // get simulation time (in seconds)
     * void startTimer(double timeout)          // set a specified timeout (in seconds)
     * void stopTimer()                         // stop the sender timer
     * boolean isTimerSet()                     // check whether the sender timer is being set
     * void sendToLowerLayer(Packet packet)     // pass a packet to the lower layer at the sender
     */

    /* sender initialization. leave it blank if you don't need it */
    public MyRdtSender() {
    }

    /* event handler, called when a message is passed from the upper
       layer at the sender */
    public void receiveFromUpperLayer(byte[] message) {
        /* todo: write code here... */
        /* modify the following to a reliable version */

        /* 1-byte header indicating the size of the payload */
        int header_size = 1;

        /* maximum payload size */
        int maxpayload_size = RDT_PKTSIZE - header_size;

        /* split the message if it is too big */

        /* the cursor always points to the first unsent byte in the message */
        int cursor = 0;

        while (message.length - cursor > maxpayload_size) {
            /* fill in the packet */
            Packet pkt = new Packet();
            pkt.data[0] = (byte) maxpayload_size;
            System.arraycopy(message, cursor, pkt.data, header_size, maxpayload_size);

            /* send it out through the lower layer */
            sendToLowerLayer(pkt);

            /* move the cursor */
            cursor += maxpayload_size;
        }

        /* send out the last packet */
        if (message.length > cursor) {
            /* fill in the packet */
            Packet pkt = new Packet();
            pkt.data[0] = (byte) (message.length - cursor);
            System.arraycopy(message, cursor, pkt.data, header_size, pkt.data[0]);

            /* send it out through the lower layer */
            sendToLowerLayer(pkt);
        }
    }

    /* event handler, called when a packet is passed from the lower
       layer at the sender */
    public void receiveFromLowerLayer(Packet packet) {
        /* todo: write code here... */
    }

    /* event handler, called when the timer expires */
    public void onTimeout() {
        /* todo: write code here... */
    }
}
