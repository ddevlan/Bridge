package me.ohvalsgod.bridge.permissions.user.grant;

import lombok.Getter;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;

import java.util.UUID;

@Getter
public class GrantBuilder {

    private UUID issuerId;
    private PermissionsGroup permissionsGroup;
    private long duration;
    private String reason = "No reason available.";
    private String scope;

    public GrantBuilder(UUID issuerId) {
        this.issuerId = issuerId;
    }

    /**
     * PermissionsGroup to be granted.
     *
     * @param permissionsGroup permissionsgroup uuid
     * @return builder
     */
    public GrantBuilder permissionsGroup(PermissionsGroup permissionsGroup) {
        this.permissionsGroup = permissionsGroup;
        return this;
    }

    /**
     * Reason for the grant being added.
     *
     * @param reason reason for grant
     * @return builder
     */
    public GrantBuilder reason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Duration of the grant in milliseconds.
     *
     * @param duration duration of grant
     * @return builder
     */
    public GrantBuilder duration(long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * The scope of a grant is which servers (or group of servers)
     * that a player would have that grant on. For instance, if you
     * used the command '/ogrant ohvals owner permanent Hubs Rank Grant description...',
     * ohvals would only have owner group on all servers that belong to the group Hub.
     * The same goes for specific servers.
     *
     * @param scope server scope
     * @return builder
     */
    public GrantBuilder scope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     *  This method is meant to be used when applying grants
     *  to players. So by default when this object is created,
     *  the current time of creation is the creation date.
     *
     * @return grant object
     */
    public Grant get() {
        Grant grant = new Grant(issuerId, permissionsGroup, scope, reason, duration);

        grant.setAddedAt(System.currentTimeMillis());

        return grant;
    }

}
