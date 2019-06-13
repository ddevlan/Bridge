package me.ohvalsgod.bridge;

import lombok.Getter;
import me.ohvalsgod.bridge.command.parameters.PermissionsGroupParameter;
import me.ohvalsgod.bridge.command.parameters.PermissionsUserParameter;
import me.ohvalsgod.bridge.database.Database;
import me.ohvalsgod.bridge.database.DatabaseHelper;
import me.ohvalsgod.bridge.database.type.DatabaseType;
import me.ohvalsgod.bridge.permissions.PermissionListener;
import me.ohvalsgod.bridge.permissions.PermissionsHandler;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bukkitlib.command.CommandHandler;
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

        saveDefaultConfig();

        //  Connect to mongo
        databaseHelper = new DatabaseHelper();

        mongo = databaseHelper.createDatabase("bridge-mongo", DatabaseType.MONGO, getConfig());
        mongo.getConnectionManager().connect();

        if (!mongo.getConnectionManager().connected()) {
            getLogger().severe("ERROR: Could not connect to database '" + mongo.getName() + "' with type '" + mongo.getType().name().toLowerCase() + "'. Shutting down.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            mongo.getConnectionManager().close();
            return;
        }

        permissionsHandler = new PermissionsHandler(this);

        //  Listeners
        new PermissionListener(this);

        //  Commands
        CommandHandler.registerParameterType(PermissionsGroup.class, new PermissionsGroupParameter());
        CommandHandler.registerParameterType(PermissionsUser.class, new PermissionsUserParameter());
        CommandHandler.loadCommandsFromPackage(this, "me.ohvalsgod.bridge.command.commands");
    }

    @Override
    public void onDisable() {
        for (PermissionsUser user : mongo.getPermissionsUserDAO().getAllUsers()) {
            mongo.getPermissionsUserDAO().saveUser(user);
        }

        for (PermissionsGroup group : permissionsHandler.getPermissionsGroups().values()) {
            mongo.getPermissionsGroupDAO().saveGroup(group);
        }

        if (mongo.getConnectionManager().connected()) {
            mongo.getConnectionManager().close();
        }
        bridgeInstance = null;
    }

}
