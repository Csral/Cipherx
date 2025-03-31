package Encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.security.SecureRandom;

public class AES {

    private KeyGenerator keyGen;
    private SecretKey secretKey;
    private IvParameterSpec ivSpec;

    AES() throws Exception {
        keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        secretKey = keyGen.generateKey();
        this.MODIFY_IVSPACE();
    }

    public void MODIFY_KEY() throws Exception {

        secretKey = keyGen.generateKey();
        this.MODIFY_IVSPACE();

    }

    private byte[] RANDOM_SPACE_GEN() {

        byte[] res = new byte[16];
        new SecureRandom().nextBytes(res);
        return res;

    }

    public void MODIFY_IVSPACE() {

        ivSpec = new IvParameterSpec(
            this.RANDOM_SPACE_GEN()
        );

    }

    public String encrypt_CBC(String data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    
    }

    public String decrypt_CBC(String encryptedData) throws Exception {
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted);
    
    }

    public String encrypt_ECB(String data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);

    }

    public String decrypt_ECB(String encryptedData) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted);

    }

    public String encrypt_CFB(String data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);

    }

    public String decrypt_CFB(String encryptedData) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted);

    }

    public String encrypt_OFB(String data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);

    }

    public String decrypt_OFB(String eData) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(eData));
        return new String(decrypted);

    }

    public String encrypt_CTR(String data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);

    }

    public String decrypt_CTR(String eData) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(eData));
        return new String(decrypted);

    }

    public String encrypt_GCM(String data) throws Exception {
        
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);

    }

    public String decrypt_GCM(String eData) throws Exception {
        
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted  = cipher.doFinal(Base64.getDecoder().decode(eData));
        return new String(decrypted);

    }

    public String encrypt_CCM(String data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);

    }

    public String decrypt(String encryptedData) throws Exception {
        
        Cipher cipher = Cipher.getInstance("AES/CCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);

    }

}