package me.ohvalsgod.bridge;

import lombok.Getter;
import me.ohvalsgod.bridge.database.Database;
import me.ohvalsgod.bridge.database.DatabaseHelper;
import me.ohvalsgod.bridge.database.type.DatabaseType;
import me.ohvalsgod.bridge.permissions.PermissionListener;
import me.ohvalsgod.bridge.permissions.PermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BridgePlugin extends JavaPlugin {

    @Getter protected static BridgePlugin bridgeInstance;

    private PermissionsHandler permissionsHandler;
    private Database mongo;
    private DatabaseHelper databaseHelper;

    @Override
    public void onEnable() {
        bridgeInstance = this;

        databaseHelper = new DatabaseHelper();

        mongo = databaseHelper.createDatabase("bridge-mongo", DatabaseType.MONGO, getConfig());
        mongo.getConnectionManager().connect();

        if (!mongo.getConnectionManager().connected()) {
            getLogger().severe("ERROR: Could not connect to database '" + mongo.getName() + "' with type '" + mongo.getType().name().toLowerCase() + "'. Shutting down.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        permissionsHandler = new PermissionsHandler();

        new PermissionListener(this);
    }

    @Override
    public void onDisable() {
        bridgeInstance = null;
    }

}
