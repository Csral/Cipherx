package com.godgamer.backend.Encryption;

public class Test {

    public static void main(String[] args) {
        
        try {

            AES aes = new AES();

            String message = "This is a top secret message!";

            String encrypted = aes.encrypt_CTR(message, "temp"); // or encrypt_GCM, encrypt_CBC, etc.
            System.out.println("Encrypted: " + encrypted);

            String filename = aes.get_active_encrypted_filename(); // You must implement this method

            AES aesDecryptor = new AES();

            String decrypted = aesDecryptor.decrypt_CTR(encrypted, filename, "temp"); // load key manually before this if needed
            System.out.println("Decrypted: " + decrypted);

        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }

    }

}