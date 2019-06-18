package me.ohvalsgod.bridge.permissions.user;

import io.github.thatkawaiisam.redstone.bukkit.RedstoneBukkitAPI;
import lombok.Getter;
import lombok.Setter;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.PermissionsHolder;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bridge.permissions.user.grant.Grant;
import me.ohvalsgod.bridge.permissions.user.grant.GrantBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.mongodb.morphia.annotations.*;

import java.util.*;

@Getter
@Setter
@Entity(value = "users", noClassnameStored = true)
@Indexes({
        @Index(fields = @Field("uniqueId"))
})
public class PermissionsUser implements PermissionsHolder {

    @Id
    private String uniqueId;

    private String name;

    @Embedded
    private List<Grant> grants = new ArrayList<>();

    private Set<String> permissions = new HashSet<>();

    @Embedded
    private Grant activeGrant = null;

    private transient PermissionAttachment attachment;

    public PermissionsUser() {
        //  Empty constructor for morphia.
    }

    public PermissionsUser(String name, UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId.toString();
    }

    public UUID getUUID() {
        return UUID.fromString(uniqueId);
    }

    @Override
    public Set<String> getPermissions() {
        return permissions;
    }

    public Grant getActiveGrant() {
        double lastWeight = -1;
        for (Grant grant : grants) {
            if (grant.isActive()) {
                if (grant.getScope().equalsIgnoreCase(RedstoneBukkitAPI.getCurrentServerName()) || grant.getScope().equalsIgnoreCase("all") || grant.getScope().equalsIgnoreCase("global")) {
                    if (grant.getPermissionsGroup().getWeight() > lastWeight) {
                        this.activeGrant = grant;
                        lastWeight = grant.getPermissionsGroup().getWeight();
                    }
                }
            }
        }

        if (activeGrant == null) {
            //TODO: don't create a new one, just grab the default grant from permission handler class
            this.activeGrant = new GrantBuilder(UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670"))
                    .permissionsGroup(BridgePlugin.getBridgeInstance().getPermissionsHandler().getGroup(BridgePlugin.getBridgeInstance().getPermissionsHandler().getDefaultGroupId()))
                    .duration(Integer.MAX_VALUE)
                    .reason("Default group granted.")
                    .scope("Global")
                    .get();

            grants.add(activeGrant);
        }
        return activeGrant;
    }

    @Override
    public boolean update() {
        //  Make sure the player is online
        if (Bukkit.getPlayer(getUUID()) != null) {
            updateDisplayName();
            updatePermissions();
            return true;
        }
        BridgePlugin.getBridgeInstance().getLogger().warning("Attempted to update player permissions when they were offline");
        return false;
    }

    private void updatePermissions() {
        //  Now let's get the permission attachment
        Player player = toPlayer();
        this.attachment = this.attachment != null ? this.attachment : player.addAttachment(BridgePlugin.getBridgeInstance());

        //  Unset all permissions
        for (String permission : attachment.getPermissions().keySet()) {
            attachment.unsetPermission(permission);
        }

        //  Set all local permissions
        for (String permission : permissions) {
            attachment.setPermission(permission.replace("-", ""), !permission.startsWith("-"));
        }

        //  Set all permissions through active grants
        for (Grant pgrant : grants) {
            if (!pgrant.isActive()) {
                continue;
            }

            if (!pgrant.getScope().equalsIgnoreCase(RedstoneBukkitAPI.getCurrentServerName()) && !pgrant.getScope().equalsIgnoreCase("ALL")) {
                continue;
            }

            if (pgrant.getPermissionsGroup() == null) {
                continue;
            }

            PermissionsGroup localGroup = pgrant.getPermissionsGroup();

            for (String permission : localGroup.getPermissions()) {
                attachment.setPermission(permission.replace("-", ""), !permission.startsWith("-"));
            }

            for (String uniqueId : localGroup.getInherits()) {
                if (BridgePlugin.getBridgeInstance().getPermissionsHandler().getGroup(UUID.fromString(uniqueId)) == null) {
                    continue;
                }

                PermissionsGroup inheritGroup = BridgePlugin.getBridgeInstance().getPermissionsHandler().getGroup(UUID.fromString(uniqueId));
                for (String permission : inheritGroup.getPermissions()) {
                    attachment.setPermission(permission.replace("-", ""), !permission.startsWith("-"));
                }
            }

        }

        player.recalculatePermissions();
    }

    private void updateDisplayName() {
        //  Grab their active grant to set their displayname
        Player player = toPlayer();
        PermissionsGroup nameGroup = getActiveGrant().getPermissionsGroup();
        player.setDisplayName(nameGroup.getColoredName(player.getName()));
    }

    public String getDisplayName() {
        return getActiveGrant().getPermissionsGroup().getFormattedName(name);
    }

    public String getColoredName() {
        return getActiveGrant().getPermissionsGroup().getColoredName(name);
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

}
