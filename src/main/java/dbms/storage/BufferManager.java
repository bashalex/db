package dbms.storage;

import dbms.query.QueryPlan;
import dbms.query.QueryResult;

public class BufferManager {
    public static final BufferManager instance = new BufferManager();

    public BufferManager getInstance() {
        return instance;
    }

    public QueryResult executeQuery(QueryPlan queryPlan) {
        QueryResult queryResult = new QueryResult();
        //TODO write inteaction with DiskManager and inner translation table
        return queryResult;
    }
}
