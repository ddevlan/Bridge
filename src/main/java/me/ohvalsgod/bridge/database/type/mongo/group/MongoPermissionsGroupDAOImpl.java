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
        PermissionsGroup permissionsGroup = findOne("uniqueId", uniqueId.toString());

        if (permissionsGroup == null) {
            return null;
        }

        return permissionsGroup;
    }

    @Override
    public PermissionsGroup getByName(String string) {
        PermissionsGroup permissionsGroup = findOne("name", string);

        if (permissionsGroup == null) {
            return null;
        }

        return permissionsGroup;
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
