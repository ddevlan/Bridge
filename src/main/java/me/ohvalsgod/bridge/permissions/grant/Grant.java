package me.ohvalsgod.bridge.permissions.grant;

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

    private String issuerId, receiverId, permissionsGroupId;
    private long duration, creationDate;
    private String reason;
    private String scope;
    private boolean active;

    public Grant(UUID issuerId, UUID receiverId, UUID permissionsGroupId, long duration, String reason, String scope) {
        this.issuerId = issuerId.toString();
        this.receiverId = receiverId.toString();
        this.permissionsGroupId = permissionsGroupId.toString();
        this.duration = duration;
        this.reason = reason;
        this.scope = scope;
        this.active = false;
    }

    public boolean isActive() {
        return active && isExpired();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationDate > duration;
    }

    public PermissionsGroup getPermissionsGroup() {
        return BridgePlugin.getBridgeInstance().getPermissionsHandler().getGroup(permissionsGroupId);
    }

    public UUID getIssuerUUID() {
        return UUID.fromString(issuerId);
    }

    public UUID getReceiverUUID() {
        return UUID.fromString(receiverId);
    }

    public UUID getPermissionsGroupUUID() {
        return UUID.fromString(permissionsGroupId);
    }

}
