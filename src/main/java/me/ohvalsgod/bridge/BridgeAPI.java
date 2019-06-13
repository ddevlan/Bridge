package me.ohvalsgod.bridge;

import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;

import java.util.UUID;

public class BridgeAPI {

    private static BridgePlugin plugin;

    public BridgeAPI(BridgePlugin plugin) {
        BridgeAPI.plugin = plugin;
    }

    /*
        PermissionsUser methods
     */
    public PermissionsUser getUser(UUID uuid) {
        return plugin.getPermissionsHandler().getUser(uuid);
    }

    public String getFormat(UUID uuid) {
        PermissionsUser user = getUser(uuid);
        return user.getActiveGrant().getPermissionsGroup().getNameColor() + user.getName();
    }

    /*
        PermissionsGroup methods
     */
    public PermissionsGroup getGroup(String groupName) {
        return plugin.getPermissionsHandler().getGroup(groupName);
    }

    public boolean exists(String groupName) {
        return plugin.getPermissionsHandler().getGroup(groupName) != null;
    }

}
