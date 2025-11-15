package com.ovvium.services.util.ovvium.encryption;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtils {

    private EncryptionUtils() {
    }

    /**
     * Very simple AES Encryptor / Decryptor. This can be improved but for simple Encryption should be enough.
     */
    private static class AESEncryptor {

        public static final String INSTANCE_NAME = "AES/ECB/PKCS5Padding";

        @SneakyThrows
        public String encrypt(String strToEncrypt, String secret) {
            var secretKey = createKey(secret);
            Cipher cipher = Cipher.getInstance(INSTANCE_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }

        @SneakyThrows
        public String decrypt(String strToDecrypt, String secret) {
            var secretKey = createKey(secret);
            Cipher cipher = Cipher.getInstance(INSTANCE_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }

        private SecretKeySpec createKey(String myKey) {
            MessageDigest sha;
            try {
                byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
                sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);
                key = Arrays.copyOf(key, 16);
                return new SecretKeySpec(key, "AES");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static String encryptAES(String value, String secret) {
        return new AESEncryptor().encrypt(value, secret);
    }

    public static String decryptAES(String value, String secret) {
        return new AESEncryptor().decrypt(value, secret);
    }

}
