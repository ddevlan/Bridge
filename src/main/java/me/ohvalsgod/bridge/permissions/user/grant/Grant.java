package me.ohvalsgod.bridge.permissions.user.grant;

import lombok.Getter;
import lombok.Setter;
import me.ohvalsgod.bridge.BridgePlugin;
import me.ohvalsgod.bridge.permissions.group.PermissionsGroup;
import me.ohvalsgod.bukkitlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mongodb.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        //  Morphia constructor
    }

    public Grant(UUID issuerId, PermissionsGroup group, String scope, String addedReason, long duration) {
        this.uniqueId = UUID.randomUUID().toString();
        this.issuerId = (issuerId == null ? "f78a4d8d-d51b-4b39-98a3-230f2de0c670":issuerId.toString());
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

    public void show(Player player) {
        List<String> toShow = new ArrayList<>();

        toShow.add("&7&m----------------------------------");
        toShow.add(String.format("&eUnique ID: &f%s", uniqueId));
        toShow.add(String.format("&eIssuer ID: &f%s &7(%s&7)", issuerId, (BridgePlugin.getBridgeInstance().getPermissionsHandler().getUser(UUID.fromString(issuerId)) == null ? "&4Console":BridgePlugin.getBridgeInstance().getPermissionsHandler().getUser(UUID.fromString(issuerId)).getName())));
        toShow.add(String.format("&ePermission Group: &f%s", BridgePlugin.getBridgeInstance().getPermissionsHandler().getGroup(UUID.fromString(permissionsGroupId)).getFancyName()));
        toShow.add(String.format("&eScope: &f%s", scope));
        toShow.add(String.format("&eTime Added: &f%s", TimeUtils.formatIntoCalendarString(new Date(addedAt))));
        toShow.add(String.format("&eReason Added: &f%s", addedReason));
        toShow.add(String.format("&eExpiration Date: &f%s", (isPermanent() ? "Never":TimeUtils.formatIntoCalendarString(new Date(addedAt + duration)))));

        if (removed) {
            toShow.add(" ");
            toShow.add(String.format("&eRemover ID: &f%s &7(%s&7)", removerId, BridgePlugin.getBridgeInstance().getPermissionsHandler().getUser(UUID.fromString(removerId)).getName()));
            toShow.add(String.format("&eTime Removed: &f%s", TimeUtils.formatIntoCalendarString(new Date(removedAt))));
            toShow.add(String.format("&eReason Removed: &f%s", removalReason));
        }
        toShow.add("&7&m----------------------------------");

        toShow.forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
    }

    public boolean isPermanent() {
        return duration == Integer.MAX_VALUE;
    }

}
