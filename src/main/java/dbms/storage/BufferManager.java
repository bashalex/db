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
import java.util.HashMap;
import java.util.Map;

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
        //TODO write interaction with DiskManager and inner translation table
        System.out.println("executeQuery:");
        // full scan:
        String curPageId = "aaa:0";
        while (true) {
            if (!isBuffered(curPageId)) {
                bufferPage(curPageId);
            }
            Page page = bufferTable.get(curPageId);
//            System.out.println("page: " + page);

            queryResult.appendResults(readAllEntities(page));
            queryResult.setSchema(getSchema(page.getMetaFileName()));

            if (page.getNextPageFileName().isEmpty()) {
                break;
            }

            curPageId = String.format("%1$s:%2$d", page.getNextPageFileName(), page.getNextPageByte());
            System.out.println("next page id: " + curPageId);
        }

        return queryResult;
    }

    private Schema getSchema(String metaFileName) {
        return schemas.get(String.format("%1$s/%2$s.meta", Consts.SCHEMA_ROOT_PATH, metaFileName));
    }

    private ArrayList<Row> readAllEntities(Page page) {
        Schema schema = getSchema(page.getMetaFileName());
        ArrayList<Row> rows = new ArrayList<>();
        ByteBuffer wrapped = ByteBuffer.wrap(page.getData());
        String v = null;
        for (int i = 0; i < schema.getColumns().size(); ++i) {
            ArrayList<String> values = new ArrayList<>(schema.getColumns().size());
            int startByte = page.getPointers().get(i);
            for (Column column: schema.getColumns()) {
                System.out.println("column: " + column + ", start: " + startByte);
                switch (column.getType()) {
                    case Consts.COLUMN_TYPE_INTEGER:
                        v = String.valueOf(wrapped.getInt(startByte));
                        values.add(v);
                        break;
                    case Consts.COLUMN_TYPE_VARCHAR:
                        char[] s = new char[column.getSize() / 2];
                        for (int j = 0; j < column.getSize() / 2; ++j) {
                            s[j] = wrapped.getChar(startByte + 2 * j);
                        }
                        v = String.valueOf(s);
                        values.add(v);
                        break;
                    case Consts.COLUMN_TYPE_DATETIME:
                        v = String.valueOf(wrapped.getInt(startByte));
                        values.add(v);
                        break;
                }
                System.out.println("value: " + v);
                startByte += column.getSize();
            }

            rows.add(new Row(values));
        }
        return rows;
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
