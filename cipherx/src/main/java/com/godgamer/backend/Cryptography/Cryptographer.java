package com.godgamer.backend.Cryptography;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cryptographer {
    
    static String verified(String data, String algorithm) {

        /*
         * 
         * Algorithms supported:
         *  MD2
         *  MD5
         *  SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
         * 
        */

        try {

            MessageDigest md = MessageDigest.getInstance(algorithm); //* init the required algorithm */

            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8)); //* Calculate message digest and return bytes */

            BigInteger no = new BigInteger(1, digest); //* Make them signum */

            return String.format("%0" + (digest.length * 2) + "x", no);

        } catch (NoSuchAlgorithmException e) {

            return "The specified algorithm doesn't exist or is not verified.";

        }

    }

}