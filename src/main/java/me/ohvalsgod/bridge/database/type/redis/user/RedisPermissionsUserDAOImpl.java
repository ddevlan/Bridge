package me.ohvalsgod.bridge.database.type.redis.user;

import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bridge.permissions.user.PermissionsUserDAO;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class RedisPermissionsUserDAOImpl implements PermissionsUserDAO {



    @Override
    public PermissionsUser getByUniqueId(UUID uniqueId) {
        return null;
    }

    @Override
    public PermissionsUser getByPlayer(Player player) {
        return null;
    }

    @Override
    public void saveUser(PermissionsUser permissionsUser) {

    }

    @Override
    public void deleteUser(PermissionsUser permissionsUser) {

    }

    @Override
    public List<PermissionsUser> getAllUsers() {
        return null;
    }

    @Override
    public List<PermissionsUser> getOnlineUsers() {
        return null;
    }
}
