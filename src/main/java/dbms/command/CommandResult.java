package dbms.command;

import dbms.Consts;
import dbms.schema.Row;
import dbms.schema.Schema;

import java.util.ArrayList;

public class CommandResult {
    private long start;
    private int status;
    private float timeSpent;
    private ArrayList<Schema> schema = new ArrayList<Schema>();
    private ArrayList<Row> results = new ArrayList<Row>();

    public CommandResult() {
        this.status = Consts.STATUS_COMMAND_UNKNOWN;
        this.start = System.currentTimeMillis();
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getTimeSpent(){
        return this.timeSpent;
    }

    public void finishCommand() {
        this.timeSpent = (System.currentTimeMillis() - this.start) / 1000;
    }

    public String toConsoleString() {
        finishCommand();

        String result = "\nResult took " + this.timeSpent + " and " + this.results.size() + " rows returned";
        return result;
    }
}
