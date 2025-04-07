package Encryption;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class AES {

    private static final Map<Character, Integer> EGC = new HashMap<>();

    static {

        EGC.put('a', 23);
        EGC.put('b', 12);
        EGC.put('c', 45);
        EGC.put('d', 30);
        EGC.put('e', 18);
        EGC.put('f', 39);
        EGC.put('g', 5);
        EGC.put('h', 27);
        EGC.put('i', 50);
        EGC.put('j', 7);
        EGC.put('k', 33);
        EGC.put('l', 15);
        EGC.put('m', 48);
        EGC.put('n', 21);
        EGC.put('o', 9);
        EGC.put('p', 37);
        EGC.put('q', 14);
        EGC.put('r', 46);
        EGC.put('s', 3);
        EGC.put('t', 29);
        EGC.put('u', 51);
        EGC.put('v', 8);
        EGC.put('w', 36);
        EGC.put('x', 16);
        EGC.put('y', 44);
        EGC.put('z', 2);

        EGC.put('A', 41);
        EGC.put('B', 6);
        EGC.put('C', 28);
        EGC.put('D', 10);
        EGC.put('E', 34);
        EGC.put('F', 1);
        EGC.put('G', 25);
        EGC.put('H', 11);
        EGC.put('I', 47);
        EGC.put('J', 4);
        EGC.put('K', 35);
        EGC.put('L', 17);
        EGC.put('M', 49);
        EGC.put('N', 24);
        EGC.put('O', 22);
        EGC.put('P', 40);
        EGC.put('Q', 13);
        EGC.put('R', 38);
        EGC.put('S', 31);
        EGC.put('T', 20);
        EGC.put('U', 32);
        EGC.put('V', 19);
        EGC.put('W', 26);
        EGC.put('X', 43);
        EGC.put('Y', 42);
        EGC.put('Z', 9);

    }

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

    public String KEY_SAVE() throws Exception {

        String filename = "key_" + System.currentTimeMillis() + "_" + ( (int) (Math.floor(Math.random()*2792)) & 2792) + ".obj.key";

        try (FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {

            byte[] keyObj = this.secretKey.getEncoded();
            byte[] ivSpecObj = this.ivSpec.getIV();

            int mkey = this.generateKey(filename) * 255;

            SecureRandom mRandom = SecureRandom.getInstanceStrong();
            byte[] Rdata = new byte[mkey];

            int times = mRandom.ints().reduce(0, (a, b) -> a ^ b) + mRandom.ints().sum();

            oos.writeInt(times);

            for (int i = 0; i < times; i++) {

                mRandom.nextBytes(Rdata);
                oos.writeObject(Rdata);

            }

            oos.writeObject(keyObj);
            oos.writeObject(ivSpecObj);

            while (times > 0) {

                mRandom.nextBytes(Rdata);
                oos.writeObject(Rdata);
                times--;

            }

            return filename;
        }

    }

    public String KEY_SAVE(boolean encrypt, String passwd) throws Exception {

        SecureRandom sr = SecureRandom.getInstanceStrong();

        String filename = "key_" + ( ( (System.currentTimeMillis() << sr.nextInt() ) & 32) ^ sr.nextInt()) + "_" + ( (int) (Math.floor(Math.random()*2792) + sr.ints(100,1,500).reduce(0, (a, b) -> a ^ b)) & 2792) + ".obj.key";

        try (FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {

            byte[] keyObj = this.secretKey.getEncoded();
            byte[] ivSpecObj = this.ivSpec.getIV();

            if (encrypt) {

                keyObj = this.BYTE_E_MODIFY(passwd, keyObj);
                ivSpecObj = this.BYTE_E_MODIFY(passwd, ivSpecObj);

            }

            int mkey = this.generateKey(filename+passwd) * 255;
            int pkey = this.generateKey2(passwd);
            int pkey2 = this.generateKey(passwd); //* Generate normal pkey */
            
            //! Generate key 2 is for n fake timed times.
            //! ( mkey ^ pkey ) & (n-1) is where it'll be randomly placed

            SecureRandom mRandom = SecureRandom.getInstanceStrong();

            int n = Math.abs(cxor(pkey2,pkey));
            int times = ( ( mRandom.ints(100, 1, 1000).reduce(0, (a, b) -> a ^ b) + mRandom.ints(100, 1, 1000).sum() ) & 511 ) + 1;
            int pos = (pkey ^ pkey2) % (n+1);

            int mtimes = 0;
            int size = 8 + mRandom.nextInt(32);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((pkey + pkey2 + "").getBytes(StandardCharsets.UTF_8));
            int hashedTimes = ByteBuffer.wrap(hash).getInt();

            while (n > 0) {

                if ( (n ^ pos) == 0) {
                    mtimes = times;
                    oos.writeInt(mtimes ^ hashedTimes);
                    n--;
                } else {
                    oos.writeInt(times ^ hashedTimes);
                    n--;
                }

            }

            times = mtimes;

            for (int i = 0; i < times; i++) {

                byte[] Rdata = new byte[size];
                mRandom.nextBytes(Rdata);
                oos.writeObject(Rdata);

            }
            // todo Store fake key (n times, n derived from passwd hash) (Store newTimes somewhere in between these n fake keys derived by password) and same for ivspec  //
            oos.writeObject(keyObj);

            times = ( ( ((times ^ pkey) & mkey) + ( mRandom.ints(146, 100, 10000).reduce(0, (a, b) -> a ^ b) + mRandom.ints(146, 100, 10000).sum() ) ) & 511 ) + 1;

            oos.writeInt(times ^ hashedTimes);

            for (int i = 0; i < times; i++) {

                byte[] Rdata = new byte[size];
                mRandom.nextBytes(Rdata);
                oos.writeObject(Rdata);

            }

            oos.writeObject(ivSpecObj);

            times = ( ( ((times ^ mRandom.nextInt()) & (mRandom.nextInt() ^ mkey)) + ( mRandom.ints(346, 1000, 10000).reduce(0, (a, b) -> a ^ b) + mRandom.ints(46, 1000, 10000).sum() ) ) & 511 ) + 1;

            while (times > 0) {
                
                byte[] Rdata = new byte[size];
                mRandom.nextBytes(Rdata);
                oos.writeObject(Rdata);
                times--;

            }

            return filename;

        }

    }

    public String KEY_SAVE_SECURE(boolean encrypt, String passwd, int degree_of_security, boolean beyond_limit) throws Exception {

        SecureRandom sr = SecureRandom.getInstanceStrong();

        int degree = (int) Math.pow(2, 10 +degree_of_security);
        int multiplicity = 1, multiplicity2 = 1;
        if (beyond_limit) {

            multiplicity = degree_of_security;
            multiplicity2 = degree_of_security*100;
            if (multiplicity < 1) {
                multiplicity = 1;
                multiplicity2 = 1;
            }

        }

        String filename = "key_" + ( ( (System.currentTimeMillis() << sr.nextInt() ) & 32) ^ sr.nextInt()) + "_" + ( (int) (Math.floor(Math.random()*2792) + sr.ints(100,1,500).reduce(0, (a, b) -> a ^ b)) & 2792) + ".obj.key";

        try (FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {

            byte[] keyObj = this.secretKey.getEncoded();
            byte[] ivSpecObj = this.ivSpec.getIV();

            if (encrypt) {

                keyObj = this.BYTE_E_MODIFY(passwd, keyObj);
                ivSpecObj = this.BYTE_E_MODIFY(passwd, ivSpecObj);

            }

            int mkey = this.generateKey(filename+passwd) * 255;
            int pkey = this.generateKey2(passwd);
            int pkey2 = this.generateKey(passwd); //* Generate normal pkey */
            
            //! Generate key 2 is for n fake timed times.
            //! (pkey ^ pkey2) % (n+1) is where it'll be randomly placed

            SecureRandom mRandom = SecureRandom.getInstanceStrong();

            int n = Math.abs(cxor(pkey2,pkey));
            int times = ( ( mRandom.ints(100*multiplicity, 1, 1000*(multiplicity2)).reduce(0, (a, b) -> a ^ b) + mRandom.ints(100*multiplicity, 1, 1000*(multiplicity2)).sum() ) & degree ) + 1;
            int pos = (pkey ^ pkey2) % (n+1);

            int mtimes = 0;

            int size = 8 + mRandom.nextInt(32);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((pkey + pkey2 + "").getBytes(StandardCharsets.UTF_8));
            int hashedTimes = ByteBuffer.wrap(hash).getInt();

            while (n > 0) {

                if ( (n ^ pos) == 0) {
                    mtimes = times;
                    oos.writeInt(mtimes ^ hashedTimes);
                    n--;
                } else {
                    oos.writeInt(times ^ hashedTimes);
                    n--;
                }

            }

            times = mtimes;

            for (int i = 0; i < times; i++) {

                byte[] Rdata = new byte[size];
                mRandom.nextBytes(Rdata);
                oos.writeObject(Rdata);

            }
            // todo Store fake key (n times, n derived from passwd hash) (Store newTimes somewhere in between these n fake keys derived by password) and same for ivspec  //
            oos.writeObject(keyObj);

            times = ( ( ((times ^ pkey) & mkey) + ( mRandom.ints(146*multiplicity, 100, 10000*(multiplicity2)).reduce(0, (a, b) -> a ^ b) + mRandom.ints(146*(multiplicity*100), 100, 10000*(multiplicity2)).sum() ) ) & degree ) + 1;

            oos.writeInt(times ^ hashedTimes);

            for (int i = 0; i < times; i++) {

                byte[] Rdata = new byte[size];
                mRandom.nextBytes(Rdata);
                oos.writeObject(Rdata);

            }

            oos.writeObject(ivSpecObj);

            times = ( ( ((times ^ mRandom.nextInt()) & (mRandom.nextInt() ^ mkey)) + ( mRandom.ints(346*multiplicity, 1000, 10000*multiplicity2).reduce(0, (a, b) -> a ^ b) + mRandom.ints(46*multiplicity, 1000, 10000*multiplicity2).sum() ) ) & degree ) + 1;

            while (times > 0) {

                byte[] Rdata = new byte[size];
                mRandom.nextBytes(Rdata);
                oos.writeObject(Rdata);
                times--;

            }

            return filename;

        }

    }

    public void KEY_LOAD(String filename) throws Exception {

        try (FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis)) {

                int times = ois.readInt();

                while (times > 0) {
                    ois.readObject();
                    times--;
                }

                byte[] keyBytes = (byte[]) ois.readObject();
                byte[] ivBytes = (byte[]) ois.readObject();

                this.secretKey = new SecretKeySpec(keyBytes, "AES");
                this.ivSpec = new IvParameterSpec(ivBytes);

        } catch (Exception eof) {
            
            SecureRandom sr = SecureRandom.getInstanceStrong();
            byte[] tmp = new byte[16];
            sr.nextBytes(tmp);

            this.ivSpec = new IvParameterSpec(tmp);
            this.secretKey = keyGen.generateKey();

        }

        //! Also catch InvalidAlgorithmParameterException e

    }

    public void KEY_LOAD(String filename, boolean encrypted, String passwd) throws Exception {

        try (FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis)) {

                int pkey = this.generateKey2(passwd);
                int pkey2 = this.generateKey(passwd); //* Generate normal pkey */

                int n = Math.abs(cxor(pkey2,pkey));
                int pos = (pkey^pkey2) % (n+1);

                int times = 0, mtimes = 0;
                
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest((pkey + pkey2 + "").getBytes(StandardCharsets.UTF_8));
                int hashedTimes = ByteBuffer.wrap(hash).getInt();

                while (n > 0) {

                    times = ois.readInt();

                    if ( (n ^ pos) == 0) {
                        mtimes = times ^ hashedTimes;
                    }

                    n--;

                }
                times = mtimes;

                while (times > 0) {
                    ois.readObject();
                    times--;
                }

                byte[] keyBytes = (byte[]) ois.readObject();

                times = ois.readInt() ^ hashedTimes;

                while (times > 0) {
                    ois.readObject();
                    times--;
                }

                byte[] ivBytes = (byte[]) ois.readObject();

                if (encrypted) {
                    
                    keyBytes = this.BYTE_E_MODIFY(passwd, keyBytes);
                    ivBytes = this.BYTE_E_MODIFY(passwd, ivBytes);

                }

                this.secretKey = new SecretKeySpec(keyBytes, "AES");
                this.ivSpec = new IvParameterSpec(ivBytes);

        } catch (Exception eofe) {
            
            SecureRandom sr = SecureRandom.getInstanceStrong();
            byte[] tmp = new byte[16];
            sr.nextBytes(tmp);

            this.ivSpec = new IvParameterSpec(tmp);
            this.secretKey = keyGen.generateKey();

        }

    }

    public int cxor(int a, int b) {
        return (( ( a & b ) << (a^b) ) ^ ( (a|b) << ((a&b)*(a|b)))) % 9223372;
    }

    private int generateKey(String passwd) {
        int key = 0;
        byte[] passwdBytes = passwd.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < passwdBytes.length; i++) {
            int ENG_CODE_CONTENT = EGC.getOrDefault((char) passwdBytes[i], 30);
            int ascii = passwdBytes[i] & 0xFF;
            int position = i + 1;

            int k = (ascii * position + 1);
            k = (k << ((position + ENG_CODE_CONTENT) % 16)) & 0xFFFF;

            int nextAscii = (i + 1 < passwdBytes.length) ? passwdBytes[i + 1] & 0xFF : passwdBytes[0] & 0xFF;
            int n = k ^ nextAscii;
            
            key ^= n;
        }

        return key;
    }

    private int generateKey2(String passwd) {
        int key = 0;
        byte[] passwdBytes = passwd.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < passwdBytes.length; i++) {
            int ascii = passwdBytes[i] & 0xFF;
            int ENG_CODE_CONTENT = EGC.getOrDefault((char) passwdBytes[i], 30) ^ ascii << (passwdBytes.length & key);
            int position = Math.abs(i + 1 + ENG_CODE_CONTENT - ascii);

            int k = (ascii * position + 1);
            k = (k << ((position + ENG_CODE_CONTENT) % 32)) & 0xFFFF;

            int nextAscii = (i + 1 < passwdBytes.length) ? passwdBytes[i + 1] & 0xFF : passwdBytes[0] & 0xFF;
            int n = k ^ nextAscii;
            
            key ^= n;
        }

        return key;
    }

    private byte[] BYTE_E_MODIFY(String passwd, byte[] content) {

        /*
         * For every letter in passwd: 
         * (letter ascii * its_pos + 1) = k
         * Left shift k the (its_pos+ ENG_CODE_CONTENT (default: 30) times)
         * xor with the next letter ascii (last letter xored with first) = n
         * Xor of all n's gives gives the key
        */

        int key = this.generateKey(passwd);
        byte[] res = new byte[content.length];

        for (int i = 0; i < content.length; i++) {
            
            res[i] = (byte) (( ((int) content[i]) ^ key ) & 255);

        }

        return res;

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

}