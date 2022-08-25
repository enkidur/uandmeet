//package com.project.uandmeet.security;
//
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.Base64;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
//public class Aes256 {
//    @Value("${aes256.secretKey}")
//    private String secretKey;
//    private String iv = "abcdefghijklmnop"; // 16자리 iv
//
//    // 암호화
//    public String encrypt(String text) {
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE,
//                    new SecretKeySpec(secretKey.getBytes(), "AES"),
//                    new IvParameterSpec(iv.getBytes()));
//
//            return new String(Base64.getEncoder().encode(cipher.doFinal(text.getBytes("UTF-8"))));
//        } catch(Exception e) {
//            return text;
//        }
//    }
//
//    // 복호화
//    public String decrypt(String encryptedText) {
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE,
//                    new SecretKeySpec(secretKey.getBytes(), "AES"),
//                    new IvParameterSpec(iv.getBytes()));
//
//            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText.getBytes("UTF-8"))));
//        } catch(Exception e) {
//            return encryptedText;
//        }
//    }
//}