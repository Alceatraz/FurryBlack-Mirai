package studio.blacktech.furryblackplus.core.utilties.cipher;


import studio.blacktech.furryblackplus.common.Api;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Objects;


@SuppressWarnings("unused")


@Api(
    value = "RSA工具",
    usage = {
        "加密和解密 encrypt(),decrypt()",
        "签名和校验 signature(),verify()"
    },
    attention = {
        "可构建只有公钥或者私钥的实例，如果进行非可行操作会发生异常（比如只有私钥却要验证签名）。"
    }
)
public class RSACipher {


    // =================================================================================================================


    private int decryptMaxLength;
    private int encryptMaxLength;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private Signature createSignature;
    private Signature verifySignature;
    private static final Encoder encoder = Base64.getEncoder();
    private static final Decoder decoder = Base64.getDecoder();


    // =================================================================================================================


    /**
     * 构造方法 生成4096位长度的密钥创建实例
     */
    public RSACipher() {
        this(4096);
    }


    /**
     * 构造方法 生成指定长度的密钥创建实例
     *
     * @param keyLength 密钥长度，至少为512
     */
    public RSACipher(int keyLength) {
        this(Objects.requireNonNull(RSACipher.generateKeyPair(keyLength)));
    }


    /**
     * 构造方法 以给定的密钥对创建实例
     *
     * @param keyPair 密钥对
     */
    public RSACipher(KeyPair keyPair) {
        this((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
    }


    /**
     * 构造方法 只传入公钥创建单边实例，仅可用于加密和签名验证
     *
     * @param publicKey RSA公钥
     */
    public RSACipher(RSAPublicKey publicKey) {
        this.initPublicSide(publicKey);
    }


    /**
     * 构造方法 只传入私钥创建单边实例，仅可用于解密和签名生成
     *
     * @param privateKey RSA私钥
     */
    public RSACipher(RSAPrivateKey privateKey) {
        this.initPrivateSide(privateKey);
    }


    /**
     * 构造方法 传入公钥和私钥创建实例
     *
     * @param publicKey  RSA公钥
     * @param privateKey RSA私钥
     */
    public RSACipher(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.initPublicSide(publicKey);
        this.initPrivateSide(privateKey);
    }


    // =================================================================================================================


    /**
     * 将编码后公钥转换为公钥实例
     *
     * @param publicKey 使用X509格式并BASE64编码的公钥
     *
     * @return 公钥
     */
    public static RSAPublicKey getRSAPublicKey(String publicKey) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            byte[] publicKeyString = decoder.decode(publicKey);
            return (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(publicKeyString));
        } catch (InvalidKeySpecException exception) {
            throw new IllegalArgumentException("ERROR: Invalidate RSA public key");
        } catch (NoSuchAlgorithmException exception) {
            throw new UnsupportedOperationException("ERROR: This runtime not support RSA", exception);
        }
    }


    /**
     * 将编码后的私钥转换为私钥实例
     *
     * @param privateKey 使用PKCS8格式并BASE64编码的私钥
     *
     * @return 私钥
     */
    public static RSAPrivateKey getRSAPrivateKey(String privateKey) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            byte[] privateKeyString = decoder.decode(privateKey);
            return (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyString));
        } catch (InvalidKeySpecException exception) {
            throw new IllegalArgumentException("ERROR: Invalidate RSA private key");
        } catch (NoSuchAlgorithmException exception) {
            throw new UnsupportedOperationException("ERROR: This runtime not support RSA", exception);
        }
    }


    // =================================================================================================================


    /**
     * 使用私钥对内容签名
     *
     * @param content 内容
     *
     * @return 签名
     */
    public String signature(String content) {
        if (this.privateKey == null) throw new IllegalArgumentException("ERROR: This RSACipher instance is verify mode only");
        try {
            byte[] temp1 = content.getBytes(StandardCharsets.UTF_8);
            this.createSignature.update(temp1);
            byte[] temp2 = this.createSignature.sign();
            byte[] temp3 = encoder.encode(temp2);
            return new String(temp3, StandardCharsets.UTF_8);
        } catch (SignatureException exception) {
            throw new IllegalArgumentException("ERROR: Signature failed", exception);
        }
    }


    /**
     * 使用公钥对签名校验
     *
     * @param content   内容
     * @param signature 签名
     *
     * @return 真假
     */
    public boolean verify(String content, String signature) {
        if (this.publicKey == null) throw new IllegalArgumentException("ERROR: This RSACipher instance is signature mode only");
        try {
            byte[] temp1 = content.getBytes(StandardCharsets.UTF_8);
            byte[] temp2 = signature.getBytes(StandardCharsets.UTF_8);
            byte[] temp3 = decoder.decode(temp2);
            this.verifySignature.update(temp1);
            return this.verifySignature.verify(temp3);
        } catch (SignatureException exception) {
            return false;
        }
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
        if (this.publicKey == null) throw new IllegalArgumentException("ERROR: Instance is decrypt mode only");
        try {
            byte[] temp1 = content.getBytes(StandardCharsets.UTF_8);
            byte[] temp2 = this.doRSAEncrypt(temp1);
            byte[] temp3 = encoder.encode(temp2);
            return new String(temp3, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new RuntimeException("ERROR: Encrypt failed", exception);
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
        if (this.privateKey == null) throw new IllegalArgumentException("ERROR: Instance is encrypt mode only");
        try {
            byte[] temp1 = content.getBytes(StandardCharsets.UTF_8);
            byte[] temp2 = decoder.decode(temp1);
            byte[] temp3 = this.doRSADecrypt(temp2);
            return new String(temp3, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new RuntimeException("ERROR: Decrypt failed", exception);
        }
    }


    // =================================================================================================================


    /**
     * 获取X509格式和BASE64编码后的公钥
     * 可配合getRSAPublicKey转换回实例
     *
     * @return 公钥
     */
    public String getEncodedPublicKey() {
        if (this.publicKey == null) throw new IllegalArgumentException("Instance has no public key");
        return new String(encoder.encode(this.publicKey.getEncoded()), StandardCharsets.UTF_8);
    }


    /**
     * 获取PKCS8格式和BASE64编码后的私钥
     * 可配合getRSAPrivateKey转换回实例
     *
     * @return 私钥
     */
    public String getEncodedPrivateKey() {
        if (this.privateKey == null) throw new IllegalArgumentException("Instance has no private key");
        return new String(encoder.encode(this.privateKey.getEncoded()), StandardCharsets.UTF_8);
    }


    // =================================================================================================================
    //
    //
    //  作为一般用户 以下内容无需关心
    //
    //
    // =================================================================================================================


    private static KeyPair generateKeyPair(int keyLength) {
        if (keyLength < 512) {
            keyLength = 512;
            System.err.println("WARNING: RSA ket length must larger then 512, I set it to 512 for you.");
        } else if (keyLength % 512 != 0) {
            int temp = keyLength / 512;
            keyLength = 512 * (temp + 1);
            System.err.println("WARNING: RSA key length must multiple of 512, I set it to " + keyLength + " for you.");
        }
        if (keyLength < 4096) System.err.println("WARNING: RSA key length less then 4096 will face security risks!");
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keyLength);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("ERROR: This runtime not support RSA", exception);
        }
    }


    private void initPublicSide(RSAPublicKey publicKey) {
        try {
            this.publicKey = publicKey;
            this.encryptCipher = Cipher.getInstance("RSA");
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            this.encryptMaxLength = publicKey.getModulus().bitLength() / 8 - 11;
            this.verifySignature = Signature.getInstance("SHA256withRSA");
            this.verifySignature.initVerify(publicKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new IllegalArgumentException("ERROR: This runtime not support RSA", exception);
        }
    }


    private void initPrivateSide(RSAPrivateKey privateKey) {
        try {
            this.privateKey = privateKey;
            this.decryptCipher = Cipher.getInstance("RSA");
            this.decryptCipher.init(Cipher.DECRYPT_MODE, this.privateKey);
            this.decryptMaxLength = privateKey.getModulus().bitLength() / 8;
            this.createSignature = Signature.getInstance("SHA256withRSA");
            this.createSignature.initSign(privateKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new IllegalArgumentException("ERROR: This runtime not support RSA", exception);
        }
    }


    private byte[] doRSAEncrypt(byte[] content) throws BadPaddingException, IllegalBlockSizeException {
        int length = content.length;
        if (length > this.encryptMaxLength) {
            int size = this.publicKey.getModulus().bitLength() / 8;
            return this.doGroupingFinal(content, length, size, this.encryptMaxLength, this.encryptCipher);
        } else {
            return this.encryptCipher.doFinal(content);
        }
    }


    private byte[] doRSADecrypt(byte[] content) throws BadPaddingException, IllegalBlockSizeException {
        int length = content.length;
        if (length > this.decryptMaxLength) {
            int size = this.privateKey.getModulus().bitLength() / 8 - 11;
            return this.doGroupingFinal(content, length, size, this.decryptMaxLength, this.decryptCipher);
        } else {
            return this.decryptCipher.doFinal(content);
        }
    }


    private byte[] doGroupingFinal(byte[] content, int length, int size, int maxLength, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int rounds = 0;
        int offset = 0;
        byte[] buffer;
        while (length > offset) {
            if (length - offset > maxLength) {
                buffer = cipher.doFinal(content, offset, maxLength);
                stream.write(buffer, 0, size);
            } else {
                buffer = cipher.doFinal(content, offset, length - offset);
                stream.write(buffer, 0, buffer.length);
            }
            rounds = rounds + 1;
            offset = rounds * maxLength;
        }
        return stream.toByteArray();
    }


    // =================================================================================================================


}
