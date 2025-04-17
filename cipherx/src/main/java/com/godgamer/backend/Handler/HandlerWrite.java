package com.godgamer.backend.Handler;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/*
 *
 * Goal: len should always be encrypted. 
 * Safety: len will be object's length if not passed.
 * Key should be different than global. So a local generator will be implemented here :D
 * This local key can be updated (and will be automatically done so) by a updater.
 * The user has only few options to set: String passwd (will be stored hashed), secureGenKeyManipulator x (x lies in [1e5, 2e9] ), genInf (byte[] of length passwd.length * 2)
 ! * All encryptions required are locally done.
 * 
*/

public class HandlerWrite implements AutoCloseable {
    
    FileOutputStream fos;
    DataOutputStream dos;

    public HandlerWrite(String file) {

        try {

            fos = new FileOutputStream(file);
            dos = new DataOutputStream(new BufferedOutputStream(fos));

        } catch (FileNotFoundException fnfe) {
            System.err.println("File not found");
        }

    }

    @Override
    public void close() {
        try {
            if (dos != null) dos.flush();
            dos.close(); // closes fos too
        } catch (Exception e) {
            System.err.println("Failed to close output stream");
        }
    }

    public int writeInt(int x) {

        try {
            dos.writeInt(x);
            dos.flush();
            return 0;
        } catch (Exception e) {
            return 1;
        }

    }

    public int writeBlock(Block writerBlock) {

        try {

            dos.writeInt(writerBlock.eleId);
            dos.writeLong(writerBlock.timestamp);
            dos.writeInt(writerBlock.len);
            dos.write(writerBlock.data);
            dos.flush();

            return 0;
            
        } catch (Exception e) {

            return 1;

        }

    }

    public int writeObject(byte[] data) {

        try {
            dos.write(data);
            dos.flush();
            return 0;
        } catch (Exception e) {
            return 1;
        }

    }

    public void flush() {

        try {
            dos.flush();
        } catch (Exception e) {
            return;
        }

    }

}