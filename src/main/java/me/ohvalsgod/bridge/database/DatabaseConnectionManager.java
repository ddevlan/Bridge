package me.ohvalsgod.bridge.database;

import lombok.Getter;

@Getter
public abstract class DatabaseConnectionManager implements IDatabaseConnectionManager {

    private DatabaseCredentials credentials;

    public DatabaseConnectionManager(DatabaseCredentials credentials) {
        this.credentials = credentials;
    }

}
