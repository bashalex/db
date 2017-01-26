package dbms.storage;

import dbms.query.QueryPlan;
import dbms.query.QueryResult;

public class BufferManager {
    public QueryResult executeQuery(QueryPlan queryPlan) {
        QueryResult queryResult = new QueryResult();
        //TODO write inteaction with DiskManager and inner translation table
        return queryResult;
    }
}
