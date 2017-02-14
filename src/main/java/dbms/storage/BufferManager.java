package dbms.storage;

import dbms.Consts;
import dbms.query.QueryPlan;
import dbms.query.QueryResult;
import dbms.schema.Column;
import dbms.schema.Row;
import dbms.schema.Schema;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BufferManager {
    private static final BufferManager instance = new BufferManager();
    private HashMap<String, Page> bufferTable = new HashMap<>();
    private DiskManager diskManager = DiskManager.getInstance();
    private Map<String, Schema> schemas;

    private BufferManager() {

    }

    public static BufferManager getInstance() {
        return instance;
    }

    public QueryResult executeQuery(QueryPlan queryPlan) throws IOException {
        QueryResult queryResult = new QueryResult();
        System.out.println("executeQuery:");
        // full scan:
        String curPageId = "aaa:0";
        while (true) {
            if (!isBuffered(curPageId)) {
                bufferPage(curPageId);
            }
            Page page = bufferTable.get(curPageId);

            queryResult.appendResults(readAllEntities(page));
            queryResult.setSchema(getSchema(page.getMetaFileName()));

            if (page.getNextPageFileName().isEmpty()) {
                break;
            }

            curPageId = String.format("%1$s:%2$d", page.getNextPageFileName(), page.getNextPageByte());
        }

        return queryResult;
    }

    private Schema getSchema(String metaFileName) {
        return schemas.get(String.format("%1$s/%2$s.meta", Consts.SCHEMA_ROOT_PATH, metaFileName));
    }

    private ArrayList<Row> readAllEntities(Page page) {
        Schema schema = getSchema(page.getMetaFileName());
        ByteBuffer wrapped = ByteBuffer.wrap(page.getData());

        return page.getPointers().stream()
                .map(startByte -> {
                    final int[] curByte = {startByte};
                    return schema.getColumns().stream().map(column -> {
                        switch (column.getType()) {
                            case Consts.COLUMN_TYPE_INTEGER:
                                curByte[0] += column.getSize();
                                return String.valueOf(wrapped.getInt(curByte[0]));
                            case Consts.COLUMN_TYPE_VARCHAR:
                                char[] s = new char[column.getSize() / 2];
                                for (int j = 0; j < column.getSize() / 2; ++j) {
                                    s[j] = wrapped.getChar(curByte[0] + 2 * j);
                                }
                                curByte[0] += column.getSize();
                                return String.valueOf(s).trim();
                            case Consts.COLUMN_TYPE_DATETIME:
                                Date date = new Date();
                                date.setTime(wrapped.getInt(curByte[0]) * 1000L);
                                curByte[0] += column.getSize();
                                return date.toString();
                        }
                        return null;
                    }).filter(s -> s != null).collect(Collectors.toCollection(ArrayList::new));
                }).map(Row::new).collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean isBuffered(String pageId) {
        return this.bufferTable.containsKey(pageId);
    }

    public void bufferPage(String pageId) throws IOException {
        this.bufferTable.put(pageId, diskManager.getPage(pageId));
    }

    public void loadSchemas() {
        this.schemas = diskManager.loadSchema();

        System.out.println(schemas.toString());
    }
}
