package dbms.storage;

public class DiskManager {
    public static final DiskManager instance = new DiskManager();

    public DiskManager getInstance() {
        return instance;
    }
}
