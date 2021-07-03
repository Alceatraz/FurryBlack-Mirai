package studio.blacktech.furryblackplus.core.utilties;


import studio.blacktech.furryblackplus.core.annotation.Api;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


/**
 * HASH工具类
 * 密码学中md5和MD5是不一样的 注意严谨性 即使这么命名会破坏JAVA命名规范
 */
@Api("哈希工具类")
public final class HashTool {


    private HashTool() { }


    @Api("md5")
    public static String md5(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
        byte[] digested = Objects.requireNonNull(digest).digest(message);
        StringBuilder builder = new StringBuilder();
        for (byte element : digested) {
            builder.append(Integer.toHexString((0x000000FF & element) | 0xFFFFFF00).substring(6));
        }
        return builder.toString();
    }

    @Api("MD5")
    public static String MD5(byte[] message) {
        return md5(message).toUpperCase();
    }

    @Api("md5")
    public static String md5(String message) {
        return md5(message.getBytes(StandardCharsets.UTF_8));
    }

    @Api("MD5")
    public static String MD5(String message) {
        return MD5(message.getBytes(StandardCharsets.UTF_8));
    }

    @Api("sha256")
    public static String sha256(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
        return digest(message, digest);
    }


    @Api("SHA256")
    public static String SHA256(byte[] message) {
        return sha256(message).toUpperCase();
    }

    @Api("sha256")
    public static String sha256(String message) {
        return sha256(message.getBytes(StandardCharsets.UTF_8));
    }

    @Api("SHA256")
    public static String SHA256(String message) {
        return SHA256(message.getBytes(StandardCharsets.UTF_8));
    }

    @Api("sha384")
    public static String sha384(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-384");
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
        return digest(message, digest);
    }

    @Api("SHA384")
    public static String SHA384(byte[] message) {
        return sha384(message).toUpperCase();
    }

    @Api("sha384")
    public static String sha384(String message) {
        return sha384(message.getBytes(StandardCharsets.UTF_8));
    }

    @Api("SHA384")
    public static String SHA384(String message) {
        return SHA384(message.getBytes(StandardCharsets.UTF_8));
    }

    @Api("sha512")
    public static String sha512(byte[] message) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
        return digest(message, digest);
    }

    @Api("SHA512")
    public static String SHA512(byte[] message) {
        return sha512(message).toUpperCase();
    }

    @Api("sha512")
    public static String sha512(String message) {
        return sha512(message.getBytes(StandardCharsets.UTF_8));
    }

    @Api("SHA512")
    public static String SHA512(String message) {
        return SHA512(message.getBytes(StandardCharsets.UTF_8));
    }


    private static String digest(byte[] message, MessageDigest digest) {
        byte[] digested = Objects.requireNonNull(digest).digest(message);
        String temp;
        StringBuilder builder = new StringBuilder();
        for (byte element : digested) {
            temp = Integer.toHexString(element & 0xFF);
            builder.append(temp.length() == 2 ? temp : ("0" + temp));
        }
        return builder.toString();
    }

}
