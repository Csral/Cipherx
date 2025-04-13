package com.godgamer.backend.Handler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HandlerRead implements AutoCloseable {

    FileInputStream fis;
    DataInputStream dis;
    
    public HandlerRead(String file) {
        try {
            fis = new FileInputStream(file);
            dis = new DataInputStream(new BufferedInputStream(fis));
        } catch (FileNotFoundException fnfe) {
            System.out.println("File not found!");
        }
    }

    public int readInt() {
        
        try {

            if (dis.available() > 0) {
                return dis.readInt();
            } else return 1;

        } catch (Exception e) {
            return 1;
        }

    }

    public long readLong() {

        try {

            if (dis.available() > 0) {
                return dis.readLong();
            } else return 1;

        } catch (Exception e) {
            return 1;
        }

    }

    public Block readBlock() {
        
        try {

            if (dis.available() > 0) {
                int id = dis.readInt();
                long ts = dis.readLong();
                int len = dis.readInt(); //* Encrypted length, decrypted by user. */
                
                Block b = new Block(id, ts);
                b.len = len;

                return b;

            } else throw new ArithmeticException("Idk");

        } catch (EOFException eofe) {

            Block b = new Block(-2, -2, null);
            return b;

        } catch (Exception e) {

            Block b = new Block(-1, -1, null);
            return b;

        }

    }

    public int readByte(Block readerBlock) {

        try {

            if (dis.available() > 0) {
                
                readerBlock.data = new byte[readerBlock.len];
                dis.readFully(readerBlock.data);

                return 0;

            } else throw new ArithmeticException("Idk");

        } catch (EOFException eofe) {

            return 2;

        } catch (Exception e) {

            return 1;
            
        }

    }

    @Override
    public void close() {
        try {
            dis.close(); // closes fis too
        } catch (Exception e) {
            System.err.println("Failed to close input stream");
        }
    }

}