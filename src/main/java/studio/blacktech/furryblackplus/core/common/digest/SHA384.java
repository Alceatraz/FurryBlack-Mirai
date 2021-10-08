/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */


package studio.blacktech.furryblackplus.core.common.digest;


import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


@SuppressWarnings("unused")


public class SHA384 {


    private SHA384() {}

    public static SHA384Digester getInstance() {
        return new SHA384Digester();
    }

    public static MACSHA384Digester getInstance(String key) {
        return getInstance(key.getBytes(StandardCharsets.UTF_8));
    }

    public static MACSHA384Digester getInstance(byte[] key) {
        return getInstance(new SecretKeySpec(key, "HmacSHA384"));
    }

    public static MACSHA384Digester getInstance(SecretKey key) {
        return new MACSHA384Digester(key);
    }

    //= ========================================================================


    public static class SHA384Digester {

        private final MessageDigest digest;

        public SHA384Digester() {
            try {
                this.digest = MessageDigest.getInstance("SHA-384");
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

    public static class MACSHA384Digester {

        private final Mac mac;

        public MACSHA384Digester(SecretKey key) {
            try {
                this.mac = Mac.getInstance("HmacSHA384");
                this.mac.init(key);
            } catch (NoSuchAlgorithmException exception) {
                throw new UnsupportedOperationException("ERROR: This runtime not support HmacSHA384", exception);
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