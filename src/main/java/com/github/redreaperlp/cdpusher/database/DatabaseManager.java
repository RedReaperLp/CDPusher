package com.github.redreaperlp.cdpusher.database;

import com.github.redreaperlp.cdpusher.database.statements.CreateTables;
import com.github.redreaperlp.cdpusher.util.logger.types.ErrorPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.InfoPrinter;
import com.github.redreaperlp.cdpusher.util.logger.types.MessagePart;
import com.github.redreaperlp.cdpusher.util.logger.types.SuccessPrinter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DatabaseManager {

    HikariDataSource dataSource;

    private static DatabaseManager instance;

    public static DatabaseManager getInstance() {
        if (instance == null) {
            new DatabaseConfiguration("45.81.235.52", "cdpusher", "cdpusher", "cdpusher").initDatabase();
        }
        return instance;
    }

    public DatabaseManager(DatabaseConfiguration conf) {
        instance = this;
        new InfoPrinter(new MessagePart("[Database Manager]")).append("Initializing Database Manager...").print();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + conf.getHost() + "/" + conf.getDatabase());
        hikariConfig.setUsername(conf.getUser());
        hikariConfig.setPassword(conf.getPassword());
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource = new HikariDataSource(hikariConfig);
        new SuccessPrinter(new MessagePart("[Database Manager]")).append("Database Manager initialized").print();

        CreateTables.CreateIfNotExists();
    }

    public long getHighestSongID() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) FROM cdpusher.songs");
            if (statement.execute()) {
                statement.getResultSet().next();
                return statement.getResultSet().getLong(1);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            new ErrorPrinter().appendException(e).print();
        }
        return null;
    }

    public void close() {
        dataSource.close();
    }

    public long getHighestDiscID() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) FROM cdpusher.discs");
            if (statement.execute()) {
                statement.getResultSet().next();
                return statement.getResultSet().getLong(1);
            }
            return 0;
        } catch (Exception e) {
            new ErrorPrinter().appendException(e).print();
        }
        return 0;
    }
}
