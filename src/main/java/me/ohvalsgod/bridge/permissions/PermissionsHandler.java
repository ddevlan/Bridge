package me.ohvalsgod.bridge.permissions;

import lombok.Getter;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PermissionsHandler {

    private Map<UUID, PermissionsGroup> permissionsGroups;
    private Map<UUID, PermissionsUser> permissionsUsers;

    public PermissionsHandler() {
        this.permissionsGroups = new HashMap<>();
        this.permissionsUsers = new HashMap<>();
    }

    public PermissionsGroup getGroup(UUID uniqueId) {
        return permissionsGroups.get(uniqueId);
    }

    public PermissionsGroup getGroup(String name) {
        return permissionsGroups.values().stream().filter(permissionsGroup -> permissionsGroup.getName().equals(name)).findFirst().orElse(null);
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

    public PermissionsUser createUser(UUID uniqueId) {
        permissionsUsers.put(uniqueId, new PermissionsUser(uniqueId));
        return permissionsUsers.get(uniqueId);
    }

    public boolean userExists(UUID uniqueId) {
        return getUser(uniqueId) != null;
    }

    public UUID getDefaultGroupId() {
        UUID toReturn = null;
        double lastWeight = -1;
        for (Map.Entry<UUID, PermissionsGroup> entry : permissionsGroups.entrySet()) {
            if (entry.getValue().isDefaultGroup()) {
                if (toReturn != null) {
                    BridgePlugin.getBridgeInstance().getLogger().warning("More than one default group found.");

                    if (lastWeight > entry.getValue().getWeight()) {
                        continue;
                    }
                }

                toReturn = entry.getKey();
                lastWeight = entry.getValue().getWeight();
            }
        }
        return toReturn;
    }

}
