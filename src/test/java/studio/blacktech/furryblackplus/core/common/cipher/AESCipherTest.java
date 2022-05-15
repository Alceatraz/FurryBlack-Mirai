/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */

package studio.blacktech.furryblackplus.core.common.cipher;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class AESCipherTest {


    @Test
    public void DiffHellmanTest() {

        AESCipher.DHExchanger aDH = new AESCipher.DHExchanger();
        AESCipher.DHExchanger bDH = new AESCipher.DHExchanger();

        // =====================================================================
        // A side
        String aPublicKey = aDH.init();

        // =====================================================================
        // B side
        String bPublicKey = bDH.init(aPublicKey);
        SecretKey bSecretKey = bDH.generate();

        // =====================================================================
        // A side
        SecretKey aSecretKey = aDH.generate(bPublicKey);

        assertArrayEquals(aSecretKey.getEncoded(), bSecretKey.getEncoded());


        // =====================================================================


        AESCipher aCipher = new AESCipher(aSecretKey);

        AESCipher bCipher = new AESCipher(bSecretKey);

        String encrypt = aCipher.encrypt("1234567890");

        String decrypt = bCipher.decrypt(encrypt);

        System.out.println(encrypt);
        System.out.println(decrypt);

    }

}