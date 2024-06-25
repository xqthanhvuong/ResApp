package com.example.restaurantmanager.helper;

import android.os.Build;

import androidx.annotation.RequiresApi;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class EncryptionHelper {
    private static final String TRANSFORMATION = "AES/CBC/PKCS7Padding";

    // Mã hóa dữ liệu
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        String ivBase64 = Base64.getEncoder().encodeToString(iv);
        String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
        return ivBase64 + ":" + encryptedBase64;
    }

    // Giải mã dữ liệu
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        String[] parts = encryptedData.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] cipherText = Base64.getDecoder().decode(parts[1]);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(cipherText);
        return new String(decrypted, "UTF-8");
    }
}
