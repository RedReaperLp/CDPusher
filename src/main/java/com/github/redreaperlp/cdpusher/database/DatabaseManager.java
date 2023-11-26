package com.github.redreaperlp.cdpusher.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class DatabaseManager {

        HikariDataSource dataSource;

        private static DatabaseManager instance;

        public static DatabaseManager getInstance() {
            return instance;
        }

        public DatabaseManager(DatabaseConfiguration conf) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:mysql://" + conf.getHost() + "/" + conf.getDatabase());
            hikariConfig.setUsername(conf.getUser());
            hikariConfig.setPassword(conf.getPassword());
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource = new HikariDataSource(hikariConfig);
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
