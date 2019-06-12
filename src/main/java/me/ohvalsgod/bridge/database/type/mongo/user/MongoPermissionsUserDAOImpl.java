package me.ohvalsgod.bridge.database.type.mongo.user;

import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bridge.permissions.user.PermissionsUserDAO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MongoPermissionsUserDAOImpl extends BasicDAO<PermissionsUser, String> implements PermissionsUserDAO {

    public MongoPermissionsUserDAOImpl(Class<PermissionsUser> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    @Override
    public PermissionsUser getByUniqueId(UUID uniqueId) {
        PermissionsUser permissionsUser = findOne("uniqueId", uniqueId.toString());

        if (permissionsUser == null) {
            permissionsUser = new PermissionsUser(uniqueId);
            save(permissionsUser);
        }

        return permissionsUser;
    }

    @Override
    public PermissionsUser getByPlayer(Player player) {
        PermissionsUser permissionsUser = findOne("uniqueId", player.getUniqueId().toString());

        if (permissionsUser == null) {
            permissionsUser = new PermissionsUser(player.getName(), player.getUniqueId());
            save(permissionsUser);
        }

        return permissionsUser;
    }

    @Override
    public void saveUser(PermissionsUser permissionsUser) {
        save(permissionsUser);
    }

    @Override
    public void deleteUser(PermissionsUser permissionsUser) {
        deleteById(permissionsUser.getUniqueId());
    }

    @Override
    public List<PermissionsUser> getAllUsers() {
        return find().asList();
    }

    @Override
    public List<PermissionsUser> getOnlineUsers() {
        return getAllUsers().stream().filter(permissionsUser -> Bukkit.getPlayer(permissionsUser.getUUID()).isOnline()).collect(Collectors.toList());
    }

}
