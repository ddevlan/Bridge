package me.ohvalsgod.bridge.database.type.redis;

import me.ohvalsgod.bridge.database.Database;
import me.ohvalsgod.bridge.database.DatabaseCredentials;
import me.ohvalsgod.bridge.database.IDatabaseConnectionManager;
import me.ohvalsgod.bridge.database.type.DatabaseType;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroupDAO;
import me.ohvalsgod.bridge.permissions.user.PermissionsUserDAO;

public class RedisDatabase extends Database {

    private int i;

    public RedisDatabase(String name, DatabaseCredentials credentials, DatabaseType type) {
        super(name, credentials, type);
    }

    @Override
    public IDatabaseConnectionManager getConnectionManager() {
        return null;
    }

    @Override
    public PermissionsGroupDAO getPermissionsGroupDAO() {
        return null;
    }

    @Override
    public PermissionsUserDAO getPermissionsUserDAO() {
        return null;
    }
}
