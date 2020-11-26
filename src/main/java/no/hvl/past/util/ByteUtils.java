package no.hvl.past.util;

/**
 * Provides some convenience functions for working with bytearrays as if they where proper lists.
 */
public class ByteUtils {

    private ByteUtils() {
    }

    /**
     * Returns a byte array, which is a concatenation of the given arguments.
     */
    public static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Returns a new byte array where the first argument (a byte) is prepended
     * to the second argument (a byte array) as a prefix.
     */
    public static byte[] prefix(byte top, byte[] tail) {
        byte[] result = new byte[tail.length + 1];
        result[0] = top;
        System.arraycopy(tail, 0, result, 1, tail.length);
        return result;
    }

    /**
     * Returns a new byte array where the fist argument (a byte) is appended
     * to the second argument (a byte array) as a suffix.
     */
    public static byte[] suffix(byte last, byte[] init) {
        byte[] result = new byte[init.length + 1];
        System.arraycopy(init, 0, result, 0, init.length);
        result[init.length] = last;
        return result;
    }

    public static byte[] shortToByteArray(short number, boolean lsbFirst) {
        return numberToByteArray(number, 2, lsbFirst);
    }

    public static byte[] intToByteArray(int number, boolean lsbFirst) {
        return numberToByteArray(number, 4, lsbFirst);
    }

    public static byte[] longToByteArray(long number, boolean lsbFirst) {
        return numberToByteArray(number, 8, lsbFirst);
    }

    private static byte[] numberToByteArray(long number, int usedbytes, boolean lsbFirst) {
        byte[] result = new byte[usedbytes];
        int index = lsbFirst ? 0 : usedbytes - 1;
        int i = 0;
        while ((lsbFirst && index < usedbytes) || (!lsbFirst && index >= 0)) {
            result[index] = (byte) (number >> (i * 8));
            i++;
            index = lsbFirst ? index + 1 : index - 1;
        }
        return result;
    }

    public static byte[] fromHexString(String hexString) {
        char[] chars = hexString.toCharArray();
        byte[] result = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            result[i] = fromHex(chars[i]);
        }
        return result;
    }

    public static byte fromHex(char hex) {
        switch (hex) {
            case '1':
                return (byte) 0x01;
            case '2':
                return (byte) 0x02;
            case '3':
                return (byte) 0x03;
            case '4':
                return (byte) 0x04;
            case '5':
                return (byte) 0x05;
            case '6':
                return (byte) 0x06;
            case '7':
                return (byte) 0x07;
            case '8':
                return (byte) 0x08;
            case '9':
                return (byte) 0x09;
            case 'A':
            case 'a':
                return (byte) 0x0A;
            case 'B':
            case 'b':
                return (byte) 0x0B;
            case 'C':
            case 'c':
                return (byte) 0x0C;
            case 'D':
            case 'd':
                return (byte) 0x0D;
            case 'E':
            case 'e':
                return (byte) 0x0E;
            case 'F':
            case 'f':
                return (byte) 0x0F;
            case '0':
            default:
                return (byte) 0x00;
        }
    }


    /**
     * Returns a new byte array, which is a substring of the given input byte array between the given indices.
     */
    public static byte[] substring(byte[] src, int from, int length) {
        byte[] result = new byte[length];
        System.arraycopy(src, from, result, 0, length);
        return result;
    }





}
