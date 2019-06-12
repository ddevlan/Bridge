package me.ohvalsgod.bridge.database;

import me.ohvalsgod.bridge.database.type.DatabaseType;
import me.ohvalsgod.bridge.database.type.mongo.MongoDatabase;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {

    private Map<String, Database> databases;

    public DatabaseHelper() {
        databases = new HashMap<>();
    }

    public Database createDatabase(String name, DatabaseType type, FileConfiguration credentialsConfig) {
        Database database;
        if (type == DatabaseType.MONGO) {
            database = new MongoDatabase(name, DatabaseCredentials.fromConfig(credentialsConfig, type));
        } else {
            database = null;
        }

        if (database != null) {
            databases.put(name, database);
        }

        return database;
    }

    public Database getDatabase(String name) {
        return databases.get(name);
    }

}
