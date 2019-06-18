package me.ohvalsgod.bridge.database.type.mongo.group;


import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroupDAO;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.List;
import java.util.UUID;

public class MongoPermissionsGroupDAOImpl extends BasicDAO<PermissionsGroup, String> implements PermissionsGroupDAO {


    public MongoPermissionsGroupDAOImpl(Class<PermissionsGroup> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    @Override
    public PermissionsGroup getByUniqueId(UUID uniqueId) {
        return findOne("uniqueId", uniqueId.toString());
    }

    @Override
    public PermissionsGroup getByName(String name) {
        return findOne("name", name);
    }

    @Override
    public void saveGroup(PermissionsGroup permissionsGroup) {
        save(permissionsGroup);
    }

    @Override
    public void deleteGroup(PermissionsGroup permissionsGroup) {
        delete(permissionsGroup);
    }

    @Override
    public List<PermissionsGroup> getAllGroups() {
        return find().asList();
    }

}
