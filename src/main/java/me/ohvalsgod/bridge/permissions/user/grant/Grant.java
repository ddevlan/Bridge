package me.ohvalsgod.bridge.permissions.user.grant;

import lombok.Getter;
import lombok.Setter;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import org.mongodb.morphia.annotations.Embedded;

import java.util.UUID;

@Getter
@Setter
@Embedded
public class Grant {

    private String uniqueId;
    private String issuerId;
    private String permissionsGroupId;
    private String scope;
    private long addedAt;
    private String addedReason;
    private long duration;
    private String removerId;
    private long removedAt;
    private String removalReason;
    private boolean removed;

    public Grant() {

    }

    public Grant(UUID issuerId, PermissionsGroup group, String scope, String addedReason, long duration) {
        this.uniqueId = UUID.randomUUID().toString();
        this.issuerId = issuerId.toString();
        this.permissionsGroupId = group.getUniqueId();
        this.scope = scope;
        this.addedReason = addedReason;
        this.duration = duration;
        this.removed = false;
    }

    public boolean isActive() {
        return !removed && !isExpired();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - addedAt > duration;
    }

    public PermissionsGroup getPermissionsGroup() {
        return BridgePlugin.getBridgeInstance().getPermissionsHandler().getGroup(UUID.fromString(permissionsGroupId));
    }

    public UUID getIssuerUUID() {
        return UUID.fromString(issuerId);
    }

    public UUID getPermissionsGroupUUID() {
        return UUID.fromString(permissionsGroupId);
    }

}
