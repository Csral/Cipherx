package com.godgamer.backend.Handler;

public class Block {
    
    public int eleId;
    public long timestamp;
    public byte[] data;
    public int len;

    public Block() {
        eleId = (int) Math.floor(Math.random() * 64323);
        timestamp = System.currentTimeMillis();
        len = 0;
    }

    public Block(int id, long ts, byte[] d) {
        eleId = id;
        timestamp = ts;
        data = d;
        len = 0;
    }

    public Block(int id, long ts) {
        eleId = id;
        timestamp = ts;
        len = 0;
    }

    public Block(int id, long ts, int len) {
        eleId = id;
        timestamp = ts;
        this.len = len;
        data = new byte[this.len];
    }

    public void updateData(byte[] d) {
        data = d;
    }

    public void debugPrint() {
        System.out.println("Element ID: " + this.eleId);
        System.out.println("Timestamp: " + this.timestamp);
        System.out.println("Length: " + this.len);
        System.out.println("Bytes: " + this.data);
    }

    public void debugPrint(String title) {
        System.out.println(title);
        System.out.println("Element ID: " + this.eleId);
        System.out.println("Timestamp: " + this.timestamp);
        System.out.println("Length: " + this.len);
        System.out.println("Bytes: " + this.data);
    }

}