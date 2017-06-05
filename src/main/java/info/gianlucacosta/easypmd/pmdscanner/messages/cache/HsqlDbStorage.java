/*
 * ==========================================================================%%#
 * EasyPmd
 * ===========================================================================%%
 * Copyright (C) 2009 - 2017 Gianluca Costa
 * ===========================================================================%%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * ==========================================================================%##
 */
package info.gianlucacosta.easypmd.pmdscanner.messages.cache;

import info.gianlucacosta.easypmd.pmdscanner.ScanMessage;
import info.gianlucacosta.easypmd.util.Throwables;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Cache backed by a lightweight db
 */
public class HsqlDbStorage implements CacheStorage {

    private static final Logger logger = Logger.getLogger(HsqlDbStorage.class.getName());

    private static final String TABLE_CREATION_DDL
            = "CREATE TABLE IF NOT EXISTS CacheItems (path VARCHAR(4096) PRIMARY KEY, lastModificationMillis BIGINT NOT NULL, scanMessages OTHER NOT NULL)";

    private static final String SELECT_QUERY
            = "SELECT lastModificationMillis, scanMessages FROM CacheItems WHERE path = ?";

    private static final String INSERT_DDL
            = "INSERT INTO CacheItems (path, lastModificationMillis, scanMessages) VALUES (?, ?, ?)";

    private static final String UPDATE_DDL
            = "UPDATE CacheItems SET lastModificationMillis = ?, scanMessages = ? WHERE path = ?";

    private static final String CLEAR_DDL
            = "DELETE FROM CacheItems";

    private Connection dbConnection;

    private PreparedStatement selectStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;
    private PreparedStatement clearStatement;

    public HsqlDbStorage(Path cacheRootPath) {
        try {
            Files.createDirectories(cacheRootPath);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Path cacheDb = cacheRootPath.resolve("ScanMessages.db");

        String connectionString = String.format(
                "jdbc:hsqldb:file:%s; shutdown=true",
                cacheDb
        );

        try {
            dbConnection = DriverManager.getConnection(connectionString);

            dbConnection.setAutoCommit(true);

            try (Statement statement = dbConnection.createStatement()) {
                statement.execute(TABLE_CREATION_DDL);
            }

            selectStatement = dbConnection.prepareStatement(SELECT_QUERY);

            insertStatement = dbConnection.prepareStatement(INSERT_DDL);

            updateStatement = dbConnection.prepareStatement(UPDATE_DDL);

            clearStatement = dbConnection.prepareStatement(CLEAR_DDL);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<CacheEntry> getEntry(String pathString) {
        try {
            selectStatement.setString(1, pathString);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    long lastModificationMillis = resultSet.getLong(1);
                    Set<ScanMessage> scanMessages = (Set<ScanMessage>) resultSet.getObject(2);

                    CacheEntry cacheEntry = new CacheEntry(lastModificationMillis, scanMessages);

                    return Optional.of(cacheEntry);
                } else {
                    return Optional.empty();
                }
            }
        } catch (Exception ex) {
            logger.warning(
                    () -> String.format(
                            "Error while retrieving db-cached scan messages for path: %s.\nCause: %s",
                            pathString,
                            Throwables.toStringWithStackTrace(ex)
                    )
            );

            return Optional.empty();
        }
    }

    @Override
    public void putEntry(String pathString, CacheEntry cacheEntry) {
        try {
            updateStatement.setLong(1, cacheEntry.getLastModificationMillis());
            updateStatement.setObject(2, cacheEntry.getScanMessages());
            updateStatement.setString(3, pathString);
            if (updateStatement.executeUpdate() == 0) {
                insertStatement.setString(1, pathString);
                insertStatement.setLong(2, cacheEntry.getLastModificationMillis());
                insertStatement.setObject(3, cacheEntry.getScanMessages());

                if (insertStatement.executeUpdate() == 0) {
                    throw new IllegalStateException(
                            String.format(
                                    "Neither UPDATE nor INSERT could add messages to the cache for path: %s",
                                    pathString
                            )
                    );
                }
            }
        } catch (Exception ex) {
            logger.warning(
                    () -> String.format(
                            "Could not write to db-cache messages for path: %s.\nCause: %s",
                            pathString,
                            Throwables.toStringWithStackTrace(ex)
                    )
            );
        }
    }

    @Override
    public boolean clearEntries() {
        try {
            clearStatement.executeUpdate();
            return true;
        } catch (Exception ex) {
            logger.warning(
                    () -> String.format(
                            "Error while clearing the db cache: %s.\nCause: %s",
                            ex,
                            Throwables.toStringWithStackTrace(ex)
                    )
            );

            return false;
        }
    }

    @Override
    public void close() throws Exception {
        dbConnection.close();
    }
}
