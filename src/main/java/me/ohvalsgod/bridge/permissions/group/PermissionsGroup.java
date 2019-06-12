package me.ohvalsgod.bridge.permissions.group;

import lombok.Getter;
import lombok.Setter;
import me.ohvalsgod.bridge.permissions.PermissionsHolder;
import org.bukkit.ChatColor;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(value = "groups", noClassnameStored = true)
public class PermissionsGroup implements PermissionsHolder {

    @Id
    @Indexed(options = @IndexOptions(unique = true))
    private String uniqueId;

    private String name;

    private String prefix = "", suffix = "", nameColor = "";

    private boolean showPrefix = false, showSuffix = false;

    private double weight = 0;

    private Set<String> permissions;

    private Set<String> inherits;

    private boolean defaultGroup = false;

    public PermissionsGroup() {
        //  Empty constructor for morphia.
    }

    public PermissionsGroup(String name) {
        this.name = name;
        this.permissions = new HashSet<>();
        this.inherits = new HashSet<>();
    }

    public PermissionsGroup(String name, UUID uniqueId) {
        this(name);
        this.uniqueId = uniqueId.toString();
    }

    @Override
    public Set<String> getPermissions() {
        return permissions;
    }

    public void addInherit(PermissionsGroup group) {
        addInherit(group.getUUID());
    }

    public void removeInherit(PermissionsGroup group) {
        removeInherit(group.getUUID());
    }

    public void addInherit(UUID permissionsGroupId) {
        inherits.add(permissionsGroupId.toString());
    }

    public void removeInherit(UUID permissionsGroupId) {
        inherits.remove(permissionsGroupId.toString());
    }

    @Override
    public boolean update() {
        return true;
    }

    public String getFancyName() {
        return nameColor + name;
    }

    public String getFormattedName(String player) {
        return ChatColor.translateAlternateColorCodes('&', (isShowPrefix() ? getPrefix():"") + getNameColor() + player + (isShowSuffix() ? getSuffix():""));
    }

    public UUID getUUID() {
        return UUID.fromString(uniqueId);
    }
    

}
