package dbms.query;

import dbms.Consts;
import dbms.command.CommandResult;
import dbms.storage.BufferManager;

import java.io.IOException;

public class QueryManager {

    public CommandResult executeCommand(String query) throws IOException {
        QueryPlan queryPlan = new QueryPlan(query);
        QueryResult queryResult = BufferManager.getInstance().executeQuery(queryPlan);

        System.out.println("queryResult: " + queryResult);

        CommandResult commandResult = new CommandResult();
        commandResult.setStatus(Consts.STATUS_COMMAND_OK);
        return commandResult;
    }
}
