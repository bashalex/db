package dbms.query;

import dbms.Consts;
import dbms.command.CommandResult;

public class QueryManager {

    public CommandResult executeCommand(String query) {
        CommandResult commandResult = new CommandResult();
        commandResult.setStatus(Consts.STATUS_COMMAND_OK);
        return commandResult;
    }
}
