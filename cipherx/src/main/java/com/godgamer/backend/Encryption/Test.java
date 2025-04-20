package com.godgamer.backend.Encryption;

import java.util.Base64;
public class Test {

    public static void main(String[] args) {
        
        try {

            AES aes = new AES();

            // String message = "This is a top secret message!";
            String message = ".wm.asds";
            byte[] enc = aes.encrypt_CBC(message.getBytes(), "SarjaPur@Satya");
            String encrypted = Base64.getEncoder().encodeToString(enc);

            System.out.println("Encrypted: " + encrypted);

            String filename = aes.get_active_encrypted_filename(); // You must implement this method
            System.out.println("Filename: " + filename);

            AES aesDecryptor = new AES();

            byte[] dec = aesDecryptor.decrypt_CBC(Base64.getDecoder().decode(encrypted), filename, "SarjaPur@Satya"); // load key manually before this if needed
            String decrypted = new String(dec);
            System.out.println("Decrypted: " + decrypted);

        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }

    }

}