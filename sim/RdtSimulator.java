package sim;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author Jiupeng Zhang
 * @since 10/04/2019
 */
public class RdtSimulator {
    private RdtSender sender;
    private RdtReceiver receiver;

    double simulationTime = 1000;           /* total simulation time */
    double messageInterval = 0.2;           /* intervals between upper messages arrival */
    int averageMessageSize = 100;           /* average size of messages (in bytes) */
    double averagePacketLatency = 0.2;      /* average one-way latency (in seconds) */
    double outOfOrderRate = 0.2;            /* probability of abnormal latency */
    double lossRate = 0.2;                  /* probability of packet loss */
    double corruptRate = 0.2;               /* probability of packet corruption */

    public RdtSimulator(RdtSender sender, RdtReceiver receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public void start() {
        RdtSession session = new RdtSession(0, simulationTime, messageInterval,
                averageMessageSize, averagePacketLatency, outOfOrderRate, lossRate, corruptRate);

        System.out.printf("## Reliable data transfer simulation with:\n" +
                        "\tsimulation time is %.3f seconds\n" +
                        "\taverage message arrival interval is %.3f seconds\n" +
                        "\taverage message size is %d bytes\n" +
                        "\taverage out-of-order delivery rate is %.2f%%\n" +
                        "\taverage loss rate is %.2f%%\n" +
                        "\taverage corrupt rate is %.2f%%\n" +
                        "Please review these inputs and press <enter> to proceed.\n",
                session.simulationTime, session.messageInterval, session.averageMessageSize,
                session.outOfOrderRate * 100, session.lossRate * 100, session.corruptRate * 100);
        new Scanner(System.in).nextLine();

        sender.join(session);
        receiver.join(session);
        session.schedule(new RdtEvent.SenderFromUpperLayer(0));
        while (session.hasNext()) {
            Event e = session.next();

            if (e.getClass() == RdtEvent.SenderFromUpperLayer.class) {
                RdtSession.LOG.info(String.format("Time %.2fs (Sender): the upper layer " +
                        "instructs rdt layer to send out a message.", session.getTime()));
                int size = (int) (Math.random() * 2 * session.averageMessageSize);
                byte[] message = generateMessage(size);
                session.counter.sent += message.length;
                sender.receiveFromUpperLayer(message);
                if (session.getTime() < session.simulationTime) {
                    e.reschedule(session.getTime() + Math.random() * 2 * session.messageInterval);
                    session.schedule(e);
                }

            } else if (e.getClass() == RdtEvent.SenderFromLowerLayer.class) {
                RdtSession.LOG.info(String.format("Time %.2fs (Sender): the lower layer informs " +
                        "the rdt layer that a packet is received from the link.", session.getTime()));
                sender.receiveFromLowerLayer(((RdtEvent.SenderFromLowerLayer) e).getPacket());

            } else if (e.getClass() == RdtEvent.SenderTimeout.class) {
                RdtSession.LOG.info(String.format("Time %.2fs (Sender): the timer expires.", session.getTime()));
                sender.onTimeout();

            } else if (e.getClass() == RdtEvent.ReceiverFromLowerLayer.class) {
                RdtSession.LOG.info(String.format("Time %.2fs (Receiver): the lower layer informs " +
                        "the rdt layer that a packet is received from the link.", session.getTime()));
                receiver.receiveFromLowerLayer(((RdtEvent.ReceiverFromLowerLayer) e).getPacket());

            } else if (e.getClass() == RdtEvent.ReceiverToUpperLayer.class) {
                byte[] message = ((RdtEvent.ReceiverToUpperLayer) e).getMessage();
                session.counter.delivered += message.length;
                if (!validateMessage(message)) {
                    session.counter.failure++;
                    RdtSession.LOG.severe(String.format("Time %.2fs (Receiver): delivered corrupted " +
                            "message \"%s\"", session.getTime(), new String(message, StandardCharsets.UTF_8)));
                }
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
        this.simulationTime = simulationTime;
    }

    public void setMessageInterval(double messageInterval) {
        this.messageInterval = messageInterval;
    }

    public void setAverageMessageSize(int averageMessageSize) {
        this.averageMessageSize = averageMessageSize;
    }

    public void setOutOfOrderRate(double outOfOrderRate) {
        this.outOfOrderRate = outOfOrderRate;
    }

    public void setLossRate(double lossRate) {
        this.lossRate = lossRate;
    }

    public void setCorruptRate(double corruptRate) {
        this.corruptRate = corruptRate;
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
        boolean res = true;
        for (int i = 0; i < message.length; i++, c2 = (c2 + 1) % 10) {
            if (message[i] != (byte) '0' + c2) res = false;
        }
        return res;
    }
}
