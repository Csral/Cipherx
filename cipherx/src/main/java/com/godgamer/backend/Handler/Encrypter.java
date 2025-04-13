package com.godgamer.backend.Handler;

import java.nio.charset.StandardCharsets;

public class Encrypter {
 
    private String passwd;
    private byte[] inf;
    private int secureGenKeyManipulator;
    private long key = 9842390;

    //! Randomize this as often as you can! Store this as well!
    private long bitMan_a_inf, bitMan_a_inf0, bitMan_a_inf1, bitMan_a_inf2, bitMan_a_inf3, bitMan_a_inf4, bitMan_a_inf5;

    public Encrypter() {
        //* No chaos */
        bitMan_a_inf = bitMan_a_inf0 = bitMan_a_inf1 = bitMan_a_inf2 = bitMan_a_inf3 = bitMan_a_inf4 = bitMan_a_inf5 = 0;
    }

    public long genKey() {

        byte[] passwdBytes = passwd.getBytes(StandardCharsets.UTF_8);

        long lcl_key = 832932949;

        for (byte b : passwdBytes) {
            long k1 = bitMan_a(b);
            long k2 = bitMan_b(b);
            long k3 = bitMan_c(b);

            lcl_key ^= bitMan_c(k1 ^ k2) ^ k3;

        }

        this.key ^= bitMan_c(lcl_key);
        this.key = bitMan_c(key);

        return this.key;

    }

    public int genRotlKey() {

        int lcl_key = 8329329;

        byte[] passwdBytes = passwd.getBytes(StandardCharsets.UTF_8);

        for (byte b : passwdBytes) {
            long k1 = bitMan_a(b+64);
            long k2 = bitMan_b(Math.abs(b-64));
            long k3 = bitMan_c(b);

            lcl_key ^= rotl(bitMan_c(k1 ^ k2) ^ k3, (b+1 % 63) + 1);

        }

        return ((lcl_key + 2) % 63) + 3;

    }

    public int rotl(int val, int shift) {
        return (val << shift) | (val >>> (64 - shift));
    }

    public long rotl(long val, int shift) {
        return (val << shift) | (val >>> (64 - shift));
    }

    public long rotl(long val, long shift) {
        return (val << shift) | (val >>> (64 - shift));
    }

    public long bitMan_a(byte x) {
        //* Manipulates bits stage - a */

        long xLong = (long) x;
        long swapped = 0;

        for (int i = 0; i < 64; i += 2) {
            long bit1 = (xLong >> (63 - i)) & 1;
            long bit2 = (xLong >> (62 - i)) & 1;
            swapped |= (bit1 << (62 - i)) | (bit2 << (63 - i));
        }

        int m = (int) (swapped % 511);
        return ( (m << xLong) ^ (xLong % 1024) ) ^ (m*xLong - 9213 + bitMan_a_inf + bitMan_a_inf0 + bitMan_a_inf1 + bitMan_a_inf2 + bitMan_a_inf3 + bitMan_a_inf4 + bitMan_a_inf5);

    }

    public long bitMan_a(int x) {
        //* Manipulates bits stage - a */

        long xLong = (long) x;
        long swapped = 0;

        for (int i = 0; i < 64; i += 2) {
            long bit1 = (xLong >> (63 - i)) & 1;
            long bit2 = (xLong >> (62 - i)) & 1;
            swapped |= (bit1 << (62 - i)) | (bit2 << (63 - i));
        }

        int m = (int) (swapped % 511);
        return ( (m << xLong) ^ (xLong % 1024) ) ^ (m*xLong - 9213 + bitMan_a_inf + bitMan_a_inf0 + bitMan_a_inf1 + bitMan_a_inf2 + bitMan_a_inf3 + bitMan_a_inf4 + bitMan_a_inf5);

    }

    public long bitMan_a(long x) {
        //* Manipulates bits stage - a */

        long xLong = (long) x;
        long swapped = 0;

        for (int i = 0; i < 64; i += 2) {
            long bit1 = (xLong >> (63 - i)) & 1;
            long bit2 = (xLong >> (62 - i)) & 1;
            swapped |= (bit1 << (62 - i)) | (bit2 << (63 - i));
        }

        int m = (int) (swapped % 511);
        return ( (m << xLong) ^ (xLong % 1024) ) ^ (m*xLong - 9213 + bitMan_a_inf + bitMan_a_inf0 + bitMan_a_inf1 + bitMan_a_inf2 + bitMan_a_inf3 + bitMan_a_inf4 + bitMan_a_inf5);

    }

    public long bitMan_b(byte x) {
        //* Manipulates bits stage - b */

        long xLong = (long) x;

        long lower = xLong & 0xFFFFFFFFL;
        long upper = (xLong >>> 32) & 0xFFFFFFFFL;
        return (lower << 32) | upper;
    }

    public long bitMan_b(int x) {
        //* Manipulates bits stage - b */

        long xLong = (long) x;

        long lower = xLong & 0xFFFFFFFFL;
        long upper = (xLong >>> 32) & 0xFFFFFFFFL;
        return (lower << 32) | upper;
    }

    public long bitMan_b(long x) {
        //* Manipulates bits stage - b */

        long xLong = (long) x;

        long lower = xLong & 0xFFFFFFFFL;
        long upper = (xLong >>> 32) & 0xFFFFFFFFL;
        return (lower << 32) | upper;
    }

    public long bitMan_c(byte x) {
        //* Manipulates bits stage - c */

        long input = (long) x;

        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) ((input >> (8 * i)) & 0xFF);
        }

        //* Chaos Mix of 8 Bytes */
        long[] A = new long[8];
        for (int round = 0; round < 8; round++) {
            byte[] temp = bytes.clone();
            for (int i = 0; i < round; i++) {
                byte t = temp[0];
                System.arraycopy(temp, 1, temp, 0, 7);
                temp[7] = t;
            }

            long mix = 0;
            for (int i = 0; i < 8; i += 2) {
                int xor = (temp[i] ^ temp[i + 1]) & 0xFF;
                int add = (temp[i] + temp[i + 1]) & 0xFF;
                mix ^= ((xor * add) ^ rotl(xor, i + 1)) ^ rotl(add, 7 - i);
            }

            A[round] = rotl(mix, round + 3) ^ (input >>> round);
        }

        //* 4x 16-bit Chunks Mix */
        long[] B = new long[4];
        for (int i = 0; i < 4; i++) {
            long seg = (input >>> (i * 16)) & 0xFFFF;
            long left = (seg >> 8) & 0xFF;
            long right = seg & 0xFF;
            long mult = (left + 1) * (right + 3);
            B[i] = rotl((left ^ right ^ mult) + i * 17, i * 3 + 1);
        }

        //* Final XOR of 32-bit halves + rotate */
        long upper = (input >>> 32) & 0xFFFFFFFFL;
        long lower = input & 0xFFFFFFFFL;
        long C = rotl((upper ^ lower ^ (upper * 3 + lower * 7)), 11);

        //* Mix A and B */
        long[] AB = new long[4];
        for (int i = 0; i < 4; i++) {
            AB[i] = (A[i] ^ A[7 - i]) + B[i] + rotl(B[i], i + 1);
        }

        //* Final Chaos Mix */
        long[] finalStage = new long[4];
        for (int i = 0; i < 4; i++) {
            finalStage[i] = AB[i] ^ rotl(C, i * 2 + 3);
        }

        //* Final calculation: max * min ^ xor(rest) */
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        long xorMid = 0;
        for (long v : finalStage) {
            max = Math.max(max, v);
            min = Math.min(min, v);
        }
        for (long v : finalStage) {
            if (v != max && v != min) xorMid ^= v;
        }

        long product = max * min;
        return product ^ xorMid;

    }

    public long bitMan_c(int x) {
        //* Manipulates bits stage - c */

        long input = (long) x;

        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) ((input >> (8 * i)) & 0xFF);
        }

        //* Chaos Mix of 8 Bytes */
        long[] A = new long[8];
        for (int round = 0; round < 8; round++) {
            byte[] temp = bytes.clone();
            for (int i = 0; i < round; i++) {
                byte t = temp[0];
                System.arraycopy(temp, 1, temp, 0, 7);
                temp[7] = t;
            }

            long mix = 0;
            for (int i = 0; i < 8; i += 2) {
                int xor = (temp[i] ^ temp[i + 1]) & 0xFF;
                int add = (temp[i] + temp[i + 1]) & 0xFF;
                mix ^= ((xor * add) ^ rotl(xor, i + 1)) ^ rotl(add, 7 - i);
            }

            A[round] = rotl(mix, round + 3) ^ (input >>> round);
        }

        //* 4x 16-bit Chunks Mix */
        long[] B = new long[4];
        for (int i = 0; i < 4; i++) {
            long seg = (input >>> (i * 16)) & 0xFFFF;
            long left = (seg >> 8) & 0xFF;
            long right = seg & 0xFF;
            long mult = (left + 1) * (right + 3);
            B[i] = rotl((left ^ right ^ mult) + i * 17, i * 3 + 1);
        }

        //* Final XOR of 32-bit halves + rotate */
        long upper = (input >>> 32) & 0xFFFFFFFFL;
        long lower = input & 0xFFFFFFFFL;
        long C = rotl((upper ^ lower ^ (upper * 3 + lower * 7)), 11);

        //* Mix A and B */
        long[] AB = new long[4];
        for (int i = 0; i < 4; i++) {
            AB[i] = (A[i] ^ A[7 - i]) + B[i] + rotl(B[i], i + 1);
        }

        //* Final Chaos Mix */
        long[] finalStage = new long[4];
        for (int i = 0; i < 4; i++) {
            finalStage[i] = AB[i] ^ rotl(C, i * 2 + 3);
        }

        //* Final calculation: max * min ^ xor(rest) */
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        long xorMid = 0;
        for (long v : finalStage) {
            max = Math.max(max, v);
            min = Math.min(min, v);
        }
        for (long v : finalStage) {
            if (v != max && v != min) xorMid ^= v;
        }

        long product = max * min;
        return product ^ xorMid;

    }

    public long bitMan_c(long x) {
        //* Manipulates bits stage - c */

        long input = (long) x;

        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) ((input >> (8 * i)) & 0xFF);
        }

        //* Chaos Mix of 8 Bytes */
        long[] A = new long[8];
        for (int round = 0; round < 8; round++) {
            byte[] temp = bytes.clone();
            for (int i = 0; i < round; i++) {
                byte t = temp[0];
                System.arraycopy(temp, 1, temp, 0, 7);
                temp[7] = t;
            }

            long mix = 0;
            for (int i = 0; i < 8; i += 2) {
                int xor = (temp[i] ^ temp[i + 1]) & 0xFF;
                int add = (temp[i] + temp[i + 1]) & 0xFF;
                mix ^= ((xor * add) ^ rotl(xor, i + 1)) ^ rotl(add, 7 - i);
            }

            A[round] = rotl(mix, round + 3) ^ (input >>> round);
        }

        //* 4x 16-bit Chunks Mix */
        long[] B = new long[4];
        for (int i = 0; i < 4; i++) {
            long seg = (input >>> (i * 16)) & 0xFFFF;
            long left = (seg >> 8) & 0xFF;
            long right = seg & 0xFF;
            long mult = (left + 1) * (right + 3);
            B[i] = rotl((left ^ right ^ mult) + i * 17, i * 3 + 1);
        }

        //* Final XOR of 32-bit halves + rotate */
        long upper = (input >>> 32) & 0xFFFFFFFFL;
        long lower = input & 0xFFFFFFFFL;
        long C = rotl((upper ^ lower ^ (upper * 3 + lower * 7)), 11);

        //* Mix A and B */
        long[] AB = new long[4];
        for (int i = 0; i < 4; i++) {
            AB[i] = (A[i] ^ A[7 - i]) + B[i] + rotl(B[i], i + 1);
        }

        //* Final Chaos Mix */
        long[] finalStage = new long[4];
        for (int i = 0; i < 4; i++) {
            finalStage[i] = AB[i] ^ rotl(C, i * 2 + 3);
        }

        //* Final calculation: max * min ^ xor(rest) */
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        long xorMid = 0;
        for (long v : finalStage) {
            max = Math.max(max, v);
            min = Math.min(min, v);
        }
        for (long v : finalStage) {
            if (v != max && v != min) xorMid ^= v;
        }

        long product = max * min;
        return product ^ xorMid;

    }

    public void setPasswd(String passwd) {
        
        this.passwd = passwd;
        this.genKey();

    }

}