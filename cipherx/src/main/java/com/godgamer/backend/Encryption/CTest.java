package com.godgamer.backend.Encryption;

import java.util.Base64;
public class CTest {
 
    public static void main(String[] args) {
        try {
            ChaCha20 chacha = new ChaCha20();

            String message = "This is a top secret message!";

            byte[] encryptedBytes = chacha.encrypt(message.getBytes(), "temp");
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.println("Encrypted (Base64): " + encrypted);

            System.out.println(encryptedBytes);

            String filename = chacha.get_active_encrypted_filename();

            ChaCha20 chachaDecryptor = new ChaCha20();

            byte[] decByte = chachaDecryptor.decrypt(Base64.getDecoder().decode(encrypted), filename, "temp");
            String decrypted = new String(decByte);
            System.out.println("Decrypted: " + decrypted);

            System.out.println(decByte);

        } catch(Exception e) {
            System.out.println("Something went wrong: " + e);
        }

    }

}