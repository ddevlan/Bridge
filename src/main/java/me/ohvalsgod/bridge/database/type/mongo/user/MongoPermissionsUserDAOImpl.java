package me.ohvalsgod.bridge.database.type.mongo.user;

import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import me.ohvalsgod.bridge.permissions.user.PermissionsUserDAO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoPermissionsUserDAOImpl extends BasicDAO<PermissionsUser, String> implements PermissionsUserDAO {

    public MongoPermissionsUserDAOImpl(Class<PermissionsUser> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    @Override
    public PermissionsUser getByUniqueId(UUID uniqueId) {
        return findOne("uniqueId", uniqueId.toString());
    }

    @Override
    public PermissionsUser getByName(String name) {
        return findOne("name", name);
    }

    @Override
    public PermissionsUser getByPlayer(Player player) {
        return getByUniqueId(player.getUniqueId());
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
        List<PermissionsUser> online = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(o -> online.add(getByPlayer(o)));
        return online;
    }

}
