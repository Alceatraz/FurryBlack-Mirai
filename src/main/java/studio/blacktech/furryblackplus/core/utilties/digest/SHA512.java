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


public class SHA512 {


    private SHA512() {}

    public static SHA512Digester getInstance() {
        return new SHA512Digester();
    }

    public static MACSHA512Digester getInstance(String key) {
        return getInstance(key.getBytes(StandardCharsets.UTF_8));
    }

    public static MACSHA512Digester getInstance(byte[] key) {
        return getInstance(new SecretKeySpec(key, "HmacSHA512"));
    }

    public static MACSHA512Digester getInstance(SecretKey key) {
        return new MACSHA512Digester(key);
    }

    //= ========================================================================


    public static class SHA512Digester {

        private final MessageDigest digest;

        public SHA512Digester() {
            try {
                this.digest = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support SHA-512", exception);
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

    public static class MACSHA512Digester {

        private final Mac mac;

        public MACSHA512Digester(SecretKey key) {
            try {
                this.mac = Mac.getInstance("HmacSHA512");
                this.mac.init(key);
            } catch (NoSuchAlgorithmException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support HmacSHA512", exception);
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
