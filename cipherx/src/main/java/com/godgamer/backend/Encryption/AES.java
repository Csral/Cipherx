package com.godgamer.backend.Encryption;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.godgamer.backend.Handler.HandlerWrite;
import com.godgamer.backend.Handler.Block;
import com.godgamer.backend.Handler.Encrypter;
import com.godgamer.backend.Handler.HandlerRead;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private Encrypter extra_Encrypter;

    AES() throws Exception {
        keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        extra_Encrypter = new Encrypter();
        secretKey = keyGen.generateKey();
        this.MODIFY_IVSPACE();
    }

    public void MODIFY_KEY() throws Exception {

        secretKey = keyGen.generateKey();
        this.MODIFY_IVSPACE();

    }

    private byte[] RANDOM_SPACE_GEN() {

        try {
            byte[] res = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(res);
            return res;
        } catch (NoSuchAlgorithmException e) {
            byte[] res = new byte[16];
            new SecureRandom().nextBytes(res);
            return res;
        }

    }

    public void MODIFY_IVSPACE() {

        ivSpec = new IvParameterSpec(
            this.RANDOM_SPACE_GEN()
        );

    }

    public String KEY_SAVE() throws Exception {

        String filename = "key_" + System.currentTimeMillis() + "_" + ( (int) (Math.floor(Math.random()*2792)) & 2792) + ".obj.key";

        try (HandlerWrite oos = new HandlerWrite(filename);) {

            byte[] keyObj = this.secretKey.getEncoded();
            byte[] ivSpecObj = this.ivSpec.getIV();

            int mkey = this.generateKey(filename) * 255;

            SecureRandom mRandom = SecureRandom.getInstanceStrong();
            byte[] Rdata = new byte[mkey];

            int times = ( ( mRandom.ints(100, 1, 1000).reduce(0, (a, b) -> a ^ b) + mRandom.ints(100, 1, 1000).sum() ) & 511 ) + 1;

            oos.writeInt(times);
            
            for (int i = 0; i < times; i++) {

                Block tmp_block = new Block(i,System.currentTimeMillis(), mkey);
                mRandom.nextBytes(Rdata);
                tmp_block.updateData(Rdata);

                oos.writeBlock(tmp_block);

            }

            Block tmp_Block = new Block(mRandom.nextInt(), System.currentTimeMillis(), keyObj.length);
            tmp_Block.updateData(keyObj);

            oos.writeBlock(tmp_Block);

            tmp_Block.timestamp = System.currentTimeMillis();
            tmp_Block.updateData(ivSpecObj);
            tmp_Block.len = ivSpecObj.length;

            oos.writeBlock(tmp_Block);

            while (times > 0) {

                Block tmp_block = new Block(times,System.currentTimeMillis(), mkey);
                mRandom.nextBytes(Rdata);
                tmp_block.updateData(Rdata);

                oos.writeBlock(tmp_block);
                times--;

            }

            return filename;
        }

    }

    public String KEY_SAVE(boolean encrypt, String passwd) throws Exception {

        SecureRandom sr = SecureRandom.getInstanceStrong();

        String filename = "key_" + ( ( (System.currentTimeMillis() << sr.nextInt() ) & 32) ^ sr.nextInt()) + "_" + ( (int) (Math.floor(Math.random()*2792) + sr.ints(100,1,500).reduce(0, (a, b) -> a ^ b)) & 2792) + ".obj.key";

        try (HandlerWrite hos = new HandlerWrite(filename)) {

            byte[] keyObj = this.secretKey.getEncoded();
            byte[] ivSpecObj = this.ivSpec.getIV();

            if (encrypt) {

                keyObj = this.BYTE_E_MODIFY(passwd, keyObj);
                ivSpecObj = this.BYTE_E_MODIFY(passwd, ivSpecObj);

            }

            int mkey = this.generateKey(filename+passwd) * 255;
            int pkey = this.generateKey2(passwd);
            int pkey2 = this.generateKey(passwd); //* Generate normal pkey */

            SecureRandom mRandom = SecureRandom.getInstanceStrong();

            int n = (Math.abs(cxor(pkey2,pkey)) % 1000) + 1;
            int pos = (pkey ^ pkey2) % (n+1);

            int mtimes = 0;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((pkey + pkey2 + "").getBytes(StandardCharsets.UTF_8));
            int hashedTimes = ByteBuffer.wrap(hash).getInt();

            while (n > 0) {

                int ftimes = ( ( mRandom.ints(100, 1, 1000).reduce(0, (a, b) -> a ^ b) + mRandom.ints(100, 1, 1000).sum() ) & 511 ) + 1;

                if ( (n ^ pos) == 0) {
                    mtimes = ftimes;
                    hos.writeInt(ftimes ^ hashedTimes);
                    n--;
                } else {
                    hos.writeInt(ftimes ^ hashedTimes);
                    n--;
                }

            }

            int times = mtimes;
            long kpos = (Math.abs( extra_Encrypter.rotl(extra_Encrypter.bitMan_a(times ^ extra_Encrypter.rotl(pos, n+1)), extra_Encrypter.bitMan_c(extra_Encrypter.rotl(pkey, pkey2 % 63))) ) % times) + 1;

            for (int i = 0; i < times; i++) {

                if (i == kpos) {
                    //* Randomly write actual key somewhere :D */
                    Block keyBlock = new Block(57490*mRandom.nextInt(), System.currentTimeMillis()*mRandom.nextInt(), keyObj.length);
                    keyBlock.updateData(keyObj);

                    hos.writeBlock(keyBlock);
               
                } else {

                    SecretKey tmp = keyGen.generateKey();
                    byte[] tmpKeyObj = tmp.getEncoded();

                    if (encrypt) tmpKeyObj = BYTE_E_MODIFY(passwd, tmpKeyObj);

                    Block dtmp = new Block(47283*mRandom.nextInt(),System.currentTimeMillis() * mRandom.nextInt(),tmpKeyObj.length);
                    
                    dtmp.updateData(tmpKeyObj);
                    hos.writeBlock(dtmp);

                    dtmp = null;
                
                }

            }
            
            int pkey3 = generateKey3(passwd);
            n = (Math.abs(cxor(pkey3,pkey * pkey2)) % 1000) + 1;
            pos = (pkey3 ^ pkey2) % (n+1);

            hash = digest.digest((pkey + pkey2 + pkey3 + "").getBytes(StandardCharsets.UTF_8));
            hashedTimes = ByteBuffer.wrap(hash).getInt();

            while (n > 0) {

                int f2times = (984328724 + sr.nextInt(238849242) + ( sr.ints(256, 100, 5000).reduce(0, (a, b) -> a ^ b) & 0xFFFF) & 512) + 1;

                if ( (n ^ pos) == 0) {
                    mtimes = f2times;
                    hos.writeInt(f2times ^ hashedTimes);
                    n--;
                } else {
                    hos.writeInt(f2times ^ hashedTimes);
                    n--;
                }

            }

            times = mtimes;
            kpos = (Math.abs( extra_Encrypter.rotl(extra_Encrypter.bitMan_a(times ^ extra_Encrypter.rotl(pos ^ pkey2, n+1)), extra_Encrypter.bitMan_c(extra_Encrypter.rotl(pkey ^ pkey2, pkey3 % 63))) ) % times) + 1;

            for (int i = 0; i < times; i++) {

                if (i == kpos) {
                  
                    Block iVBlock = new Block(37908*mRandom.nextInt(), System.currentTimeMillis()*mRandom.nextInt(), ivSpecObj.length);
                    iVBlock.updateData(ivSpecObj);
                    hos.writeBlock(iVBlock);

                } else {
                  
                    Block tmp = new Block(84023*mRandom.nextInt(),System.currentTimeMillis()*mRandom.nextInt(),ivSpecObj.length);
                    if (encrypt) tmp.updateData(this.BYTE_E_MODIFY(passwd, this.RANDOM_SPACE_GEN()));
                    else tmp.updateData(this.RANDOM_SPACE_GEN());
                    hos.writeBlock(tmp);
                
                }

            }

            
            times = ( ( ((times ^ mRandom.nextInt()) & (mRandom.nextInt() ^ mkey)) + ( mRandom.ints(346, 1000, 10000).reduce(0, (a, b) -> a ^ b) + mRandom.ints(46, 1000, 10000).sum() ) ) & 511 ) + 1;
            times %= 1000;
            times++;

            while (times > 0) {

                int size = 8 + mRandom.nextInt(32);
                Block tmp = new Block(73033*mRandom.nextInt(),System.currentTimeMillis()*mRandom.nextInt(),size);
                mRandom.nextBytes(tmp.data);

                hos.writeBlock(tmp);
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

        try (HandlerWrite hos = new HandlerWrite(filename)) {

            byte[] keyObj = this.secretKey.getEncoded();
            byte[] ivSpecObj = this.ivSpec.getIV();

            if (encrypt) {

                keyObj = this.BYTE_E_MODIFY(passwd, keyObj);
                ivSpecObj = this.BYTE_E_MODIFY(passwd, ivSpecObj);

            }

            int mkey = this.generateKey(filename+passwd) * 255;
            int pkey = this.generateKey2(passwd);
            int pkey2 = this.generateKey(passwd); //* Generate normal pkey */

            SecureRandom mRandom = SecureRandom.getInstanceStrong();

            int n = (Math.abs(cxor(pkey2,pkey)) % 1000) + 1;
            int pos = (pkey ^ pkey2) % (n+1);

            int mtimes = 0;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((pkey + pkey2 + "").getBytes(StandardCharsets.UTF_8));
            int hashedTimes = ByteBuffer.wrap(hash).getInt();

            while (n > 0) {

                int ftimes = ( ( mRandom.ints(100*multiplicity, 1, 1000*(multiplicity2)).reduce(0, (a, b) -> a ^ b) + mRandom.ints(100*multiplicity, 1, 1000*(multiplicity2)).sum() ) & degree ) + 1;

                if ( (n ^ pos) == 0) {
                    mtimes = ftimes;
                    hos.writeInt(ftimes ^ hashedTimes);
                    n--;
                } else {
                    hos.writeInt(ftimes ^ hashedTimes);
                    n--;
                }

            }

            int times = mtimes;
            long kpos = (Math.abs( extra_Encrypter.rotl(extra_Encrypter.bitMan_a(times ^ extra_Encrypter.rotl(pos, n+1)), extra_Encrypter.bitMan_c(extra_Encrypter.rotl(pkey, pkey2 % 63))) ) % times) + 1;

            for (int i = 0; i < times; i++) {

                if (i == kpos) {
                    //* Randomly write actual key somewhere :D */
                    Block keyBlock = new Block(83490*mRandom.nextInt(), System.currentTimeMillis()*mRandom.nextInt(), keyObj.length);
                    keyBlock.updateData(keyObj);

                    hos.writeBlock(keyBlock);
               
                } else {

                    SecretKey tmp = keyGen.generateKey();
                    byte[] tmpKeyObj = tmp.getEncoded();

                    if (encrypt) tmpKeyObj = BYTE_E_MODIFY(passwd, tmpKeyObj);

                    Block dtmp = new Block(65783*mRandom.nextInt(),System.currentTimeMillis() * mRandom.nextInt(),tmpKeyObj.length);
                    
                    dtmp.updateData(tmpKeyObj);
                    hos.writeBlock(dtmp);

                    dtmp = null;
                
                }

            }
            
            int pkey3 = generateKey3(passwd);
            n = (Math.abs(cxor(pkey3,pkey * pkey2)) % 1000) + 1;
            pos = (pkey3 ^ pkey2) % (n+1);

            hash = digest.digest((pkey + pkey2 + pkey3 + "").getBytes(StandardCharsets.UTF_8));
            hashedTimes = ByteBuffer.wrap(hash).getInt();

            while (n > 0) {

                int f2times = 984328*Math.abs(multiplicity-multiplicity2) + sr.nextInt(2388429*(multiplicity+multiplicity2)) + ( sr.ints(256*multiplicity, 100, 5000*multiplicity2).reduce(0, (a, b) -> a ^ b) & (0xFFFF << (degree % 31)) );

                if ( (n ^ pos) == 0) {
                    mtimes = f2times;
                    hos.writeInt(f2times ^ hashedTimes);
                    n--;
                } else {
                    hos.writeInt(f2times ^ hashedTimes);
                    n--;
                }

            }

            times = mtimes;
            kpos = (Math.abs( extra_Encrypter.rotl(extra_Encrypter.bitMan_a(times ^ extra_Encrypter.rotl(pos ^ pkey2, n+1)), extra_Encrypter.bitMan_c(extra_Encrypter.rotl(pkey ^ pkey2, pkey3 % 63))) ) % times) + 1;

            for (int i = 0; i < times; i++) {

                if (i == kpos) {
                  
                    Block iVBlock = new Block(97908*mRandom.nextInt(), System.currentTimeMillis()*mRandom.nextInt(), ivSpecObj.length);
                    iVBlock.updateData(ivSpecObj);
                    hos.writeBlock(iVBlock);

                } else {
                  
                    Block tmp = new Block(54023*mRandom.nextInt(),System.currentTimeMillis()*mRandom.nextInt(),ivSpecObj.length);
                    if (encrypt) tmp.updateData(this.BYTE_E_MODIFY(passwd, this.RANDOM_SPACE_GEN()));
                    else tmp.updateData(this.RANDOM_SPACE_GEN());
                    hos.writeBlock(tmp);
                
                }

            }

            
            times = ( ( ((times ^ mRandom.nextInt()) & (mRandom.nextInt() ^ mkey)) + ( mRandom.ints(346, 1000, 10000).reduce(0, (a, b) -> a ^ b) + mRandom.ints(46, 1000, 10000).sum() ) ) & 511 ) + 1;
            times %= 1000;
            times++;

            while (times > 0) {

                int size = 8 + mRandom.nextInt(32);
                Block tmp = new Block(87033*mRandom.nextInt(),System.currentTimeMillis()*mRandom.nextInt(),size);
                mRandom.nextBytes(tmp.data);

                hos.writeBlock(tmp);
                times--;

            }

            return filename;

        }

    }

    public void KEY_LOAD(String filename) throws Exception {

        try (HandlerRead ois = new HandlerRead(filename)) {

                int times = ois.readInt();

                while (times > 0) {
                    Block ans = ois.readBlock();
                    ois.readByte(ans);
                    times--;
                }

                Block tmp = ois.readBlock();
                ois.readByte(tmp);

                byte[] keyBytes = tmp.data;

                tmp = ois.readBlock();
                ois.readByte(tmp);

                byte[] ivBytes = tmp.data;

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

        try (HandlerRead his = new HandlerRead(filename)) {

                int pkey = this.generateKey2(passwd);
                int pkey2 = this.generateKey(passwd); //* Generate normal pkey */

                int n = (Math.abs(cxor(pkey2,pkey)) % 1000) + 1;
                int pos = (pkey^pkey2) % (n+1);

                int times = 0, mtimes = 0;
                
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest((pkey + pkey2 + "").getBytes(StandardCharsets.UTF_8));
                int hashedTimes = ByteBuffer.wrap(hash).getInt();

                while (n > 0) {

                    times = his.readInt();

                    if ( (n ^ pos) == 0) {
                        mtimes = times ^ hashedTimes;
                    }

                    n--;

                }

                times = mtimes;

                long kpos = (Math.abs( extra_Encrypter.rotl(extra_Encrypter.bitMan_a(times ^ extra_Encrypter.rotl(pos, n+1)), extra_Encrypter.bitMan_c(extra_Encrypter.rotl(pkey, pkey2 % 63))) ) % times) + 1;

                Block keyBlock = new Block();
                byte[] keyBytes = new byte[32];

                for (int i = 0 ; i < times ; i++) {
                    
                    if (i == kpos) {
                    
                        keyBlock = his.readBlock();
                        his.readByte(keyBlock);
                        keyBytes = keyBlock.data;

                    } else {
                    
                        Block rtmp = his.readBlock();
                        his.readByte(rtmp);
                    
                    }

                }

                int pkey3 = generateKey3(passwd);

                hash = digest.digest((pkey + pkey2 + pkey3 + "").getBytes(StandardCharsets.UTF_8));
                hashedTimes = ByteBuffer.wrap(hash).getInt();

                n = (Math.abs(cxor(pkey3,pkey * pkey2)) % 1000) + 1;
                pos = (pkey3 ^ pkey2) % (n+1);

                while (n > 0) {

                    times = his.readInt();

                    if ( (n ^ pos) == 0) {
                        mtimes = times ^ hashedTimes;
                    }

                    n--;

                }

                times = mtimes;
                kpos = (Math.abs( extra_Encrypter.rotl(extra_Encrypter.bitMan_a(times ^ extra_Encrypter.rotl(pos ^ pkey2, n+1)), extra_Encrypter.bitMan_c(extra_Encrypter.rotl(pkey ^ pkey2, pkey3 % 63))) ) % times) + 1;

                Block ivBlock = new Block();
                byte[] ivBytes = new byte[16];

                for (int i = 0 ; i < times; i++) {

                    if (i == kpos) {
                        ivBlock = his.readBlock();
                        his.readByte(ivBlock);
                        ivBytes = ivBlock.data;
                    } else {
                        Block rtmp = his.readBlock();
                        his.readByte(rtmp);
                    }
                }

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

    private int iSum(byte x) {

        int p = 0;
        int res = 0;

        while ((int) (x / (int) Math.pow(10,p)) > 0) {

            res += x % (int) Math.pow(10,p);
            x = (byte) (x % (int) Math.pow(10,p));

        }

        return res;

    }

    private byte[] byteSum(byte[] ele) {

        byte[] result = new byte[ele.length];

        for (int i = 0; i < ele.length; i++) {
            result[i] = (byte) iSum(ele[i]);
        }

        return result;

    }

    private int insideSum(byte[] x) {

        int result = 0;
        byte[] cBytes = byteSum(x);

        for (byte b : cBytes)
            result += (int) b;

        return result;

    }

    private int insideSum(byte[] x, int y) {

        int result = 0;
        byte[] cBytes = byteSum(x);

        for (byte b : cBytes)
            result += (int) b  ^ y;

        return result;

    }

    private int insideProd(byte[] x) {

        int result = 1;
        byte[] cBytes = byteSum(x);

        for (byte b : cBytes)
            result *= (int) b;

        return result;

    }

    private int insideProd(byte[] x, int y) {

        int result = 1;
        byte[] cBytes = byteSum(x);

        for (byte b : cBytes)
            result *= (int) b ^ y;

        return result;

    }

    private int generateKey3(String passwd) {
        int key = 0;
        byte[] passwdBytes = passwd.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < passwdBytes.length; i++) {
            int ascii = passwdBytes[i] & 0xFF;
            
            int k = ( insideSum(passwdBytes, ( (int) passwdBytes[i] << (i+1) ) ) ^ insideProd(passwdBytes, ( (int) passwdBytes[i] << (i+1) )) ) << (i);

            int nextAscii = (i + 1 < passwdBytes.length) ? passwdBytes[i + 1] & 0xFF : passwdBytes[0] & 0xFF;
            int n = k ^ ( nextAscii ^ ascii );
            
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

        this.MODIFY_KEY();
        this.MODIFY_IVSPACE();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());

        this.KEY_SAVE();

        return Base64.getEncoder().encodeToString(encrypted);
    
    }

    public String encrypt_CBC(String data, String passwd) throws Exception {

        this.MODIFY_KEY();
        this.MODIFY_IVSPACE();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());

        this.KEY_SAVE(true, passwd);

        return Base64.getEncoder().encodeToString(encrypted);
    
    }

    public String encrypt_CBC(String data, String passwd, int degree_of_security, boolean off_limits) throws Exception {

        this.MODIFY_KEY();
        this.MODIFY_IVSPACE();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());

        this.KEY_SAVE_SECURE(true, passwd, degree_of_security, off_limits);

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