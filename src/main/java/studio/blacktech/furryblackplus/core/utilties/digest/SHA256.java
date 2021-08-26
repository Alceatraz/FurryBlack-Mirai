package studio.blacktech.furryblackplus.core.utilties.digest;


import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


@SuppressWarnings("unused")


public class SHA256 {


    private SHA256() {}

    public static SHA256Digester getInstance() {
        return new SHA256Digester();
    }

    public static MACSHA256Digester getInstance(String key) {
        return getInstance(key.getBytes(StandardCharsets.UTF_8));
    }

    public static MACSHA256Digester getInstance(byte[] key) {
        return getInstance(new SecretKeySpec(key, "HmacSHA256"));
    }

    public static MACSHA256Digester getInstance(SecretKey key) {
        return new MACSHA256Digester(key);
    }

    //= ========================================================================


    public static class SHA256Digester {

        private final MessageDigest digest;

        public SHA256Digester() {
            try {
                this.digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support SHA-256", exception);
            }
        }

        public String digest(String message) {
            byte[] digest = this.digest(message.getBytes(StandardCharsets.UTF_8));
            return new String(digest, StandardCharsets.UTF_8);
        }

        public byte[] digest(byte[] message) {
            byte[] bytes = this.digest.digest(message);
            return Base64.getEncoder().encode(bytes);
        }

    }

    public static class MACSHA256Digester {

        private final Mac mac;

        public MACSHA256Digester(SecretKey key) {
            try {
                this.mac = Mac.getInstance("HmacSHA256");
                this.mac.init(key);
            } catch (NoSuchAlgorithmException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support HmacSHA256", exception);
            } catch (InvalidKeyException exception) {
                throw new IllegalArgumentException("ERROR: Invalidate key", exception);
            }
        }

        public String digest(String message) {
            byte[] digest = this.digest(message.getBytes(StandardCharsets.UTF_8));
            return new String(digest, StandardCharsets.UTF_8);
        }

        public byte[] digest(byte[] message) {
            byte[] bytes = this.mac.doFinal(message);
            return Base64.getEncoder().encode(bytes);
        }
    }
}
