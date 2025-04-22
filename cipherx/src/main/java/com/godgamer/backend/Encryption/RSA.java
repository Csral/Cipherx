package com.godgamer.backend.Encryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Scanner;

import javax.crypto.Cipher;

public class RSA {

    private final static BigInteger E = new BigInteger("65537");

    private BigInteger n;
    private BigInteger d;
    private BigInteger e;

    private static class SimulatedBlock {

        long id;
        long timestamp;
        int len;
        byte[] data;

        public SimulatedBlock(long id, long timestamp, int len, byte[] data) {
            this.id = id;
            this.timestamp = timestamp;
            this.len = len;
            this.data = data;
        }
    }

    public boolean isPublicKeyLoaded()
    {
        return this.n != null && this.e != null;
    }
    public boolean isPrivateKeyLoaded() {
        return this.n != null && this.d != null;
    }

    public void generateKeys(int degreeOfSecurity, boolean offLimit) {
        int bitLength = 1024 + (degreeOfSecurity * 256);
        SecureRandom random = new SecureRandom();
        BigInteger p, q;

        do {
            p = new BigInteger(bitLength, 100, random);
            q = new BigInteger(bitLength, 100, random);
        } while (p.equals(q));

        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = E;
        d = e.modInverse(phi);

        System.out.println("Key generation complete.");
        System.out.println("Public Key (e, n): (" + e.toString().substring(0, Math.min(e.toString().length(), 20)) + "..., " + n.toString().substring(0, Math.min(n.toString().length(), 20)) + "...)");
        System.out.println("Private Key (d, n): (" + d.toString().substring(0, Math.min(d.toString().length(), 20)) + "..., " + n.toString().substring(0, Math.min(d.toString().length(), 20)) + "...)");
    }

    public void KEY_SAVE_SECURE(String publicKeyFilename, String privateKeyFilename, boolean encrypt, String passwd, int degreeOfSecurity, boolean beyondLimit) {
        if (this.n == null || this.e == null || this.d == null) {
            System.err.println("Keys have not been generated yet. Call generateKeys first.");
            return;
        }

        SecureRandom sr = new SecureRandom();

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(publicKeyFilename))) {
            byte[] nBytes = this.n.toByteArray();
            byte[] eBytes = this.e.toByteArray();

            if (encrypt) {
                nBytes = simulate_BYTE_E_MODIFY(passwd, nBytes);
                eBytes = simulate_BYTE_E_MODIFY(passwd, eBytes);
            }

            int degree = (int) Math.pow(2, 10 + degreeOfSecurity);
            int multiplicity = 1, multiplicity2 = 1;
            if (beyondLimit) {
                multiplicity = degreeOfSecurity > 0 ? degreeOfSecurity : 1;
                multiplicity2 = degreeOfSecurity * 100 > 0 ? degreeOfSecurity * 100 : 1;
            }

            int numRandomBlocks = (sr.nextInt(100 * multiplicity) + 50) * (beyondLimit ? 2 : 1);
            dos.writeInt(numRandomBlocks);

            for (int i = 0; i < numRandomBlocks; i++) {
                int randomSize = 16 + sr.nextInt(32 * multiplicity2);
                byte[] randomData = new byte[randomSize];
                sr.nextBytes(randomData);
                SimulatedBlock randomBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), randomSize, randomData);
                writeSimulatedBlock(dos, randomBlock);
            }

            SimulatedBlock nBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), nBytes.length, nBytes);
            writeSimulatedBlock(dos, nBlock);

            SimulatedBlock eBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), eBytes.length, eBytes);
            writeSimulatedBlock(dos, eBlock);

            int numTrailingRandomBlocks = (sr.nextInt(100 * multiplicity) + 50) * (beyondLimit ? 2 : 1);
            dos.writeInt(numTrailingRandomBlocks);

            for (int i = 0; i < numTrailingRandomBlocks; i++) {
                int randomSize = 16 + sr.nextInt(32 * multiplicity2);
                byte[] randomData = new byte[randomSize];
                sr.nextBytes(randomData);
                SimulatedBlock randomBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), randomSize, randomData);
                writeSimulatedBlock(dos, randomBlock);
            }

            System.out.println("Public key conceptually saved to " + publicKeyFilename);

        } catch (IOException ex) {
            System.err.println("Error saving public key: " + ex.getMessage());
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(privateKeyFilename))) {
            byte[] nBytes = this.n.toByteArray();
            byte[] dBytes = this.d.toByteArray();

            if (encrypt) {
                nBytes = simulate_BYTE_E_MODIFY(passwd, nBytes);
                dBytes = simulate_BYTE_E_MODIFY(passwd, dBytes);
            }

            int degree = (int) Math.pow(2, 10 + degreeOfSecurity);
            int multiplicity = 1, multiplicity2 = 1;
            if (beyondLimit) {
                multiplicity = degreeOfSecurity > 0 ? degreeOfSecurity : 1;
                multiplicity2 = degreeOfSecurity * 100 > 0 ? degreeOfSecurity * 100 : 1;
            }

            int numRandomBlocks = (sr.nextInt(100 * multiplicity) + 50) * (beyondLimit ? 2 : 1);
            dos.writeInt(numRandomBlocks);

            for (int i = 0; i < numRandomBlocks; i++) {
                int randomSize = 16 + sr.nextInt(32 * multiplicity2);
                byte[] randomData = new byte[randomSize];
                sr.nextBytes(randomData);
                SimulatedBlock randomBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), randomSize, randomData);
                writeSimulatedBlock(dos, randomBlock);
            }

            SimulatedBlock nBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), nBytes.length, nBytes);
            writeSimulatedBlock(dos, nBlock);

            SimulatedBlock dBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), dBytes.length, dBytes);
            writeSimulatedBlock(dos, dBlock);

            int numTrailingRandomBlocks = (sr.nextInt(100 * multiplicity) + 50) * (beyondLimit ? 2 : 1);
            dos.writeInt(numTrailingRandomBlocks);

            for (int i = 0; i < numTrailingRandomBlocks; i++) {
                int randomSize = 16 + sr.nextInt(32 * multiplicity2);
                byte[] randomData = new byte[randomSize];
                sr.nextBytes(randomData);
                SimulatedBlock randomBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), randomSize, randomData);
                writeSimulatedBlock(dos, randomBlock);
            }

            System.out.println("Private key conceptually saved to " + privateKeyFilename);
            System.out.println("Note: This is a simulated secure save. Implement your actual HandlerWrite and Block logic.");

        } catch (IOException ex) {
            System.err.println("Error saving private key: " + ex.getMessage());
        }
    }

    public void KEY_LOAD_PUBLIC(String filename, boolean encrypted, String passwd) {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int numRandomBlocks = dis.readInt();
            for (int i = 0; i < numRandomBlocks; i++) {
                readSimulatedBlock(dis);
            }

            SimulatedBlock nBlock = readSimulatedBlock(dis);
            SimulatedBlock eBlock = readSimulatedBlock(dis);

            byte[] nBytes = nBlock.data;
            byte[] eBytes = eBlock.data;

            if (encrypted) {
                nBytes = simulate_BYTE_E_MODIFY(passwd, nBytes);
                eBytes = simulate_BYTE_E_MODIFY(passwd, eBytes);
            }

            this.n = new BigInteger(nBytes);
            this.e = new BigInteger(eBytes);
            this.d = null;

            int numTrailingRandomBlocks = dis.readInt();
            for (int i = 0; i < numTrailingRandomBlocks; i++) {
                readSimulatedBlock(dis);
            }

            System.out.println("Public key loaded successfully from " + filename);
            System.out.println("Public Key (e, n): (" + e.toString().substring(0, Math.min(e.toString().length(), 20)) + "..., " + n.toString().substring(0, Math.min(n.toString().length(), 20)) + "...)");

        } catch (IOException ex) {
            System.err.println("Error loading public key: " + ex.getMessage());
            this.n = null;
            this.e = null;
            this.d = null;
        }
    }

    public void KEY_LOAD_PRIVATE(String filename, boolean encrypted, String passwd) {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int numRandomBlocks = dis.readInt();
            for (int i = 0; i < numRandomBlocks; i++) {
                readSimulatedBlock(dis);
            }

            SimulatedBlock nBlock = readSimulatedBlock(dis);
            SimulatedBlock dBlock = readSimulatedBlock(dis);

            byte[] nBytes = nBlock.data;
            byte[] dBytes = dBlock.data;

            if (encrypted) {
                nBytes = simulate_BYTE_E_MODIFY(passwd, nBytes);
                dBytes = simulate_BYTE_E_MODIFY(passwd, dBytes);
            }

            this.n = new BigInteger(nBytes);
            this.d = new BigInteger(dBytes);
            this.e = null;

            int numTrailingRandomBlocks = dis.readInt();
            for (int i = 0; i < numTrailingRandomBlocks; i++) {
                readSimulatedBlock(dis);
            }

            System.out.println("Private key loaded successfully from " + filename);
            System.out.println("Private Key (d, n): (" + d.toString().substring(0, Math.min(d.toString().length(), 20)) + "..., " + n.toString().substring(0, Math.min(n.toString().length(), 20)) + "...)");

        } catch (IOException ex) {
            System.err.println("Error loading private key: " + ex.getMessage());
            this.n = null;
            this.e = null;
            this.d = null;
        }
    }

    private void writeSimulatedBlock(DataOutputStream dos, SimulatedBlock block) throws IOException {
        dos.writeLong(block.id);
        dos.writeLong(block.timestamp);
        dos.writeInt(block.len);
        dos.write(block.data);
    }

    private SimulatedBlock readSimulatedBlock(DataInputStream dis) throws IOException {
        long id = dis.readLong();
        long timestamp = dis.readLong();
        int len = dis.readInt();
        byte[] data = new byte[len];
        dis.readFully(data);
        return new SimulatedBlock(id, timestamp, len, data);
    }

    private byte[] simulate_BYTE_E_MODIFY(String passwd, byte[] content) {
        try {
            byte[] passwdBytes = passwd.getBytes(StandardCharsets.UTF_8);
            byte[] modifiedContent = new byte[content.length];
            int key = 0;
            for (byte b : passwdBytes) {
                key ^= b;
            }

            for (int i = 0; i < content.length; i++) {
                modifiedContent[i] = (byte) (content[i] ^ key);
            }
            return modifiedContent;
        } catch (Exception e) {
            e.printStackTrace();
            return content;
        }
    }

    public byte[] encrypt(byte[] data) throws Exception {
        if (this.e == null || this.n == null) {
            throw new IllegalStateException("Public key is not loaded or generated.");
        }
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(this.n, this.e);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] encryptedData) throws Exception {
        if (this.d == null || this.n == null) {
            throw new IllegalStateException("Private key is not loaded or generated.");
        }
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(this.n, this.d);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(encryptedData);
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getE() {
        return e;
    }

    public static void main(String[] args) {
        RSA rsa = new RSA();
        Scanner scanner = new Scanner(System.in);

        int degreeOfSecurity = 1;
        boolean offLimit = true;

        System.out.println("Generating keys with degreeOfSecurity = " + degreeOfSecurity + " and offLimit = " + offLimit);
        rsa.generateKeys(degreeOfSecurity, offLimit);

        String publicKeyFilename = "rsa_public.key";
        String privateKeyFilename = "rsa_private.key";
        String password = "mysecretpassword";
        boolean encryptKeys = true;

        System.out.println("\nSimulating secure key saving to " + publicKeyFilename + " and " + privateKeyFilename);
        rsa.KEY_SAVE_SECURE(publicKeyFilename, privateKeyFilename, encryptKeys, password, degreeOfSecurity, offLimit);

        System.out.println("\n--- Loading Public Key ---");
        RSA rsaPublicLoader = new RSA();
        rsaPublicLoader.KEY_LOAD_PUBLIC(publicKeyFilename, encryptKeys, password);

        System.out.println("\n--- Loading Private Key ---");
        RSA rsaPrivateLoader = new RSA();
        rsaPrivateLoader.KEY_LOAD_PRIVATE(privateKeyFilename, encryptKeys, password);

        if (rsaPublicLoader.getN() == null || rsaPrivateLoader.getN() == null) {
            System.err.println("Failed to load keys. Exiting.");
            return;
        }

        System.out.println("\n--- Encryption with User Input ---");
        System.out.print("Enter the text to encrypt: ");
        String originalMessage = scanner.nextLine();

        byte[] messageBytes = originalMessage.getBytes(StandardCharsets.UTF_8);

        System.out.println("Original Message: " + originalMessage);

        try {
            byte[] encryptedBytes = rsaPublicLoader.encrypt(messageBytes);
            System.out.println("Encrypted Message (Bytes): " + new BigInteger(encryptedBytes).toString(16));

            byte[] decryptedBytes = rsaPrivateLoader.decrypt(encryptedBytes);

            String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);
            System.out.println("Decrypted Message (Plaintext): " + decryptedString);

            if (originalMessage.equals(decryptedString)) {
                System.out.println("Decryption successful!");
            } else {
                System.out.println("Decryption failed!");
            }

        } catch (IllegalStateException e) {
            System.err.println("Error during encryption/decryption: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }
}
