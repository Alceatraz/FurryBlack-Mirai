package studio.blacktech.furryblackplus.system.common.utilties;


import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;


/**
 * 随机工具类
 *
 * @author BTS - Alceatraz Warprays alceatraz@blacktech.studio
 */
public class RandomTool {


    // ==========================================================================================================================================================


    private static final String LOWER_CHAR = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String EN_US_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String EN_US_NUMB = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final String BASE58 = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ123456789";
    private static final String BASE64 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+/";

    private static final String DEC = "0123456789";
    private static final String HEX = "0123456789ABCDEF";


    // ==========================================================================================================================================================


    private static SecureRandom secureRandom;


    // ==========================================================================================================================================================


    private static final RandomTool instance = new RandomTool();


    private RandomTool() {

        try {
            // Linux使用/dev/random熵池
            secureRandom = SecureRandom.getInstance("NativePRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException ignored1) {
            try {
                // Windows使用CryptGenRandom
                secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            } catch (NoSuchAlgorithmException | NoSuchProviderException ignored2) {
                // 都不行就用默认的
                secureRandom = new SecureRandom();
            }
        }

    }


    public RandomTool getInstance() {
        return instance;
    }


    // ==========================================================================================================================================================


    public static int nextInt() {
        return secureRandom.nextInt();
    }


    public static int nextInt(int max) {
        return secureRandom.nextInt(max);
    }


    public static int nextInt(int min, int max) {
        return secureRandom.nextInt(max - min) + min;
    }


    /**
     * 关于hashInt的名字由来：
     * JDK7中HashMap使用了一个搅拌算法,以防止弱的hash算法产生的严重hash碰撞
     * 在JDK8由于HashMap整个重构导致被替代了
     * 这个操作非常的暴力而且歪门邪道 不要随意模仿
     */
    public static int hashInt() {
        int random = secureRandom.nextInt();
        int phase1 = random >>> 20 ^ random >>> 12 ^ random;
        int phase2 = phase1 >>> 7 ^ phase1 >>> 4;
        return phase1 ^ phase2;
    }


    /**
     * 将 hashInt转为hexString
     */
    public static byte[] hashHex() {
        return Integer.toHexString(hashInt()).getBytes();
    }


    /**
     * 对hashInt转为hexString计算MD5
     */
    public static String hashMD5() {
        return HashTool.MD5(hashHex());
    }


    /**
     * 对hashInt转为hexString计算MD5 - 4轮
     */
    public static String hashMD5R4() {
        byte[] input = hashHex();
        String temp1 = HashTool.MD5(input);
        String temp2 = HashTool.MD5(temp1.getBytes());
        String temp3 = HashTool.MD5(temp2.getBytes());
        return HashTool.MD5(temp3.getBytes());
    }


    /**
     * 对hashInt转为hexString计算MD5 - 4组
     */
    public static String hashMD5S4() {
        String hash1 = HashTool.md5(hashHex());
        String hash2 = HashTool.md5(hashHex());
        String hash3 = HashTool.md5(hashHex());
        String hash4 = HashTool.md5(hashHex());
        return HashTool.MD5((hash1 + hash2 + hash3 + hash4).getBytes());
    }


    // ==========================================================================================================================================================


    /**
     * 随机字符串：从十进制阿拉伯数字
     */
    public static String randomDECString(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++) chat[i] = DEC.charAt(secureRandom.nextInt(9));
        return new String(chat);
    }


    /**
     * 随机字符串：从十六进制阿拉伯数字与大写字母
     */
    public static String randomHEXString(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++) chat[i] = HEX.charAt(secureRandom.nextInt(15));
        return new String(chat);
    }


    /**
     * 随机字符串：小写英文字符
     */
    public static String randomStringLOWER(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++) chat[i] = LOWER_CHAR.charAt(secureRandom.nextInt(25));
        return new String(chat);
    }


    /**
     * 随机字符串：大写英文字符
     */
    public static String randomStringUPPER(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++) chat[i] = UPPER_CHAR.charAt(secureRandom.nextInt(25));
        return new String(chat);
    }


    /**
     * 随机字符串：小写和大写英文字符
     */
    public static String randomString(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++) chat[i] = EN_US_CHAR.charAt(secureRandom.nextInt(51));
        return new String(chat);
    }


    /**
     * 随机字符串：小写和大写英文字符与十进制阿拉伯数字
     */
    public static String randomStringDEC(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++) chat[i] = EN_US_NUMB.charAt(secureRandom.nextInt(61));
        return new String(chat);
    }


    /**
     * 随机字符串：BASE58的字符范围
     */
    public static String randomStringBASE58(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++) chat[i] = BASE58.charAt(secureRandom.nextInt(57));
        return new String(chat);
    }

    /**
     * 随机字符串：BASE64的字符范围
     */
    public static String randomStringBASE64(int length) {
        char[] chat = new char[length];
        for (int i = 0; i < length; i++) chat[i] = BASE64.charAt(secureRandom.nextInt(63));
        return new String(chat);
    }
}
