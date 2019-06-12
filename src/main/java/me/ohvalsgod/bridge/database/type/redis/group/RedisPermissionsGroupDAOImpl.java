package me.ohvalsgod.bridge.database.type.redis.group;

import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroupDAO;

import java.util.List;
import java.util.UUID;

public class RedisPermissionsGroupDAOImpl implements PermissionsGroupDAO {

    @Override
    public PermissionsGroup getByUniqueId(UUID uniqueId) {
        return null;
    }

    @Override
    public PermissionsGroup getByName(String string) {
        return null;
    }

    @Override
    public void saveGroup(PermissionsGroup permissionsGroup) {

    }

    @Override
    public void deleteGroup(PermissionsGroup permissionsGroup) {

    }

    @Override
    public List<PermissionsGroup> getAllGroups() {
        return null;
    }

}
