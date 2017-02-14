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
        os.writeInt(0);
        os.writeInt(200);
        os.writeInt(700);

        for (int i = 0; i < 64 - numOfEntities; ++i) { // write 244 empty bytes
            os.writeInt(0);
        }
        // 300 bytes - header

        // write entities
        writeEntity(1984, "Hello", (int) (System.currentTimeMillis() / 1000L), os); // 318 is the last byte
        for (int i = 0; i < 182; ++i) os.writeByte(0); // offset

        writeEntity(451, "from", (int) (System.currentTimeMillis() / 1000L), os); // 518 is the last byte
        for (int i = 0; i < 482; ++i) os.writeByte(0); // offset

        writeEntity(397, "the", (int) (System.currentTimeMillis() / 1000L), os); // 1018 is the last byte
        for (int i = 0; i < 3078; ++i) os.writeByte(0); // offset

        // second page

        writeString(metaFileName, os, 20); // 20 bytes

        nextPageFileName = "";
        writeString(nextPageFileName, os, 20); // 20 bytes

        os.writeInt(451); // next page start byte, 4 bytes

        // 256 bytes for pointers, max 64 entities in the block
        os.writeInt(0);
        os.writeInt(200);
        os.writeInt(700);

        for (int i = 0; i < 64 - numOfEntities; ++i) {
            os.writeInt(0);
        }
        // 300 bytes - header

        // write entities
        writeEntity(350, "other", (int) (System.currentTimeMillis() / 1000L), os); // 318 is the last byte
        for (int i = 0; i < 182; ++i) os.writeByte(0); // offset

        writeEntity(1234, "side", (int) (System.currentTimeMillis() / 1000L), os); // 518 is the last byte
        for (int i = 0; i < 482; ++i) os.writeByte(0); // offset

        writeEntity(2017, "!", (int) (System.currentTimeMillis() / 1000L), os); // 1018 is the last byte
        for (int i = 0; i < 3078; ++i) os.writeByte(0);  // offset

        os.close();
    }

    private static void writeString(String name, DataOutputStream os, int maxBytes) throws IOException{
        int shortage = maxBytes - name.length() * 2;
        for (int i = 0; i < shortage; ++i) os.writeByte(0);
        os.writeChars(name);
    }

    private static void writeEntity(int id, String name, int timestamp, DataOutputStream os) throws IOException {
        os.writeInt(id);
        writeString(name, os, 10);
        os.writeInt(timestamp);
    }
}