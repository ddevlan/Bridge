package me.ohvalsgod.bridge.permissions.user;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface PermissionsUserDAO {

    PermissionsUser getByUniqueId(UUID uniqueId);
    PermissionsUser getByName(String name);
    PermissionsUser getByPlayer(Player player);
    void saveUser(PermissionsUser permissionsUser);
    void deleteUser(PermissionsUser permissionsUser);

    List<PermissionsUser> getAllUsers();
    List<PermissionsUser> getOnlineUsers();

}
