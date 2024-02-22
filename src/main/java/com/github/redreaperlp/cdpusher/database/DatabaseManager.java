package com.github.redreaperlp.cdpusher.database;

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
            return instance;
        }

        public  DatabaseManager(DatabaseConfiguration conf) {
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
        }

        public long getHighestID() {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) FROM cd_pusher.music");
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
                e.printStackTrace();
            }
            return null;
        }

    public void close() {
        dataSource.close();
    }
}
