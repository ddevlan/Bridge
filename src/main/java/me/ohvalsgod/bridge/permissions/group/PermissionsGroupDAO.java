package me.ohvalsgod.bridge.permissions.group;

import java.util.List;
import java.util.UUID;

public interface PermissionsGroupDAO {

    PermissionsGroup getByUniqueId(UUID uniqueId);
    PermissionsGroup getByName(String string);
    void saveGroup(PermissionsGroup permissionsGroup);
    void deleteGroup(PermissionsGroup permissionsGroup);

    List<PermissionsGroup> getAllGroups();

}
