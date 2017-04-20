package org.jacoco.examples.scala.kafka;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Random;
import java.util.UUID;

/**
 * Will generate time-based UUID (version 1 UUID).
 * Requires JDK 1.6+
 *
 * @author Oleg Zhurakousky
 */
public class TimeBasedUUIDGenerator {

    public static final Object lock = new Object();

    private static long lastTime;
    private static long clockSequence = 0;
    private static final long hostIdentifier = getHostId();

    /**
     * Will generate unique time based UUID where the next UUID is
     * always greater then the previous.
     */
    public final static UUID generateId() {
        return generateIdFromTimestamp(System.currentTimeMillis());
    }

    public final static UUID generateIdFromTimestamp(long currentTimeMillis){
        long time;

        synchronized (lock) {
            if (currentTimeMillis > lastTime) {
                lastTime = currentTimeMillis;
                clockSequence = 0;
            } else  {
                ++clockSequence;
            }
        }

        time = currentTimeMillis;

        // low Time
        time = currentTimeMillis << 32;

        // mid Time
        time |= ((currentTimeMillis & 0xFFFF00000000L) >> 16);

        // hi Time
        time |= 0x1000 | ((currentTimeMillis >> 48) & 0x0FFF);

        long clockSequenceHi = clockSequence;

        clockSequenceHi <<=48;

        long lsb = clockSequenceHi | hostIdentifier;

        return new UUID(time, lsb);
    }
    private static final long getHostId(){
        long  macAddressAsLong = 0;
        try {
            Random random = new Random();
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if (ni != null) {
                byte[] mac = ni.getHardwareAddress();
                random.nextBytes(mac); // we don't really want to reveal the actual MAC address
                //Converts array of unsigned bytes to an long
                if (mac != null) {
                    for (int i = 0; i < mac.length; i++) {
                        macAddressAsLong <<= 8;
                        macAddressAsLong ^= (long)mac[i] & 0xFF;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macAddressAsLong;
    }
/*
0000000000000000000000010101101110001010010111101010000111100100    currentTimeMillis
1000101001011110101000011110010000000000000000000000000000000000    currentTimeMillis << 32 (为currentTimeMillis最后32位)

0000000000000000111111111111111100000000000000000000000000000000    0xFFFF00000000L

0000000000000000000000010101101100000000000000000000000000000000    currentTimeMillis & 0xFFFF00000000L


0000000000000000000000000000000000000001010110110000000000000000    (currentTimeMillis & 0xFFFF00000000L) >> 16


1000101001011110101000011110010000000001010110110000000000000000    time |= ((currentTimeMillis & 0xFFFF00000000L) >> 16);//time = currentTimeMillis << 32


0000000000000000000000000000000000000000000000000000000000000000    currentTimeMillis >> 48

0000000000000000000000000000000000000000000000000000000000000000    (currentTimeMillis >> 48) & 0x0FFF


0000000000000000000000000000000000000000000000000001000000000000    0x1000 | ((currentTimeMillis >> 48) & 0x0FFF);

1000101001011110101000011110010000000001010110110001000000000000    time |= 0x1000 | ((currentTimeMillis >> 48) & 0x0FFF);
* */
    public static void main(String[] args) {
        System.out.println(1L << 32);
        System.out.println(1492675109348L << 32);
        System.out.println(System.currentTimeMillis());
        System.out.println(TimeBasedUUIDGenerator.hostIdentifier);
        UUID u = TimeBasedUUIDGenerator.generateId();// UUID.fromString("0a1cf270-259a-11e7-9598-0800200c9a66");// UUID.randomUUID();

        System.out.println(u.toString());
        System.out.println(u.clockSequence());
        System.out.println(u.timestamp());
        System.out.println(u.variant());
        System.out.println(u.version());
        System.out.println(u.node());
        System.out.println(u.getLeastSignificantBits());
        System.out.println(u.getMostSignificantBits());
    }
}