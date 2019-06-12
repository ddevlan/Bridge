package me.ohvalsgod.bridge.permissions.user;

import io.github.thatkawaiisam.redstone.bukkit.RedstoneBukkitAPI;
import lombok.Getter;
import lombok.Setter;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.PermissionsHolder;
import me.ohvalsgod.bridge.permissions.grant.Grant;
import me.ohvalsgod.bridge.permissions.grant.GrantBuilder;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.mongodb.morphia.annotations.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Entity(value = "users", noClassnameStored = true)
public class PermissionsUser implements PermissionsHolder {

    @Id
    @Indexed(options = @IndexOptions(unique = true))
    private String uniqueId;

    @Setter
    private String name;

    @Embedded
    private Set<Grant> grants;

    private Set<String> permissions;

    @Transient
    private transient PermissionAttachment attachment;

    public PermissionsUser() {
        //  Empty constructor for morphia.
    }

    public PermissionsUser(String name, UUID uniqueId) {
        this(uniqueId);
        this.name = name;
    }

    public PermissionsUser(UUID uniqueId) {
        this.uniqueId = uniqueId.toString();
        this.grants = new HashSet<>();
        this.permissions = new HashSet<>();
    }

    private Grant getActiveGrant() {
        for (Grant grant : grants
                .stream()
                .min(Comparator.comparingDouble(value -> BridgePlugin.getBridgeInstance().getPermissionsHandler().getGroup(value.getPermissionsGroupUUID()).getWeight())).stream().collect(Collectors.toList())) {
            if (grant.isActive()) {
                if (grant.getScope().equalsIgnoreCase(RedstoneBukkitAPI.getCurrentServerName()) || grant.getScope().equalsIgnoreCase("ALL")) {
                    return grant;
                }
            }
        }
        return new GrantBuilder(null)
                        .receiver(getUUID())
                        .permissionsGroup(BridgePlugin.getBridgeInstance().getPermissionsHandler().getDefaultGroupId())
                        .duration(2147483647L)
                        .reason("Default group granted.")
                        .scope("ALL")
                        .get();
    }

    public UUID getUUID() {
        return UUID.fromString(uniqueId);
    }

    @Override
    public Set<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean update() {
        //  Make sure the player is online
        if (Bukkit.getPlayer(getUUID()) != null) {
            //  Now let's get the permission attachment
            Player player = Bukkit.getPlayer(getUUID());
            this.attachment = this.attachment != null ? this.attachment : player.addAttachment(BridgePlugin.getBridgeInstance());

            //  Grab their active grant to set their displayname
            PermissionsGroup nameGroup = getActiveGrant().getPermissionsGroup();
            player.setDisplayName(nameGroup.getFormattedName(player.getName()));

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
            return true;
        }
        BridgePlugin.getBridgeInstance().getLogger().warning("Attempted to update player permissions when they were offline");
        return false;
    }

}
