package studio.blacktech.furryblackplus.system.common.utilties;


import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具
 *
 * @author Alceatraz Warprays alceatraz@blacktech.studio
 */

public class RandomTool {

    private static final String LOWER_CHAR = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String EN_US_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String EN_US_NUMB = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final String BASE58 = "abcdefghiklmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ123456789";
    private static final String BASE64 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+/";

    private static final String DEC = "0123456789";
    private static final String HEX = "0123456789ABCDEF";


    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }


    public static int nextInt(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }


    public static int nextInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }


    public static int hashInt() {
        int random = ThreadLocalRandom.current().nextInt();
        int phase1 = ((random >>> 20) ^ (random >>> 12)) ^ random;
        int phase2 = (phase1 >>> 7) ^ (phase1 >>> 4);
        return phase1 ^ phase2;
    }


    /**
     * 将 hashInt转为hexString
     */
    public static byte[] hashHex() {
        return Integer.toHexString(hashInt()).getBytes();
    }


    /**
     * 随机字符串：从十进制阿拉伯数字
     */
    public static String randomDECString(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++)
            chat[i] = DEC.charAt(ThreadLocalRandom.current().nextInt(10));
        return new String(chat);
    }


    /**
     * 随机字符串：从十六进制阿拉伯数字与大写字母
     */
    public static String randomHEXString(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++)
            chat[i] = HEX.charAt(ThreadLocalRandom.current().nextInt(16));
        return new String(chat);
    }


    /**
     * 随机字符串：小写英文字符
     */
    public static String randomStringLOWER(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++)
            chat[i] = LOWER_CHAR.charAt(ThreadLocalRandom.current().nextInt(26));
        return new String(chat);
    }


    /**
     * 随机字符串：大写英文字符
     */
    public static String randomStringUPPER(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++)
            chat[i] = UPPER_CHAR.charAt(ThreadLocalRandom.current().nextInt(26));
        return new String(chat);
    }


    /**
     * 随机字符串：小写和大写英文字符
     */
    public static String randomString(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++)
            chat[i] = EN_US_CHAR.charAt(ThreadLocalRandom.current().nextInt(52));
        return new String(chat);
    }


    /**
     * 随机字符串：小写和大写英文字符与十进制阿拉伯数字
     */
    public static String randomStringDEC(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++)
            chat[i] = EN_US_NUMB.charAt(ThreadLocalRandom.current().nextInt(62));
        return new String(chat);
    }


    /**
     * 随机字符串：BASE58的字符范围
     */
    public static String randomBASE58(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++)
            chat[i] = BASE58.charAt(ThreadLocalRandom.current().nextInt(58));
        return new String(chat);
    }


    /**
     * 随机字符串：BASE64的字符范围
     */
    public static String randomBASE64(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++)
            chat[i] = BASE64.charAt(ThreadLocalRandom.current().nextInt(64));
        return new String(chat);
    }


}
