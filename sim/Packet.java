package sim;

import java.util.Random;

/**
 * a data unit passed between rdt layer and the lower layer, each
 * packet has a fixed size of 64
 */
public class Packet {
    public static final int RDT_PKTSIZE = 64;
    private static Random random = new Random();

    public byte[] data;

    public Packet() {
        this.data = new byte[RDT_PKTSIZE];
        /* initialize bytes randomly */
        random.nextBytes(this.data);
    }
}
