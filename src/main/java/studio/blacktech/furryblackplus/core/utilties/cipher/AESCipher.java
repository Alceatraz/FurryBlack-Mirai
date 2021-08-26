package studio.blacktech.furryblackplus.core.utilties.cipher;


import studio.blacktech.furryblackplus.common.Api;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.OperationNotSupportedException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;


@SuppressWarnings("unused")


@Api(
    value = "RSA签名工具",
    usage = {
        "使用Diffie-Hellman协商密钥：initDiffieHellman(),generateDiffieHellmanKey()",
        "AES的CBC和EBC模式加密和解密：encrypt(),decrypt()"
    },
    attention = {
        "EBC模式不安全",
        "AES-128已经非常安全",
        "JAVA标准库不支持AES-XTS模式"
    }
)
public class AESCipher {


    // =================================================================================================================


    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    private static final Encoder encoder = Base64.getEncoder();
    private static final Decoder decoder = Base64.getDecoder();


    // =================================================================================================================


    public AESCipher() throws OperationNotSupportedException {

        // @formatter:off

        throw new OperationNotSupportedException(
                "为了安全性，密码和IV不会保存在实例中 初始化后 无法获取\n" +
                "所以不提供无参数构造方法 必须传入密码/向量\n" +
                "使用 getSecretKey 转换密钥\n" +
                "使用 getIVector 将向量转换为IV"
        );

        // @formatter:on

    }


    // =================================================================================================================
    // EBC


    /**
     * 使用密码初始化 * EBC模式
     *
     * @param key 密码
     */
    public AESCipher(String key) {
        this(getSecretKey(key));
    }


    /**
     * 使用密钥初始化 * EBC模式
     *
     * @param secretKey 密钥
     */
    public AESCipher(SecretKey secretKey) {
        try {
            this.encryptCipher = Cipher.getInstance("AES");
            this.decryptCipher = Cipher.getInstance("AES");
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            this.decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException exception) {
            throw new IllegalArgumentException("ERROR: Secret is invalidate", exception);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new UnsupportedOperationException("ERROR: This runtime not support AES", exception);
        }
    }


    // =================================================================================================================
    // CBC


    /**
     * 使用密码和向量初始化 * CBC模式
     *
     * @param key    密码
     * @param vector 向量
     */
    public AESCipher(String key, String vector) {
        this(getSecretKey(key), getInitVector(vector));
    }


    /**
     * 使用密钥和IV初始化 * CBC模式
     *
     * @param secretKey       密钥
     * @param ivParameterSpec IV
     */
    public AESCipher(SecretKey secretKey, IvParameterSpec ivParameterSpec) {
        try {
            this.encryptCipher = Cipher.getInstance("AES/CBC/NoPadding");
            this.decryptCipher = Cipher.getInstance("AES/CBC/NoPadding");
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            this.decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        } catch (InvalidKeyException exception) {
            throw new IllegalArgumentException("ERROR: Secret is invalidate", exception);
        } catch (InvalidAlgorithmParameterException exception) {
            throw new IllegalArgumentException("ERROR: Vector is invalidate", exception);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new UnsupportedOperationException("ERROR: This runtime not support AES", exception);
        }
    }

    // =================================================================================================================


    /**
     * 生成密钥实例 必须是16字符长度
     *
     * @param key 密码
     *
     * @return 密钥
     */
    public static SecretKey getSecretKey(String key) {
        if (key.length() != 16) throw new IllegalArgumentException("ERROR: AES-128 key length must be 16");
        return getSecretKey(key.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 生成密钥实例 必须是16字符长度
     *
     * @param key 密码
     *
     * @return 密钥
     */
    public static SecretKey getSecretKey(byte[] key) {
        if (key.length != 16) throw new IllegalArgumentException("ERROR: AES-128 key length must be 16");
        return new SecretKeySpec(key, "AES");
    }


    /**
     * 由初始向量生成IV实例 必须是16字符长度
     *
     * @param vector 初始向量
     *
     * @return IV
     */
    public static IvParameterSpec getInitVector(String vector) {
        if (vector.length() != 16) throw new IllegalArgumentException("ERROR: AES-128 IV length must be 16");
        return getInitVector(vector.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 由初始向量生成IV实例 必须是16字符长度
     *
     * @param vector 初始向量
     *
     * @return IV
     */
    public static IvParameterSpec getInitVector(byte[] vector) {
        if (vector.length != 16) throw new IllegalArgumentException("ERROR: AES-128 IV length must be 16");
        return new IvParameterSpec(vector);
    }


    // =================================================================================================================


    /**
     * 加密
     *
     * @param content 原文
     *
     * @return 密文
     */
    public String encrypt(String content) {
        try {
            byte[] temp1 = content.getBytes(StandardCharsets.UTF_8);
            byte[] temp2 = this.encryptCipher.doFinal(temp1);
            byte[] temp3 = encoder.encode(temp2);
            return new String(temp3, StandardCharsets.UTF_8);
        } catch (BadPaddingException | IllegalBlockSizeException exception) {
            throw new RuntimeException("ERROR: This AES provider going wrong", exception);
        }
    }


    /**
     * 解密
     *
     * @param content 密文
     *
     * @return 原文
     */
    public String decrypt(String content) {
        try {
            byte[] temp1 = content.getBytes(StandardCharsets.UTF_8);
            byte[] temp2 = decoder.decode(temp1);
            byte[] temp3 = this.decryptCipher.doFinal(temp2);
            return new String(temp3, StandardCharsets.UTF_8);
        } catch (BadPaddingException | IllegalBlockSizeException exception) {
            throw new IllegalArgumentException("ERROR: Content can't decrypt", exception);
        }
    }


    // =================================================================================================================


    public static class DHExchanger {


        private final int keyLength;
        private final KeyFactory keyFactory;
        private final KeyAgreement keyAgreement;
        private KeyPair keyPair;


        /**
         * 使用Diffie-Hellman协议协商密钥使用此构造函数 默认使用4096位长度的DH
         *
         * AESCipher aliceCipher = new AESCipher();
         * String alicePublicKey = aliceCipher.initDiffieHellman(4096);
         * // 传输Alice的公钥给Bob
         * AESCipher bobCipher = new AESCipher();
         * String bobPublicKey = bobCipher.initDiffieHellman(alicePublicKey);
         * SecretKeySpec bobKey = bobCipher.generateDiffieHellmanKey();
         * // 传输Bob的公钥给Alice
         * SecretKeySpec aliceKey = aliceCipher.generateDiffieHellmanKey(bobPublicKey);
         */
        public DHExchanger() {
            this(8192);
        }


        public DHExchanger(int keyLength) {
            if (keyLength < 512) {
                keyLength = 512;
                System.err.println("WARNING: DiffieHellman key length minimal is 512, I set it 512 for you.");
            } else if (keyLength > 8192) {
                keyLength = 8192;
                System.err.println("WARNING: DiffieHellman key length maximal is 8192, I set it 8192 for you.");
            } else if (keyLength % 64 != 0) {
                keyLength = 64 * (keyLength / 64 + 1);
                System.err.println("WARNING: DiffieHellman key length must multiple 64, I set it " + keyLength + " for you.");
            }
            this.keyLength = keyLength;
            try {
                this.keyFactory = KeyFactory.getInstance("DH");
                this.keyAgreement = KeyAgreement.getInstance("DH");
            } catch (NoSuchAlgorithmException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support DH", exception);
            }
        }


        /**
         * Alice一方使用此方法初始化DH
         *
         * @return Alice的公钥
         */
        public String init() {
            SecureRandom random = new SecureRandom();
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
                keyPairGenerator.initialize(this.keyLength, random);
                this.keyPair = keyPairGenerator.generateKeyPair();
                this.keyAgreement.init(this.keyPair.getPrivate());
                byte[] temp1 = this.keyPair.getPublic().getEncoded();
                byte[] temp2 = encoder.encode(temp1);
                return new String(temp2, StandardCharsets.UTF_8);
            } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support DH", exception);
            }
        }


        /**
         * Bob一方使用此方法初始化DH
         *
         * @param publicKey Alice的公钥
         *
         * @return Bob的公钥
         */
        public String init(String publicKey) {
            try {
                byte[] temp1 = decoder.decode(publicKey);
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(temp1);
                DHPublicKey dhPublicKeySpec = (DHPublicKey) this.keyFactory.generatePublic(x509KeySpec);
                DHParameterSpec dhParameterSpec = dhPublicKeySpec.getParams();
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
                keyPairGenerator.initialize(dhParameterSpec);
                this.keyPair = keyPairGenerator.generateKeyPair();
                this.keyAgreement.init(this.keyPair.getPrivate());
                this.keyAgreement.doPhase(dhPublicKeySpec, true);
                byte[] temp2 = encoder.encode(this.keyPair.getPublic().getEncoded());
                return new String(temp2, StandardCharsets.UTF_8);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException exception) {
                throw new IllegalArgumentException("ERROR: Diffie-Hellman Alice public key is invalidate!", exception);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support DH", exception);
            }
        }


        /**
         * Bob一方使用此方法生成密钥
         *
         * @return 密钥
         */
        public SecretKey generate() {
            byte[] secret = this.keyAgreement.generateSecret();
            return new SecretKeySpec(secret, 0, 16, "AES");
        }


        /**
         * Alice一方使用此方法生成密钥
         *
         * @param publicKey BOT公钥
         *
         * @return 密钥
         */
        public SecretKey generate(String publicKey) {
            try {
                byte[] temp1 = decoder.decode(publicKey);
                X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(temp1);
                DHPublicKey dhPublicKey = (DHPublicKey) this.keyFactory.generatePublic(x509EncodedKeySpec);
                this.keyAgreement.doPhase(dhPublicKey, true);
                byte[] secret = this.keyAgreement.generateSecret();
                return new SecretKeySpec(secret, 0, 16, "AES");
            } catch (InvalidKeyException exception) {
                throw new IllegalArgumentException("ERROR: Diffie-Hellman Alice public key is invalidate!", exception);
            } catch (InvalidKeySpecException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support DH", exception);
            }
        }


    }

}
