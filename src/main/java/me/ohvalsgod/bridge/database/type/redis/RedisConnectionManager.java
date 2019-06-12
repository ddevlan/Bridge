package me.ohvalsgod.bridge.database.type.redis;

import lombok.Getter;
import me.ohvalsgod.bridge.database.DatabaseConnectionManager;
import me.ohvalsgod.bridge.database.DatabaseCredentials;
import me.ohvalsgod.bridge.database.type.redis.group.RedisPermissionsGroupDAOImpl;
import me.ohvalsgod.bridge.database.type.redis.user.RedisPermissionsUserDAOImpl;

public class RedisConnectionManager extends DatabaseConnectionManager {

    @Getter private RedisPermissionsUserDAOImpl permissionsUserDAO;
    @Getter private RedisPermissionsGroupDAOImpl permissionsGroupDAO;

    public RedisConnectionManager(DatabaseCredentials credentials) {
        super(credentials);
    }

    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean connected() {
        return false;
    }
}
