package me.ohvalsgod.bridge.database.type.mongo;

import me.ohvalsgod.bridge.database.Database;
import me.ohvalsgod.bridge.database.DatabaseCredentials;
import me.ohvalsgod.bridge.database.IDatabaseConnectionManager;
import me.ohvalsgod.bridge.database.type.DatabaseType;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroupDAO;
import me.ohvalsgod.bridge.permissions.user.PermissionsUserDAO;

public class MongoDatabase extends Database {

    private MongoConnectionManager connectionManager;

    public MongoDatabase(String name, DatabaseCredentials credentials) {
        super(name, credentials, DatabaseType.MONGO);

        this.connectionManager = new MongoConnectionManager(credentials);
    }


    @Override
    public IDatabaseConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public PermissionsGroupDAO getPermissionsGroupDAO() {
        return connectionManager.getPermissionsGroupDAO();
    }

    @Override
    public PermissionsUserDAO getPermissionsUserDAO() {
        return connectionManager.getPermissionsUserDAO();
    }
}
