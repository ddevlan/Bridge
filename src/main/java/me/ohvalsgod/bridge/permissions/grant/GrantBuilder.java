package me.ohvalsgod.bridge.permissions.grant;

import java.util.UUID;

public class GrantBuilder {

    private UUID issuerId, receiverId, permissionsGroupId;
    private long duration;
    private String reason = "No reason available.";
    private String scope = "ALL";


    public GrantBuilder(UUID issuerId) {
        this.issuerId = issuerId;
    }

    /**
     * Receiver of the grant
     *
     * @param receiverId the receiver's uuid
     * @return builder
     */
    public GrantBuilder receiver(UUID receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    /**
     * PermissionsGroup to be granted.
     *
     * @param permissionsGroupId permissionsgroup uuid
     * @return builder
     */
    public GrantBuilder permissionsGroup(UUID permissionsGroupId) {
        this.permissionsGroupId = permissionsGroupId;
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
     * The scope of a grant is which servers (or group of servers)
     * that a player would have that grant on. For instance, if you
     * used the command '/ogrant ohvals owner permanent Hubs Rank Grant description...',
     * ohvals would only have owner rank on all servers that belong to the group Hub.
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
        Grant grant = new Grant(issuerId, receiverId, permissionsGroupId, duration, reason, scope);

        grant.setCreationDate(System.currentTimeMillis());
        grant.setActive(true);

        return grant;
    }

}
