package com.example.sem6project.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String ENCRYPT_ALGO = "AES/CBC/PKCS7Padding"; //in code they used AES/GCM/NoPadding
    public static boolean error_occur;

    public static HashMap encrypt(@NonNull byte[] dataToEncrypt, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        HashMap map = new HashMap();

        try {
            SecureRandom secureRandom = new SecureRandom();

            byte[] salt = new byte[256];

            secureRandom.nextBytes(salt);

            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, 1324, 256);

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            SecretKey secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(pbeKeySpec).getEncoded(), "AES");

            SecureRandom ivRandom = new SecureRandom();

            byte[] iv = new byte[16];

            ivRandom.nextBytes(iv);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

            cipher.init(Cipher.ENCRYPT_MODE, (Key) secretKey, (AlgorithmParameterSpec) ivParameterSpec);

            byte[] encryptedFile = cipher.doFinal(dataToEncrypt);

            ((Map) map).put("salt", salt);

            ((Map) map).put("iv", iv);

            ((Map) map).put("encryptedFile", encryptedFile);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (Exception var15){

        }

        return map;
    }

    public static byte[] decrypt(@NonNull HashMap map, @NonNull String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        byte[] decrypted = (byte[]) null;

        try {

            byte[] salt = (byte[])map.get("salt");

            byte[] iv = (byte[])map.get("iv");

            byte[] encryptedFile = (byte[])map.get("encFile");

            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, 1324, 256);

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            SecretKey secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(pbeKeySpec).getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, (Key) secretKey, (AlgorithmParameterSpec) ivParameterSpec);

            decrypted = cipher.doFinal(encryptedFile);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        } catch (InvalidKeySpecException e) {

            e.printStackTrace();
        } catch (NoSuchPaddingException e) {

            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {

            e.printStackTrace();
        } catch (InvalidKeyException e) {

            e.printStackTrace();
        } catch (BadPaddingException e) {
            error_occur = true;
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }catch (Exception var13){
        }
        return decrypted;
    }
}
