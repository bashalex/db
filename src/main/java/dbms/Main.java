package dbms;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        int portNumber = Consts.PORT;

        if (args.length == 1) {
            portNumber = Integer.parseInt(args[0]);
        }

        Server server = new Server(portNumber);
        server.start();
//        writeTestBinaryFile();
    }

    public static void writeTestBinaryFile() throws FileNotFoundException, IOException {
        DataOutputStream os = new DataOutputStream(new FileOutputStream("/tmp/simpledb/aaa.data"));
        String metaFileName = "aaa";
        writeString(metaFileName, os, 20); // 20 bytes

        String nextPageFileName = "aaa";
        writeString(nextPageFileName, os, 20); // 20 bytes

        os.writeInt(4096); // next page start byte, 4 bytes

        // 256 bytes for pointers, max 64 entities in the block
        int numOfEntities = 3;
        os.writeInt(300);
        os.writeInt(500);
        os.writeInt(1000);

        for (int i = 0; i < 64 - numOfEntities; ++i) { // write 244 empty bytes
            os.writeInt(0);
        }
        // 300 bytes - header

        // write entities
        os.writeInt(1984);
        os.writeChars("abcde");
        os.writeInt((int) (System.nanoTime() / 1000)); // 318 is the last byte

        // offset
        for (int i = 0; i < 182; ++i) os.writeByte(0);

        os.writeInt(428);
        os.writeChars("bcdef");
        os.writeInt((int) (System.nanoTime() / 1000)); // 518 is the last byte

        // offset
        for (int i = 0; i < 482; ++i) os.writeByte(0);

        os.writeInt(1025);
        os.writeChars("hello");
        os.writeInt((int) (System.nanoTime() / 1000)); // 1018 is the last byte

        // offset
        for (int i = 0; i < 3078; ++i) os.writeByte(0);

        // second page

        writeString(metaFileName, os, 20); // 20 bytes

        nextPageFileName = "";
        writeString(nextPageFileName, os, 20); // 20 bytes

        os.writeInt(451); // next page start byte, 4 bytes

        // 256 bytes for pointers, max 64 entities in the block
        os.writeInt(4096 + 300);
        os.writeInt(4096 + 500);
        os.writeInt(4096 + 1000);

        for (int i = 0; i < 64 - numOfEntities; ++i) {
            os.writeInt(0);
        }
        // 300 bytes - header

        // write entities
        os.writeInt(1);
        os.writeChars("abcde");
        os.writeInt((int) (System.nanoTime() / 1000)); // 318 is the last byte

        // offset
        for (int i = 0; i < 182; ++i) os.writeByte(0);

        os.writeInt(2);
        os.writeChars("bcdef");
        os.writeInt((int) (System.nanoTime() / 1000)); // 518 is the last byte

        // offset
        for (int i = 0; i < 482; ++i) os.writeByte(0);

        os.writeInt(1025);
        os.writeChars("hello");
        os.writeInt((int) (System.nanoTime() / 1000)); // 1018 is the last byte

        // offset
        for (int i = 0; i < 3078; ++i) os.writeByte(0);

        os.close();
    }

    private static void writeString(String name, DataOutputStream os, int maxBytes) throws IOException{
        int shortage = maxBytes - name.length() * 2;
        for (int i = 0; i < shortage; ++i) os.writeByte(0);
        os.writeChars(name);
    }
}