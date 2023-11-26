package com.github.redreaperlp.cdpusher.database;

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
        this.manager = new DatabaseManager(this);
    }

    public DatabaseManager getDBManager() {
        return manager;
    }
}
