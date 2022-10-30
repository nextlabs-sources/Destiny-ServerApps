package com.nextlabs.destiny.cc.installer.dto;

public class SecureStore {

    private String name;

    private byte[] storeFile;

    public SecureStore(String name, byte[] storeFile) {
        super();
        this.name = name;
        this.storeFile = storeFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getStoreFile() {
        return storeFile;
    }

    public void setStoreFile(byte[] storeFile) {
        this.storeFile = storeFile;
    }
}
