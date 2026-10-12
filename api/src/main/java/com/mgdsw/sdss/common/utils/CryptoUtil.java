/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgdsw.sdss.common.enums.ErrorCodes;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Component
public class CryptoUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${security.crypto.staticSecretKey}")
    private String staticSecretKey;

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12; // GCM recommended IV length
    private static final int TAG_LENGTH_BIT = 128; // GCM authentication tag length

    // Convert Base64-encoded secret key to SecretKey object
    private SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(staticSecretKey);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    // Encrypt any object
    public <T> String encrypt(T object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(object);

            // Generate an initialization vector (IV)
            byte[] iv = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            // Create a cipher instance and initialize for encryption
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), gcmParameterSpec);

            // Encrypt the data
            byte[] encryptedBytes = cipher.doFinal(jsonString.getBytes(StandardCharsets.UTF_8));

            // Encode the IV and encrypted data to Base64
            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

            // Return the IV and encrypted data concatenated
            return ivBase64 + ":" + encryptedDataBase64;
        } catch (JsonProcessingException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException |
                 IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new CustomDataException(e.getMessage(), ErrorCodes.INVALID_ARGUMENTS.getErrorCode());
        }
    }

    // Decrypt the encrypted string back to the original object
    public <T> T decrypt(String encryptedData, Class<T> valueType) {
        try {
            // Split the IV and the encrypted data
            String[] parts = encryptedData.split(":");
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            // Create a cipher instance and initialize for decryption
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), gcmParameterSpec);

            // Decrypt the data
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String jsonString = new String(decryptedBytes, StandardCharsets.UTF_8);

            // Convert JSON string back to the original object
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, valueType);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException |
                 NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | JsonProcessingException e) {
            throw new CustomDataException(e.getMessage(), ErrorCodes.INVALID_ARGUMENTS.getErrorCode());
        }
    }
}
