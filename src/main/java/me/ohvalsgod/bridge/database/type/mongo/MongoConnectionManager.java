package me.ohvalsgod.bridge.database.type.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import me.ohvalsgod.bridge.database.DatabaseConnectionManager;
import me.ohvalsgod.bridge.database.DatabaseCredentials;
import me.ohvalsgod.bridge.database.type.mongo.group.MongoPermissionsGroupDAOImpl;
import me.ohvalsgod.bridge.database.type.mongo.user.MongoPermissionsUserDAOImpl;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.PermissionsUser;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.ArrayList;
import java.util.List;

public class MongoConnectionManager extends DatabaseConnectionManager {
    
    private MongoClient mongoClient;
    private Morphia morphia;
    private Datastore bridgeStore;

    private MongoPermissionsUserDAOImpl permissionsUserDAO;
     private MongoPermissionsGroupDAOImpl permissionsGroupDAO;

    public MongoConnectionManager(DatabaseCredentials credentials) {
        super(credentials);
    }

    @Override
    public boolean connect() {
        try {
            //  By default, we should be authenticating with username and password for mongo.
            if (getCredentials().shouldAuthenticateWithUsernameAndPassword()) {
                ServerAddress address = new ServerAddress(getCredentials().getHost(), getCredentials().getPort());
                List<MongoCredential> credentialList = new ArrayList<>();
                credentialList.add(MongoCredential.createCredential(getCredentials().getUsername(), getCredentials().getDatabase(), getCredentials().getPassword().toCharArray()));

                this.mongoClient = new MongoClient(address, credentialList);
                this.morphia = new Morphia();
            } else {    //  But if we can't, then we will just attempt to use no authentication.
                ServerAddress address = new ServerAddress(getCredentials().getHost(), getCredentials().getPort());

                this.mongoClient = new MongoClient(address);
                this.morphia = new Morphia();
            }

            //  Morphia will handle all of the mapping for us, making saving and fetching significantly easier
            this.bridgeStore = morphia.createDatastore(mongoClient, "bridge");

            this.morphia.map(PermissionsUser.class);

            this.morphia.map(PermissionsGroup.class);

            this.bridgeStore.ensureIndexes();

            this.permissionsUserDAO = new MongoPermissionsUserDAOImpl(PermissionsUser.class, bridgeStore);
            this.permissionsGroupDAO = new MongoPermissionsGroupDAOImpl(PermissionsGroup.class, bridgeStore);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;   //  And if something bad happens, we just close the server.
        }
    }

    @Override
    public void close() {
        this.mongoClient.close();
        this.morphia = null;
    }

    @Override
    public boolean connected() {
        try {
            mongoClient.getAddress();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public MongoPermissionsGroupDAOImpl getPermissionsGroupDAO() {
        return permissionsGroupDAO;
    }

    public MongoPermissionsUserDAOImpl getPermissionsUserDAO() {
        return permissionsUserDAO;
    }
}
