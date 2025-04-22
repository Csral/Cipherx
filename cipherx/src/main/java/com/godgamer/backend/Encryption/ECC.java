package com.godgamer.backend.Encryption;

import java.io.DataInputStream; // Import ByteBuffer
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey; // For specifying ECC curve parameters
import java.security.SecureRandom; // For encoding/decoding private keys
import java.security.spec.ECGenParameterSpec; // For encoding/decoding public keys
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner; // For ECGenParameterSpec

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ECC {

    // Parameters for PBKDF2 and AES-CBC
    private static final int PBKDF2_ITERATIONS = 10000; // Number of iterations for PBKDF2 (increase for security)
    private static final int KEY_LENGTH_BITS = 256; // Derived key length (for AES-256)
    private static final int SALT_LENGTH_BYTES = 16; // Salt length for PBKDF2
    private static final int AES_BLOCK_SIZE_BYTES = 16; // AES block size for IV

    // ECC Curve Specification (using a standard NIST curve)
    private static final String ECC_CURVE = "secp256r1"; // A commonly used 256-bit NIST curve

    private PrivateKey privateKey;
    private PublicKey publicKey;

    // Using the SimulatedBlock structure for file handling, similar to RSA
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

    public ECC() {
        // Standard JCA providers should have ECC support
    }

    public boolean isPublicKeyLoaded() {
        return this.publicKey != null;
    }

    public boolean isPrivateKeyLoaded() {
        return this.privateKey != null;
    }

    /**
     * Generates a new ECC key pair using a specified curve.
     */
    public void generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(ECC_CURVE);
            keyGen.initialize(ecSpec, new SecureRandom());
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

            System.out.println("ECC Key generation complete using curve: " + ECC_CURVE);
            System.out.println("Public Key Algorithm: " + publicKey.getAlgorithm());
            System.out.println("Private Key Algorithm: " + privateKey.getAlgorithm());

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            System.err.println("Error generating ECC keys: " + ex.getMessage());
            ex.printStackTrace();
            this.privateKey = null;
            this.publicKey = null;
        }
    }

    /**
     * Saves the public key to a file (unencrypted) and the private key to a
     * file (encrypted). The private key is encrypted using PBKDF2 and AES-CBC
     * with a password.
     *
     * @param publicKeyFilename The filename for the public key.
     * @param privateKeyFilename The filename for the private key.
     * @param passwd The password to encrypt the private key.
     * @param degreeOfSecurity Security degree (influences random data in file
     * structure).
     * @param beyondLimit Beyond limit flag (influences random data in file
     * structure).
     */
    public void KEY_SAVE_SECURE(String publicKeyFilename, String privateKeyFilename, String passwd, int degreeOfSecurity, boolean beyondLimit) {
        if (this.privateKey == null || this.publicKey == null) {
            System.err.println("ECC keys have not been generated yet. Call generateKeys first.");
            return;
        }

        SecureRandom sr = new SecureRandom();

        // Save Public Key (unencrypted)
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(publicKeyFilename))) {
            // Public keys are typically saved in X.509 format
            byte[] publicKeyBytes = this.publicKey.getEncoded();

            // Simulate writing random blocks and key data based on original structure
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

            SimulatedBlock publicKeyBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), publicKeyBytes.length, publicKeyBytes);
            writeSimulatedBlock(dos, publicKeyBlock);

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

        // Save Private Key (encrypted with password using PBKDF2 and AES-CBC)
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(privateKeyFilename))) {
            // Private keys are typically saved in PKCS#8 format
            byte[] privateKeyBytes = this.privateKey.getEncoded();

            // Generate salt for PBKDF2
            byte[] salt = new byte[SALT_LENGTH_BYTES];
            sr.nextBytes(salt);

            // Derive AES key using PBKDF2
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(passwd.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS);
            SecretKey aesKey = new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "AES");

            // Generate IV for AES-CBC
            byte[] iv = new byte[AES_BLOCK_SIZE_BYTES]; // IV size must match block size for CBC
            sr.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Encrypt private key data with AES-CBC
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Use CBC with PKCS5Padding
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            byte[] encryptedPrivateKeyBytes = cipher.doFinal(privateKeyBytes);

            // Simulate writing random blocks and encrypted data based on original structure
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

            // Write salt, IV, and encrypted data (wrapped in SimulatedBlocks)
            SimulatedBlock saltBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), salt.length, salt);
            writeSimulatedBlock(dos, saltBlock);

            SimulatedBlock ivBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), iv.length, iv);
            writeSimulatedBlock(dos, ivBlock);

            SimulatedBlock encryptedDataBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), encryptedPrivateKeyBytes.length, encryptedPrivateKeyBytes);
            writeSimulatedBlock(dos, encryptedDataBlock);

            int numTrailingRandomBlocks = (sr.nextInt(100 * multiplicity) + 50) * (beyondLimit ? 2 : 1);
            dos.writeInt(numTrailingRandomBlocks);

            for (int i = 0; i < numTrailingRandomBlocks; i++) {
                int randomSize = 16 + sr.nextInt(32 * multiplicity2);
                byte[] randomData = new byte[randomSize];
                sr.nextBytes(randomData);
                SimulatedBlock randomBlock = new SimulatedBlock(sr.nextLong(), System.currentTimeMillis(), randomSize, randomData);
                writeSimulatedBlock(dos, randomBlock);
            }

            System.out.println("Private key conceptually saved securely to " + privateKeyFilename);
            System.out.println("Note: This is a simulated secure save. Implement your actual HandlerWrite and Block logic if needed.");

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException ex) {
            System.err.println("Error saving private key: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Loads the public key from a file.
     *
     * @param filename The filename of the public key.
     */
    public void KEY_LOAD_PUBLIC(String filename) {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            // Read and discard initial random blocks
            int numRandomBlocks = dis.readInt();
            for (int i = 0; i < numRandomBlocks; i++) {
                readSimulatedBlock(dis);
            }

            // Read public key bytes block
            SimulatedBlock publicKeyBlock = readSimulatedBlock(dis);
            byte[] publicKeyBytes = publicKeyBlock.data;

            // Read and discard trailing random blocks
            int numTrailingRandomBlocks = dis.readInt();
            for (int i = 0; i < numTrailingRandomBlocks; i++) {
                readSimulatedBlock(dis);
            }

            // Reconstruct PublicKey object from bytes (X.509 format)
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = keyFactory.generatePublic(keySpec);
            this.privateKey = null; // Ensure private key is not loaded

            if (!validatePublicKey()) {
                throw new InvalidKeySpecException("Loaded public key failed validation.");
            }

            System.out.println("Public key loaded successfully from " + filename);
            System.out.println("Loaded Public Key Algorithm: " + publicKey.getAlgorithm());

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Error loading public key: " + ex.getMessage());
            this.publicKey = null;
            this.privateKey = null;
        }
    }

    /**
     * Loads and decrypts the private key from a file using PBKDF2 and AES-CBC.
     *
     * @param filename The filename of the private key.
     * @param passwd The password to decrypt the private key.
     */
    public void KEY_LOAD_PRIVATE(String filename, String passwd) {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            // Read and discard initial random blocks
            int numRandomBlocks = dis.readInt();
            for (int i = 0; i < numRandomBlocks; i++) {
                readSimulatedBlock(dis);
            }

            // Read salt, IV, and encrypted data blocks
            SimulatedBlock saltBlock = readSimulatedBlock(dis);
            byte[] salt = saltBlock.data;

            SimulatedBlock ivBlock = readSimulatedBlock(dis);
            byte[] iv = ivBlock.data;

            SimulatedBlock encryptedDataBlock = readSimulatedBlock(dis);
            byte[] encryptedPrivateKeyBytes = encryptedDataBlock.data;

            // Read and discard trailing random blocks
            int numTrailingRandomBlocks = dis.readInt();
            for (int i = 0; i < numTrailingRandomBlocks; i++) {
                readSimulatedBlock(dis);
            }

            // Derive AES key using PBKDF2, the loaded salt, and the provided password
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(passwd.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS);
            SecretKey aesKey = new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "AES");

            // Decrypt private key data with AES-CBC using the derived key and loaded IV
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            byte[] privateKeyBytes = cipher.doFinal(encryptedPrivateKeyBytes);

            // Reconstruct PrivateKey object from bytes (PKCS#8 format)
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = keyFactory.generatePrivate(keySpec);
            this.publicKey = null; // Ensure public key is not loaded

            if (!validatePrivateKey()) {
                throw new InvalidKeySpecException("Loaded private key failed validation.");
            }

            System.out.println("Private key loaded successfully from " + filename);
            System.out.println("Loaded Private Key Algorithm: " + privateKey.getAlgorithm());

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException ex) {
            System.err.println("Error loading private key: " + ex.getMessage());
            ex.printStackTrace();
            this.privateKey = null;
            this.publicKey = null;
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

    /**
     * Encrypts data using ECC ECIES with AES-CBC for symmetric encryption.
     * Note: This is a simplified ECIES implementation for demonstration. A full
     * ECIES implementation would typically include a KDF for the shared secret
     * and potentially a MAC for authentication.
     *
     * @param data The byte array to encrypt.
     * @return The encrypted byte array (includes ephemeral public key, IV, and
     * ciphertext).
     * @throws Exception if an error occurs during encryption.
     */
    public byte[] encrypt(byte[] data) throws Exception {
        if (this.publicKey == null) {
            throw new IllegalStateException("Public key is not loaded or generated.");
        }

        // Generate an ephemeral ECC key pair for ECIES
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(ECC_CURVE);
        keyGen.initialize(ecSpec, new SecureRandom());
        KeyPair ephemeralKeyPair = keyGen.generateKeyPair();
        PrivateKey ephemeralPrivateKey = ephemeralKeyPair.getPrivate();
        PublicKey ephemeralPublicKey = ephemeralKeyPair.getPublic(); // This will be sent with ciphertext

        // Perform ECDH key agreement to derive a shared secret
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(ephemeralPrivateKey);
        keyAgreement.doPhase(this.publicKey, true);
        // Get the raw shared secret bytes
        byte[] sharedSecretBytes = keyAgreement.generateSecret(); // Get raw bytes

        // Use the raw bytes to create an AES SecretKeySpec
        SecretKey sharedSecret = new SecretKeySpec(sharedSecretBytes, 0, KEY_LENGTH_BITS / 8, "AES"); // Use KEY_LENGTH_BITS / 8 for byte length

        // Generate IV for AES-CBC
        SecureRandom sr = new SecureRandom();
        byte[] iv = new byte[AES_BLOCK_SIZE_BYTES];
        sr.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Encrypt data with AES-CBC using the shared secret
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sharedSecret, ivSpec);
        byte[] ciphertext = cipher.doFinal(data);

        // Concatenate ephemeral public key bytes, IV, and ciphertext for transmission
        byte[] ephemeralPublicKeyBytes = ephemeralPublicKey.getEncoded();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + ephemeralPublicKeyBytes.length + 4 + iv.length + ciphertext.length);
        byteBuffer.putInt(ephemeralPublicKeyBytes.length);
        byteBuffer.put(ephemeralPublicKeyBytes);
        byteBuffer.putInt(iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(ciphertext);

        return byteBuffer.array();
    }

    /**
     * Decrypts data using ECC ECIES with AES-CBC for symmetric decryption.
     *
     * @param encryptedData The encrypted byte array (includes ephemeral public
     * key, IV, and ciphertext).
     * @return The decrypted byte array.
     * @throws Exception if an error occurs during decryption.
     */
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        if (this.privateKey == null) {
            throw new IllegalStateException("Private key is not loaded or generated.");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);

        // Extract ephemeral public key bytes, IV, and ciphertext
        int ephemeralPublicKeyLength = byteBuffer.getInt();
        byte[] ephemeralPublicKeyBytes = new byte[ephemeralPublicKeyLength];
        byteBuffer.get(ephemeralPublicKeyBytes);

        int ivLength = byteBuffer.getInt();
        byte[] iv = new byte[ivLength];
        byteBuffer.get(iv);

        byte[] ciphertext = new byte[byteBuffer.remaining()];
        byteBuffer.get(ciphertext);

        // Reconstruct ephemeral public key object
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(ephemeralPublicKeyBytes);
        PublicKey ephemeralPublicKey = keyFactory.generatePublic(keySpec);

        // Perform ECDH key agreement to derive the shared secret
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(this.privateKey);
        keyAgreement.doPhase(ephemeralPublicKey, true);
        // Get the raw shared secret bytes
        byte[] sharedSecretBytes = keyAgreement.generateSecret(); // Get raw bytes

        // Use the raw bytes to create an AES SecretKeySpec
        SecretKey sharedSecret = new SecretKeySpec(sharedSecretBytes, 0, KEY_LENGTH_BITS / 8, "AES"); // Use KEY_LENGTH_BITS / 8 for byte length

        // Decrypt data with AES-CBC using the shared secret and IV
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sharedSecret, ivSpec);
        byte[] decryptedData = cipher.doFinal(ciphertext);

        return decryptedData;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private String getUserPassword(Scanner scanner) {
        System.out.print("Enter password: ");
        return scanner.nextLine();
    }

    private boolean validatePublicKey() {
        // Basic validation: check if key object is not null and algorithm is EC
        return this.publicKey != null && "EC".equals(this.publicKey.getAlgorithm());
    }

    private boolean validatePrivateKey() {
        // Basic validation: check if key object is not null and algorithm is EC
        return this.privateKey != null && "EC".equals(this.privateKey.getAlgorithm());
    }

    public static void main(String[] args) {

        ECC ecc = new ECC();
        Scanner scanner = new Scanner(System.in);

        int degreeOfSecurity = 1; // This will influence the simulated random data in file structure
        boolean offLimit = true; // This will influence the simulated random data in file structure

        System.out.println("Generating ECC keys...");
        ecc.generateKeys();

        if (ecc.getPrivateKey() == null || ecc.getPublicKey() == null) {
            System.err.println("Failed to generate ECC keys. Exiting.");
            scanner.close();
            return;
        }

        String publicKeyFilename = "ecc_public.key";
        String privateKeyFilename = "ecc_private.key";

        System.out.println("\n--- Saving ECC Keys ---");
        System.out.println("Enter password to protect the PRIVATE key file:");
        String savePassword = ecc.getUserPassword(scanner);
        try {
            ecc.KEY_SAVE_SECURE(publicKeyFilename, privateKeyFilename, savePassword, degreeOfSecurity, offLimit);
        } catch (Exception e) {
            System.err.println("Error during ECC key saving: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- Loading ECC Public Key ---");
        ECC eccPublicLoader = new ECC();
        try {
            eccPublicLoader.KEY_LOAD_PUBLIC(publicKeyFilename);
        } catch (Exception e) {
            System.err.println("Error during ECC public key loading: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- Loading ECC Private Key ---");
        System.out.println("Enter password to load the PRIVATE key file (must match the password used for saving):");
        ECC eccPrivateLoader = new ECC();
        String loadPassword = eccPrivateLoader.getUserPassword(scanner);
        try {
            eccPrivateLoader.KEY_LOAD_PRIVATE(privateKeyFilename, loadPassword);
        } catch (Exception e) {
            System.err.println("Error during ECC private key loading: " + e.getMessage());
            e.printStackTrace();
        }

        if (eccPublicLoader.getPublicKey() == null || eccPrivateLoader.getPrivateKey() == null) {
            System.err.println("Failed to load ECC keys. Exiting.");
            scanner.close();
            return;
        }

        System.out.println("\n--- ECC Encryption with User Input ---");
        System.out.print("Enter the text to encrypt: ");
        String originalMessage = scanner.nextLine();

        byte[] messageBytes = originalMessage.getBytes(StandardCharsets.UTF_8);

        System.out.println("Original Message: " + originalMessage);

        try {
            // Encrypt using the loaded public key
            byte[] encryptedBytes = eccPublicLoader.encrypt(messageBytes);
            System.out.println("Encrypted Message (Bytes - includes ephemeral public key, IV, ciphertext): " + new BigInteger(1, encryptedBytes).toString(16));

            // Decrypt using the loaded private key
            byte[] decryptedBytes = eccPrivateLoader.decrypt(encryptedBytes);

            String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);
            System.out.println("Decrypted Message (Plaintext): " + decryptedString);

            if (originalMessage.equals(decryptedString)) {
                System.out.println("Decryption successful!");
            } else {
                System.out.println("Decryption failed!");
            }

        } catch (IllegalStateException e) {
            System.err.println("Error during ECC encryption/decryption: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }
}
