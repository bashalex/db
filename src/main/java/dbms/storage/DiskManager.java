package dbms.storage;

import dbms.Consts;
import dbms.schema.Column;
import dbms.schema.Schema;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiskManager {
    public static final DiskManager instance = new DiskManager();

    public static DiskManager getInstance() {
        return instance;
    }

    public Page getPage(String pageId) throws IOException {
        // pageID: "fileName:startByte"
        File file = new File(Consts.SCHEMA_ROOT_PATH, pageId.substring(0, pageId.indexOf(":")) + ".data");
        System.out.println("file: " + file.getAbsolutePath());
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        System.out.println("file opened");
        int startByte = Integer.parseInt(pageId.substring(pageId.indexOf(":") + 1));
        System.out.println("startByte: " + startByte);
        raf.seek(startByte);
        byte[] result = new byte[Consts.BLOCK_SIZE];
        raf.read(result, 0, Consts.BLOCK_SIZE);
        raf.close();
        return new Page(result);
    }

//    private List<String> readFile(String filePath) throws FileNotFoundException {
//        new BufferedReader(new FileReader(filePath))
//                .lines()
//                .map(line -> line.split(";"))
//                .map()
//                .collect(Collectors.toList());
//    }

    public Map<String, Schema> loadSchema() {
        return getAllSchemaFilePaths()
                .map(filePath -> {
                            try {
                                return new Entity(filePath, new BufferedReader(new FileReader(filePath))
                                        .lines()
                                        .map(line -> line.split(";"))
                                        .map(column -> {
                                            if (column.length == 2) {
                                                return new Column(column[0], column[1]);
                                            } else {
                                                return new Column(column[0], column[1], Integer.parseInt(column[2]));
                                            }
                                        })
                                        .collect(Collectors.toCollection(ArrayList::new)));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            return new Entity(null, new ArrayList<>());
                        })
                .filter(entity -> entity.name != null)
                .collect(Collectors.toMap(entity -> entity.name, entity -> new Schema(entity.columns, entity.name)));
    }

    private class Entity {
        String name;
        ArrayList<Column> columns;

        public Entity(String name, ArrayList<Column> columns) {
            this.name = name;
            this.columns = columns;
        }
    }

    private Stream<String> getAllSchemaFilePaths() {
        File folder = new File(Consts.SCHEMA_ROOT_PATH);
        return Arrays.stream(folder.listFiles()).filter(this::isMeta).map(File::getAbsolutePath);
    }

    private Stream<String> getAllDataFilePaths() {
        File folder = new File(Consts.SCHEMA_ROOT_PATH);
        return Arrays.stream(folder.listFiles()).filter(this::isData).map(File::getAbsolutePath);
    }

    private boolean isMeta(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1).equals("meta");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isData(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1).equals("data");
        } catch (Exception e) {
            return false;
        }
    }
}
