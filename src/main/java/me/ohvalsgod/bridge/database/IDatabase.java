package me.ohvalsgod.bridge.database;

import me.ohvalsgod.bridge.permissions.group.PermissionsGroupDAO;
import me.ohvalsgod.bridge.permissions.user.PermissionsUserDAO;

public interface IDatabase {

    IDatabaseConnectionManager getConnectionManager();
    PermissionsGroupDAO getPermissionsGroupDAO();
    PermissionsUserDAO getPermissionsUserDAO();

}
