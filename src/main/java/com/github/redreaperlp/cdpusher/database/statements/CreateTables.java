package com.github.redreaperlp.cdpusher.database.statements;

import com.github.redreaperlp.cdpusher.database.DatabaseManager;

public enum CreateTables {
    CREATE_DISCS("""
            CREATE TABLE IF NOT EXISTS discs
            (
                DiscId      BIGINT       NOT NULL AUTO_INCREMENT,
                Title       VARCHAR(255) NULL,
                Label       VARCHAR(255) NULL,
                Country     VARCHAR(255) NULL,
                ResourceUrl VARCHAR(255) NULL,
                Year        INT,
                PRIMARY KEY (DiscId)
            );
            """),

    CREATE_SONGS("""
            CREATE TABLE IF NOT EXISTS songs
            (
                SongId           BIGINT       NOT NULL AUTO_INCREMENT,
                DiscId           BIGINT       NOT NULL,
                Title            VARCHAR(255) NULL,
                Album            VARCHAR(255) NULL,
                Artist           VARCHAR(255) NULL,
                TrackNumber      BIGINT,
                CdNumber         INT          NULL,
                SpotifyMismatch BOOL DEFAULT FALSE,
                SpotifySearch    BOOL DEFAULT FALSE,
                ImageUri         VARCHAR(255) NULL,
                Duration         BIGINT,
                Year             INT          NULL,
                FOREIGN KEY (DiscId) REFERENCES discs (DiscId),
                PRIMARY KEY (SongId)
            );
            """);

    private final String statement;

    CreateTables(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    public static void CreateIfNotExists() {
        try (var con = DatabaseManager.getInstance().getConnection()) {
            for (CreateTables table : CreateTables.values()) {
                var ps = con.prepareStatement(table.getStatement());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
