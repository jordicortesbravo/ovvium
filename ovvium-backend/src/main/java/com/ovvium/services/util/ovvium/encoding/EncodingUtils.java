package com.ovvium.services.util.ovvium.encoding;

import lombok.*;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.text.MessageFormat;
import java.util.Base64;

public class EncodingUtils {

    private EncodingUtils(){}

    @Getter
    @RequiredArgsConstructor
    public enum Algorithm {
        MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"), SHA512("SHA-512");

        private final String code;
    }

    @Getter
    @RequiredArgsConstructor
    public enum HmacAlgorithm {
        MD5("HmacMD5", "HMAC-MD5"), SHA1("HmacSHA1", "HMAC-SHA1"), SHA256("HmacSHA256", "HMAC-256"), SHA512("HmacSHA512", "HMAC-512");

        private final String code;
        private final String id;
    }

    @RequiredArgsConstructor
    public enum SecretKeyAlgorithm {
        PBKDF2("PBKDF2With{0}"), AES("AES"), ARCFOUR("ARCFOUR"), DES("DES"), DESEDE("DESede");

        private final String code;

        public String getCode() {
            return getCode(null);
        }

        public String getCode(HmacAlgorithm hmacAlgorithm) {
            return hmacAlgorithm != null ? MessageFormat.format(code, hmacAlgorithm.getCode()) : code;
        }
    }

    @RequiredArgsConstructor
    public enum SecureRandomAlgorithm {
        NATIVE_PRNG("NativePRNG"), PKCS11("PKCS11"), SHA1PRNG("SHA1PRNG");

        @Getter
        private final String code;
    }

    @SneakyThrows
    public static byte[] hash(Algorithm algorithm, InputStream is) {
        val digest = MessageDigest.getInstance(algorithm.getCode());
        byte[] buffer = new byte[1024];
        @Cleanup val dis = new DigestInputStream(is, digest);
        while (dis.read(buffer) != -1) {}
        return digest.digest();
    }

    public static byte[] hash(Algorithm algorithm, byte[] data) {
        return hash(algorithm, new ByteArrayInputStream(data));
    }

    public static byte[] hash(Algorithm algorithm, String data, Charset charset) {
        return hash(algorithm, data.getBytes(charset));
    }

    @SneakyThrows
    public static byte[] mac(HmacAlgorithm algorithm, String data, String key) {
        val mac = Mac.getInstance(algorithm.getCode());
        mac.init(new SecretKeySpec(key.getBytes(), algorithm.getCode()));
        return mac.doFinal(data.getBytes());
    }

    public static byte[] secret(SecretKeyAlgorithm algorithm, KeySpec spec) {
        return secret(algorithm, null, spec);
    }

    @SneakyThrows
    public static byte[] secret(SecretKeyAlgorithm algorithm, HmacAlgorithm digest, KeySpec spec) {
        return SecretKeyFactory.getInstance(algorithm.getCode(digest)).generateSecret(spec).getEncoded();
    }

    @SneakyThrows
    public static byte[] rng(SecureRandomAlgorithm algorithm, int length) {
        return SecureRandom.getInstance(algorithm.getCode()).generateSeed(length);
    }

    public static byte[] md5(InputStream is) {
        return hash(Algorithm.MD5, is);
    }

    public static byte[] sha1(InputStream is) {
        return hash(Algorithm.SHA1, is);
    }

    public static byte[] sha256(InputStream is) {
        return hash(Algorithm.SHA256, is);
    }

    public static byte[] sha512(InputStream is) {
        return hash(Algorithm.SHA512, is);
    }

    public static byte[] md5(byte[] data) {
        return hash(Algorithm.MD5, data);
    }

    public static byte[] sha1(byte[] data) {
        return hash(Algorithm.SHA1, data);
    }

    public static byte[] sha256(byte[] data) {
        return hash(Algorithm.SHA256, data);
    }

    public static byte[] sha512(byte[] data) {
        return hash(Algorithm.SHA512, data);
    }

    public static byte[] md5(String data, Charset charset) {
        return hash(Algorithm.MD5, data, charset);
    }

    public static byte[] sha1(String data, Charset charset) {
        return hash(Algorithm.SHA1, data, charset);
    }

    public static byte[] sha256(String data, Charset charset) {
        return hash(Algorithm.SHA256, data, charset);
    }

    public static byte[] sha512(String data, Charset charset) {
        return hash(Algorithm.SHA512, data, charset);
    }

    public static byte[] hmacMd5(String data, String key) {
        return mac(HmacAlgorithm.MD5, data, key);
    }

    public static byte[] hmacSha1(String data, String key) {
        return mac(HmacAlgorithm.SHA1, data, key);
    }

    public static byte[] hmacSha256(String data, String key) {
        return mac(HmacAlgorithm.SHA256, data, key);
    }

    public static byte[] hmacSha512(String data, String key) {
        return mac(HmacAlgorithm.SHA512, data, key);
    }

    public static byte[] pbkdf2(String secret, byte[] salt, int rounds, int keyLength) {
        return pbkdf2(secret, salt, rounds, keyLength, HmacAlgorithm.SHA1);
    }

    public static byte[] pbkdf2(String secret, byte[] salt, int rounds, int keyLength, HmacAlgorithm hmacAlgorithm) {
        return secret(SecretKeyAlgorithm.PBKDF2, hmacAlgorithm, new PBEKeySpec(secret.toCharArray(), salt, rounds, keyLength * Byte.SIZE));
    }

    public static String hex(byte[] data) {
        return DatatypeConverter.printHexBinary(data);
    }

    public static String hex(InputStream is) {
        return hex(getBytes(is));
    }

    public static String hex(String data, Charset charset) {
        return hex(data.getBytes(charset));
    }

    public static String hexSha512(String data) {
        return hex(sha512(data.getBytes()));
    }

    public static String b64(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }

    public static String b64(InputStream is) {
        return b64(getBytes(is));
    }

    public static String b64(String data, Charset charset) {
        return b64(data.getBytes(charset));
    }

    public static byte[] decodeHex(String encoded) {
        return DatatypeConverter.parseHexBinary(encoded);
    }

    public static byte[] decodeB64(String encoded, boolean enablePadding) {
        if(enablePadding) {
            return Base64.getDecoder().decode(encoded);
        } else {
            return DatatypeConverter.parseBase64Binary(encoded);
        }
    }
    
    public static byte[] decodeB64(String encoded) {
        return decodeB64(encoded, false);
    }

    // TODO: Juraria que això ho tenim a algun altre lloc :o
    @SneakyThrows
    private static byte[] getBytes(InputStream is) {
        @Cleanup val os = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            os.write(data, 0, nRead);
        }
        os.flush();
        return os.toByteArray();
    }

}
