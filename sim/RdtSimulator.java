package sim;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
public class RdtSimulator {
    private static final Logger LOG = Logger.getLogger(RdtSimulator.class.getName());

    private RdtSession session;
    private RdtSender sender;
    private RdtReceiver receiver;

    public RdtSimulator(RdtSender sender, RdtReceiver receiver) {
        this.session = new RdtSession(0);
        this.sender = sender;
        this.receiver = receiver;
        this.sender.session = this.session;
        this.receiver.session = this.session;
    }

    public void run() {
        System.out.printf("## Reliable data transfer simulation with:\n" +
                        "\tsimulation time is %.3f seconds\n" +
                        "\taverage message arrival interval is %.3f seconds\n" +
                        "\taverage message size is %d bytes\n" +
                        "\taverage out-of-order delivery rate is %.2f%%\n" +
                        "\taverage loss rate is %.2f%%\n" +
                        "\taverage corrupt rate is %.2f%%\n" +
                        "Please review these inputs and press <enter> to proceed.\n",
                session.simulationTime, session.messageInterval, session.averageMessageSize,
                session.outOfOrderRate * 100.0, session.lossRate * 100.0, session.corruptRate * 100.0);
        new Scanner(System.in).nextLine();

        session.schedule(new RdtEvent.SenderFromUpperLayer(0));
        while (session.hasNext()) {
            Event e = session.next();
            if (e.getClass() == RdtEvent.SenderFromUpperLayer.class) {
                handleSenderFromUpperLayer((RdtEvent.SenderFromUpperLayer) e);
            } else if (e.getClass() == RdtEvent.SenderFromLowerLayer.class) {
                handleSenderFromLowerLayer((RdtEvent.SenderFromLowerLayer) e);
            } else if (e.getClass() == RdtEvent.SenderTimeout.class) {
                handleSenderTimeout((RdtEvent.SenderTimeout) e);
            } else if (e.getClass() == RdtEvent.ReceiverFromLowerLayer.class) {
                handleReceiverFromLowerLayer((RdtEvent.ReceiverFromLowerLayer) e);
            } else if (e.getClass() == RdtEvent.ReceiverToUpperLayer.class) {
                handleReceiverToUpperLayer((RdtEvent.ReceiverToUpperLayer) e);
            }
        }

        System.out.printf("## Simulation completed at time %.2fs with\n" +
                        "\t%d characters sent\n\t%d characters delivered\n" +
                        "\t%d packets passed between the sender and the receiver\n",
                session.getTime(), session.counter.sent, session.counter.delivered, session.counter.packetPassed);

        if (session.counter.failure == 0 && session.counter.sent == session.counter.delivered) {
            System.out.println("## Congratulations! This session is error-free, loss-free, and in order.\n");
        } else {
            System.out.println("## Something is wrong! This session is NOT error-free, loss-free, and in order.\n");
        }
    }

    public void setSimulationTime(double simulationTime) {
        session.simulationTime = simulationTime;
    }

    public void setMessageInterval(double messageInterval) {
        session.messageInterval = messageInterval;
    }

    public void setAverageMessageSize(int averageMessageSize) {
        session.averageMessageSize = averageMessageSize;
    }

    public void setOutOfOrderRate(double outOfOrderRate) {
        session.outOfOrderRate = outOfOrderRate;
    }

    public void setLossRate(double lossRate) {
        session.lossRate = lossRate;
    }

    public void setCorruptRate(double corruptRate) {
        session.corruptRate = corruptRate;
    }

    private void handleSenderFromUpperLayer(RdtEvent.SenderFromUpperLayer e) {
        int size = (int) (Math.random() * 2 * session.averageMessageSize);
        byte[] message = generateMessage(size);
        session.counter.sent += message.length;
        sender.receiveFromUpperLayer(message);
        if (session.getTime() < session.simulationTime) {
            e.reschedule(session.getTime() + Math.random() * 2 * session.messageInterval);
            session.schedule(e);
        }
    }

    private void handleSenderFromLowerLayer(RdtEvent.SenderFromLowerLayer e) {
        sender.receiveFromLowerLayer(e.getPacket());
    }

    private void handleSenderTimeout(RdtEvent.SenderTimeout e) {
        sender.onTimeout();
    }

    private void handleReceiverFromLowerLayer(RdtEvent.ReceiverFromLowerLayer e) {
        receiver.receiveFromLowerLayer(e.getPacket());
    }

    private void handleReceiverToUpperLayer(RdtEvent.ReceiverToUpperLayer e) {
        byte[] message = e.getMessage();
        session.counter.delivered += message.length;
        if (!validateMessage(message))
            System.err.println("corrupted message: " + new String(message, StandardCharsets.UTF_8));
    }

    /* NOTE: change this part if you want to generate different messages for
       testing. we will certainly use different messages in our grading! */
    private static int c1 = 0;
    private byte[] generateMessage(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++, c1 = (c1 + 1) % 10) {
            bytes[i] = (byte) ('0' + c1);
        }
        return bytes;
    }

    /* NOTE: change this part if you want to generate different messages for
       testing. we will certainly use different messages in our grading! */
    private static int c2 = 0;
    private boolean validateMessage(byte[] message) {
        boolean ret = true;
        for (int i = 0; i < message.length; i++, c2 = (c2 + 1) % 10) {
            if (message[i] != (byte) '0' + c2) {
                session.counter.failure++;
                ret = false;
            }
        }
        return ret;
    }
}
