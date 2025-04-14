package com.godgamer.backend.Encryption;

public class CTest {
 
    public static void main(String[] args) {
        
        try {
            ChaCha20 chacha = new ChaCha20();

            String message = "This is a top secret message!";

            String encrypted = chacha.encrypt(message, "temp");
            System.out.println("Encrypted: " + encrypted);

            String filename = chacha.get_active_encrypted_filename();

            ChaCha20 chachaDecryptor = new ChaCha20();

            String decrypted = chachaDecryptor.decrypt(encrypted, filename, "temp");
            System.out.println("Decrypted: " + decrypted);

        } catch(Exception e) {
            System.out.println("Something went wrong: " + e);
        }

    }

}