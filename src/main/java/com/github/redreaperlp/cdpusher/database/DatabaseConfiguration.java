package com.github.redreaperlp.cdpusher.database;

import com.github.redreaperlp.cdpusher.util.logger.types.InfoPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.SuccessPrinter;
import org.json.JSONObject;

import javax.xml.crypto.Data;

public class DatabaseConfiguration {

    private String host;
    private String user;
    private String password;
    private String database;
    private DatabaseManager manager;

    public DatabaseConfiguration(JSONObject json) {
        this.host = json.getString("host");
        this.user = json.getString("user");
        this.password = json.getString("password");
        this.database = json.getString("database");
    }

    public DatabaseConfiguration(String host, String user, String password, String database) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public void initDatabase() {
        new InfoPrinter().append("Initializing Database").print();
        this.manager = new DatabaseManager(this);
        new SuccessPrinter().append("Database initialized").print();
    }

    public DatabaseManager getDBManager() {
        return manager;
    }
}
