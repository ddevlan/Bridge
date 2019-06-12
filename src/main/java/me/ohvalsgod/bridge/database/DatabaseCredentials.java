package me.ohvalsgod.bridge.database;

import lombok.Getter;
import me.ohvalsgod.bridge.database.type.DatabaseType;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class DatabaseCredentials {

    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

    public DatabaseCredentials(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    public DatabaseCredentials(String host, int port, String database, String password) {
        this(host, port, database);
        this.password = password;
    }

    public DatabaseCredentials(String host, int port, String database, String username, String password) {
        this(host, port, database, password);
        this.username = username;
    }

    public boolean shouldAuthenticate() {
        return password != null && !password.isEmpty();
    }

    public boolean shouldAuthenticateWithUsernameAndPassword() {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }

    public static DatabaseCredentials fromConfig(FileConfiguration configuration, DatabaseType type) {

        if (!configuration.isConfigurationSection(type.name().toLowerCase())) {
            return null;
        }

        return new DatabaseCredentials(
                configuration.getString(type.name().toLowerCase() + ".host"),
                configuration.getInt(type.name().toLowerCase() + ".port"),
                configuration.getString(type.name().toLowerCase() + ".database"),
                configuration.getString(type.name().toLowerCase() + ".username"),
                configuration.getString(type.name().toLowerCase() + ".password")
        );
    }

}
