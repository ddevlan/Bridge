package me.ohvalsgod.bridge.permissions;

import lombok.Getter;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PermissionsHandler {

    private Map<UUID, PermissionsGroup> permissionsGroups;
    private Map<UUID, PermissionsUser> permissionsUsers;
    private UUID defaultGroupId;

    public PermissionsHandler(BridgePlugin plugin) {
        this.permissionsGroups = new HashMap<>();
        plugin.getMongo().getPermissionsGroupDAO().getAllGroups().forEach(group -> permissionsGroups.put(group.getUUID(), group));

        this.permissionsUsers = new HashMap<>();
        plugin.getMongo().getPermissionsUserDAO().getAllUsers().forEach(permissionsUser -> permissionsUsers.put(permissionsUser.getUUID(), permissionsUser));

        permissionsUsers.put(UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670"), new PermissionsUser("Console", UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670")));

        //  Find the default group, if there is none create it and save.
        if (!findDefaultGroup()) {
            PermissionsGroup group = createGroup("Default");
            group.setDefaultGroup(true);

            plugin.getMongo().getPermissionsGroupDAO().saveGroup(group);
            this.defaultGroupId = group.getUUID();
            System.out.println("Could not find a default group. Creating one.");
        } else {
            System.out.println("Default group has been found! :D");
        }
    }

    public PermissionsGroup getGroup(UUID uniqueId) {
        return permissionsGroups.get(uniqueId);
    }

    public PermissionsGroup getGroup(String name) {
        return permissionsGroups.values().stream().filter(permissionsGroup -> permissionsGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public PermissionsGroup createGroup(String name) {
        UUID uniqueId = UUID.randomUUID();
        permissionsGroups.put(uniqueId, new PermissionsGroup(name, uniqueId));

        return getGroup(uniqueId);
    }

    public boolean groupExists(String name) {
        return getGroup(name) != null;
    }

    public boolean groupExists(UUID uniqueId) {
        return getGroup(uniqueId) != null;
    }

    public PermissionsUser getUser(UUID uniqueId) {
        return permissionsUsers.get(uniqueId);
    }

    public PermissionsUser getUser(String name) {
        return permissionsUsers.values().stream().filter(permissionsUser -> permissionsUser.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public PermissionsUser createUser(Player player) {
        permissionsUsers.put(player.getUniqueId(), new PermissionsUser(player.getName(), player.getUniqueId()));
        return permissionsUsers.get(player.getUniqueId());
    }

    public PermissionsUser createUser(OfflinePlayer player) {
        permissionsUsers.put(player.getUniqueId(), new PermissionsUser(player.getName(), player.getUniqueId()));
        return permissionsUsers.get(player.getUniqueId());
    }

    public boolean userExists(UUID uniqueId) {
        return getUser(uniqueId) != null;
    }

    public boolean findDefaultGroup() {
        for (PermissionsGroup group : getPermissionsGroups().values()) {
            if (group.isDefaultGroup()) {
                this.defaultGroupId = group.getUUID();
                return true;
            }
        }
        return false;
    }

}
