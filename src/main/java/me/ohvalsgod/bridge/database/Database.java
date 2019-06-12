package me.ohvalsgod.bridge.database;

import lombok.Getter;
import me.ohvalsgod.bridge.database.type.DatabaseType;

@Getter
public abstract class Database implements IDatabase {

    private String name;
    private DatabaseCredentials credentials;
    private DatabaseType type;

    protected Database(String name, DatabaseCredentials credentials, DatabaseType type) {
        this.name = name;
        this.credentials = credentials;
        this.type = type;
    }

}
