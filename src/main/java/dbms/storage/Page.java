package dbms.storage;

import dbms.Consts;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Page  {
    private String metaFileName;
    private String nextPageFileName;
    private int nextPageByte;

    private ArrayList<Integer> pointers;
    private byte[] data;

    public Page(byte[] page) {
        extractHeaders(page);
        this.data = new byte[Consts.BLOCK_SIZE - Consts.BLOCK_HEADER_SIZE];
        this.data = Arrays.copyOfRange(page, Consts.BLOCK_HEADER_SIZE, Consts.BLOCK_SIZE);
    }

    public byte[] getData() {
        return data;
    }

    public String getMetaFileName() {
        return metaFileName;
    }

    public String getNextPageFileName() {
        return nextPageFileName;
    }

    public int getNextPageByte() {
        return nextPageByte;
    }

    public ArrayList<Integer> getPointers() {
        return pointers;
    }

    private void extractHeaders(byte[] page) {
        // parse meta file name
        ByteBuffer wrapped = ByteBuffer.wrap(page);
        StringBuilder metaFileName = new StringBuilder(20);
        for (int i = 0; i < 10; ++i) metaFileName.append(wrapped.getChar());
        System.out.println("metaFileName: " + metaFileName);
        this.metaFileName = metaFileName.toString().trim();

        // parse next page file name
        StringBuilder nextPageFileName = new StringBuilder(20);
        for (int i = 0; i < 10; ++i) nextPageFileName.append(wrapped.getChar());
        System.out.println("nextPageFileName: " + nextPageFileName);
        this.nextPageFileName = nextPageFileName.toString().trim();

        // parse next page byte
        this.nextPageByte = wrapped.getInt();
        System.out.println("nextPageByte: " + nextPageByte);

        this.pointers = new ArrayList<>(64);
        for (int i = 0; i < 64; ++i) {
            int nextPointer = wrapped.getInt();
            if (i > 0 && nextPointer == 0) break;
            pointers.add(nextPointer);
        }
        pointers.trimToSize();

        System.out.println("pointers: " + pointers.toString() + ", len: " + pointers.size());
    }

    @Override
    public String toString() {
        return "Page{" +
                "metaFileName='" + metaFileName + '\'' +
                ", nextPageByte=" + nextPageByte +
                ", pointers=" + pointers +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
